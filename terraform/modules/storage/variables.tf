##############################################################################
# modules/storage/variables.tf
##############################################################################

variable "name_prefix" { type = string }
variable "environment" { type = string }
variable "s3_bucket_name_prefix" { type = string }
variable "enable_versioning" { type = bool }
variable "ec2_instance_role_name" { type = string }

variable "tags" {
  type    = map(string)
  default = {}
}
