##############################################################################
# environments/prod/terraform.tfvars
# terraform workspace select prod
# terraform apply -var-file="environments/prod/terraform.tfvars"
##############################################################################

aws_region         = "us-east-1"
vpc_cidr           = "10.2.0.0/16"
availability_zones = ["us-east-1a", "us-east-1b", "us-east-1c"]

cluster_name                   = "moeware-ims"
kubernetes_version             = "1.29"
cluster_endpoint_public_access = false  # VPN/bastion only

node_instance_type = "t3.medium"
node_min_size      = 3
node_desired_size  = 3
node_max_size      = 10
node_disk_size     = 50

fargate_namespaces = ["inventory-app", "kube-system"]

# Prod: managed RDS with Multi-AZ and 30-day backups
enable_rds               = true
db_instance_class        = "db.t3.medium"
db_name                  = "inventory_db"
db_username              = "ims_admin"
db_multi_az              = true
db_backup_retention_days = 30

s3_bucket_prefix   = "moeware-ims"
backend_image_tag  = "v1.0.0"
frontend_image_tag = "v1.0.0"
