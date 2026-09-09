#!/bin/bash
##############################################################################
# scripts/setup-backend.sh — Bootstrap Remote State Infrastructure
#
# Run this ONCE before ever running terraform init.
# Creates the S3 bucket and DynamoDB table for remote state storage.
#
# Usage:
#   AWS_PROFILE=your-profile ./scripts/setup-backend.sh
#   AWS_DEFAULT_REGION=us-east-1 ./scripts/setup-backend.sh
#
# These resources are created with the AWS CLI (not Terraform) because
# Terraform can't store its state before the state backend exists — 
# it's the classic "chicken and egg" problem.
##############################################################################

set -euo pipefail

AWS_REGION="${AWS_DEFAULT_REGION:-us-east-1}"
STATE_BUCKET="moeware-ims-terraform-state"
LOCK_TABLE="moeware-ims-terraform-locks"

echo "🚀 Setting up Terraform remote state backend..."
echo "   Region: ${AWS_REGION}"
echo "   Bucket: ${STATE_BUCKET}"
echo "   Table:  ${LOCK_TABLE}"
echo ""

# ============================================================================
# S3 BUCKET FOR STATE STORAGE
# ============================================================================

echo "📦 Creating S3 bucket for state storage..."

# Check if bucket already exists
if aws s3api head-bucket --bucket "${STATE_BUCKET}" 2>/dev/null; then
  echo "   ✅ Bucket already exists: ${STATE_BUCKET}"
else
  # Create the bucket
  if [[ "$AWS_REGION" == "us-east-1" ]]; then
    # us-east-1 doesn't accept LocationConstraint
    aws s3api create-bucket \
      --bucket "${STATE_BUCKET}" \
      --region "${AWS_REGION}"
  else
    aws s3api create-bucket \
      --bucket "${STATE_BUCKET}" \
      --region "${AWS_REGION}" \
      --create-bucket-configuration LocationConstraint="${AWS_REGION}"
  fi

  # Enable versioning (required — allows recovery of previous state files)
  aws s3api put-bucket-versioning \
    --bucket "${STATE_BUCKET}" \
    --versioning-configuration Status=Enabled

  # Enable server-side encryption (state files contain sensitive data!)
  aws s3api put-bucket-encryption \
    --bucket "${STATE_BUCKET}" \
    --server-side-encryption-configuration '{
      "Rules": [{
        "ApplyServerSideEncryptionByDefault": {
          "SSEAlgorithm": "AES256"
        }
      }]
    }'

  # Block all public access (state files must never be public)
  aws s3api put-public-access-block \
    --bucket "${STATE_BUCKET}" \
    --public-access-block-configuration \
      "BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true"

  echo "   ✅ Bucket created and configured: ${STATE_BUCKET}"
fi

# ============================================================================
# DYNAMODB TABLE FOR STATE LOCKING
# ============================================================================

echo ""
echo "🔒 Creating DynamoDB table for state locking..."

# Check if table already exists
TABLE_STATUS=$(aws dynamodb describe-table \
  --table-name "${LOCK_TABLE}" \
  --region "${AWS_REGION}" \
  --query 'Table.TableStatus' \
  --output text 2>/dev/null || echo "NOT_FOUND")

if [[ "$TABLE_STATUS" == "ACTIVE" ]]; then
  echo "   ✅ DynamoDB table already exists: ${LOCK_TABLE}"
else
  aws dynamodb create-table \
    --table-name "${LOCK_TABLE}" \
    --attribute-definitions AttributeName=LockID,AttributeType=S \
    --key-schema AttributeName=LockID,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --region "${AWS_REGION}"

  # Wait for table to become active
  echo "   ⏳ Waiting for table to become active..."
  aws dynamodb wait table-exists \
    --table-name "${LOCK_TABLE}" \
    --region "${AWS_REGION}"

  echo "   ✅ DynamoDB table created: ${LOCK_TABLE}"
fi

# ============================================================================
# DONE
# ============================================================================

echo ""
echo "✅ Remote state backend is ready!"
echo ""
echo "Next steps:"
echo "  1. Run: terraform init"
echo "  2. Run: ./scripts/deploy.sh dev plan"
echo "  3. Run: ./scripts/deploy.sh dev apply"
