# Week 10 — Monitoring, Logging & Observability

Install order matches the roadmap's day-by-day structure. Run these against your EKS cluster
(or `kind`/`minikube` locally) after Week 9's Kubernetes deployment is up.

## Day 1-2 — Prometheus, Alertmanager & Grafana

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
kubectl create namespace monitoring

helm install prometheus prometheus-community/kube-prometheus-stack \
  -n monitoring -f prometheus/values-prometheus.yaml

kubectl apply -f prometheus/servicemonitor-backend.yaml
kubectl apply -f prometheus/alert-rules.yaml

kubectl port-forward -n monitoring svc/prometheus-grafana 3000:80
# http://localhost:3000  (user: admin / password from values-prometheus.yaml, replace before real use)
```

Then, in the **backend** repo:
- merge `backend/application-observability.yml` into `application.yml`
- copy `backend/logback-spring.xml` into `src/main/resources/`
- add the dependencies listed in `backend/pom-dependencies-snippet.xml`
- add the classes in `backend/business-metrics-examples.java` under `config/` and `service/` (split into two files — see the note at the top of that file)
- import `grafana/dashboards/inventory-app-dashboard.json` into Grafana: Dashboards → New → Import

## Day 3-4 — Loki & Promtail (centralized logging)

```bash
helm repo add grafana https://grafana.github.io/helm-charts
helm install loki grafana/loki-stack -n monitoring -f loki/values-loki-stack.yaml
```

Loki is already wired as a Grafana datasource via `prometheus/values-prometheus.yaml`
(`grafana.additionalDataSources`). Use Grafana → Explore → Loki to run the LogQL queries
saved in `docs/OBSERVABILITY.md`.

## Day 5 — Jaeger (distributed tracing)

```bash
kubectl apply -f https://github.com/jaegertracing/jaeger-operator/releases/download/v1.57.0/jaeger-operator.yaml -n monitoring
kubectl apply -f jaeger/jaeger-instance.yaml
kubectl port-forward -n monitoring svc/inventory-jaeger-query 16686:16686
```

## Day 6 — Golden Signals & Observability

Read `docs/OBSERVABILITY.md` for the Golden Signals mapping, SLIs/SLOs, saved PromQL/LogQL
queries, and a runbook entry for every alert defined in `prometheus/alert-rules.yaml`.

## File map

```
observability/
├── prometheus/
│   ├── values-prometheus.yaml       # kube-prometheus-stack Helm values (Prometheus+Alertmanager+Grafana)
│   ├── servicemonitor-backend.yaml  # tells Prometheus to scrape the Spring Boot backend
│   └── alert-rules.yaml             # PrometheusRule: error rate, latency, CPU/mem, restarts, DB pool, low stock
├── loki/
│   └── values-loki-stack.yaml       # Loki + Promtail Helm values
├── jaeger/
│   └── jaeger-instance.yaml         # Jaeger Operator CR
├── grafana/
│   └── dashboards/inventory-app-dashboard.json
├── backend/
│   ├── application-observability.yml
│   ├── logback-spring.xml
│   ├── pom-dependencies-snippet.xml
│   └── business-metrics-examples.java
└── docs/
    └── OBSERVABILITY.md             # Golden Signals, SLIs/SLOs, saved queries, runbook
```
