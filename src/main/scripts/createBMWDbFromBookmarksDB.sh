mysql -uroot -padmin bmw < /home/git/bookmarks/src/main/scripts/sql/bmw_structure.sql
mysql -uroot -padmin bmw < /home/git/bookmarks/src/main/scripts/sql/populate_db.sql
mysqldump -uroot -padmin bmw > live.sql
tar cjf live.tar.bz2 live.sql
rm live.sql
 

