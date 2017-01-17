package org.bookmarks.groovy

@Grapes(
    @Grab(group='com.opencsv', module='opencsv', version='3.8')
)

import groovy.sql.Sql;
import groovy.grape.Grape;
import com.opencsv.CSVReader


//Load the mysql driver
Grape.grab(group:'mysql', module:'mysql-connector-java', version:'5.1.39', classLoader: this.class.classLoader.rootLoader)
this.class.classLoader.getURLs().each{
  ClassLoader.systemClassLoader.addURL(it);
}

def sql = Sql.newInstance("jdbc:mysql://localhost:3306/bookmarks", "root", "cyclops", "com.mysql.jdbc.Driver")

count = 0

CSVReader reader = new CSVReader(new FileReader("/home/bookmarks/bank.csv"));

println reader



println "Finished - Updated ${count} accounts"
