package org.bookmarks.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;

@Entity
@Table(name="author")
public class Author extends AbstractEntity {

    @NotNull
    @Size(max = 255)
    @Column(unique = true)
    private String name;
    
    @Size(max = 255)
    @Column(unique = true, name = "az_name")
    private String azName;    

    @ManyToMany(mappedBy="authors")
    private Set<StockItem> stockItems; 

    public Author() {
    	super();
    }
    public Author(Long id, String name) {
		this(name);
		setId(id);
	}

	public Author(String name) {
		this();
		setName(name);
		setAzName(name);
	}
	
	public String getName() {
    	return name;
    }

    public void setName(String name) {
    	this.name = name;
    }
    
	public boolean equals(Object o) {
	    if(o == null)
	        return false;

	    if(!(o instanceof Author))
	        return false;

	    // Our natural key has not been filled
	    // So we must return false;
	    if(this.getName() == null)
	        return false;

	    Author that = (Author) o;
	    if(!(this.getName().equals(that.getName())))
	        return false;


	   return true;
	}

	// default implementation provided by NetBeans
	public int hashcode() {
	    int hash = 3;

	    hash = 47 * hash + ((getName() != null) ? getName().hashCode() : 0);

	    return hash;
	}
	
	public int compareTo(Object o) {
		Author that = (Author) o;
		return this.getName().compareTo(that.getName());
	}

	public Set<StockItem> getStockItems() {
		return stockItems;
	}

	public void setStockItems(Set<StockItem> stockItems) {
		this.stockItems = stockItems;
	}
	
	public String toString() {
		return getId() + " " + getName();
	}

	public String getAzName() {
		return azName;
	}

	public void setAzName(String azName) {
		this.azName = azName;
	}
}
