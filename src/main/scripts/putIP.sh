#!/bin/sh
curl http://bot.whatismyipaddress.com > ip.txt

echo "Placing ip address on ftp servers"

wput -nc -u ip.txt ftp://u73194415-7wandX3:$PASSWORD@s468164439.websitehome.co.uk/bookmarks/ip.txt


