#! /usr/bin/bash

# Variables
TOMCATVERSION=9.0.20

<<<<<<< HEAD
# wget -P /tmp http://apache.mirror.anlx.net/tomcat/tomcat-9/v$TOMCATVERSION/bin/apache-tomcat-$TOMCATVERSION.tar.gz

# Extract
tar xf /tmp/apache-tomcat-$TOMCATVERSION.tar.gz -C /opt

# Prepare
rm -rf /opt/apache-tomcat-$TOMCATVERSION/webapps

cp /opt/tomcat/conf/server.xml /opt/apache-tomcat-$TOMCATVERSION/conf/server.xml
cp /opt/tomcat/bin/setenv.sh /opt/apache-tomcat-$TOMCATVERSION/bin/setenv.sh

cp -r /opt/tomcat/webapps /opt/apache-tomcat-$TOMCATVERSION/webapps
=======
wget -P /tmp http://apache.mirror.anlx.net/tomcat/tomcat-9/v$TOMCATVERSION/bin/apache-tomcat-$TOMCATVERSION.tar.gz

# Extract
sudo tar xf /tmp/apache-tomcat-$TOMCATVERSION.tar.gz -C /opt

# Prepare
sudo rm -rf /opt/apache-tomcat-$TOMCATVERSION/webapps
sudo mkdir /opt/apache-tomcat-$TOMCATVERSION/webapps
sudo cp /home/git/paper/scripts/server.ajp.xml /opt/apache-tomcat-$TOMCATVERSION/conf/server.xml
sudo cp /home/git/paper/scripts/context.xml /opt/apache-tomcat-$TOMCATVERSION/conf/context.xml
sudo cp /home/git/paper/scripts/setenv.sh /opt/apache-tomcat-$TOMCATVERSION/bin/setenv.sh

# Paper app
sudo cp /opt/tomcat/webapps/ROOT.war /opt/apache-tomcat-$TOMCATVERSION/webapps/ROOT.war
>>>>>>> ae8253f... More nonscnce

# Tomcat systemd
# sudo cp /home/git/puma/src/main/etc/install/tomcat.service /lib/systemd/system/
# systemctl enable tomcat

# Re-link
<<<<<<< HEAD
rm /opt/tomcat
ln -s /opt/apache-tomcat-$TOMCATVERSION /opt/tomcat

systemctl restart tomcat
=======
sudo rm /opt/tomcat
sudo ln -s /opt/apache-tomcat-$TOMCATVERSION /opt/tomcat

sudo systemctl restart tomcat
>>>>>>> ae8253f... More nonscnce
