#!/bin/bash
echo "Reseting beans database"
. ./password
SCRIPT_PATH=/home/svn/bookmarks
mysql -uroot -p$DB_PASSWORD bookmarks < $SCRIPT_PATH/src/main/scripts/sql/resetMini.sql
echo "All done!"
 

