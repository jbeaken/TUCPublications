package org.bookmarks.controller;

import org.bookmarks.domain.Author;

public class AuthorSearchBean extends AbstractSearchBean {

	public AuthorSearchBean() {
		super();
		author = new Author();
		author.setName("");
//		setSortColumn("e.startDate");
//		setSortOrder("DESC");
		setPage("1");
	}
	
	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	private Author author;

	@Override
	public void reset() {
		author = new Author();
	}
	
}
