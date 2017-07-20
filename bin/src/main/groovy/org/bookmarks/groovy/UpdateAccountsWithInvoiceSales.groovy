// From Beans :
// Sales Reports - invoice list - export as csv
/// copy to  /home/bookmarks/invoices.csv

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

new File("/home/bookmarks/invoices.csv").splitEachLine(",") {fields ->

  def customerId = fields[0]
  def customerName = fields[1]
	def amount = fields[2]

  if(!customerId) {println "Invalid id ${customerId}!";return} //Skip nonsense row
	if(!amount) {println "Invalid current balance ${currentBalance}!";return} //Skip nonsense row

	println "*****************************************"
	println "customerName ${customerName}, customerID : ${customerId},  amount ${amount}"

	sql.executeUpdate('update customer set currentBalance = currentBalance - ? where id=?', [amount, customerId])
	count++

} //end splitEachLine

println "Finished - Updated ${count} accounts"
