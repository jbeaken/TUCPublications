package org.bookmarks.ui;

import java.util.Locale;

import org.bookmarks.domain.StockItem;


public class SearchStockItemsDecorator extends AbstractBookmarksTableDecorator {
	
	protected String getInfo(StockItem stockItem) {
		StringBuilder info = new StringBuilder();
		if(stockItem.isBook()){
			info.append(stockItem.getBinding().getDisplayName() + "/");
		}
		info.append(stockItem.getPublisher().getName()
				+ "/"
				+ stockItem.getType().getDisplayName()
				+ (stockItem.getPublishedDate() == null ? "" : "/" + stockItem.getPublishedDate())
				+ "/ ID : "
				+ stockItem.getId());
		String sales = stockItem.getTwentyTwelveSales();
		if(sales != null && !sales.trim().equals("")) {
			//Messy, just need last figure 
			//which is the total.
			int saleQuantity = 0;
			for(String s : sales.split(",")) {
				Integer local = Integer.parseInt(s);
				saleQuantity = local;
			}
			info.append("/Sales 2012 : " + saleQuantity);
		}

		return info.toString();
	}	
	
	public String getPriceForMarxism() {
		StockItem stockItem = (StockItem)getCurrentRowObject();
		if(stockItem.getQuantityForMarxism() != null) {
			double priceForMarxism =  stockItem.getSellPrice().doubleValue() * stockItem.getQuantityForMarxism().longValue();
			return currencyFormatter.print(priceForMarxism, Locale.UK);
		}
		return "0";
	}
	
	public String getRealQuantityForMarxism() {
		StockItem stockItem = (StockItem)getCurrentRowObject();
		Long quantityForMarxism = stockItem.getQuantityForMarxism() == null ? 0 : stockItem.getQuantityForMarxism().longValue();
		Long realQuantityForMarxism = quantityForMarxism - stockItem.getQuantityInStock().longValue();
		if( realQuantityForMarxism < 0 ) return "0";
		return realQuantityForMarxism.toString();
	}
	
	
	public String getPriceMissingForMarxism() {
		StockItem stockItem = (StockItem)getCurrentRowObject();
		if(stockItem.getQuantityForMarxism() != null) {
			long amountMissing = stockItem.getQuantityForMarxism().longValue() - stockItem.getQuantityInStock().longValue();
			if(amountMissing > 0) {
				double priceForMarxism =  stockItem.getSellPrice().doubleValue() * amountMissing;
				return currencyFormatter.print(priceForMarxism, Locale.UK);
			}
		}
		return "0";
	}	

	public String getInfo() {
		StockItem stockItem = (StockItem)getCurrentRowObject();
		return getInfo(stockItem);
	}
	
	public String getSellPrice() {
		StockItem stockItem = (StockItem)getCurrentRowObject();
		return currencyFormatter.print(stockItem.getSellPrice(), Locale.UK);
	}
	
	public String getPublisherPrice() {
		StockItem stockItem = (StockItem)getCurrentRowObject();
		return getPublisherPrice(stockItem);
	}
	
	public String getPublisherPrice(StockItem stockItem) {
		return currencyFormatter.print(stockItem.getPublisherPrice(), Locale.UK);
	}
	
	public String getPrices() {
		StockItem stockItem = (StockItem)getCurrentRowObject();
		return getPrices(stockItem);
		
	}	
	
	protected String getPrices(StockItem stockItem) {
		return currencyFormatter.print(stockItem.getSellPrice(), Locale.UK) + "/" +
			currencyFormatter.print(stockItem.getPublisherPrice(), Locale.UK) + "/" +
			currencyFormatter.print(stockItem.getCostPrice(), Locale.UK);
	}
	
	public String getQuantities() {
		StockItem stockItem = (StockItem)getCurrentRowObject();
		Long qFm = (stockItem.getQuantityForMarxism() == null ? 0 : stockItem.getQuantityForMarxism());
		return stockItem.getQuantityInStock() + 
				"/" + stockItem.getQuantityOnOrder() + 
				"/" + stockItem.getQuantityForCustomerOrder() +
				"/" + stockItem.getQuantityReadyForCustomer() +
				"/" + qFm;
	}
}
