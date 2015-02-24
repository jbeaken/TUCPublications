package org.bookmarks.controller.bean;

import java.util.Date;
import java.util.Locale;

import org.bookmarks.domain.Event;
import org.springframework.format.datetime.DateFormatter;

public class CalendarEvent {
	
	private static DateFormatter dateFormatter = new DateFormatter("yyyy-MM-dd");
	private Long id;
	private String title;
	private Boolean allDay;
	private String start;
	private String end;
	private String url;
	private String color;
	private String textColor;
	
	public CalendarEvent(Event e) {
		this();
		this.id = e.getId();
		this.title = e.getName();
		this.start = dateFormatter.print(e.getStartDate(), Locale.UK);
		this.end = dateFormatter.print(e.getEndDate(), Locale.UK);
		allDay = false;
		color = "red";
		textColor = "white";
	}
	public CalendarEvent() {
		super();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Boolean getAllDay() {
		return allDay;
	}
	public void setAllDay(Boolean allDay) {
		this.allDay = allDay;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getTextColor() {
		return textColor;
	}
	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}

}
