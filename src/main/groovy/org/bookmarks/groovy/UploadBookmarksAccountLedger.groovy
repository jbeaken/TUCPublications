// From VT :
// Display Ledger Reports, customers, copy entire report button,
// Run this script


/*
JRN 000001 appears to always be an opening account

Problems :
Customers: Ford, J Glyn								
045488		

What is D?
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

def sql = Sql.newInstance("jdbc:mysql://localhost:3306/bookmarks", "root", "", "com.mysql.jdbc.Driver")

count = 0
Long customerId = 1
nextRowIsCustomer = false
creditNotes = []
dateFormatter = new java.text.SimpleDateFormat("dd/MM/yy")
Customer customer = null
customers = []

new File("/home/bookmarks/ledgers_club_account.txt").splitEachLine("\t") {fields ->

	def firstRow = fields[0]      //This will be customer id, of blank
	
	//Blank rows
	if(!firstRow) {
		return
	}

	

	if( firstRow ==~ /\d{5}/ ) {
		//have a customer

		
		if(customer && !customer.currentBalance)  {
			throw new Exception("Have found customer ${firstRow}, but already have a customer ${customer.id} in progress!")
			return
		}		
		
		if(customer) customers.add( customer )

		customer = new Customer(id : Long.valueOf(firstRow))
		return
	}

	if(firstRow.startsWith("Balance at")) {
		println "balance at " + fields

		customer.currentBalance = Float.valueOf( fields[8].replace(",", "") ) * -1
	}	

	if( firstRow ==~ /\d{2}\/\d{2}\/\d{2}/ ) {
		if(!customer)  {
			//println "Have found a credit note without a customer being selected"
			return
		}
		//have a date
		//println "Date ${firstRow}"
		//println fields


		Date date = dateFormatter.parse( firstRow )
		String reference = fields[1]
		String details = fields[4]

		Float balance = null
		Float liability = null
		Float asset = null

		if(fields[8]) balance = Float.valueOf( fields[8].replace(",", "") )
		if(fields[7]) liability = Float.valueOf( fields[7].replace(",", "") )
		if(fields[6]) asset = Float.valueOf( fields[6].replace(",", "") )		

		if(reference == "JRN 000001") {
				//have an opening balance
				//println "JRN" + balance
				//println asset
				//println liability
			customer.openingBalance = liability
			return
		}

				
		
		CreditNote cn = new CreditNote(reference : reference, balance : balance, asset : asset, liability : liability, details : details, date : date, )
		//println cn
		customer.creditNotes.add( cn )
	}

	count++

	//if(count > 20) System.exit(0)
 
} //end splitEachLine
println "Finished - Updated ${count} credit notes"
//println customers

sql.executeUpdate("delete from CreditNote")

//Persist
customers.each {c -> 
	//if(c.id != 45671) return
	println c.id
	//println c.currentBalance
	
	if(!customer.currentBalance) throw new Exception("Have customer with no current balance")

	sql.executeUpdate("update customer set openingBalance = ?, currentBalance = ? where id = ?", [c.openingBalance, c.currentBalance, c.id])


	c.creditNotes.each {cn ->
		//println cn
		sql.executeInsert("insert into CreditNote (creationDate, customer_id, date, transactionReference, transactionDescription, amount) values (now(), ?, ?, ?, ?, ?)",
				[c.id, cn.date, cn.reference, cn.details, cn.amount])
	}
}

@ToString(includeNames=true)
class Customer {
	Long id
	Float openingBalance = 0.0
	Float currentBalance 
	def creditNotes = []
}

@ToString(includeNames=true)
class CreditNote {
	Date date
	String details
	String reference
	Float asset
	Float liability
	Float balance

	Float getAmount() {
		if(asset || asset == 0) return asset * -1
		return liability;
	}
}

/*
@ToString(includeNames=true)
class CreditNote {

	BigDecimal amount;

	Date date;

	TransactionType transactionType;

	String transactionReference;

	String transactionDescription;
}
*/