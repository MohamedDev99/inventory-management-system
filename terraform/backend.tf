##############################################################################
# backend.tf — Remote State Configuration
#
# WHY: Terraform's state file tracks every resource it manages.
#      Storing it locally means:
#        - Your teammates can't collaborate (they have no state)
#        - One bad commit and you lose track of your infrastructure
#        - No locking = two people apply at the same time = corruption
#
# SOLUTION: S3 for storage + DynamoDB for locking.
#
# SETUP BEFORE USE:
#   1. Create the S3 bucket manually ONCE:
#        aws s3api create-bucket --bucket moeware-ims-terraform-state \
#          --region us-east-1
#        aws s3api put-bucket-versioning \
#          --bucket moeware-ims-terraform-state \
#          --versioning-configuration Status=Enabled
#        aws s3api put-bucket-encryption \
#          --bucket moeware-ims-terraform-state \
#          --server-side-encryption-configuration \
#          '{"Rules":[{"ApplyServerSideEncryptionByDefault":{"SSEAlgorithm":"AES256"}}]}'
#
#   2. Create the DynamoDB table manually ONCE:
#        aws dynamodb create-table \
#          --table-name moeware-ims-terraform-locks \
#          --attribute-definitions AttributeName=LockID,AttributeType=S \
#          --key-schema AttributeName=LockID,KeyType=HASH \
#          --billing-mode PAY_PER_REQUEST \
#          --region us-east-1
#
#   3. Then run: terraform init
##############################################################################

terraform {

   # Minimum Terraform version required for this configuration.
   # Ensures everyone on the team uses a compatible version.
   required_version = ">=1.6.0"

   # Provider version constraints.
   # "~> 5.0" means "5.x.x but not 6.0" — allows minor updates, blocks breaking changes.
   required_providers {
      aws = {
         source  = "hashicorp/aws"
         version = "~> 5.0"
      }
      random = {
         source  = "hashicorp/random"
         version = "~> 3.5"
      }
  }


   # Remote state backend.
   # After `terraform init`, state is stored here instead of locally.
   backend "s3" {
      bucket         = "moeware-ims-terraform-state"
      key            = "ims/terraform.tfstate" # Path inside the bucket
      region         = "us-east-1"
      encrypt        = true # Encrypt state at rest
      dynamodb_table = "moeware-ims-terraform-locks" # Lock table


      # The key will be prefixed by workspace name automatically:
      # - default workspace: ims/terraform.tfstate
      # - dev workspace:     ims/env:/dev/terraform.tfstate
      # - prod workspace:    ims/env:/prod/terraform.tfstate
      workspace_key_prefix = "ims"
   }
}