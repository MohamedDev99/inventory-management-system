##############################################################################
# modules/networking/main.tf
#
# EKS NETWORKING — KEY DIFFERENCES FROM PLAIN EC2 NETWORKING:
#
# 1. SUBNET TAGS — EKS uses tags to auto-discover subnets for load balancers.
#    Without these tags, `kubectl apply -f ingress.yaml` won't create an AWS LB.
#
#    Public subnets:  kubernetes.io/role/elb = "1"
#    Private subnets: kubernetes.io/role/internal-elb = "1"
#    Both:            kubernetes.io/cluster/<cluster-name> = "shared" or "owned"
#
# 2. THREE SUBNET TIERS (same as before but EKS-aware):
#    Public:   10.0.1-3.0/24   → NLB/ALB (created by ingress-nginx)
#    Private:  10.0.11-13.0/24 → EKS worker nodes + Fargate pods
#    Database: 10.0.21-23.0/24 → RDS (prod only)
#
# 3. DNS HOSTNAMES — must be enabled for EKS nodes to register with cluster
##############################################################################

# ============================================================================
# VPC
# ============================================================================

resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true  # Required for EKS node registration
  enable_dns_hostnames = true  # Required for EKS node registration

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-vpc"
    # This tag tells EKS this VPC belongs to the cluster
    "kubernetes.io/cluster/${var.cluster_name}" = "shared"
  })
}

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id
  tags   = merge(var.tags, { Name = "${var.name_prefix}-igw" })
}

# ============================================================================
# PUBLIC SUBNETS — NLB/ALB created by ingress-nginx lives here
# ============================================================================

resource "aws_subnet" "public" {
  count = length(var.availability_zones)

  vpc_id                  = aws_vpc.main.id
  cidr_block              = cidrsubnet(var.vpc_cidr, 8, count.index + 1)
  availability_zone       = var.availability_zones[count.index]
  map_public_ip_on_launch = true

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-public-${var.availability_zones[count.index]}"
    Tier = "public"

    # CRITICAL TAG: tells the AWS Load Balancer Controller and ingress-nginx
    # that internet-facing load balancers can use this subnet.
    # Without this tag, your Ingress resource won't create an AWS NLB.
    "kubernetes.io/role/elb" = "1"

    # Tells EKS this subnet belongs to the cluster
    "kubernetes.io/cluster/${var.cluster_name}" = "shared"
  })
}

# ============================================================================
# PRIVATE SUBNETS — EKS worker nodes + Fargate pods run here
# ============================================================================

resource "aws_subnet" "private" {
  count = length(var.availability_zones)

  vpc_id                  = aws_vpc.main.id
  cidr_block              = cidrsubnet(var.vpc_cidr, 8, count.index + 11)
  availability_zone       = var.availability_zones[count.index]
  map_public_ip_on_launch = false

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-private-${var.availability_zones[count.index]}"
    Tier = "private"

    # CRITICAL TAG: tells AWS internal load balancers (ClusterIP, internal NLB)
    # they can use this subnet. Required for inter-pod communication via LB.
    "kubernetes.io/role/internal-elb" = "1"

    # "owned" means only this cluster uses these subnets — safer than "shared"
    "kubernetes.io/cluster/${var.cluster_name}" = "owned"
  })
}

# ============================================================================
# DATABASE SUBNETS — RDS only (prod), no EKS involvement
# ============================================================================

resource "aws_subnet" "database" {
  count = length(var.availability_zones)

  vpc_id                  = aws_vpc.main.id
  cidr_block              = cidrsubnet(var.vpc_cidr, 8, count.index + 21)
  availability_zone       = var.availability_zones[count.index]
  map_public_ip_on_launch = false

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-database-${var.availability_zones[count.index]}"
    Tier = "database"
  })
}

# ============================================================================
# NAT GATEWAYS — one per AZ so Fargate pods can pull images from DockerHub
#
# WHY FARGATE NEEDS NAT:
#   Fargate pods run in private subnets.
#   They need to pull container images from DockerHub / ECR.
#   Private subnets have no internet — NAT Gateway provides outbound access.
#   Without NAT, your cronjob pods will fail to start (ImagePullBackOff).
# ============================================================================

resource "aws_eip" "nat" {
  count  = length(var.availability_zones)
  domain = "vpc"
  tags   = merge(var.tags, { Name = "${var.name_prefix}-nat-eip-${count.index + 1}" })
  depends_on = [aws_internet_gateway.main]
}

resource "aws_nat_gateway" "main" {
  count         = length(var.availability_zones)
  subnet_id     = aws_subnet.public[count.index].id
  allocation_id = aws_eip.nat[count.index].id

  tags = merge(var.tags, { Name = "${var.name_prefix}-nat-${count.index + 1}" })
  depends_on = [aws_internet_gateway.main]
}

# ============================================================================
# ROUTE TABLES
# ============================================================================

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = merge(var.tags, { Name = "${var.name_prefix}-rt-public" })
}

resource "aws_route_table_association" "public" {
  count          = length(aws_subnet.public)
  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}

# One private route table per AZ — each points to its own NAT Gateway
# This is important for EKS: if one AZ's NAT fails, nodes in other AZs
# still have internet access through their own NAT Gateway
resource "aws_route_table" "private" {
  count  = length(var.availability_zones)
  vpc_id = aws_vpc.main.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.main[count.index].id
  }

  tags = merge(var.tags, { Name = "${var.name_prefix}-rt-private-${count.index + 1}" })
}

resource "aws_route_table_association" "private" {
  count          = length(aws_subnet.private)
  subnet_id      = aws_subnet.private[count.index].id
  route_table_id = aws_route_table.private[count.index].id
}

resource "aws_route_table" "database" {
  vpc_id = aws_vpc.main.id
  tags   = merge(var.tags, { Name = "${var.name_prefix}-rt-database" })
}

resource "aws_route_table_association" "database" {
  count          = length(aws_subnet.database)
  subnet_id      = aws_subnet.database[count.index].id
  route_table_id = aws_route_table.database.id
}
