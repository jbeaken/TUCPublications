#!/bin/bash
# Script to build beans from git
git pull
/opt/maven/bin/mvn clean package
cp /opt/tomcat/webapps/bookmarks.war /home/bak/
<<<<<<< HEAD
<<<<<<< HEAD
#rm -rf /opt/tomcat/webapps/bookmarks/
#rm /opt/tomcat/logs/*
cp target/bookmarks.war /opt/tomcat/webapps/bookmarks.war
systemctl start tomcat
=======
=======
>>>>>>> 30c8b70... upgrading sourcer 1.7
sudo rm -rf /opt/tomcat/webapps/bookmarks*
sudo rm /opt/tomcat/logs/*
sudo cp target/bookmarks.war /opt/tomcat/webapps/bookmarks.war
sudo stop tomcat


<<<<<<< HEAD
>>>>>>> 6688150... Merge
=======
sudo cp target/bookmarks.war /opt/tomcat/webapps/bookmarks.war
sudo start tomcat
>>>>>>> 30c8b70... upgrading sourcer 1.7
