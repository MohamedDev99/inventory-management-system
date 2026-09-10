# Observability Guide — MoeWare Inventory Management System

Companion doc to Week 10 of the DevOps roadmap. Covers what's deployed, how to query it, and
how to respond when something fires.

## 1. What's deployed

| Layer      | Tool                                | Purpose                                       | Namespace      |
|------------|--------------------------------------|------------------------------------------------|----------------|
| Metrics    | Prometheus (kube-prometheus-stack)   | Scrape & store time-series metrics              | monitoring     |
| Alerting   | Alertmanager                         | Route firing alerts to Slack                    | monitoring     |
| Dashboards | Grafana                              | Visualize metrics, logs, and traces in one place| monitoring     |
| Logs       | Loki + Promtail                      | Aggregate structured JSON logs from every pod   | monitoring     |
| Traces     | Jaeger                                | Distributed request tracing                     | monitoring     |
| App        | Micrometer + Actuator                | Expose `/actuator/prometheus`, emit trace context| inventory-app |

## 2. Golden Signals mapping

Google SRE's four signals, and where to find each one in this setup:

- **Latency** — Grafana panel "Latency Percentiles" (`histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket[5m])) by (le))`)
- **Traffic** — "Request Rate by Endpoint" panel — requests/sec by URI
- **Errors** — `HighErrorRate` alert (alert-rules.yaml) — 5xx / total, fires above 5%
- **Saturation** — `HighCPUUsage` / `HighMemoryUsage` / `DatabaseConnectionPoolExhaustion` alerts

## 3. SLIs / SLOs

| SLI                  | SLO target | Alert threshold / window |
|-----------------------|-----------|----------------------------|
| API availability       | 99.9%     | tracked indirectly via the error-rate alert |
| p95 request latency     | < 500ms   | `HighRequestLatencyP95`, 10 minutes |
| 5xx error rate           | < 1%      | `HighErrorRate` fires at 5% for 5 minutes (headroom before it's a full incident) |

Treat these as *starting points to tune*, not fixed truths — revisit after a few weeks of real
traffic and adjust the thresholds in `prometheus/alert-rules.yaml` accordingly.

## 4. Useful PromQL

```promql
# Request rate by endpoint, last 5 minutes
sum(rate(http_server_requests_seconds_count{application="inventory-backend"}[5m])) by (uri)

# Error percentage
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))
/ sum(rate(http_server_requests_seconds_count[5m]))

# JVM heap usage as a fraction of max
sum(jvm_memory_used_bytes{area="heap"}) by (pod) / sum(jvm_memory_max_bytes{area="heap"}) by (pod)

# HikariCP pool saturation
hikaricp_connections_active / hikaricp_connections_max
```

## 5. Saved Loki queries (LogQL)

Paste these into Grafana's Explore view with the Loki datasource selected.

```logql
# Failed login attempts (security)
{app="inventory-backend"} | json | message =~ "(?i)login failed|authentication failed"

# Inventory discrepancies / optimistic lock conflicts
{app="inventory-backend"} | json | message =~ "(?i)OptimisticLockException|CONFLICT"

# Order processing errors
{app="inventory-backend"} | json | level="ERROR" | message =~ "(?i)order"

# Database connection issues
{app="inventory-backend"} | json | message =~ "(?i)connection.*(timeout|refused|pool)"

# All log lines for one request, once you have a traceId from Jaeger
{app="inventory-backend"} | json | traceId="<paste-trace-id-here>"
```

## 6. Log levels — when to use which

- `ERROR` — something broke and a human should look (failed payment, unhandled exception). Should drive an alert if it spikes.
- `WARN` — degraded but recovered (retry succeeded, optimistic lock conflict resolved). Worth a dashboard panel, not a page.
- `INFO` — business events worth an audit trail (order created, PO approved). Most log volume should be here.
- `DEBUG` — dev-only, disabled in staging/prod via `logging.level.root` in `application-observability.yml`.

## 7. Runbook — common incidents

### Alert: HighErrorRate
1. Open Grafana → "Inventory App — Golden Signals," check which `uri` is failing.
2. Jump to Loki, filter `level="ERROR"` for that time window, grab a `traceId`.
3. Look up that trace in Jaeger to see which downstream call (DB, external service) failed.
4. If it's a bad deploy: `kubectl rollout undo deployment/inventory-backend -n inventory-app`.

### Alert: DatabaseConnectionPoolExhaustion
1. Check `hikaricp_connections_active` vs `_max` in Grafana — confirm it's sustained, not a blip.
2. Check for a slow query holding connections open (Loki query above, or `pg_stat_activity` on RDS).
3. Short-term: bump `spring.datasource.hikari.maximum-pool-size` and redeploy.
4. Root cause: usually a missing index or an N+1 query — check `pg_stat_statements`.

### Alert: PodRestartingFrequently
1. `kubectl describe pod <pod> -n inventory-app` — check the `Last State` reason (OOMKilled vs CrashLoopBackOff vs failed readiness probe).
2. OOMKilled → check `HighMemoryUsage` history in Grafana, raise the memory limit or find the leak.
3. Failed readiness probe → check `/actuator/health/readiness`, usually a DB connectivity issue at startup.

### Alert: LowStockProducts
1. Not an incident — a business alert. Check `GET /api/v1/products/low-stock`.
2. Confirm a purchase order exists or is being created for the affected SKUs.

## 8. Notes on simplifications made for this learning project

Worth calling out explicitly if this goes into a portfolio write-up:

- Alertmanager's Slack webhook is left as a plaintext placeholder — a real deployment puts it in
  a Kubernetes Secret referenced via `alertmanagerConfigSecret`, never committed to git.
- Jaeger is deployed `allinone` with in-memory storage — fine for a demo, but traces vanish on
  pod restart. Production needs Elasticsearch or Cassandra as the storage backend (see the
  commented block in `jaeger/jaeger-instance.yaml`).
- Trace sampling is set to 10% (`management.tracing.sampling.probability: 0.1`) to keep overhead
  low — turn it up to `1.0` locally when actively debugging one issue.
