#!/bin/bash

TOMCATVERSION=9.0.27

# Packages
apt -y update
apt -y dist-upgrade
apt -y install git vim-nox zip unzip

# Install sdkman with java and maven
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 11.0.5.hs-adpt
sdk use java 11.0.5.hs-adpt
sdk install maven

# git
mkdir /home/git
git clone https://github.com/jbeaken/festival.git /home/git/festival
git clone https://github.com/jbeaken/TUCPublications.git /home/git/bookmarks
git clone https://github.com/jbeaken/bmw.git /home/git/bmw

# Directories
mkdir -p /home/bookmarks/logs
mkdir /home/bookmarks/backups
mkdir /home/bookmarks/ssl
mkdir -p /home/sftponly/.ssh

# Gives access to application-prod.properties and init-mariadb.sql
gpg -d /home/git/bookmarks/src/main/etc/install/ubuntu/app.gpg > app.tar
tar xf app.tar
mv bmw-prod.properties /home/git/bmw/src/main/resources/application-prod.properties
mv festival-prod.properties /home/git/festival/src/main/resources/application-prod.properties
mv bookmarks-prod.properties /home/git/bookmarks/src/main/resources/spring/application-prod.properties
mv sftponly_id_rsa /home/sftponly/.ssh/id_rsa
mv sftponly_known_hosts /home/sftponly/.ssh/known_hosts



# Link build files
ln -s /home/git/festival/build.sh buildFestival.sh
ln -s /home/git/bookmarks/build.sh buildBookmarks.sh
ln -s /home/git/bmw/build.sh buildBmw.sh

# Tomcat
wget -P /tmp http://apache.mirror.anlx.net/tomcat/tomcat-9/v$TOMCATVERSION/bin/apache-tomcat-$TOMCATVERSION.tar.gz
tar xf /tmp/apache-tomcat-$TOMCATVERSION.tar.gz -C /opt
ln -s /opt/apache-tomcat-$TOMCATVERSION /opt/tomcat
rm -rf /opt/tomcat/webapps
mkdir -p /opt/tomcat/webapps/bmw
mkdir /opt/tomcat/webapps/bookmarks
mkdir /opt/tomcat/webapps/festival
cp -rf /home/git/bookmarks/src/main/etc/install/tomcat/Catalina /opt/apache-tomcat-$TOMCATVERSION/conf/Catalina
cp /home/git/bookmarks/src/main/etc/install/tomcat/server.xml /opt/tomcat/conf/
cp /home/git/bookmarks/src/main/etc/install/tomcat/setenv.sh /opt/tomcat/bin/

#apache
apt install -y apache2
# apt-get install -y libapr1-dev libssl-dev
apt-get install -y libapache2-mod-jk
#cp /home/git/bookmarks/src/main/etc/install/apache2/bookmarksbookshop.conf /etc/apache2/sites-available/
cp /home/git/bookmarks/src/main/etc/install/apache2/bookmarksonline-min.conf /etc/apache2/sites-available/
#cp /home/git/bookmarks/src/main/etc/install/apache2/marxismfestival.conf /etc/apache2/sites-available/

a2enmod ssl
#a2ensite marxismfestival
a2ensite bookmarksonline-min
#a2ensite bookmarksbookshop
a2dissite 000-default
cp /home/git/bookmarks/src/main/etc/install/apache2/workers.properties /etc/libapache2-mod-jk/

apt-get -y install software-properties-common
add-apt-repository -y universe
add-apt-repository -y ppa:certbot/certbot
apt-get -y update
apt-get -y install certbot python-certbot-apache
#certbot --apache
certbot certonly --webroot -d www.bookmarksonline.website -w /var/www/html
certbot certonly --webroot -d bookmarksonline.website -w /var/www/html
cp /home/git/bookmarks/src/main/etc/install/apache2/bookmarksonline.conf /etc/apache2/sites-available/
a2ensite bookmarksonline
a2dissite bookmarksonline-min
systemctl reload apache2
#ufw allow 'Apache Full'

# Build
sh buildFestival.sh
sh buildBmw.sh
sh buildBookmarks.sh

# Systemd
cp /home/git/bookmarks/src/main/etc/install/tomcat/tomcat.service /lib/systemd/system/
systemctl enable tomcat
systemctl start tomcat

# Mysql
apt -y install mysql-server
mysql -uroot festival < /home/git/bookmarks/src/main/etc/install/sql/festival.sql
mysql -uroot bmw < /home/git/bookmarks/src/main/etc/install/sql/bmw.sql
mysql -uroot bookmarks < /home/git/bookmarks/src/main/etc/install/sql/bookmarks.sql
mysql -uroot < init-mysql.sql
shred init-mysql.sql
rm init-mysql.sql

# Misc
git clone --depth 1 https://github.com/junegunn/fzf.git ~/.fzf
cd ~/.fzf/
./install
cd ~
