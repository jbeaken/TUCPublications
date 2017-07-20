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
# JCE
cp /home/git/bookmarks/src/main/scripts/UnlimitedJCEPolicy/*.jar /usr/lib/jvm/java-8-oracle/jre/lib/security
# Maven
wget ftp://mirror.reverse.net/pub/apache/maven/maven-3/3.5.0/binaries/apache-maven-3.5.0-bin.tar.gz
tar -xf apache-maven-3.5.0-bin.tar.gz -C /opt
ln -s /opt/apache-maven-3.5.0 /opt/maven

# Mysql
apt-get -y install mysql-server

# Tomcat
wget mirror.vorboss.net/apache/tomcat/tomcat-8/v8.5.14/bin/apache-tomcat-8.5.14.tar.gz
tar xf apache-tomcat-8.5.14.tar.gz -C /opt
ln -s /opt/apache-tomcat-8.5.14 /opt/tomcat
rm -rf /opt/tomcat/webapps/*
cp /home/git/bookmarks/src/main/etc/conf/server.xml /opt/tomcat/conf/server.xml
cp /home/git/bookmarks/src/main/etc/conf/setenv-dev.sh /opt/tomcat/bin/setenv.sh
useradd -u 220 -r -s /bin/false tomcat
chown tomcat:tomcat -R /opt/apache-tomcat-8.5.14

# Tomcat systemd
cp /home/git/bookmarks/src/main/etc/install/tomcat.service /lib/systemd/system/
systemctl enable tomcat

# Write to path
cp /etc/environment /etc/environment.bak
echo "PATH=$PATH:/opt/maven/bin" >> /etc/environment
. /etc/environment
