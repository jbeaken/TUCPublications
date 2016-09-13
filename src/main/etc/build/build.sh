#!/bin/bash
# Script to build beans from git

# Make sure only root can run our script
if [ "$(id -u)" = "0" ]; then
   echo "Do not run as sudo, valid command is : sh build.sh" 1>&2
   exit 1
fi

# Get latest from repository
git pull
if [ "$?" = "1" ]; then
	echo "git error, aborting!" 
	exit 1
fi

# Clean and build
mvn clean package
if [ "$?" = "1" ]; then
	echo "maven error, aborting!"
        exit 1
fi

# Back up
cp /usr/local/share/tomcat/webapps/bookmarks.war /home/bak/

# Stop tomcat
sudo systemctl stop tomcat

sudo rm -rf /usr/local/share/tomcat/webapps/bookmarks*
sudo rm /usr/local/share/tomcat/logs/*

sudo cp target/bookmarks.war /usr/local/share/tomcat/webapps/bookmarks.war

# Start tomcat
sudo systemctl start tomcat
