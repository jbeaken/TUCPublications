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

sql.executeUpdate('update customer set currentBalance = null, amountPaidInMonthly = null, paysInMonthly = false') //Reset all balances

new File("/home/bookmarks/pubclub.csv").splitEachLine("\t") {fields ->

//	def customerName = fields[0]
  def lastname = fields[0]      //This will be customer id, of blank
	def firstname = fields[1]      //This will be customer id, of blank
	def address = fields[2]
	def joined = fields[3]
  def booksCutoff = fields[4]

  if(lastname == 'Surname' || lastname == 'Curtis (2nd acct)') return

println "**"
  println lastname
  println firstname
  println address
  println "joined : ${joined}"
  println "booksCutoff : ${booksCutoff}"

  //def customer = sql.rows("select * from customer where firstname = ? and lastname = ? and sponsor = true", firstname, lastname)
  def customer = sql.rows("select * from customer where lastname = ? and sponsor = true", lastname)

if(!customer) {
  println "no match"
  if(!customer) System.exit(0);
}

if(customer.size() > 1) {
  println "too many results"
  customer.each {
    println it
  }
  if(!customer) System.exit(0);
}

  println customer

count++
  //if(count > 4) System.exit(2)
} //end splitEachLine
