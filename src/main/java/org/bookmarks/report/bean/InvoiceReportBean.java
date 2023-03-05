package org.bookmarks.report.bean;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import org.bookmarks.domain.Category;
import org.bookmarks.domain.Customer;
import org.springframework.format.number.CurrencyStyleFormatter;

public class InvoiceReportBean {
	private BigDecimal total;
	private Double vat;
	private Customer customer;

	protected CurrencyStyleFormatter CurrencyStyleFormatter = new CurrencyStyleFormatter();

	public InvoiceReportBean() {
		super();
	}

	public InvoiceReportBean(Long customerId, String firstName, String lastName, BigDecimal total, Double vat) {
		this.customer = new Customer(customerId, firstName, lastName, "", null);

		this.total = total;
		this.vat = vat == null ? 0.0 : vat;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public String getFormattedTotal() {
		return CurrencyStyleFormatter.print(total, Locale.UK);
	}

	public String getFormattedVat() {
		return CurrencyStyleFormatter.print(vat, Locale.UK);
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public Double getVat() {
		return vat;
	}
	public void setVat(Double vat) {
		this.vat = vat;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

}
