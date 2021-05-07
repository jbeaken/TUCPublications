package org.bookmarks.controller;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.bookmarks.domain.StockItem;
import org.bookmarks.repository.CustomerRepository;
import org.bookmarks.repository.StockItemRepository;
import org.bookmarks.service.ChipsService;
import org.bookmarks.service.CustomerOrderService;
import org.bookmarks.service.Service;
import org.bookmarks.service.StockItemService;
import org.bookmarks.website.domain.WebsiteCustomer;

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

	@Value("#{ applicationProperties['chips.get.orders'] }")
	private Boolean chipsGetOrders;

	@Autowired
	private StockItemService stockItemService;

	@Autowired
	private ChipsService chipsService;

	private final Logger logger = LoggerFactory.getLogger(ChipsController.class);

	@RequestMapping(value = "/updateChips", method = RequestMethod.GET)
	public String updateChips(HttpSession session, ModelMap modelMap) {
		logger.info("Attempting to update chips");
		
		String response = null;
		
		try {
			response = chipsService.updateChips();
			logger.debug("Update response : {}", response);
		} catch (Exception e) {
			logger.error("Cannot update chips ", e);
			addError("Cannot update chips : " + e.getMessage(), modelMap);
			return "welcome";
		}
		
		if(response.equals("success")) {
			addSuccess("Have updated chips", modelMap);
			logger.info("Successfully updated chips!");
		} else {
			addError("Cannot update chips : " + response, modelMap);
		}
		return "welcome";
	}

	@RequestMapping(value = "/updateEvents", method = RequestMethod.GET)
	public String updateEvents(HttpSession session, ModelMap modelMap) {

		logger.info("Attempting to update events on chips");
		
		String result = chipsService.updateEvents();
		
		if(result.equals("success")) {
			addSuccess("Have updated events", modelMap);	
			logger.info("Successfully updated events on chips!");
		} else {
			logger.error("Cannot update events! {}", result);
			addError("Cannot update events " + result, modelMap);			
		}

		return "welcome";
	}

	@RequestMapping(value = "/updateReadingLists", method = RequestMethod.GET)
	public String updateReadingLists(HttpSession session, ModelMap modelMap) {

		logger.info("Attempting to update reading lists on chips");

		try {
			String result = chipsService.updateReadingLists();
			addSuccess("Have updated events", modelMap);
			logger.info("Successfully updated events on chips!");
		} catch (Exception e) {
			logger.error("Cannot update reading lists! {}", e);
			addError("Cannot update reading lists " + e.getMessage(), modelMap);
		}

		return "welcome";
	}

	@RequestMapping(value = "/uploadBrochure", method = RequestMethod.GET)
	public String uploadBrochure(Long id, HttpSession session, ModelMap modelMap) {
		modelMap.addAttribute(new StockItem());
		return "displayUploadBrochure";
	}

	@RequestMapping(value = "/uploadBrochure", method = RequestMethod.POST)
	public String uploadBrochure(StockItem stockItem, HttpSession session, ModelMap modelMap) {
		logger.info("Attempting to upload brouchure");

		MultipartFile file = stockItem.getFile();
		String fileName = file.getOriginalFilename();
		Long fileSize = file.getSize();

		if (fileName.indexOf(".pdf") == -1 && fileName.indexOf(".jpeg") == -1) {
			logger.error("Brouchure filename {} is not a pdf", fileName);
			addError("Can only upload pdfs", modelMap);
			modelMap.addAttribute(stockItem);
			return "displayUploadBrochure";
		}

		if (fileSize < 50000) {
			addError("File too small!", modelMap);
			logger.error("Brouchure file is too small {}", fileSize);
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
		logger.info("Successfully uploaded brochure!");

		addSuccess("Have successfully uploaded brochure ", modelMap);
		return "welcome";
	}

	@RequestMapping(value = "/removeFromWebsite", method = RequestMethod.GET)
	public String removeFromWebsite(Long id, HttpSession session, ModelMap modelMap) {
		chipsService.removeFromWebsite(id);
		modelMap.addAttribute("closeWindowNoRefresh", true);
		return "closeWindow";
	}

	@RequestMapping(value = "/putOnWebsite", method = RequestMethod.GET)
	public String putOnWebsite(Long id, HttpSession session, ModelMap modelMap) {
		chipsService.putOnWebsite(id);
		modelMap.addAttribute("closeWindowNoRefresh", true);
		return "closeWindow";
	}

	@RequestMapping(value = "/syncStockItemWithChips", method = RequestMethod.GET)
	public String syncStockItemWithChips(Long id, ModelMap modelMap) {

		StockItem stockItem = stockItemService.get(id);

		String response = null;
		
		try {
			
			response = chipsService.syncStockItemWithChips(stockItem);
			
			if(response.equals("success")) {
				if (stockItem.getPutOnWebsite()) {
					addSuccess("Successfully updated " + stockItem.getTitle(), modelMap);
				} else {
					addSuccess("Have removed stock item " + stockItem.getTitle(), modelMap);
				}
			} else if(response.equals("imagefailure")) {
				addError("Put item on website but unable to upload image! " + response, modelMap);
			} else  {
				addError("Cannot sync with chips! " + response, modelMap);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			addError("ERROR!! Cannot sync with chips! " + e.getMessage(), modelMap);
		}

		return "welcome";
	}

	@RequestMapping(value = "/buildIndex", method = RequestMethod.GET)
	public String buildIndex(ModelMap modelMap) {

		long start = System.currentTimeMillis();

		logger.info("Building index on chips");

		try {

			chipsService.buildIndex();

			long end = System.currentTimeMillis();
			long time = (end - start) / 1000;

			addSuccess("Successfully built chips index in " + time + " seconds", modelMap);

			logger.info("Successfully built chips index in " + time + " seconds");
		} catch (Exception e) {
			logger.error("Cannot build index", e);
			addError("Error building Chips index " + e.getMessage(), modelMap);
		}

		return "welcome";
	}

	@RequestMapping(value = "/removeConsumedCustomers")
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

	@RequestMapping(value = "/getOrders")
	public String getOrders(ModelMap modelMap) {

		logger.info("User manual request for getOrders started");

		if (chipsGetOrders != true) {
			logger.info("Aborting getOrders(), turned off");
			addWarning("Retrieval of chips orders is turned off!", modelMap);
			return "chipsTransferReport";
		}

		try {

			Collection<WebsiteCustomer> chipsCustomers = chipsService.getOrders();

			addSuccess("Have retrieved " + chipsCustomers.size() + " orders from chips", modelMap);

			modelMap.addAttribute(chipsCustomers);

			logger.info("User request for getOrders successful");

		} catch (Exception e) {
			logger.error("Cannot get orders", e);

			addError("Cannot get orders from chips! " + e.getMessage(), modelMap);
		}

		return "chipsTransferReport";
	}

	@Override
	public Service getService() {
		return null;
	}
}
