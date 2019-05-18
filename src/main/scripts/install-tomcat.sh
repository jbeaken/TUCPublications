#! /usr/bin/bash

# Variables
TOMCATVERSION=9.0.20

# wget -P /tmp http://apache.mirror.anlx.net/tomcat/tomcat-9/v$TOMCATVERSION/bin/apache-tomcat-$TOMCATVERSION.tar.gz

# Extract
tar xf /tmp/apache-tomcat-$TOMCATVERSION.tar.gz -C /opt

# Prepare
rm -rf /opt/apache-tomcat-$TOMCATVERSION/webapps

cp /opt/tomcat/conf/server.xml /opt/apache-tomcat-$TOMCATVERSION/conf/server.xml
cp /opt/tomcat/bin/setenv.sh /opt/apache-tomcat-$TOMCATVERSION/bin/setenv.sh

cp -r /opt/tomcat/webapps /opt/apache-tomcat-$TOMCATVERSION/webapps

# Tomcat systemd
# sudo cp /home/git/puma/src/main/etc/install/tomcat.service /lib/systemd/system/
# systemctl enable tomcat

# Re-link
rm /opt/tomcat
ln -s /opt/apache-tomcat-$TOMCATVERSION /opt/tomcat

systemctl restart tomcat
