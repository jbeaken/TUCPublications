package org.bookmarks.groovy
import groovy.sql.Sql;
import groovy.grape.Grape;

//Load the mysql driver
Grape.grab(group:'mysql', module:'mysql-connector-java', version:'5.1.6', classLoader: this.class.classLoader.rootLoader)
this.class.classLoader.getURLs().each{ 
  ClassLoader.systemClassLoader.addURL(it); 
}

//class StockItem {
//    // properties
//    String isbn
//    Integer categoryId
//    String author
//
//    // sample code
//    static void main(args) {
//        def customer = new StockItem(id:1, name:"Gromit", dob:new Date())
//        println("Hello ${customer.name}")
//    }
//}


def sql = Sql.newInstance("jdbc:mysql://localhost:3306/bookmarks",
  "root", "h31nz", "com.mysql.jdbc.Driver")
def stockItem = sql.dataSet("StockItem")
count = 0
author = null
price = null
title = null
isbn = null
new File("c:\\art.csv").splitEachLine(",") {fields ->
	//fields.each {line -> print line;}
	if(fields[1]) author = fields[1]
	if(fields[0] == "") return
	price = fields[0][4..6] + "." + fields[0][7..8]
	title = fields[0]
//	isbn = '777777777' + count < 100 ? '7' : '' + count < 10 ? '7' : '' 
	isbn = '7777777777' + count
	println "price = " + price
	println "author = " + author
	println "title = " + title
	println "isbn = " + isbn
	count++
	
	stockItem.add(
	    isbn: isbn,
		title: title,
	    concatenatedAuthors: author,
	    sellPrice: price.toBigDecimal(),
		publisherPrice: price.toBigDecimal(),
		costPrice: 0,
		discount: 0,
		isInBrochure: false,
		isStaffPick: false,
		isbnAsNumber: isbn,
		quantityInStock: 0,
		quantityOnLoan: 0,
		stockItemType: 'ARTWORK',
		quantityReadyForCustomer: 0,
		quantityOnOrder: 0,
		quantityForCustomerOrder: 0,
		imageURLUnavailable: false,
		quantityToKeepInStock: 0,
		lastReorderReviewDate: new Date(),
		category_id: 35,
		publisher_id: 1,
		creationDate: new Date(),
		availability: 'PUBLISHED',
		binding: 'NA'
	)
}
