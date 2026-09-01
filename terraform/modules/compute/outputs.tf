##############################################################################
# modules/compute/outputs.tf
##############################################################################

output "alb_dns_name" {
  description = "DNS name of the Application Load Balancer."
  value       = aws_lb.main.dns_name
}

output "alb_zone_id" {
  description = "Zone ID of the ALB — needed for Route53 alias records."
  value       = aws_lb.main.zone_id
}

output "alb_arn" {
  description = "ARN of the ALB."
  value       = aws_lb.main.arn
}

output "autoscaling_group_name" {
  description = "Name of the Auto Scaling Group — used in deployment scripts."
  value       = aws_autoscaling_group.app.name
}

output "app_security_group_id" {
  description = "Security group ID for EC2 instances — referenced by database module to allow DB connections."
  value       = aws_security_group.app.id
}

output "ec2_instance_role_name" {
  description = "IAM role name for EC2 instances — referenced by storage module to grant S3 access."
  value       = aws_iam_role.ec2_instance.name
}

output "launch_template_id" {
  description = "Launch Template ID."
  value       = aws_launch_template.app.id
}

output "target_group_arn" {
  description = "Backend target group ARN."
  value       = aws_lb_target_group.backend.arn
}
