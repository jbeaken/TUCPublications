package org.bookmarks.service;

import java.util.Collection;

import org.bookmarks.controller.CustomerSearchBean;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.report.InvoiceReportLine;
import org.bookmarks.report.bean.CategoryStockTakeBean;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.IntervalXYDataset;

import org.bookmarks.controller.bean.MonthlySaleReportBean;
import org.bookmarks.controller.bean.CustomerReportBean;
import org.bookmarks.controller.bean.SaleReportBean;
import org.bookmarks.controller.bean.SaleTotalBean;
import org.bookmarks.controller.SearchBean;

public interface CustomerReportService {

	Collection<InvoiceReportLine> getInvoiceReport(CustomerReportBean reportBean);

}
