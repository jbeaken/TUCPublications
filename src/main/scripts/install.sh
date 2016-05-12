# Install git
apt-get -y install git
mkdir /home/git
cd /home/git
git clone ssh://git@109.109.239.50:2298/home/git/bookmarks bookmarks

# Install Java
# Accept license automatically
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections

# Install java 8
echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee /etc/apt/sources.list.d/webupd8team-java.list
echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
apt-get update
apt-get -y install oracle-java8-installer
#apt-get install oracle-java8-set-default

# Git (requires password entry)
apt-get -y install git
mkdir /home/git
git clone ssh://git@109.109.239.50:2298/home/git/bookmarks /home/git/bookmarks

# Tomcat
wget -P /opt http://www.mirror.vorboss.net/apache/tomcat/tomcat-8/v8.0.33/bin/apache-tomcat-8.0.33.tar.gz
tar  -xzC /opt -f /opt/apache-tomcat-8.0.33.tar.gz
ln -s /opt/apache-tomcat-8.0.33 /opt/tomcat
rm -rf /opt/tomcat/webapps/*
cp /home/git/bookmarks/src/main/scripts/server.xml /opt/tomcat/conf/server.xml
cp /home/git/bookmarks/src/main/scripts/setenv.sh /opt/tomcat/bin/setenv.sh

# Maven
wget -P /opt ftp://mirror.reverse.net/pub/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
tar -xzC /opt -f /opt/apache-maven-3.3.9-bin.tar.gz
ln -s /opt/apache-maven-3.3.9 /opt/maven

# Mysql
apt-get -y install mysql-server

# Download bookmarks database.
mkdir -p /home/bookmarks/logs
DAY_OF_WEEK=$(date +"%a")
FILENAME=$DAY_OF_WEEK
wget -P /home/bookmarks ftp://u34059913-kevin:obama08elected@217.160.101.49/backup/bm.$FILENAME.sql.gpg

# Import bookmarks database
# THIS REQUIRES THE INFO@BOOKMARKSBOOKSHOP.CO.UK PRIVATE KEY TO BE INSTALLED.
# Find at dropbox
#gpg -d /home/bookmarks/bm.$FILENAME.sql.gpg > /home/bookmarks/bm.sql
#mysql -uroot -p < /home/bookmarks/bm.sql
#shred /home/bookmarks/bm.sql
#rm /home/bookmarks/bm.sql

# Build bookmarks
mkdir /home/bak
cp /home/git/bookmarks/src/main/resources/spring/application.dev.properties /home/git/bookmarks/src/main/resources/spring/application.prod.properties
sh /home/git/bookmarks/src/main/scripts/build.sh

# Tomcat systemd
cp /home/git/bookmarks/src/main/scripts/tomcat.service /lib/systemd/system/
systemctl enable tomcat

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
