package org.bookmarks.controller;

import java.awt.Color;
import java.awt.Composite;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bookmarks.controller.bean.MonthlySaleReportBean;
import org.bookmarks.controller.bean.CustomerReportBean;
import org.bookmarks.controller.bean.SaleReportBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.Invoice;
import org.bookmarks.domain.SalesReportType;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.Event;
import org.bookmarks.domain.StockItemType;
import org.bookmarks.controller.bean.SaleTotalBean;
import org.bookmarks.controller.validation.SaleReportBeanValidator;
import org.bookmarks.domain.StockItem;
import org.bookmarks.report.bean.CategoryStockTakeBean;
import org.bookmarks.report.bean.InvoiceReportBean;
import org.bookmarks.report.bean.PublisherStockTakeBean;
import org.bookmarks.service.CustomerOrderService;
import org.bookmarks.service.CustomerService;
import org.bookmarks.service.InvoiceService;
import org.bookmarks.service.SaleReportService;
import org.bookmarks.service.SaleService;
import org.bookmarks.service.StockItemService;
import org.bookmarks.service.Service;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.hibernate.validator.constraints.NotEmpty;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/saleReport")
public class SaleReportController extends AbstractBookmarksController {
	
	@Autowired
	private SaleReportService saleReportService;
	
	@Autowired
	private SaleService saleService;


	@Autowired
	private InvoiceService invoiceService;
	
	@Autowired
	private StockItemService stockItemService;	
	
	@Autowired
	private SaleReportBeanValidator saleReportBeanValidator;
	
	/**
	 * Main entry point, analyses report bean to see which report to call
	 * @param saleReportBean
	 * @param bindingResult
	 * @param request
	 * @param session
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/report")
	public String report(@Valid SaleReportBean saleReportBean, BindingResult bindingResult, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		saleReportBeanValidator.validate(saleReportBean, bindingResult);
		if(bindingResult.hasErrors()) {
			modelMap.addAttribute(saleReportBean);
			modelMap.addAttribute(getPublishers());
			modelMap.addAttribute(getCategories());
			return "salesReport";
		}		
		session.setAttribute("saleReportBean", saleReportBean);
		
		switch (saleReportBean.getSalesReportType()) {
			case SALE_LIST:
				return saleListReport(saleReportBean, request, modelMap);
			case BY_CATEGORY:
				return categoryReport(saleReportBean, request, session, modelMap);
			case BY_SOURCE:
				return sourceReport(saleReportBean, request, session, modelMap);	
			case CATEGORY_STOCK_TAKE:
				return categoryStockTakeReport(saleReportBean, request, session, modelMap);
			case TIME_OF_DAY:
				return timeOfDayReport(saleReportBean, request, session, modelMap);
			case INVOICE:
				return invoice(saleReportBean, request, modelMap);
			case UNSOLD:
				return unsold(saleReportBean, request, modelMap);
			case PUBLISHER_STOCK_TAKE:
				return publisherStockTakeReport(saleReportBean, request, session, modelMap);				
			default:
				return null;
		}		
	}

	private String timeOfDayReport(SaleReportBean saleReportBean, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		DefaultCategoryDataset timeOfDayCategoryDataset = saleReportService.getTimeOfDayBarChartDataset(saleReportBean);
		session.setAttribute("timeOfDayCategoryDataset", timeOfDayCategoryDataset);
		modelMap.addAttribute("showTimeOfDayReport", true);
		fillModel(modelMap);
		return "salesReport";
	}
	


	@RequestMapping(value="/saleReport.png")
	@ResponseBody
	public void saleReport(Long id, String duration, OutputStream stream) throws Exception {
		JFreeChart chart = saleReportService.getSaleReportBarChart(id);
		ChartUtilities.writeChartAsPNG(stream, chart, 900, 400);
	}
	
	@RequestMapping(value="/displayStockItemMonthlySaleReport")
	public String displayStockItemMonthlySaleReport(Long id, ModelMap modelMap) throws Exception {
		StockItem stockItem = stockItemService.get(id);
		MonthlySaleReportBean monthlySaleReportBean = saleReportService.getMonthlySaleReportBean(stockItem);
		modelMap.addAttribute(monthlySaleReportBean); 
		return "displayStockItemMonthlySaleReport";
	}	
	
	@RequestMapping(value="/delete")
	public String delete(Long id, HttpSession session, HttpServletRequest request, ModelMap modelMap) throws Exception {
		Sale sale = saleService.get(id);
		saleService.delete(sale);
		SaleReportBean saleReportBean = (SaleReportBean)session.getAttribute("saleReportBean");
		return saleListReport(saleReportBean, request, modelMap);
	}	
	
	
	@RequestMapping(value="/edit")
	public String edit(Long id, HttpSession session, HttpServletRequest request, ModelMap modelMap) throws Exception {
		Sale sale = saleService.get(id);
		sale.setOriginalQuantity(sale.getQuantity());

		modelMap.addAttribute(sale);
		return "editSale";
	}		
	
	@RequestMapping(value="/categoryReport.png")
	@ResponseBody
	public void categoryReport(OutputStream stream, HttpSession session) throws Exception {
		// Get pie dataset stored in session earlier and create a chart to render
		DefaultPieDataset categoryReportPieDataset = (DefaultPieDataset) session.getAttribute("categoryReportPieDataset");
		
		JFreeChart chart = saleReportService.getCategoryReportPieChart(categoryReportPieDataset);
		
		ChartUtilities.writeChartAsPNG(stream, chart, 900, 400);
		
		session.removeAttribute("categoryReportPieDataset");
	}	
	
	@RequestMapping(value="/showTimeOfDayReport.png")
	@ResponseBody
	public void timeOfDayReport(OutputStream stream, HttpSession session) throws Exception {
		DefaultCategoryDataset timeOfDayCategoryDataset = (DefaultCategoryDataset) session.getAttribute("timeOfDayCategoryDataset");
		
		JFreeChart chart = saleReportService.getTimeOfDayReportBarChart(timeOfDayCategoryDataset);
		
		ChartUtilities.writeChartAsPNG(stream, chart, 900, 400);
		
		session.removeAttribute("timeOfDayCategoryDataset");
	}
	
	/**
	 * Report for invoice : has to be a mapping as well due to pagination and sorting
	 */
	@RequestMapping(value="/unsold")
	public String unsold(SaleReportBean saleReportBean, HttpServletRequest request, ModelMap modelMap) {
		setPaginationFromRequest(saleReportBean, request); 
		
		//Get a invoice search bean
		InvoiceSearchBean invoiceSearchBean = new InvoiceSearchBean();
		invoiceSearchBean.setEndDate(saleReportBean.getEndDate());
		invoiceSearchBean.setStartDate(saleReportBean.getStartDate());
		
		//Don't like, fix for shitty export
		setPageSize(saleReportBean, modelMap, saleReportBean.getSearchResultCount());
		
		Collection<StockItem> stockItems = saleReportService.unsold(invoiceSearchBean);
		
		modelMap.addAttribute(stockItems);
		modelMap.addAttribute(saleReportBean);
		modelMap.addAttribute(getCategories());
		modelMap.addAttribute(getPublishers());
		
		return "salesReport";
	}		
	
	/**
	 * Report for invoice : has to be a mapping as well due to pagination and sorting
	 */
	@RequestMapping(value="/invoice")
	public String invoice(SaleReportBean saleReportBean, HttpServletRequest request, ModelMap modelMap) {
		setPaginationFromRequest(saleReportBean, request); 
		
		//Get a invoice search bean
		InvoiceSearchBean invoiceSearchBean = new InvoiceSearchBean();
		invoiceSearchBean.setEndDate(saleReportBean.getEndDate());
		invoiceSearchBean.setStartDate(saleReportBean.getStartDate());
		
		//Don't like, fix for shitty export
		setPageSize(saleReportBean, modelMap, saleReportBean.getSearchResultCount());
		
		Collection<InvoiceReportBean> invoiceReportBeans = invoiceService.getInvoiceReportBeans(invoiceSearchBean);
		
		modelMap.addAttribute(invoiceReportBeans);
		modelMap.addAttribute(saleReportBean);
		modelMap.addAttribute(getCategories());
		modelMap.addAttribute(getPublishers());
		
		return "salesReport";
	}		
	
	/**
	 * Report for SaleList : has to be a mapping as well due to pagination and sorting
	 */
	@RequestMapping(value="/saleList")
	public String saleListReport(SaleReportBean saleReportBean, HttpServletRequest request, ModelMap modelMap) {
		setPaginationFromRequest(saleReportBean, request);
		
		Collection<Sale> sales = saleService.search(saleReportBean);
		
		SaleTotalBean saleTotalBean = saleReportService.getSaleTotalBean(saleReportBean);
		
		//Don't like, fix for shitty export
		setPageSize(saleReportBean, modelMap, saleReportBean.getSearchResultCount());
		
		modelMap.addAttribute(sales);
		modelMap.addAttribute("searchResultCount", saleReportBean.getSearchResultCount());
		modelMap.addAttribute(saleReportBean);
		modelMap.addAttribute(getCategories());
		modelMap.addAttribute(getPublishers());
		modelMap.addAttribute(saleTotalBean);
		
		return "salesReport";
	}	
	
	private void fillModel(ModelMap modelMap) {
		modelMap.addAttribute(getCategories());
		modelMap.addAttribute(getPublishers());
	}
	/**
	 * Show entire stock record by category, showing total publisher price and total sale price
	 * ie. quantityInStock * publisherPrice (and sellPrice)
	 * @param saleReportBean
	 * @param request
	 * @param session
	 * @param modelMap
	 * @return
	 */
	private String categoryStockTakeReport(SaleReportBean saleReportBean, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		Collection<CategoryStockTakeBean> categoryStockTakeBeans = saleReportService.getCategoryStockTakeBeans(); 
		
		modelMap.addAttribute(categoryStockTakeBeans);
		
		return "salesReport";
	}
	
	/**
	 * Show entire stock record by published, showing total publisher price and total sale price
	 * ie. quantityInStock * publisherPrice (and sellPrice)
	 * @param saleReportBean
	 * @param request
	 * @param session
	 * @param modelMap
	 * @return
	 */
	private String publisherStockTakeReport(SaleReportBean saleReportBean, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		Collection<PublisherStockTakeBean> publisherStockTakeBeans = saleReportService.getPublisherStockTakeBeans(); 
		
		modelMap.addAttribute(publisherStockTakeBeans);
		
		return "salesReport";
	}	
	
	/**
	 * Category report
	 * Get all sales in date range, sort into catgories
	 * and place in session for late access by img request.
	 * Use maker showCategoryReport to inform jsp to make img request call
	 * @param saleReportBean
	 * @param request
	 * @param modelMap
	 * @return
	 */
	private String categoryReport(SaleReportBean saleReportBean, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		DefaultPieDataset categoryPieDataset = saleReportService.getCategoryPieDataset(saleReportBean);
		
		session.setAttribute("categoryReportPieDataset", categoryPieDataset);
		modelMap.addAttribute("showCategoryReport", true);
		return "salesReport";
	}
	
	/**
	 * source report
	 * Get all sales by source
	 * and place in session for late access by img request.
	 * @param saleReportBean
	 * @param request
	 * @param modelMap
	 * @return
	 */
	private String sourceReport(SaleReportBean saleReportBean,	HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		DefaultPieDataset categoryPieDataset = saleReportService.getSourcePieDataset(saleReportBean);
		
		session.setAttribute("sourceReportPieDataset", categoryPieDataset);
		modelMap.addAttribute("showCategoryReport", true);
		return "salesReport";
	}	

	/**
	 * Report for Daily Report
	 * Gather data for daily sales and put in session
	 * as there will be requests from a jsp page for charts
	 * Ensure last request cleans session
	 * @param saleReportBean
	 * @param request
	 * @param modelMap
	 * @return
	 */
	private String dailyReport(SaleReportBean saleReportBean, HttpServletRequest request, ModelMap modelMap) {
		Calendar startCal = new GregorianCalendar();
		startCal.setTime(saleReportBean.getStartDate());
		resetToMidnight(startCal);
		
		Calendar endCal = new GregorianCalendar();
		endCal.setTime(saleReportBean.getStartDate());
		resetToEndOfDay(endCal);
		
		Collection<Sale> sales = saleService.getAll(startCal.getTime(), endCal.getTime());
		return null;
	}
	
	@RequestMapping(value="/init", method=RequestMethod.GET)
	public String init(ModelMap modelMap) {
		modelMap.addAttribute(new SaleReportBean());
		modelMap.addAttribute(SalesReportType.values());
		modelMap.addAttribute(getCategories());
		modelMap.addAttribute(getPublishers());
		return "salesReport";
	}
	
	@RequestMapping(value="/buildSalesReport")
	public String buildSalesReport(SaleReportBean saleSearchBean, HttpServletRequest request, ModelMap modelMap) {
		setPaginationFromRequest(saleSearchBean, request);
		//Using report bean, get sales, search
		Collection<Sale> saleList = saleService.search(saleSearchBean);
		modelMap.addAttribute(saleList);
		modelMap.addAttribute(new CustomerReportBean());
		return "displaySalesReport";
	}
	
	public Calendar getOneYearAgo() {
		Calendar yearAgo = new GregorianCalendar();
		yearAgo.setTime(new Date());
		yearAgo.add(Calendar.YEAR, -1);
		resetToFirstDayOfMonth(yearAgo);
		return yearAgo;
	}	
	
	public Calendar resetToFirstDayOfMonth(Calendar cal) {
		cal.set(Calendar.DAY_OF_MONTH, 1);
		resetToMidnight(cal);
		return cal;
	}
	
	public Calendar resetToMidnight(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal;
	}
	
	public Calendar resetToEndOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal;
	}

	@Override
	public Service<Customer> getService() {
		return null;
	}	
	
	
}
