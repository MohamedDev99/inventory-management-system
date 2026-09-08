##############################################################################
# modules/eks/main.tf
#
# WHAT THIS FILE CREATES (from scratch — no modules, raw AWS resources):
#
#  1. Security Groups   — cluster control plane SG + node SG
#  2. EKS Cluster       — the Kubernetes control plane (AWS-managed masters)
#  3. OIDC Provider     — enables IRSA (pods get AWS IAM roles)
#  4. Managed Node Group— EC2 worker nodes (t3.medium, with Spot option)
#  5. Fargate Profile   — serverless nodes for CronJobs in inventory-app
#  6. EKS Add-ons       — CoreDNS, kube-proxy, VPC CNI (required for K8s)
#
# WHAT "MANAGED" MEANS IN EKS:
#   Control plane (masters): fully managed by AWS — you never SSH into them
#   Worker nodes:            you choose EC2 type + count, AWS handles patching
#   Fargate pods:            fully serverless — no EC2 at all, pay per pod/second
##############################################################################

# ============================================================================
# SECURITY GROUPS
#
# EKS SECURITY GROUP MODEL:
#   Cluster SG  ↔  Node SG: must allow traffic between them (kubectl → node)
#   Node SG:    allow all inter-node traffic (pod-to-pod communication)
#   Node SG:    allow HTTPS from cluster SG (kubelet API calls)
# ============================================================================

# Cluster security group — for the EKS control plane API server
resource "aws_security_group" "cluster" {
  name        = "${var.name_prefix}-eks-cluster-sg"
  description = "EKS cluster control plane security group."
  vpc_id      = var.vpc_id

  # Allow all outbound (control plane needs to talk to nodes, AWS APIs, etc.)
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-eks-cluster-sg"
    # EKS uses this tag to auto-associate the cluster's ENIs with this SG
    "kubernetes.io/cluster/${var.cluster_name}" = "owned"
  })
}

# Node security group — for EC2 worker nodes
resource "aws_security_group" "node" {
  name        = "${var.name_prefix}-eks-node-sg"
  description = "EKS worker node security group."
  vpc_id      = var.vpc_id

  # Pod-to-pod communication — all pods on all nodes must talk to each other
  ingress {
    description = "All traffic between nodes (pod-to-pod)"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    self        = true  # "self" means: from other resources with THIS same SG
  }

  # Control plane → node communication (kubelet, metrics, exec)
  ingress {
    description     = "Control plane to node (kubelet API)"
    from_port       = 443
    to_port         = 443
    protocol        = "tcp"
    security_groups = [aws_security_group.cluster.id]
  }

  ingress {
    description     = "Control plane to node (kubelet port)"
    from_port       = 10250
    to_port         = 10250
    protocol        = "tcp"
    security_groups = [aws_security_group.cluster.id]
  }

  # NodePort range — if you use NodePort services
  ingress {
    description = "NodePort services"
    from_port   = 30000
    to_port     = 32767
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-eks-node-sg"
    "kubernetes.io/cluster/${var.cluster_name}" = "owned"
  })
}

# NOTE: We do NOT add an aws_security_group_rule here for cluster→node port 443
# because that rule is already defined as an inline ingress block inside
# aws_security_group.node above. Mixing inline rules and separate
# aws_security_group_rule resources for the same SG causes:
#   Error: InvalidGroup.Duplicate — conflicting rules
# All rules for aws_security_group.node are defined inline only.

# ============================================================================
# EKS CLUSTER — The Kubernetes control plane
#
# WHAT AWS MANAGES FOR YOU:
#   - etcd (K8s database storing all cluster state)
#   - API server (what kubectl talks to)
#   - Controller manager (handles replication, endpoints, etc.)
#   - Scheduler (decides which node a pod runs on)
#   - Master node patching and upgrades
#
# WHAT YOU MANAGE:
#   - Worker nodes (the t3.medium EC2 instances)
#   - What runs ON those nodes (your deployments, services, etc.)
# ============================================================================

resource "aws_eks_cluster" "main" {
  name     = var.cluster_name
  version  = var.kubernetes_version
  role_arn = aws_iam_role.cluster.arn

  vpc_config {
    subnet_ids = concat(var.private_subnet_ids, var.public_subnet_ids)

    # Cluster security group — used for control plane network interfaces
    security_group_ids = [aws_security_group.cluster.id]

    # endpoint_public_access = true  → kubectl works from your laptop
    # endpoint_public_access = false → kubectl only works from inside the VPC
    endpoint_public_access  = var.cluster_endpoint_public_access
    endpoint_private_access = true  # Always enable — nodes use this internally
  }

  # Enable CloudWatch logging for the control plane
  # These logs help debug cluster-level issues (auth failures, API errors)
  enabled_cluster_log_types = ["api", "audit", "authenticator", "controllerManager", "scheduler"]

  # CRITICAL: encryption for Kubernetes Secrets at rest
  # Without this, your K8s Secrets (DB passwords, JWT keys) are stored as
  # plain base64 in etcd — not actually encrypted
  encryption_config {
    resources = ["secrets"]
    provider {
      key_arn = aws_kms_key.eks.arn
    }
  }

  tags = merge(var.tags, {
    Name = var.cluster_name
  })

  depends_on = [
    aws_iam_role_policy_attachment.cluster_policy,
    aws_cloudwatch_log_group.cluster,
  ]
}

# KMS key for encrypting Kubernetes Secrets in etcd
resource "aws_kms_key" "eks" {
  description             = "EKS cluster ${var.cluster_name} — Kubernetes Secrets encryption"
  deletion_window_in_days = 7
  enable_key_rotation     = true  # Rotate encryption key annually

  tags = merge(var.tags, { Name = "${var.name_prefix}-eks-kms" })
}

resource "aws_kms_alias" "eks" {
  name          = "alias/${var.name_prefix}-eks"
  target_key_id = aws_kms_key.eks.key_id
}

# CloudWatch log group for cluster control plane logs
resource "aws_cloudwatch_log_group" "cluster" {
  name              = "/aws/eks/${var.cluster_name}/cluster"
  retention_in_days = 30
  tags              = var.tags
}

# ============================================================================
# OIDC PROVIDER — Enables IRSA (IAM Roles for Service Accounts)
#
# The EKS cluster acts as an OIDC identity provider.
# This allows K8s ServiceAccounts to exchange their JWT tokens
# for temporary AWS credentials — no static keys needed.
#
# Flow:
#   Pod starts → K8s injects ServiceAccount token into pod
#   Pod calls AWS SDK → SDK calls STS with the token
#   STS validates token with EKS OIDC provider
#   STS returns temporary AWS credentials
#   Pod can now call S3, Secrets Manager, etc.
# ============================================================================

data "tls_certificate" "cluster" {
  url = aws_eks_cluster.main.identity[0].oidc[0].issuer
}

resource "aws_iam_openid_connect_provider" "cluster" {
  client_id_list  = ["sts.amazonaws.com"]
  thumbprint_list = [data.tls_certificate.cluster.certificates[0].sha1_fingerprint]
  url             = aws_eks_cluster.main.identity[0].oidc[0].issuer

  tags = merge(var.tags, { Name = "${var.name_prefix}-eks-oidc" })
}

# ============================================================================
# MANAGED NODE GROUP — EC2 worker nodes
#
# "Managed" means AWS:
#   - Patches the node OS automatically during maintenance windows
#   - Drains nodes gracefully before terminating (zero-downtime upgrades)
#   - Handles node replacement during K8s version upgrades
#
# You define:
#   - Instance type, min/max/desired count
#   - Disk size, labels, taints
# ============================================================================

resource "aws_eks_node_group" "main" {
  cluster_name    = aws_eks_cluster.main.name
  node_group_name = "${var.name_prefix}-node-group"
  node_role_arn   = aws_iam_role.node_group.arn

  # Place nodes in PRIVATE subnets — they're not directly internet-accessible
  subnet_ids = var.private_subnet_ids

  # Instance configuration
  instance_types = [var.node_instance_type]
  disk_size      = var.node_disk_size
  ami_type       = "AL2_x86_64"  # Amazon Linux 2 — optimized for EKS

  # Capacity type: ON_DEMAND or SPOT
  # Spot saves ~70% but can be interrupted — good for dev, bad for prod
  capacity_type = var.use_spot_nodes ? "SPOT" : "ON_DEMAND"

  scaling_config {
    min_size     = var.node_min_size
    desired_size = var.node_desired_size
    max_size     = var.node_max_size
  }

  # Rolling update strategy — how node upgrades are performed
  update_config {
    max_unavailable = 1  # Only 1 node down at a time during upgrades
  }

  # Labels applied to all nodes — use in pod nodeSelector or affinity rules
  # e.g., nodeSelector: { role: "worker" }
  labels = {
    role        = "worker"
    environment = var.environment
  }

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-node"
    # Required for cluster-autoscaler to discover and manage this node group
    "k8s.io/cluster-autoscaler/enabled"                     = "true"
    "k8s.io/cluster-autoscaler/${var.cluster_name}"         = "owned"
  })

  depends_on = [
    aws_iam_role_policy_attachment.node_worker_policy,
    aws_iam_role_policy_attachment.node_cni_policy,
    aws_iam_role_policy_attachment.node_ecr_policy,
  ]

  lifecycle {
    # Ignore desired_size changes — cluster-autoscaler manages this at runtime
    ignore_changes = [scaling_config[0].desired_size]
  }
}

# ============================================================================
# FARGATE PROFILES — Serverless nodes for CronJobs
#
# HOW FARGATE WORKS WITH EKS:
#   1. You create a Fargate Profile that says "pods in namespace X run on Fargate"
#   2. When K8s schedules a pod in that namespace, it runs serverless — no EC2
#   3. AWS provisions isolated micro-VMs per pod (much stronger isolation than shared nodes)
#   4. You pay per pod CPU/memory second — no idle node costs
#
# YOUR USE CASE:
#   CronJobs in inventory-app namespace run on Fargate.
#   They spin up, do their job, exit. You pay only for actual runtime.
#   No wasted EC2 capacity sitting idle between runs.
# ============================================================================

resource "aws_eks_fargate_profile" "main" {
  for_each = toset(var.fargate_namespaces)

  cluster_name           = aws_eks_cluster.main.name
  fargate_profile_name   = "${var.name_prefix}-fargate-${each.key}"
  pod_execution_role_arn = aws_iam_role.fargate.arn

  # Fargate pods run in private subnets (same as EC2 nodes)
  subnet_ids = var.private_subnet_ids

  # Selector: which pods use Fargate?
  # Any pod in this namespace will run on Fargate instead of EC2 nodes
  selector {
    namespace = each.key
  }

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-fargate-${each.key}"
  })

  depends_on = [aws_iam_role_policy_attachment.fargate_policy]
}

# ============================================================================
# EKS ADD-ONS — Required system components
#
# These are AWS-managed versions of core K8s plugins.
# AWS keeps them patched and compatible with your K8s version.
#
#  vpc-cni:     Assigns VPC IPs to pods (pods get real VPC IPs, not overlay)
#  coredns:     DNS for service discovery (backend.inventory-app.svc.cluster.local)
#  kube-proxy:  Network rules for Service routing (ClusterIP, NodePort)
# ============================================================================

resource "aws_eks_addon" "vpc_cni" {
  cluster_name             = aws_eks_cluster.main.name
  addon_name               = "vpc-cni"
  resolve_conflicts_on_create = "OVERWRITE"
  resolve_conflicts_on_update = "OVERWRITE"
  tags                     = var.tags
}

resource "aws_eks_addon" "coredns" {
  cluster_name             = aws_eks_cluster.main.name
  addon_name               = "coredns"
  resolve_conflicts_on_create = "OVERWRITE"
  resolve_conflicts_on_update = "OVERWRITE"
  tags                     = var.tags

  depends_on = [aws_eks_node_group.main]  # CoreDNS needs nodes to schedule on
}

resource "aws_eks_addon" "kube_proxy" {
  cluster_name             = aws_eks_cluster.main.name
  addon_name               = "kube-proxy"
  resolve_conflicts_on_create = "OVERWRITE"
  resolve_conflicts_on_update = "OVERWRITE"
  tags                     = var.tags
}
