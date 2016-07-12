
package org.bookmarks.groovy

import groovy.sql.Sql;
import groovy.grape.Grape;

//Load the mysql driver
Grape.grab(group:'mysql', module:'mysql-connector-java', version:'5.1.34', classLoader: this.class.classLoader.rootLoader)
this.class.classLoader.getURLs().each{
  ClassLoader.systemClassLoader.addURL(it);
}

def sql = Sql.newInstance("jdbc:mysql://localhost:3306/bookmarks", "root", "admin", "com.mysql.jdbc.Driver")

count = 0

def isbns = sql.rows('select isbn from stockitem where img_url is not null')



println "found ${isbns.size()} img urls"

def missing = []
isbns.each {
  File f = new File("/home/bookmarks/images/original${it}.jpg")
  if(f.exists()) return
  missing << it
}

println missing
