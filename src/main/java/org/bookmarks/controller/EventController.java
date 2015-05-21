package org.bookmarks.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Collection;
<<<<<<< HEAD
<<<<<<< HEAD
=======
import java.util.Date;
=======
>>>>>>> 720c3a6... Download, upload event sales all looking good
import java.util.List;
<<<<<<< HEAD
>>>>>>> 27fd69b... First cut of csv sales download
=======
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

>>>>>>> e7d9133... Getting to csv download
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.math.BigDecimal;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVPrinter;

import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.bookmarks.bean.CSVResponse;
import org.bookmarks.controller.bean.CalendarEvent;
import org.bookmarks.controller.bean.SaleReportBean;
import org.bookmarks.controller.validation.EventValidator;
import org.bookmarks.domain.AbstractEntity;
import org.bookmarks.domain.Event;
import org.bookmarks.domain.EventType;
import org.bookmarks.domain.Invoice;
import org.bookmarks.domain.SalesReportType;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Customer;
import org.bookmarks.exceptions.BookmarksException;

import org.bookmarks.service.EventService;
import org.bookmarks.service.Service;
import org.bookmarks.service.SaleService;
import org.bookmarks.service.StockItemService;
import org.bookmarks.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/events")
public class EventController extends AbstractBookmarksController {

	@Autowired
	private EventService eventService;
	
	@Autowired
	private StockItemService stockItemService;
	
	@Autowired
	private SaleController saleController;
	
	@Autowired
<<<<<<< HEAD
	private SaleReportController reportController;	
	
=======
	private SaleService saleService;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private SaleReportController reportController;

>>>>>>> 27fd69b... First cut of csv sales download
	@Value("#{ applicationProperties['imageFileLocation'] }")
	private String imageFileLocation;	

	private Logger logger = LoggerFactory.getLogger(EventController.class);

<<<<<<< HEAD
	@RequestMapping(value="/upload", method=RequestMethod.POST)
<<<<<<< HEAD
	public @ResponseBody String search(MultipartFile files, Long eventId, HttpServletRequest request, ModelMap modelMap) throws IOException {
	 
=======
	public @ResponseBody String upload(MultipartFile files, Long eventId, HttpServletRequest request, ModelMap modelMap) throws IOException {
=======
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody String uploadImage(MultipartFile files, Long eventId, HttpServletRequest request, ModelMap modelMap) throws IOException {
>>>>>>> 720c3a6... Download, upload event sales all looking good

>>>>>>> 27fd69b... First cut of csv sales download
		if (!ServletFileUpload.isMultipartContent(request)) {
			throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
		}
<<<<<<< HEAD
		 
		 InputStream is = files.getInputStream();
			
			//Normally /home/bookmarks/images/orginal/isbn.jpg
			File originalFile = new File(imageFileLocation + "events" + File.separator + eventId + ".jpg");
			
			OutputStream os = new FileOutputStream(originalFile);

			// if file doesnt exists, then create it
			if (!originalFile.exists()) {
				originalFile.createNewFile();
			}
			
			//Save file to beans local file system
			IOUtils.copy(is, os);
			
=======

		InputStream is = files.getInputStream();

		// Normally /home/bookmarks/images/orginal/isbn.jpg
		File originalFile = new File(imageFileLocation + "events" + File.separator + eventId + ".jpg");

		OutputStream os = new FileOutputStream(originalFile);

		// if file doesnt exists, then create it
		if (!originalFile.exists()) {
			originalFile.createNewFile();
		}

		// Save file to beans local file system
		IOUtils.copy(is, os);

>>>>>>> 720c3a6... Download, upload event sales all looking good
		return "searchEvents";
	}
<<<<<<< HEAD
	
	
=======

	/**
	 * Text file to upload from mini beans, for extennal event 1) CSV file
	 * contains with sales, followed by invoice sales. 2) Create event 3) Create
	 * sales and invoices
	 **/
	@RequestMapping(value = "/uploadSales", method = RequestMethod.POST)
	public String uploadSales(Event event, HttpSession session, ModelMap modelMap) throws IOException, java.text.ParseException {

		MultipartFile file = event.getFile();
		String fileName = file.getOriginalFilename();
		Long fileSize = file.getSize();

		float total = 0;

		logger.info("Uploading sales for new Event " + event.getName());

		if (fileName.indexOf(".csv") == -1) {
			addError("The file must be a csv file ", modelMap);
			modelMap.addAttribute("event", new Event());
			return "uploadSales";
		}

		if (fileSize > 100000) {
			addError("File too big! Size is " + (fileSize / 1000) + " Kb", modelMap);
			return "uploadSales";
		}

		List<Sale> sales = new ArrayList<Sale>();
		Map<Long, Invoice> invoiceMap = new HashMap<Long, Invoice>();

		Reader reader = new InputStreamReader(file.getInputStream());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);

		for (CSVRecord record : records) {

			String type = record.get(0);
			String quantity = record.get(1);
			String discount = record.get(2);
			String sellPrice = record.get(3);
			String vat = record.get(4);
			String creationDateStr = record.get(5);
			String stockItemId = record.get(6);

			System.out.println(creationDateStr);
			System.out.println(quantity);
			System.out.println(discount);
			System.out.println(stockItemId);

			Date creationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(creationDateStr);

			// Sale
			Sale s = new Sale();
			s.setQuantity(new Long(quantity));
			s.setDiscount(new BigDecimal(discount));
			s.setVat(new BigDecimal(vat));
			s.setSellPrice(new BigDecimal(sellPrice));
			s.setEvent(event);
			s.setCreationDate(creationDate);

			StockItem si = new StockItem();
			si.setId(new Long(stockItemId));

			s.setStockItem(si);

			if (type.equals("S")) {
				sales.add(s);
			} else if (type.equals("I")) {
				// Invoice
				Long customerId = new Long(record.get(7));
				String invoiceCreationDateStr = record.get(8);

				Date invoiceCreationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(invoiceCreationDateStr);

				Invoice invoice = invoiceMap.get(customerId);

				if (invoice == null) {
					invoice = new Invoice();
					Customer c = new Customer();
					c.setId(customerId);
					invoice.setCustomer(c);
					invoice.setSales(new HashSet<Sale>());
					invoice.setCreationDate(invoiceCreationDate);

					invoiceMap.put(customerId, invoice);
				}

				invoice.getSales().add(s);

			} else {
				throw new BookmarksException("Cannot identify row");
			}

			total = +(s.getQuantity() * s.getSellPrice().floatValue());
		}

		// Persist sales
		for (Sale s : sales) {
			//Decrements stock level
			saleService.sell(s);
		}

		// Persist invoices. This will decrement stock as well
		for (Invoice i : invoiceMap.values()) {
			invoiceService.save(i, i.getSales(), null, event);
		}

		addSuccess("Have successfully uploaded mini-beans sales totaling " + total + " for " + event.getName(), modelMap);

		return "redirect:/events/search";
	}

	/**
	 * Text file to upload from mini beans, persist external event sales
	 **/
	@RequestMapping(value = "/uploadSales", method = RequestMethod.GET)
	public String uploadSales(Long eventId, ModelMap modelMap) throws IOException {
		Event event = eventService.get(eventId);
		modelMap.addAttribute("event", event);
		return "uploadSales";
	}

	/**
	 * Generate text file of all sales and invoices on minibeans
	 **/
	@RequestMapping(value = "/downloadSales", method = RequestMethod.GET)
	public @ResponseBody CSVResponse downloadSales(ModelMap modelMap) throws IOException {

		// sa.quantity, sa.discount, sa.sellPrice, sa.vat, si.id
		List<String[]> sales = saleService.getAllForCsv();

		// sa.quantity, sa.discount, sa.sellPrice, sa.vat, si.id, i.customerId
		List<String[]> invoices = invoiceService.getAllForCsv();

		sales.addAll(invoices);

		return new CSVResponse(sales, "mini-beans.csv");
	}

<<<<<<< HEAD
>>>>>>> 27fd69b... First cut of csv sales download
	@RequestMapping(value="/getJson", method = RequestMethod.GET)
=======
	@RequestMapping(value = "/getJson", method = RequestMethod.GET)
>>>>>>> 720c3a6... Download, upload event sales all looking good
	@ResponseBody
	public Collection<CalendarEvent> getJson() {
		
		EventSearchBean eventSearchBean = new EventSearchBean();
		Calendar c = new GregorianCalendar();
		c.add(Calendar.DAY_OF_YEAR, -30);
		eventSearchBean.getEvent().setStartDate(c.getTime());
		Collection<Event> events = eventService.search(eventSearchBean);
		
		Collection<CalendarEvent> calendarEvents = new ArrayList<CalendarEvent>();
		for (Event e : events) {
			CalendarEvent ce = new CalendarEvent(e);
			calendarEvents.add(ce);
		}

		return calendarEvents;
<<<<<<< HEAD
	}	
	
	@RequestMapping(value="/search")
=======
	}

	@RequestMapping(value = "/search")
>>>>>>> 720c3a6... Download, upload event sales all looking good
	public String search(EventSearchBean eventSearchBean, HttpServletRequest request, ModelMap modelMap) {
		if (eventSearchBean == null) { // this is coming from the nav bar
			eventSearchBean = new EventSearchBean();
		}
		setPaginationFromRequest(eventSearchBean, request);

		Collection<Event> events = eventService.search(eventSearchBean);

		modelMap.addAttribute(events);
		modelMap.addAttribute("searchResultCount", eventSearchBean.getSearchResultCount());
		modelMap.addAttribute(EventType.values());

		return "searchEvents";
	}
<<<<<<< HEAD
	
	@RequestMapping(value="/delete")
=======

	@RequestMapping(value = "/delete")
>>>>>>> 720c3a6... Download, upload event sales all looking good
	public String delete(Long id, HttpServletRequest request, ModelMap modelMap) {
		Event event = new Event();
		event.setId(id);
		try {
			eventService.delete(event);
			addSuccess("Event has been deleted", modelMap);
		} catch (Exception e) {
			e.printStackTrace();
			addError("Cannot delete, most probably because event has sales attached.", modelMap);
		}
		EventSearchBean eventSearchBean = new EventSearchBean();
		modelMap.addAttribute("eventSearchBean", eventSearchBean);
		return search(eventSearchBean, request, modelMap);
	}
<<<<<<< HEAD
	
	@RequestMapping(value="/startSelling")
=======

	@RequestMapping(value = "/startSelling")
>>>>>>> 720c3a6... Download, upload event sales all looking good
	public String startSelling(Long eventId, String eventName, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		Event event = new Event();
		event.setId(eventId);
		event.setName(eventName);
		
		session.setAttribute("event", event);
		
		return saleController.displaySellStockItem(modelMap, session);
	}
<<<<<<< HEAD
	
	
	@RequestMapping(value="/showSales")
=======

	@RequestMapping(value = "/showSales")
>>>>>>> 720c3a6... Download, upload event sales all looking good
	public String showSales(Long eventId, String eventName, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		Event event = new Event();
		event.setId(eventId);
		event.setName(eventName);
		
		SaleReportBean saleReportBean = new SaleReportBean(event);
<<<<<<< HEAD
		
//		modelMap.addAttribute(saleReportBean);
		
		modelMap.addAttribute(saleReportBean);
		modelMap.addAttribute(SalesReportType.values());
		modelMap.addAttribute(getCategories(session));
		return "salesReport";		
		
//		return reportController.saleListReport(saleReportBean, request, session, modelMap);
	}	
	
	@RequestMapping(value="/stopSelling")
=======

		// modelMap.addAttribute(saleReportBean);

		modelMap.addAttribute(saleReportBean);
		modelMap.addAttribute(SalesReportType.values());
		modelMap.addAttribute(getCategories(session));
		return "salesReport";

		// return reportController.saleListReport(saleReportBean, request,
		// session, modelMap);
	}

	@RequestMapping(value = "/stopSelling")
>>>>>>> 720c3a6... Download, upload event sales all looking good
	public String stopSelling(HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		session.removeAttribute("event");
		
		return "home";
	}

	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	public String reset(HttpServletRequest request, ModelMap modelMap) {
		EventSearchBean eventSearchBean = new EventSearchBean();
		modelMap.addAttribute(eventSearchBean);
		return search(eventSearchBean, request, modelMap);
	}	

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(@Valid Event event, BindingResult bindingResult, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		if (event.getStockItem().getIsbn().trim().equals("")) {
			event.setStockItem(null);
		} else {
			StockItem stockItem = stockItemService.getByISBNAsNumber(event.getStockItem().getIsbn());
			if (stockItem == null) {
				addError("Cannot find stock " + event.getStockItem().getIsbn(), modelMap);
				modelMap.addAttribute(EventType.values());
				return "addEvent";
			}
			event.setStockItem(stockItem);
		}
		
		EventValidator eventValidator = new EventValidator();
		eventValidator.validate(event, bindingResult);
<<<<<<< HEAD
		
		//Check for errors
		if(bindingResult.hasErrors()){
=======

		// Check for errors
		if (bindingResult.hasErrors()) {
>>>>>>> 720c3a6... Download, upload event sales all looking good
			modelMap.addAttribute(EventType.values());
			return "addEvent";
		}

		eventService.save(event);
<<<<<<< HEAD
		
		//Redirect
=======

		// Redirect
>>>>>>> 720c3a6... Download, upload event sales all looking good
		EventSearchBean eventSearchBean = new EventSearchBean();
		eventSearchBean.getEvent().setName(event.getName());
		session.setAttribute("searchBean", eventSearchBean);
		return "redirect:searchFromSession";
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String displayAdd(ModelMap modelMap) {
		modelMap.addAttribute(new Event());
		modelMap.addAttribute(EventType.values());
		return "addEvent";
	}

	@RequestMapping(value = "/displayAddFromCalendar", method = RequestMethod.GET)
	public String displayAddFromCalendar(Event event, ModelMap modelMap) {
		modelMap.addAttribute(event);
		modelMap.addAttribute(EventType.values());
		return "addEvent";
<<<<<<< HEAD
	}	
	
	@RequestMapping(value="/searchFromSession")
=======
	}

	@RequestMapping(value = "/searchFromSession")
>>>>>>> 720c3a6... Download, upload event sales all looking good
	public String searchFromSession(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		EventSearchBean eventSearchBean = (EventSearchBean) session.getAttribute("searchBean");
		eventSearchBean.isFromSession(true);
		modelMap.addAttribute(eventSearchBean);
		modelMap.addAttribute(EventType.values());
		return search(eventSearchBean, request, modelMap);
	}	

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String edit(@Valid Event event, BindingResult bindingResult, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		
		EventValidator eventValidator = new EventValidator();
		eventValidator.validate(event, bindingResult);

<<<<<<< HEAD
<<<<<<< HEAD
		logger.info(event.getDescription());
		
=======
>>>>>>> e7d9133... Getting to csv download
		//Check for errors
		if(bindingResult.hasErrors()){
			modelMap.addAttribute(EventType.values());
			return "editEvent";
		}
		
		if(event.getStockItem().getIsbn().trim().equals("")) {
=======
		// Check for errors
		if (bindingResult.hasErrors()) {
			modelMap.addAttribute(EventType.values());
			return "editEvent";
		}

		if (event.getStockItem().getIsbn().trim().equals("")) {
>>>>>>> 720c3a6... Download, upload event sales all looking good
			event.setStockItem(null);
		} else {
			StockItem stockItem = stockItemService.getByISBNAsNumber(event.getStockItem().getIsbn());
			if (stockItem == null) {
				addError("Cannot find stock " + event.getStockItem().getIsbn(), modelMap);
				modelMap.addAttribute(EventType.values());
				return "editEvent";
			}
			event.setStockItem(stockItem);
		}
<<<<<<< HEAD
		
		if(event.getDescription() != null) {
=======

		if (event.getDescription() != null) {
>>>>>>> 720c3a6... Download, upload event sales all looking good
			String description = event.getDescription();
			description = description.replace("<br/>", "");
			event.setDescription(description);
		}
<<<<<<< HEAD
<<<<<<< HEAD
		logger.info("AFTER:" + event.getDescription());
=======
>>>>>>> e7d9133... Getting to csv download
		if(event.getNote() != null && event.getNote().trim().equals("")){
=======
		if (event.getNote() != null && event.getNote().trim().equals("")) {
>>>>>>> 720c3a6... Download, upload event sales all looking good
			event.setNote(null);
		}
		
		eventService.update(event);
<<<<<<< HEAD
		logger.info("AFTER_UPDATE:" + event.getDescription());
=======

<<<<<<< HEAD
>>>>>>> e7d9133... Getting to csv download
		//Redirect
=======
		// Redirect
>>>>>>> 720c3a6... Download, upload event sales all looking good
		EventSearchBean eventSearchBean = new EventSearchBean();
		eventSearchBean.getEvent().setName(event.getName());
		session.setAttribute("searchBean", eventSearchBean);
		return "redirect:searchFromSession";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap modelMap) {
		Event event = eventService.get(id);

<<<<<<< HEAD
		logger.info(event.getDescription());
		
=======
>>>>>>> e7d9133... Getting to csv download
		modelMap.addAttribute(EventType.values());
		modelMap.addAttribute(event);
		
		return "editEvent";
	}

	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String displayView(Long id, ModelMap modelMap) {
<<<<<<< HEAD
=======

>>>>>>> 27fd69b... First cut of csv sales download
		Event event = eventService.get(id);
		modelMap.addAttribute(event);
		return "viewEvent";
	}

	@Override
	public Service getService() {
		return eventService;
	}

}
