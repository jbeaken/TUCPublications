#! /usr/bin/bash

# Variables
TOMCATVERSION=9.0.7
MAVENVERSION=3.5.2

# Install Java, accept license automatically
#sudo echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections

# Install java 8
#sudo echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee /etc/apt/sources.list.d/webupd8team-java.list
#sudo echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list
#sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
#sudo apt update
#sudo apt -y install oracle-java8-installer
#sudo apt install oracle-java8-set-default

# Directories
sudo mkdir /home/bak
sudo mkdir -p /home/bookmarks/logs
sudo mkdir /home/bookmarks/backups
sudo mkdir /home/git
sudo mkdir /home/ssl

# Permissions
sudo chown beans:beans -R /home/bookmarks
sudo chown beans:beans -R /home/git

# Clone bookmarks repo
git clone ssh://root@bookmarksbookshop.co.uk/home/git/bookmarks /home/git/bookmarks

#Copy files
cp /home/git/bookmarks/src/main/etc/getIP.sh /home/bookmarks
cp /home/git/bookmarks/src/main/etc/backup/backupBeansDB.sh /home/bookmarks/backups/
cp /home/git/bookmarks/src/main/etc/build/build.sh /home/bookmarks/
cp /home/git/bookmarks/src/main/etc/backup/restore.sh /home/bookmarks/

# Maven
#echo Installing maven  $MAVENVERSION
#wget apache.mirror.anlx.net/maven/maven-3/$MAVENVERSION/binaries/apache-maven-$MAVENVERSION-bin.tar.gz
#sudo tar xf apache-maven-$MAVENVERSION-bin.tar.gz -C /opt
#sudo rm /opt/maven
#sudo ln -s /opt/apache-maven-$MAVENVERSION /opt/maven

# Mysql
#sudo apt -y install mysql-server
#mysql -uroot -p < /home/git/bookmarks/src/main/etc/install/create-bookmarks-db.sql

# Tomcat
#sudo wget apache.mirror.anlx.net/tomcat/tomcat-9/v$TOMCATVERSION/bin/apache-tomcat-$TOMCATVERSION.tar.gz
#sudo tar xf apache-tomcat-$TOMCATVERSION.tar.gz -C /opt
#sudo ln -s /opt/apache-tomcat-$TOMCATVERSION /opt/tomcat
#sudo rm -rf /opt/tomcat/webapps/*
#sudo cp /home/git/bookmarks/src/main/etc/conf/server.xml /opt/tomcat/conf/server.xml
#sudo cp /home/git/bookmarks/src/main/etc/conf/setenv-prod.sh /opt/tomcat/bin/setenv.sh

# Tomcat systemd
#sudo cp /home/git/bookmarks/src/main/etc/install/tomcat.service /lib/systemd/system/
#sudo systemctl enable tomcat

# OPENSSL
#DO DO

# CRONTAB

# Write to path
#cp /etc/environment /etc/environment.bak
#echo "PATH=$PATH:/opt/maven/bin" >> /etc/environment
#. /etc/environment

# Permissions for git
sudo chown beans:beans -R /home/git

# Build bookmarks
#git clone https://github.com/grelland/jasypt-hibernate5 /home/git/jasypt-hibernate5
#/opt/maven/bin/mvn -f /home/git/jasypt-hibernate5 -Dmaven.javadoc.skip=true -Dgpg.skip=true install

#ln -s /home/git/bookmarks/src/main/etc/build/build.sh /home/bookmarks/build.sh
#sh /home/bookmarks/build.sh

# Build bookmarks database
gpg -d /home/git/bookmarks/src/main/etc/backup/password.gpg > /home/bookmarks/password
DAY_OF_WEEK=$(date +"%a")
sh /home/bookmarks/restore.sh $DAY_OF_WEEK


# Import bookmarks database
# THIS REQUIRES THE INFO@BOOKMARKSBOOKSHOP.CO.UK PRIVATE KEY TO BE INSTALLED.
# Find at dropbox
#gpg -d /home/bookmarks/bm.$FILENAME.sql.gpg > /home/bookmarks/bm.sql
#mysql -uroot -p < /home/bookmarks/bm.sql
#shred /home/bookmarks/bm.sql
#rm /home/bookmarks/bm.sql

# Static ip
# /etc/network/interfaces
# The primary network interface
#allow-hotplug eth0
#iface eth0 inet static
# address 192.168.1.88
# netmask 255.255.255.0
# gateway 192.168.1.1
# dns-domain bookmarks
# dns-nameservers 192.168.1.1

# File server
#apt-get install samba cifs-utils
#Add to fstab
#UUID=144E3AED4E3AC770 /srv/samba/share ntfs guest 0 0

# Add to /etc/samba/smb.conf
# [share]
#    comment = BRAIN File Server Share
#    path = /srv/samba/share
#    browsable = yes
#    guest ok = yes
#    read only = no
#    create mask = 0755
