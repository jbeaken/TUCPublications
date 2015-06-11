#!/bin/bash
# Script to build beans from git
git pull
/opt/maven/bin/mvn clean install
cp /opt/tomcat/webapps/bookmarks.war /home/bak/
#rm -rf /opt/tomcat/webapps/bookmarks/
#rm /opt/tomcat/logs/*
cp target/bookmarks.war /opt/tomcat/webapps/bookmarks.war
restart tomcat
