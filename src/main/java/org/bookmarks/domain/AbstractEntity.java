package org.bookmarks.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@MappedSuperclass
public abstract class AbstractEntity  implements Entity, Comparable {

	public AbstractEntity() {
		super();
	}
	
	public AbstractEntity(Long id) {
		this();
		this.id = id;
	}
	
	@Id
	@GeneratedValue
	private Long id;
	
//	@Max(value=255)
	private String note;
	
	@NotNull
	@DateTimeFormat 
	@Column(name="creationDate")
	private Date creationDate = new Date();
	
	public String getNote() {
		if(note != null) {
			if(note.trim().equals("")) {
				setNote(null);
			}
		}
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int compareTo(Object that) {
		// Default to comparing ids
		AbstractEntity abstractEntity = (AbstractEntity) that;
//		if(this.getId() == null || abstractEntity.getId() == null) {
//			return 0;
//		}
		return this.getId().compareTo(abstractEntity.getId());
	}
	
	@Override
	public boolean equals(Object that) {
		// Default to comparing ids
		AbstractEntity abstractEntity = (AbstractEntity) that;
		if(this.getId() == null || abstractEntity.getId() == null) {
			return false;
		}
		if(this.getId().longValue() == abstractEntity.getId().longValue())
			return true;
		return false;
	}

	
}
