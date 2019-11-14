resource "aws_route53_zone" "bookmarks" {
  name = "bookmarks.com"

  vpc {
    vpc_id = module.vpc.vpc_id
  }
}


resource "aws_route53_record" "database" {
      zone_id = "${aws_route53_zone.bookmarks.zone_id}"
      name = "database.bookmarks.com"
      type = "CNAME"
      ttl = "300"
      records = [module.db.this_db_instance_address]
}
