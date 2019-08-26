mvn clean package -f /home/git/bookmarks
cp /home/git/bookmarks/target/bookmarks.war stuff/tomcat/webapps/ROOT.war
tar cjf stuff.tar.bz2 stuff
packer build initial_ami.json
