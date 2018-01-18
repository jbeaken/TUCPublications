#!/bin/bash
# Script to build beans from git


# Get latest from repository

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

# Stop tomcat
systemctl stop tomcat

rm -rf /opt/tomcat/webapps/bookmarks*

cp target/bookmarks.war /opt/tomcat/webapps/bookmarks.war

# Start tomcat
systemctl start tomcat
