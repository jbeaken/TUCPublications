#!/bin/bash
# Script to build beans from git

# Make sure only root can run our script
if [ "$(id -u)" != "0" ]; then
   echo "This script must be run as root" 1>&2
   exit 1
fi

# Get latest from repository
git pull

. /etc/environment

# Clean and build
mvn clean package

# Back up
cp /usr/local/share/tomcat/webapps/bookmarks.war /home/bak/

# Stop tomcat
systemctl stop tomcat

rm -rf /usr/local/share/tomcat/webapps/bookmarks*
rm /usr/local/share/tomcat/logs/*

cp target/bookmarks.war /usr/local/share/tomcat/webapps/bookmarks.war

# Start tomcat
systemctl start tomcat
