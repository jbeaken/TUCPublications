#!/bin/bash
if [ -z "$1" ]
  then
     echo "Enter day (Mon, Tue, Wed, Thu, Fri, Sat, Sun) or month (1, 2, 3.. 12)"
     exit 1
fi
echo Restoring backup with timestamp $1

#Load password
. /home/bookmarks/password

#download SQL
wget -O bm.sql.gpg ftp://$USERNAME:$PASSWORD@$HOSTNAME/bookmarks/bm.$1.sql.gpg

if [ $? -ne 0 ]
  then
        echo "Database file with timestamp $1 cannot be found, aborting!!"
       exit 1
fi


gpg -d bm.sql.gpg > bm.sql

#Restore database
echo "Restoring database...."
mysql -uroot -p$DB_PASSWORD bookmarks < bm.sql
echo "....All Done!"

#Shredding
echo "Shredding..."
#shred bm.sql

echo "Restore Complete"
