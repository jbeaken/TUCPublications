# Accept license automatically
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections

# Install java 8
echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee /etc/apt/sources.list.d/webupd8team-java.list
echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
apt-get update
apt-get install oracle-java8-installer
#apt-get install oracle-java8-set-default

# Git (requires password entry)
apt-get install git
mkdir /home/git
git clone ssh://git@109.109.239.50:2298/home/git/bookmarks /home/git/bookmarks


# Tomcat
wget -P /opt http://www.mirrorservice.org/sites/ftp.apache.org/tomcat/tomcat-8/v8.0.23/bin/apache-tomcat-8.0.23.tar.gz
tar  -xzC /opt -f /opt/apache-tomcat-8.0.23.tar.gz
ln -s /opt/apache-tomcat-8.0.23 /opt/tomcat
rm -rf /opt/tomcat/webapps/*
cp /home/git/bookmarks/src/main/scripts/server.xml /opt/tomcat/conf/server.xml
cp /home/git/bookmarks/src/main/scripts/setenv.sh /opt/tomcat/bin/setenv.sh

# Maven
wget -P /opt ftp://mirror.reverse.net/pub/apache/maven/maven-3/3.3.3/binaries/apache-maven-3.3.3-bin.tar.gz
tar -xzC /opt -f /opt/apache-maven-3.3.3-bin.tar.gz
ln -s /opt/apache-maven-3.3.3 /opt/maven

# Mysql
apt-get install mysql-server

# Build bookmarks
cp /home/git/bookmarks/src/main/resources/spring/application.dev.properties /home/git/bookmarks/src/main/resources/spring/application.prod.properties
sh /home/git/bookmarks/src/main/scripts/build.sh

# Tomcat systemd
cp /home/git/bookmarks/src/main/scripts/tomcat.service /lib/systemd/system/
systemctl enable tomcat
