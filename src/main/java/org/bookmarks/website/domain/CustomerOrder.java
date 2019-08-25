package org.bookmarks.website.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class CustomerOrder {

	private WebsiteCustomer customer;

	public CustomerOrder() {
		customer = new WebsiteCustomer();
		customer.setOrders(new HashSet<OrderLine>());
	}

    public WebsiteCustomer getCustomer() {
		return customer;
	}

	public void setCustomer(WebsiteCustomer customer) {
		this.customer = customer;
	}

	public void addOrderLine(StockItem stockItem) {
		OrderLine orderLine = null;
		Set<OrderLine> orderLines = customer.getOrders();

		//Check not already been added
		for(OrderLine ol : orderLines) {
			if(ol.getStockItem().getId().equals(stockItem.getId())) {
				orderLine = ol;
				break;
			}
		}
		if(orderLine == null) {
			//New stockitem, so create new orderline and add to order
			orderLine = new OrderLine();


			orderLine.setStockItem(stockItem);
			orderLine.setQuantity(1);

			//TO-DO, if second hand (but no OUT_OF_PRINT)
			orderLine.setSellPrice(stockItem.getSellPrice());

			//Is it second hand
			if(stockItem.getAvailability() == Availability.OUT_OF_PRINT) {
				orderLine.setIsSecondHand(true);
			}
			orderLine.setPostage(stockItem.getPostage());

			orderLines.add(orderLine);
		} else {
			//Already been added, so just increment
			orderLine.incrementQuantity(1);
		}
	}

	public void removeOrderLine(Long stockItemId) {
		OrderLine toRemove = null;
		for(OrderLine orderLine : customer.getOrders()) {
			if(orderLine.getStockItem().getId().equals(stockItemId)) {
				toRemove = orderLine;
			}
		}
		customer.getOrders().remove(toRemove);
	}

	public BigDecimal getTotalPrice() {
		return getStockPrice().add(getPostage()); //TODO means postage is called twice!
	}

	public BigDecimal getStockPrice() {
		BigDecimal stockPrice = new BigDecimal(0);
		for(OrderLine line : customer.getOrders()) {
			stockPrice = stockPrice.add(line.getTotalPrice());
		}
		return stockPrice;
	}

	public BigDecimal getPostage() {

		boolean isOverseas = getIsOverseas();

		if(isOverseas) {
			return getStockPrice().multiply(new BigDecimal( 0.25 ));
		}

		BigDecimal postage = null;

		for(OrderLine ol : customer.getOrders()) {
			postage = new BigDecimal( 2.0 ).add(ol.getStockItem().getPostage().multiply(new BigDecimal(ol.getQuantity())));
		}

		//Maximum of 6 pounds
		if(postage.floatValue() > 6) {
			return new BigDecimal(6);
		}

		return postage;
	}

	private boolean getIsOverseas() {
		Address address = customer.getAddress();
		String country = address.getCountry();

		if(country == null) return false;

		if(country.equals("United Kingdom")) {
			return false;
		}

		return true;
	}

	public void updateQuantity(Long stockItemId, Integer quantity) {
		for(OrderLine orderLine : customer.getOrders()) {
			if(orderLine.getStockItem().getId().equals(stockItemId)) {
				orderLine.setQuantity(quantity);
			}
		}

	}
}
