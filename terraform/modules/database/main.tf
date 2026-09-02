##############################################################################
# modules/database/main.tf
#
# Creates a managed PostgreSQL database on AWS RDS.
#
# WHY RDS INSTEAD OF SELF-MANAGED POSTGRES?
#   Self-managed (on EC2): You handle backups, patching, replication, failover.
#   RDS: AWS handles all of that. You just connect and query.
#
#   For a DevOps project, RDS is the right choice — it demonstrates
#   knowing WHEN to use managed services vs. when to roll your own.
#
# COMPONENTS:
#   1. Security Group     — only EC2 app servers can connect
#   2. DB Subnet Group    — RDS must know which subnets it can use
#   3. DB Parameter Group — PostgreSQL configuration tuning
#   4. Random Password    — generated and stored in Secrets Manager
#   5. RDS Instance       — the actual PostgreSQL database
#   6. CloudWatch Alarms  — alert on CPU, storage, connections
##############################################################################

# ============================================================================
# RANDOM PASSWORD GENERATION
#
# Never hardcode database passwords. This generates a cryptographically
# secure random password and stores it in AWS Secrets Manager.
# ============================================================================

resource "random_password" "db_password" {
  length           = 32
  special          = true
  override_special = "!#$%&*()-_=+[]{}<>:?" # Safe special chars for PostgreSQL URLs
}

# Store the generated password in AWS Secrets Manager
# Your Spring Boot app retrieves it at startup instead of having it in env vars
resource "aws_secretsmanager_secret" "db_password" {
  name        = "/${var.name_prefix}/database/password"
  description = "RDS PostgreSQL master password for ${var.name_prefix}"

  # After deletion, this secret is recoverable for 7 days
  # Set to 0 for immediate deletion (useful in dev/testing)
  recovery_window_in_days = var.environment == "prod" ? 30 : 7

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-db-password"
  })
}

resource "aws_secretsmanager_secret_version" "db_password" {
  secret_id = aws_secretsmanager_secret.db_password.id
  secret_string = jsonencode({
    password = random_password.db_password.result
    username = var.db_username
    dbname   = var.db_name
    engine   = "postgres"
    host     = "" # filled after RDS is created — updated by application
    port     = 5432
  })
}

# ============================================================================
# SECURITY GROUP FOR RDS
# Only EC2 app servers can connect. Nothing else — not even your laptop.
# To connect for debugging, use SSM Session Manager to tunnel through EC2.
# ============================================================================

resource "aws_security_group" "rds" {
  name        = "${var.name_prefix}-rds-sg"
  description = "Security group for RDS. Only allows connections from EC2 app servers."
  vpc_id      = var.vpc_id

  # INBOUND: Only from EC2 app servers, on PostgreSQL port
  ingress {
    description     = "PostgreSQL from EC2 app servers only"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [var.app_security_group_id] # Only from the compute module's SG
  }

  # OUTBOUND: RDS typically doesn't need outbound (no egress rule = deny all outbound)
  # This is the most restrictive possible config — perfect for databases.

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-rds-sg"
  })

  lifecycle {
    create_before_destroy = true
  }
}

# ============================================================================
# DB SUBNET GROUP
# Tells RDS which subnets it can place the database in.
# Must include subnets in at least 2 AZs (required by AWS).
# ============================================================================

resource "aws_db_subnet_group" "main" {
  name        = "${var.name_prefix}-db-subnet-group"
  description = "Database subnet group for ${var.name_prefix} (private, no internet access)"
  subnet_ids  = var.database_subnet_ids # The isolated database subnets from networking module

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-db-subnet-group"
  })
}

# ============================================================================
# DB PARAMETER GROUP — PostgreSQL Configuration Tuning
#
# AWS provides default parameter groups, but we create our own to:
# 1. Enable query logging (essential for debugging slow queries)
# 2. Tune memory settings for better performance
# 3. Have visibility into what parameters are set
#
# IMPORTANT: Some parameters require a DB restart (static parameters).
#            Others take effect immediately (dynamic parameters).
# ============================================================================

resource "aws_db_parameter_group" "main" {
  name        = "${var.name_prefix}-pg15-params"
  family      = "postgres15" # Must match the engine version below
  description = "Custom PostgreSQL 15 parameters for IMS"

  # Enable query logging — critical for performance troubleshooting
  parameter {
    name  = "log_min_duration_statement"
    value = "1000" # Log queries slower than 1000ms (1 second)
  }

  parameter {
    name  = "log_connections"
    value = "1" # Log when clients connect (useful for debugging connection issues)
  }

  parameter {
    name  = "log_disconnections"
    value = "1"
  }

  # Connection pooling hint — set based on instance size
  # For db.t3.medium: max_connections defaults to ~120
  parameter {
    name  = "max_connections"
    value = "200"
  }

  # Enable pg_stat_statements extension for query performance analysis
  # Requires DB restart (static parameter)
  parameter {
    name         = "shared_preload_libraries"
    value        = "pg_stat_statements"
    apply_method = "pending-reboot" # Takes effect after restart
  }

  parameter {
    name  = "pg_stat_statements.track"
    value = "ALL"
  }

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-pg-params"
  })
}

# ============================================================================
# RDS INSTANCE — The actual PostgreSQL database
# ============================================================================

resource "aws_db_instance" "main" {
  identifier = "${var.name_prefix}-postgres"

  # Engine configuration
  engine               = "postgres"
  engine_version       = "15.4" # Matches the parameter group family "postgres15"
  instance_class       = var.db_instance_class

  # Storage configuration
  allocated_storage     = var.db_allocated_storage
  max_allocated_storage = var.db_max_allocated_storage # Auto-scales up to this limit
  storage_type          = "gp3" # General Purpose SSD v3 — faster and cheaper than gp2
  storage_encrypted     = true  # Encrypt data at rest — always do this

  # Database credentials
  db_name  = var.db_name
  username = var.db_username
  password = random_password.db_password.result # Use the generated password

  # Network placement
  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]
  publicly_accessible    = false # CRITICAL: Never expose RDS to the internet

  # Configuration
  parameter_group_name = aws_db_parameter_group.main.name

  # High Availability — Multi-AZ creates a synchronous standby replica
  # If primary fails, automatic failover to standby (~1-2 min downtime)
  multi_az = var.db_multi_az

  # Backup configuration
  backup_retention_period   = var.db_backup_retention_days
  backup_window             = "03:00-04:00"   # UTC — low traffic window for backups
  maintenance_window        = "sun:04:00-sun:05:00" # Weekly maintenance after backup window

  # Performance Insights — gives you query-level performance metrics
  # Free for up to 7 days of retention on most instance sizes
  performance_insights_enabled          = true
  performance_insights_retention_period = 7 # Days (free tier)

  # Enhanced Monitoring — OS-level metrics every 60 seconds (free)
  monitoring_interval = 60 # Seconds between metric collection (0 to disable)

  # Auto minor version upgrades — keeps you on latest patch (5.7.1 → 5.7.2)
  # Major version upgrades (15 → 16) are never automatic
  auto_minor_version_upgrade = true

  # Deletion protection — prevents accidental terraform destroy in production
  deletion_protection = var.db_deletion_protection

  # Skip final snapshot on destroy — set to false in production!
  # When false: `terraform destroy` creates a final snapshot before deleting
  skip_final_snapshot       = var.environment != "prod"
  final_snapshot_identifier = var.environment == "prod" ? "${var.name_prefix}-final-snapshot" : null

  # Copy automated backups to another region for disaster recovery (optional)
  # Uncomment for production:
  # copy_tags_to_snapshot = true
  # replicate_source_db   = ""  # cross-region replication source

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-postgres"
  })
}

# ============================================================================
# CLOUDWATCH ALARMS — Alert when database is struggling
# ============================================================================

# Alert when CPU > 80% for 5 minutes
resource "aws_cloudwatch_metric_alarm" "db_cpu_high" {
  alarm_name          = "${var.name_prefix}-rds-cpu-high"
  alarm_description   = "RDS CPU utilization is above 80% — investigate slow queries"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2     # Must breach 2 consecutive periods
  metric_name         = "CPUUtilization"
  namespace           = "AWS/RDS"
  period              = 300   # 5-minute periods
  statistic           = "Average"
  threshold           = 80
  treat_missing_data  = "notBreaching"

  dimensions = {
    DBInstanceIdentifier = aws_db_instance.main.identifier
  }

  # Add SNS topic ARN here to get email/Slack alerts:
  # alarm_actions = [aws_sns_topic.alerts.arn]

  tags = var.tags
}

# Alert when free storage < 5GB
resource "aws_cloudwatch_metric_alarm" "db_storage_low" {
  alarm_name          = "${var.name_prefix}-rds-storage-low"
  alarm_description   = "RDS free storage is below 5GB — consider upgrading storage"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = 1
  metric_name         = "FreeStorageSpace"
  namespace           = "AWS/RDS"
  period              = 300
  statistic           = "Average"
  threshold           = 5368709120 # 5GB in bytes
  treat_missing_data  = "notBreaching"

  dimensions = {
    DBInstanceIdentifier = aws_db_instance.main.identifier
  }

  tags = var.tags
}

# Alert when connections are high (possible connection leak)
resource "aws_cloudwatch_metric_alarm" "db_connections_high" {
  alarm_name          = "${var.name_prefix}-rds-connections-high"
  alarm_description   = "RDS connection count is above 150 — check for connection leaks"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "DatabaseConnections"
  namespace           = "AWS/RDS"
  period              = 300
  statistic           = "Average"
  threshold           = 150
  treat_missing_data  = "notBreaching"

  dimensions = {
    DBInstanceIdentifier = aws_db_instance.main.identifier
  }

  tags = var.tags
}
