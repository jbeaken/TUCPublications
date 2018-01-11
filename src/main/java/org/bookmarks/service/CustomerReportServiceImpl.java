package org.bookmarks.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.stream.*;
import java.util.stream.Collectors;

import org.bookmarks.controller.SearchBean;
import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.Invoice;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.report.InvoiceReportLine;
import org.bookmarks.report.bean.CategoryStockTakeBean;
import org.bookmarks.repository.CreditNoteRepository;
import org.bookmarks.repository.InvoiceRepository;
import org.bookmarks.repository.SaleReportRepository;
import org.bookmarks.repository.SaleRepository;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.bookmarks.controller.bean.MonthlySaleReportBean;
import org.bookmarks.controller.bean.CustomerReportBean;
import org.bookmarks.controller.bean.SaleReportBean;
import org.bookmarks.controller.bean.SaleTotalBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@Transactional
public class CustomerReportServiceImpl implements CustomerReportService {


	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private CreditNoteRepository creditNoteRepository;

	private Logger logger = LoggerFactory.getLogger(CustomerReportServiceImpl.class);

	@Override
	public Collection<InvoiceReportLine> getInvoiceReport(CustomerReportBean customerReportBean) {

		long start = System.currentTimeMillis();
		long realStart = start;

		Collection<Invoice> invoices = invoiceRepository.getInvoiceReport(customerReportBean);
		long end = System.currentTimeMillis();
		float time = ((end - start));
		logger.debug("Get Invoices took " + time + " milli seconds");
		start = end;

		Collection<CreditNote> creditNotes = creditNoteRepository.getCreditNotes(customerReportBean);

		end = System.currentTimeMillis();
		time = ((end - start));
		logger.debug("Get creditNotes took " + time + " milli seconds");
		start = end;

		//Put them into InvoiceReportLine objects
		List<InvoiceReportLine> invoiceReportLines = new ArrayList<InvoiceReportLine>();

		for(Invoice invoice : invoices) {
			for(Sale sale : invoice.getSales()) {
				//System.out.println(sale.getStockItem().getTitle());
				//System.out.println(invoice.getId());
				InvoiceReportLine irl = new InvoiceReportLine();
				irl.setSale(sale);
				invoiceReportLines.add(irl);
				irl.setDeliveryType(invoice.getDeliveryType());
			}
			if(invoice.getSecondHandPrice() != null && invoice.getSecondHandPrice().floatValue() != 0) {
				InvoiceReportLine irl = new InvoiceReportLine();
				Sale sale = new Sale();
				sale.setDiscount(new BigDecimal(0));
				sale.setQuantity(1l);
				sale.setSellPrice(invoice.getSecondHandPrice());
				StockItem stockItem = new StockItem();
				stockItem.setIsbn("");
				stockItem.setTitle("Second Hand");
				sale.setStockItem(stockItem);
				sale.setCreationDate(invoice.getCreationDate());
				irl.setSale(sale);
				irl.setDeliveryType(invoice.getDeliveryType());
				invoiceReportLines.add(irl);
			}
			if(invoice.getServiceCharge() != null && invoice.getServiceCharge().floatValue() != 0) {
				InvoiceReportLine irl = new InvoiceReportLine();
				Sale sale = new Sale();
				sale.setDiscount(new BigDecimal(0));
				sale.setQuantity(1l);
				sale.setSellPrice(invoice.getServiceCharge());
				StockItem stockItem = new StockItem();
				stockItem.setIsbn("");
				stockItem.setTitle("Service Charge");
				sale.setStockItem(stockItem);
				sale.setCreationDate(invoice.getCreationDate());
				irl.setSale(sale);
				irl.setDeliveryType(invoice.getDeliveryType());
				invoiceReportLines.add(irl);
			}
		}

		for(CreditNote creditNote : creditNotes) {
			InvoiceReportLine irl = new InvoiceReportLine();
			irl.setCreditNote(creditNote);
			invoiceReportLines.add(irl);
		}

		//Filter if necessary
		String filter = customerReportBean.getFilter();
		if(filter != null) {
			if(filter.equals("DEBITS")) {
				invoiceReportLines = invoiceReportLines.stream()
					.filter( irl -> irl.getCredit().signum() < 0 )
					.collect(Collectors.toList());

			}
			if(filter.equals("CREDITS")) {
				invoiceReportLines = invoiceReportLines.stream()
					.filter( irl -> irl.getCredit().signum() > 0 )
					.collect(Collectors.toList());

			}
		}

		//Sort them by date, most recent first
		Collections.sort(invoiceReportLines);

		//Get current balance from customer
		BigDecimal latestBalance = customerReportBean.getCustomer().getBookmarksAccount().getCurrentBalance();

		//Starting with the latest work backwards, setting balance at time of sale/credit note
		for(InvoiceReportLine invoiceReportLine : invoiceReportLines) {
			invoiceReportLine.setCurrentBalance(latestBalance);
			latestBalance = latestBalance.subtract(invoiceReportLine.getCredit());
		}

		end = System.currentTimeMillis();
		time = ((end - realStart));
		logger.debug("setCurrentBalance took " + time + " milli seconds");


		return invoiceReportLines;
	}
}
