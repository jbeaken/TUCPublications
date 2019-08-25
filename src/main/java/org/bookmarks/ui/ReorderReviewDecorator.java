package org.bookmarks.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.bookmarks.website.domain.Address;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.domain.TelephoneDirectory;
import org.displaytag.decorator.TableDecorator;

public class ReorderReviewDecorator extends AbstractBookmarksTableDecorator {
	DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.UK);
	
	public String getTitle() {
		SupplierOrderLine supplierOrderLine = (SupplierOrderLine) getCurrentRowObject();
			StockItem stockItem = supplierOrderLine.getStockItem();
			return getAnchor("/bookmarks/stock/edit?id=" + stockItem.getId(),
					stockItem.getTitle(),"ISBN", true, false);

		}
	
	public String getStockItemTitle() {
		return "adf";
	}	
	
	public String getAmount() {
		SupplierOrderLine supplierOrderLine = (SupplierOrderLine) getCurrentRowObject();
		return getTextInput(supplierOrderLine.getAmount());
	}

	private String getTextInput(Long value) {
		return "<input type=\"text\" value=\"" +  value + "\" />";
	}
	
}
