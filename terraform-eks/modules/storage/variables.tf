variable "name_prefix" { type = string }
variable "environment" { type = string }
variable "s3_bucket_prefix" { type = string }
variable "tags" {
  type    = map(string)
  default = {}
}
