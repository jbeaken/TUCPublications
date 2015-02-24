package org.bookmarks.groovy


import groovy.sql.Sql
@GrabConfig(systemClassLoader=true)
@Grab('mysql:mysql-connector-java:5.1.31')

def bookmarks = Sql.newInstance("jdbc:mysql://localhost:3306/bookmarks", "root", "admin", "com.mysql.jdbc.Driver")

	def count = 0
	
def sd = bookmarks.executeInsert("""insert into supplierdelivery
		(creationDate, invoiceNumber, supplier_id) 
			values 
		(now(), 12537962, 1)""")
def sdId = sd[0][0]
			
new File("/home/hal/invoice.txt").eachLine {text ->

	def isbn = text =~ /\d{13}/
	count++
	if(isbn) {

	  def quantityInStock = text =~ /\s\d{1,2}\s/
	 
	  
	  def result = bookmarks.firstRow('select id, sellPrice from stockitem where isbn = ?', [isbn[0]])
	  
	  def id = result[0]
	  def sellPrice = result[1]

	
	def sdl = bookmarks.executeInsert("""insert into supplierdeliveryline 
		(creationDate, amount, price, sellPrice, publisherPrice, stockItem_id, discount) 
			values 
		(now(),?,?,?,?,?,40)""", [quantityInStock[0], sellPrice, sellPrice, sellPrice, id])
		
	def sdlId = sdl[0][0]
	println sdlId
	
	bookmarks.executeUpdate("insert into  supplierdelivery_supplierdeliveryline (SupplierDelivery_id, supplierDeliveryLine_id) values (?, ?)", [sdId,  sdlId])
 
 	def quantity = quantityInStock[0].trim()
 	println isbn[0] + " " + quantityInStock[0]
	bookmarks.executeUpdate('update stockitem set quantityInStock = quantityInStock + ? where isbn = ?', [quantityInStock[0], isbn[0]])
	
}
	//if(count > 40) System.exit(2);
}
	
