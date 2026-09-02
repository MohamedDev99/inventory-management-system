##############################################################################
# modules/compute/variables.tf
##############################################################################

variable "name_prefix" { type = string }
variable "environment" { type = string }

# Network
variable "vpc_id" { type = string }
variable "public_subnet_ids" { type = list(string) }
variable "private_subnet_ids" { type = list(string) }

# Instance
variable "ami_id" { type = string }
variable "instance_type" { type = string }
variable "key_pair_name" {
  type    = string
  default = null
}

# Auto Scaling
variable "min_instances" { type = number }
variable "desired_instances" { type = number }
variable "max_instances" { type = number }

variable "enable_spot_instances" {
  type    = bool
  default = false
}

# Application
variable "backend_image_tag" { type = string }
variable "frontend_image_tag" { type = string }

# Dependencies from other modules
variable "db_endpoint" { type = string }
variable "db_name" { type = string }
variable "db_username" { type = string }
variable "db_password" {
  type      = string
  sensitive = true
}
variable "s3_bucket_name" { type = string }
variable "aws_region" { type = string }

variable "tags" {
  type    = map(string)
  default = {}
}
