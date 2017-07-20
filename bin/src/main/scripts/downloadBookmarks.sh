#!/bin/bash

NOW=$(date +"%F")

echo "Starting Download for $1"
. ./password

wget ftp://u34059913-kevin:$PASSWORD@ftp.bookmarksbookshop.co.uk/backup/bm.$1.sql.gpg

gpg -d bm.$1.sql.gpg > bm.sql
echo "All done!"
