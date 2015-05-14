package org.bookmarks.groovy

@Grapes([
  @Grab('mysql:mysql-connector-java:5.1.31'),
  @Grab('org.bouncycastle:bcprov-jdk15on:1.52'),
  @Grab('org.jasypt:jasypt:1.9.2'),
  @GrabConfig(systemClassLoader=true)
])

import groovy.sql.Sql
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.bouncycastle.jce.provider.BouncyCastleProvider


StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
encryptor.setProvider(new BouncyCastleProvider());
encryptor.setPassword("aS4%hu&hbHrfgfJdd^77ksghEt&jjh!");
encryptor.setAlgorithm("PBEWITHSHA256AND128BITAES-CBC-BC");

def sql = Sql.newInstance("jdbc:mysql://localhost:3306/bookmarks", "root", "admin", "com.mysql.jdbc.Driver")

/**
String encryptedText = encryptor.encrypt("bastard")
println encryptedText
**/

//Get all customer order lines and encyprt
def customers = sql.eachRow("select * from customerorderline ") { row ->

  if(row.creditCard1 == null) return
  	
  String cc1 = row.creditCard1
  String encc1 = encryptor.encrypt(cc1)
  
  String cc2 = row.creditCard2
  String encc2 = encryptor.encrypt(cc2)
    
  String cc3 = row.creditCard3
  String encc3 = encryptor.encrypt(cc3)
  
  String cc4 = row.creditCard4
  String encc4 = encryptor.encrypt(cc4)    
  
  String sc = row.securityCode
  String encSC = encryptor.encrypt(sc)    
  
  String month = row.expiryMonth
  String encMonth = encryptor.encrypt(month)    
  
  String year = row.expiryYear
  String encYear = encryptor.encrypt(year)   
  
  String nameOnCard = row.nameOnCard
  String encNameOnCard = encryptor.encrypt(nameOnCard)
    
  println "Processing id ${row.id} ${encc4}"
      
  sql.executeUpdate("""update customerorderline set creditCard1 = :cc1, creditCard2 = :cc2, creditCard3 = :cc3, creditCard4 = :cc4, expiryMonth = :expiryMonth, expiryYear = :expiryYear, securityCode = :sc, nameOnCard = :nameOnCard 
  			where id = :id""", [cc1 : encc1, cc2 : encc2, cc3 : encc3, cc4 : encc4, expiryMonth : encMonth, expiryYear : encYear, sc : encSC, nameOnCard : encNameOnCard, id : row.id])
}

println "Finished"
