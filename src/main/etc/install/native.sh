JAVA_HOME=/usr/lib/jvm/java-8-oracle

apt-get install -y libapr1-dev libssl-dev

wget mirrors.muzzy.org.uk/apache/tomcat/tomcat-connectors/native/1.2.12/source/tomcat-native-1.2.23-src.tar.gz

tar xzf tomcat-native-1.2.12-src.tar.gz -C /usr/local/share

cd /usr/local/share/tomcat-native-1.2.12-src/native

./configure --with-java-home=$JAVA_HOME

make && make install
