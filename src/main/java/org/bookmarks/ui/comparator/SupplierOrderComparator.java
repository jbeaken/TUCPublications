package org.bookmarks.ui.comparator;

import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.domain.SupplierOrderLineStatus;

import java.util.Comparator;

public class SupplierOrderComparator implements Comparator {

	public int compare(Object obj1, Object obj2){
		SupplierOrderLine sol1 = (SupplierOrderLine)obj1;
		SupplierOrderLine sol2 = (SupplierOrderLine)obj2;
		String title1 = sol1.getStockItem().getTitle().toLowerCase();
		String title2 = sol2.getStockItem().getTitle().toLowerCase();
		
		if(sol1.getSupplierOrderLineStatus() == SupplierOrderLineStatus.ON_HOLD) {
			if(sol2.getSupplierOrderLineStatus() == SupplierOrderLineStatus.ON_HOLD) {
				return title1.compareTo(title2);
			} else return 1; //sol1
		}
		if(sol2.getSupplierOrderLineStatus() == SupplierOrderLineStatus.READY_TO_SEND) {
			return title1.compareTo(title2);
		} 
		return -1;
	}
}
