#!/bin/bash
# Script to build beans from git


# Get latest from repository
git pull

if [ "$?" = "1" ]; then
	echo "git error, aborting!" 
	exit 1
fi

# Clean and build
mvn -f /home/git/bookmarks clean package

if [ "$?" = "1" ]; then
	echo "maven error, aborting!"
        exit 1
fi

# Stop tomcat
sudo systemctl stop tomcat

sudo rm -rf /opt/tomcat/webapps/bookmarks*

sudo cp /home/git/bookmarks/target/bookmarks.war /opt/tomcat/webapps/bookmarks.war

# Start tomcat
sudo systemctl start tomcat
