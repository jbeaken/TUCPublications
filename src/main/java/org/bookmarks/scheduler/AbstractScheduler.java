package org.bookmarks.scheduler;

import org.bookmarks.exceptions.BookmarksException;
import org.bookmarks.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public abstract class AbstractScheduler {

	@Autowired	private Environment environment;

	@Autowired	protected EmailService emailService;

	private Logger logger = LoggerFactory.getLogger(AbstractScheduler.class);

	protected boolean isProduction() {

		if(environment != null) {
			String profile = environment.getActiveProfiles()[0];

		    if(profile.equals("dev") || profile.equals("test")) {
		    	logger.info("Aborting due to profile " + profile);
		    	return false;
		    }

		    if(profile.equals("prod")) {
					logger.debug("Current active profile : " + profile);
		    	return true;
		    }

		    throw new BookmarksException("Invalid active profile : " + profile);
		}

		//For main application calls
		return true;
	}

//	protected abstract Set<ScraperISBNHolder> getIsbnHolderList(String drilldownURL);
}
