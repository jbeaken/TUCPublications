package org.bookmarks.website.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import javax.validation.constraints.NotNull;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;

@Embeddable
public class Publisher {

    @NotNull
    @Column(name="publisher_id")
    @Field(index=Index.NO, store=Store.YES)
    private Long id;
	
    @NotNull
    @Column(name="publisher_name")
    @Field(index=Index.NO, store=Store.YES)
    private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
 
}
