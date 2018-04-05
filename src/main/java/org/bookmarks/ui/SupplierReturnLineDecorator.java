package org.bookmarks.ui;

import java.util.Locale;

import org.bookmarks.domain.SupplierReturnLine;
import org.bookmarks.domain.SupplierReturnLine;

public class SupplierReturnLineDecorator extends SearchStockItemsDecorator {

	public String getLink()	{
        SupplierReturnLine supplierReturnLine = (SupplierReturnLine)getCurrentRowObject();
        return  showView(supplierReturnLine) + showEdit(supplierReturnLine)	+ showDelete(supplierReturnLine);
	}

	public String getTitle() {
		SupplierReturnLine supplierReturnLine = (SupplierReturnLine)getCurrentRowObject();
		return getStockTitleLink(supplierReturnLine.getStockItem(), "editSupplierOrderLine");
	}

	private String showEdit(SupplierReturnLine supplierReturnLine) {
		Long stockItemid = supplierReturnLine.getStockItem().getId();
		return getImageAnchor(contextPath + "/supplierReturn/editSupplierReturnOrderLine?id=" + stockItemid,
				"write_medium.png",
				"Edit", false);
	}

	private  String showDelete(SupplierReturnLine supplierReturnLine) {
		Long stockItemid = supplierReturnLine.getStockItem().getId();
		return getImageAnchor(contextPath + "/supplierReturn/deleteSupplierReturnOrderLine?id=" + stockItemid,
				"delete_medium.png",
				"Delete", false);
	}

	public String getPublisherPrice() {
		SupplierReturnLine supplierReturnLine = (SupplierReturnLine)getCurrentRowObject();
		return CurrencyStyleFormatter.print(supplierReturnLine.getStockItem().getPublisherPrice(), Locale.UK);
	}

	public String getSellPrice() {
		SupplierReturnLine supplierReturnLine = (SupplierReturnLine)getCurrentRowObject();
		return CurrencyStyleFormatter.print(supplierReturnLine.getStockItem().getSellPrice(), Locale.UK);
	}

	public String getTotalPrice() {
		SupplierReturnLine supplierReturnLine = (SupplierReturnLine) getCurrentRowObject();
		return CurrencyStyleFormatter.print(supplierReturnLine.getAmount() * supplierReturnLine.getStockItem().getSellPrice().doubleValue(), Locale.UK);
	}
}
