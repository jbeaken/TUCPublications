package org.bookmarks.groovy;
import groovy.sql.Sql;
import groovy.grape.Grape;


//select count(*) from stockitem si left join stockitem_author sia on si.id = sia.stockitem_id	where sia.stockitem_id is null;
//update stockitem si set si.is_synced_with_az = false where si.id not in (select stockitem_id from stockitem_author);
//Load the mysql driver
Grape.grab(group:'mysql', module:'mysql-connector-java', version:'5.1.39', classLoader: this.class.classLoader.rootLoader)
this.class.classLoader.getURLs().each{ 
  ClassLoader.systemClassLoader.addURL(it); 
}

def rectifyAuthor = {
	int index = it.indexOf(",")
	def surname = it.substring(0, index)
	def firstname = it.substring(index + 2)
	firstname = firstname[0].toUpperCase() + firstname.substring(1)
	surname = surname[0].toUpperCase() + surname.substring(1)
	"${firstname} ${surname}"
}

def bm = Sql.newInstance("jdbc:mysql://localhost:3306/bookmarks",	"root", "admin", "com.mysql.jdbc.Driver")

bm.eachRow("""select id, concatenatedauthors, isbn from stockitem si where concatenatedauthors like '%,%'""") { row ->
	def id = row[0]
	def concatenatedauthors = row[1]
	println "${id} ${row[2]} ${concatenatedauthors}"
	try {
		String newName = rectifyAuthor( concatenatedauthors)
		bm.executeUpdate('update stockitem set concatenatedauthors = :newName where id = :id',
			[newName : newName, id : id])
		println newName
	} catch (StringIndexOutOfBoundsException e) {
		println "ERROR!"
	}
}
		



