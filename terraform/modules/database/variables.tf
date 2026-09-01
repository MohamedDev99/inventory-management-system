##############################################################################
# modules/database/variables.tf
##############################################################################

variable "name_prefix" { type = string }
variable "environment" { type = string }

variable "vpc_id" { type = string }
variable "database_subnet_ids" { type = list(string) }
variable "app_security_group_id" { type = string }

variable "db_instance_class" { type = string }
variable "db_name" { type = string }
variable "db_username" {
  type      = string
  sensitive = true
}
variable "db_allocated_storage" { type = number }
variable "db_max_allocated_storage" { type = number }

variable "db_multi_az" { type = bool }
variable "db_backup_retention_days" { type = number }
variable "db_deletion_protection" { type = bool }

variable "tags" {
  type    = map(string)
  default = {}
}
