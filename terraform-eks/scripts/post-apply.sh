#!/bin/bash
##############################################################################
# scripts/post-apply.sh — Run AFTER terraform apply
#
# This script bridges Terraform (infrastructure) and kubectl (application).
# It:
#   1. Configures kubectl to point at the new EKS cluster
#   2. Annotates the K8s ServiceAccount with the IRSA role ARN
#      (so pods can access S3 and Secrets Manager without credentials)
#   3. Prints the values you need to paste into your k8s/ ConfigMaps/Secrets
#
# Usage:
#   ./scripts/post-apply.sh dev
#   ./scripts/post-apply.sh prod
##############################################################################

set -euo pipefail

ENVIRONMENT="${1:-dev}"

echo "🔧 Post-apply setup for: $ENVIRONMENT"

cd "$(dirname "$0")/.."

terraform workspace select "$ENVIRONMENT" 2>/dev/null

# ============================================================================
# Step 1 — Configure kubectl
# ============================================================================

echo ""
echo "📡 Configuring kubectl..."

CLUSTER_NAME=$(terraform output -raw cluster_name)
AWS_REGION=$(terraform output -raw aws_region 2>/dev/null || echo "us-east-1")

aws eks update-kubeconfig \
  --region "$AWS_REGION" \
  --name "$CLUSTER_NAME"

echo "✅ kubectl configured for cluster: $CLUSTER_NAME"

# Verify connection
kubectl cluster-info
kubectl get nodes

# ============================================================================
# Step 2 — Apply namespaces first (everything else depends on them)
# ============================================================================

echo ""
echo "📦 Applying namespaces..."
kubectl apply -f ../k8s/namespaces/namespaces.yaml

# ============================================================================
# Step 3 — Annotate ServiceAccount with IRSA role ARN
#
# This is the critical step that links a K8s ServiceAccount to an IAM Role.
# Without this, pods can't access S3 or Secrets Manager.
#
# Your backend/deployment.yaml should reference serviceAccountName: ims-backend
# ============================================================================

echo ""
echo "🔐 Configuring IRSA for pod S3/Secrets access..."

IRSA_ROLE_ARN=$(terraform output -raw pod_irsa_role_arn)

# Create the ServiceAccount if it doesn't exist, then annotate it
kubectl create serviceaccount ims-backend \
  --namespace inventory-app \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl annotate serviceaccount ims-backend \
  --namespace inventory-app \
  --overwrite \
  "eks.amazonaws.com/role-arn=${IRSA_ROLE_ARN}"

echo "✅ ServiceAccount 'ims-backend' annotated with IRSA role"

# ============================================================================
# Step 4 — Print values needed for k8s/ ConfigMaps and Secrets
# ============================================================================

echo ""
echo "════════════════════════════════════════════════════════"
echo "  VALUES TO UPDATE IN YOUR k8s/ MANIFESTS"
echo "════════════════════════════════════════════════════════"
echo ""
echo "📄 k8s/configmaps/configmaps.yaml — update these values:"
echo ""
echo "  AWS_REGION:         $AWS_REGION"
echo "  S3_UPLOADS_BUCKET:  $(terraform output -raw uploads_bucket_name)"
echo "  S3_REPORTS_BUCKET:  $(terraform output -raw reports_bucket_name)"
echo "  S3_BACKUPS_BUCKET:  $(terraform output -raw backups_bucket_name)"

DB_ENDPOINT=$(terraform output -raw db_endpoint 2>/dev/null || echo "")
if [[ -n "$DB_ENDPOINT" && "$DB_ENDPOINT" != "not-created"* ]]; then
  echo "  DB_HOST:            $DB_ENDPOINT"
  echo ""
  echo "  ⚠️  Update k8s/secrets/secrets.yaml with the DB password:"
  echo "  Secret ARN: $(terraform output -raw db_secret_arn)"
  echo "  Fetch with: aws secretsmanager get-secret-value --secret-id $(terraform output -raw db_secret_arn) --query SecretString --output text"
else
  echo ""
  echo "  DB_HOST: using in-cluster StatefulSet (dev mode)"
  echo "           → postgres-service.inventory-app.svc.cluster.local"
fi

echo ""
echo "════════════════════════════════════════════════════════"
echo "  NEXT STEPS"
echo "════════════════════════════════════════════════════════"
echo ""
echo "1. Update k8s/configmaps/configmaps.yaml with the values above"
echo "2. Update k8s/secrets/secrets.yaml with your actual secrets"
echo "3. Deploy your application:"
echo ""
echo "   # Option A — raw manifests:"
echo "   kubectl apply -f k8s/configmaps/configmaps.yaml"
echo "   kubectl apply -f k8s/secrets/secrets.yaml"
echo "   kubectl apply -f k8s/database/statefulset.yaml   # dev only"
echo "   kubectl apply -f k8s/backend/deployment.yaml"
echo "   kubectl apply -f k8s/frontend/deployment.yaml"
echo "   kubectl apply -f k8s/ingress/ingress.yaml"
echo "   kubectl apply -f k8s/hpa/hpa.yaml"
echo "   kubectl apply -f k8s/cronjobs/cronjobs.yaml"
echo ""
echo "   # Option B — Helm:"
echo "   helm install inventory ./k8s/helm/inventory-chart \\"
echo "     -f k8s/helm/inventory-chart/values-${ENVIRONMENT}.yaml \\"
echo "     -n inventory-app"
echo ""
echo "4. Get your app URL:"
echo "   kubectl get svc -n ingress-nginx"
