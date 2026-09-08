output "uploads_bucket_name" { value = aws_s3_bucket.uploads.bucket }
output "uploads_bucket_arn"  { value = aws_s3_bucket.uploads.arn }
output "reports_bucket_name" { value = aws_s3_bucket.reports.bucket }
output "reports_bucket_arn"  { value = aws_s3_bucket.reports.arn }
output "backups_bucket_name" { value = aws_s3_bucket.backups.bucket }
output "backups_bucket_arn"  { value = aws_s3_bucket.backups.arn }
