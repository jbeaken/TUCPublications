#!/bin/bash

if [ -z "$1" ]
  then
     echo "Enter version number (must be higher than any in /opt/tomcat/webapps)"
     exit 1
fi

set -e
NOW=$(date +"%m-%d-%Y")
#export JAVA_HOME=/usr/lib/jvm/java-8-oracle

/opt/maven/bin/mvn -DskipTests clean install

echo Deploying new version bookmarks##$1.war

sudo cp target/bookmarks.war /opt/tomcat/webapps/bookmarks##$1.war





