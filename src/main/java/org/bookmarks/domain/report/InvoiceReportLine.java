package org.bookmarks.domain.report;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.Sale;
import org.bookmarks.website.domain.DeliveryType;

import org.springframework.format.number.CurrencyStyleFormatter;

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

	public String getRef() {
		//Invoice
		if(!isCredit()) {
			return getSale().getStockItem().getTitle();
		}

		//Credit Note
		if(getCreditNote().getTransactionDescription() == null) {
			return getCreditNote().getNote();
		}

		return getCreditNote().getTransactionDescription();
	}

	public String getIsbn() {
		if(!isCredit()) {
			return getSale().getStockItem().getIsbn();
		}
		return getCreditNote().getTransactionReference();
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

	public String getDiscount() {
		if(!isCredit()) {
			return getSale().getDiscount() + "%";
		}
		return "-";
	}


	public String getDeliveryTypeDisplay() {
		if(getDeliveryType() == null) return "";
		return getDeliveryType().getDisplayName();
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

	public BigDecimal getCredit() {
		if(isCredit()) {
   		return creditNote.getAmount();
		} else {
    	return sale.getTotalPrice().multiply(new BigDecimal(-1));
	}

	}
	public String getCreditAmount(CurrencyStyleFormatter currencyFormatter) {
		if(isCredit()) {
			return currencyFormatter.print(creditNote.getAmount(), Locale.UK);
		} else {
			return "";
		}
	}

	public String getDebitAmount(CurrencyStyleFormatter currencyFormatter) {
		if(isCredit()) {
			return "";
		} else {
			return currencyFormatter.print( sale.getTotalPrice().multiply(new BigDecimal(-1)), Locale.UK);
		}
	}

}
