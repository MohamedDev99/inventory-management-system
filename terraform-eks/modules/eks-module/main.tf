##############################################################################
# modules/eks-module/main.tf
#
# THE SAME EKS CLUSTER — using the official community module.
# Compare line-by-line with modules/eks/ to see what the module abstracts.
#
# HOW TO SWITCH FROM SCRATCH TO MODULE:
#   In root main.tf change:  source = "./modules/eks"
#                       to:  source = "./modules/eks-module"
#   Outputs are identical — nothing else changes.
#
# WHAT THE MODULE ABSTRACTS AWAY VS SCRATCH:
#   ✅ Cluster IAM role + policy attachment
#   ✅ Node group IAM role + 4 policy attachments
#   ✅ Fargate execution IAM role + policy attachment
#   ✅ OIDC provider creation
#   ✅ Security group rules between control plane and nodes
#   ✅ aws-auth ConfigMap (grants nodes permission to join cluster)
#
# WHAT STILL NEEDS EXPLICIT RESOURCES (not covered by module):
#   ❌ KMS key for Secrets encryption     → aws_kms_key.eks
#   ❌ CloudWatch log group + retention   → aws_cloudwatch_log_group.cluster
#   ❌ Pod IRSA role (app-specific)       → module.pod_irsa_role
#   ❌ Pod IAM policies (S3, Secrets Mgr) → aws_iam_policy.*
##############################################################################

terraform {
  required_providers {
    aws = { source = "hashicorp/aws", version = "~> 5.0" }
  }
}

# ============================================================================
# KMS KEY — encrypts Kubernetes Secrets stored in etcd
#
# The community module accepts a key_arn but does NOT create the key itself.
# We must create it here and pass the ARN in.
# Without this, Terraform apply fails with:
#   "cluster_encryption_config: key_arn is required"
# ============================================================================

resource "aws_kms_key" "eks" {
  description             = "EKS cluster ${var.cluster_name} — Kubernetes Secrets encryption"
  deletion_window_in_days = 7
  enable_key_rotation     = true

  tags = merge(var.tags, { Name = "${var.name_prefix}-eks-kms" })
}

resource "aws_kms_alias" "eks" {
  name          = "alias/${var.name_prefix}-eks"
  target_key_id = aws_kms_key.eks.key_id
}

# ============================================================================
# CLOUDWATCH LOG GROUP — control plane logs
#
# If you don't create this, EKS auto-creates /aws/eks/<name>/cluster with
# NO retention policy — logs accumulate forever and costs grow unbounded.
# Create it explicitly so we control the 30-day retention.
# ============================================================================

resource "aws_cloudwatch_log_group" "cluster" {
  name              = "/aws/eks/${var.cluster_name}/cluster"
  retention_in_days = 30
  tags              = var.tags
}

# ============================================================================
# FARGATE EXECUTION ROLE
#
# The community module's fargate_profiles block requires pod_execution_role_arn.
# The module does NOT create this role automatically (unlike node group roles).
# We create it here and pass it in per-profile.
# ============================================================================

resource "aws_iam_role" "fargate" {
  name        = "${var.name_prefix}-eks-fargate-role"
  description = "IAM role for EKS Fargate pod execution."

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "eks-fargate-pods.amazonaws.com" }
    }]
  })

  tags = var.tags
}

resource "aws_iam_role_policy_attachment" "fargate" {
  role       = aws_iam_role.fargate.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSFargatePodExecutionRolePolicy"
}

# ============================================================================
# EKS CLUSTER — community module
# ============================================================================

module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 20.0"

  cluster_name    = var.cluster_name
  cluster_version = var.kubernetes_version

  # API server accessibility
  cluster_endpoint_public_access  = var.cluster_endpoint_public_access
  cluster_endpoint_private_access = true  # always enable — nodes use this

  # Network placement
  vpc_id     = var.vpc_id
  subnet_ids = var.private_subnet_ids  # worker node subnets

  # Control plane ENIs go in private subnets across all AZs
  control_plane_subnet_ids = var.private_subnet_ids

  # Control plane logs — CloudWatch log group created above
  cluster_enabled_log_types = ["api", "audit", "authenticator", "controllerManager", "scheduler"]

  # Encrypt K8s Secrets at rest using our KMS key
  cluster_encryption_config = {
    resources        = ["secrets"]
    provider_key_arn = aws_kms_key.eks.arn
  }

  # OIDC provider — enables IRSA for pods
  enable_irsa = true

  # EKS-managed add-ons (vpc-cni, coredns, kube-proxy)
  cluster_addons = {
    vpc-cni = {
      most_recent = true
      # Enables prefix delegation — allows more pods per node
      configuration_values = jsonencode({
        env = {
          ENABLE_PREFIX_DELEGATION = "true"
          WARM_PREFIX_TARGET       = "1"
        }
      })
    }
    coredns    = { most_recent = true }
    kube-proxy = { most_recent = true }
  }

  # ---- MANAGED NODE GROUPS ------------------------------------------------

  eks_managed_node_groups = {
    main = {
      name           = "${var.name_prefix}-node-group"
      instance_types = [var.node_instance_type]

      # SPOT for dev (saves ~70%), ON_DEMAND for prod (no interruptions)
      capacity_type = var.use_spot_nodes ? "SPOT" : "ON_DEMAND"

      min_size     = var.node_min_size
      desired_size = var.node_desired_size
      max_size     = var.node_max_size
      disk_size    = var.node_disk_size

      # Rolling update: only 1 node down at a time during K8s version upgrades
      update_config = {
        max_unavailable = 1
      }

      labels = {
        role        = "worker"
        environment = var.environment
      }

      tags = merge(var.tags, {
        # Required for cluster-autoscaler to discover and manage this group
        "k8s.io/cluster-autoscaler/enabled"             = "true"
        "k8s.io/cluster-autoscaler/${var.cluster_name}" = "owned"
      })
    }
  }

  # ---- FARGATE PROFILES ---------------------------------------------------
  # Pods in these namespaces run serverless (no EC2 nodes).
  # Your CronJobs in inventory-app namespace run here.

  fargate_profiles = {
    for ns in var.fargate_namespaces : ns => {
      name                   = "${var.name_prefix}-fargate-${ns}"
      pod_execution_role_arn = aws_iam_role.fargate.arn  # required — module won't create this

      selectors = [{ namespace = ns }]

      subnet_ids = var.private_subnet_ids  # Fargate pods run in private subnets
    }
  }

  tags = var.tags

  depends_on = [aws_cloudwatch_log_group.cluster]
}

# ============================================================================
# IRSA ROLE FOR APPLICATION PODS
#
# The community iam module creates the role + OIDC trust policy for us.
# We still need to define WHAT the role can do (S3, Secrets Manager).
# ============================================================================

module "pod_irsa_role" {
  source  = "terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks"
  version = "~> 5.0"

  role_name = "${var.name_prefix}-pod-irsa"

  # Link the role to a specific K8s ServiceAccount via OIDC
  # Only pods using serviceAccountName: ims-backend in inventory-app can assume this role
  oidc_providers = {
    main = {
      provider_arn               = module.eks.oidc_provider_arn
      namespace_service_accounts = ["inventory-app:ims-backend"]
    }
  }

  # Attach our custom policies to the role
  role_policy_arns = {
    s3_access       = aws_iam_policy.pod_s3.arn
    secrets_manager = aws_iam_policy.pod_secrets.arn
  }

  tags = var.tags
}

# S3 access — upload product images, read/write reports, write backups
resource "aws_iam_policy" "pod_s3" {
  name        = "${var.name_prefix}-pod-s3"
  description = "Allows EKS pods to access the IMS S3 buckets."

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Sid      = "S3BucketAccess"
      Effect   = "Allow"
      Action   = ["s3:GetObject", "s3:PutObject", "s3:DeleteObject", "s3:ListBucket"]
      Resource = concat(
        var.s3_bucket_arns,
        [for arn in var.s3_bucket_arns : "${arn}/*"]
      )
    }]
  })

  tags = var.tags
}

# Secrets Manager access — Spring Boot reads DB password and JWT secret at startup
resource "aws_iam_policy" "pod_secrets" {
  name        = "${var.name_prefix}-pod-secrets"
  description = "Allows EKS pods to read secrets from Secrets Manager."

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Sid    = "SecretsManagerAccess"
      Effect = "Allow"
      Action = [
        "secretsmanager:GetSecretValue",
        "secretsmanager:DescribeSecret"
      ]
      # Scoped to only this environment's secrets — not all secrets in the account
      Resource = "arn:aws:secretsmanager:*:*:secret:/${var.name_prefix}/*"
    }]
  })

  tags = var.tags
}
