#! /usr/bin/bash

TOMCATVERSION=9.0.27
MAVENVERSION=3.5.4

# Install sdkman with java and maven
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 11.0.5.hs-adpt
sdk install maven

# Packages
apt -y install git vim-nox

# git
mkdir /home/git
chown beans:beans -R /home/git
git clone https://github.com/jbeaken/festival.git /home/git/festival
git clone https://github.com/jbeaken/TUCPublications.git /home/git/bookmarks
git clone https://github.com/jbeaken/bmw.git /home/git/bmw

# Gives access to application-prod.properties and init-mariadb.sql
gpg -d /home/git/bookmarks/src/main/etc/install/ubuntu/app.gpg > app.tar
tar xf app.tar
mv bmw-prod.properties /home/git/bmw/src/main/resources
mv festival-prod.properties /home/git/festival/src/main/resources
mv bookmarks-prod.properties /home/git/bookmarks/src/main/resources/spring

# Link build files
ln -s /home/git/festival/build.sh buildFestival.sh
ln -s /home/git/bookmarks/build.sh buildBookmarks.sh
ln -s /home/git/bmw/build.sh buildBmw.sh

sh buildFestival.sh
sh buildBmw.sh
sh buildBookmarks.sh

# Directories
mkdir -p /home/bookmarks/logs
mkdir /home/bookmarks/backups
mkdir /home/bookmarks/ssl

# Tomcat
wget -P /tmp http://apache.mirror.anlx.net/tomcat/tomcat-9/v$TOMCATVERSION/bin/apache-tomcat-$TOMCATVERSION.tar.gz
# Extract
tar xf /tmp/apache-tomcat-$TOMCATVERSION.tar.gz -C /opt
# Link
ln -s /opt/apache-tomcat-$TOMCATVERSION /opt/tomcat
# Directories
rm -rf /opt/tomcat/webapps
mkdir /opt/tomcat/webapps/bmw
mkdir /opt/tomcat/webapps/bookmarks
mkdir /opt/tomcat/webapps/festival
cp -rf /home/git/festival/src/main/etc/tomcat/Catalina /opt/apache-tomcat-$TOMCATVERSION/conf/Catalina
cp /home/git/festival/src/main/etc/tomcat/server.xml /opt/tomcat/conf/
cp /home/git/festival/src/main/etc/tomcat/setenv.sh /opt/tomcat/bin/

# Systemd
cp /home/git/festival/src/main/etc/tomcat/tomcat.service /lib/systemd/system/
systemctl enable tomcat
systemctl start tomcat


# Mysql
apt -y install mysql-server


# Build bookmarks database
gpg -d /home/git/bookmarks/src/main/etc/backup/password.gpg > /home/bookmarks/password
DAY_OF_WEEK=$(date +"%a")
sh /home/bookmarks/restore.sh $DAY_OF_WEEK
sudo echo "PATH=$PATH:/opt/maven/bin" >> /etc/environment
. /etc/environment
