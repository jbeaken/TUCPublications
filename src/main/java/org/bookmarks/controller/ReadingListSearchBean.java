package org.bookmarks.controller;

import org.bookmarks.domain.ReadingList;

public class ReadingListSearchBean extends AbstractSearchBean {

	private ReadingList readingList = new ReadingList();
	
	
	public ReadingListSearchBean() {
		super();
		setPage("1");
	}
	
	@Override
	public void reset() {
		readingList = new ReadingList();
	}


	public ReadingList getReadingList() {
		return readingList;
	}


	public void setReadingList(ReadingList readingList) {
		this.readingList = readingList;
	}

}
