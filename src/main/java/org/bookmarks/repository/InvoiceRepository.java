package org.bookmarks.repository;

import java.util.Collection;
import java.util.List;

import org.bookmarks.controller.InvoiceSearchBean;
import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.controller.bean.CustomerReportBean;
import org.bookmarks.domain.Invoice;
import org.bookmarks.domain.StockItem;
import org.bookmarks.report.bean.InvoiceReportBean;

public interface InvoiceRepository extends Repository<Invoice>{

	Collection<Invoice> getInvoiceReport(CustomerReportBean reportBean);

	Collection<InvoiceReportBean> getInvoiceReportBeans(InvoiceSearchBean invoiceSearchBean);

	List getAllForCsv();
}
