package org.bookmarks.controller.bean;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.Event;
import org.springframework.format.annotation.DateTimeFormat;

public class ReorderReviewBean {

	public ReorderReviewBean() {
		super();
	}
	
	public ReorderReviewBean(Long supplierId) {
		Supplier supplier = new Supplier();
		supplier.setId(supplierId);
		setSupplier(supplier);
		setIsDateAgnostic(Boolean.TRUE);
	}
	
	private Boolean isDateAgnostic = Boolean.FALSE;
	
	public Boolean getIsDateAgnostic() {
		return isDateAgnostic;
	}
	
	public void setIsDateAgnostic(Boolean isDateAgnostic) {
		this.isDateAgnostic = isDateAgnostic;
	}
	
	@DateTimeFormat(pattern="dd-MMM-yyyy")
	@NotNull
	private Date startDate = new Date();

	@DateTimeFormat(pattern="dd-MMM-yyyy")
	@NotNull
	private Date endDate = new Date();
	
	@Min(value=0)
	@Max(value=23)
	@NotNull
	private Integer startHour = 0;
	
	@Min(value=0)
	@Max(value=59)	
	@NotNull
	private Integer startMinute = 0;

	@Min(value=0)
	@Max(value=23)
	@NotNull
	private Integer endHour = 23;
	
	@Min(value=0)
	@Max(value=59)
	@NotNull
	private Integer endMinute = 59;
	
	@NotNull
	private Supplier supplier;	
	
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

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
		Calendar endCalendar = new GregorianCalendar();
		endCalendar.setTime(endDate);
		endCalendar.set(Calendar.HOUR, getEndHour());
		endCalendar.set(Calendar.MINUTE, getEndMinute());
		
		return endCalendar.getTime();
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
