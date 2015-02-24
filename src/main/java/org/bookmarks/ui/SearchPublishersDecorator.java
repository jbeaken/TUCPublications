package org.bookmarks.ui;

import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.Publisher;

public class SearchPublishersDecorator extends AbstractBookmarksTableDecorator{
	public String getLink()	{
	        Publisher publisher = (Publisher)getCurrentRowObject();
	        return showView(publisher)        				
	        		+ showEdit(publisher)        				
	        		+ showEditNote(publisher);
	}
	
	public String getAddress() {
		Publisher publisher = (Publisher)getCurrentRowObject();
		return getAddress(publisher.getAddress());
	}
	
	public String getTelephone() {
		Publisher publisher = (Publisher)getCurrentRowObject();
		if(publisher.getTelephone1() == null || publisher.getTelephone1().isEmpty()) {
			return "No telephone number";
		}
		return publisher.getTelephone1();
	}
	
	public String getContactName() {
		Publisher publisher = (Publisher)getCurrentRowObject();
		return getContactName(publisher.getContactName());
	}
	
	public String getEmail() {
		Publisher publisher = (Publisher)getCurrentRowObject();
		return getEmail(publisher.getEmail());
	}	
}
