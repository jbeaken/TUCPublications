// From VT :
// Display Ledger Reports, customers, copy entire report button,
// Run this script


/*
Problems :
Customers: Ford, J Glyn								
045488		

*/
package org.bookmarks.groovy

import groovy.transform.ToString
import groovy.sql.Sql;
import groovy.grape.Grape;

//Load the mysql driver
Grape.grab(group:'mysql', module:'mysql-connector-java', version:'5.1.39', classLoader: this.class.classLoader.rootLoader)
this.class.classLoader.getURLs().each{
  ClassLoader.systemClassLoader.addURL(it);
}

def sql = Sql.newInstance("jdbc:mysql://localhost:3306/bookmarks", "root", "admin", "com.mysql.jdbc.Driver")

count = 0
Long customerId = 1
nextRowIsCustomer = false
customerMap = [:]
dateFormatter = new java.text.SimpleDateFormat("dd/MM/yy")

new File("/home/bookmarks/ledgers_club_account.txt").splitEachLine("\t") {fields ->

	def firstRow = fields[0]      //This will be customer id, of blank
	//println firstRow
	
	//Blank rows
	if(!firstRow) {
		//Terminates customer collection
		customerId = null
		return
	}


	if( firstRow ==~ /\d{5}/ ) {
		//have a customer
		println "Have customer ID : ${firstRow}"
		customerId = Long.valueOf(firstRow)
	}

	if( firstRow ==~ /\d{2}\/\d{2}\/\d{2}/ ) {
		if(!customerId)  {
			println "Have found a credit note without a customer being selected"
			return
		}
		//have a date
		//println "Date ${firstRow}"
		println fields
		Date date = dateFormatter.parse( firstRow )
		String reference = fields[1]
		String details = fields[4]

		Float balance = null
		Float liability = null
		Float asset = null
		
		if(fields[8]) balance = Float.valueOf( fields[8].replace(",", "") )
		if(fields[7]) liability = Float.valueOf( fields[7].replace(",", "") )
		if(fields[6]) asset = Float.valueOf( fields[6].replace(",", "") )

		CreditNote cn = new CreditNote(customerId : customerId, reference : reference, balance : balance, asset : asset, liability : liability, details : details, date : date, )
		println cn
	}

	count++

	//if(count > 20) System.exit(0)
 
} //end splitEachLine
println "Finished - Updated ${count} accounts"

@ToString(includeNames=true)
class CreditNote {
	Long customerId
	Date date
	String details
	String reference
	Float asset
	Float liability
	Float balance
}