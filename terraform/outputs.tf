##############################################################################
# outputs.tf — Output Values
#
# Outputs are the "return values" of your Terraform configuration.
# After `terraform apply`, these values are printed and can be:
#   - Read by other Terraform configurations (data "terraform_remote_state")
#   - Used by scripts: terraform output -raw alb_dns_name
#   - Displayed in CI/CD logs for visibility
#
# Mark sensitive outputs with `sensitive = true` to hide from logs.
##############################################################################