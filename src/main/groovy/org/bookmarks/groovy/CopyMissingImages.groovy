package org.bookmarks.groovy


def isbns = new File("/home/bookmarks/missing_isbns.csv").text.split(", ")



isbns.each {
  new File('/home/bookmarks/images/original/${it}.jpg').bytes = new File('/home/bookmarks/images/missing/${it}.jpg').bytes
}
