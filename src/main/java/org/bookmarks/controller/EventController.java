package org.bookmarks.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
<<<<<<< HEAD
=======
import java.util.Date;
import java.util.List;
>>>>>>> 27fd69b... First cut of csv sales download
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

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

	@RequestMapping(value="/upload", method=RequestMethod.POST)
<<<<<<< HEAD
	public @ResponseBody String search(MultipartFile files, Long eventId, HttpServletRequest request, ModelMap modelMap) throws IOException {
	 
=======
	public @ResponseBody String upload(MultipartFile files, Long eventId, HttpServletRequest request, ModelMap modelMap) throws IOException {

>>>>>>> 27fd69b... First cut of csv sales download
		if (!ServletFileUpload.isMultipartContent(request)) {
			throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
		}
		 
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
			
		return "searchEvents";
	}
<<<<<<< HEAD
	
	
=======

	/**
	* Text file to upload from mini beans, persist external event sales
	**/
	@RequestMapping(value="/uploadSales", method=RequestMethod.POST)
	public String uploadSales(Event event, HttpSession session, ModelMap modelMap) {

		MultipartFile file = event.getFile();
		String fileName = file.getOriginalFilename();
		Long fileSize = file.getSize();
//		String extension = ".jpg";
//		if(fileName.indexOf(".gif") != -1) extension = ".gif";
//		if(fileName.indexOf(".png") != -1) extension = ".png";

		if(fileName.indexOf(".csv") == -1) {
			addError("The file must be a csv file ", modelMap);
			modelMap.addAttribute("event", new Event());
			return "uploadSales";
		}

		if(fileSize > 100000) {
			addError("File too big!", modelMap);
			return "displayUploadEventImage";
		}

		addSuccess("Have successfully created event and added sales", modelMap);

		return "redirect:/event/displaySearch";
	}
/**
* Text file to upload from mini beans, persist external event sales
**/
	@RequestMapping(value="/uploadSales", method=RequestMethod.GET)
	public String uploadSales(ModelMap modelMap) throws IOException {
		modelMap.addAttribute("event", new Event());
		return "uploadSales";
	}

	/**
	* Generate text file of all sales and invoices on minibeans
	**/
		@RequestMapping(value="/downloadSales", method=RequestMethod.GET)
		public @ResponseBody CSVResponse downloadSales(ModelMap modelMap) throws IOException {

			//
			List<String[]> sales = saleService.getAllForCsv();

			//i.id, si.id, sa.quantity, sa.discount
			List<String[]> invoices = invoiceService.getAllForCsv();

			sales.addAll(invoices);

			return new CSVResponse(sales, "mini-beans.csv");
		}

>>>>>>> 27fd69b... First cut of csv sales download
	@RequestMapping(value="/getJson", method = RequestMethod.GET)
	@ResponseBody
	public Collection<CalendarEvent> getJson() {
		
		EventSearchBean eventSearchBean = new EventSearchBean();
		Calendar c = new GregorianCalendar();
		c.add(Calendar.DAY_OF_YEAR, -30);
		eventSearchBean.getEvent().setStartDate(c.getTime());
		Collection<Event> events = eventService.search(eventSearchBean);
		
		Collection<CalendarEvent> calendarEvents = new ArrayList<CalendarEvent>();
		for(Event e : events) {
			CalendarEvent ce = new CalendarEvent(e);
			calendarEvents.add(ce);
		}


		return calendarEvents;
	}	
	
	@RequestMapping(value="/search")
	public String search(EventSearchBean eventSearchBean, HttpServletRequest request, ModelMap modelMap) {
		if(eventSearchBean == null) { //this is coming from the nav bar
			eventSearchBean = new EventSearchBean();
		}
		setPaginationFromRequest(eventSearchBean, request);

		Collection<Event> events = eventService.search(eventSearchBean);

		modelMap.addAttribute(events);
		modelMap.addAttribute("searchResultCount", eventSearchBean.getSearchResultCount());
		modelMap.addAttribute(EventType.values());

		return "searchEvents";
	}
	
	@RequestMapping(value="/delete")
	public String delete(Long id, HttpServletRequest request, ModelMap modelMap) {
		Event event = new Event();
		event.setId(id);
		try {
			eventService.delete(event);
			addSuccess("Event has been deleted", modelMap);
		} catch(Exception e) {
			e.printStackTrace();
			addError("Cannot delete, most probably because event has sales attached.", modelMap);
		}
		EventSearchBean eventSearchBean = new EventSearchBean();
		modelMap.addAttribute("eventSearchBean", eventSearchBean);
		return search(eventSearchBean, request, modelMap);
	}
	
	@RequestMapping(value="/startSelling")
	public String startSelling(Long eventId, String eventName, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		Event event = new Event();
		event.setId(eventId);
		event.setName(eventName);
		
		session.setAttribute("event", event);
		
		return saleController.displaySellStockItem(modelMap, session);
	}
	
	
	@RequestMapping(value="/showSales")
	public String showSales(Long eventId, String eventName, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		Event event = new Event();
		event.setId(eventId);
		event.setName(eventName);
		
		SaleReportBean saleReportBean = new SaleReportBean(event);
		
//		modelMap.addAttribute(saleReportBean);
		
		modelMap.addAttribute(saleReportBean);
		modelMap.addAttribute(SalesReportType.values());
		modelMap.addAttribute(getCategories(session));
		return "salesReport";		
		
//		return reportController.saleListReport(saleReportBean, request, session, modelMap);
	}	
	
	@RequestMapping(value="/stopSelling")
	public String stopSelling(HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		session.removeAttribute("event");
		
		return "home";
	}

	@RequestMapping(value="/reset", method=RequestMethod.POST)
	public String reset(HttpServletRequest request, ModelMap modelMap) {
		EventSearchBean eventSearchBean = new EventSearchBean();
		modelMap.addAttribute(eventSearchBean);
		return search(eventSearchBean, request, modelMap);
	}	

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String add(@Valid Event event, BindingResult bindingResult, HttpServletRequest request,  HttpSession session, ModelMap modelMap) {
		if(event.getStockItem().getIsbn().trim().equals("")) {
			event.setStockItem(null);
		} else {
			StockItem stockItem = stockItemService.getByISBNAsNumber(event.getStockItem().getIsbn());
			if(stockItem == null) {
				addError("Cannot find stock " + event.getStockItem().getIsbn(), modelMap);
				modelMap.addAttribute(EventType.values());
				return "addEvent";
			}
			event.setStockItem(stockItem);
		}
		
		EventValidator eventValidator = new EventValidator();
		eventValidator.validate(event, bindingResult);
		
		//Check for errors
		if(bindingResult.hasErrors()){
			modelMap.addAttribute(EventType.values());
			return "addEvent";
		}

		eventService.save(event);
		
		//Redirect
		EventSearchBean eventSearchBean = new EventSearchBean();
		eventSearchBean.getEvent().setName(event.getName());
		session.setAttribute("searchBean", eventSearchBean);
		return "redirect:searchFromSession";
	}

	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String displayAdd(ModelMap modelMap) {
		modelMap.addAttribute(new Event());
		modelMap.addAttribute(EventType.values());
		return "addEvent";
	}

@RequestMapping(value="/displayAddFromCalendar", method=RequestMethod.GET)
	public String displayAddFromCalendar(Event event, ModelMap modelMap) {
		modelMap.addAttribute(event);
		modelMap.addAttribute(EventType.values());
		return "addEvent";
	}	
	
	@RequestMapping(value="/searchFromSession")
	public String searchFromSession(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		EventSearchBean eventSearchBean = (EventSearchBean) session.getAttribute("searchBean");
		eventSearchBean.isFromSession(true);
		modelMap.addAttribute(eventSearchBean);
		modelMap.addAttribute(EventType.values());
		return search(eventSearchBean, request, modelMap);
	}	

	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(@Valid Event event, BindingResult bindingResult, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		
		EventValidator eventValidator = new EventValidator();
		eventValidator.validate(event, bindingResult);

		logger.info(event.getDescription());
		
		//Check for errors
		if(bindingResult.hasErrors()){
			modelMap.addAttribute(EventType.values());
			return "editEvent";
		}
		
		if(event.getStockItem().getIsbn().trim().equals("")) {
			event.setStockItem(null);
		} else {
			StockItem stockItem = stockItemService.getByISBNAsNumber(event.getStockItem().getIsbn());
			if(stockItem == null) {
				addError("Cannot find stock " + event.getStockItem().getIsbn(), modelMap);
				modelMap.addAttribute(EventType.values());
				return "editEvent";
			}
			event.setStockItem(stockItem);
		}
		
		if(event.getDescription() != null) {
			String description = event.getDescription();
			description = description.replace("<br/>", "");
			event.setDescription(description);
		}
		logger.info("AFTER:" + event.getDescription());
		if(event.getNote() != null && event.getNote().trim().equals("")){
			event.setNote(null);
		}
		
		eventService.update(event);
		logger.info("AFTER_UPDATE:" + event.getDescription());
		//Redirect
		EventSearchBean eventSearchBean = new EventSearchBean();
		eventSearchBean.getEvent().setName(event.getName());
		session.setAttribute("searchBean", eventSearchBean);
		return "redirect:searchFromSession";
	}

	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String edit(Long id, ModelMap modelMap) {
		Event event = eventService.get(id);

		logger.info(event.getDescription());
		
		modelMap.addAttribute(EventType.values());
		modelMap.addAttribute(event);
		
		return "editEvent";
	}

	@RequestMapping(value="/view", method=RequestMethod.GET)
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
