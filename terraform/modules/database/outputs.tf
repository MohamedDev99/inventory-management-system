##############################################################################
# modules/database/outputs.tf
##############################################################################

output "db_endpoint" {
  description = "RDS endpoint (host:port). Use in Spring Boot datasource URL."
  value       = aws_db_instance.main.endpoint
}

output "db_host" {
  description = "RDS hostname only (without port)."
  value       = aws_db_instance.main.address
}

output "db_port" {
  description = "RDS port (5432 for PostgreSQL)."
  value       = aws_db_instance.main.port
}

output "db_password" {
  description = "Auto-generated database master password. Also stored in Secrets Manager."
  value       = random_password.db_password.result
  sensitive   = true
}

output "db_instance_identifier" {
  description = "RDS instance identifier — use to find the instance in AWS Console."
  value       = aws_db_instance.main.identifier
}

output "db_instance_arn" {
  description = "RDS instance ARN."
  value       = aws_db_instance.main.arn
}

output "db_secret_arn" {
  description = "ARN of the Secrets Manager secret containing DB credentials."
  value       = aws_secretsmanager_secret.db_password.arn
}

output "db_security_group_id" {
  description = "Security group ID for RDS."
  value       = aws_security_group.rds.id
}
