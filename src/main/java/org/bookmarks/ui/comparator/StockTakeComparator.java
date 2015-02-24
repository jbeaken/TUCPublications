package org.bookmarks.ui.comparator;

import org.bookmarks.domain.StockTakeLine;
import java.util.Comparator;

public class StockTakeComparator implements Comparator {

	public int compare(Object obj1, Object obj2){
		StockTakeLine stockRecord1 = (StockTakeLine)obj1;
		StockTakeLine stockRecord2 = (StockTakeLine)obj2;
		return stockRecord2.getCreationDate().compareTo(stockRecord1.getCreationDate());
	}
}
