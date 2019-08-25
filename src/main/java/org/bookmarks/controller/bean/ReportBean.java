package org.bookmarks.controller.bean;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.bookmarks.controller.AbstractSearchBean;
import org.bookmarks.domain.SalesReportType;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Event;
import org.bookmarks.domain.StockItem;
import org.springframework.format.annotation.DateTimeFormat;

public abstract class ReportBean extends AbstractSearchBean {



	private Boolean isDateAgnostic = Boolean.FALSE;

	public Boolean getIsDateAgnostic() {
		return isDateAgnostic;
	}

	public void setIsDateAgnostic(Boolean isDateAgnostic) {
		this.isDateAgnostic = isDateAgnostic;
	}

	@DateTimeFormat(pattern="dd-MMM-yyyy")
	private Date startDate;

	@DateTimeFormat(pattern="dd-MMM-yyyy")
	private Date endDate;

	@Min(value=0)
	@Max(value=23)
	private Integer startHour = 0;

	@Min(value=0)
	@Max(value=59)
	private Integer startMinute = 0;

	@Min(value=0)
	@Max(value=23)
	private Integer endHour = 23;

	@Min(value=0)
	@Max(value=59)
	private Integer endMinute = 59;

	public Integer getStartHour() {
		return startHour;
	}

	public void setStartHour(Integer startHour) {
		this.startHour = startHour;
	}

	public Integer getStartMinute() {
		return startMinute;
	}

	public void setStartMinute(Integer startMinute) {
		this.startMinute = startMinute;
	}

	public Integer getEndHour() {
		return endHour;
	}

	public void setEndHour(Integer endHour) {
		this.endHour = endHour;
	}

	public Integer getEndMinute() {
		return endMinute;
	}

	public void setEndMinute(Integer endMinute) {
		this.endMinute = endMinute;
	}


	public Date getStartDate() {
		if(isDateAgnostic || startDate == null) return null;
		Calendar startCalendar = new GregorianCalendar();
		startCalendar.setTime(startDate);
		startCalendar.set(Calendar.HOUR, getStartHour());
		startCalendar.set(Calendar.MINUTE, getStartMinute());

		return startCalendar.getTime();
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		if(isDateAgnostic || endDate == null) return null;
		Calendar endCalendar = new GregorianCalendar();
		endCalendar.setTime(endDate);
		endCalendar.set(Calendar.HOUR, getEndHour());
		endCalendar.set(Calendar.MINUTE, getEndMinute());

		return endCalendar.getTime();
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
