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
import org.bookmarks.domain.CustomerReportType;
import org.bookmarks.domain.Invoice;
import org.bookmarks.domain.SalesReportType;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.Event;

import org.bookmarks.controller.bean.SaleTotalBean;
import org.bookmarks.controller.validation.CustomerReportBeanValidator;
import org.bookmarks.controller.validation.SaleReportBeanValidator;

import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.report.InvoiceReportLine;
import org.bookmarks.report.bean.CategoryStockTakeBean;
import org.bookmarks.service.CustomerOrderService;
import org.bookmarks.service.CustomerReportService;
import org.bookmarks.service.CustomerService;
import org.bookmarks.service.SaleReportService;
import org.bookmarks.service.SaleService;
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
@RequestMapping("/customerReport")
public class CustomerReportController extends AbstractBookmarksController {

	@Autowired
	private CustomerReportService customerReportService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomerReportBeanValidator customerReportBeanValidator;

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
	public String report(@Valid CustomerReportBean customerReportBean, BindingResult bindingResult, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		Customer customer = customerService.get(customerReportBean.getCustomer());
		customerReportBean.setCustomer(customer);

		customerReportBeanValidator.validate(customerReportBean, bindingResult);
		if(bindingResult.hasErrors()) {
			modelMap.addAttribute(customerReportBean);
			return "customerReport";
		}
		switch (customerReportBean.getCustomerReportType()) {
			case INVOICE:
				return invoiceReport(customerReportBean, request, modelMap);
			default:
				return null;
		}
	}

	private String invoiceReport(CustomerReportBean customerReportBean,	HttpServletRequest request, ModelMap modelMap) {

		Collection<InvoiceReportLine> invoiceReportLines = customerReportService.getInvoiceReport(customerReportBean);
		Customer customer = customerService.get( customerReportBean.getCustomer().getId() );

		modelMap.addAttribute(invoiceReportLines);
		modelMap.addAttribute(customer);

		return "customerReport";
	}

	@Override
	public Service getService() {
		// TODO Auto-generated method stub
		return null;
	}

	@RequestMapping(value="/init", method=RequestMethod.GET)
	public String init(ModelMap modelMap) {
		modelMap.addAttribute(new CustomerReportBean());
		modelMap.addAttribute(CustomerReportType.values());
		modelMap.addAttribute("info", "Select customer and dates to show all books ordered on account for given period");
		return "customerReport";
	}
}
