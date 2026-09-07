##############################################################################
# locals.tf — Computed Values & Environment Config Map
##############################################################################

locals {
  environment = terraform.workspace == "default" ? "dev" : terraform.workspace

  # Full cluster name includes environment: "moeware-ims-dev", "moeware-ims-prod"
  cluster_name = "${var.cluster_name}-${local.environment}"

  # Naming prefix for all AWS resources
  name_prefix = "moeware-ims-${local.environment}"

  # ============================================================================
  # PER-ENVIRONMENT OVERRIDES
  # Same pattern as the EC2 Terraform — one map, picked by workspace name.
  # ============================================================================
  env_config = {
    dev = {
      # Cluster
      cluster_endpoint_public_access = true # kubectl from laptop — OK for dev
      kubernetes_version             = "1.29"

      # Nodes — small, 2 nodes is enough for dev
      node_instance_type = "t3.medium"
      node_min_size      = 1
      node_desired_size  = 2
      node_max_size      = 3

      # Database — use StatefulSet in cluster, NOT RDS
      enable_rds               = false
      db_instance_class        = "db.t3.micro"
      db_multi_az              = false
      db_backup_retention_days = 1
      db_deletion_protection   = false

      # Cost optimization
      use_spot_nodes = true # Spot instances save ~70% on worker nodes
    }

    prod = {
      # Cluster — locked down
      cluster_endpoint_public_access = false # kubectl only via VPN/bastion
      kubernetes_version             = "1.29"

      # Nodes — 3 minimum for HA across 3 AZs
      node_instance_type = "t3.medium"
      node_min_size      = 3
      node_desired_size  = 3
      node_max_size      = 10

      # Database — RDS with Multi-AZ and 30-day backups
      enable_rds               = true
      db_instance_class        = "db.t3.medium"
      db_multi_az              = true
      db_backup_retention_days = 30
      db_deletion_protection   = true

      # On-demand only in production — Spot can be interrupted
      use_spot_nodes = false
    }
  }

  config = lookup(local.env_config, local.environment, local.env_config["dev"])

  # Resolved values — used in module calls below
  enable_rds     = local.config.enable_rds
  use_spot_nodes = local.config.use_spot_nodes

  common_tags = {
    Environment = local.environment
    Cluster     = local.cluster_name
    Terraform   = "true"
  }
}
