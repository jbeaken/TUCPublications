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

def count = 0



new File("/srv/samba/share/redwords.csv").splitEachLine("\t") {fields ->
//new File("/home/till/Brain/bookmarks.csv").splitEachLine("\t") {fields ->
	def isbn = fields[0]
	def title = fields[1]      
	
	def amount1 = fields[3] ? Integer.parseInt(fields[3]) : 0
	def amount2 = fields[4] ? Integer.parseInt(fields[4]) : 0
	def amount3 = fields[5] ? Integer.parseInt(fields[5]) : 0
	def amount4 = fields[6] ? Integer.parseInt(fields[6]) : 0
	def amount5 = fields[7] ? Integer.parseInt(fields[7]) : 0
	def total = amount1 + amount2 + amount3 + amount4 + amount5

	println "${isbn} ${title} ${amount1} ${amount2} ${amount3} ${amount4} ${amount5} ${total}"

	def id = sql.firstRow("select id from stockitem where isbn = ?", [isbn])
	
	println id
	
	if(!id) {
		println "Failed isbn : ${isbn}"
		System.exit(0)
	}

	count = count + total
	
	sql.executeUpdate('update stockitem set quantityInStock = quantityInStock + ? where id = ?', [total, id[0]])
}

println "Have updated ${count} rows"


