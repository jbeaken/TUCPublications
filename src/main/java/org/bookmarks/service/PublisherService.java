package org.bookmarks.service;

import org.bookmarks.domain.Publisher;
import java.util.Collection;

public interface PublisherService extends Service<Publisher>{
	Collection<Publisher> getForAutoComplete(String searchString);
	
	Publisher findBySimilarName(String publisherName);
}
