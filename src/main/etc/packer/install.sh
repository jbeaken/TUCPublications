#/bin/bash -e

sudo yum update -y
sudo amazon-linux-extras install -y corretto8
sudo amazon-linux-extras install -y tomcat8.5

# Directories
sudo mkdir -p /home/bookmarks/log
sudo mkdir -p /home/bookmarks/images
mkdir ~/.aws

# Extract needed files
tar xjf files.tar.bz2

# tomcat
sudo cp files/tomcat/conf/server.xml /usr/share/tomcat/conf/server.xml
sudo cp files/tomcat/conf/tomcat.conf /etc/tomcat/conf.d/bookmarks.conf
sudo cp files/tomcat/webapps/ROOT.war /usr/share/tomcat/webapps/ROOT.war

# images
cp files/aws/credentials ~/.aws/credentials
cp files/aws/config ~/.aws/config
sudo chown tomcat:tomcat -R /home/bookmarks/

# aws s3 sync s3://paper-files-bucket /home/paper/Image

# Cloud watch
wget https://s3.amazonaws.com/amazoncloudwatch-agent/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm
#sudo rpm -U ./amazon-cloudwatch-agent.rpm

sudo systemctl enable tomcat
