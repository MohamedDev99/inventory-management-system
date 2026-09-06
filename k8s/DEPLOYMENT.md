# =============================================================================

# Kubernetes Deployment Guide & kubectl Reference

# MoeWare Inventory Management System

# =============================================================================

## Prerequisites

# Install kubectl

curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" chmod +x kubectl && sudo mv kubectl
/usr/local/bin/

# Install Helm

curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash

# Install Minikube (local development)

curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64 sudo install minikube-linux-amd64 /usr/local/bin/minikube

# =============================================================================

# LOCAL DEVELOPMENT SETUP

# =============================================================================

## Start local cluster

minikube start --cpus=4 --memory=8192 --driver=docker

# Enable required addons

minikube addons enable ingress # Nginx Ingress Controller minikube addons enable metrics-server # Required for HPA minikube addons enable dashboard #
Web UI

# View the dashboard

minikube dashboard

## Apply manifests to local cluster

# 1. Create namespaces first (other resources depend on them)

kubectl apply -f k8s/namespaces/namespaces.yaml

# 2. Create ConfigMaps and Secrets

kubectl apply -f k8s/configmaps/configmaps.yaml kubectl apply -f k8s/secrets/secrets.yaml

# 3. Database (must be running before backend starts)

kubectl apply -f k8s/database/statefulset.yaml

# 4. Wait for database to be ready

kubectl rollout status statefulset/postgres -n inventory-app

# OR watch pods:

kubectl get pods -n inventory-app -w

# 5. Deploy backend and frontend

kubectl apply -f k8s/backend/deployment.yaml kubectl apply -f k8s/frontend/deployment.yaml

# 6. Configure Ingress

kubectl apply -f k8s/ingress/ingress.yaml

# 7. Set up autoscaling and quotas

kubectl apply -f k8s/hpa/hpa.yaml

# 8. Schedule background jobs

kubectl apply -f k8s/cronjobs/cronjobs.yaml

# =============================================================================

# HELM DEPLOYMENT (recommended)

# =============================================================================

## Install cert-manager (for SSL)

kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml

## Install Nginx Ingress Controller

helm upgrade --install ingress-nginx ingress-nginx \
 --repo https://kubernetes.github.io/ingress-nginx \
 --namespace ingress-nginx --create-namespace

## Development deployment

helm upgrade --install moeware ./k8s/helm/inventory-chart \
 -f ./k8s/helm/inventory-chart/values-dev.yaml \
 --namespace inventory-dev \
 --create-namespace \
 --wait \
 --timeout 5m

## Production deployment

helm upgrade --install moeware ./k8s/helm/inventory-chart \
 -f ./k8s/helm/inventory-chart/values-prod.yaml \
 --namespace inventory-app \
 --create-namespace \
 --set backend.image.tag=$GIT_SHA \
  --set frontend.image.tag=$GIT_SHA \
 --wait \
 --timeout 10m

## Check release status

helm status moeware -n inventory-app

## View rendered templates (dry run — see what would be applied)

helm template moeware ./k8s/helm/inventory-chart -f values-dev.yaml

## Upgrade (rolling deploy)

helm upgrade moeware ./k8s/helm/inventory-chart \
 --reuse-values \
 --set backend.image.tag=v1.2.0

## Rollback to previous version

helm rollback moeware 1 -n inventory-app # Rollback to revision 1 helm history moeware -n inventory-app # See all revisions

## Uninstall (deletes all resources)

helm uninstall moeware -n inventory-app

# =============================================================================

# ESSENTIAL KUBECTL COMMANDS

# =============================================================================

# ---- Cluster Info ----

kubectl cluster-info kubectl get nodes kubectl top nodes # Node CPU/memory (needs metrics-server)

# ---- Namespace Operations ----

kubectl get namespaces kubectl config set-context --current --namespace=inventory-app # Set default namespace

# ---- Pod Operations ----

kubectl get pods -n inventory-app kubectl get pods -n inventory-app -o wide # Show node assignment and IPs kubectl get pods -n inventory-app -w #
Watch for changes (live updates)

kubectl describe pod <pod-name> -n inventory-app # Detailed info, events, errors kubectl logs <pod-name> -n inventory-app # View logs kubectl logs
<pod-name> -n inventory-app -f # Follow logs (like tail -f) kubectl logs <pod-name> -n inventory-app --previous # Logs from crashed container

# Execute a command inside a running pod

kubectl exec -it <pod-name> -n inventory-app -- /bin/sh kubectl exec -it <pod-name> -n inventory-app -- curl localhost:8080/actuator/health

# Copy files to/from pods

kubectl cp <pod-name>:/app/logs/app.log ./app.log -n inventory-app

# ---- Deployment Operations ----

kubectl get deployments -n inventory-app kubectl describe deployment backend -n inventory-app kubectl rollout status deployment/backend -n
inventory-app kubectl rollout history deployment/backend -n inventory-app kubectl rollout undo deployment/backend -n inventory-app # Rollback kubectl
rollout undo deployment/backend --to-revision=3 -n inventory-app

# Scale manually (bypasses HPA temporarily)

kubectl scale deployment/backend --replicas=5 -n inventory-app

# Force restart all pods (triggers rolling restart)

kubectl rollout restart deployment/backend -n inventory-app

# ---- Service & Networking ----

kubectl get services -n inventory-app kubectl describe service backend-service -n inventory-app

# Access a service locally (port-forward)

kubectl port-forward service/backend-service 8080:8080 -n inventory-app kubectl port-forward service/frontend-service 3000:80 -n inventory-app kubectl
port-forward pod/postgres-0 5432:5432 -n inventory-app # DB access

# ---- StatefulSet Operations ----

kubectl get statefulsets -n inventory-app kubectl describe statefulset postgres -n inventory-app

# ---- ConfigMaps & Secrets ----

kubectl get configmaps -n inventory-app kubectl describe configmap backend-config -n inventory-app kubectl get secret backend-secrets -n inventory-app
-o yaml

# Decode a secret value:

kubectl get secret backend-secrets -n inventory-app -o jsonpath='{.data.DB_PASSWORD}' | base64 --decode

# ---- Events (great for debugging) ----

kubectl get events -n inventory-app --sort-by='.lastTimestamp'

# ---- Resource Usage ----

kubectl top pods -n inventory-app kubectl top pods -n inventory-app --sort-by=cpu

# ---- HPA ----

kubectl get hpa -n inventory-app kubectl describe hpa backend-hpa -n inventory-app

# ---- CronJobs ----

kubectl get cronjobs -n inventory-app kubectl get jobs -n inventory-app

# Manually trigger a CronJob (useful for testing)

kubectl create job --from=cronjob/low-stock-check manual-low-stock-$(date +%s) -n inventory-app

# ---- Ingress ----

kubectl get ingress -n inventory-app kubectl describe ingress inventory-ingress -n inventory-app

# ---- Debugging ----

# Pod stuck in Pending? Check node resources and events

kubectl describe pod <pod-name> -n inventory-app # Look for events at bottom

# Pod in CrashLoopBackOff? Check logs

kubectl logs <pod-name> -n inventory-app --previous

# Image pull error?

kubectl describe pod <pod-name> | grep -A5 Events

# Connection issues between pods?

# Spawn a temporary debug pod in the namespace

kubectl run debug --image=curlimages/curl -it --rm -n inventory-app -- sh

# From inside debug pod:

# curl backend-service:8080/actuator/health

# nslookup postgres-service

# Check DNS resolution

kubectl run debug --image=busybox -it --rm -n inventory-app -- nslookup backend-service

# =============================================================================

# PRODUCTION OPERATIONS

# =============================================================================

## Zero-downtime deployment

# Update image tag and apply rolling update

kubectl set image deployment/backend backend=yourdockerhub/inventory-backend:v1.2.0 -n inventory-app kubectl rollout status deployment/backend -n
inventory-app # Watch progress

## Database migration (run before deploying new backend)

kubectl run db-migration \
 --image=yourdockerhub/inventory-backend:v1.2.0 \
 --restart=Never \
 --rm -it \
 -n inventory-app \
 --command -- java -jar /app/app.jar --spring.batch.job.name=flyway-migrate

## Emergency scale-up

kubectl scale deployment/backend --replicas=10 -n inventory-app

## Emergency rollback

kubectl rollout undo deployment/backend -n inventory-app kubectl rollout undo deployment/frontend -n inventory-app

## Get a shell in the database

kubectl exec -it postgres-0 -n inventory-app -- psql -U inventory_user -d inventory_db

## Check all resources in namespace

kubectl get all -n inventory-app

# =============================================================================

# MONITORING COMMANDS

# =============================================================================

## Install Prometheus stack

helm repo add prometheus-community https://prometheus-community.github.io/helm-charts helm repo update helm install prometheus
prometheus-community/kube-prometheus-stack \
 --namespace monitoring \
 --create-namespace \
 --set grafana.adminPassword=your-password

## Access Grafana

kubectl port-forward svc/prometheus-grafana 3000:80 -n monitoring

# Open http://localhost:3000 (admin/your-password)

## Access Prometheus

kubectl port-forward svc/prometheus-kube-prometheus-prometheus 9090:9090 -n monitoring

## Install Loki (for logs)

helm repo add grafana https://grafana.github.io/helm-charts helm install loki grafana/loki-stack --namespace monitoring

# =============================================================================

# CLEANUP

# =============================================================================

## Delete specific resources

kubectl delete deployment backend -n inventory-app kubectl delete service backend-service -n inventory-app

## Delete everything in namespace (⚠️ destructive)

kubectl delete all --all -n inventory-app

## Delete namespace (⚠️ also deletes all resources in it)

kubectl delete namespace inventory-dev

## Helm uninstall

helm uninstall moeware -n inventory-app\
