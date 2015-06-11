# Accept license automatically
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections

# Install java 8
echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee /etc/apt/sources.list.d/webupd8team-java.list
echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
apt-get update
apt-get install oracle-java8-installer
apt-get install oracle-java8-set-default

# Tomcat
wget -P /opt http://www.mirrorservice.org/sites/ftp.apache.org/tomcat/tomcat-8/v8.0.23/bin/apache-tomcat-8.0.23.tar.gz
tar  -xzC /opt -f /opt/apache-tomcat-8.0.23.tar.gz
ln -s /opt/apache-tomcat-7.0.53 /opt/tomcat

# Maven
wget -P /opt ftp://mirror.reverse.net/pub/apache/maven/maven-3/3.3.3/binaries/apache-maven-3.3.3-bin.tar.gz
tar  -xzC /opt -f /opt/apache-maven-3.3.3-bin.tar.gz
ln -s /opt/apache-maven-3.2.1 /opt/maven

# Git (requires password entry)
apt-get install git
mkdir /home/git
git clone ssh://git@109.109.239.50:2298/home/git/bookmarks /home/git/bookmarks

# Mysql
apt-get install mysql-server
