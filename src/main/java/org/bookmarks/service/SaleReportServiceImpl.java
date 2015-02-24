package org.bookmarks.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.bookmarks.controller.InvoiceSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemSales;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.SupplierDelivery;
import org.bookmarks.report.bean.CategoryStockTakeBean;
import org.bookmarks.report.bean.PublisherStockTakeBean;
import org.bookmarks.repository.SaleReportRepository;
import org.bookmarks.repository.SaleRepository;
import org.bookmarks.repository.StockItemRepository;
import org.bookmarks.repository.StockItemSalesRepository;
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
import org.bookmarks.controller.bean.SaleReportBean;
import org.bookmarks.controller.bean.SaleTotalBean;

@Service
@Transactional
public class SaleReportServiceImpl implements SaleReportService {
	
	@Autowired
	private SaleService saleService;	
	
	@Autowired
	private SaleReportRepository saleReportRepository;
	
	@Autowired
	private SaleRepository saleRepository;	

	@Autowired
	private StockItemRepository stockItemRepository;
	

	@Autowired
	private StockItemSalesRepository stockItemSalesRepository;	
	
	
	@Override
	public SaleTotalBean getSaleTotalBean(SearchBean searchBean) {
		return saleRepository.getSaleTotalBean(searchBean);
	}

	@Override
	public DefaultPieDataset getCategoryPieDataset(SaleReportBean saleReportBean) {
		Collection<Sale> sales = saleService.getFull(saleReportBean.getStartDate(), saleReportBean.getEndDate());
		Map<String, Long> map = new HashMap<String, Long>(); // <category name, saleNo.>
		for(Sale sale : sales) {
			String categoryName = sale.getStockItem().getCategory().getName();
			 Long total = map.get(categoryName);
			 if(total == null) {
				 total = sale.getQuantity();
			 } else {
				 total = total + sale.getQuantity();
			 }
			 map.put(categoryName, total);
		}
		DefaultPieDataset categoryPieDataset = new DefaultPieDataset();
		Set<String> categoryNames = map.keySet();
		for(String categoryName : categoryNames) {
			Long total = map.get(categoryName);
			categoryPieDataset.setValue(categoryName, total);
		}		
		return categoryPieDataset;
	}
	
	@Override
	public DefaultPieDataset getSourcePieDataset(SaleReportBean saleReportBean) {
		Collection<Sale> sales = saleService.getFull(saleReportBean.getStartDate(), saleReportBean.getEndDate());
		Map<String, Long> map = new HashMap<String, Long>(); // <category name, saleNo.>
		for(Sale sale : sales) {
			String categoryName = sale.getStockItem().getCategory().getName();
			 Long total = map.get(categoryName);
			 if(total == null) {
				 total = sale.getQuantity();
			 } else {
				 total = total + sale.getQuantity();
			 }
			 map.put(categoryName, total);
		}
		DefaultPieDataset categoryPieDataset = new DefaultPieDataset();
		Set<String> categoryNames = map.keySet();
		for(String categoryName : categoryNames) {
			Long total = map.get(categoryName);
			categoryPieDataset.setValue(categoryName, total);
		}		
		return categoryPieDataset;
	}	

	@Override
	public JFreeChart getCategoryReportPieChart(DefaultPieDataset categoryReportPieDataset) {
		JFreeChart chart = ChartFactory.createPieChart(
				"Sales By Category",
				categoryReportPieDataset,
				true, // legend?
				true, // tooltips?
				false // URLs?
			);
		return chart;
	}
	
	@Override
	public JFreeChart getTimeOfDayReportBarChart(DefaultCategoryDataset timeOfDayCategoryDataset) {
		JFreeChart chart = ChartFactory.createBarChart(
			"Sales by hour", // chart title
			"Hour", // domain axis label
			"No. Sales", // range axis label
			timeOfDayCategoryDataset, // data
			PlotOrientation.VERTICAL, // orientation
			true, // include legend
			true, // tooltips?
			false // URLs?
		);
		return chart;
	}	

	@Override
	public JFreeChart getSaleReportBarChart(Long id) {
		DefaultCategoryDataset saleReportCategoryDataset = getCategoryDataset(id);
//			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//			dataset.addValue(1.0, "Row 1", "August");
//			dataset.addValue(5.0, "Row 1", "September");
//			dataset.addValue(3.0, "Row 1", "October");
//			dataset.addValue(1.0, "Row 1", "November");
//			dataset.addValue(5.0, "Row 1", "December");
//			dataset.addValue(3.0, "Row 1", "January");
//			dataset.addValue(2.0, "Row 2", "Column 1");
//			dataset.addValue(3.0, "Row 2", "Column 2");
//			dataset.addValue(2.0, "Row 2", "Column 3");
			
			JFreeChart chart = ChartFactory.createBarChart(
			"Sale Figures", // chart title
			"Months", // domain axis label
			"No. Sales", // range axis label
			saleReportCategoryDataset, // data
			PlotOrientation.VERTICAL, // orientation
			true, // include legend
			true, // tooltips?
			false // URLs?
			);
			return chart;
	}
	
	@Override
	public DefaultCategoryDataset getTimeOfDayBarChartDataset(SaleReportBean saleReportBean) {
		Collection<Sale> sales = saleService.getFull(saleReportBean.getStartDate(), saleReportBean.getEndDate());
		Map<Integer, Long> map = new HashMap<Integer, Long>(); // <hour, saleNo.>
		
		int startHour = 7;
		int endHour = 22;
		
		//Set to zero
		for(int i = startHour;  i < endHour; i++) {
			map.put(i, 0l); //Set to zero sales
		}
		
		for(Sale sale : sales) {
			Date saleTime = sale.getCreationDate();
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(saleTime);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			
			Long saleNo = map.get(hour);
			if(saleNo == null) continue; //our of range
			saleNo = saleNo + sale.getQuantity();
			map.put(hour, saleNo);

		}
		DefaultCategoryDataset timeOfDayCategoryDataset = new DefaultCategoryDataset();
		
		for(Integer i = startHour;  i < endHour; i++) {
			timeOfDayCategoryDataset.addValue(map.get(i), "Time of sale", i + "-" + (i+1));
		}
		
		
		return timeOfDayCategoryDataset;
			
/*			JFreeChart chart = ChartFactory.createBarChart(
			"Sale Figures", // chart title
			"Months", // domain axis label
			"No. Sales", // range axis label
			saleReportCategoryDataset, // data
			PlotOrientation.VERTICAL, // orientation
			true, // include legend
			true, // tooltips?
			false // URLs?
			);
			return chart;*/
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
	
	private DefaultCategoryDataset getCategoryDataset(Long id) {
		final DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
//		dataset.addValue(1.0, "Row 1", "August");		
		Calendar oneYearAgo = getOneYearAgo();  //Used to loop through

//		id = 2944l;
		Collection<Sale> sales = saleService.get(id, oneYearAgo.getTime(), new Date());
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM/yyyy");
		SimpleDateFormat localFormatter = new SimpleDateFormat("dd/MMM/yyyy");
		int[] timeArray = new int[400];
		
		int count = 0;
		for(Sale sale : sales) {
			Date creationDate = sale.getCreationDate();
			while(true) {
//				System.out.println(creationDate + " " + oneYearAgo.getTime());
				if(creationDate.after(oneYearAgo.getTime())) {
					dataSet.addValue(new Integer(timeArray[count]), "row 1", formatter.format(oneYearAgo.getTime()));
					oneYearAgo.add(Calendar.MONTH, 1);
					count ++;
				} else {
					timeArray[count]++;
					System.out.println("Count=" + count + " Added sale for period previous to " + localFormatter.format(oneYearAgo.getTime()) + " with create date " + localFormatter.format(creationDate));
					break;
				}
			}
			//Add last lot
			dataSet.addValue(new Integer(timeArray[count]), "row 1", formatter.format(oneYearAgo.getTime()));
//			series.add(oneYearAgo.getTime().getTime(), new Integer(timeArray[count]));
			
		}

//		final XYSeriesCollection dataSet = new XYSeriesCollection(series);
		return dataSet;
	}	
	
	@Override
	public IntervalXYDataset getIntervalXYDataset(Long id) {
		final XYSeries series = new XYSeries("Days of week");
		Calendar oneYearAgo = getOneYearAgo();  //Used to loop through

		id = 2944l;
		Collection<Sale> sales = saleService.get(id, oneYearAgo.getTime(), new Date());
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy");
		int[] timeArray = new int[400];
		
		int count = 0;
		for(Sale sale : sales) {
			Date creationDate = sale.getCreationDate();
			while(true) {
				if(creationDate.after(oneYearAgo.getTime())) {
					series.add(oneYearAgo.getTime().getTime(), new Integer(timeArray[count]));
					oneYearAgo.add(Calendar.DATE, 7);
					count ++;
				} else {
					timeArray[count]++;
					System.out.println("Count=" + count + " Added sale for period previous to " + formatter.format(oneYearAgo.getTime()) + " with create date " + formatter.format(creationDate));
					break;
				}
			}
			//Add last lot
			series.add(oneYearAgo.getTime().getTime(), new Integer(timeArray[count]));
			
		}

		final XYSeriesCollection dataset = new XYSeriesCollection(series);
		return dataset;
	}	
	
	@Override
	public JFreeChart getIntervalXYChart(IntervalXYDataset dataset) {

		final JFreeChart chart = ChartFactory.createXYBarChart(
	            "Weekly Sales Figures for Year",
	            "X", 
	            true,
	            "Sales", 
	            dataset,
	            PlotOrientation.VERTICAL,
	            true,
	            true,
	            false
	        );
		
	       XYPlot plot = (XYPlot) chart.getPlot();
	       
	       NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	       rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	       
	       DateAxis dateAxis = (DateAxis) plot.getDomainAxis();
	       dateAxis.setLabel("Date");
	       dateAxis.setStandardTickUnits(DateAxis.createStandardDateTickUnits());
//	       dateAxis.setRange(getFirstDayOfYear(), new Date());
//	       dateAxis.
//	       dateAxis.set
//	       dateAxis.set
//	        domainAxis.setRange(0, 52);
//	        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());	
//	        plot.setDomainAxis(dateAxis);
		return chart;

	}

	@Override
	@Transactional
	public Collection<CategoryStockTakeBean> getCategoryStockTakeBeans() {
		// TODO Auto-generated method stub
		return saleReportRepository.getCategoryStockTakeBeans();
	}

	@Override
	@Transactional
	public Collection<PublisherStockTakeBean> getPublisherStockTakeBeans() {
		// TODO Auto-generated method stub
		return saleReportRepository.getPublisherStockTakeBeans();
	}
	
	@Override
	public MonthlySaleReportBean getMonthlySaleReportBean(StockItem stockItem) {
		Calendar c = new java.util.GregorianCalendar();
		c.setTime(new Date());
		Integer currentYear = c.get(Calendar.YEAR);
		
		List<StockItemSales> sales = stockItemRepository.getStockItemSales(stockItem);
		
		for(int year = 2012; year <= currentYear; year++) {
			StockItemSales stockItemSales = null;
			for(StockItemSales s : sales) {
				if(s.getYear() == year) {
					stockItemSales = s;
				}
			}
			//Check doesn't already exist
			if(stockItemSales == null) {
				stockItemSales = getSales(year, stockItem);
				stockItemSales.setStockItem(stockItem);
				sales.add(stockItemSales);
				
				if(year != currentYear) {
					stockItemSalesRepository.save(stockItemSales);
				}
			}
			populateSalesList(stockItemSales);
		}

		MonthlySaleReportBean monthlySaleReportBean = new MonthlySaleReportBean();
		monthlySaleReportBean.setCurrentYear(currentYear);
		monthlySaleReportBean.setSales(sales);
		
		//Get last supplier delivery date
		SupplierDeliveryLine lastSupplierDeliveryLine = saleReportRepository.getLastSupplierDeliveryLine(stockItem);
		SupplierDelivery lastSupplierDelivery = saleReportRepository.getLastSupplierDelivery(stockItem);
		Collection<SupplierDeliveryLine > supplierDeliveryLines = saleReportRepository.getLastSupplierDeliveryLines(stockItem);
		//Get last sale date
		Collection<Date> lastSaleDates = saleReportRepository.getLastSaleDates(stockItem);
		Collection<Sale> lastSales = saleReportRepository.getLastSales(stockItem);
		monthlySaleReportBean.setLastSaleDates(lastSaleDates);
		monthlySaleReportBean.setLastSales(lastSales);
		monthlySaleReportBean.setLastSupplierDeliveryLine(lastSupplierDeliveryLine);
		monthlySaleReportBean.setLastSupplierDeliveryLines(supplierDeliveryLines);
		monthlySaleReportBean.setLastSupplierDelivery(lastSupplierDelivery);

		return monthlySaleReportBean;
	}


	private void populateSalesList(StockItemSales stockItemSales) {
		System.out.println("Adding sales " + stockItemSales.getSales() + " for year " + stockItemSales.getYear());
		//Also populate List<Long> salesList
		if(stockItemSales.getSales() != null) {
			int count = 0;
			StringTokenizer st = new StringTokenizer(stockItemSales.getSales(), ",");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				stockItemSales.getSalesList().add(Long.parseLong(token));
	         	System.out.println("Adding token " + token);
	     	}
		}
		System.out.println("Have finished " + stockItemSales.getSalesList());
		
	}

	private StockItemSales getSales(int year, StockItem stockItem) {
		String sales = "";
		int totalSales = 0;
		for(int i = 0; i < 12; i++) {
			Date startDate = getStartOfMonth(i, year);
			Date endDate = getEndOfMonth(i, year);	
			Long quantityOfSales = saleService.getTotalQuantityForPeriod(stockItem, startDate, endDate);		
			if(quantityOfSales == null) quantityOfSales = 0l;
			totalSales += quantityOfSales.intValue();
			sales = sales + quantityOfSales.toString() + ","; 
		}
		sales = sales + totalSales + ",";

		StockItemSales s = new StockItemSales();
		s.setYear(year);
		s.setSales(sales);
		s.setStockItem(stockItem);
		return s;
	}	
	
	private Date getEndOfMonth(int month, int year) {
		Calendar startCalendar = new GregorianCalendar();
		startCalendar.set(Calendar.YEAR, year);
		startCalendar.set(Calendar.MONTH, month);
		startCalendar.set(Calendar.DAY_OF_MONTH, startCalendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
		startCalendar.set(Calendar.HOUR_OF_DAY, 23);
		startCalendar.set(Calendar.MINUTE, 59);
		startCalendar.set(Calendar.SECOND, 59);
		return startCalendar.getTime();
	}
	
	
	private Date getStartOfMonth(int month, int year) {
		Calendar startCalendar = new GregorianCalendar();
		startCalendar.set(Calendar.YEAR, year);
		startCalendar.set(Calendar.MONTH, month);
		startCalendar.set(Calendar.DAY_OF_MONTH, 1);
		startCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startCalendar.set(Calendar.MINUTE, 0);
		startCalendar.set(Calendar.SECOND, 0);
		return startCalendar.getTime();
	}

	@Override
	public Collection<StockItem> unsold(InvoiceSearchBean invoiceSearchBean) {
		return stockItemRepository.unsold(invoiceSearchBean);
	}
}
