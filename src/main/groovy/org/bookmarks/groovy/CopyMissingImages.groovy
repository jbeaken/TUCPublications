package org.bookmarks.groovy


def isbns = new File("/home/bookmarks/missing_isbns.csv").text.split(", ")



isbns.each {
def filename = "/home/bookmarks/images/missing150/${it}.jpg"
def original = "/home/bookmarks/images/150/${it}.jpg"

println filename  

def originalfile = new File(original)

if(!originalfile.exists()) return

new File(filename).bytes = new File(original).bytes
}
