package org.bookmarks.bean;

public class ScraperUrl {
	
	private String url;
	
	private String categoryName;

	public ScraperUrl(String url, String categoryName) {
		super();
		this.url = url;
		this.categoryName = categoryName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

}
