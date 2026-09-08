# MoeWare IMS — Terraform EKS Infrastructure

Kubernetes infrastructure on AWS EKS, managed with Terraform.
Start here → [Bootstrap (Day 1)](#day-1-bootstrap) then graduate to [Full Setup](#full-setup).

---

## Architecture

```
                    Internet
                       │
              ┌────────▼─────────┐
              │  AWS NLB          │  ← created automatically by ingress-nginx
              │  (public subnet)  │
              └────────┬──────────┘
                       │ HTTPS
              ┌────────▼──────────────────────────┐
              │         EKS CLUSTER               │
              │  ┌─────────────────────────────┐  │
              │  │    inventory-app namespace  │  │
              │  │                             │  │
              │  │  [frontend pod] ──► nginx   │  │
              │  │  [backend pod]  ──► :8080   │  │  private subnets
              │  │  [postgres pod] ──► :5432   │  │  (dev only)
              │  │  [cronjob pods] ──► Fargate │  │
              │  └─────────────────────────────┘  │
              └──────────────┬────────────────────┘
                             │ (prod only)
                    ┌────────▼────────┐
                    │  RDS PostgreSQL  │  database subnets
                    │  (Multi-AZ)     │  no internet access
                    └─────────────────┘
```

---

## Project Structure

```
terraform-eks/
├── bootstrap/              ← Day 1: minimal EKS from scratch (learning)
│   └── main.tf             ← single file, no modules — read this first
│
├── modules/
│   ├── networking/         ← VPC with EKS-specific subnet tags
│   ├── eks/                ← EKS from scratch (IAM, cluster, nodes, Fargate)
│   │   ├── iam.tf          ← 4 IAM roles (cluster, node, Fargate, IRSA)
│   │   └── main.tf         ← cluster, security groups, node group, add-ons
│   ├── eks-module/         ← same cluster using terraform-aws-modules/eks
│   ├── database/           ← RDS PostgreSQL (prod only, count=0 in dev)
│   └── storage/            ← S3 buckets (uploads, reports, backups)
│
├── environments/
│   ├── local/README.md     ← Minikube/Kind setup (no Terraform needed)
│   ├── dev/terraform.tfvars
│   └── prod/terraform.tfvars
│
├── scripts/
│   ├── deploy.sh           ← terraform plan/apply/destroy wrapper
│   └── post-apply.sh       ← configures kubectl + prints K8s values after apply
│
├── backend.tf  providers.tf  variables.tf  locals.tf  main.tf  outputs.tf
```

---

## Day 1: Bootstrap

Learn what EKS actually creates before using the full module setup.

```bash
cd bootstrap/

# Initialize (local state — no S3 backend needed for learning)
terraform init

# See what will be created
terraform plan

# Create the cluster (~10 minutes)
terraform apply

# Configure kubectl
aws eks update-kubeconfig --region us-east-1 --name moeware-bootstrap

# Verify
kubectl get nodes
kubectl get pods -A

# Try deploying your first app
kubectl apply -f ../k8s/namespaces/namespaces.yaml
kubectl apply -f ../k8s/backend/deployment.yaml

# Destroy when done learning
terraform destroy
```

---

## Full Setup

After understanding bootstrap, use the production-ready modular setup.

### Prerequisites

```bash
# Tools needed
brew install terraform awscli kubectl helm

# AWS credentials
aws configure
aws sts get-caller-identity  # verify it works

# Create remote state backend (one time)
chmod +x scripts/setup-backend.sh
./scripts/setup-backend.sh
```

### Deploy Dev

```bash
cd terraform-eks/

terraform init

# Plan and review
./scripts/deploy.sh dev plan

# Apply (~15-20 minutes — EKS takes time)
./scripts/deploy.sh dev apply

# Configure kubectl and get values for k8s/ manifests
./scripts/post-apply.sh dev

# Deploy your application
kubectl apply -f k8s/configmaps/configmaps.yaml
kubectl apply -f k8s/secrets/secrets.yaml
kubectl apply -f k8s/database/statefulset.yaml   # dev: in-cluster PostgreSQL
kubectl apply -f k8s/backend/deployment.yaml
kubectl apply -f k8s/frontend/deployment.yaml
kubectl apply -f k8s/ingress/ingress.yaml
kubectl apply -f k8s/hpa/hpa.yaml
kubectl apply -f k8s/cronjobs/cronjobs.yaml

# Or use Helm
helm install inventory ./k8s/helm/inventory-chart \
  -f k8s/helm/inventory-chart/values-dev.yaml \
  -n inventory-app
```

### Deploy Prod

```bash
terraform workspace select prod
./scripts/deploy.sh prod plan   # review carefully
./scripts/deploy.sh prod apply  # requires manual confirmation
./scripts/post-apply.sh prod

# Prod uses RDS (no StatefulSet) — skip statefulset.yaml
helm install inventory ./k8s/helm/inventory-chart \
  -f k8s/helm/inventory-chart/values-prod.yaml \
  -n inventory-app
```

---

## What Terraform Manages vs kubectl/Helm

| Layer | Managed By | Examples |
|-------|-----------|---------|
| VPC, subnets, IGW, NAT | Terraform | networking module |
| EKS cluster + node groups | Terraform | eks module |
| Fargate profiles | Terraform | eks module |
| IAM roles (cluster, node, IRSA) | Terraform | eks/iam.tf |
| RDS PostgreSQL (prod) | Terraform | database module |
| S3 buckets | Terraform | storage module |
| nginx-ingress (system) | Terraform helm_release | main.tf |
| cert-manager (system) | Terraform helm_release | main.tf |
| Namespaces | kubectl | k8s/namespaces/ |
| ConfigMaps, Secrets | kubectl | k8s/configmaps/, k8s/secrets/ |
| Backend/Frontend Deployments | kubectl or Helm | k8s/backend/, k8s/frontend/ |
| PostgreSQL StatefulSet (dev) | kubectl | k8s/database/ |
| Ingress rules | kubectl or Helm | k8s/ingress/ |
| HPA, CronJobs | kubectl or Helm | k8s/hpa/, k8s/cronjobs/ |
| App Helm chart | Helm (CI/CD) | k8s/helm/inventory-chart |

---

## IRSA — How Pods Access AWS Without Credentials

After running `post-apply.sh`, your `ims-backend` ServiceAccount is annotated:

```yaml
# This annotation is added automatically by post-apply.sh
apiVersion: v1
kind: ServiceAccount
metadata:
  name: ims-backend
  namespace: inventory-app
  annotations:
    eks.amazonaws.com/role-arn: arn:aws:iam::123456789:role/moeware-ims-dev-eks-pod-role
```

In your `backend/deployment.yaml`, reference this ServiceAccount:

```yaml
spec:
  serviceAccountName: ims-backend  # ← add this line
  containers:
    - name: backend
      # AWS SDK automatically picks up credentials — no env vars needed
```

The pod can now call S3 and Secrets Manager without any credentials stored in K8s.

---

## Key Commands After Deployment

```bash
# Watch pods start
kubectl get pods -n inventory-app -w

# Check logs
kubectl logs -f deployment/ims-backend -n inventory-app

# Get the NLB public URL (your app's entry point)
kubectl get svc -n ingress-nginx

# Scale manually
kubectl scale deployment ims-backend --replicas=3 -n inventory-app

# Check HPA status
kubectl get hpa -n inventory-app

# Check CronJobs
kubectl get cronjobs -n inventory-app
kubectl get jobs -n inventory-app

# Connect to a pod
kubectl exec -it deployment/ims-backend -n inventory-app -- /bin/sh

# Port-forward for local debugging
kubectl port-forward svc/ims-backend 8080:8080 -n inventory-app
```

---

## Scratch vs Module Comparison

To switch from scratch EKS to the community module, in `main.tf` change:

```hcl
# Current (scratch — for learning):
module "eks" {
  source = "./modules/eks"
  ...
}

# Switch to (community module — for production teams):
module "eks" {
  source = "./modules/eks-module"
  ...
}
```

Both modules expose identical outputs — nothing else changes.
