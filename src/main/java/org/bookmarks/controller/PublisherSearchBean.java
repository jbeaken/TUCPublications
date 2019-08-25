package org.bookmarks.controller;

import org.bookmarks.domain.Publisher;

public class PublisherSearchBean extends AbstractSearchBean {

	public PublisherSearchBean() {
		super();
		publisher = new Publisher();
	}
	
	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	private Publisher publisher;

	@Override
	public void reset() {
		publisher = new Publisher();
	}
	
}
