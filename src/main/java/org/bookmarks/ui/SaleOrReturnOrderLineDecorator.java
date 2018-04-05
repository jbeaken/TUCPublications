package org.bookmarks.ui;

import java.util.Locale;

import org.bookmarks.domain.SaleOrReturnOrderLine;
import org.springframework.format.number.CurrencyStyleFormatter;
import org.springframework.format.number.PercentStyleFormatter;

public class SaleOrReturnOrderLineDecorator extends SearchStockItemsDecorator {
	protected CurrencyStyleFormatter CurrencyStyleFormatter = new CurrencyStyleFormatter();
	protected PercentStyleFormatter PercentStyleFormatter = new PercentStyleFormatter();
	
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
		return CurrencyStyleFormatter.print(saleOrReturnOrderLine.getPrice(), Locale.UK);
	}
	
	public String getSellPrice() {
		SaleOrReturnOrderLine saleOrReturnOrderLine = (SaleOrReturnOrderLine)getCurrentRowObject();
		return CurrencyStyleFormatter.print(saleOrReturnOrderLine.getSellPrice(), Locale.UK);
	}
	
	public String getStockItemTitle() {
		SaleOrReturnOrderLine saleOrReturnOrderLine = (SaleOrReturnOrderLine)getCurrentRowObject();
		return saleOrReturnOrderLine.getStockItem().getTitle();
	}	
}
