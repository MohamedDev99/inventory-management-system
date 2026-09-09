##############################################################################
# backend.tf — Remote State for EKS Infrastructure
#
# Separate state from the old EC2 Terraform — clean slate.
# key = "eks/..." keeps it isolated in the same S3 bucket.
#
# FIRST-TIME SETUP (run once before terraform init):
#   ./scripts/setup-backend.sh
##############################################################################

terraform {
  required_version = ">= 1.6.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    # Kubernetes provider — used ONLY to configure kubectl after cluster exists.
    # App resources (deployments, services) stay in k8s/ folder, NOT here.
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.27"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.13"
    }
    tls = {
      source  = "hashicorp/tls"
      version = "~> 4.0"
    }
  }

  backend "s3" {
    bucket               = "moeware-ims-terraform-state"
    key                  = "eks/terraform.tfstate"
    region               = "us-east-1"
    encrypt              = true
    dynamodb_table       = "moeware-ims-terraform-locks"
    workspace_key_prefix = "eks"
  }
}
