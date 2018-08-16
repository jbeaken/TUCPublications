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

#!/bin/bash
tar czf /home/bookmarks/backup/brain.tar.bz2 -C /srv/samba/share WENDY Senan Jack Finance Eileen DaveG

# Encrypt
gpg --encrypt --yes --recipient info@bookmarksbookshop.co.uk --trust-model always --output /home/bookmarks/backup/brain.$FILENAME.gpg /home/bookmarks/backup/brain.tar.bz2

# Upload
wput -nc -u /home/bookmarks/backup/brain.$FILENAME.gpg ftp://$USERNAME:$PASSWORD@$HOSTNAME/bookmarks/brain.$FILENAME.gpg

echo "Shredding documents..."
shred /home/bookmarks/backup/brain.tar.bz2

echo "...All shredded"
rm /home/bookmarks/backup/brain.tar.bz2
# rm /home/bookmarks/backup/brain.$FILENAME.gpg
