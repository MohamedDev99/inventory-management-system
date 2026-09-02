##############################################################################
# modules/networking/variables.tf
##############################################################################

variable "name_prefix" {
  description = "Prefix for all resource names in this module."
  type        = string
}

variable "environment" {
  description = "Environment name (dev, staging, prod)."
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC."
  type        = string
}

variable "availability_zones" {
  description = "List of availability zones to deploy subnets into."
  type        = list(string)
}

variable "enable_nat_gateway" {
  description = "Whether to create NAT Gateways for private subnet outbound internet access."
  type        = bool
  default     = true
}

variable "single_nat_gateway" {
  description = "Use a single shared NAT Gateway instead of one per AZ. Cheaper but less resilient."
  type        = bool
  default     = false
}

variable "tags" {
  description = "Additional tags to apply to all resources."
  type        = map(string)
  default     = {}
}
