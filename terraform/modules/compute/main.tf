##############################################################################
# modules/compute/main.tf
#
# ARCHITECTURE:
#   Internet → ALB (public subnet) → EC2 instances (private subnets)
#
# Components:
#   1. Security Groups    — firewall rules for ALB and EC2
#   2. IAM Role           — permissions for EC2 to access S3, SSM, CloudWatch
#   3. Application Load Balancer — distributes traffic across instances
#   4. Launch Template    — EC2 instance configuration blueprint
#   5. Auto Scaling Group — manages fleet of instances
#   6. Scaling Policies   — scale up/down based on CPU
##############################################################################

# ============================================================================
# SECURITY GROUPS
# Think of these as virtual firewalls that control traffic in/out of resources.
#
# PRINCIPLE: Deny everything by default, explicitly allow only what's needed.
# ============================================================================

# ALB Security Group — allows internet traffic in, forwards to EC2
resource "aws_security_group" "alb" {
  name        = "${var.name_prefix}-alb-sg"
  description = "Security group for Application Load Balancer. Allows HTTP/HTTPS from internet."
  vpc_id      = var.vpc_id

  # INBOUND: Allow HTTP and HTTPS from anywhere (internet users)
  ingress {
    description = "HTTP from internet"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTPS from internet"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # OUTBOUND: ALB needs to forward traffic to EC2 instances (on port 8080)
  egress {
    description = "All outbound to EC2 instances"
    from_port   = 0
    to_port     = 0
    protocol    = "-1" # All protocols
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-alb-sg"
  })

  lifecycle {
    create_before_destroy = true # Prevent downtime during SG updates
  }
}

# EC2 Security Group — only accepts traffic from the ALB, not directly from internet
resource "aws_security_group" "app" {
  name        = "${var.name_prefix}-app-sg"
  description = "Security group for EC2 app servers. Only accepts traffic from ALB."
  vpc_id      = var.vpc_id

  # INBOUND: Only from ALB, on the Spring Boot port
  ingress {
    description     = "HTTP from ALB on Spring Boot port"
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id] # ONLY from ALB, not from internet
  }

  # Frontend (Nginx) port
  ingress {
    description     = "Frontend Nginx from ALB"
    from_port       = 3000
    to_port         = 3000
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  # SSH — restricted (only if key pair is set)
  # In production, prefer SSM Session Manager over SSH (no keys needed)
  dynamic "ingress" {
    for_each = var.key_pair_name != null ? [1] : []
    content {
      description = "SSH access (restricted)"
      from_port   = 22
      to_port     = 22
      protocol    = "tcp"
      # IMPORTANT: In real production, replace with your VPN/bastion CIDR
      # Never allow SSH from 0.0.0.0/0 in production
      cidr_blocks = ["10.0.0.0/8"] # Only from within VPC/VPN
    }
  }

  # OUTBOUND: EC2 needs to reach internet for Docker pulls, updates, etc.
  egress {
    description = "All outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-app-sg"
  })

  lifecycle {
    create_before_destroy = true
  }
}

# ============================================================================
# IAM ROLE FOR EC2
#
# Instead of storing AWS credentials on EC2 instances (dangerous!),
# we give the instance a ROLE. When the app runs `aws s3 cp ...`,
# it automatically uses the instance's role credentials.
#
# This is the AWS-recommended way — no keys to rotate, no risk of exposure.
# ============================================================================

resource "aws_iam_role" "ec2_instance" {
  name = "${var.name_prefix}-ec2-role"
  description = "IAM role for EC2 instances. Allows access to S3, SSM, CloudWatch."

  # This trust policy says: "EC2 service can assume this role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "ec2.amazonaws.com" }
    }]
  })

  tags = var.tags
}

# Allow EC2 to use SSM Session Manager (SSH alternative — no key needed)
resource "aws_iam_role_policy_attachment" "ssm" {
  role       = aws_iam_role.ec2_instance.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

# Allow EC2 to send logs and metrics to CloudWatch
resource "aws_iam_role_policy_attachment" "cloudwatch" {
  role       = aws_iam_role.ec2_instance.name
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy"
}

# Custom policy for S3 access (only to the app's specific bucket)
resource "aws_iam_role_policy" "s3_access" {
  name = "${var.name_prefix}-s3-access"
  role = aws_iam_role.ec2_instance.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "S3BucketAccess"
        Effect = "Allow"
        Action = [
          "s3:GetObject",
          "s3:PutObject",
          "s3:DeleteObject",
          "s3:ListBucket"
        ]
        Resource = [
          "arn:aws:s3:::${var.s3_bucket_name}",
          "arn:aws:s3:::${var.s3_bucket_name}/*"
        ]
      },
      {
        Sid    = "ECRAccess"
        Effect = "Allow"
        Action = [
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage",
          "ecr:GetAuthorizationToken"
        ]
        Resource = "*"
      }
    ]
  })
}

# Instance profile — the "holder" that attaches an IAM role to an EC2 instance
resource "aws_iam_instance_profile" "ec2" {
  name = "${var.name_prefix}-ec2-profile"
  role = aws_iam_role.ec2_instance.name

  tags = var.tags
}

# ============================================================================
# APPLICATION LOAD BALANCER
#
# The ALB is the public-facing entry point. It:
# - Accepts HTTP/HTTPS traffic from users
# - Routes /api/* to backend instances on port 8080
# - Routes /* to frontend instances on port 3000
# - Performs health checks and stops routing to unhealthy instances
# - Enables HTTPS termination (decrypts SSL once here, plain HTTP to backends)
# ============================================================================

resource "aws_lb" "main" {
  name               = "${var.name_prefix}-alb"
  internal           = false # internet-facing
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = var.public_subnet_ids # ALB spans all public subnets

  # Access logs for debugging and compliance
  # Stores logs in S3 (requires a bucket — simplified here)
  # access_logs {
  #   bucket  = "your-alb-logs-bucket"
  #   enabled = true
  # }

  # Deletion protection for production — prevents accidental deletion
  enable_deletion_protection = var.environment == "prod" ? true : false

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-alb"
  })
}

# Target Group — the pool of EC2 instances that receive traffic
resource "aws_lb_target_group" "backend" {
  name     = "${var.name_prefix}-backend-tg"
  port     = 8080     # Spring Boot port
  protocol = "HTTP"
  vpc_id   = var.vpc_id

  # Health check — ALB polls this endpoint. If it returns 200, the instance is healthy.
  # If health check fails 3x, the instance is removed from rotation.
  health_check {
    enabled             = true
    healthy_threshold   = 2   # 2 consecutive successes = healthy
    unhealthy_threshold = 3   # 3 consecutive failures = unhealthy
    timeout             = 10  # seconds to wait for response
    interval            = 30  # seconds between health checks
    path                = "/api/v1/health" # Spring Boot Actuator health endpoint
    matcher             = "200"
  }

  # Keep connections alive during rolling deployments
  deregistration_delay = 30 # Wait 30s before removing deregistered instance

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-backend-tg"
  })
}

# HTTP Listener — accepts port 80, redirects to HTTPS
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = "redirect"
    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301" # Permanent redirect
    }
  }
}

# HTTPS Listener — accepts port 443, routes to backend
# NOTE: Requires an ACM certificate. If you don't have a domain yet,
# comment out the certificate_arn and use HTTP only for testing.
resource "aws_lb_listener" "https" {
  load_balancer_arn = aws_lb.main.arn
  port              = 443
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-TLS13-1-2-2021-06" # Strong TLS settings

  # REPLACE with your ACM certificate ARN from AWS Certificate Manager
  # certificate_arn = "arn:aws:acm:us-east-1:123456789:certificate/abc-123"

  # TEMPORARY: Forward everything to backend until cert is configured
  # Remove this and uncomment certificate_arn when you have a domain
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.backend.arn
  }

  # Comment this out when you add certificate_arn above
  lifecycle {
    ignore_changes = [certificate_arn]
  }
}

# Route /api/* to backend target group
resource "aws_lb_listener_rule" "api" {
  listener_arn = aws_lb_listener.https.arn
  priority     = 100

  condition {
    path_pattern {
      values = ["/api/*"]
    }
  }

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.backend.arn
  }
}

# ============================================================================
# LAUNCH TEMPLATE — Blueprint for EC2 instances
#
# Defines everything about the instance: size, AMI, startup script, IAM role.
# The Auto Scaling Group uses this template to create instances.
# ============================================================================

resource "aws_launch_template" "app" {
  name_prefix   = "${var.name_prefix}-lt-"
  image_id      = var.ami_id
  instance_type = var.instance_type

  # Attach IAM profile so the instance can access S3, SSM, etc.
  iam_instance_profile {
    name = aws_iam_instance_profile.ec2.name
  }

  # Network interface configuration
  network_interfaces {
    associate_public_ip_address = false # Private subnet — no public IP
    security_groups             = [aws_security_group.app.id]
    delete_on_termination       = true
  }

  # SSH key (optional — use SSM Session Manager instead for production)
  key_name = var.key_pair_name

  # Encrypt the root volume for security compliance
  block_device_mappings {
    device_name = "/dev/sda1"
    ebs {
      volume_size           = 30   # GB
      volume_type           = "gp3" # Faster and cheaper than gp2
      encrypted             = true  # Encrypt disk at rest
      delete_on_termination = true
    }
  }

  # User data — script that runs when instance first boots.
  # This installs Docker and starts the application.
  user_data = base64encode(templatefile("${path.module}/user_data.sh.tpl", {
    environment        = var.environment
    aws_region         = var.aws_region
    db_endpoint        = var.db_endpoint
    db_name            = var.db_name
    db_username        = var.db_username
    db_password        = var.db_password
    s3_bucket_name     = var.s3_bucket_name
    backend_image_tag  = var.backend_image_tag
    frontend_image_tag = var.frontend_image_tag
  }))

  # Use Spot instances if enabled (significant cost savings)
  dynamic "instance_market_options" {
    for_each = var.enable_spot_instances ? [1] : []
    content {
      market_type = "spot"
      spot_options {
        max_price                      = "0.05" # Maximum price per hour
        spot_instance_type             = "persistent"
        instance_interruption_behavior = "stop"
      }
    }
  }

  # Required for Auto Scaling to work with newer instance types
  metadata_options {
    http_endpoint               = "enabled"
    http_tokens                 = "required" # Enforce IMDSv2 (security best practice)
    http_put_response_hop_limit = 1
  }

  monitoring {
    enabled = true # Enable detailed CloudWatch monitoring (1-minute intervals)
  }

  tags = var.tags

  # When launch template is updated, create new version before destroying old
  lifecycle {
    create_before_destroy = true
  }
}

# ============================================================================
# AUTO SCALING GROUP — Manages the fleet of EC2 instances
#
# Automatically adds instances when load increases, removes when it drops.
# Ensures minimum instances are always running for availability.
# ============================================================================

resource "aws_autoscaling_group" "app" {
  name = "${var.name_prefix}-asg"

  # Scale between min and max, targeting desired
  min_size         = var.min_instances
  max_size         = var.max_instances
  desired_capacity = var.desired_instances

  # Place instances in private subnets (across multiple AZs for HA)
  vpc_zone_identifier = var.private_subnet_ids

  # Register instances with the ALB target group
  target_group_arns = [aws_lb_target_group.backend.arn]

  # Use the launch template we defined above
  launch_template {
    id      = aws_launch_template.app.id
    version = "$Latest" # Always use the latest launch template version
  }

  # Health check settings
  health_check_type         = "ELB"     # Use ALB health checks (more accurate than EC2 status)
  health_check_grace_period = 300       # 5 min grace period after instance starts before health checks

  # ROLLING UPDATE STRATEGY
  # When you deploy a new version:
  # - Launch 1 new instance first (with new code)
  # - Wait until it's healthy in ALB
  # - Then terminate 1 old instance
  # - Continue until all instances are updated
  # Result: Zero downtime deployments
  instance_refresh {
    strategy = "Rolling"
    preferences {
      min_healthy_percentage = 50  # Keep 50% healthy during update
      instance_warmup        = 120 # Wait 2 minutes for instance to warm up
    }
  }

  # Instance termination policy — which instance to terminate when scaling in
  termination_policies = ["OldestInstance"] # Remove oldest first (freshest code stays)

  # Add Name tag to all instances in this ASG
  tag {
    key                 = "Name"
    value               = "${var.name_prefix}-app-server"
    propagate_at_launch = true
  }

  # Propagate all common tags to instances
  dynamic "tag" {
    for_each = var.tags
    content {
      key                 = tag.key
      value               = tag.value
      propagate_at_launch = true
    }
  }

  lifecycle {
    # Don't let Terraform destroy the ASG if capacity was changed by Auto Scaling
    ignore_changes = [desired_capacity]
  }
}

# ============================================================================
# AUTO SCALING POLICIES — When to add/remove instances
# ============================================================================

# Scale UP when average CPU > 70% for 2 consecutive 5-minute periods
resource "aws_autoscaling_policy" "scale_up" {
  name                   = "${var.name_prefix}-scale-up"
  autoscaling_group_name = aws_autoscaling_group.app.name
  policy_type            = "TargetTrackingScaling"

  target_tracking_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ASGAverageCPUUtilization"
    }
    target_value = 70.0 # Target 70% CPU utilization
  }
}

# Scale DOWN when average CPU < 30% (handled automatically by TargetTracking above)
# Target tracking automatically scales down when below target.
