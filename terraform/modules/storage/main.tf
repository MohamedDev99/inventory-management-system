##############################################################################
# modules/storage/main.tf
#
# Creates three S3 buckets:
#   1. uploads  — Product images, PO attachments, invoice PDFs uploaded by users
#   2. reports  — Generated inventory/sales reports (async jobs write here)
#   3. backups  — Database dumps from scheduled backup scripts
#
# Each bucket has:
#   - Encryption at rest (AES-256)
#   - Public access blocked (no accidental exposure)
#   - Versioning (protect against accidental deletion/overwrite)
#   - Lifecycle rules (auto-delete old files to control costs)
#   - CORS (allow frontend to upload directly)
##############################################################################

# Random suffix ensures globally unique bucket names
# S3 bucket names are GLOBAL across ALL AWS accounts worldwide
resource "random_id" "bucket_suffix" {
  byte_length = 4 # Generates 8-char hex string like "a1b2c3d4"
}

locals {
  bucket_suffix = random_id.bucket_suffix.hex
}

# ============================================================================
# HELPER: Reusable bucket configuration via locals
# We create the same security config for all buckets using a shared pattern
# ============================================================================

# ============================================================================
# BUCKET 1: UPLOADS — Product images, attachments
# ============================================================================

resource "aws_s3_bucket" "uploads" {
  bucket = "${var.s3_bucket_name_prefix}-uploads-${var.environment}-${local.bucket_suffix}"

  tags = merge(var.tags, {
    Name    = "${var.name_prefix}-uploads"
    Purpose = "Product images, order attachments, invoice PDFs"
  })
}

# Block ALL public access — never allow public buckets for app data
resource "aws_s3_bucket_public_access_block" "uploads" {
  bucket = aws_s3_bucket.uploads.id

  block_public_acls       = true # Reject requests with public ACLs
  block_public_policy     = true # Reject bucket policies that grant public access
  ignore_public_acls      = true # Ignore existing public ACLs
  restrict_public_buckets = true # Restrict access to authorized AWS accounts only
}

# Encrypt all objects at rest using AES-256
resource "aws_s3_bucket_server_side_encryption_configuration" "uploads" {
  bucket = aws_s3_bucket.uploads.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256" # Free, AWS-managed encryption
    }
    bucket_key_enabled = true # Reduces KMS request costs (if using KMS)
  }
}

# Versioning — keep old versions when files are overwritten
# Allows you to recover from accidental deletes or bad uploads
resource "aws_s3_bucket_versioning" "uploads" {
  bucket = aws_s3_bucket.uploads.id

  versioning_configuration {
    status = var.enable_versioning ? "Enabled" : "Disabled"
  }
}

# Lifecycle rules — automatically delete old versions to control storage costs
resource "aws_s3_bucket_lifecycle_configuration" "uploads" {
  bucket = aws_s3_bucket.uploads.id

  # Rule 1: Clean up old versions of files (keeps costs down)
  rule {
    id     = "cleanup-old-versions"
    status = "Enabled"

    filter {} # Empty filter = apply to ALL objects in the bucket

    noncurrent_version_expiration {
      noncurrent_days = 90 # Delete versions older than 90 days
    }

    noncurrent_version_transition {
      noncurrent_days = 30
      storage_class   = "STANDARD_IA" # Move to cheaper storage after 30 days
    }
  }

  # Rule 2: Move large infrequently-accessed files to cheaper storage
  rule {
    id     = "move-to-ia"
    status = "Enabled"

    filter {
      # Only apply to files in the 'reports/' prefix (typically large)
      prefix = "reports/"
    }

    transition {
      days          = 30
      storage_class = "STANDARD_IA" # ~58% cheaper than STANDARD after 30 days
    }

    transition {
      days          = 90
      storage_class = "GLACIER" # ~80% cheaper — for archival, retrieval takes hours
    }
  }

  # Rule 3: Clean up incomplete multipart uploads (prevents orphaned chunks)
  rule {
    id     = "cleanup-incomplete-uploads"
    status = "Enabled"

    filter {} # Empty filter = apply to ALL objects in the bucket

    abort_incomplete_multipart_upload {
      days_after_initiation = 7 # Clean up after 7 days
    }
  }
}

# CORS configuration — allows the React frontend to upload directly to S3
# (instead of routing file uploads through the backend, which is slower)
resource "aws_s3_bucket_cors_configuration" "uploads" {
  bucket = aws_s3_bucket.uploads.id

  cors_rule {
    allowed_headers = ["*"]
    allowed_methods = ["GET", "PUT", "POST", "DELETE", "HEAD"]
    allowed_origins = [
      "https://*.moeware.com",
      "http://localhost:5173",  # Vite dev server
      "http://localhost:3000",  # Docker frontend
    ]
    expose_headers  = ["ETag", "Content-Length"]
    max_age_seconds = 3600
  }
}

# ============================================================================
# BUCKET 2: REPORTS — Generated inventory/sales reports
# ============================================================================

resource "aws_s3_bucket" "reports" {
  bucket = "${var.s3_bucket_name_prefix}-reports-${var.environment}-${local.bucket_suffix}"

  tags = merge(var.tags, {
    Name    = "${var.name_prefix}-reports"
    Purpose = "Generated reports: stock valuation, sales analysis, movement history"
  })
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
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_versioning" "reports" {
  bucket = aws_s3_bucket.reports.id
  versioning_configuration {
    status = "Enabled"
  }
}

# Reports expire after 1 year — they can always be regenerated
resource "aws_s3_bucket_lifecycle_configuration" "reports" {
  bucket = aws_s3_bucket.reports.id

  rule {
    id     = "expire-old-reports"
    status = "Enabled"

    filter {} # Empty filter = apply to ALL objects

    transition {
      days          = 30
      storage_class = "STANDARD_IA"
    }

    transition {
      days          = 90
      storage_class = "GLACIER"
    }

    expiration {
      days = 365 # Delete reports after 1 year
    }

    noncurrent_version_expiration {
      noncurrent_days = 30
    }
  }
}

# ============================================================================
# BUCKET 3: BACKUPS — Database dumps
# ============================================================================

resource "aws_s3_bucket" "backups" {
  bucket = "${var.s3_bucket_name_prefix}-backups-${var.environment}-${local.bucket_suffix}"

  tags = merge(var.tags, {
    Name    = "${var.name_prefix}-backups"
    Purpose = "Database backup dumps from pg_dump cron jobs"
  })
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
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_versioning" "backups" {
  bucket = aws_s3_bucket.backups.id
  versioning_configuration {
    status = "Enabled"
  }
}

# Keep 30 daily backups, then move to Glacier for 1 year
resource "aws_s3_bucket_lifecycle_configuration" "backups" {
  bucket = aws_s3_bucket.backups.id

  rule {
    id     = "backup-retention"
    status = "Enabled"

    filter {} # Empty filter = apply to ALL objects

    transition {
      days          = 30
      storage_class = "GLACIER" # Long-term archival after 30 days
    }

    expiration {
      days = 365 # Delete after 1 year
    }

    noncurrent_version_expiration {
      noncurrent_days = 7
    }
  }
}

# ============================================================================
# IAM POLICY ATTACHMENT — Grant EC2 instances access to all three buckets
# ============================================================================

resource "aws_iam_role_policy" "s3_full_access" {
  name = "${var.name_prefix}-s3-full-access"
  role = var.ec2_instance_role_name

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "UploadsFullAccess"
        Effect = "Allow"
        Action = [
          "s3:GetObject",
          "s3:PutObject",
          "s3:DeleteObject",
          "s3:ListBucket",
          "s3:GetObjectVersion",
          "s3:DeleteObjectVersion"
        ]
        Resource = [
          aws_s3_bucket.uploads.arn,
          "${aws_s3_bucket.uploads.arn}/*"
        ]
      },
      {
        Sid    = "ReportsReadWrite"
        Effect = "Allow"
        Action = [
          "s3:GetObject",
          "s3:PutObject",
          "s3:ListBucket"
        ]
        Resource = [
          aws_s3_bucket.reports.arn,
          "${aws_s3_bucket.reports.arn}/*"
        ]
      },
      {
        Sid    = "BackupsWrite"
        Effect = "Allow"
        Action = [
          "s3:PutObject",
          "s3:ListBucket"
        ]
        Resource = [
          aws_s3_bucket.backups.arn,
          "${aws_s3_bucket.backups.arn}/*"
        ]
      }
    ]
  })
}
