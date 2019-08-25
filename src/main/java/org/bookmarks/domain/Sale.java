package org.bookmarks.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.bookmarks.website.domain.DeliveryType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.NumberFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * sellPrice is snap shot of stockItem.sellPrice as later is variable over time
 * discountedPrice is sellPrice * discount
 * @author Administrator
 *
 */
@Entity
@Table(name="sale")
public class Sale extends AbstractEntity {

@Transient
private Logger logger = LoggerFactory.getLogger(Sale.class);

	//This is messy!!
	@Transient //Could argue it should be persisted, on edit it will have to be overriden again
	private Boolean discountHasBeenOverridden = Boolean.FALSE;

	@Transient
	private Long originalQuantity;

	//Even messier
	@Transient
	private BigDecimal newDiscount;

	@NotNull
	@ManyToOne
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name="stockitem_id")
	private StockItem stockItem;

	@Min(value=0)
	@NotNull
	@NumberFormat(pattern="#.##")
	@Column(name="sellPrice")
	private BigDecimal sellPrice;

	@NotNull
	@Min(value=0)
	@Max(value=100)
	private BigDecimal discount = new BigDecimal(0);

	@Transient
	@Column(name="discountedPrice")
	private BigDecimal discountedPrice;

	@NotNull
	@Min(value=0)
	@Max(value=100)
	private BigDecimal vat;

	@ManyToOne
	@JoinColumn(name="event_id")
	private Event event;

	@Transient
	private BigDecimal vatAmount = new BigDecimal(0);

	@OneToOne(mappedBy="sale")
	private CustomerOrderLine customerOrderLine;


	//Constructors
	public Sale() {
		super();
	}

	public Sale(Long id) {
		this();
		setId(id);
	}

	public Sale(StockItem stockItem) {
		this();
		setStockItem(stockItem);
	}

	public Sale(Date creationDate, Long quantity, BigDecimal sellPrice, String categoryName) {
		this();
		setCreationDate(creationDate);
		setQuantity(quantity);
		setSellPrice(sellPrice);
		StockItem stockItem = new StockItem();
		stockItem.setCategory(new Category(categoryName));
		setStockItem(stockItem);
	}

	//From salerepository search (no group by)
	public Sale(Long id, Date creationDate, Long quantity, BigDecimal discount, BigDecimal sellPrice, String title, String isbn, String publisherName, String eventName) {
		this();
		setId(id);
		setDiscount(discount);
		setCreationDate(creationDate);
		setQuantity(quantity);
		setSellPrice(sellPrice);

		StockItem stockItem = new StockItem(title, isbn);
		Publisher publisher = new Publisher();
		publisher.setName(publisherName);
		stockItem.setPublisher(publisher);
		setStockItem(stockItem);

		Event event = new Event();
		if(eventName != null) {
			event.setName(eventName);
		} else event.setName("Shop");
		setEvent(event);
	}

	//From salerepository search (category search)
	public Sale(Long id, Date creationDate, Long quantity, BigDecimal discount, BigDecimal sellPrice, String title, String isbn, String publisherName, String eventName, String categoryName) {
		this();
		setId(id);
		setDiscount(discount);
		setCreationDate(creationDate);
		setQuantity(quantity);
		setSellPrice(sellPrice);

		StockItem stockItem = new StockItem(title, isbn);
		Publisher publisher = new Publisher();
		Category category = new Category();
		category.setName(categoryName);
		publisher.setName(publisherName);
		stockItem.setPublisher(publisher);
		stockItem.setCategory(category);
		setStockItem(stockItem);

		Event event = new Event();
		if(eventName != null) {
			event.setName(eventName);
		} else event.setName("Shop");
		setEvent(event);




	}

	/** From saleRespository.search (group by)**/
	public Sale(String title, String isbn, Long sumQuantity, BigDecimal sumSellPrice, String publisherName) {
		this();

		StockItem stockItem = new StockItem(title, isbn);
		Publisher publisher = new Publisher();
		publisher.setName(publisherName);
		stockItem.setPublisher(publisher);

		setStockItem(stockItem);
		setQuantity(sumQuantity);
		setSellPrice(sumSellPrice);
	}

	public Sale(Date creationDate) {
		this();
		setCreationDate(creationDate);
	}

	public Sale(StockItem stockItem, Invoice invoice) {
		this();
		setStockItem(stockItem);
		setQuantity(1l);
		if(invoice.getCustomer().getCustomerType() == CustomerType.SISTER) {
			setSellPrice(getStockItem().getPublisherPrice());
		} else {
			setSellPrice(getStockItem().getSellPrice());
		}
//		calculateDiscount(stockItem, invoice);
		setVat(stockItem.getVat());
		//calculate(invoice, true);
	}



	public BigDecimal getNewDiscount() {
		return newDiscount;
	}

	public void setNewDiscount(BigDecimal newDiscount) {
		this.newDiscount = newDiscount;
	}

	public Boolean getDiscountHasBeenOverridden() {
		return discountHasBeenOverridden;
	}

	public void setDiscountHasBeenOverridden(Boolean discountHasBeenOverridden) {
		this.discountHasBeenOverridden = discountHasBeenOverridden;
	}

	public CustomerOrderLine getCustomerOrderLine() {
		return customerOrderLine;
	}

	public void setCustomerOrderLine(CustomerOrderLine customerOrderLine) {
		this.customerOrderLine = customerOrderLine;
	}

	public Long getOriginalQuantity() {
		return originalQuantity;
	}

	public void setOriginalQuantity(Long originalQuantity) {
		this.originalQuantity = originalQuantity;
	}

	public BigDecimal getDiscountedPrice() {
		if(discountedPrice == null) {
			BigDecimal discount = new BigDecimal(100 - getDiscount().intValue()).divide(new BigDecimal(100));
			discountedPrice = getSellPrice().multiply(discount);
		}
		return discountedPrice;
	}

	public void setDiscountedPrice(BigDecimal discountedPrice) {
		this.discountedPrice = discountedPrice;
	}
	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getVat() {
		return vat;
	}

	public void setVat(BigDecimal vat) {
		this.vat = vat;
	}

	public BigDecimal getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(BigDecimal vatAmount) {
		this.vatAmount = vatAmount;
	}

	public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	@NotNull
	private Long quantity;

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

	private void calculateDiscount(StockItem stockItem, Invoice invoice) {
		Customer customer = invoice.getCustomer();
		DeliveryType deliveryType = invoice.getDeliveryType();
		float discount = 0;

		//Check for overrides
		if(stockItem.isBookmarksPublication()) { //Bookmarks
			if(customer.getBookmarksDiscount() != null) {
				logger.info("This is a bookmarks publication and discount has been overriden");
				setDiscount(customer.getBookmarksDiscount());
				return;
			}
		} else {
			if(customer.getNonBookmarksDiscount() != null) {
				logger.info("This is a non-bookmarks publication and discount has been overriden");
				setDiscount(customer.getNonBookmarksDiscount());
				return;
			}
		}

		switch(customer.getCustomerType()) {
			case BOOKMARKS: {
				discount = 20;
				break;
			}
			case SISTER: {
				if(stockItem.isBookmarksPublication()) {
					discount = 40;
				} else { //Non bookmarks, 5% if mailorder (free postage), 10% is collection
					discount = 10;
				}
				break;
			}
			case CENTRE: {
				//If account holder, 20% or 10% and postage
				if(customer.getBookmarksAccount().getAccountHolder() == true) {
					if(deliveryType == DeliveryType.COLLECTION) {
						discount = 20;
					} else {
						discount = 10;
					}
				} else { //not an account holder
					if(deliveryType == DeliveryType.COLLECTION) {
						discount = 10;
					} else {
						discount = 0;
					}
				}
				break;
			}
			case BRANCH_DISTRICT: {
				if(stockItem.isBookmarksPublication()) {
					if(deliveryType == DeliveryType.COLLECTION) {
						discount = 20;
					} else { //Mail Order
						discount = 15;
					}
				} else { //Non bookmarks, 5% if mailorder (free postage), 10% is collection
					if(deliveryType == DeliveryType.COLLECTION) {
						discount = 10;
					} else {
						discount = 5;
					}
				}
				break;
			}
			case CUSTOMER: {
				//0% if mailorder (free postage), 10% is collection but only if paying in over 10 pounds monthly
				if(deliveryType == DeliveryType.COLLECTION && customer.getBookmarksAccount().getAmountPaidInMonthly() != null && customer.getBookmarksAccount().getAmountPaidInMonthly().floatValue() > 9) {
					discount = 10;
				} else {
					discount = 0;
				}
				break;
			}
			case TRADE: {
				//This on publisherPrice unless more than sellPrice
				discount = 40;
				break;
			}
			case INSTITUTION: {
				discount = 10;
				break;
			}
		}
		setDiscount(new BigDecimal(discount));
	}

	void calculate(Invoice invoice, boolean calculateDiscount) {

		if(getDiscountHasBeenOverridden() == Boolean.FALSE && calculateDiscount == true) {
			calculateDiscount(stockItem, invoice);
		}

		if(logger.isDebugEnabled()) {
			logger.debug("StockItem = " + getStockItem().getTitle());
			logger.debug("Publisher = " + (getStockItem().getPublisher()  == null ? null : getStockItem().getPublisher().getId() + " : " + getStockItem().getPublisher().getName() ));
			logger.debug("Discount = " + getDiscount());
		}

		BigDecimal discountedPrice =
				getSellPrice()
				.multiply(new BigDecimal(100).subtract(getDiscount()))
				.divide(new BigDecimal(100));
		setDiscountedPrice(discountedPrice);

		BigDecimal vatAmount =  //Is this vat on sell price or discounted price
				discountedPrice
				.multiply(getStockItem().getVat())
				.divide(new BigDecimal(100));
		setVatAmount(vatAmount);

		if(logger.isDebugEnabled()) {
			logger.debug("In calculate discount for sale");
			logger.debug("StockItem = " + getStockItem().getTitle());
			logger.debug("Discount = " + getDiscount());
			logger.debug("VAT amount = " + getVatAmount());
			logger.debug("Discounted Price = " + getDiscountedPrice());
		}
//		price = price.setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}

	@Override
	public boolean equals(Object obj) {
		Sale that = (Sale) obj;
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

	public BigDecimal getTotalPrice() {
		BigDecimal totalPrice = getSellPrice().multiply(new BigDecimal(getQuantity())).multiply(getPercentileDiscount());
		return totalPrice;
	}

	private BigDecimal getPercentileDiscount() {
		return new BigDecimal(100).subtract(getDiscount()).divide(new BigDecimal(100));
	}

	public boolean isSecondHand() {
		if(getStockItem().getId() == 39625l) return true;
		return false;
	}

}
