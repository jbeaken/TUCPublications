#!/bin/bash
. /home/bookmarks/backup/password

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
/usr/bin/mysqldump  -u root -p$DB_PASSWORD bookmarks -B --result-file=/home/bookmarks/backup/bm.sql

echo "...finished! Uploading to backup server.."

# Encrypt
gpg --encrypt --batch --yes --recipient info@bookmarksbookshop.co.uk --trust-model always --output /home/bookmarks/backup/bm.$FILENAME.sql.gpg /home/bookmarks/backup/bm.sql

# Upload
wput -nc -u /home/bookmarks/backup/bm.$FILENAME.sql.gpg ftp://$USERNAME:$PASSWORD@$HOSTNAME/bookmarks/bm.$FILENAME.sql.gpg

echo "Shredding documents..."
shred /home/bookmarks/backup/bm.sql
echo "...All shredded"
rm /home/bookmarks/backup/bm.sql

