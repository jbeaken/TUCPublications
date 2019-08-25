// From VT :
// Display Ledger Balances, customers, copy entire report button,
/// paste into /home/bookmarks/accounts.csv with delimiter \t
// Run this script

package org.bookmarks.groovy

import groovy.sql.Sql;
import groovy.grape.Grape;

//Load the mysql driver
Grape.grab(group:'mysql', module:'mysql-connector-java', version:'5.1.39', classLoader: this.class.classLoader.rootLoader)
this.class.classLoader.getURLs().each{
  ClassLoader.systemClassLoader.addURL(it);
}
problems = []
emails = []

def sql = Sql.newInstance("jdbc:mysql://localhost:3306/puma", "root", "admin", "com.mysql.jdbc.Driver")

count = 0

new File("/home/pod/invite_judith.csv").splitEachLine(",") {fields ->
  def fullName = fields[0]
  if(!fullName) return //Some are blank
  def list =  fullName.tokenize(" ")
  if(list.size() == 1) {
    problems << list[0]
    return
  }

  println "Looking up ${list[0]} ${list[1]}"
  def email = sql.firstRow("select email from person p where p.firstname = ? and p.lastname = ? and p.is_member = true", [ list[0], list[1] ])
  if(!email || !email[0]) {
    problems << list
    return
  }
  println "Got ${email[0]}"
  emails << email[0]

}
println emails
println problems
