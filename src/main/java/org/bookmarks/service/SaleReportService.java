package org.bookmarks.service;

import java.util.Collection;

import org.bookmarks.controller.CustomerSearchBean;
import org.bookmarks.controller.InvoiceSearchBean;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.report.bean.CategoryStockTakeBean;
import org.bookmarks.report.bean.PublisherStockTakeBean;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.bookmarks.controller.bean.MonthlySaleReportBean;
import org.bookmarks.controller.bean.SaleReportBean;
import org.bookmarks.controller.bean.SaleTotalBean;
import org.bookmarks.controller.SearchBean;

public interface SaleReportService {

	DefaultPieDataset getCategoryPieDataset(Collection<Sale> sales);

	JFreeChart getCategoryReportPieChart(DefaultPieDataset categoryReportPieDataset);

	JFreeChart getSaleReportBarChart(Long id);

	IntervalXYDataset getIntervalXYDataset(Long id);

	JFreeChart getIntervalXYChart(IntervalXYDataset dataset);

	Collection<CategoryStockTakeBean> getCategoryStockTakeBeans();
	
	Collection<PublisherStockTakeBean> getPublisherStockTakeBeans();
	
	SaleTotalBean getSaleTotalBean(SearchBean searchBean);

	MonthlySaleReportBean getMonthlySaleReportBean(StockItem stockItem);

	DefaultPieDataset getSourcePieDataset(SaleReportBean saleReportBean);

	DefaultCategoryDataset getTimeOfDayBarChartDataset(SaleReportBean saleReportBean);

	JFreeChart getTimeOfDayReportBarChart(DefaultCategoryDataset timeOfDayCategoryDataset);

	Collection<StockItem> unsold(InvoiceSearchBean invoiceSearchBean);

}
