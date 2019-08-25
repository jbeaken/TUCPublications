package org.bookmarks.domain;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Transient;

@Entity
public class CreditNote extends AbstractEntity {

	@NotNull
	private BigDecimal amount;

	@NotNull
	@DateTimeFormat(pattern="dd/MM/yy")
	private Date date;

	private TransactionType transactionType;

	private String transactionReference;

	private String transactionDescription;

	@Transient private String status;
	
	@Transient private boolean isClubAccount;

	@ManyToOne
	@NotNull
	private Customer customer;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public CreditNote() {
		super();
	}
	public CreditNote(Customer customer) {
		this();
		setCustomer(customer);
		setAmount(customer.getCreditAmount());
	}

	public CreditNote(Long customerId) {
		this();
		Customer customer = new Customer(customerId);
		setCustomer(customer);
	}

	public String getTransactionDescription() {
		return transactionDescription;
	}

	public void setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
	}
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setTransactionReference(String transactionReference) {
		this.transactionReference = transactionReference;
	}

	public String getTransactionReference() {
		return transactionReference;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public boolean isClubAccount() {
		return isClubAccount;
	}

	public void setClubAccount(boolean isClubAccount) {
		this.isClubAccount = isClubAccount;
	}

	@Override
	public String toString() {
		return "CreditNote [amount=" + amount + ", date=" + date + ", transactionType=" + transactionType + ", transactionReference=" + transactionReference + ", transactionDescription=" + transactionDescription + ", status=" + status + ", isClubAccount=" + isClubAccount + ", customer=" + customer + "]";
	}
}
