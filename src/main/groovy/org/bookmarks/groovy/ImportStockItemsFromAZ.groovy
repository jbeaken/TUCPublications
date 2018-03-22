package org.bookmarks.groovy

import org.jsoup.select.Elements
import org.jsoup.nodes.Element
import org.jsoup.nodes.Document
import org.jsoup.Jsoup

import org.bookmarks.service.AZLookupServiceImpl
import org.bookmarks.domain.StockItem


import groovy.sql.Sql
@GrabConfig(systemClassLoader=true)
//@Grab('mysql:mysql-connector-java:5.1.39')
//@Grab('ch.qos.logback:logback-classic:1.0.13')
//@Grab('org.jsoup:jsoup:1.7.2')
	def service = new AZLookupServiceImpl()

	def bmw = Sql.newInstance("jdbc:mysql://localhost:3306/chips", "root", "h31nz", "com.mysql.jdbc.Driver")

	def count = 0

	bmw.eachRow("select isbn, id, title from stock_item where isbn like '978%' and title like '%marx%' limit 0, 10000") { row ->
	def isbn = row[0]
	def sId = row[1]
	def originalTitle = row[2]
	def stockItem = new StockItem();
	stockItem.isbn = isbn
	service.lookupWithJSoup(stockItem)

	def authors = stockItem.authors

	//PERSIT
	authors.each {
		def aId
		bmw.eachRow("select id from author where name = :name", [name : it.text()]) {authorRow ->
			aId = authorRow[0]
		}

		if(!aId) {
			def keys = bmw.executeInsert("insert into author (name) values (:name)", [name : it.text()])
			aId = keys[0][0]
		}
		try {
			bmw.executeInsert("insert into stock_item_authors (stock_items, authors) values (:sId, :aId)", [sId : sId, aId : aId])
		} catch (Exception e) {}
		System.out.println("Author : ${it.text()}")
	}

	def pId
	bmw.eachRow("select id from publisher where name = :name", [name : publisher]) {publisherRow ->
		pId = publisherRow[0]
	}

	if(!pId) {
		def keys = bmw.executeInsert("insert into publisher (name) values (:name)", [name : publisher])
		pId = keys[0][0]
	}

	bmw.execute("update stock_item set title = :title, published_date = :publishedDate, publisher = :pId, image_filename = :imageUrl, review = :review where id = :sId", [title : title, publishedDate : publishedDate, pId : pId, sId : sId, review : review, imageUrl : imageUrl])
	System.out.println()
} //End eachRow


def persistAuthors = {

}
