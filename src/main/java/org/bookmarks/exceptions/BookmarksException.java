package org.bookmarks.exceptions;

public class BookmarksException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public BookmarksException() {
		super();
	}
	public BookmarksException(String message) {
		super(message);
	}
}
