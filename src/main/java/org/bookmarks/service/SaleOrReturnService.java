package org.bookmarks.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.SaleOrReturn;
import org.bookmarks.domain.SaleOrReturnOrderLine;
import org.bookmarks.domain.SaleOrReturn;

public interface SaleOrReturnService extends Service<SaleOrReturn>{

	void sell(SaleOrReturn saleOrReturn);
	
	void markAsReturn(SaleOrReturn saleOrReturn);
	
//	BigDecimal getPrice(SaleOrReturn saleOrReturn, Map<Long, SaleOrReturnOrderLine> orderLineMap);
	
//	BigDecimal getPrice(SaleOrReturn saleOrReturn);
}
