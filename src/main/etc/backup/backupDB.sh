#!/bin/bash
. ./password

DAY_OF_WEEK=$(date +"%a")
DAY_OF_MONTH=$(date +"%d")
MONTH=$(date +"%b")

echo "Day of month : $DAY_OF_MONTH"
echo "Week day : $DAY_OF_WEEK"
echo "Month : $MONTH"
echo "Dumping database...."

if [ "$DAY_OF_MONTH" = "01" ] ; then
   FILENAME=$MONTH
else
   FILENAME=$DAY_OF_WEEK
fi
echo $FILENAME
/usr/bin/mysqldump  -u root -p$DB_PASSWORD bookmarks --add-drop-database --result-file=/home/bookmarks/bm.sql

echo "...finished! Uploading to backup server.."

# Encrypt
gpg --encrypt --recipient info@bookmarksbookshop.co.uk --trust-model always --output /home/bookmarks/bm.$FILENAME.sql.gpg /home/bookmarks/bm.sql

# Upload
wput -nc -u /home/bookmarks/bm.$FILENAME.sql.gpg ftp://$USERNAME:$PASSWORD@$HOSTNAME/bookmarks/bm.$FILENAME.sql.gpg

echo "Shredding documents..."
#shred /home/bookmarks/bm.sql
echo "...All shredded"
rm /home/bookmarks/bm.sql
rm /home/bookmarks/bm.$FILENAME.sql.gpg 
