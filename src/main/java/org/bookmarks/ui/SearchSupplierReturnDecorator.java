package org.bookmarks.ui;

import org.bookmarks.domain.SupplierReturn;
import org.bookmarks.domain.SupplierReturnStatus;


public class SearchSupplierReturnDecorator extends AbstractBookmarksTableDecorator {


	public String getLink()	{
        SupplierReturn supplierReturn = (SupplierReturn)getCurrentRowObject();
				String link = showView(supplierReturn)
        		+ showEditNote(supplierReturn);

			if(supplierReturn.getStatus() == SupplierReturnStatus.ON_SHELVES) {
					link = link +  getImageAnchor("sendToSupplier?id=" + supplierReturn.getId(),
						"printer.png", "Send To Supplier", true, false)	+ showEdit(supplierReturn, false);
			}

			if(supplierReturn.getStatus() == SupplierReturnStatus.AWAITING_CREDIT) {
					link = link +  getImageAnchor("markAsReceivedCredit?id=" + supplierReturn.getId(),
						"printer.png", "Received Credit", true, false) + showEdit(supplierReturn, false);
			}

			return link;
	}

	public String getStatus() {
		SupplierReturn supplierReturn = (SupplierReturn)getCurrentRowObject();
		return supplierReturn.getStatus().getDisplayName();
	}
}
