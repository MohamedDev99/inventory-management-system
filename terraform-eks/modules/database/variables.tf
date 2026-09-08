variable "name_prefix" { type = string }
variable "environment" { type = string }
variable "vpc_id" { type = string }
variable "database_subnet_ids" { type = list(string) }
variable "eks_node_security_group_id" { type = string }
variable "db_instance_class" { type = string }
variable "db_name" { type = string }
variable "db_username" {
  type      = string
  sensitive = true
}
variable "db_multi_az" { type = bool }
variable "db_backup_retention_days" { type = number }
variable "db_deletion_protection" { type = bool }
variable "skip_final_snapshot" {
  type        = bool
  default     = true
  description = "Skip final snapshot on destroy. true for dev (fast teardown), false for prod (safety)."
}
variable "tags" {
  type    = map(string)
  default = {}
}
