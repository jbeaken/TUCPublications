package org.bookmarks.groovy

def command = """/usr/bin/mysqldump -uroot -padmin bookmarks -C --result-file=/home/hal/Dropbox/Backups/bm.sql"""// Create the String
def proc = command.execute()                 // Call *execute* on the string
proc.waitFor()  

command = """tar cfz /home/hal/Dropbox/Backups/bm.${new Date()}.tar.gz /home/bookmarks/backup/bm.sql"""// Create the String
proc = command.execute()                 // Call *execute* on the string
proc.waitFor()

