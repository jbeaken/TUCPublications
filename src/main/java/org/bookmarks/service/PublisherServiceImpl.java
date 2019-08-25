package org.bookmarks.service;

import org.bookmarks.domain.Publisher;
import org.bookmarks.repository.PublisherRepository;
import org.bookmarks.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collection;

@Service
public class PublisherServiceImpl extends AbstractService<Publisher> implements PublisherService {
	
	@Autowired
	private PublisherRepository publisherRepository;

	@Override
	public Repository<Publisher> getRepository() {
		return publisherRepository;
	}

	@Override
	public Collection<Publisher> getForAutoComplete(String searchString) {
		return publisherRepository.getForAutoComplete(searchString);
	}
	
	@Override
	public Publisher findBySimilarName(String publisherName) {
		return publisherRepository.findBySimilarName(publisherName);
	}

}
