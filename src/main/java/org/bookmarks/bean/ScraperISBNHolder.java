package org.bookmarks.bean;

public class ScraperISBNHolder {
	
	private String isbn;
	
	private String categoryName;

	public ScraperISBNHolder() {
		super();
	}

	public ScraperISBNHolder(String isbn, String categoryName) {
		this();
		this.categoryName = categoryName;
		this.isbn = isbn;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

}
