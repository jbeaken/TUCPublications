// Display Ledger Balances, customers, copy entire report button, 
/// paste into /home/bookmarks/accounts.csv,

package org.bookmarks.groovy
import groovy.sql.Sql;
import groovy.grape.Grape;

//Load the mysql driver
Grape.grab(group:'mysql', module:'mysql-connector-java', version:'5.1.39', classLoader: this.class.classLoader.rootLoader)
this.class.classLoader.getURLs().each{ 
  ClassLoader.systemClassLoader.addURL(it); 
}

def sql = Sql.newInstance("jdbc:mysql://localhost:3306/bookmarks", "root", "admin", "com.mysql.jdbc.Driver")

String extras = new File("/home/hal/extras.txt").text
def isbns  = extras =~ /\d{13}/
sql.executeUpdate('update stockitem set is_on_extras = false')

isbns.each {
	println it
	sql.executeUpdate('update stockitem set is_on_extras = true where isbn = ?', [it])
}
//sql.executeUpdate('update stockitem set quantityInStock = quantityInStock + ? where id = ?', [total, id[0]])



