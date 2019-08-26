mvn clean package -f /home/git/bookmarks
cp /home/git/bookmarks/target/bookmarks.war files/tomcat/webapps/ROOT.war
tar cjf files.tar.bz2 files
packer build initial_ami.json
