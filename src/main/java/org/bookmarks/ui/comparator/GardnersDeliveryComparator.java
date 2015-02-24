package org.bookmarks.ui.comparator;

import org.bookmarks.domain.SupplierDeliveryLine;
import java.util.Comparator;

public class GardnersDeliveryComparator implements Comparator {

	public int compare(Object obj1, Object obj2){
		SupplierDeliveryLine sdl1 = (SupplierDeliveryLine)obj1;
		SupplierDeliveryLine sdl2 = (SupplierDeliveryLine)obj2;
		String title1 = removeTheAndAnd(sdl1.getStockItem().getTitle().toLowerCase());
		String title2 = removeTheAndAnd(sdl2.getStockItem().getTitle().toLowerCase());
		return title1.compareTo(title2);
	}

	private String removeTheAndAnd(String s) {
		if(s.startsWith("the ")){
			return s.substring(4);
		} else if(s.startsWith("a ")){
			return s.substring(2);
		}
		return s;
	}
}
