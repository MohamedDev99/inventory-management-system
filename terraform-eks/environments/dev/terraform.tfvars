##############################################################################
# environments/dev/terraform.tfvars
# terraform workspace select dev
# terraform apply -var-file="environments/dev/terraform.tfvars"
##############################################################################

aws_region         = "us-east-1"
vpc_cidr           = "10.0.0.0/16"
availability_zones = ["us-east-1a", "us-east-1b"]

cluster_name                   = "moeware-ims"
kubernetes_version             = "1.29"
cluster_endpoint_public_access = true  # kubectl from laptop

node_instance_type = "t3.medium"
node_min_size      = 1
node_desired_size  = 2
node_max_size      = 3
node_disk_size     = 30

fargate_namespaces = ["inventory-app", "kube-system"]

# Dev: StatefulSet handles PostgreSQL inside K8s — no RDS cost
enable_rds = false

s3_bucket_prefix   = "moeware-ims"
backend_image_tag  = "latest"
frontend_image_tag = "latest"
