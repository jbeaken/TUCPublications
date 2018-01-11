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
import java.util.Locale;

import java.time.LocalDate;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.servlet.http.HttpServletResponse;

import org.springframework.format.number.CurrencyStyleFormatter;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.bookmarks.report.bean.Rotate;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;


@Controller
@RequestMapping("/customerReport")
public class CustomerReportController extends AbstractBookmarksController {

	@Autowired
	private CustomerReportService customerReportService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomerReportBeanValidator customerReportBeanValidator;

	private Logger logger = LoggerFactory.getLogger(CustomerReportController.class);

	/**
	 * Main entry point, analyses report bean to see which report to call
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

	@RequestMapping(value="/generatePdf")
	public @ResponseBody void generatePdf(@Valid CustomerReportBean customerReportBean, BindingResult bindingResult, HttpSession session, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Customer customer = customerService.get(customerReportBean.getCustomer());

		customerReportBean.setCustomer(customer);

		customerReportBeanValidator.validate(customerReportBean, bindingResult);

		if(bindingResult.hasErrors()) {
			modelMap.addAttribute(customerReportBean);
			return;
		}

		Collection<InvoiceReportLine> invoiceReportLines = getInvoiceReportLines( customerReportBean );

		//PDFtastico

		// Page variables
		float fixedHeight = 125f;
		float marginTop = 0f;
		float marginLeft = 10f;

		Document doc = new Document(PageSize.A4.rotate());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// Here We are writing the pdf contents to the output stream via
		// pdfwriter
		// doc will contain the all the format and pages generated by the iText

		PdfWriter writer = PdfWriter.getInstance(doc, baos);

		doc.open();
	//	doc.setMargins(marginLeft, 0, marginTop, 0);

		Rotate rotation = new Rotate();
		writer.setPageEvent(rotation);

		doc.newPage();

		rotation.setRotation(PdfPage.LANDSCAPE);


		PdfPTable table = new PdfPTable(8);
		//date, title, quantity, discount, delivery type, price, current balance, credit amount
		//date, title, quantity, discount, delivery type, credit, debit, current balance
		table.setWidths(new int[] { 3, 8, 1, 2, 3, 2, 2, 2 });

		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		table.setWidthPercentage(100);
		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

		String nl = System.getProperty("line.separator");

		CurrencyStyleFormatter currencyFormatter = new CurrencyStyleFormatter();

		for (InvoiceReportLine irl : invoiceReportLines) {

			Font DOC_FONT = new Font(Font.FontFamily.COURIER, 12, Font.NORMAL);

			table.addCell( new SimpleDateFormat("dd/MM/yyyy").format( irl.getDate() ) );

			table.addCell( irl.getRef() );

			if(irl.getSale() == null) {
				table.addCell("");
			} else {
				table.addCell( irl.getSale().getQuantity() + "" );
			}

			table.addCell( irl.getDiscount() );

			table.addCell( irl.getDeliveryTypeDisplay() );

			table.addCell( irl.getCreditAmount(currencyFormatter) );

			table.addCell( irl.getDebitAmount(currencyFormatter) );

			table.addCell( currencyFormatter.print(irl.getCurrentBalance(), Locale.UK)  );



		}

		doc.add(table);

		doc.close();

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=\"account.pdf\"");
		response.setContentLength(baos.size());

		OutputStream os = response.getOutputStream();
		baos.writeTo(os);
		os.flush();
		os.close();
	}

	private Collection<InvoiceReportLine> getInvoiceReportLines(CustomerReportBean customerReportBean) {

		if(customerReportBean.getStartDate() == null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, -10);
			customerReportBean.setStartDate( cal.getTime() );
		}

		if(customerReportBean.getEndDate() == null) {
			customerReportBean.setEndDate(new Date());
		}

		logger.info("Retrieving invoice report lines with customerReportBean : {}", customerReportBean);

		Collection<InvoiceReportLine> invoiceReportLines = customerReportService.getInvoiceReport(customerReportBean);

		logger.info("Have {} InvoiceReportLines", invoiceReportLines.size());
		return invoiceReportLines;
	}

	private String invoiceReport(CustomerReportBean customerReportBean,	HttpServletRequest request, ModelMap modelMap) {

		Collection<InvoiceReportLine> invoiceReportLines = getInvoiceReportLines( customerReportBean );

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
