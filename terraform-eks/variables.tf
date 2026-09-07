##############################################################################
# variables.tf — All Input Variables
##############################################################################

# ============================================================================
# GENERAL
# ============================================================================

variable "aws_region" {
  description = "AWS region for all resources."
  type        = string
  default     = "us-east-1"
}

variable "owner_email" {
  description = "Team email — appears in resource tags."
  type        = string
  default     = "devops@moeware.com"
}

# ============================================================================
# NETWORKING
# ============================================================================

variable "vpc_cidr" {
  description = "CIDR block for the VPC."
  type        = string
  default     = "10.0.0.0/16"
}

variable "availability_zones" {
  description = "AZs to deploy into. EKS requires at least 2."
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b", "us-east-1c"]
}

# ============================================================================
# EKS CLUSTER
# ============================================================================

variable "cluster_name" {
  description = "EKS cluster name. Used in IAM roles and kubectl config."
  type        = string
  default     = "moeware-ims"
}

variable "kubernetes_version" {
  description = "Kubernetes version for the EKS cluster."
  type        = string
  default     = "1.29"
}

variable "cluster_endpoint_public_access" {
  description = <<-EOT
    Allow kubectl from the internet (true) or VPN/bastion only (false).
    dev/staging:  true  (convenient for development)
    prod:         false (only accessible from within VPC — more secure)
  EOT
  type        = bool
  default     = true
}

# ============================================================================
# NODE GROUPS
# ============================================================================

variable "node_instance_type" {
  description = <<-EOT
    EC2 instance type for managed node group workers.
    t3.medium = 2 vCPU, 4GB RAM — minimum for Spring Boot + React side by side.
    t3.large  = 2 vCPU, 8GB RAM — comfortable for all workloads + monitoring.
  EOT
  type        = string
  default     = "t3.medium"
}

variable "node_min_size" {
  description = "Minimum nodes in the managed node group."
  type        = number
  default     = 2
}

variable "node_desired_size" {
  description = "Desired number of nodes."
  type        = number
  default     = 2
}

variable "node_max_size" {
  description = "Maximum nodes (Auto Scaling scales up to this)."
  type        = number
  default     = 5
}

variable "node_disk_size" {
  description = "Root EBS volume size per node in GB."
  type        = number
  default     = 30
}

# ============================================================================
# FARGATE (for CronJobs)
# ============================================================================

variable "fargate_namespaces" {
  description = <<-EOT
    Kubernetes namespaces where Fargate profiles will run pods.
    Pods in these namespaces run on serverless Fargate — no EC2 nodes needed.
    Your cronjobs/ manifests use namespace "inventory-app" — add it here.
  EOT
  type        = list(string)
  default     = ["inventory-app", "kube-system"]
}

# ============================================================================
# DATABASE (RDS — prod only)
# ============================================================================

variable "enable_rds" {
  description = <<-EOT
    Whether to create RDS PostgreSQL.
    dev:  false (use StatefulSet inside K8s cluster instead)
    prod: true  (managed RDS — automated backups, Multi-AZ, no maintenance)
  EOT
  type        = bool
  default     = false
}

variable "db_instance_class" {
  description = "RDS instance class. Only used when enable_rds = true."
  type        = string
  default     = "db.t3.medium"
}

variable "db_name" {
  description = "PostgreSQL database name."
  type        = string
  default     = "inventory_db"
}

variable "db_username" {
  description = "PostgreSQL master username."
  type        = string
  default     = "ims_admin"
  sensitive   = true
}

variable "db_multi_az" {
  description = "Multi-AZ RDS. false for dev/staging, true for prod."
  type        = bool
  default     = false
}

variable "db_backup_retention_days" {
  description = "RDS automated backup retention. 0 disables backups."
  type        = number
  default     = 7
}

# ============================================================================
# STORAGE
# ============================================================================

variable "s3_bucket_prefix" {
  description = "Prefix for S3 bucket names."
  type        = string
  default     = "moeware-ims"
}

# ============================================================================
# APPLICATION
# ============================================================================

variable "backend_image_tag" {
  description = "Docker image tag for backend. Set by CI/CD."
  type        = string
  default     = "latest"
}

variable "frontend_image_tag" {
  description = "Docker image tag for frontend. Set by CI/CD."
  type        = string
  default     = "latest"
}

variable "domain_name" {
  description = "Domain for the app (e.g. ims.moeware.com). Used for cert-manager."
  type        = string
  default     = ""
}
