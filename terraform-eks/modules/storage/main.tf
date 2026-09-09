##############################################################################
# modules/storage/main.tf
#
# S3 BUCKETS — used by pods via IRSA (no credentials stored in K8s Secrets)

terraform {
  required_providers {
    aws    = { source = "hashicorp/aws", version = "~> 5.0" }
    random = { source = "hashicorp/random", version = "~> 3.5" }
  }
}
#
# HOW PODS ACCESS S3:
#   1. Pod uses the "ims-backend" ServiceAccount (annotated with IRSA role ARN)
#   2. AWS SDK in Spring Boot detects the IRSA token automatically
#   3. SDK exchanges token for temporary S3 credentials via STS
#   4. Pod reads/writes S3 — no AWS keys stored anywhere in K8s
#
# BUCKET PURPOSES:
#   uploads  → product images, PO attachments (Spring Boot writes here)
#   reports  → generated PDF/Excel reports (CronJob writes, users download)
#   backups  → pg_dump files (backup CronJob writes here nightly)
##############################################################################

resource "random_id" "suffix" {
  byte_length = 4
}

locals {
  suffix = random_id.suffix.hex
}

# ---- UPLOADS BUCKET --------------------------------------------------------

resource "aws_s3_bucket" "uploads" {
  bucket = "${var.s3_bucket_prefix}-uploads-${var.environment}-${local.suffix}"
  tags   = merge(var.tags, { Name = "${var.name_prefix}-uploads", Purpose = "Product images and attachments" })
}

resource "aws_s3_bucket_public_access_block" "uploads" {
  bucket                  = aws_s3_bucket.uploads.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "uploads" {
  bucket = aws_s3_bucket.uploads.id
  rule {
    apply_server_side_encryption_by_default { sse_algorithm = "AES256" }
    bucket_key_enabled = true
  }
}

resource "aws_s3_bucket_versioning" "uploads" {
  bucket = aws_s3_bucket.uploads.id
  versioning_configuration { status = "Enabled" }
}

resource "aws_s3_bucket_cors_configuration" "uploads" {
  bucket = aws_s3_bucket.uploads.id
  cors_rule {
    allowed_headers = ["*"]
    allowed_methods = ["GET", "PUT", "POST", "DELETE", "HEAD"]
    allowed_origins = ["https://*.moeware.com", "http://localhost:5173", "http://localhost:3000"]
    expose_headers  = ["ETag", "Content-Length"]
    max_age_seconds = 3600
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "uploads" {
  bucket = aws_s3_bucket.uploads.id

  rule {
    id     = "cleanup-old-versions"
    status = "Enabled"
    filter {}
    noncurrent_version_expiration { noncurrent_days = 90 }
    noncurrent_version_transition {
      noncurrent_days = 30
      storage_class   = "STANDARD_IA"
    }
  }

  rule {
    id     = "cleanup-incomplete-uploads"
    status = "Enabled"
    filter {}
    abort_incomplete_multipart_upload { days_after_initiation = 7 }
  }
}

# ---- REPORTS BUCKET --------------------------------------------------------

resource "aws_s3_bucket" "reports" {
  bucket = "${var.s3_bucket_prefix}-reports-${var.environment}-${local.suffix}"
  tags   = merge(var.tags, { Name = "${var.name_prefix}-reports", Purpose = "Generated reports from CronJobs" })
}

resource "aws_s3_bucket_public_access_block" "reports" {
  bucket                  = aws_s3_bucket.reports.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "reports" {
  bucket = aws_s3_bucket.reports.id
  rule {
    apply_server_side_encryption_by_default { sse_algorithm = "AES256" }
  }
}

resource "aws_s3_bucket_versioning" "reports" {
  bucket = aws_s3_bucket.reports.id
  versioning_configuration { status = "Enabled" }
}

resource "aws_s3_bucket_lifecycle_configuration" "reports" {
  bucket = aws_s3_bucket.reports.id
  rule {
    id     = "expire-old-reports"
    status = "Enabled"
    filter {}
    transition {
      days          = 30
      storage_class = "STANDARD_IA"
    }
    transition {
      days          = 90
      storage_class = "GLACIER"
    }
    expiration { days = 365 }
    noncurrent_version_expiration { noncurrent_days = 30 }
  }
}

# ---- BACKUPS BUCKET --------------------------------------------------------

resource "aws_s3_bucket" "backups" {
  bucket = "${var.s3_bucket_prefix}-backups-${var.environment}-${local.suffix}"
  tags   = merge(var.tags, { Name = "${var.name_prefix}-backups", Purpose = "Database pg_dump backups" })
}

resource "aws_s3_bucket_public_access_block" "backups" {
  bucket                  = aws_s3_bucket.backups.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "backups" {
  bucket = aws_s3_bucket.backups.id
  rule {
    apply_server_side_encryption_by_default { sse_algorithm = "AES256" }
  }
}

resource "aws_s3_bucket_versioning" "backups" {
  bucket = aws_s3_bucket.backups.id
  versioning_configuration { status = "Enabled" }
}

resource "aws_s3_bucket_lifecycle_configuration" "backups" {
  bucket = aws_s3_bucket.backups.id
  rule {
    id     = "backup-retention"
    status = "Enabled"
    filter {}
    transition {
      days          = 30
      storage_class = "GLACIER"
    }
    expiration { days = 365 }
    noncurrent_version_expiration { noncurrent_days = 7 }
  }
}
