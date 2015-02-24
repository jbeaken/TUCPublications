
#!/bin/sh

mvn -Dmaven.test.skip=true -Dspring.profiles.active=prod clean package

rm -rf /opt/tomcat/webapps/bookmarks/*

cp -r target/bookmarks/. /opt/tomcat/webapps/bookmarks/

systemctl restart tomcat


