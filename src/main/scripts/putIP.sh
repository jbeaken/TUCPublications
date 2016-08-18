#!/bin/sh

#Load password
. ./password

# Get ip address
curl http://bot.whatismyipaddress.com > ip.txt

# Put on ftp server
wput -nc -u ip.txt ftp://u73194415-7wandX3:$PASSWORD@s468164439.websitehome.co.uk/bookmarks/ip.txt


