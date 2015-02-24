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
//def customer = sql.dataSet("Customer")
count = 0

sql.executeUpdate('update customer set currentBalance = 0, amountPaidInMonthly = 0, paysInMonthly = false') //Reset all balances

new File("/home/bookmarks/accounts.csv").splitEachLine("\t") {fields ->
	def customerName = fields[0]
	def customerId = fields[1]      //This will be customer id, of blank
	def currentBalance = fields[2]
	def monthlyPayment = fields[3]
	def monthlyPaymentAsFloat = 0
	def monthlyMatcher  = (monthlyPayment =~ /£\d{1,2}/)
	
	println customerName + " " + customerId + " " + currentBalance + " " + currentBalance
	
	if(customerName == 'Name') return  //Skip header row
	if(!customerId) {println "No!";return} //Skip nonscense row
	
	if(currentBalance && customerId) {
		currentBalance = currentBalance.replace("\"","")
		currentBalance = currentBalance.replace(",","")


		def currentBalanceAsFloat = Float.parseFloat(currentBalance) * -1
		
		if(monthlyMatcher)	{
			//println "Have regex match " + monthlyMatcher[0]
			monthlyPaymentAsFloat = Float.parseFloat(monthlyMatcher[0].replace('£', ''))
		}	
		
		println "*****************************************"
		println "customerName : " + customerName
		println "customerID : " + customerId
		println "currentBalance : " + currentBalance
		println "monthlyPayment : " + monthlyPayment
		println "monthlyPaymentAsFloat : " + monthlyPaymentAsFloat			    
		println "currentBalanceAsFloat : " + currentBalanceAsFloat
		
		sql.executeUpdate('update customer set currentBalance = ?, amountPaidInMonthly = ? where id=?', [(currentBalanceAsFloat), monthlyPaymentAsFloat, customerId])
		count++
	}
}
sql.executeUpdate('update customer set paysInMonthly = true where amountPaidInMonthly > 0')
println "Finished - Updated ${count} accounts"
