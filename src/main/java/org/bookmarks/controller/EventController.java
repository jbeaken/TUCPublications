package org.bookmarks.controller;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.format.number.CurrencyStyleFormatter;

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
	private SaleService saleService;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private SaleReportController reportController;

	@Value("#{ applicationProperties['imageFileLocation'] }")
	private String imageFileLocation;

	private Logger logger = LoggerFactory.getLogger(EventController.class);

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody String uploadImage(MultipartFile files, Long eventId, HttpServletRequest request, ModelMap modelMap) throws IOException {

		if (!ServletFileUpload.isMultipartContent(request)) {
			throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
		}

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

		return "searchEvents";
	}

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

		logger.info("Uploading sales for new Event " + ReflectionToStringBuilder.toString(event));

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

			// System.out.println(creationDateStr);
			// System.out.println(quantity);
			// System.out.println(discount);
			// System.out.println(stockItemId);

			Date creationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(creationDateStr);

			// Sale
			Sale s = new Sale();
			s.setQuantity(Long.valueOf(quantity));
			s.setDiscount(new BigDecimal(discount));
			s.setVat(new BigDecimal(vat));
			s.setSellPrice(new BigDecimal(sellPrice));
			s.setEvent(event);
			s.setCreationDate(creationDate);

			StockItem si = new StockItem();
			si.setId(Long.valueOf(stockItemId));

			s.setStockItem(si);

			if (type.equals("S")) {
				sales.add(s);
			} else if (type.equals("I")) {
				// Invoice
				Long customerId = Long.valueOf(record.get(7));
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

			total = total + (s.getQuantity() * s.getSellPrice().floatValue());
		}

		String totalFormatted = new CurrencyStyleFormatter().print( total, java.util.Locale.UK );

		session.setAttribute("salesForUpload", sales);
		session.setAttribute("invoicesForUpload", invoiceMap.values());
		session.setAttribute("eventForUpload", event);

		addSuccess("Have found sales of value " + totalFormatted + " for " + event.getName() + ". Press confirm to save", modelMap);

		return "confirmUploadSales";
	}

	@RequestMapping(value = "/confirmUploadSales", method = RequestMethod.GET)
	public String confirmUploadSales(HttpSession session, ModelMap modelMap) throws IOException, java.text.ParseException {

		Collection<Invoice> invoices = (Collection<Invoice>) session.getAttribute( "invoicesForUpload" );
		List<Sale> sales = (List<Sale>)session.getAttribute( "salesForUpload" );
		Event event = (Event)session.getAttribute("eventForUpload");

		// Persist sales and decrements stock level
		sales.forEach (	s -> saleService.sell(s, event.getSkipUpdatingStockRecord(), true) );

		// Persist invoices. This will decrement stock as well
		for (Invoice i : invoices) {
			invoiceService.save(i, i.getSales(), null, event);
		}

		//Clear session objects

		return "redirect:/events/search";
	}


	/**
	 * Text file to upload from mini beans, persist external event sales
	 **/
	@RequestMapping(value = "/uploadSales/{id}", method = RequestMethod.GET)
	public String uploadSales(@PathVariable("id") Long id, ModelMap modelMap) throws IOException {
		Event event = eventService.get(id);

		modelMap.addAttribute("event", event);

		return "uploadSales";
	}

	/**
	 * Generate text file of all sales and invoices on minibeans
	 **/
	@RequestMapping(value = "/downloadSales", method = RequestMethod.GET)
	public @ResponseBody CSVResponse downloadSales() throws IOException {

		logger.info("Request to download sales");

		// sa.quantity, sa.discount, sa.sellPrice, sa.vat, si.id
		List<String[]> sales = saleService.getAllForCsv();

		logger.info("Have {} sales", sales.size());

		// sa.quantity, sa.discount, sa.sellPrice, sa.vat, si.id, i.customerId
		List<String[]> invoices = invoiceService.getAllForCsv();

		logger.info("Have {} invoices", invoices.size());

		sales.addAll(invoices);

		logger.info("Saving response to mini-beans.csv");

		return new CSVResponse(sales, "mini-beans.csv");
	}

	@RequestMapping(value = "/getJson", method = RequestMethod.GET)
	@ResponseBody
	public Collection<CalendarEvent> getJson(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date start, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date end) {

		logger.info("Search for calendar using date range {} - {}", start, end);

		EventSearchBean eventSearchBean = new EventSearchBean();

		Calendar c = new GregorianCalendar();
		c.add(Calendar.DAY_OF_YEAR, -30);

		eventSearchBean.getEvent().setStartDate(start);
		eventSearchBean.getEvent().setEndDate(end);

		logger.debug("Search for calendar using search bean {}", eventSearchBean);

		Collection<Event> events = eventService.search(eventSearchBean);

		Collection<CalendarEvent> calendarEvents = new ArrayList<CalendarEvent>();

		logger.debug("Have {} events in date range", events.size());

		for (Event e : events) {
			CalendarEvent ce = new CalendarEvent(e);
			calendarEvents.add(ce);
			logger.debug("Adding {}", ce);
		}

		logger.debug("Have {} events to send to client", calendarEvents.size());

		return calendarEvents;
	}

	@RequestMapping(value = "/search")
	public String search(EventSearchBean eventSearchBean, HttpServletRequest request, ModelMap modelMap) {
		if (eventSearchBean == null) { // this is coming from the nav bar
			eventSearchBean = new EventSearchBean();
		}
		setPaginationFromRequest(eventSearchBean, request);

		Collection<Event> events = eventService.search(eventSearchBean);

		// Don't like, fix for shitty export
		setPageSize(eventSearchBean, modelMap, events.size());

		modelMap.addAttribute(events);
		modelMap.addAttribute("searchResultCount", eventSearchBean.getSearchResultCount());
		modelMap.addAttribute(EventType.values());

		return "searchEvents";
	}

	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable("id") Long id, HttpServletRequest request, ModelMap modelMap) {

		Event event = eventService.get(id);

		try {
			eventService.delete(event);
			addSuccess("Event " + event.getName() + " has been deleted", modelMap);
		} catch (Exception e) {
			e.printStackTrace();
			addError("Cannot delete event, most probably because sales have been attached.", modelMap);
		}

		EventSearchBean eventSearchBean = new EventSearchBean();
		modelMap.addAttribute("eventSearchBean", eventSearchBean);

		logger.info("Deleted event : {}", event.getName());

		return search(eventSearchBean, request, modelMap);
	}

	@RequestMapping(value = "/startSelling/{id}")
	public String startSelling(@PathVariable("id") Long id, HttpServletRequest request, HttpSession session, ModelMap modelMap) {

		Event event = eventService.get( id );

		addSuccess("Any sales or invoices created will now be attached to event " + event.getName() + ". To stop, click 'Events - Stop Selling'", modelMap);

		session.setAttribute("event", event);

		return saleController.displaySellStockItem(modelMap, session);
	}

	@RequestMapping(value = "/showSales/{id}")
	public String showSales(@PathVariable("id") Long id, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		Event event = eventService.get( id );

		SaleReportBean saleReportBean = new SaleReportBean(event);

		modelMap.addAttribute(saleReportBean);
		modelMap.addAttribute(SalesReportType.values());
		modelMap.addAttribute(getCategories(session));

		return "salesReport";
	}

	@RequestMapping(value = "/stopSelling")
	public String stopSelling(HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		session.removeAttribute("event");

		addSuccess("Have stopped selling for event", modelMap);
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

		// Check for errors
		if (bindingResult.hasErrors()) {
			modelMap.addAttribute(EventType.values());
			return "addEvent";
		}

		eventService.save(event);

		// Redirect
		EventSearchBean eventSearchBean = new EventSearchBean();
		eventSearchBean.getEvent().setName(event.getName());
		session.setAttribute("searchBean", eventSearchBean);

		logger.info("Added event : {}", ReflectionToStringBuilder.toString(event));

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
	}

	@RequestMapping(value = "/searchFromSession")
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

		// Check for errors
		if (bindingResult.hasErrors()) {
			modelMap.addAttribute(EventType.values());
			return "editEvent";
		}

		if (event.getStockItem().getIsbn().trim().equals("")) {
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

		if (event.getDescription() != null) {
			String description = event.getDescription();
			description = description.replace("<br/>", "");
			event.setDescription(description);
		}
		if (event.getNote() != null && event.getNote().trim().equals("")) {
			event.setNote(null);
		}

		eventService.update(event);

		// Redirect
		EventSearchBean eventSearchBean = new EventSearchBean();
		eventSearchBean.getEvent().setName(event.getName());
		session.setAttribute("searchBean", eventSearchBean);

		logger.info("Edited event : " + event.getName());

		return "redirect:searchFromSession";
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String edit(@PathVariable("id") Long id, ModelMap modelMap) {
		Event event = eventService.get(id);

		modelMap.addAttribute(EventType.values());
		modelMap.addAttribute(event);

		return "editEvent";
	}

	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String view(@PathVariable("id") Long id, ModelMap modelMap) {

		Event event = eventService.get(id);
		modelMap.addAttribute(event);

		return "viewEvent";
	}

	@Override
	public Service getService() {
		return eventService;
	}

}
