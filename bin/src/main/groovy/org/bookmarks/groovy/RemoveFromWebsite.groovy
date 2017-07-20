package org.bookmarks.groovy

import groovy.sql.Sql;
import groovy.grape.Grape;

//Load the mysql driver
Grape.grab(group:'mysql', module:'mysql-connector-java', version:'5.1.39', classLoader: this.class.classLoader.rootLoader)
this.class.classLoader.getURLs().each{ 
  ClassLoader.systemClassLoader.addURL(it); 
}

def sql = Sql.newInstance("jdbc:mysql://localhost:3306/bookmarks", "root", "admin", "com.mysql.jdbc.Driver")

if(this.args.size() < 1) {//Not enough parameters providedprintln this.args
	println 'Usage: RemoveFromWebsite isbnToRemove'
	System.exit(0);
}

def isbn = this.args[0]

println "Removing isbn ${isbn}"

sql.executeUpdate('update stockitem set put_on_website = false where isbn = :isbn', [isbn : isbn])

println "Finished"
