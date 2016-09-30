#!/bin/bash
# Script to build beans from git


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
systemctl stop tomcat

rm -rf /usr/local/share/tomcat/webapps/bookmarks*

cp target/bookmarks.war /usr/local/share/tomcat/webapps/bookmarks.war

# Start tomcat
systemctl start tomcat
