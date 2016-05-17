# Install git
apt-get -y install git
mkdir /home/git
cd /home/git
git clone ssh://git@109.109.239.50:2298/home/git/bookmarks bookmarks

# Build (Maven)
wget www.mirrorservice.org/sites/ftp.apache.org/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
tar xf apache-maven-3.3.9-bin.tar.gz -C /opt
/opt/apache-maven-3.3.9/bin/mvn package

wget mirror.vorboss.net/apache/tomcat/tomcat-8/v8.0.33/bin/apache-tomcat-8.0.33.tar.gz


tar xf apache-tomcat-8.0.33.tar.gz -C /opt
tar xf apache-maven-3.3.9-bin.tar.gz -C /opt

rm /opt/apache-tomcat-8.0.33/webapps/*
