#Load password
. ./password

echo "Reseting mini bean..."

# Reset mini
mysql -uroot -p$DB_PASSWORD bookmarks < /home/git/bookmarks/src/main/scripts/sql/resetMini.sql
echo "....All Done!"
