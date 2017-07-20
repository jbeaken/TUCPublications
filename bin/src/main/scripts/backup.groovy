def command = """cmd.exe /c 'mysqldump -uroot -p bookmarks  bm.sql'"""// Create the String
def proc = command.execute()                 // Call *execute* on the string
proc.waitFor() 

println "return code: ${ proc.exitValue()}"