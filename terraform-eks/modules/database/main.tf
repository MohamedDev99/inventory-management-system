##############################################################################
# modules/database/main.tf
#
# RDS PostgreSQL — PROD ONLY
#
# DEV ENVIRONMENT:
#   This module is skipped entirely (count = 0 in main.tf).
#   Dev uses the PostgreSQL StatefulSet in your k8s/database/statefulset.yaml.
#   That's simpler and costs nothing extra.
#
# PROD ENVIRONMENT:
#   Managed RDS is used because:
#   - Automated daily backups (30-day retention, point-in-time recovery)
#   - Multi-AZ automatic failover (~1-2 min RTO if primary AZ fails)
#   - No maintenance burden — AWS patches OS and PostgreSQL minor versions
#   - StatefulSets in K8s are tricky: PVC resizing, backup orchestration,
#     and failover are all manual work. RDS handles all of it.
#
# HOW PODS CONNECT TO RDS:
#   1. RDS lives in database subnets (no internet, not in K8s)
#   2. EKS pods in private subnets CAN reach RDS via internal VPC routing
#   3. The RDS security group allows traffic from the EKS node security group
#   4. Spring Boot reads DB credentials from Secrets Manager via IRSA
##############################################################################

resource "random_password" "db" {
  length           = 32
  special          = true
  override_special = "!#$%&*()-_=+[]{}<>:?"
}

resource "aws_secretsmanager_secret" "db" {
  name                    = "/${var.name_prefix}/database/password"
  description             = "RDS master password for ${var.name_prefix}"
  recovery_window_in_days = 30
  tags                    = var.tags
}

resource "aws_secretsmanager_secret_version" "db" {
  secret_id = aws_secretsmanager_secret.db.id
  secret_string = jsonencode({
    username = var.db_username
    password = random_password.db.result
    dbname   = var.db_name
    engine   = "postgres"
    port     = 5432
    host     = aws_db_instance.main.address
  })

  # host is only known after RDS is created — this creates the right dependency order
  depends_on = [aws_db_instance.main]
}

# Security group — only EKS nodes can reach RDS
resource "aws_security_group" "rds" {
  name        = "${var.name_prefix}-rds-sg"
  description = "RDS PostgreSQL — allows connections from EKS nodes only."
  vpc_id      = var.vpc_id

  ingress {
    description     = "PostgreSQL from EKS worker nodes"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [var.eks_node_security_group_id]
  }

  tags = merge(var.tags, { Name = "${var.name_prefix}-rds-sg" })
}

resource "aws_db_subnet_group" "main" {
  name       = "${var.name_prefix}-db-subnet-group"
  subnet_ids = var.database_subnet_ids
  tags       = merge(var.tags, { Name = "${var.name_prefix}-db-subnet-group" })
}

resource "aws_db_parameter_group" "main" {
  name   = "${var.name_prefix}-pg15"
  family = "postgres15"

  parameter {
    name  = "log_min_duration_statement"
    value = "1000"
  }
  parameter {
    name  = "shared_preload_libraries"
    value = "pg_stat_statements"
    apply_method = "pending-reboot"
  }

  tags = var.tags
}

# IAM role required by RDS Enhanced Monitoring (monitoring_interval > 0).
# Without this role, Terraform apply fails:
#   Error: "monitoring_role_arn": required field is not set
resource "aws_iam_role" "rds_monitoring" {
  name        = "${var.name_prefix}-rds-monitoring-role"
  description = "Allows RDS to send Enhanced Monitoring metrics to CloudWatch."

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "monitoring.rds.amazonaws.com" }
    }]
  })

  tags = var.tags
}

resource "aws_iam_role_policy_attachment" "rds_monitoring" {
  role       = aws_iam_role.rds_monitoring.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonRDSEnhancedMonitoringRole"
}

resource "aws_db_instance" "main" {
  identifier        = "${var.name_prefix}-postgres"
  engine            = "postgres"
  engine_version    = "15.4"
  instance_class    = var.db_instance_class

  allocated_storage     = 50
  max_allocated_storage = 500
  storage_type          = "gp3"
  storage_encrypted     = true

  db_name  = var.db_name
  username = var.db_username
  password = random_password.db.result

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]
  parameter_group_name   = aws_db_parameter_group.main.name
  publicly_accessible    = false

  multi_az                  = var.db_multi_az
  backup_retention_period   = var.db_backup_retention_days
  backup_window             = "03:00-04:00"
  maintenance_window        = "sun:04:00-sun:05:00"

  # deletion_protection = true  → terraform destroy will fail until set to false (prod safety)
  # deletion_protection = false → terraform destroy proceeds normally (dev convenience)
  deletion_protection = var.db_deletion_protection

  # skip_final_snapshot = true  → destroy immediately, no snapshot (dev)
  # skip_final_snapshot = false → take a final snapshot before destroying (prod)
  # Controlled per-environment via variable — not hardcoded
  skip_final_snapshot       = var.skip_final_snapshot
  final_snapshot_identifier = var.skip_final_snapshot ? null : "${var.name_prefix}-final-snapshot"

  performance_insights_enabled          = true
  performance_insights_retention_period = 7

  # Enhanced Monitoring — OS-level metrics (CPU steal, disk I/O per process).
  # Requires monitoring_role_arn when interval > 0.
  monitoring_interval = 60
  monitoring_role_arn = aws_iam_role.rds_monitoring.arn

  auto_minor_version_upgrade = true

  tags = merge(var.tags, { Name = "${var.name_prefix}-postgres" })

  depends_on = [aws_iam_role_policy_attachment.rds_monitoring]
}
