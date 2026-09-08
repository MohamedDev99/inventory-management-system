output "cluster_name"           { value = module.eks.cluster_name }
output "cluster_endpoint"       { value = module.eks.cluster_endpoint }
output "cluster_ca"             { value = module.eks.cluster_certificate_authority_data }
output "cluster_oidc_issuer"    { value = module.eks.cluster_oidc_issuer_url }
output "node_security_group_id" { value = module.eks.node_security_group_id }
output "node_group_role_arn"    { value = module.eks.eks_managed_node_groups["main"].iam_role_arn }
output "pod_irsa_role_arn"      { value = module.pod_irsa_role.iam_role_arn }
# Reference the local resource directly — avoids fragile map key lookup
# that breaks if fargate_namespaces doesn't contain "inventory-app"
output "fargate_role_arn"       { value = aws_iam_role.fargate.arn }
output "kms_key_arn"            { value = aws_kms_key.eks.arn }
