output "db_endpoint" { value = aws_db_instance.main.endpoint }
output "db_host" { value = aws_db_instance.main.address }
output "db_port" { value = aws_db_instance.main.port }
output "db_secret_arn" { value = aws_secretsmanager_secret.db.arn }
output "db_password" {
  value     = random_password.db.result
  sensitive = true
}
