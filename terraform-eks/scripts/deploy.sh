#!/bin/bash
##############################################################################
# scripts/deploy.sh — Terraform + kubectl deployment wrapper
#
# Usage:
#   ./scripts/deploy.sh dev plan
#   ./scripts/deploy.sh dev apply
#   ./scripts/deploy.sh prod apply
#   ./scripts/deploy.sh dev destroy
##############################################################################

set -euo pipefail

ENVIRONMENT="${1:-}"
COMMAND="${2:-plan}"

if [[ -z "$ENVIRONMENT" ]]; then
  echo "Usage: $0 <dev|prod> <plan|apply|destroy>"
  exit 1
fi

VAR_FILE="environments/${ENVIRONMENT}/terraform.tfvars"

if [[ ! -f "$VAR_FILE" ]]; then
  echo "❌ Var file not found: $VAR_FILE"
  exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$(dirname "$SCRIPT_DIR")"

# Production guard
if [[ "$ENVIRONMENT" == "prod" && "$COMMAND" == "apply" ]]; then
  echo "⚠️  Deploying to PRODUCTION"
  read -r -p "Type 'yes' to confirm: " CONFIRM
  [[ "$CONFIRM" == "yes" ]] || { echo "Aborted."; exit 1; }
fi

echo "🔧 Environment: $ENVIRONMENT | Command: $COMMAND"

terraform init -input=false
terraform workspace select "$ENVIRONMENT" 2>/dev/null || terraform workspace new "$ENVIRONMENT"

case "$COMMAND" in
  plan)
    terraform plan -var-file="$VAR_FILE" -input=false -out="tfplan-${ENVIRONMENT}"
    echo "✅ Plan saved. Run: $0 $ENVIRONMENT apply"
    ;;
  apply)
    if [[ -f "tfplan-${ENVIRONMENT}" ]]; then
      terraform apply "tfplan-${ENVIRONMENT}"
      rm -f "tfplan-${ENVIRONMENT}"
    else
      terraform apply -var-file="$VAR_FILE" -input=false -auto-approve
    fi
    echo "✅ Infrastructure ready!"
    echo ""
    echo "Next step: configure kubectl and deploy K8s manifests:"
    echo "  ./scripts/post-apply.sh $ENVIRONMENT"
    ;;
  destroy)
    terraform destroy -var-file="$VAR_FILE" -input=false -auto-approve
    ;;
esac
