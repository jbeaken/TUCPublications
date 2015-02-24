package org.bookmarks.controller;

import org.bookmarks.domain.Category;

public class CategorySearchBean extends AbstractSearchBean {

	public CategorySearchBean() {
		super();
		category = new Category();
	}
	
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	private Category category;

	@Override
	public void reset() {
		category = new Category();
	}
	
}
