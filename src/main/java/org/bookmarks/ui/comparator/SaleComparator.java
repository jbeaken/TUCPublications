package org.bookmarks.ui.comparator;

import org.bookmarks.domain.Sale;
import java.util.Comparator;

public class SaleComparator implements Comparator {

	public int compare(Object obj1, Object obj2){
		Sale sale1 = (Sale)obj1;
		Sale sale2 = (Sale)obj2;
		return sale2.getCreationDate().compareTo(sale1.getCreationDate());
	}
}
