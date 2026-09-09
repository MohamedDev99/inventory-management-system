##############################################################################
# providers.tf — AWS + Kubernetes + Helm Providers
#
# WHY THREE PROVIDERS?
#   aws        → creates the EKS cluster itself (AWS API)
#   kubernetes → configures kubectl to point at the new cluster
#   helm       → installs nginx-ingress + cert-manager into the cluster
#
# CHICKEN-AND-EGG PROBLEM:
#   kubernetes and helm providers need the cluster endpoint + CA cert to connect.
#   But the cluster doesn't exist yet when Terraform starts.
#
# SOLUTION:
#   Reference module.eks.cluster_endpoint and module.eks.cluster_ca
#   Terraform resolves these after the EKS module runs.
#   On the FIRST apply these will be empty strings — that's fine because
#   no kubernetes/helm resources are created on first apply either.
##############################################################################

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = "MoeWare-IMS"
      ManagedBy   = "Terraform"
      Environment = local.environment
      Owner       = var.owner_email
    }
  }
}

# Kubernetes provider — points kubectl at the EKS cluster Terraform creates.
# Uses AWS IAM authentication (exec plugin) — no static kubeconfig needed.
provider "kubernetes" {
  host                   = module.eks.cluster_endpoint
  cluster_ca_certificate = base64decode(module.eks.cluster_ca)

  exec {
    api_version = "client.authentication.k8s.io/v1beta1"
    command     = "aws"
    args = [
      "eks", "get-token",
      "--cluster-name", module.eks.cluster_name,
      "--region", var.aws_region
    ]
  }
}

# Helm provider — installs system-level charts (nginx-ingress, cert-manager).
# App charts (inventory-chart) are deployed separately via CI/CD.
provider "helm" {
  kubernetes {
    host                   = module.eks.cluster_endpoint
    cluster_ca_certificate = base64decode(module.eks.cluster_ca)

    exec {
      api_version = "client.authentication.k8s.io/v1beta1"
      command     = "aws"
      args = [
        "eks", "get-token",
        "--cluster-name", module.eks.cluster_name,
        "--region", var.aws_region
      ]
    }
  }
}
