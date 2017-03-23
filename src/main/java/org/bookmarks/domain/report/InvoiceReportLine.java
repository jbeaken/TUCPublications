package org.bookmarks.domain.report;

import java.math.BigDecimal;
import java.util.Date;

import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.Sale;
import org.bookmarks.website.domain.DeliveryType;

public class InvoiceReportLine implements Comparable {

	private Sale sale;
	private CreditNote creditNote;
	private BigDecimal currentBalance;
	
	private DeliveryType deliveryType;
	
	public DeliveryType getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(DeliveryType deliveryType) {
		this.deliveryType = deliveryType;
	}
	public BigDecimal getCurrentBalance() {
		return currentBalance;
	}
	public void setCurrentBalance(BigDecimal currentBalance) {
		this.currentBalance = currentBalance;
	}
	public Sale getSale() {
		return sale;
	}
	public void setSale(Sale sale) {
		this.sale = sale;
	}
	public CreditNote getCreditNote() {
		return creditNote;
	}
	public void setCreditNote(CreditNote creditNote) {
		this.creditNote = creditNote;
	}
	
	@Override
	public int compareTo(Object o) {
		InvoiceReportLine that = (InvoiceReportLine) o;

		Date thatCreationDate = that.getDate();
		Date thisCreationDate = getDate();

		return thatCreationDate.compareTo(thisCreationDate);
	}
	
	public Date getDate() {
		if(isCredit()) {
			return getCreditNote().getDate();
		} else {
			return getSale().getCreationDate();
		}
	}
	
	/**
	 * Is this a credit (creditNote not null) or a debit (sale not null)
	 * @return
	 */
	public boolean isCredit() {
		if(getSale() == null) {
			return true;
		} else {
			return false;
		}
		
	}
	public BigDecimal getCreditAmount() {
		if(isCredit()) {
			return creditNote.getAmount();
		} else {
			return sale.getTotalPrice().multiply(new BigDecimal(-1));
		}
	}

}
