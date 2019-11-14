variable "instances_number" {
  default = 1
}

##################################################################
# Data sources to get VPC, subnet, security group and AMI details
##################################################################

data "aws_ami" "bookmarks" {
  most_recent = true

  owners = ["325312459751"]

  filter {
    name = "name"

    values = [
      "bookmarks*",
    ]
  }
}

module "security_group" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 3.0"

  name        = "ec2"
  description = "Security group for example usage with EC2 instance"
  vpc_id      = module.vpc.vpc_id

  ingress_with_cidr_blocks = [
    {
      from_port   = 8080
      to_port     = 8080
      protocol    = "tcp"
      description = "User-service ports"
      cidr_blocks = "0.0.0.0/0"
    },
    {
      from_port   = 22
      to_port     = 22
      protocol    = "tcp"
      description = "SSH"
      cidr_blocks = "0.0.0.0/0"
    }
  ]

  egress_rules        = ["all-all"]
}

module "ec2" {
  source = "terraform-aws-modules/ec2-instance/aws"

  instance_count = var.instances_number

  name                        = "bookmarks"
  ami                         = data.aws_ami.bookmarks.id
  instance_type               = "t2.micro"
  subnet_id                   = module.vpc.public_subnets[0]
  vpc_security_group_ids      = [module.security_group.this_security_group_id]
  associate_public_ip_address = false
  key_name             = "${var.key_name}"

}

resource "aws_volume_attachment" "this_ec2" {
  count = var.instances_number

  device_name = "/dev/sdh"
  volume_id   = aws_ebs_volume.this[count.index].id
  instance_id = module.ec2.id[count.index]
}

resource "aws_ebs_volume" "this" {
  count = var.instances_number

  availability_zone = module.ec2.availability_zone[count.index]
  size              = 1
}
