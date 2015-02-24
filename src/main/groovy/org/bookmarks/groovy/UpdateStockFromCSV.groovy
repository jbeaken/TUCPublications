// Display Ledger Balances, customers, copy entire report button, 
/// paste into /home/bookmarks/accounts.csv,

package org.bookmarks.groovy
import groovy.sql.Sql;
import groovy.grape.Grape;

//Load the mysql driver
Grape.grab(group:'mysql', module:'mysql-connector-java', version:'5.1.31', classLoader: this.class.classLoader.rootLoader)
this.class.classLoader.getURLs().each{ 
  ClassLoader.systemClassLoader.addURL(it); 
}

def sql = Sql.newInstance("jdbc:mysql://localhost:3306/bookmarks", "root", "admin", "com.mysql.jdbc.Driver")

def count = 0



new File("/home/hal/stock_tab.csv").splitEachLine("\t") {fields ->

	def isbn = fields[0]
	def title = fields[1]      
	
	def amount1 = fields[4] ? Integer.parseInt(fields[4]) : 0
	def amount2 = fields[5] ? Integer.parseInt(fields[5]) : 0
	def total = amount1 + amount2
	println "${isbn} ${title} ${amount1} ${amount2} ${total}"

	def id = sql.firstRow("select id from stockitem where isbn = ?", [isbn])
	
	count = count + total
	
	
	if(!id) {
		println "Count : " + count
		System.exit(0)
	}
	
	println id[0]
	
	sql.executeUpdate('update stockitem set quantityInStock = quantityInStock + ? where id = ?', [total, id[0]])
}


