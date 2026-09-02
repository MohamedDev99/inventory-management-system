##############################################################################
# modules/networking/main.tf
#
# NETWORK ARCHITECTURE (3-tier):
#
#  Internet
#     │
#  Internet Gateway (entry point to VPC)
#     │
#  ┌──┴──────────────────────────────────┐
#  │  PUBLIC SUBNETS (10.0.1.0/24, etc.) │  ← ALB lives here
#  │  Has route to Internet Gateway       │
#  └─────────────────────────────────────┘
#     │ NAT Gateway (private → internet)
#  ┌──┴──────────────────────────────────┐
#  │  PRIVATE SUBNETS (10.0.11.0/24,etc.)│  ← EC2 instances live here
#  │  Internet via NAT only (outbound)   │
#  └─────────────────────────────────────┘
#     │ (no internet at all)
#  ┌──┴──────────────────────────────────┐
#  │  DATABASE SUBNETS (10.0.21.0/24,etc)│  ← RDS lives here
#  │  No internet access whatsoever      │
#  └─────────────────────────────────────┘
#
# WHY THREE TIERS?
#   - Public: things the internet talks to (load balancer)
#   - Private: your app servers (internet can reach them via ALB, not directly)
#   - Database: only accessible from private subnet (defense in depth)
##############################################################################

# ============================================================================
# VPC — The isolated network container for all your resources
# ============================================================================

resource "aws_vpc" "main" {
  cidr_block = var.vpc_cidr

  # enable_dns_support + enable_dns_hostnames are needed for:
  # - RDS to get a DNS hostname instead of just an IP
  # - ECS service discovery
  # - SSM Session Manager (no-SSH access to EC2)
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-vpc"
  })
}

# ============================================================================
# INTERNET GATEWAY — Connects the VPC to the internet
# Required for anything in public subnets to be reachable.
# ============================================================================

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-igw"
  })
}

# ============================================================================
# SUBNETS — Divided into 3 tiers, spread across availability zones
#
# cidrsubnet(vpc_cidr, 8, index) explanation:
#   cidrsubnet("10.0.0.0/16", 8, 1) → "10.0.1.0/24"
#   cidrsubnet("10.0.0.0/16", 8, 2) → "10.0.2.0/24"
#   8 bits added = /16 + /8 = /24 subnets (256 IPs each)
# ============================================================================

# PUBLIC SUBNETS — one per AZ, for ALB
resource "aws_subnet" "public" {
  count = length(var.availability_zones)

  vpc_id            = aws_vpc.main.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, count.index + 1) # 10.0.1.0/24, 10.0.2.0/24
  availability_zone = var.availability_zones[count.index]

  # Auto-assign public IPs to instances launched here.
  # ALBs need this; EC2 instances in private subnets do NOT need this.
  map_public_ip_on_launch = true

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-public-${var.availability_zones[count.index]}"
    Tier = "public"
    # These tags are used by Kubernetes/EKS to auto-discover subnets.
    # Keep them even if you're not using K8s yet — good practice.
    "kubernetes.io/role/elb" = "1"
  })
}

# PRIVATE SUBNETS — one per AZ, for EC2 instances
resource "aws_subnet" "private" {
  count = length(var.availability_zones)

  vpc_id            = aws_vpc.main.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, count.index + 11) # 10.0.11.0/24, 10.0.12.0/24
  availability_zone = var.availability_zones[count.index]

  # No public IPs — instances here are not directly reachable from internet.
  map_public_ip_on_launch = false

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-private-${var.availability_zones[count.index]}"
    Tier = "private"
    "kubernetes.io/role/internal-elb" = "1"
  })
}

# DATABASE SUBNETS — one per AZ, for RDS (most restrictive)
resource "aws_subnet" "database" {
  count = length(var.availability_zones)

  vpc_id            = aws_vpc.main.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, count.index + 21) # 10.0.21.0/24, 10.0.22.0/24
  availability_zone = var.availability_zones[count.index]

  map_public_ip_on_launch = false

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-database-${var.availability_zones[count.index]}"
    Tier = "database"
  })
}

# ============================================================================
# NAT GATEWAY — Allows private subnet instances to reach the internet
# (outbound only — internet cannot initiate connections in)
#
# Example: EC2 in private subnet needs to:
#   - Pull Docker images from DockerHub
#   - Download OS updates
#   - Send emails via SendGrid
#
# NAT Gateway sits in a PUBLIC subnet and forwards traffic on behalf
# of private subnet instances.
# ============================================================================

# Elastic IP for NAT Gateway(s)
# If single_nat_gateway = true: create 1 EIP
# If single_nat_gateway = false: create one EIP per AZ
resource "aws_eip" "nat" {
  count = var.enable_nat_gateway ? (var.single_nat_gateway ? 1 : length(var.availability_zones)) : 0

  domain = "vpc"

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-nat-eip-${count.index + 1}"
  })

  # EIP must be created after the Internet Gateway exists
  depends_on = [aws_internet_gateway.main]
}

resource "aws_nat_gateway" "main" {
  count = var.enable_nat_gateway ? (var.single_nat_gateway ? 1 : length(var.availability_zones)) : 0

  # NAT Gateway goes in the PUBLIC subnet (to have internet access)
  subnet_id = aws_subnet.public[count.index].id

  # Each NAT Gateway needs an Elastic IP (static public IP)
  allocation_id = aws_eip.nat[count.index].id

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-nat-${count.index + 1}"
  })

  depends_on = [aws_internet_gateway.main]
}

# ============================================================================
# ROUTE TABLES — Define how traffic is routed
#
# Think of route tables like GPS routing rules:
#   - "Traffic to 0.0.0.0/0 (internet)? Go through the Internet Gateway."
#   - "Traffic to 10.0.0.0/16 (local VPC)? Route locally."
# ============================================================================

# PUBLIC route table — routes internet traffic through IGW
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-rt-public"
  })
}

# Associate each public subnet with the public route table
resource "aws_route_table_association" "public" {
  count = length(aws_subnet.public)

  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}

# PRIVATE route table(s) — routes internet traffic through NAT Gateway
# If single_nat_gateway: one route table, shared by all private subnets
# If not: one route table per AZ, each pointing to its own NAT Gateway
resource "aws_route_table" "private" {
  count = var.enable_nat_gateway ? (var.single_nat_gateway ? 1 : length(var.availability_zones)) : 1

  vpc_id = aws_vpc.main.id

  # Only add internet route if NAT Gateway is enabled
  dynamic "route" {
    for_each = var.enable_nat_gateway ? [1] : []
    content {
      cidr_block     = "0.0.0.0/0"
      nat_gateway_id = aws_nat_gateway.main[count.index].id
    }
  }

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-rt-private-${count.index + 1}"
  })
}

# Associate private subnets with private route tables
resource "aws_route_table_association" "private" {
  count = length(aws_subnet.private)

  subnet_id = aws_subnet.private[count.index].id
  # Use the correct route table index (single shared one, or per-AZ)
  route_table_id = var.single_nat_gateway ? aws_route_table.private[0].id : aws_route_table.private[count.index].id
}

# DATABASE subnets get their own route table with NO internet route.
# Databases should never have internet access — not even outbound.
resource "aws_route_table" "database" {
  vpc_id = aws_vpc.main.id

  # NO route to internet — this is intentional for security.
  # Databases only need to talk to app servers within the VPC.

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-rt-database"
  })
}

resource "aws_route_table_association" "database" {
  count = length(aws_subnet.database)

  subnet_id      = aws_subnet.database[count.index].id
  route_table_id = aws_route_table.database.id
}

# ============================================================================
# VPC FLOW LOGS — Capture all network traffic for security auditing
# Helps detect: port scans, unusual traffic, failed connection attempts
# ============================================================================

resource "aws_cloudwatch_log_group" "vpc_flow_logs" {
  name              = "/aws/vpc/flow-logs/${var.name_prefix}"
  retention_in_days = 30 # Keep 30 days of network logs

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-vpc-flow-logs"
  })
}

resource "aws_iam_role" "vpc_flow_logs" {
  name = "${var.name_prefix}-vpc-flow-logs-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "vpc-flow-logs.amazonaws.com"
      }
    }]
  })

  tags = var.tags
}

resource "aws_iam_role_policy" "vpc_flow_logs" {
  name = "${var.name_prefix}-vpc-flow-logs-policy"
  role = aws_iam_role.vpc_flow_logs.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents",
        "logs:DescribeLogGroups",
        "logs:DescribeLogStreams"
      ]
      Effect   = "Allow"
      Resource = "*"
    }]
  })
}

resource "aws_flow_log" "main" {
  iam_role_arn    = aws_iam_role.vpc_flow_logs.arn
  log_destination = aws_cloudwatch_log_group.vpc_flow_logs.arn
  traffic_type    = "ALL" # Capture ACCEPT, REJECT, and ALL traffic
  vpc_id          = aws_vpc.main.id

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-flow-log"
  })
}
