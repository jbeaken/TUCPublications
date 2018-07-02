#! /usr/bin/bash

# Must not be root
if [ "$(id -u)" -eq "0" ]; then
   echo "This script must not be run as root" 1>&2
   exit 1
fi

JAVA_HOME=/usr/lib/jvm/java-8-oracle
TOMCATVERSION=9.0.7
TOMCAT_NATIVE_VERSION=1.2.16

wget apache.mirror.anlx.net/tomcat/tomcat-9/v$TOMCATVERSION/bin/apache-tomcat-$TOMCATVERSION.tar.gz

sudo tar xf apache-tomcat-$TOMCATVERSION.tar.gz -C /opt

sudo ln -s /opt/apache-tomcat-$TOMCATVERSION /opt/tomcat

sudo rm -rf /opt/tomcat/webapps/*
