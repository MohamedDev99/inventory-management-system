#!/bin/bash
##############################################################################
# scripts/deploy.sh — Terraform Deployment Script
#
# Usage:
#   ./scripts/deploy.sh dev plan              # Preview changes for dev
#   ./scripts/deploy.sh dev apply             # Apply changes to dev
#   ./scripts/deploy.sh prod apply            # Deploy to production
#   ./scripts/deploy.sh prod destroy          # Destroy environment (CAREFUL!)
#
# The script:
#   1. Validates the environment argument
#   2. Selects (or creates) the correct workspace
#   3. Runs terraform init to ensure backend is configured
#   4. Runs the requested command with the right var file
##############################################################################

set -euo pipefail

# ============================================================================
# ARGUMENT VALIDATION
# ============================================================================

ENVIRONMENT="${1:-}"
COMMAND="${2:-plan}"

if [[ -z "$ENVIRONMENT" ]]; then
  echo "❌ ERROR: Environment required."
  echo ""
  echo "Usage: $0 <environment> <command>"
  echo "       $0 dev plan"
  echo "       $0 staging apply"
  echo "       $0 prod apply"
  exit 1
fi

VALID_ENVIRONMENTS=("dev" "staging" "prod")
if [[ ! " ${VALID_ENVIRONMENTS[*]} " =~ " ${ENVIRONMENT} " ]]; then
  echo "❌ ERROR: Invalid environment '${ENVIRONMENT}'"
  echo "Valid environments: ${VALID_ENVIRONMENTS[*]}"
  exit 1
fi

VALID_COMMANDS=("plan" "apply" "destroy" "output" "refresh")
if [[ ! " ${VALID_COMMANDS[*]} " =~ " ${COMMAND} " ]]; then
  echo "❌ ERROR: Invalid command '${COMMAND}'"
  echo "Valid commands: ${VALID_COMMANDS[*]}"
  exit 1
fi

VAR_FILE="environments/${ENVIRONMENT}/terraform.tfvars"

if [[ ! -f "$VAR_FILE" ]]; then
  echo "❌ ERROR: var file not found: $VAR_FILE"
  exit 1
fi

# ============================================================================
# PRODUCTION SAFETY CHECKS
# ============================================================================

if [[ "$ENVIRONMENT" == "prod" ]]; then
  echo ""
  echo "⚠️  ═══════════════════════════════════════════════════════"
  echo "⚠️   WARNING: You are about to modify PRODUCTION"
  echo "⚠️  ═══════════════════════════════════════════════════════"
  echo ""

  if [[ "$COMMAND" == "destroy" ]]; then
    echo "🚨 DESTROY on production?! This will delete EVERYTHING!"
    echo ""
    read -r -p "Type 'destroy production' to confirm: " CONFIRM
    if [[ "$CONFIRM" != "destroy production" ]]; then
      echo "Aborted."
      exit 1
    fi
  elif [[ "$COMMAND" == "apply" ]]; then
    read -r -p "Are you sure you want to apply to production? (yes/no): " CONFIRM
    if [[ "$CONFIRM" != "yes" ]]; then
      echo "Aborted."
      exit 1
    fi
  fi
fi

# ============================================================================
# TERRAFORM EXECUTION
# ============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TERRAFORM_DIR="$(dirname "$SCRIPT_DIR")"

cd "$TERRAFORM_DIR"

echo ""
echo "🔧 Environment: ${ENVIRONMENT}"
echo "🔧 Command:     terraform ${COMMAND}"
echo "🔧 Var file:    ${VAR_FILE}"
echo ""

# Initialize (downloads providers, configures backend)
echo "📦 Initializing Terraform..."
terraform init -input=false

# Select workspace (create if it doesn't exist)
echo "🌍 Selecting workspace: ${ENVIRONMENT}"
terraform workspace select "${ENVIRONMENT}" 2>/dev/null || terraform workspace new "${ENVIRONMENT}"

# Run the command
case "$COMMAND" in
  "plan")
    echo "📋 Planning changes..."
    terraform plan \
      -var-file="${VAR_FILE}" \
      -out="tfplan-${ENVIRONMENT}" \
      -input=false
    echo ""
    echo "✅ Plan complete. Review the output above."
    echo "   To apply: $0 ${ENVIRONMENT} apply"
    ;;

  "apply")
    # Check if a plan file exists from a recent plan
    if [[ -f "tfplan-${ENVIRONMENT}" ]]; then
      echo "📝 Found existing plan file. Applying it..."
      terraform apply "tfplan-${ENVIRONMENT}"
      rm -f "tfplan-${ENVIRONMENT}"
    else
      echo "⚠️  No plan file found. Running plan+apply together..."
      terraform apply \
        -var-file="${VAR_FILE}" \
        -input=false \
        -auto-approve
    fi

    echo ""
    echo "✅ Apply complete!"
    echo ""
    echo "📊 Deployment summary:"
    terraform output deployment_summary
    ;;

  "destroy")
    echo "💥 Destroying environment: ${ENVIRONMENT}"
    terraform destroy \
      -var-file="${VAR_FILE}" \
      -input=false \
      -auto-approve
    echo "✅ Environment destroyed."
    ;;

  "output")
    terraform output
    ;;

  "refresh")
    terraform refresh -var-file="${VAR_FILE}"
    ;;
esac
