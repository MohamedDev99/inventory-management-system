##############################################################################
# bootstrap/main.tf — Week 9, Day 1: Basic K8s + kubectl
#
# This is the SIMPLEST possible EKS setup — no modules, no Fargate, no Helm.
# Use this FIRST to understand what EKS actually is before using the full setup.
#
# Once you understand what each resource does, move to the full terraform-eks/
# root module which adds node groups, Fargate, IRSA, and Helm charts.
#
# WHAT THIS CREATES:
#   - VPC with 2 public + 2 private subnets
#   - EKS cluster (control plane only)
#   - One managed node group (2x t3.medium)
#
# WHAT YOU DO AFTER:
#   aws eks update-kubeconfig --region us-east-1 --name moeware-bootstrap
#   kubectl get nodes
#   kubectl apply -f ../k8s/namespaces/namespaces.yaml
##############################################################################

terraform {
  required_version = ">= 1.6.0"
  required_providers {
    aws = { source = "hashicorp/aws", version = "~> 5.0" }
    tls = { source = "hashicorp/tls", version = "~> 4.0" }
  }
  # Use local state for bootstrap — it's temporary and for learning only
  # backend "s3" { ... }  # uncomment when you're ready for shared state
}

provider "aws" {
  region = "us-east-1"
  default_tags {
    tags = {
      Project   = "MoeWare-IMS-Bootstrap"
      ManagedBy = "Terraform"
    }
  }
}

locals {
  cluster_name = "moeware-bootstrap"
  vpc_cidr     = "10.100.0.0/16"
  azs          = ["us-east-1a", "us-east-1b"]
}

# ---- MINIMAL VPC -----------------------------------------------------------

resource "aws_vpc" "main" {
  cidr_block           = local.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags                 = { Name = "moeware-bootstrap-vpc" }
}

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id
  tags   = { Name = "moeware-bootstrap-igw" }
}

resource "aws_subnet" "public" {
  count                   = 2
  vpc_id                  = aws_vpc.main.id
  cidr_block              = cidrsubnet(local.vpc_cidr, 8, count.index + 1)
  availability_zone       = local.azs[count.index]
  map_public_ip_on_launch = true
  tags = {
    Name                                          = "bootstrap-public-${count.index + 1}"
    "kubernetes.io/role/elb"                      = "1"
    "kubernetes.io/cluster/${local.cluster_name}" = "shared"
  }
}

resource "aws_subnet" "private" {
  count             = 2
  vpc_id            = aws_vpc.main.id
  cidr_block        = cidrsubnet(local.vpc_cidr, 8, count.index + 11)
  availability_zone = local.azs[count.index]
  tags = {
    Name                                          = "bootstrap-private-${count.index + 1}"
    "kubernetes.io/role/internal-elb"             = "1"
    "kubernetes.io/cluster/${local.cluster_name}" = "owned"
  }
}

resource "aws_eip" "nat" {
  domain     = "vpc"
  depends_on = [aws_internet_gateway.main]
}

resource "aws_nat_gateway" "main" {
  subnet_id     = aws_subnet.public[0].id
  allocation_id = aws_eip.nat.id
  depends_on    = [aws_internet_gateway.main]
  tags          = { Name = "bootstrap-nat" }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }
  tags = { Name = "bootstrap-rt-public" }
}

resource "aws_route_table_association" "public" {
  count          = 2
  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table" "private" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.main.id
  }
  tags = { Name = "bootstrap-rt-private" }
}

resource "aws_route_table_association" "private" {
  count          = 2
  subnet_id      = aws_subnet.private[count.index].id
  route_table_id = aws_route_table.private.id
}

# ---- IAM ROLES -------------------------------------------------------------

resource "aws_iam_role" "cluster" {
  name = "bootstrap-eks-cluster-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "eks.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "cluster" {
  role       = aws_iam_role.cluster.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
}

resource "aws_iam_role" "node" {
  name = "bootstrap-eks-node-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = { Service = "ec2.amazonaws.com"
    } }]
  })
}

resource "aws_iam_role_policy_attachment" "node_worker" {
  role       = aws_iam_role.node.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy"
}
resource "aws_iam_role_policy_attachment" "node_cni" {
  role       = aws_iam_role.node.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy"
}
resource "aws_iam_role_policy_attachment" "node_ecr" {
  role       = aws_iam_role.node.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
}

# ---- EKS CLUSTER -----------------------------------------------------------

resource "aws_eks_cluster" "main" {
  name     = local.cluster_name
  version  = "1.29"
  role_arn = aws_iam_role.cluster.arn

  vpc_config {
    subnet_ids              = concat(aws_subnet.private[*].id, aws_subnet.public[*].id)
    endpoint_public_access  = true
    endpoint_private_access = true
  }

  depends_on = [aws_iam_role_policy_attachment.cluster]
}

# ---- NODE GROUP ------------------------------------------------------------

resource "aws_eks_node_group" "main" {
  cluster_name    = aws_eks_cluster.main.name
  node_group_name = "bootstrap-nodes"
  node_role_arn   = aws_iam_role.node.arn
  subnet_ids      = aws_subnet.private[*].id
  instance_types  = ["t3.medium"]
  capacity_type   = "SPOT" # Spot for learning — saves money

  scaling_config {
    min_size     = 1
    desired_size = 2
    max_size     = 3
  }

  depends_on = [
    aws_iam_role_policy_attachment.node_worker,
    aws_iam_role_policy_attachment.node_cni,
    aws_iam_role_policy_attachment.node_ecr,
  ]
}

# ---- OUTPUTS ---------------------------------------------------------------

output "kubeconfig_command" {
  value = "aws eks update-kubeconfig --region us-east-1 --name ${local.cluster_name}"
}

output "cluster_endpoint" {
  value = aws_eks_cluster.main.endpoint
}
