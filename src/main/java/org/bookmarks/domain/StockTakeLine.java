package org.bookmarks.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.NumberFormat;


@Entity
public class StockTakeLine extends AbstractEntity {

	public StockTakeLine() {
		super();
	}

	public StockTakeLine(Long id) {
		this();
		setId(id);
	}
	//new StockTakeLine(stl.id, stl.quantity, s.isbn, s.title)
	public StockTakeLine(Long id, Long quantity, String isbn, String title) {
		this(id);
		setQuantity(quantity);
		StockItem stockItem = new StockItem();
		stockItem.setIsbn(isbn);
		stockItem.setTitle(title);
		setStockItem(stockItem);
	}


	public StockTakeLine(StockItem stockItem) {
		this();
		setStockItem(stockItem);
		setQuantity(1l);
	}

	@OneToOne
	private StockItem stockItem;

	private Date dateOfUpdate;

	@NotNull
	private Long quantity;

	@Transient
	private Long amountToIncrement = 0l;

	public Date getDateOfUpdate() {
		return dateOfUpdate;
	}

	public void setDateOfUpdate(Date dateOfUpdate) {
		this.dateOfUpdate = dateOfUpdate;
	}

	public Long getAmountToIncrement() {
		return amountToIncrement;
	}

	public void setAmountToIncrement(Long amountToIncrement) {
		this.amountToIncrement = amountToIncrement;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public StockItem getStockItem() {
		return stockItem;
	}

	public void setStockItem(StockItem stockItem) {
		this.stockItem = stockItem;
	}

	@Override
	public boolean equals(Object obj) {
		StockTakeLine that = (StockTakeLine) obj;
		if(this.getId() == null && that.getId() == null) {
			if(this.getStockItem().getId().equals(that.getStockItem().getId()))
				return true;
			return false;
		}

		if(this.getId().equals(that.getId())) return true;
		return false;
	}

	@Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        } else {
            return super.hashCode();
        }
    }
}
