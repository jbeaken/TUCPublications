package org.bookmarks.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bookmarks.controller.InvoiceSearchBean;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.Event;
import org.bookmarks.domain.Invoice;
import org.bookmarks.domain.InvoiceOrderLine;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.report.bean.InvoiceReportBean;

public interface InvoiceService extends Service<Invoice>{
	void addStockItem(CustomerOrderLine col, Map<Long, Sale> orderLineMap, Invoice invoice);

	void save(Invoice invoice, Collection<Sale> values, Map<Long, CustomerOrderLine> customerOrderLineMap, Event event);

	void addStockItem(StockItem stockItem, Map<Long, Sale> orderLineMap, Invoice invoice);

	Invoice getNewInvoice(Long customerId);

	void addStockItem(Long stockItemId, Map<Long, Sale> orderLineMap, Invoice invoice);

	void addCustomerOrderLine(CustomerOrderLine customerOrderLine, Map<Long, Sale> orderLineMap,
			Invoice invoice);

	void update(Invoice invoice, Collection<Sale> sales,
			Map<Long, CustomerOrderLine> customerOrderLineMap, BigDecimal originalInvoicePrice);

	Collection<InvoiceReportBean> getInvoiceReportBeans(InvoiceSearchBean invoiceSearchBean);

	List<String[]> getAllForCsv();
}
