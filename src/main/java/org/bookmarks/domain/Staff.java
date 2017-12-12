package org.bookmarks.domain;

import java.math.BigDecimal;
import java.util.Collection;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.bookmarks.website.domain.Address;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.Length;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="staff")
public class Staff extends AbstractNamedEntity {

	public Staff() {
		super();
	}

	public Staff(Long id) {
		this();
		setId(id);
	}

	public Staff(Long id, String name, String telephone, String email) {
		this(id);
		setName(name);
		setEmail(email);
		setTelephone(telephone);
	}

	public Staff(String name) {
		this();
		setName(name);
	}

	@NotNull
	private String email;

	@NotNull
	private String telephone;

	private String slackHandle;

	public String getSlackHandle() {
		return slackHandle;
	}

	public void setSlackHandle(String slackHandle) {
		this.slackHandle = slackHandle;
	}	

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
