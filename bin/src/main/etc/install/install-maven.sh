VERSION=3.5.0

wget mirrors.ukfast.co.uk/sites/ftp.apache.org/maven/maven-3/$VERSION/binaries/apache-maven-3.5.0-bin.tar.gz
sudo tar xf apache-maven-$VERSION-bin.tar.gz -C /opt
rm apache-maven-$VERSION-bin.tar.gz
sudo ln -s /opt/apache-maven-$VERSION /opt/maven

