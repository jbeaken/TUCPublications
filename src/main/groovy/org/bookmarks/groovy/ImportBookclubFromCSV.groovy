// From VT :
// Display Ledger Balances, customers, copy entire report button,
// Past into libre calc, save as csv, edit filters, delimiter tab
/// copy to  /home/bookmarks/accounts.csv
// Run this script

package org.bookmarks.groovy

import groovy.sql.Sql;
import groovy.grape.Grape;

//Load the mysql driver
Grape.grab(group:'mysql', module:'mysql-connector-java', version:'5.1.43', classLoader: this.class.classLoader.rootLoader)
this.class.classLoader.getURLs().each{
  ClassLoader.systemClassLoader.addURL(it);
}

def sql = Sql.newInstance("jdbc:mysql://localhost:3306/bookmarks", "root", "cyclops", "com.mysql.jdbc.Driver")

count = 0

new File("/home/bookmarks/pubclub.csv").splitEachLine("\t") {fields ->

//	def customerName = fields[0]
  def lastname = fields[0]      //This will be customer id, of blank
	def firstname = fields[1]      //This will be customer id, of blank
	def address = fields[2]
	def startDate = fields[3]
  def endDate = fields[4]
  def comment = fields[5]

  if(lastname == 'Surname' || lastname == 'Curtis (2nd acct)' || lastname == 'Stone (2ns acct)') return

println "**"
  println lastname
  println firstname
  println address
  println "startDate : ${startDate}"
  println "endDate : ${endDate}"
  println "comment : ${comment}"


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

if(comment) {
  comment = comment + "<br/>" + customer[0].sponsorshipComment
  } else {
    comment = customer[0].sponsorshipComment
  }

  println comment

  def sDate = Date.parse("dd/MM/yy", startDate)
  def eDate = Date.parse("dd/MM/yy", endDate)


  sql.executeUpdate("update customer set sponsorshipStartDate = ?, sponsorshipEndDate = ?, sponsorshipComment = ? where id = ?", sDate, eDate, comment, customer[0].id)

count++
  //if(count > 4) System.exit(2)
} //end splitEachLine
