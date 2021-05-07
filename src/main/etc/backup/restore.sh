#!/bin/bash
if [ -z "$1" ]
  then
     echo "Enter day (Mon, Tue, Wed, Thu, Fri, Sat, Sun) or month (1, 2, 3.. 12)"
     exit 1
fi
echo Restoring backup with timestamp $1

#Load password
. ./password

DAY_OF_MONTH=$(date +"%d")
MONTH=$(date +"%b")
YEAR=$(date +'%Y')
FULL_DATE=$(date +'%F')

#download SQL
sshpass -e sftp -oBatchMode=no -b - $UN@$HOSTNAME << !
   get /bookmarks/bm.$1.sql.gpg bm.sql.$FULL_DATE.gpg
   bye
!

if [ $? -ne 0 ]
  then
      echo "Database file with timestamp $1 cannot be found, aborting!!"
      exit 1
fi

echo "Decrypting  database...."

gpg -d bm.sql.$FULL_DATE.gpg > bm.sql

if [ $? -ne 0 ]
  then
      echo "Error in encyption, aborting!!"
      exit 1
fi

sudo mysql  < bm.sql

echo "....All Done!"
