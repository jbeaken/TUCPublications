package org.bookmarks.ui.comparator;

import org.bookmarks.domain.SupplierDeliveryLine;
import java.util.Comparator;

public class GardnersDeliveryComparator implements Comparator {

	public int compare(Object obj1, Object obj2){
		SupplierDeliveryLine sdl1 = (SupplierDeliveryLine)obj1;
		SupplierDeliveryLine sdl2 = (SupplierDeliveryLine)obj2;

		String title1 = prepare(sdl1.getStockItem().getTitle().toLowerCase());
		String title2 = prepare(sdl2.getStockItem().getTitle().toLowerCase());
		
		return title1.compareTo(title2);
	}

/**
* Remove leading The or 'a'
* Remove apostraphes (I've -> Ive)
**/
	private String prepare(String s) {

		if(s.startsWith("the ")){
			s = s.substring(4);
		} else if(s.startsWith("a ")){
			return s.substring(2);
		}

		s = s.replace("\'", "");
		return s;
	}
}
