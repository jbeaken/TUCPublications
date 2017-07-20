#!/bin/sh
curl http://bot.whatismyipaddress.com > ip.txt

. ./password

echo "Placing ip address on ftp servers"

wput -nc -u ip.txt ftp://$USERNAME:$PASSWORD@$HOSTNAME/bookmarks/ip.txt


