{{/*
=============================================================================
_helpers.tpl — Reusable template definitions
=============================================================================
Templates defined here can be called from any other template file using:
  {{ include "inventory.labels" . }}

The leading underscore in _helpers.tpl means Helm won't render this file
directly as a Kubernetes manifest — it's only for helper definitions.
=============================================================================
*/}}

{{/*
Expand the name of the chart.
*/}}
{{- define "inventory.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because Kubernetes name fields are limited to this.
*/}}
{{- define "inventory.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "inventory.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels — applied to every resource.
These are the standard recommended K8s labels.
They enable filtering with: kubectl get all -l app.kubernetes.io/instance=moeware
*/}}
{{- define "inventory.labels" -}}
helm.sh/chart: {{ include "inventory.chart" . }}
{{ include "inventory.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: moeware-ims
environment: {{ .Values.global.environment }}
{{- end }}

{{/*
Selector labels — used in spec.selector.matchLabels and pod template labels.
These must be stable (don't change between releases) because selectors are immutable.
*/}}
{{- define "inventory.selectorLabels" -}}
app.kubernetes.io/name: {{ include "inventory.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the image name with registry prefix.
Usage: {{ include "inventory.image" (dict "registry" .Values.global.imageRegistry "repository" .Values.backend.image.repository "tag" .Values.backend.image.tag) }}
*/}}
{{- define "inventory.image" -}}
{{- if .registry }}
{{- printf "%s/%s:%s" .registry .repository .tag }}
{{- else }}
{{- printf "%s:%s" .repository .tag }}
{{- end }}
{{- end }}

{{/*
Database URL — constructs the JDBC connection string.
Used in backend ConfigMap to ensure consistency.
*/}}
{{- define "inventory.databaseUrl" -}}
{{- if .Values.postgresql.enabled }}
{{- printf "jdbc:postgresql://%s-postgres-service:5432/%s" .Release.Name "inventory_db" }}
{{- else }}
{{- printf "jdbc:postgresql://%s:%d/%s" .Values.postgresql.external.host (.Values.postgresql.external.port | int) .Values.postgresql.external.database }}
{{- end }}
{{- end }}
