#!/bin/bash
if [ -z "$1" ]
  then
     echo "Enter day (Mon, Tue, Wed, Thu, Fri, Sat, Sun) or month (1, 2, 3.. 12)"
     exit 1
fi
echo Restoring backup with timestamp $1

#Load password
. ./password

#download SQL
wget -O bm.sql.gpg ftp://u73194415-7wandX3:$PASSWORD@s468164439.websitehome.co.uk/bookmarks/bm.$1.sql.gpg

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

#Restore images
echo "Restoring files....."
tar -xjC /home -f paper.files.tar.bz2
echo "....All Done!"

#Reset passwords
echo "Reseting paswords"
mysql -uroot -p$DB_PASSWORD paper < /home/git/paper/scripts/sql/resetPassword.sql
echo "....All Done!"

echo "Shredding..."
shred bm.sql

echo "Restore Complete"
