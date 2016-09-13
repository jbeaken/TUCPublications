#!/bin/bash

# Make sure only root can run our script
if [ "$(id -u)" != "0" ]; then
   echo "This script must be run as root" 1>&2
   exit 1
fi

apt-add-repository ppa:fish-shell/release-2
apt-get update
apt-get install fish

chsh -s /usr/bin/fish
