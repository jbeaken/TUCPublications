#!/bin/bash

DAY_OF_WEEK=$(date +"%a")
DAY_OF_MONTH=$(date +"%d")
MONTH=$(date +"%b")

echo "Day of month : $DAY_OF_MONTH"
echo "Week day : $DAY_OF_WEEK"
echo "Month : $MONTH"


FILENAME=$DAY_OF_WEEK
echo $FILENAME

echo "Dumping tomcat server......"
sudo tar chjf /home/bookmarks/tomcat.tar.bz2 --exclude=logs/* --exclude=*.war --exclude=work/* --exclude=temp/* --exclude=work/* -C /opt tomcat


gpg --batch --yes --recipient info@bookmarksbookshop.co.uk -e --output /home/bookmarks/bm.$FILENAME.tomcat.gpg /home/bookmarks/tomcat.tar.bz2

wput -nc -u /home/bookmarks/bm.$FILENAME.tomcat.gpg ftp://u34059913-kevin:obama08elected@217.160.101.49/backup/bm.$FILENAME.tomcat.gpg

echo "..... All Finished!"

