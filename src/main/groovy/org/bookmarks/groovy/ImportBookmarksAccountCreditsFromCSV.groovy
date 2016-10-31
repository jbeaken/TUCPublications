// From VT :
// Display Ledger Balances, customers, copy entire report button,
// Past into libre calc, save as csv, edit filters, delimiter tab
/// copy to  /home/bookmarks/accounts.csv
// Run this script

package org.bookmarks.groovy

import groovy.sql.Sql;
import groovy.grape.Grape;

//Load the mysql driver
Grape.grab(group:'mysql', module:'mysql-connector-java', version:'5.1.39', classLoader: this.class.classLoader.rootLoader)
this.class.classLoader.getURLs().each{
  ClassLoader.systemClassLoader.addURL(it);
}

def sql = Sql.newInstance("jdbc:mysql://localhost:3306/bookmarks", "root", "cyclops", "com.mysql.jdbc.Driver")

count = 0

new File("/home/bookmarks/bank.csv").splitEachLine(",") {fields ->

//	def customerName = fields[0]
  def field1 = fields[0]      //This will be customer id, of blank
	def field2 = fields[1]      //This will be customer id, of blank
	def field3 = fields[2]
	def field4 = fields[3]
	def field5 = fields[4]
  def field6 = fields[5]


println field1
    println field5
} //end splitEachLine
//sql.executeUpdate('update customer set paysInMonthly = true where amountPaidInMonthly > 0')
println "Finished - Updated ${count} accounts"
