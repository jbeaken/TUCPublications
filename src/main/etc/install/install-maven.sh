#! /usr/bin/bash
MAVENVERSION=3.5.2
echo $MAVENVERSION
wget apache.mirror.anlx.net/maven/maven-3/$MAVENVERSION/binaries/apache-maven-$MAVENVERSION-bin.tar.gz

sudo tar xf apache-maven-$MAVENVERSION-bin.tar.gz -C /opt

rm apache-maven-$MAVENVERSION-bin.tar.gz

sudo rm /opt/maven

sudo ln -s /opt/apache-maven-$MAVENVERSION /opt/maven

