package org.bookmarks.domain;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * @author jack
 */
@Entity
@Table(name="invoiceorderline")
public class InvoiceOrderLine extends DiscountedOrderLine {

	public InvoiceOrderLine() {
		super();
	}

	@OneToOne
	@NotNull
	@Cascade(value={CascadeType.ALL})
	private Sale sale = new Sale();

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}


	public InvoiceOrderLine(StockItem stockItem, Customer customer) {
		this();
		setStockItem(stockItem);
		setSellPrice(getStockItem().getSellPrice());
		BigDecimal discount = calculateDiscount(stockItem, customer);
		setDiscount(discount);
		calculate();
	}

	public InvoiceOrderLine(CustomerOrderLine customerOrderLine,
			Customer customer) {
		this();
		StockItem stockItem = customerOrderLine.getStockItem();
		setStockItem(stockItem);
//		setSellPrice(getStockItem().getSellPrice());
		getSale().setQuantity(customerOrderLine.getAmount());
		BigDecimal discount = calculateDiscount(stockItem, customer);
		setDiscount(discount);
	}

	public void calculate() {
		BigDecimal discountedPrice =
				getSellPrice()
				.multiply(new BigDecimal(100).subtract(getDiscount()))
				.divide(new BigDecimal(100));

		setDiscountedPrice(discountedPrice);

		BigDecimal vatAmount =  //Is this vat on sell price or discounted price
				discountedPrice
				.multiply(getStockItem().getVat())
				.divide(new BigDecimal(100));
//		BigDecimal totalPrice = getDiscountedPrice().multiply(new BigDecimal(getAmount()));

//		sale.setQuantity(getAmount());
		sale.setVat(getStockItem().getVat());
		sale.setSellPrice(getDiscountedPrice());
		sale.setStockItem(getStockItem());
		sale.setVatAmount(vatAmount);
//		price = price.setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}

	private BigDecimal calculateDiscount(StockItem stockItem, Customer customer) {
		float discount = 10;

		switch(customer.getCustomerType()) {
			case BOOKMARKS: {
				discount = 20;
				break;
			}
			case SISTER: {
				//Discount on publisherPrice unless more than sellPrice
				//Pay postage
				discount = 40;
				break;
			}
			case CENTRE: {
				//Free postage and no discount, 10% if collection
				//If account holder, 20% or 10% and postage
				if(customer.getBookmarksAccount().getAccountHolder() == true) {
					discount = 20;
				} else discount = 10;
				break;
			}
			case BRANCH_DISTRICT: {
				//5% if mailorder (free postage), 10% is collection
				discount = 10;
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
		return new BigDecimal(discount);
	}
}
