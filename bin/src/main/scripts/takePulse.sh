result=$(curl -I http://socialistworker.co.uk | grep 200)
echo $result
if [ $var == 'HTTP/1.1 200' ]
then
cat - file.log << EOF | sendmail -t
to:jack747@gmail.com
from:beans@beans.com
subject:Testing 123
EOF
fi

