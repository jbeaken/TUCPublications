package org.bookmarks.domain;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import javax.persistence.Transient;

@Entity
public class CreditNote extends AbstractEntity {

	@NotNull
	private BigDecimal amount;

	private Date date;

	private TransactionType transactionType;

	private String transactionReference;

	private String transactionDescription;

	@Transient private String status;
	
	@Transient private boolean isClubAccount;

	//For upload of sales csv
	@Transient private MultipartFile file;

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

	public MultipartFile getFile() {
		return file;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
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
}
