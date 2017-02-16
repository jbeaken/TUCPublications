import org.jsoup.select.Elements
import org.jsoup.nodes.Element
import org.jsoup.nodes.Document
import org.jsoup.Jsoup

@Grapes(
	@Grab(group='org.jsoup', module='jsoup', version='1.10.2')
)


Document doc = Jsoup.connect("http://thecharnelhouse.org/2015/08/04/open-source-marxism-free-pdfs-from-historical-materialism-verso-and-jacobin/").get();

Elements hrefs = doc.select("li > a")

hrefs.each {
  String url = it.attr("href")


  if(url.contains(".pdf")) {
    println url

    if(!url.contains("/20")) throw new Exception("Bub")
    int index = url.indexOf("/20") + 9
    String filename = url[index..-1]
    println filename

    def file = new File('/home/pod/books/' + filename)
    if(!file.exists()) {
			def stream = file.newOutputStream()
			stream << new URL(url).openStream()
			stream.close()
		}



  }
}
