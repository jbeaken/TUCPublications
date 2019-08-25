package org.bookmarks.domain;

public class ABEInfo {
	private String bookseller;
	private String price;
	private String shippingPrice;
	private String description;
	private String quantityAvailable;
	private String addToBasketURL;
	private String viewURL;
	public String getViewURL() {
		return viewURL;
	}
	public void setViewURL(String viewURL) {
		this.viewURL = viewURL;
	}
	private String shippingInfo;
	
	
	public String getShippingInfo() {
		return shippingInfo;
	}
	public void setShippingInfo(String shippingInfo) {
		this.shippingInfo = shippingInfo;
	}
	public String getAddToBasketURL() {
		return addToBasketURL;
	}
	public void setAddToBasketURL(String addToBasketURL) {
		this.addToBasketURL = addToBasketURL;
	}
	public String getQuantityAvailable() {
		return quantityAvailable;
	}
	public void setQuantityAvailable(String quantityAvailable) {
		this.quantityAvailable = quantityAvailable;
	}
	public String getBookseller() {
		return bookseller;
	}
	public void setBookseller(String bookseller) {
		this.bookseller = bookseller;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getShippingPrice() {
		return shippingPrice;
	}
	public void setShippingPrice(String shippingPrice) {
		this.shippingPrice = shippingPrice;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
