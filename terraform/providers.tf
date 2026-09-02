##############################################################################
# providers.tf — AWS Provider Configuration
#
# The provider is the plugin that translates your Terraform code into
# actual API calls to AWS. Every resource you create goes through it.
#
# AUTHENTICATION (in order of precedence):
#   1. Environment variables: AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY
#   2. Shared credentials file: ~/.aws/credentials
#   3. IAM role (when running on EC2 or in CI/CD)
#
# BEST PRACTICE: Never hardcode credentials in .tf files.
#   For local dev: use `aws configure` to set up ~/.aws/credentials
#   For CI/CD: use GitHub Secrets + OIDC (no long-lived keys)
##############################################################################

provider "aws" {
    region = "us-east-1"

    # Default tags applied to EVERY resource created by Terraform.
    # This is critical for cost tracking and resource management.
    # You'll see these tags in AWS Cost Explorer to understand what each
    # environment/project is costing you.
    default_tags {
      tags = {
        Project     = "MoeWare-IMS"
        ManagedBy   = "Terraform"
       #Environment = local.environment
       #Owner       = var.owner_email
        Repository  = "https://github.com/MomamedDev99/inventory-management-system"
      }
    }
}

# A second provider alias for a different region.
# Used for cross-region resources like CloudFront, Route53, or replication.
# Uncomment if you need multi-region resources:
#
# provider "aws" {
#   alias  = "us_west"
#   region = "us-west-2"
# }