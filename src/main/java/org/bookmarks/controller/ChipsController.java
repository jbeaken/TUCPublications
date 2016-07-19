package org.bookmarks.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.bookmarks.domain.StockItem;
import org.bookmarks.repository.CustomerRepository;
import org.bookmarks.repository.StockItemRepository;
import org.bookmarks.service.ChipsService;
import org.bookmarks.service.CustomerOrderService;
import org.bookmarks.service.Service;
import org.bookmarks.service.StockItemService;
import org.bookmarks.website.domain.Customer;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/chips")
public class ChipsController extends AbstractBookmarksController {

	@Value("#{ applicationProperties['chipsUrl'] }")
	private String chipsUrl;

 	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private StockItemRepository stockItemRepository;

	@Autowired
	private StockItemService stockItemService;

	@Autowired
	private ChipsService chipsService;

	@Autowired
	private CustomerOrderService customerOrderService;

	 final static Logger logger = LoggerFactory.getLogger(ChipsController.class);

	@RequestMapping(value="/updateChips", method=RequestMethod.GET)
	public String updateChips(HttpSession session, ModelMap modelMap) {
		try {
			chipsService.updateChips();
		} catch (Exception e) {
			logger.error("Cannot update chips ", e);
			addError("Cannot update chips : " + e.getMessage(), modelMap);
			return "welcome";
		}
		addSuccess("Have updated chips", modelMap);
		return "welcome";
	}


	@RequestMapping(value="/updateEvents", method=RequestMethod.GET)
	public String updateEvents(HttpSession session, ModelMap modelMap) {
		try {
			chipsService.updateEvents();
		} catch (Exception e) {
			logger.error("Cannot update events", e);
			addError("Cannot update events : " + e.getMessage(), modelMap);
			return "welcome";
		}
		addSuccess("Have updated events", modelMap);
		return "welcome";
	}

	@RequestMapping(value="/updateReadingLists", method=RequestMethod.GET)
	public String updateReadingLists(HttpSession session, ModelMap modelMap) {
		try {
			chipsService.updateReadingLists();
		} catch (Exception e) {
			addError("Cannot update reading lists : " + e.getMessage(), modelMap);
			return "welcome";
		}
		addSuccess("Have updated reading lists", modelMap);
		return "welcome";
	}
	/*
	@RequestMapping(value="/evictAll", method=RequestMethod.GET)
	public String evictAll(HttpSession session, ModelMap modelMap) {
		try {
			chipsService.evictAll();
		} catch (Exception e) {
			addError("Cannot reset chips : " + e.getMessage(), modelMap);
			return "welcome";
		}
		addSuccess("Have reset beans", modelMap);
		return "welcome";
	}
	*/

	@RequestMapping(value="/uploadBrochure", method=RequestMethod.GET)
	public String uploadBrochure(Long id, HttpSession session, ModelMap modelMap) {
		modelMap.addAttribute( new StockItem() );
		return "displayUploadBrochure";
	}

	@RequestMapping(value="/uploadBrochure", method=RequestMethod.POST)
	public String uploadBrochure(	StockItem stockItem, HttpSession session, ModelMap modelMap) {

		MultipartFile file = stockItem.getFile();
		String fileName = file.getOriginalFilename();
		Long fileSize = file.getSize();
//		String extension = ".jpg";
//		if(fileName.indexOf(".gif") != -1) extension = ".gif";
//		if(fileName.indexOf(".png") != -1) extension = ".png";

		if(fileName.indexOf(".pdf") == -1 && fileName.indexOf(".jpeg") == -1) {
			addError("Can only upload pdfs", modelMap);
			modelMap.addAttribute(stockItem);
			return "displayUploadBrochure";
		}



		if(fileSize < 50000) {
			addError("File too small!", modelMap);
			modelMap.addAttribute(stockItem);
			return "displayUploadBrochure";
		}

		try {
			chipsService.uploadBrochure(file.getInputStream());
		} catch (Exception e) {
			logger.error("Cannot upload brochure", e);
			addError("Cannot upload brochure!! " + e.getMessage(), modelMap);
			modelMap.addAttribute(stockItem);
			return "displayUploadBrochure";
		}

		addSuccess("Have successfully uploaded brochure ", modelMap);
		return "welcome";
	}

	@RequestMapping(value="/removeFromWebsite", method=RequestMethod.GET)
	public String removeFromWebsite(Long id, HttpSession session, ModelMap modelMap) {
		chipsService.removeFromWebsite(id);
		modelMap.addAttribute("closeWindowNoRefresh", true);
		return "closeWindow";
	}

	@RequestMapping(value="/putOnWebsite", method=RequestMethod.GET)
	public String putOnWebsite(Long id, HttpSession session, ModelMap modelMap) {
		chipsService.putOnWebsite(id);
		modelMap.addAttribute("closeWindowNoRefresh", true);
		return "closeWindow";
	}

	@RequestMapping(value="/syncStockItemWithChips", method=RequestMethod.GET)
	public String syncStockItemWithChips(Long id, ModelMap modelMap)  {

		StockItem stockItem = stockItemService.get(id);

		try {
			chipsService.syncStockItemWithChips(stockItem);
			if(stockItem.getPutOnWebsite()) {
				addSuccess("Successfully updated " + stockItem.getTitle(), modelMap);
			} else addSuccess("Have removed stock item " + stockItem.getTitle(), modelMap);
		} catch (Exception e) {
			e.printStackTrace();
			addError("Cannot sync with chips! " + e.getMessage(), modelMap);
		}

		return "welcome";
	}

	@RequestMapping(value="/buildIndex", method=RequestMethod.GET)
	public String buildIndex(ModelMap modelMap)  {

		long start = System.currentTimeMillis();

		logger.info("Building index on chips");

		try {
			chipsService.buildIndex();
			long end = System.currentTimeMillis();
			long time = (end - start) / 1000;
			addSuccess("Successfully build chips index in " + time + " seconds", modelMap);
			logger.info("Success!! Build index");
		} catch (Exception e) {
			e.printStackTrace();
			addError("Error building Chips index " + e.getMessage(), modelMap);
		}

		return "welcome";
	}

	@RequestMapping(value="/removeConsumedCustomers")
	public String removeConsumedCustomers(ModelMap modelMap) {
		try {
			chipsService.removeConsumedCustomers();
		} catch (Exception e) {
			addError("Cannot put stock item on website! " + e.getMessage(), modelMap);
			return "chipsTransferReport";
		}
		addSuccess("Have removed all consumed customers", modelMap);
		return "chipsTransferReport";
	}

	@RequestMapping(value="/getOrders")
	public String getOrders(ModelMap modelMap) {

		List<Customer> chipsCustomers;
<<<<<<< HEAD
=======

>>>>>>> 49e2612... Added upload brochure functionality
		try {
			chipsCustomers = chipsService.getOrders();
		} catch (Exception e) {
			logger.error("Cannot get orders", e);
			addError("Cannot get orders from chips! " + e.getMessage(), modelMap);
			return "chipsTransferReport";
		}

<<<<<<< HEAD
=======
		if(chipsCustomers == null) {
			return "chipsTransferReport";
		}

>>>>>>> 49e2612... Added upload brochure functionality
		try {
			customerOrderService.saveChipsOrders(chipsCustomers);
		} catch (Exception e) {
			logger.error("Cannot get orders", e);
			addError("Have got orders, but beans cannot process them :  " + e.getMessage(), modelMap);
			return "chipsTransferReport";
		}

		addSuccess("Have retrieved " + chipsCustomers.size() + " orders from chips" , modelMap);

		modelMap.addAttribute(chipsCustomers);

		return "chipsTransferReport";
	}

	@Override
	public Service getService() {
		return null;
	}
}
