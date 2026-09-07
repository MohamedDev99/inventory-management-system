##############################################################################
# main.tf — Root Orchestrator
#
# MODULE DEPENDENCY CHAIN:
#
#   networking → eks → database (conditional) + storage
#                  ↓
#             helm_releases (nginx-ingress, cert-manager)
#
# WHAT TERRAFORM MANAGES HERE:
#   ✅ VPC, subnets, route tables (EKS-specific networking)
#   ✅ EKS cluster + node groups + Fargate profiles
#   ✅ All IAM roles (cluster, nodes, Fargate, IRSA)
#   ✅ RDS PostgreSQL (prod only — dev uses StatefulSet)
#   ✅ S3 buckets (uploads, reports, backups)
#   ✅ nginx-ingress + cert-manager (system-level Helm charts only)
#
# WHAT STAYS IN k8s/ FOLDER (NOT here):
#   ❌ Namespaces, ConfigMaps, Secrets
#   ❌ Backend/Frontend Deployments and Services
#   ❌ StatefulSet for PostgreSQL (dev)
#   ❌ Ingress rules, HPA, CronJobs
#   ❌ Your inventory Helm chart
##############################################################################

# ============================================================================
# DATA SOURCES
# ============================================================================

data "aws_caller_identity" "current" {}

data "aws_availability_zones" "available" {
  state = "available"
}

# ============================================================================
# MODULE: NETWORKING
# EKS-specific VPC — different from regular EC2 VPC because:
#   - Subnets need specific tags for EKS to discover them
#   - Public subnets need tag: kubernetes.io/role/elb = 1
#   - Private subnets need tag: kubernetes.io/role/internal-elb = 1
#   - The cluster name must be in the tags for ALB/NLB auto-discovery
# ============================================================================
module "networking" {
  source = "./modules/networking"

  name_prefix        = local.name_prefix
  environment        = local.environment
  cluster_name       = local.cluster_name
  vpc_cidr           = var.vpc_cidr
  availability_zones = var.availability_zones

  tags = local.common_tags
}

# ============================================================================
# MODULE: EKS (written from scratch — for learning)
# Creates the actual Kubernetes cluster on AWS.
# See modules/eks/ for full resource breakdown.
# ============================================================================
module "eks" {
  source = "./modules/eks"

  name_prefix    = local.name_prefix
  environment    = local.environment
  cluster_name   = local.cluster_name
  aws_region     = var.aws_region
  aws_account_id = data.aws_caller_identity.current.account_id

  # Network placement
  vpc_id             = module.networking.vpc_id
  private_subnet_ids = module.networking.private_subnet_ids
  public_subnet_ids  = module.networking.public_subnet_ids

  # Cluster config
  kubernetes_version             = local.config.kubernetes_version
  cluster_endpoint_public_access = local.config.cluster_endpoint_public_access

  # Managed node group
  node_instance_type = local.config.node_instance_type
  node_min_size      = local.config.node_min_size
  node_desired_size  = local.config.node_desired_size
  node_max_size      = local.config.node_max_size
  node_disk_size     = var.node_disk_size
  use_spot_nodes     = local.config.use_spot_nodes

  # Fargate profiles — pods in these namespaces run serverless
  # Your cronjobs/ run in inventory-app namespace → no EC2 nodes needed
  fargate_namespaces = var.fargate_namespaces

  # IRSA — gives pods access to AWS services without storing credentials
  # (IRSA = IAM Roles for Service Accounts)
  s3_bucket_arns = [
    module.storage.uploads_bucket_arn,
    module.storage.reports_bucket_arn,
    module.storage.backups_bucket_arn,
  ]

  tags = local.common_tags
}

# ============================================================================
# MODULE: DATABASE (RDS — prod only)
# Dev uses PostgreSQL StatefulSet inside the cluster (your statefulset.yaml).
# Prod uses managed RDS — more reliable, automated backups, no maintenance.
#
# The conditional count = local.enable_rds ? 1 : 0 means:
#   dev workspace  → module is skipped entirely (count = 0)
#   prod workspace → module runs (count = 1)
# ============================================================================
module "database" {
  source = "./modules/database"
  count  = local.enable_rds ? 1 : 0

  name_prefix = local.name_prefix
  environment = local.environment

  vpc_id              = module.networking.vpc_id
  database_subnet_ids = module.networking.database_subnet_ids

  # Only pods in the EKS node security group can reach RDS
  eks_node_security_group_id = module.eks.node_security_group_id

  db_instance_class        = local.config.db_instance_class
  db_name                  = var.db_name
  db_username              = var.db_username
  db_multi_az              = local.config.db_multi_az
  db_backup_retention_days = local.config.db_backup_retention_days
  db_deletion_protection   = local.config.db_deletion_protection

  tags = local.common_tags
}

# ============================================================================
# MODULE: STORAGE (S3 — both dev and prod)
# Same buckets in both environments — always needed for file uploads, reports.
# ============================================================================
module "storage" {
  source = "./modules/storage"

  name_prefix      = local.name_prefix
  environment      = local.environment
  s3_bucket_prefix = var.s3_bucket_prefix

  tags = local.common_tags
}

# ============================================================================
# HELM: SYSTEM-LEVEL CHARTS
# Terraform installs infrastructure-level Helm charts only.
# Your app chart (inventory-chart) is deployed by CI/CD, not Terraform.
# ============================================================================

# nginx-ingress — your ingress/ingress.yaml uses nginx ingress class
resource "helm_release" "nginx_ingress" {
  name             = "ingress-nginx"
  repository       = "https://kubernetes.github.io/ingress-nginx"
  chart            = "ingress-nginx"
  namespace        = "ingress-nginx"
  create_namespace = true
  version          = "4.10.0"

  # On AWS, the ingress controller creates an AWS Network Load Balancer
  set {
    name  = "controller.service.type"
    value = "LoadBalancer"
  }

  # Annotate NLB as internet-facing
  set {
    name  = "controller.service.annotations.service\\.beta\\.kubernetes\\.io/aws-load-balancer-type"
    value = "nlb"
  }

  set {
    name  = "controller.service.annotations.service\\.beta\\.kubernetes\\.io/aws-load-balancer-scheme"
    value = "internet-facing"
  }

  depends_on = [module.eks]
}

# cert-manager — your ingress.yaml references cert-manager for TLS certificates
resource "helm_release" "cert_manager" {
  name             = "cert-manager"
  repository       = "https://charts.jetstack.io"
  chart            = "cert-manager"
  namespace        = "cert-manager"
  create_namespace = true
  version          = "v1.14.4"

  # Required: installs CRDs (Certificate, Issuer, ClusterIssuer resources)
  set {
    name  = "installCRDs"
    value = "true"
  }

  depends_on = [module.eks]
}
