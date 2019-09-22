module "lb_security_group" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 3.0"

  name        = "lb"
  description = "Security group for load balancer"
  vpc_id      = module.vpc.vpc_id

  ingress_with_cidr_blocks = [
    {
      from_port   = 80
      to_port     = 80
      protocol    = "tcp"
      description = "HTTP ports"
      cidr_blocks = "0.0.0.0/0"
    },
    {
        from_port   = 443
        to_port     = 443
        protocol    = "tcp"
        description = "SSL ports"
        cidr_blocks = "0.0.0.0/0"
    }
  ]

  egress_rules        = ["all-all"]
}

# resource "aws_eip" "front_end" {
#   vpc      = true
# }


resource "aws_lb" "front_end" {
  name               = "bookmarks-lb-tf"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [module.lb_security_group.this_security_group_id]
  subnets            = module.vpc.public_subnets

  enable_deletion_protection = true

  # access_logs {
  #   bucket  = "${aws_s3_bucket.lb_logs.bucket}"
  #   prefix  = "test-lb"
  #   enabled = false
  # }

  tags = {
    Environment = "production"
  }
}

resource "aws_lb_target_group" "front_end" {
  name     = "bookmarks-lb-tg"
  port     = 8080
  protocol = "HTTP"
  target_type = "instance"
  vpc_id   = module.vpc.vpc_id

  health_check {
    enabled = "true"
    interval = 300
    port = 8080
    protocol = "HTTP"
    path = "/"
    matcher = "302"
  }
}

resource "aws_lb_target_group_attachment" "front_end" {
  target_group_arn = "${aws_lb_target_group.front_end.arn}"
  target_id        = module.ec2.id[0]
}

resource "aws_lb_listener" "front_end-http" {
  load_balancer_arn = "${aws_lb.front_end.arn}"
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type = "redirect"

    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}

resource "aws_lb_listener" "front_end-https" {
  load_balancer_arn = "${aws_lb.front_end.arn}"
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = "arn:aws:iam::325312459751:server-certificate/bookmarks"


  default_action {
    type             = "forward"
    target_group_arn = "${aws_lb_target_group.front_end.arn}"
  }
}

# resource "aws_lb_listener" "front_end" {
#   load_balancer_arn = "${aws_lb.front_end.arn}"
#   port              = "443"
#   protocol          = "HTTPS"
#   ssl_policy        = "ELBSecurityPolicy-2016-08"
#   certificate_arn   = "arn:aws:iam::187416307283:server-certificate/test_cert_rab3wuqwgja25ct3n4jdj2tzu4"
#
#   default_action {
#     type             = "forward"
#     target_group_arn = "${aws_lb_target_group.front_end.arn}"
#   }
# }
