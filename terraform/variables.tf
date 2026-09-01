##############################################################################
# variables.tf — Input Variables (Root Module)
#
# Variables are Terraform's way of making code reusable.
# Instead of hardcoding "t3.medium" everywhere, you define a variable
# and pass different values per environment.
#
# VARIABLE TYPES:
#   string  — "us-east-1"
#   number  — 3
#   bool    — true / false
#   list    — ["a", "b", "c"]
#   map     — { key = "value" }
#   object  — { name = string, count = number }
#
# HOW TO OVERRIDE:
#   1. CLI flag:        terraform apply -var="instance_type=t3.large"
#   2. .tfvars file:    terraform apply -var-file="prod.tfvars"
#   3. Environment var: export TF_VAR_instance_type=t3.large
#   4. Workspace file:  environments/prod/terraform.tfvars (used in this project)
##############################################################################

# ============================================================================
# GENERAL CONFIGURATION
# ============================================================================

variable "aws_region" {
  description = "AWS region to deploy all resources. Choose closest to your users."
  type        = string
  default     = "us-east-1"

  # Validation ensures you don't accidentally deploy to the wrong region.
  validation {
    condition     = contains(["us-east-1", "us-west-2", "eu-west-1", "ap-southeast-1"], var.aws_region)
    error_message = "Region must be one of: us-east-1, us-west-2, eu-west-1, ap-southeast-1."
  }
}

variable "owner_email" {
  description = "Email of the team/person responsible for this infrastructure. Used in tags."
  type        = string
  default     = "devops@moeware.com"
}

# ============================================================================
# NETWORKING
# ============================================================================

variable "vpc_cidr" {
  description = <<-EOT
    CIDR block for the VPC. This is the IP address range for the entire network.

    Common choices:
      - 10.0.0.0/16  → 65,536 IPs (most common for production)
      - 10.1.0.0/16  → different network for staging
      - 172.16.0.0/16 → alternative RFC1918 range

    /16 gives you plenty of room to create subnets within.
  EOT
  type        = string
  default     = "10.0.0.0/16"

  validation {
    condition     = can(cidrhost(var.vpc_cidr, 0))
    error_message = "vpc_cidr must be a valid CIDR block (e.g., 10.0.0.0/16)."
  }
}

variable "availability_zones" {
  description = <<-EOT
    List of Availability Zones for multi-AZ deployment.
    Using 2 AZs provides high availability — if one data center fails,
    the other keeps your app running.
    Using 3 AZs is more resilient but costs more.
  EOT
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b"]
}

variable "enable_nat_gateway" {
  description = <<-EOT
    Whether to create a NAT Gateway for private subnet internet access.

    NAT Gateway costs ~$32/month + data transfer.
    For dev/staging, you might disable this to save money.
    For production, always enable it (private subnets need internet for updates/Docker pulls).
  EOT
  type        = bool
  default     = true
}

variable "single_nat_gateway" {
  description = <<-EOT
    Use a single NAT Gateway instead of one per AZ.

    true  → cheaper (~$32/month), but single point of failure
    false → one NAT per AZ (~$64+/month), high availability

    Recommended: true for dev/staging, false for production.
  EOT
  type        = bool
  default     = false
}

# ============================================================================
# COMPUTE (EC2 / AUTO SCALING)
# ============================================================================

variable "instance_type" {
  description = <<-EOT
    EC2 instance type for application servers.

    Development:  t3.micro  (2 vCPU, 1GB RAM)  — free tier eligible
    Staging:      t3.small  (2 vCPU, 2GB RAM)  — ~$15/month
    Production:   t3.medium (2 vCPU, 4GB RAM)  — ~$30/month
    High traffic: t3.large  (2 vCPU, 8GB RAM)  — ~$60/month

    t3 instances have "burstable" CPU — good for variable workloads like IMS.
  EOT
  type        = string
  default     = "t3.medium"
}

variable "min_instances" {
  description = "Minimum number of EC2 instances in the Auto Scaling Group. Never goes below this."
  type        = number
  default     = 1

  validation {
    condition     = var.min_instances >= 1
    error_message = "min_instances must be at least 1."
  }
}

variable "desired_instances" {
  description = "Desired number of EC2 instances. Auto Scaling targets this number normally."
  type        = number
  default     = 2
}

variable "max_instances" {
  description = "Maximum number of EC2 instances. Auto Scaling never exceeds this."
  type        = number
  default     = 4
}

variable "key_pair_name" {
  description = <<-EOT
    Name of an existing AWS EC2 Key Pair for SSH access.
    Create one in AWS Console → EC2 → Key Pairs → Create key pair.
    Download the .pem file — you can't download it again!

    Set to null to create instances without SSH key (use SSM Session Manager instead).
  EOT
  type        = string
  default     = null
}

variable "enable_spot_instances" {
  description = <<-EOT
    Use EC2 Spot Instances for cost savings (up to 90% cheaper).
    Spot instances can be terminated with 2-minute notice when AWS needs capacity.

    RECOMMENDED: true for dev/staging (interruptions OK), false for production.
  EOT
  type        = bool
  default     = false
}

# ============================================================================
# DATABASE (RDS)
# ============================================================================

variable "db_instance_class" {
  description = <<-EOT
    RDS instance size.

    Development:  db.t3.micro  (2 vCPU, 1GB RAM)  — free tier eligible
    Staging:      db.t3.small  (2 vCPU, 2GB RAM)
    Production:   db.t3.medium (2 vCPU, 4GB RAM)  — recommended minimum
    High load:    db.r6g.large (2 vCPU, 16GB RAM) — memory-optimized for PostgreSQL
  EOT
  type        = string
  default     = "db.t3.medium"
}

variable "db_name" {
  description = "Name of the PostgreSQL database to create inside RDS."
  type        = string
  default     = "inventory_db"
}

variable "db_username" {
  description = "Master username for the RDS instance. Avoid 'admin' or 'postgres' (too guessable)."
  type        = string
  default     = "ims_admin"
  sensitive   = true # Hides value in logs
}

variable "db_allocated_storage" {
  description = "Initial storage for RDS in GB. RDS can auto-scale storage up if enabled."
  type        = number
  default     = 20
}

variable "db_max_allocated_storage" {
  description = "Maximum auto-scaled storage in GB. Set to 0 to disable auto-scaling."
  type        = number
  default     = 100
}

variable "db_multi_az" {
  description = <<-EOT
    Enable Multi-AZ deployment for RDS.

    Multi-AZ creates a synchronous standby replica in another AZ.
    If primary fails, AWS automatically fails over (~1-2 minutes downtime).

    Cost: ~2x the price of single-AZ.
    RECOMMENDED: false for dev/staging, true for production.
  EOT
  type        = bool
  default     = false
}

variable "db_backup_retention_days" {
  description = <<-EOT
    How many days to keep automated RDS backups.
    0 = disabled (not recommended for anything with real data).
    7 = good for dev/staging.
    30 = recommended for production.
    Max = 35 days.
  EOT
  type        = number
  default     = 7

  validation {
    condition     = var.db_backup_retention_days >= 0 && var.db_backup_retention_days <= 35
    error_message = "db_backup_retention_days must be between 0 and 35."
  }
}

variable "db_deletion_protection" {
  description = <<-EOT
    Prevent accidental RDS deletion via Terraform or AWS Console.

    When true: `terraform destroy` will FAIL until you set this to false first.
    This is intentional — it protects production databases from accidents.

    RECOMMENDED: false for dev/staging (allows easy teardown), true for production.
  EOT
  type        = bool
  default     = false
}

# ============================================================================
# STORAGE (S3)
# ============================================================================

variable "s3_bucket_name_prefix" {
  description = <<-EOT
    Prefix for S3 bucket names. Full name will be: {prefix}-{environment}-{random_suffix}
    The random suffix ensures global uniqueness (S3 bucket names are global across all AWS accounts).

    Example: "moeware-ims" → "moeware-ims-prod-a1b2c3"
  EOT
  type        = string
  default     = "moeware-ims"
}

variable "enable_s3_versioning" {
  description = "Enable S3 object versioning. Protects against accidental deletions by keeping old versions."
  type        = bool
  default     = true
}

# ============================================================================
# APPLICATION
# ============================================================================

variable "app_domain" {
  description = <<-EOT
    Domain name for the application (e.g., ims.moeware.com).
    Used for SSL certificate and Route53 records.
    Leave empty to skip DNS/SSL configuration.
  EOT
  type        = string
  default     = ""
}

variable "backend_image_tag" {
  description = <<-EOT
    Docker image tag for the backend application.
    In CI/CD, this gets set to the git commit SHA for traceability.
    Example: "v1.2.0" or "sha-abc1234"
  EOT
  type        = string
  default     = "latest"
}

variable "frontend_image_tag" {
  description = "Docker image tag for the frontend application."
  type        = string
  default     = "latest"
}