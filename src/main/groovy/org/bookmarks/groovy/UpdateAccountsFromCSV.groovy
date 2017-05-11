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

def sql = Sql.newInstance("jdbc:mysql://localhost:3306/bookmarks", "root", "admin", "com.mysql.jdbc.Driver")

count = 0

sql.executeUpdate('update customer set currentBalance = 0, amountPaidInMonthly = 0, paysInMonthly = false') //Reset all balances

new File("/home/bookmarks/accounts.csv").splitEachLine("\t") {fields ->

//	def customerName = fields[0]
  def customerName = fields[0]      //This will be customer id, of blank
	def customerId = fields[1]      //This will be customer id, of blank
	def currentBalance = fields[2]
	def comment = fields[3]
	def commentAsFloat = 0
	def monthlyMatcher  = (comment =~ /\d{1,2}\smonthly/)

  if(!customerId) {println "Invalid id ${customerId}!";return} //Skip nonsense row
	if(!currentBalance) {println "Invalid current balance ${currentBalance}!";return} //Skip nonsense row
  if(customerName == 'Name') {println "Header row, skipping";return}//Skip certian header fields
	println "*****************************************"
	println "customerName ${customerName}, customerID : ${customerId},  currentBalance ${currentBalance}"

	currentBalance = currentBalance.replace("\"","")
	currentBalance = currentBalance.replace(",","")

	def currentBalanceAsFloat = Float.parseFloat(currentBalance) * -1

	if(monthlyMatcher)	{
		//println "Have regex match " + monthlyMatcher[0]
    def amount = monthlyMatcher[0].replace(" monthly", "")
		commentAsFloat = Float.parseFloat( amount )
	}


	println fields
//	println "customerName : " + customerName
	println "customerID : " + customerId
	println "currentBalance : " + currentBalance
	println "comment : " + comment
	println "commentAsFloat : " + commentAsFloat
	println "currentBalanceAsFloat : " + currentBalanceAsFloat

	sql.executeUpdate('update customer set currentBalance = ?, amountPaidInMonthly = ?, comment = ? where id=?', [(currentBalanceAsFloat), commentAsFloat, comment, customerId])
	count++
  //if(count > 4) System.exit(2)
} //end splitEachLine
sql.executeUpdate('update customer set paysInMonthly = true where amountPaidInMonthly > 0')
println "Finished - Updated ${count} accounts"
