##############################################################################
# outputs.tf — Values exposed after terraform apply
#
# These are used by:
#   - post-apply.sh  → configures kubectl
#   - CI/CD pipeline → knows where to deploy
#   - k8s/ manifests → need RDS endpoint for prod ConfigMap
##############################################################################

output "cluster_name" {
  description = "EKS cluster name — used in kubectl/helm commands."
  value       = module.eks.cluster_name
}

output "cluster_endpoint" {
  description = "EKS API server endpoint — used by kubectl."
  value       = module.eks.cluster_endpoint
}

output "cluster_ca" {
  description = "EKS cluster CA certificate (base64)."
  value       = module.eks.cluster_ca
  sensitive   = true
}

output "kubeconfig_command" {
  description = "Run this command to configure kubectl after apply."
  value       = "aws eks update-kubeconfig --region ${var.aws_region} --name ${module.eks.cluster_name}"
}

output "vpc_id" {
  description = "VPC ID the cluster lives in."
  value       = module.networking.vpc_id
}

# RDS endpoint — only populated when enable_rds = true (prod)
output "db_endpoint" {
  description = "RDS endpoint. Empty in dev (uses in-cluster PostgreSQL StatefulSet)."
  value       = local.enable_rds ? module.database[0].db_endpoint : "not-created-using-statefulset"
}

output "db_secret_arn" {
  description = "ARN of Secrets Manager secret containing DB credentials (prod only)."
  value       = local.enable_rds ? module.database[0].db_secret_arn : "not-created"
}

output "uploads_bucket_name" {
  description = "S3 uploads bucket — set in your ConfigMap / Helm values."
  value       = module.storage.uploads_bucket_name
}

output "reports_bucket_name" {
  description = "S3 reports bucket."
  value       = module.storage.reports_bucket_name
}

output "backups_bucket_name" {
  description = "S3 backups bucket — used by backup CronJob."
  value       = module.storage.backups_bucket_name
}

output "pod_irsa_role_arn" {
  description = <<-EOT
    IAM Role ARN for IRSA (IAM Roles for Service Accounts).
    Annotate your K8s ServiceAccount with this ARN so pods can access S3/Secrets:
      kubectl annotate serviceaccount ims-backend \
        eks.amazonaws.com/role-arn=<this_value> \
        -n inventory-app
  EOT
  value       = module.eks.pod_irsa_role_arn
}

output "node_group_role_arn" {
  description = "IAM Role ARN for EKS node group."
  value       = module.eks.node_group_role_arn
}

output "environment" {
  value = local.environment
}

output "deployment_summary" {
  description = "Quick summary for CI/CD logs."
  value = {
    environment    = local.environment
    cluster_name   = module.eks.cluster_name
    region         = var.aws_region
    node_type      = local.config.node_instance_type
    node_count     = local.config.node_desired_size
    use_spot       = local.config.use_spot_nodes
    rds_enabled    = local.enable_rds
    uploads_bucket = module.storage.uploads_bucket_name
  }
}
