#!/bin/bash

apt-get -y install git
mkdir /home/git
git clone ssh://git@109.109.239.50:2298/home/git/bookmarks /home/git/bookmarks

# This will give you install script at /home/git/bookmarks/src/main/etc/install.sh 
# which will install java, tomcat, maven etc

# Build script at /home/git/bookmarks/src/main/etc/build.sh

