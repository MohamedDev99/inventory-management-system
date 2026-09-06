{{/*
=============================================================================
Helm Template — Backend Deployment
=============================================================================
Helm templates use Go templating syntax.

Key syntax:
  {{ .Values.backend.replicaCount }}  — access values.yaml
  {{ .Release.Name }}                 — the helm release name
  {{ .Release.Namespace }}            — the target namespace
  {{ include "inventory.labels" . }}  — call a named template (from _helpers.tpl)
  {{- if .Values.backend.autoscaling.enabled }}  — conditional blocks
  {{- range .Values.ingress.hosts }}             — loops
=============================================================================
*/}}

{{- if .Values.backend.enabled }}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ .Release.Name }}-backend
  namespace: {{ .Release.Namespace }}
  labels:
    {{- include "inventory.labels" . | nindent 4 }}
    app.kubernetes.io/component: backend
  annotations:
    # This annotation enables `kubectl rollout history` to show change causes
    kubernetes.io/change-cause: "helm upgrade {{ .Release.Name }} to chart {{ .Chart.Version }}, app {{ .Chart.AppVersion }}"
spec:
  {{- if not .Values.backend.autoscaling.enabled }}
  replicas: {{ .Values.backend.replicaCount }}
  {{- end }}
  {{- /* When HPA is enabled, we let HPA control replicas. Setting replicas
       in the Deployment would fight with HPA. */}}

  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0

  selector:
    matchLabels:
      {{- include "inventory.selectorLabels" . | nindent 6 }}
      app.kubernetes.io/component: backend

  template:
    metadata:
      labels:
        {{- include "inventory.selectorLabels" . | nindent 8 }}
        app.kubernetes.io/component: backend
        app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/path: "/actuator/prometheus"
        prometheus.io/port: {{ .Values.backend.service.port | quote }}
        # Checksum forces pod restart when ConfigMap changes
        checksum/config: {{ include (print $.Template.BasePath "/configmap.yaml") . | sha256sum }}

    spec:
      {{- with .Values.global.imagePullSecrets }}
      imagePullSecrets:
        {{- toYaml . | nindent 8 }}
      {{- end }}

      securityContext:
        runAsNonRoot: true
        runAsUser: 1000
        fsGroup: 1000

      containers:
        - name: backend
          image: "{{ .Values.global.imageRegistry }}/{{ .Values.backend.image.repository }}:{{ .Values.backend.image.tag }}"
          imagePullPolicy: {{ .Values.backend.image.pullPolicy }}

          ports:
            - name: http
              containerPort: {{ .Values.backend.service.port }}
              protocol: TCP

          envFrom:
            - configMapRef:
                name: {{ .Release.Name }}-backend-config
            - secretRef:
                name: backend-secrets

          env:
            - name: POD_NAME
              valueFrom:
                fieldRef:
                  fieldPath: metadata.name
            - name: POD_NAMESPACE
              valueFrom:
                fieldRef:
                  fieldPath: metadata.namespace

          resources:
            {{- toYaml .Values.backend.resources | nindent 12 }}

          livenessProbe:
            httpGet:
              path: {{ .Values.backend.probes.liveness.path }}
              port: {{ .Values.backend.service.port }}
            initialDelaySeconds: {{ .Values.backend.probes.liveness.initialDelaySeconds }}
            periodSeconds: {{ .Values.backend.probes.liveness.periodSeconds }}
            timeoutSeconds: 10
            failureThreshold: 3

          readinessProbe:
            httpGet:
              path: {{ .Values.backend.probes.readiness.path }}
              port: {{ .Values.backend.service.port }}
            initialDelaySeconds: {{ .Values.backend.probes.readiness.initialDelaySeconds }}
            periodSeconds: {{ .Values.backend.probes.readiness.periodSeconds }}
            timeoutSeconds: 5
            failureThreshold: 3

          securityContext:
            allowPrivilegeEscalation: false
            capabilities:
              drop:
                - ALL

      terminationGracePeriodSeconds: 30
{{- end }}

---

{{- if .Values.backend.enabled }}
apiVersion: v1
kind: Service
metadata:
  name: {{ .Release.Name }}-backend-service
  namespace: {{ .Release.Namespace }}
  labels:
    {{- include "inventory.labels" . | nindent 4 }}
    app.kubernetes.io/component: backend
spec:
  type: {{ .Values.backend.service.type }}
  selector:
    {{- include "inventory.selectorLabels" . | nindent 4 }}
    app.kubernetes.io/component: backend
  ports:
    - name: http
      protocol: TCP
      port: {{ .Values.backend.service.port }}
      targetPort: http
{{- end }}

---

{{- if and .Values.backend.enabled .Values.backend.autoscaling.enabled }}
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: {{ .Release.Name }}-backend-hpa
  namespace: {{ .Release.Namespace }}
  labels:
    {{- include "inventory.labels" . | nindent 4 }}
    app.kubernetes.io/component: backend
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: {{ .Release.Name }}-backend
  minReplicas: {{ .Values.backend.autoscaling.minReplicas }}
  maxReplicas: {{ .Values.backend.autoscaling.maxReplicas }}
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: {{ .Values.backend.autoscaling.targetCPUUtilizationPercentage }}
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: {{ .Values.backend.autoscaling.targetMemoryUtilizationPercentage }}
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
{{- end }}