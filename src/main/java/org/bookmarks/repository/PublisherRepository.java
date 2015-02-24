package org.bookmarks.repository;

import org.bookmarks.domain.Publisher;
import java.util.Collection;

public interface PublisherRepository extends Repository<Publisher>{

	Publisher findBySimilarName(String publisherName);
	
	Collection<Publisher> getForAutoComplete(String searchString);
}
