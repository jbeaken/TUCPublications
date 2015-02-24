package org.bookmarks.ui;

import java.math.BigDecimal;
import java.util.Locale;

import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.displaytag.decorator.TableDecorator;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.number.CurrencyFormatter;
import org.springframework.format.number.PercentFormatter;

public class InvoiceOrderLineDecorator extends SearchStockItemsDecorator {
	protected CurrencyFormatter currencyFormatter = new CurrencyFormatter();
	protected PercentFormatter percentFormatter = new PercentFormatter();
	
	public String getLink()	{
        Sale sale = (Sale)getCurrentRowObject();
        Long id = sale.getStockItem().getId();

        return 	getImageAnchor(contextPath + "/invoice/editInvoiceOrderLine?id=" + id, 
        				"edit_green.png", 
        				"Edit", false)
        		+ getImageAnchor(contextPath + "/invoice/deleteInvoiceOrderLine?id=" + id, 
        				"delete_green.png", 
        				"Delete", false);
	}
	
	public String getTotalPrice() {
		Sale sale = (Sale)getCurrentRowObject();
		return currencyFormatter.print(sale.getDiscountedPrice().multiply(new BigDecimal(sale.getQuantity())), Locale.UK);
	}
	
	public String getVatAmount() {
		Sale sale = (Sale)getCurrentRowObject();
		return currencyFormatter.print(sale.getVatAmount(), Locale.UK);
	}
	
	public String getVat() {
		Sale sale = (Sale)getCurrentRowObject();
		return sale.getVat() + "%";
	}
	
	public String getSellPrice() {
		Sale sale = (Sale)getCurrentRowObject();
		return currencyFormatter.print(sale.getSellPrice(), Locale.UK);
	}
	
	public String getDiscountedPrice() {
		Sale sale = (Sale)getCurrentRowObject();
		return currencyFormatter.print(sale.getDiscountedPrice(), Locale.UK);
	}
	
	public String getDiscount() {
		Sale sale = (Sale)getCurrentRowObject();
		return sale.getDiscount() + "%";
	}
	
	public String getStockItemTitle() {
		Sale sale = (Sale)getCurrentRowObject();
		return sale.getStockItem().getTitle();
	}	
}
