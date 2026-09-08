variable "name_prefix" { type = string }
variable "environment" { type = string }
variable "cluster_name" { type = string }
variable "aws_region" { type = string }
variable "aws_account_id" { type = string }
variable "vpc_id" { type = string }
variable "private_subnet_ids" { type = list(string) }
variable "public_subnet_ids" { type = list(string) }
variable "kubernetes_version" { type = string }
variable "cluster_endpoint_public_access" { type = bool }
variable "node_instance_type" { type = string }
variable "node_min_size" { type = number }
variable "node_desired_size" { type = number }
variable "node_max_size" { type = number }
variable "node_disk_size" { type = number }
variable "use_spot_nodes" { type = bool }
variable "fargate_namespaces" { type = list(string) }
variable "s3_bucket_arns" { type = list(string) }
variable "tags" {
  type    = map(string)
  default = {}
}
