#!/bin/bash

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
/usr/bin/mysqldump  -u root -padmin bookmarks --result-file=/home/bookmarks/bm.sql

echo "...finished! Uploading to backup server.."

#tar cfj bm.sql.tar.bz2 bm.sql
gpg --recipient info@bookmarksbookshop.co.uk -e --output /home/bookmarks/bm.$FILENAME.sql.gpg /home/bookmarks/bm.sql
wput -nc -u /home/bookmarks/bm.$FILENAME.sql.gpg ftp://u34059913-kevin:obama08elected@217.160.101.49/backup/bm.$FILENAME.sql.gpg
#shred /home/bookmarks/bm.sql
#rm /home/bookmarks/bm.sql
#rm /home/bookmarks/bm.$FILENAME.sql.gpg

echo "...All done!"

