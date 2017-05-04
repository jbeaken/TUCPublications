# Make sure root
if [ "$(id -u)" = "0" ]; then
   echo "This script must not be run as root" 1>&2
   exit 1
fi

sudo apt-get install -y curl
curl -s get.sdkman.io | bash

source "$HOME/.sdkman/bin/sdkman-init.sh"

sdk install grails 2.5.6
