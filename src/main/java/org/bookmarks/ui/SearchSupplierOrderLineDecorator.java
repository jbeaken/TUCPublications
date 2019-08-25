package org.bookmarks.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.domain.SupplierOrderLineStatus;
import org.bookmarks.domain.SupplierOrderLineType;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;

public class SearchSupplierOrderLineDecorator extends AbstractBookmarksTableDecorator {
	DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yy hh:mm");

	public String getLink()	{
        SupplierOrderLine supplierOrderLine = (SupplierOrderLine)getCurrentRowObject();
        Long id = supplierOrderLine.getId();
        String text = "Put on hold";
        if(supplierOrderLine.getSupplierOrderLineStatus() == SupplierOrderLineStatus.ON_HOLD) text = "Take off hold";
        String link = getImageAnchor("toggleOnHold?id=" + id,
        				"green-write.gif",
        				text, false)
				+ getImageAnchor("edit?id=" + id,
        				"green-write.gif",
        				"Edit", true)
        		+ getImageAnchor("delete?id=" + id,
        				"green-nuke.gif",
        				"Delete", false)
        		+ showEditNote(supplierOrderLine);
		if(supplierOrderLine.getSupplierOrderLineStatus() == SupplierOrderLineStatus.READY_TO_SEND) link = link + getImageAnchor("sendToSupplier?id=" + id,
        				"green-write.gif",
        				"Send To Supplier", false) ;
        return link;
	}

	public String getSendDate() {
		SupplierOrderLine supplierOrderLine = (SupplierOrderLine)getCurrentRowObject();
		return supplierOrderLine.getSendDate() == null ? "Not Sent" : dateFormat.format(supplierOrderLine.getSendDate());
	}

	public String getType() {
		SupplierOrderLine supplierOrderLine = (SupplierOrderLine)getCurrentRowObject();
		SupplierOrderLineType type = supplierOrderLine.getType();

		if(type == SupplierOrderLineType.CUSTOMER_ORDER) {
			return getAnchor(contextPath  + "/supplierOrderLine/editCustomerOrderLine?id=" + supplierOrderLine.getId(), type.getDisplayName(), "Edit", true, false);
		}
		
		return supplierOrderLine.getType().getDisplayName();
	}

	public String getTitle() {
		SupplierOrderLine supplierOrderLine = (SupplierOrderLine)getCurrentRowObject();
		StockItem stockItem = supplierOrderLine.getStockItem();
		return getStockItemTitleLinkNoAuthor(stockItem, contextPath + "/supplierOrderLine/searchCustomerOrders");
	}

	public String getIsbn() {
		SupplierOrderLine supplierOrderLine = (SupplierOrderLine)getCurrentRowObject();
		StockItem stockItem = supplierOrderLine.getStockItem();
		return getAnchor(contextPath  + "/saleReport/displayStockItemMonthlySaleReport?id=" + stockItem.getId() +"&flow=searchSupplierOrderLines",
				stockItem.getIsbn(), "Show Sales", true, false);
	}

	public String getSupplier() {
		SupplierOrderLine supplierOrderLine = (SupplierOrderLine)getCurrentRowObject();
		Supplier supplier = supplierOrderLine.getSupplier();
		return getAnchor(contextPath  + "/supplier/view?id=" + supplier.getId() +"&flow=searchSupplierOrderLines",
				supplier.getName(),"Supplier", true, false);
	}

	public String getSupplierOrderLineStatus() {
		SupplierOrderLine supplierOrder = (SupplierOrderLine)getCurrentRowObject();
		return supplierOrder.getSupplierOrderLineStatus().getDisplayName();
	}
}
