package org.bookmarks.groovy;
import groovy.sql.Sql;
import groovy.grape.Grape;

//Load the mysql driver
Grape.grab(group:'mysql', module:'mysql-connector-java', version:'5.1.39', classLoader: this.class.classLoader.rootLoader)
this.class.classLoader.getURLs().each{ 
  ClassLoader.systemClassLoader.addURL(it); 
}

def sql = Sql.newInstance("jdbc:mysql://localhost:3306/bookmarks",	"root", "admin", "com.mysql.jdbc.Driver")

//Reset img_filename
sql.executeUpdate('update bookmarks.stockitem set img_filename = null')
	
//Image directory
def dir = new File("/home/bookmarks/images/original")

def count = 0
//Get all files (jgps or gigs)
dir.eachFileRecurse { file ->
	def imageFilename = file.path.substring(28) 
	println "${count++} ${imageFilename}"
	def isbn = imageFilename.substring(0, 13)
	
	//Could check for existence first, and delete jpg if it doesn't exist
	//or use check put_on_website
	sql.executeUpdate('update bookmarks.stockitem set img_filename = :imageFilename where isbn = :isbn', 
		[imageFilename : imageFilename, isbn : isbn])
}


