package org.bookmarks.domain;

import java.util.Date;

import org.bookmarks.bean.VTTransactionStatus;
import org.bookmarks.bean.VTTransactionType;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import javax.persistence.Transient;

@Entity
public class VTTransaction extends AbstractEntity {

	private VTTransactionType type;
	
	private VTTransactionStatus status;

	private String primaryAccount;
	
	private String details;
	
	private Float total;

	public VTTransactionType getType() {
		return type;
	}

	public void setType(VTTransactionType type) {
		this.type = type;
	}

	public String getPrimaryAccount() {
		return primaryAccount;
	}

	public void setPrimaryAccount(String primaryAccount) {
		this.primaryAccount = primaryAccount;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Float getTotal() {
		return total;
	}

	public void setTotal(Float total) {
		this.total = total;
	}

	public VTTransactionStatus getStatus() {
		return status;
	}

	public void setStatus(VTTransactionStatus status) {
		this.status = status;
	}
}
