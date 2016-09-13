# Make sure root
if [ "$(id -u)" != "0" ]; then
   echo "This script must be run as root" 1>&2
   exit 1
fi

# Install Java, accept license automatically
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections

# Install java 8
echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee /etc/apt/sources.list.d/webupd8team-java.list
echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
apt-get update
apt-get -y install oracle-java8-installer
apt-get install oracle-java8-set-default

# Git (requires password entry)
apt-get -y install git
mkdir /home/git
git clone ssh://git@109.109.239.50:2298/home/git/bookmarks /home/git/bookmarks

# Directories
mkdir /home/bak
mkdir -p /home/bookmarks/logs

# Maven
wget ftp://mirror.reverse.net/pub/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
tar -xf apache-maven-3.3.9-bin.tar.gz -C /usr/local/share
ln -s /usr/local/share/apache-maven-3.3.9 /usr/local/share/maven

# Mysql
apt-get -y install mysql-server

# Tomcat
wget mirror.vorboss.net/apache/tomcat/tomcat-8/v8.5.5/bin/apache-tomcat-8.5.5.tar.gz
tar xf apache-tomcat-8.5.5.tar.gz -C /usr/local/share
ln -s /usr/local/share/apache-tomcat-8.5.5 /usr/local/share/tomcat
rm -rf /usr/local/share/tomcat/webapps/*
cp /home/git/bookmarks/src/main/etc/conf/server.xml /usr/local/share/tomcat/conf/server.xml
cp /home/git/bookmarks/src/main/etc/conf/setenv-dev.sh /usr/local/share/tomcat/bin/setenv.sh
useradd -u 220 -r -s /bin/false tomcat
chown tomcat:tomcat -R /usr/local/share/apache-tomcat-8.5.5

# Tomcat systemd
cp /home/git/bookmarks/src/main/etc/install/tomcat.service /lib/systemd/system/
systemctl enable tomcat

# Write to path
cp /etc/environment /etc/environment.bak
echo "PATH=$PATH:/usr/local/share/maven/bin" >> /etc/environment
. /etc/environment

# Build bookmarks
cd /home/git/bookmarks
ln -s /home/git/bookmarks/src/main/build/build.sh /home/git/bookmarks/build.sh
sh build.sh

# DAY_OF_WEEK=$(date +"%a")
# FILENAME=$DAY_OF_WEEK
# wget -P /home/bookmarks ftp://u34059913-kevin:obama08elected@217.160.101.49/backup/bm.$FILENAME.sql.gpg

# Import bookmarks database
# THIS REQUIRES THE INFO@BOOKMARKSBOOKSHOP.CO.UK PRIVATE KEY TO BE INSTALLED.
# Find at dropbox
#gpg -d /home/bookmarks/bm.$FILENAME.sql.gpg > /home/bookmarks/bm.sql
#mysql -uroot -p < /home/bookmarks/bm.sql
#shred /home/bookmarks/bm.sql
#rm /home/bookmarks/bm.sql

# Build bookmarks
# mkdir /home/bak
#cp /home/git/bookmarks/src/main/resources/spring/application.dev.properties /home/git/bookmarks/src/main/resources/spring/application.prod.properties
# sh /home/git/bookmarks/src/main/scripts/build.sh

# Tomcat systemd
# cp /home/git/bookmarks/src/main/scripts/tomcat.service /lib/systemd/system/
# systemctl enable tomcat

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
