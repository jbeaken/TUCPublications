#!/bin/bash

NOW=$(date +"%F")

echo "Starting restore of database for $NOW"

mysql -uroot -padmin bookmarks < bm.sql

shred bm.sql

rm bm.sql

echo "Bookmarks database done!"



