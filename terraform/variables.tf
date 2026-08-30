##############################################################################
# locals.tf — Local Values (Computed Variables)
#
# Locals are like variables, but they're COMPUTED inside Terraform
# rather than passed in from outside. Use them to:
#   - Derive values from other variables (avoid repetition)
#   - Create maps of settings that differ per environment
#   - Build naming conventions consistently
#
# WORKSPACE PATTERN:
#   terraform.workspace returns the current workspace name.
#   We use it to look up the right settings from a map.
#   This way, one codebase handles dev/staging/prod cleanly.
##############################################################################

locals {
  # The current environment name, derived from the Terraform workspace.
  # `terraform workspace select prod` → local.environment = "prod"
  # This is the CORE pattern for multi-environment management.
  environment = terraform.workspace == "default" ? "dev" : terraform.workspace

  # ===========================================================================
  # ENVIRONMENT-SPECIFIC OVERRIDES
  #
  # This map is the heart of the multi-environment pattern.
  # Each workspace gets its own set of sane defaults.
  # Values here OVERRIDE the root variables for that environment.
  # ===========================================================================
  env_config = {
    dev = {
      # Compute — smallest sizes to save money
      instance_type         = "t3.micro"
      min_instances         = 1
      desired_instances     = 1
      max_instances         = 2
      enable_spot_instances = true # Save ~70% on dev servers

      # Database — minimal, no redundancy needed
      db_instance_class        = "db.t3.micro"
      db_multi_az              = false
      db_backup_retention_days = 1
      db_deletion_protection   = false
      db_allocated_storage     = 20

      # Networking — single NAT gateway to save money
      single_nat_gateway = true
      enable_nat_gateway = true

      # Tags
      cost_center = "engineering-dev"
    }

    staging = {
      # Compute — close to prod but cheaper
      instance_type         = "t3.small"
      min_instances         = 1
      desired_instances     = 2
      max_instances         = 3
      enable_spot_instances = true

      # Database — slightly larger, short backup retention
      db_instance_class        = "db.t3.small"
      db_multi_az              = false
      db_backup_retention_days = 7
      db_deletion_protection   = false
      db_allocated_storage     = 20

      # Networking
      single_nat_gateway = true
      enable_nat_gateway = true

      # Tags
      cost_center = "engineering-staging"
    }

    prod = {
      # Compute — production-grade sizing
      instance_type         = "t3.medium"
      min_instances         = 2   # Always at least 2 for HA
      desired_instances     = 2
      max_instances         = 6   # Can handle traffic spikes
      enable_spot_instances = false # Never use Spot for production — can be terminated

      # Database — high availability, long backups, protected from deletion
      db_instance_class        = "db.t3.medium"
      db_multi_az              = true  # Automatic failover to standby
      db_backup_retention_days = 30    # 30 days of point-in-time recovery
      db_deletion_protection   = true  # Can't delete without setting this false first
      db_allocated_storage     = 50

      # Networking — one NAT per AZ for redundancy
      single_nat_gateway = false
      enable_nat_gateway = true

      # Tags
      cost_center = "engineering-prod"
    }
  }

  # Get the config for the current workspace, fallback to dev if unknown.
  config = lookup(local.env_config, local.environment, local.env_config["dev"])

  # ===========================================================================
  # NAMING CONVENTION
  #
  # Consistent naming makes it easy to find resources in AWS Console.
  # Format: {project}-{environment}-{resource}
  # Example: moeware-ims-prod-vpc
  # ===========================================================================
  name_prefix = "moeware-ims-${local.environment}"

  # Common tags applied to every resource (merged with provider default_tags).
  # These go on top of the provider-level tags.
  common_tags = {
    Environment = local.environment
    CostCenter  = local.config.cost_center
    Terraform   = "true"
  }

  # ===========================================================================
  # RESOLVED VALUES
  #
  # Merge user-supplied variables with environment defaults.
  # Environment config wins over variable defaults, but if the user
  # explicitly sets a variable via tfvars or CLI, that takes precedence.
  # ===========================================================================

  # Effective compute settings
  effective_instance_type     = local.config.instance_type
  effective_min_instances     = local.config.min_instances
  effective_desired_instances = local.config.desired_instances
  effective_max_instances     = local.config.max_instances

  # Effective database settings
  effective_db_instance_class        = local.config.db_instance_class
  effective_db_multi_az              = local.config.db_multi_az
  effective_db_backup_retention_days = local.config.db_backup_retention_days
  effective_db_deletion_protection   = local.config.db_deletion_protection
}