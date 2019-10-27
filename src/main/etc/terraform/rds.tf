module "db_security_group" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 3.0"

  name        = "database"
  description = "Security group for rds"
  vpc_id      = module.vpc.vpc_id

  ingress_with_source_security_group_id = [ {
      rule                     = "mysql-tcp"
      source_security_group_id = module.security_group.this_security_group_id
    }
  ]
}

module "db" {
  source = "terraform-aws-modules/rds/aws"

  identifier = "bookmarksdb"

  # All available versions: http://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_MySQL.html#MySQL.Concepts.VersionMgmt
  engine            = "mysql"
  engine_version    = "5.7.26"
  instance_class    = "db.t2.small"
  allocated_storage = 5
  storage_encrypted = false

  # kms_key_id        = "arm:aws:kms:<region>:<accound id>:key/<kms key id>"
  name     = "bookmarksdb"
  username = "${var.rds_username}"
  password = "${var.rds_password}"
  port     = "3306"

  vpc_security_group_ids = [module.db_security_group.this_security_group_id]

  maintenance_window = "Mon:00:00-Mon:03:00"
  backup_window      = "03:00-06:00"

  multi_az = true

  # disable backups to create DB faster
  backup_retention_period = 0

  tags = {
    Owner       = "bookmarks"
    Environment = "dev"
  }

  enabled_cloudwatch_logs_exports = ["audit", "general"]

  # DB subnet group
  subnet_ids = module.vpc.private_subnets

  # DB parameter group
  family = "mysql5.7"

  # DB option group
  major_engine_version = "5.7"

  # Snapshot name upon DB deletion
  final_snapshot_identifier = "bookmarksdb"

  # Database Deletion Protection
  deletion_protection = false

  parameters = [
    {
      name  = "character_set_client"
      value = "utf8"
    },
    {
      name  = "character_set_server"
      value = "utf8"
    }
  ]

  options = [
    {
      option_name = "MARIADB_AUDIT_PLUGIN"

      option_settings = [
        {
          name  = "SERVER_AUDIT_EVENTS"
          value = "CONNECT"
        },
        {
          name  = "SERVER_AUDIT_FILE_ROTATIONS"
          value = "37"
        },
      ]
    },
  ]
}
