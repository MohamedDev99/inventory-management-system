# Local Development — No Terraform Needed

For local development with Minikube or Kind, Terraform is not used. All AWS infrastructure (EKS, RDS, S3) stays in the cloud environments. Locally you
use:

## Quick Start (Minikube)

```bash
# Start local cluster
minikube start --cpus=4 --memory=8192 --driver=docker

# Enable ingress addon (replaces nginx-ingress Helm chart)
minikube addons enable ingress

# Apply all k8s manifests
kubectl apply -f k8s/namespaces/namespaces.yaml
kubectl apply -f k8s/configmaps/configmaps.yaml
kubectl apply -f k8s/secrets/secrets.yaml
kubectl apply -f k8s/database/statefulset.yaml
kubectl apply -f k8s/backend/deployment.yaml
kubectl apply -f k8s/frontend/deployment.yaml
kubectl apply -f k8s/ingress/ingress.yaml
kubectl apply -f k8s/hpa/hpa.yaml
kubectl apply -f k8s/cronjobs/cronjobs.yaml

# Or use Helm
helm install inventory ./k8s/helm/inventory-chart \
  -f k8s/helm/inventory-chart/values-dev.yaml \
  -n inventory-app

# Get local URL
minikube service list
minikube tunnel  # enables LoadBalancer services
```

## Quick Start (Kind)

```bash
kind create cluster --name moeware-ims

# Install ingress-nginx for Kind
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml

kubectl apply -f k8s/namespaces/namespaces.yaml
# ... rest same as Minikube
```

## Key Differences — Local vs Dev vs Prod

| Setting          | Local (Minikube) | Dev (EKS)        | Prod (EKS)      |
| ---------------- | ---------------- | ---------------- | --------------- |
| Kubernetes       | Minikube/Kind    | EKS 1.29         | EKS 1.29        |
| PostgreSQL       | StatefulSet      | StatefulSet      | RDS Multi-AZ    |
| S3               | LocalStack/fake  | Real S3          | Real S3         |
| Ingress          | minikube addon   | nginx-ingress    | nginx-ingress   |
| Replicas         | 1                | 1-2 (values-dev) | 3 (values-prod) |
| Resource limits  | Low              | Medium           | High            |
| Terraform needed | ❌ No            | ✅ Yes           | ✅ Yes          |
