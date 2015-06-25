#!/bin/bash
# Script to build beans from git
#git pull
systemctl stop tomcat
/opt/maven/bin/mvn clean install
cp /opt/tomcat/webapps/bookmarks.war /home/bak/
<<<<<<< HEAD
#rm -rf /opt/tomcat/webapps/bookmarks/
#rm /opt/tomcat/logs/*
cp target/bookmarks.war /opt/tomcat/webapps/bookmarks.war
systemctl start tomcat
=======
sudo rm -rf /opt/tomcat/webapps/bookmarks*
sudo rm /opt/tomcat/logs/*
sudo cp target/bookmarks.war /opt/tomcat/webapps/bookmarks.war
sudo restart tomcat


>>>>>>> 6688150... Merge
