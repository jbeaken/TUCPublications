package org.bookmarks.groovy

import groovy.sql.Sql;
import groovy.grape.Grape;

//Load the mysql driver
Grape.grab(group:'mysql', module:'mysql-connector-java', version:'5.1.39', classLoader: this.class.classLoader.rootLoader)
this.class.classLoader.getURLs().each{ 
  ClassLoader.systemClassLoader.addURL(it); 
}

def sql = Sql.newInstance("jdbc:mysql://localhost:3306/bookmarks", "root", "admin", "com.mysql.jdbc.Driver")

if(this.args.size() < 2) {//Not enough parameters providedprintln this.args
	println 'Usage: TransferCustomer customerIdToKeep customerIdToDelete'
	System.exit(0);
}

def customer1Id = this.args[0]
def customer2Id = this.args[1]

def customer1 = sql.firstRow("select lastname, firstname from customer c where id = :id", [id : customer1Id])
def customer2 = sql.firstRow("select lastname, firstname from customer c where id = :id", [id : customer2Id])

if(customer1 == null) {
	println "Id ${customer1Id} doesn't exist"
	System.exit(0)
}
if(customer2 == null) {
	println "Id ${customer2Id} doesn't exist"
	System.exit(0)
}

def customer1Name = customer1.getAt(0) + " " + customer1.getAt(1)
def customer2Name = customer2.getAt(0) + " " + customer2.getAt(1)
println "Transfering from ${customer2Id} ${customer2Name} to ${customer1Id} ${customer1Name}"

if(customer1.getAt(1).toLowerCase() != customer2.getAt(1).toLowerCase()) {
	println "Customer names don't match"
	System.exit(0);
}

sql.executeUpdate('update customer_customerorderline set customer_id = :customer1Id where customer_id = :customer2Id', [customer1Id : customer1Id, customer2Id : customer2Id])
sql.executeUpdate('update customerorderline set customer_id = :customer1Id where customer_id = :customer2Id', [customer1Id : customer1Id, customer2Id : customer2Id])
sql.executeUpdate('update invoice set customer_id = :customer1Id where customer_id = :customer2Id', [customer1Id : customer1Id, customer2Id : customer2Id])
sql.executeUpdate('update SaleOrReturn set customer_id = :customer1Id where customer_id = :customer2Id', [customer1Id : customer1Id, customer2Id : customer2Id])
sql.executeUpdate('delete from customer where id = :customer2Id', [customer2Id : customer2Id])

println "Finished"
