#! /usr/bin/bash

# Static ip
/etc/network/interfaces
The primary network interface
allow-hotplug eth0
iface eth0 inet static
address 192.168.1.8
netmask 255.255.255.0
gateway 192.168.1.1
dns-domain bookmarks
dns-nameservers 192.168.1.1

# File server
apt-get install samba cifs-utils
#Add to fstab
UUID=144E3AED4E3AC770 /srv/samba/share ntfs guest 0 0

sudo adduser trout
sudo smbpassrd -a trout
# Add to /etc/samba/smb.conf :
# [share]
#    comment = BRAIN File Server Share
#    security = user
#    path = /srv/samba/share
#    valid users = trout
#    guest ok = no
#    read only = no

# On client machine
sudo apt install cifs-utils
# Add to /etc/fstab
//192.168.1.8/brain /home/beans/brain cifs credentials=.smbcredentials 0 0
