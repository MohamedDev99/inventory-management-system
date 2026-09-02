##############################################################################
# modules/storage/outputs.tf
##############################################################################

output "uploads_bucket_name" {
  description = "S3 bucket name for file uploads."
  value       = aws_s3_bucket.uploads.bucket
}

output "uploads_bucket_arn" {
  description = "S3 bucket ARN for uploads."
  value       = aws_s3_bucket.uploads.arn
}

output "reports_bucket_name" {
  description = "S3 bucket name for generated reports."
  value       = aws_s3_bucket.reports.bucket
}

output "reports_bucket_arn" {
  description = "S3 bucket ARN for reports."
  value       = aws_s3_bucket.reports.arn
}

output "backups_bucket_name" {
  description = "S3 bucket name for database backups."
  value       = aws_s3_bucket.backups.bucket
}

output "backups_bucket_arn" {
  description = "S3 bucket ARN for backups."
  value       = aws_s3_bucket.backups.arn
}
