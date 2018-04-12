JAVA_HOME=/usr/lib/jvm/java-8-oracle
TOMCAT_NATIVE_VERSION=1.2.16

sudo apt-get install -y libapr1-dev libssl-dev build-essential

wget www.mirrorservice.org/sites/ftp.apache.org/tomcat/tomcat-connectors/native/$TOMCAT_NATIVE_VERSION/source/tomcat-native-$TOMCAT_NATIVE_VERSION-src.tar.gz

tar xzf tomcat-native-$TOMCAT_NATIVE_VERSION-src.tar.gz

cd tomcat-native-$TOMCAT_NATIVE_VERSION-src/native

./configure --with-java-home=$JAVA_HOME

make 
sudo make install



