#/bin/bash -e

sudo yum update -y
sudo amazon-linux-extras install -y corretto8
sudo amazon-linux-extras install -y tomcat8.5

# Directories
sudo mkdir -p /home/bookmarks/log
sudo mkdir -p /home/bookmarks/images
mkdir ~/.aws

# Extract needed files
tar xjf stuff.tar.bz2

# tomcat
sudo cp stuff/tomcat/conf/tomcat.conf /etc/tomcat/conf.d/bookmarks.conf
sudo cp stuff/tomcat/webapps/ROOT.war /usr/share/tomcat/webapps/ROOT.war

# images
cp stuff/aws/credentials ~/.aws/credentials
cp stuff/aws/config ~/.aws/config
sudo chown ec2-user:ec2-user -R /home/bookmarks/

# aws s3 sync s3://paper-files-bucket /home/paper/Image

# sudo systemctl enable tomcat
#sudo systemctl enable httpd
sudo systemctl enable tomcat

#sudo systemctl start httpd
#sudo systemctl start tomcat
