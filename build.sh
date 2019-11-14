#!/bin/sh

mvn -f /home/git/bookmarks/pom.xml -Dmaven.test.skip=true -Dspring.profiles.active=prod clean package

rm -rf /opt/tomcat/webapps/bookmarks/*

cp -r /home/git/bookmarks/target/bookmarks/. /opt/tomcat/webapps/bookmarks/
