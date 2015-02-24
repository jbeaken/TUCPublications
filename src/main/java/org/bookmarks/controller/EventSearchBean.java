package org.bookmarks.controller;

import org.bookmarks.domain.Event;

public class EventSearchBean extends AbstractSearchBean {

	public EventSearchBean() {
		super();
		event = new Event();
		event.setName("");
		setGroupBy(true);
		setSortColumn("e.startDate");
		setSortOrder("DESC");
		setPage("1");
	}
	
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	private Event event;

	@Override
	public void reset() {
		event = new Event();
	}
	
}
