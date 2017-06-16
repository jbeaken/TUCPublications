#!/bin/sh

#Load password
. ./password

wget -O ip.txt ftp://$USERNAME:$PASSWORD@$HOSTNAME/bookmarks/ip.txt


