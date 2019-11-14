rm app.gpg
tar cf app.tar -C /home/hal/dev/bookmarks password bmw-prod.properties bookmarks-prod.properties festival-prod.properties
gpg -c -o app.gpg app.tar
shred app.tar
rm app.tar
