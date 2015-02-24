package org.bookmarks.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.Collection;
import java.util.HashSet;


@Entity
@Table(name="category")
public class Category extends AbstractNamedEntity {

	public Category() {
		super();
	}
	
	public Category(Long id) {
		this();
		setId(id);
	}	
	
	
	public Category(String name) {
		this();
		setName(name);
	}

	public Category(Long id, String name) {
		this(id);
		setName(name);
	}

	@ManyToOne
	@JoinColumn(name="parent_id")
	private Category parent;

	@OneToMany(fetch=FetchType.EAGER)
	@Cascade({CascadeType.ALL})
	private Collection<Category> children;

	@Column(name="is_on_website")
    private Boolean isOnWebsite = Boolean.TRUE;
    
	@Column(name="is_in_sidebar")
    private Boolean isInSidebar = Boolean.FALSE;
    
    
	public Boolean getIsOnWebsite() {
		return isOnWebsite;
	}

	public void setIsOnWebsite(Boolean isOnWebsite) {
		this.isOnWebsite = isOnWebsite;
	}

	public Boolean getIsInSidebar() {
		return isInSidebar;
	}

	public void setIsInSidebar(Boolean isInSidebar) {
		this.isInSidebar = isInSidebar;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Collection<Category> getChildren() {
		return children;
	}

	public void setChildren(Collection<Category> children) {
		this.children = children;
	}

	public void add(Category category) {
		if(children == null) {
			children = new HashSet<Category>();
		}
		children.add(category);
		
	}	
}