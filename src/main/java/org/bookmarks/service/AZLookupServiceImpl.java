package org.bookmarks.service;

import static org.imgscalr.Scalr.OP_ANTIALIAS;
import static org.imgscalr.Scalr.OP_BRIGHTER;
import static org.imgscalr.Scalr.resize;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.bookmarks.domain.Category;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.Author;
import org.bookmarks.domain.Availablity;
import org.bookmarks.domain.Binding;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemType;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.WebsiteInfo;
import org.bookmarks.exceptions.BookmarksException;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Lookup stockitem information at AZ. Use the ISBN to do an initial search
 * e.g. http://www.amazon.co.uk/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords= + isbn;
 * This is the landing page and also contains the image as the drilldown page makes images harder to
 * retrieve using base64
 *
 * Use drilldown page to get prices etc.
 * @author jack
 */
@Service
public class AZLookupServiceImpl implements AZLookupService {

	// public final String azUrl = "https://www.amazon.co.uk/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords=";
	public final String azUrl = "https://www.amazon.co.uk/s/ref=nb_sb_noss?url=search-alias%3Dstripbooks&field-keywords=";
	@Value("#{ applicationProperties['imageFileLocation'] }")
	private String imageFileLocation;

	@Autowired
	private PublisherService publisherService;

	@Autowired
	private SupplierService supplierService;

	@Autowired
	private AuthorService authorService;

	private Logger logger = LoggerFactory.getLogger(AZLookupServiceImpl.class);

	@Autowired
	private StockItemImageService stockItemImageService;

	@Autowired
	private StaticDataService staticDataService;

	@Override
	public StockItem lookupWithJSoup(String isbn) throws Exception {
		return lookupWithJSoup(isbn, true);
	}


	@Override
	public StockItem lookupWithJSoup(String isbn, boolean downloadImage) throws Exception {
		StockItem stockItem = new StockItem();
		stockItem.setIsbn(isbn);

		//Pre drill down, get the first page when searching for an isbn
		Document doc = getLandingPageDocument(isbn);

		logger.debug("Looking up isbn " + isbn);
		if(doc == null) {
			logger.info("The isbn " + isbn + " is not found on AZ");
			return null;
		}

		//Image is on first page as drilldown page uses base64 encoded images
		//Get the image and download if it exists and download = true
		if(downloadImage == true) {
			try {
				String imageUrl = getImageUrlFromDocument(doc);
				if(imageUrl != null) {
					stockItem.setImageURL(imageUrl);
					stockItem.setImageFilename(isbn + ".jpg");
					saveImageToFileSystem(imageUrl, stockItem); //Saves filename as well
					stockItem.setIsImageOnAZ(true);
				} else {
					stockItem.setIsImageOnAZ(false);
				}
			} catch(Exception e) {
				logger.error("Cannot get image", e);
				stockItem.setIsImageOnAZ(false);
			}
		}

		//Now use drilldown
		doc = getDrilldownPageDocument(doc);
		//check if haven't been replaced by newer edition
		Element newerEdition = doc.select("div#ob-replacement_feature_div").first();
		if(newerEdition != null) {
			stockItem.setHasNewerEdition(true);
		}
		//Bucket contains various elements for use
		Elements bucket = doc.select("td.bucket ul");
		if(logger.isTraceEnabled() ) {
			logger.trace("td.bucket : " + bucket);
		}
		getTitle(doc, stockItem);

		getAuthors2(doc, stockItem);

		getPriceAndAvailability2(doc, stockItem);

		getBindingAndNoOfPages(bucket, stockItem);

		getPublisherInfo(bucket, stockItem);

		getDimensions(bucket, stockItem);

		getReview3(doc, stockItem);

		getCategory(doc, stockItem);

		stockItem.setIsOnAZ(true);

		stockItem.setSyncedWithAZ(true);

		return stockItem;
	}

	private void getCategory(Document doc, StockItem stockItem) {
		try {
			Elements elements = doc.select("div.bucket");

			for(Element e : elements) {
				Element h2 = e.select("h2").first();

				if(h2 == null || !h2.text().equals("Look for similar items by category")) {
					continue;
				}
				Elements li = e.select("ul li");
				String text = li.text();

				logger.debug("Category text : " + li.text());

				if(text.contains("Poetry")) {
					stockItem.setCategory(new Category(21l)) ;
				} else if(text.contains("Fiction")) {
					stockItem.setCategory(new Category(11l)) ;
				} else if(text.contains("Music")) {
					stockItem.setCategory(new Category(20l)) ;
				} else {
					//General politics (default)
					stockItem.setCategory(new Category(13l)) ;
				}

				//Override

				if(text.contains("Economics")) {
					stockItem.setCategory(new Category(9l)) ;
				}if(text.contains("Ireland")) {
					stockItem.setCategory(new Category(38l)) ;
				}
				if(text.contains("China")) {
					stockItem.setCategory(new Category(86l)) ;
				}
				if(text.contains("Africa")) {
					stockItem.setCategory(new Category(4l)) ;
					logger.debug("Have selected Africa category");
				}
				if(text.contains("Middle East")) {
					stockItem.setCategory(new Category(42l)) ;
					logger.debug("Have selected Middle East category");
				}
				if(text.contains("Britain")) {
					stockItem.setCategory(new Category(31l)) ;
					logger.debug("Have selected Britain category");
				}
				if(text.contains("Black Studies")) {
					stockItem.setCategory(new Category(7l)) ;
					logger.debug("Have selected Black Studies category");
				}
				if(text.contains("Children")) {
					stockItem.setCategory(new Category(95l)) ;
					logger.debug("Have selected Kid's category");
				}
				if(text.contains("Young Adult")) {
					stockItem.setCategory(new Category(98l)) ;
					logger.debug("Have selected Young Adult category");
				}
				if(text.contains("Women's Studies")) {
					stockItem.setCategory(new Category(28l)) ;
					logger.debug("Have selected Women's category");
				}
				if(text.contains("Sport")) {
					stockItem.setCategory(new Category(44l)) ;
					logger.debug("Have selected Women's category");
				}

				logger.debug(li.html());
				//Exit if category is sorted
				if(stockItem.getCategory() != null) break;
			}
		} catch(Exception e) {
			logger.error("Cannot get category", e);
		}
	}


	@Override
	public String getImageUrl(String isbn) throws IOException {
		Document doc = getLandingPageDocument(isbn); //can be null
		doc = getDrilldownPageDocument(doc);
		String imageUrl = getImageUrlFromDocument(doc);
		logger.debug("Have imageUrl " + imageUrl + " for isbn " + isbn);
		return imageUrl;
	}

	private Document getLandingPageDocument(String isbn) throws IOException {

//		String url = "http://www.amazon.co.uk/s/ref=nb_sb_noss?url=search-alias%3Dstripbooks&field-keywords=" + isbn + "&rh=n%3A266239%2Ck%3A9780952203834";
		//http://www.amazon.co.uk/s/ref=nb_sb_noss?url=search-alias%3Dstripbooks&field-keywords=9780198229773&rh=n%3A266239%2Ck%3A9780198229773
		String url = azUrl + isbn;
		logger.debug("Search Url : " + url);

		Document doc = Jsoup.connect(url)
			.data("query", "Java")
			.data("Cache-Control", "max-age=0")
			.data("Accept-Language", "en-GB,en;q=0.5")


			.data("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:41.0) Gecko/20100101 Firefox/41.0")
			.cookie("auth", "token")
			.timeout(12000)
			.post();


		Element element = doc.select("h1#noResultsTitle").first();
		if(element != null) {
			//There is a no results message so skip this isbn
			logger.info("No results from AZ");
			return null;
		}

		return doc;

	}

	private Document getDrilldownPageDocument(Document doc) throws IOException {

		// logger.debug(doc.html());

		Elements elements = doc.select(".newaps");

		//Drill down for more details
		String drilldownUrl = elements.select("a[href]").attr("href");

		if(drilldownUrl.isEmpty()) {
			logger.debug("Cannot find drilldown using .newapps, attempting with .s-access-detail-page");
			//2nd look
			//<a class="a-link-normal s-access-detail-page  a-text-normal"

			elements = doc.select(".s-access-detail-page");
			logger.debug("Elements size : " + elements.size());
			for(Element e : elements) {
				logger.debug("Have href : " + e.attr("href"));
			}
			if(!elements.isEmpty()) drilldownUrl = elements.first().attr("href");
		}

		if(drilldownUrl.isEmpty()) {
			logger.debug("Cannot find drilldown using .s-access-detail-page, attempting with a.s-access-detail-page");
			//3nd look
			elements = doc.select("a.s-access-detail-page");
			if(!elements.isEmpty()) drilldownUrl = elements.first().attr("href");

		}
		if(drilldownUrl.isEmpty()) throw new BookmarksException("Cannot get drill down page");

		if(drilldownUrl.indexOf("https://www.amazon.co.uk") == -1) {
			drilldownUrl = "https://www.amazon.co.uk" + drilldownUrl;
		}
		logger.debug("Drilldown : " + drilldownUrl);
		doc = Jsoup.connect(drilldownUrl)
			.data("query", "Java")
			.data("Cache-Control", "max-age=0")
			.data("Accept-Language", "en-GB,en;q=0.5")
			.data("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:41.0) Gecko/20100101 Firefox/41.0")
			.cookie("auth", "token")
			.timeout(8000)
			.post();
		return doc;
	}

	private void getTitle(Document doc, StockItem stockItem) {
		//String title = doc.select("span#btAsinTitle").text().replaceFirst( /\[\w*]/, "");
		String title  = doc.select("span#btAsinTitle").text();
		if(title.isEmpty()) title = doc.select("span#productTitle").text();
		title = title.replace(" [Paperback]", "");
		title = title.replace(" [Hardcover]", "");
		logger.debug("Title : " + title);

		if(title.length() > 254) {
			title = title.substring(0, 254);
			logger.debug("Shrunk Title : " + title);
		}
		stockItem.setTitle(title);
	}

	private void getAuthors2(Document doc, StockItem stockItem) {

		Elements authors =  doc.select("span.author");

		for(Element e : authors) {
			//Create author
			String name = e.select("span.a-size-medium").text();
			if(name.isEmpty()) {
				name = e.select("a.a-link-normal").text();
			}
			name = name.replace(" (Author)", "");
			Author author = new Author(name);

			//Does author already exist?
			Author exists = authorService.findByName(author.getName());
			if(exists == null) {
				stockItem.getAuthors().add(author);
			} else {
				stockItem.getAuthors().add(exists);
			}

			logger.debug("Author: " + author.getName());
		}



		// }
		//It is possible to have no author eg 9780978527105
		// for(Element a : authors) {
		// 	logger.debug("Author : " + a.text());
		// }
	}

	private void getAuthors(Document doc, StockItem stockItem) {

		//Authors
		// Elements authors = doc.select("[href~=contributorNameTrigger]");
		// for(Element a : authors) {
		// 	logger.debug("Author1 : " + a.text());
		// }

		// authors = doc.select("[href~=field-author]");
		// for(Element a : authors) {
		// 	logger.debug("Author2 : " + a.text());
		// }
		// if(authors == null) {
		Elements authors =  doc.select("[href~=ntt_athr]");

		//If no authors, could be artist e.g 5060116370034
		if(authors.isEmpty()) {
			//Try something else
			authors =  doc.select("[href~=field-artist]");
		}

		for(Element e : authors) {
			//Create author
			String name = e.text();
			Author author = new Author(name);

			//Does author already exist?
			Author exists = authorService.findByName(author.getName());
			if(exists == null) {
				stockItem.getAuthors().add(author);
			} else {
				stockItem.getAuthors().add(exists);
			}

			logger.debug("Author: " + author.getName());
		}



		// }
		//It is possible to have no author eg 9780978527105
		// for(Element a : authors) {
		// 	logger.debug("Author : " + a.text());
		// }
	}

	private void getPriceAndAvailability2(Document doc, StockItem stockItem) {
			//Default
			stockItem.setAvailability(Availablity.PUBLISHED);

		try {
			Element priceSection = doc.select("div#buyNewInner").first();
			if(logger.isTraceEnabled()) {
				logger.trace(doc.html());
			}
			if(priceSection == null) {
				priceSection = doc.select("div#buyNewSection").first();
			}

			//AZ Price
			String azPrice = doc.select("span.offer-price").first().text().replace("£", "");

			//RRP
			Element rrpSection = priceSection.select("span.a-color-secondary").get(1);
			String rrp = rrpSection.text().replace("£", "");

			//New and used 3rd Party
			//Element newAndUsedElement = doc.select("span.tmm-olp-links").first;
//			String used = doc.select("span.olp-used a.a-link-normal").text();
//			String newBooks = doc.select("span.olp-new a.a-link-normal").text();

			logger.debug("RRP (span#listPriceValue) : " + rrp);
			stockItem.setSellPrice(new BigDecimal(rrp));
			stockItem.setPublisherPrice(stockItem.getSellPrice());
			stockItem.setDiscount(new BigDecimal(40));
			stockItem.calculateCostPrice();

			if(azPrice != null && !azPrice.isEmpty()) {
				logger.debug("AZRP (span#actualPriceValue) : " + azPrice);
				stockItem.setPriceAtAZ(new BigDecimal(azPrice));
			} else logger.debug("AZ Price is not available");

			//3rd Party
//			if(newBooks != null && !newBooks.isEmpty()) {
//				logger.debug("3rd Party new price : " + newBooks);
//				stockItem.setPriceThirdPartyNew(new BigDecimal(newBooks));
//			}
//			if(used != null && !used.isEmpty()) {
//				logger.debug("3rd Party second hand price : " + used);
//				stockItem.setPriceThirdPartySecondHand(new BigDecimal(used));
//			}




			if(stockItem.getSellPrice() != null) {
				stockItem.setAvailability(Availablity.PUBLISHED);
			} else if(stockItem.getPriceThirdPartyNew() != null) {
				stockItem.setAvailability(Availablity.AVAILABLE_NEW_FROM_THIRD_PARTY);
			} else	stockItem.setAvailability(Availablity.OUT_OF_PRINT);
			logger.debug("Have set availablity to : " + stockItem.getAvailability().getDisplayName());
		} catch(Exception e) {
			try {
				logger.warn("Cannot get price from first lookup, falling back");
				String azPrice = doc.select("span.offer-price").first().text().replace("£", "");
				Float price = Float.parseFloat(azPrice);
				stockItem.setSellPrice(new BigDecimal(price));
				stockItem.setPublisherPrice(new BigDecimal(price));
				stockItem.setDiscount(new BigDecimal(40));
				stockItem.calculateCostPrice();
			}catch(Exception e1) {
				logger.error("Fallback price didn't work, giving up", e1);
			}
		}
	}

private void getPriceAndAvailability(Document doc, StockItem stockItem) {
	//Default
	stockItem.setAvailability(Availablity.PUBLISHED);

	Element priceSection = doc.select("div#olpDivId").first();

	if(priceSection != null) { //Otherwise has 'Currently unavailable' flag
		logger.debug(priceSection.html());

		Elements priceSections = priceSection.select("span.price");

		String thirdPartyNewPrice = null;
		String thirdPartySecondHandPrice = null;
		String thirdPartyCollectablePrice = null;

		if(priceSections.size() == 2) {
			thirdPartyNewPrice = priceSection.select("span.price").get(0).text(); //Two, first is new
			thirdPartySecondHandPrice = priceSection.select("span.price").get(1).text(); //Two, first is new
		} else if(priceSections.size() == 1) {
			thirdPartySecondHandPrice = priceSection.select("span.price").get(0).text(); //Two, first is new
		} else if(priceSections.size() == 3) {
			thirdPartyNewPrice = priceSection.select("span.price").get(0).text(); //Two, first is new
			thirdPartySecondHandPrice = priceSection.select("span.price").get(1).text(); //Two, first is new
			thirdPartyCollectablePrice = priceSection.select("span.price").get(2).text(); //Two, first is new
		}

		String rrprice = doc.select("span#listPriceValue").text();
		String azprice = doc.select("span#actualPriceValue").text();

		//Sometimes just have az price
		if(rrprice.isEmpty() && !azprice.isEmpty()) {
			logger.debug("RRPrice isn't set, setting to AZ Price");
			rrprice = azprice;
		}

		if(rrprice != null && !rrprice.isEmpty()) {
			logger.debug("RRP (span#listPriceValue) : " + rrprice);
			stockItem.setSellPrice(new BigDecimal(rrprice.substring(1)));
			stockItem.setPublisherPrice(stockItem.getSellPrice());
 			stockItem.setDiscount(new BigDecimal(40));
			stockItem.calculateCostPrice();
		} else {
			logger.debug("RRPrice is not available");
		}
		if(azprice != null && !azprice.isEmpty()) {
			logger.debug("AZRP (span#actualPriceValue) : " + azprice);
			stockItem.setPriceAtAZ(new BigDecimal(azprice.substring(1)));
		} else logger.debug("AZ Price is not available");

		//3rd Party
		if(thirdPartyNewPrice != null && !thirdPartyNewPrice.isEmpty()) {
			logger.debug("3rd Party new price : " + thirdPartyNewPrice);
			stockItem.setPriceThirdPartyNew(new BigDecimal(thirdPartyNewPrice.substring(1)));
		}
		if(thirdPartySecondHandPrice != null && !thirdPartySecondHandPrice.isEmpty()) {
			logger.debug("3rd Party second hand price : " + thirdPartySecondHandPrice);
			stockItem.setPriceThirdPartySecondHand(new BigDecimal(thirdPartySecondHandPrice.substring(1)));
		}
		if(thirdPartyCollectablePrice != null && !thirdPartyCollectablePrice.isEmpty()) {
			logger.debug("3rd Party collectable price : " + thirdPartyCollectablePrice);
			stockItem.setPriceThirdPartyCollectable(new BigDecimal(thirdPartyCollectablePrice.substring(1)));
		}

	} else {
		//Try something else, this should replace above at some stage
		priceSection = doc.select("span#listPriceValue").first();
		logger.debug(doc.text());
		logger.debug(priceSection.html());

		String azprice = doc.select("span#actualPriceValue").text();
		String rrprice = doc.select("span#listPriceValue").text();
		if(rrprice != null) {
			logger.debug("RRP (span#listPriceValue) : " + rrprice);
			stockItem.setSellPrice(new BigDecimal(rrprice.substring(1)));
			stockItem.setPublisherPrice(stockItem.getSellPrice());
 			stockItem.setDiscount(new BigDecimal(40));
			stockItem.calculateCostPrice();
		}
		if(azprice != null) {
			logger.debug("AZRP (span#actualPriceValue) : " + azprice);
			stockItem.setPriceAtAZ(new BigDecimal(azprice.substring(1)));
		}
	}

	if(stockItem.getSellPrice() != null) {
		stockItem.setAvailability(Availablity.PUBLISHED);
	} else if(stockItem.getPriceThirdPartyNew() != null) {
		stockItem.setAvailability(Availablity.AVAILABLE_NEW_FROM_THIRD_PARTY);
	} else	stockItem.setAvailability(Availablity.OUT_OF_PRINT);
	logger.debug("Have set availablity to : " + stockItem.getAvailability().getDisplayName());
}

private void getBindingAndNoOfPages(Elements bucket, StockItem stockItem) {
	String binding = null;
	try {
		binding = bucket.select("li").get(0).text();
	} catch(IndexOutOfBoundsException e) {
		logger.info("Cannot get binding");
		return;
	}
	logger.debug("Raw binding : " + binding);
	Binding type = Binding.OTHER;
	if(binding.indexOf("Kindle") != -1) {
		type = Binding.KINDLE;
	} else if(binding.indexOf("Paperback") != -1) {
		type = Binding.PAPERBACK;
	} else if(binding.indexOf("Hardback") != -1) {
		type = Binding.HARDBACK;
	} else if(binding.indexOf("Hardcover") != -1) {
		type = Binding.HARDBACK;
	} else if(binding.indexOf("Audio CD") != -1) {
		type = Binding.CD;
		stockItem.setType(StockItemType.CD);
	}
	Pattern pattern = Pattern.compile("\\d+");
	Matcher matcher = pattern.matcher(binding);
	while(matcher.find()) {
		Integer noOfPages = Integer.parseInt(matcher.group());
		stockItem.setNoOfPages(noOfPages);
	}
	logger.debug("Binding : " + type.getDisplayName());
	logger.debug("No. of pages : " + stockItem.getNoOfPages());
	stockItem.setBinding(type);
}

private void getPublisherInfo(Elements bucket, StockItem stockItem) throws ParseException {
	//Publisher and published date
	String publisher = null;
	try {
		publisher = bucket.select("li").get(1).text();
	} catch(IndexOutOfBoundsException e) {
		logger.info("Cannot get publisher");
		return;
	}
	String purePublisher = null;
	logger.debug("Stage 1 publisher and date : " + publisher);

	Date publishedDate = null;
	String sPublishedDate = null;
	if(publisher.indexOf("Language: English") == -1 && publisher.indexOf("ISBN-") == -1) { //Sometimes!!
		publisher = publisher.replaceAll("Publisher: ", "");
		publisher = publisher.replaceAll("; (Reissue)", "");
		publisher = publisher.replaceAll(" edition ", "");
		logger.debug("Stage 2 publisher and date  : " + publisher);

		int i = publisher.indexOf("(");
		if(i != -1) { //Sometimes date is not available e.g 9781899021055 Reggae Head [Audiobook] [Audio CD]
			purePublisher = publisher.substring(0, i);
		} else {
			purePublisher = publisher;
		}
		i = purePublisher.indexOf(';');
    	if(i != -1) {
    		purePublisher = purePublisher.substring(0, i);
		}
		logger.debug("Pure Publisher : " + purePublisher);

		Pattern pattern = Pattern.compile("\\d\\d+ \\w{3} \\d{4}");
		Matcher matcher = pattern.matcher(publisher);
		while (matcher.find()) {
			sPublishedDate = matcher.group();
			publishedDate = new SimpleDateFormat("dd MMM yyyy").parse(sPublishedDate);
    	}
    	if(publishedDate == null) {
    		//Publisher: HarperCollins Publishers Ltd; New edition edition (May 1973)
    		//or (April 1972)
    		//or Abacus
    		pattern = Pattern.compile("\\w{3}\\w+ \\d{4}");
			matcher = pattern.matcher(publisher);
			while (matcher.find()) {
				sPublishedDate = matcher.group();
				try {
					publishedDate = new SimpleDateFormat("MMM yyyy").parse(sPublishedDate);
				} catch(Exception e) {
					logger.error("Cannot format", e);
				}
			}
    	}
    	if(publishedDate == null) {
    		//Publisher: Flamingo; 1st edition (2003)
    		pattern = Pattern.compile("\\d{4}");
			matcher = pattern.matcher(publisher);
			while (matcher.find()) {
				sPublishedDate = matcher.group();
				publishedDate = new SimpleDateFormat("yyyy").parse(sPublishedDate);
			}
    	}
		if(sPublishedDate != null) {
			publisher = publisher.replace(sPublishedDate, "");
		}
    	publisher = publisher.replace("()", "");
    	int index = publisher.indexOf(';');
    	if(index != -1) {
			publisher = publisher.substring(0, index);
		}
		logger.debug("publisher : " + publisher);
		logger.debug("pub date : " + publishedDate);
		stockItem.setPublishedDate(publishedDate);

		//Get publisher
		Publisher p = publisherService.getByName(purePublisher);
		if(p == null) { //This publisher isn't in the bookmarks database, save it
			logger.info("Adding new publisher : " + purePublisher);
			p = new Publisher();
			p.setName(purePublisher);
			if(p.getName() != null) {
				Supplier supplier = supplierService.get(1l); //Gardners
				p.setSupplier(supplier);
				publisherService.save(p);
			} else {
				p = null;
			}
			staticDataService.resetPublishers();
		}
		stockItem.setPublisher(p);
	}
}
	private void getDimensions(Elements bucket, StockItem stockItem) {
		int width = -1;
		int height = -1;
		int depth = -1;
		String dimensions = null;
		if(bucket.select("li").size() > 5) {
			dimensions = bucket.select("li").get(5).text();

			if(dimensions.indexOf("Dimensions") == -1) {
				dimensions = null;
			} else {
				dimensions = dimensions.replace("Product Dimensions: ", "");
				Pattern pat = Pattern.compile("[0-9]*\\.?[0-9]?");
				Matcher vector = pat.matcher(dimensions);
				while(vector.find()) {
//					logger.debug(vector.group());
				}
			}
		}
		stockItem.setDimensions(dimensions);
		logger.debug("dimensions : " + dimensions);
	}
	private void getReview(Document doc, StockItem stockItem) {
		//Review
		Element reviewElement = doc.select("div#postBodyPS").first();

		if(reviewElement == null) {
			reviewElement = doc.select("div.productDescriptionWrapper").first();
		}

		if(reviewElement != null) {
			String reviewAsText = reviewElement.text();
			String reviewAsHtml = reviewElement.html();
			if(reviewAsText.indexOf("##############") != -1) {
				reviewAsText = null;
				reviewAsHtml = null;
			} else {
				logger.debug("review : " + reviewAsText);

				if(reviewAsText.length() > 10000) {
					reviewAsText = reviewAsText.substring(0, 9999);
				}
				if(reviewAsHtml.length() > 10000) {
					reviewAsHtml = reviewAsHtml.substring(0, 9999);
				}
			}

			stockItem.setReviewAsText(reviewAsText);
			stockItem.setReviewAsHTML(reviewAsHtml);
			stockItem.setIsReviewOnAZ(true);
		}
	}


	private void getReview3(Document doc, StockItem stockItem) throws java.io.UnsupportedEncodingException {

		logger.debug("Looking up review in review3 : " );
		String reviewAsHtml = null;
		String reviewAsText = null;

		//Check for presense of noscript
		Elements noScriptElements = doc.select("noscript");
		if(noScriptElements != null) {
			for(Element e : noScriptElements) {
				logger.debug("Have noScript as text = " + e.text());
				logger.debug("Have noScript as html = " + e.html());
			}
		} else {
			logger.debug("noScriptElements is null!");
		}

		//Check for presense of id=iframeContent
		Elements iframeContentElements = doc.select("div#iframeContent");

		if(iframeContentElements != null) {
			Element iframeContent = iframeContentElements.first();
			if(iframeContent != null) {
				logger.debug("Have iframeContent as text = " + iframeContent.text());
				logger.debug("Have iframeContent as html = " + iframeContent.html());
				reviewAsText = iframeContent.text();
				reviewAsHtml = "<p" + iframeContent.html() + "</p>";
			} else {
					logger.debug("iframeContent is null!");
			}
		}

		try {

			Elements productDescriptionElements = doc.select("div#productDescription");

			if(productDescriptionElements != null) {

				Element productDescription = productDescriptionElements.first();

				logger.debug(productDescription.html());

				if(productDescription != null) {
					if(reviewAsText == null) reviewAsText = "";
					if(reviewAsHtml == null) reviewAsHtml = "";

					for(Element e : productDescription.select("p")) {
						logger.debug(e.text());
						logger.debug(e.html());
						reviewAsText = reviewAsText + e.text();
						reviewAsHtml =  "<p>" + reviewAsHtml + e.html() + "</p>";
					}

					reviewAsText = java.net.URLDecoder.decode(reviewAsText, "UTF-8");

			  		//logger.debug("review : " + reviewAsText);

			  		//reviewAsHtml = reviewAsText;
					stockItem.setReviewAsText(reviewAsText);
					stockItem.setReviewAsHTML(reviewAsHtml);
					stockItem.setIsReviewOnAZ(true);
					return;
				}
		} else {
			logger.debug("Cannot get review from lookup 1 : Cannot find elements div#productDescription");
		}
	} catch(Exception e) {
		logger.error("Cannot get review", e);
	}

/*
		String docText = doc.html();
		int start = docText.indexOf("bookDescEncodedData");
		int end = docText.indexOf("bookDescriptionAvailableHeight");
	logger.debug("start : " + start );
	logger.debug("end : " + end );

		if(start != -1 && end != -1) {
		//	String reviewAsText = docText.substring(start, end);
			String reviewAsText = docText
				.substring(start, end)
				.replace("bookDescEncodedData = ", "")
				.replace("\",", "")
				.replace("\"", "");

				reviewAsText = java.net.URLDecoder.decode(reviewAsText, "UTF-8");

			logger.debug("review : " + reviewAsText);

			String reviewAsHtml = reviewAsText;

				if(reviewAsText.length() > 10000) {
					reviewAsText = reviewAsText.substring(0, 9999);
				}
				if(reviewAsHtml.length() > 10000) {
					reviewAsHtml = reviewAsHtml.substring(0, 9999);
				}


			stockItem.setReviewAsText(reviewAsText);
			stockItem.setReviewAsHTML(reviewAsHtml);
			stockItem.setIsReviewOnAZ(true);
		} else {
			//Try something else
			Element reviewElement2 = doc.select("div#iframeContent").first();
				logger.debug("re2 : " + reviewElement2.html() );
		}
		*/
	}

	private void getReview2(Document doc, StockItem stockItem) {
		//Review
		Element reviewElement = doc.select("noscript").first();


		if(reviewElement != null) {
			String reviewAsText = reviewElement.text();
			String reviewAsHtml = reviewElement.html();
			if(reviewAsText.indexOf("##############") != -1) {
				reviewAsText = null;
				reviewAsHtml = null;
			} else {
				logger.debug("review : " + reviewAsText);

				if(reviewAsText.length() > 10000) {
					reviewAsText = reviewAsText.substring(0, 9999);
				}
				if(reviewAsHtml.length() > 10000) {
					reviewAsHtml = reviewAsHtml.substring(0, 9999);
				}
			}

			stockItem.setReviewAsText(reviewAsText);
			stockItem.setReviewAsHTML(reviewAsHtml);
			stockItem.setIsReviewOnAZ(true);
		}
	}

	/**
	 * Gets image url from AZ
	 */
	@Override
	public BufferedImage saveImageToFileSystem(StockItem stockItem) throws IOException {
		String imageUrl = getImageUrl(stockItem.getIsbn());
		return saveImageToFileSystem(imageUrl, stockItem, true);
	}

	//Image url is provided, not from az lookup
	@Override
	public BufferedImage saveImageToFileSystem(String imageUrl, StockItem stockItem) {
		return saveImageToFileSystem(imageUrl, stockItem, true);
	}

	//Image url is provided, not from az lookup
	@Override
	public BufferedImage saveImageToFileSystem(String imageUrl, StockItem stockItem, boolean resize) {
		//Get image
		BufferedImage image = getImageFromURL(imageUrl);
		try {
			saveImageToLocalFileSystem(image, imageFileLocation + "original" + File.separator + stockItem.getIsbn() + ".jpg");
			stockItem.setImageFilename(stockItem.getIsbn() + ".jpg");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Resize
		if(resize) {
			try {
				BufferedImage resizedImage = resize(image, Method.ULTRA_QUALITY, Mode.FIT_TO_WIDTH, 150, OP_ANTIALIAS);
				saveImageToLocalFileSystem(resizedImage, imageFileLocation + "150" + File.separator + stockItem.getIsbn() + ".jpg");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return image;
	}

	private String getImageUrlFromDocument(Document doc) {

		String imageUrl = null;

		Elements elements = doc.select("img.productImage");

		if(!elements.isEmpty()) {
			imageUrl = elements.first().attr("src");
		} else {
			elements = doc.select("img.s-access-image");
			imageUrl = elements.first().attr("src");
		}

		int indexOfComma = imageUrl.indexOf("._");
		String newUrl = imageUrl.substring(0, indexOfComma) + ".jpg";

		logger.debug("Pre AZimageUrl : " + imageUrl);

		imageUrl = imageUrl.replaceAll("sticker-arrow-click", "");
		imageUrl = imageUrl.replaceAll("sitb-sticker-arrow-dp", "");
		imageUrl = imageUrl.replaceAll("TopRight,", "");


		imageUrl = imageUrl.replaceAll("\\._\\w*_", "");
		imageUrl = imageUrl.replace("_BO2,204,203,200_PIsitb-,", "");

		imageUrl = imageUrl.replace("_BO2,204,203,200_PIsitb-,", "");

		imageUrl = imageUrl.replace("_BO2,204,203,200_PIsitb--small,", "");
		imageUrl = imageUrl.replace("_BO2,204,203,200_PIsitb-sticker-arrow-click,", "");
		imageUrl = imageUrl.replace("TopRight,35,-76_AA300_SH20_OU02_.", "");
		imageUrl = imageUrl.replace("TopRight,12,-30_AA300_SH20_OU02_.", "");
		//imageUrl = imageUrl.replace("_AA160_.", "");

		imageUrl = imageUrl.replace("._.", ".");

		logger.debug("Post production AZimageUrl : " + imageUrl);
		logger.debug("Post production newUrl : " + newUrl);

		if(imageUrl.indexOf("no-img-lg-uk") != -1) {
			logger.debug("Have found no-img-lg-uk in image URL, therefore no image on AZ. Setting imageUrl to null");
			return null;
		}

		return newUrl;
	}

	private BufferedImage getImageFromURL(String urlLocation) {
		BufferedImage image = null;
		try {
			URL url = new URL(urlLocation);
			image = ImageIO.read(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	private void saveImageToLocalFileSystem(BufferedImage image, String fileLocation) throws IOException {
	    File outputFile = new File(fileLocation);
		ImageIO.write(image, "jpg", outputFile);
	}
}
