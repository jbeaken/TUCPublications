// From VT :
// Run this before importbookclubFromCSV

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

sql.executeUpdate('update customer set sponsorshipAmount = null, sponsorshipComment = null, sponsorshipStartDate = null, sponsorshipEndDate = null') //Reset 

Set set = []

new File("/home/bookmarks/bookclub.csv").splitEachLine("\t") {fields ->

	def customerName = fields[0]
  if(!customerName) return
  int indexOfComma = customerName.indexOf( "," )
  if(indexOfComma == -1) return
  def firstname = customerName.substring(indexOfComma + 2)
	def lastname = customerName.substring(0, indexOfComma)
	def amount = fields[2]
  def comment = fields[3]

  if(lastname == 'Surname' || lastname == 'Curtis (2nd acct)') return

println "*****************"
  println ":" + lastname + ":"
  println ":" + firstname + ":"
  println fields[1]
  println "amount : ${amount}"
  println "comment : ${comment}"

  set << amount

  //def customer = sql.rows("select * from customer where firstname = ? and lastname = ? and sponsor = true", firstname, lastname)
  def customer = sql.rows("select * from customer where lastname = ? and sponsor = true", lastname)

if(!customer) {
  println "no match"
 }

if(customer.size() > 1) {
  println "too many results"
  customer = sql.rows("select * from customer where lastname = ? and firstname = ? and sponsor = true", lastname, firstname)
  if(!customer) System.exit(0);
  customer.each {
    println it
  }

  if(customer.size() > 1) {
     println "too many results"
     System.exit(0);
   }
}

  

   def dAmount = Double.parseDouble( amount.replace(",", "") ) * -1

 if(comment) comment = comment.trim()

  sql.executeUpdate("update customer set sponsorshipAmount = ?, sponsorshipComment = ? where id = ?", dAmount, comment, customer[0].id)
  //sql.executeUpdate("update customer set sponsorshipAmount = ?, sponsorshipComment = sponsorshipComment + ? where id = ?", dAmount , comment, customer.id)

count++
  //if(count > 4) System.exit(2)
} //end splitEachLine
sql.executeUpdate("update customer set sponsor = null where sponsorshipAmount is null")
println set
