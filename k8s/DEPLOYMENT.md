# Kubernetes Deployment Guide

**MoeWare Inventory Management System**

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Local Development Setup](#local-development-setup)
- [Applying Manifests Manually](#applying-manifests-manually)
- [Helm Deployment (Recommended)](#helm-deployment-recommended)
- [Essential kubectl Commands](#essential-kubectl-commands)
- [Production Operations](#production-operations)
- [Monitoring Setup](#monitoring-setup)
- [Debugging Common Issues](#debugging-common-issues)
- [Cleanup](#cleanup)

---

## Prerequisites

Install the required tools before proceeding.

**kubectl** — the Kubernetes CLI

```bash
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
chmod +x kubectl && sudo mv kubectl /usr/local/bin/
```

**Helm** — the Kubernetes package manager

```bash
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
```

**Minikube** — local Kubernetes cluster for development

```bash
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube
```

---

## Local Development Setup

Start a local cluster with enough resources for the full stack.

```bash
minikube start --cpus=4 --memory=8192 --driver=docker
```

Enable required addons.

```bash
minikube addons enable ingress          # Nginx Ingress Controller
minikube addons enable metrics-server   # Required for HPA to work
minikube addons enable dashboard        # Web UI (optional)
```

Open the dashboard in your browser.

```bash
minikube dashboard
```

---

## Applying Manifests Manually

Apply resources in dependency order. Resources applied out of order will fail because the backend depends on the database, and both depend on
namespaces and secrets existing first.

**Step 1 — Namespaces** (everything else lives inside these)

```bash
kubectl apply -f k8s/namespaces/namespaces.yaml
```

**Step 2 — ConfigMaps and Secrets**

```bash
kubectl apply -f k8s/configmaps/configmaps.yaml
kubectl apply -f k8s/secrets/secrets.yaml
```

**Step 3 — Database** (must be ready before the backend starts)

```bash
kubectl apply -f k8s/database/statefulset.yaml

# Wait for the database pod to be ready before continuing
kubectl rollout status statefulset/postgres -n inventory-app

# Or watch all pods live
kubectl get pods -n inventory-app -w
```

**Step 4 — Backend and Frontend**

```bash
kubectl apply -f k8s/backend/deployment.yaml
kubectl apply -f k8s/frontend/deployment.yaml
```

**Step 5 — Ingress**

```bash
kubectl apply -f k8s/ingress/ingress.yaml
```

**Step 6 — Autoscaling and Quotas**

```bash
kubectl apply -f k8s/hpa/hpa.yaml
```

**Step 7 — Scheduled Jobs**

```bash
kubectl apply -f k8s/cronjobs/cronjobs.yaml
```

---

## Helm Deployment (Recommended)

Helm packages all the manifests into a single release, making upgrades and rollbacks much cleaner than managing individual YAML files.

### Install Dependencies First

**cert-manager** (handles SSL certificate provisioning automatically)

```bash
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml
```

**Nginx Ingress Controller**

```bash
helm upgrade --install ingress-nginx ingress-nginx \
  --repo https://kubernetes.github.io/ingress-nginx \
  --namespace ingress-nginx \
  --create-namespace
```

### Deploy to Development

```bash
helm upgrade --install moeware ./k8s/helm/inventory-chart \
  -f ./k8s/helm/inventory-chart/values-dev.yaml \
  --namespace inventory-dev \
  --create-namespace \
  --wait \
  --timeout 5m
```

### Deploy to Production

Pass the current Git SHA as the image tag so deployments are traceable.

```bash
helm upgrade --install moeware ./k8s/helm/inventory-chart \
  -f ./k8s/helm/inventory-chart/values-prod.yaml \
  --namespace inventory-app \
  --create-namespace \
  --set backend.image.tag=$GIT_SHA \
  --set frontend.image.tag=$GIT_SHA \
  --wait \
  --timeout 10m
```

### Common Helm Operations

Check the status of a release.

```bash
helm status moeware -n inventory-app
```

Preview what would be applied without touching the cluster (dry run).

```bash
helm template moeware ./k8s/helm/inventory-chart -f values-dev.yaml
```

Upgrade with a new image tag only.

```bash
helm upgrade moeware ./k8s/helm/inventory-chart \
  --reuse-values \
  --set backend.image.tag=v1.2.0
```

Roll back to a previous revision.

```bash
helm history moeware -n inventory-app       # List all revisions
helm rollback moeware 1 -n inventory-app    # Roll back to revision 1
```

Remove the release entirely.

```bash
helm uninstall moeware -n inventory-app
```

---

## Essential kubectl Commands

### Cluster and Nodes

```bash
kubectl cluster-info
kubectl get nodes
kubectl top nodes                            # CPU/memory per node (needs metrics-server)
```

### Namespaces

```bash
kubectl get namespaces

# Set a default namespace so you don't have to type -n every time
kubectl config set-context --current --namespace=inventory-app
```

### Pods

```bash
kubectl get pods -n inventory-app
kubectl get pods -n inventory-app -o wide    # Shows node and IP per pod
kubectl get pods -n inventory-app -w         # Live watch mode

kubectl describe pod <pod-name> -n inventory-app   # Full detail + events at the bottom
kubectl logs <pod-name> -n inventory-app           # Print logs
kubectl logs <pod-name> -n inventory-app -f        # Stream logs (like tail -f)
kubectl logs <pod-name> -n inventory-app --previous  # Logs from a crashed container

# Open a shell inside a running pod
kubectl exec -it <pod-name> -n inventory-app -- /bin/sh

# Run a one-off command inside a pod
kubectl exec -it <pod-name> -n inventory-app -- curl localhost:8080/actuator/health

# Copy a file out of a pod
kubectl cp <pod-name>:/app/logs/app.log ./app.log -n inventory-app
```

### Deployments

```bash
kubectl get deployments -n inventory-app
kubectl describe deployment backend -n inventory-app

# Track a rolling update in progress
kubectl rollout status deployment/backend -n inventory-app

# View the change history
kubectl rollout history deployment/backend -n inventory-app

# Roll back one version
kubectl rollout undo deployment/backend -n inventory-app

# Roll back to a specific revision
kubectl rollout undo deployment/backend --to-revision=3 -n inventory-app

# Manually scale (overrides HPA temporarily)
kubectl scale deployment/backend --replicas=5 -n inventory-app

# Restart all pods with a rolling restart (no downtime)
kubectl rollout restart deployment/backend -n inventory-app
```

### Services and Networking

```bash
kubectl get services -n inventory-app
kubectl describe service backend-service -n inventory-app

# Forward a cluster service to your local machine
kubectl port-forward service/backend-service 8080:8080 -n inventory-app
kubectl port-forward service/frontend-service 3000:80 -n inventory-app

# Forward directly to the database pod (useful for inspecting data)
kubectl port-forward pod/postgres-0 5432:5432 -n inventory-app
```

### StatefulSets

```bash
kubectl get statefulsets -n inventory-app
kubectl describe statefulset postgres -n inventory-app
```

### ConfigMaps and Secrets

```bash
kubectl get configmaps -n inventory-app
kubectl describe configmap backend-config -n inventory-app

# View a secret (values are base64-encoded)
kubectl get secret backend-secrets -n inventory-app -o yaml

# Decode a specific secret value
kubectl get secret backend-secrets -n inventory-app \
  -o jsonpath='{.data.DB_PASSWORD}' | base64 --decode
```

### Events

Events are the first place to look when something isn't working.

```bash
kubectl get events -n inventory-app --sort-by='.lastTimestamp'
```

### Resource Usage

```bash
kubectl top pods -n inventory-app
kubectl top pods -n inventory-app --sort-by=cpu
```

### HPA

```bash
kubectl get hpa -n inventory-app
kubectl describe hpa backend-hpa -n inventory-app
```

### CronJobs and Jobs

```bash
kubectl get cronjobs -n inventory-app
kubectl get jobs -n inventory-app

# Manually trigger a CronJob for testing
kubectl create job --from=cronjob/low-stock-check manual-test-$(date +%s) -n inventory-app
```

### Ingress

```bash
kubectl get ingress -n inventory-app
kubectl describe ingress inventory-ingress -n inventory-app
```

---

## Production Operations

### Zero-Downtime Deployment

Update the image tag on a running deployment. Kubernetes performs the rolling update automatically.

```bash
kubectl set image deployment/backend \
  backend=yourdockerhub/inventory-backend:v1.2.0 \
  -n inventory-app

# Watch the rollout complete
kubectl rollout status deployment/backend -n inventory-app
```

### Run a Database Migration

Always run migrations before deploying a new backend version. This creates a temporary pod that runs Flyway, then removes itself.

```bash
kubectl run db-migration \
  --image=yourdockerhub/inventory-backend:v1.2.0 \
  --restart=Never \
  --rm -it \
  -n inventory-app \
  --command -- java -jar /app/app.jar --spring.batch.job.name=flyway-migrate
```

### Emergency Scale-Up

```bash
kubectl scale deployment/backend --replicas=10 -n inventory-app
```

### Emergency Rollback

```bash
kubectl rollout undo deployment/backend -n inventory-app
kubectl rollout undo deployment/frontend -n inventory-app
```

### Open a Database Shell

```bash
kubectl exec -it postgres-0 -n inventory-app -- psql -U inventory_user -d inventory_db
```

### View All Resources in a Namespace

```bash
kubectl get all -n inventory-app
```

---

## Monitoring Setup

### Install Prometheus + Grafana

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

helm install prometheus prometheus-community/kube-prometheus-stack \
  --namespace monitoring \
  --create-namespace \
  --set grafana.adminPassword=your-secure-password
```

### Access the Dashboards

Grafana (metrics and dashboards)

```bash
kubectl port-forward svc/prometheus-grafana 3000:80 -n monitoring
# Open http://localhost:3000 — login: admin / your-secure-password
```

Prometheus (raw metrics and alerts)

```bash
kubectl port-forward svc/prometheus-kube-prometheus-prometheus 9090:9090 -n monitoring
# Open http://localhost:9090
```

### Install Loki (Centralized Logging)

```bash
helm repo add grafana https://grafana.github.io/helm-charts
helm install loki grafana/loki-stack --namespace monitoring
```

---

## Debugging Common Issues

### Pod Stuck in `Pending`

The pod can't be scheduled. Usually means not enough CPU/memory on any node, or a PVC can't be provisioned.

```bash
kubectl describe pod <pod-name> -n inventory-app
# Read the Events section at the bottom — it will tell you exactly why
```

### Pod in `CrashLoopBackOff`

The pod starts, crashes, and Kubernetes keeps restarting it. Check what the app printed before crashing.

```bash
kubectl logs <pod-name> -n inventory-app --previous
```

### Image Pull Error (`ErrImagePull` / `ImagePullBackOff`)

The node can't pull the Docker image. Check registry credentials or the image name/tag.

```bash
kubectl describe pod <pod-name> -n inventory-app | grep -A 10 Events
```

### Pods Can't Talk to Each Other

Spawn a temporary debug pod in the same namespace and test connectivity manually.

```bash
kubectl run debug --image=curlimages/curl -it --rm -n inventory-app -- sh

# Inside the debug pod:
curl backend-service:8080/actuator/health
nslookup postgres-service
```

### DNS Not Resolving

```bash
kubectl run debug --image=busybox -it --rm -n inventory-app -- nslookup backend-service
```

---

## Cleanup

Delete specific resources.

```bash
kubectl delete deployment backend -n inventory-app
kubectl delete service backend-service -n inventory-app
```

Delete all workloads in a namespace (keeps the namespace itself).

```bash
# ⚠️ Destructive — cannot be undone
kubectl delete all --all -n inventory-app
```

Delete a namespace and everything inside it.

```bash
# ⚠️ Destructive — deletes all resources in the namespace
kubectl delete namespace inventory-dev
```

Remove a Helm release.

```bash
helm uninstall moeware -n inventory-app
```
