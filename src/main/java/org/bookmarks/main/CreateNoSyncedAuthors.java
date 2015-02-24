package org.bookmarks.main;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bookmarks.domain.Author;
import org.bookmarks.domain.Review;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.WebsiteInfo;
import org.bookmarks.service.AZLookupService;
import org.bookmarks.service.AuthorService;
import org.bookmarks.service.StockItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * 
 * @author King Kong
 * mvn exec:java -Dexec.mainClass="org.bookmarks.main.CreateNoSyncedAuthors"
 * 
 */
public class CreateNoSyncedAuthors extends AbstractSpringBooter {
	
//	private static SessionFactory sessionFactory = getSessionFactory();	
	
	static Logger logger = LoggerFactory.getLogger(CreateNoSyncedAuthors.class);
	
	public static void main(String[] args) {
		ApplicationContext applicationContext = getApplicationContext();
		
		StockItemService stockItemService = (StockItemService) applicationContext.getBean("stockItemServiceImpl");
		AuthorService authorService = (AuthorService) applicationContext.getBean("authorServiceImpl");
		
		while(true) {
			Collection<StockItem> stockItems = stockItemService.getNoAZAuthors(0, 100);
			logger.info("Loaded batch of " + stockItems.size());
			for(StockItem si : stockItems) {
				logger.info("****** Lookup of : " + si.toString());
				try {
//					logger.info("Attaching author " + si.getConcatenatedAuthors());
					Author author = new Author();
//					author.setName(si.getConcatenatedAuthors());
					Set<Author> authors = new HashSet<Author>(1);
					authors.add(author);
					persistAuthors(si, authors, authorService);
					log(si);
					stockItemService.update(si);
					
					logger.info("******** Success!!");
				} catch(Exception e) {
					logger.error("Error thrown while looking up, cannot persist author", e);

				}
			}//end for
			logger.info("Finished batch, reloading next batch");
		}//end while
	}

	private static void log(StockItem si) {
		logger.info("Publisher : " + si.getPublisher());
		logger.info("Title : " + si.getTitle());
		logger.info("Dimensions : " + si.getDimensions());
		logger.info("Sell Price : " + si.getSellPrice());
		for(Author a : si.getAuthors()) {
			logger.info("Author : " + a.getName());
		}
	}

	private static void persistAuthors(StockItem si, Set<Author> authors, AuthorService authorService) {
		Set<Author> authorsToAdd = new HashSet<Author>();
		for(Author author : authors) {
			Author exists = authorService.findByName(author.getName());
			if(exists == null) { //This author isn't in the bookmarks database, save it
				logger.info("Adding new author : " + author.getName());
				authorService.save(author);
				authorsToAdd.add(author);
				//si.getAuthors().add(author);
			} else {
				authorsToAdd.add(exists);
				//si.getAuthors().add(exists);
			}
		}
		si.setAuthors(authorsToAdd);
	}
}
