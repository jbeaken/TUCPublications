package org.bookmarks.ui;

import java.util.Locale;

import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.SaleOrReturnOrderLine;
import org.bookmarks.domain.StockItem;
import org.displaytag.decorator.TableDecorator;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.number.CurrencyFormatter;
import org.springframework.format.number.PercentFormatter;

public class SaleOrReturnOrderLineDecorator extends SearchStockItemsDecorator {
	protected CurrencyFormatter currencyFormatter = new CurrencyFormatter();
	protected PercentFormatter percentFormatter = new PercentFormatter();
	
	public String getLink()	{
        SaleOrReturnOrderLine saleOrReturnOrderLine = (SaleOrReturnOrderLine)getCurrentRowObject();
        Long id = saleOrReturnOrderLine.getStockItem().getId();

        return 	getImageAnchor("editSaleOrReturnOrderLine?id=" + id, 
        				"write_medium.png", 
        				"Edit", false)
        		+ getImageAnchor("deleteSaleOrReturnOrderLine?id=" + id, 
        				"delete_medium.png", 
        				"Delete", false);
	}
	
	public String getPrice() {
		SaleOrReturnOrderLine saleOrReturnOrderLine = (SaleOrReturnOrderLine)getCurrentRowObject();
		return currencyFormatter.print(saleOrReturnOrderLine.getPrice(), Locale.UK);
	}
	
	public String getSellPrice() {
		SaleOrReturnOrderLine saleOrReturnOrderLine = (SaleOrReturnOrderLine)getCurrentRowObject();
		return currencyFormatter.print(saleOrReturnOrderLine.getSellPrice(), Locale.UK);
	}
	
	public String getStockItemTitle() {
		SaleOrReturnOrderLine saleOrReturnOrderLine = (SaleOrReturnOrderLine)getCurrentRowObject();
		return saleOrReturnOrderLine.getStockItem().getTitle();
	}	
}
