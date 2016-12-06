package org.bookmarks.controller;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import org.bookmarks.controller.bean.ReorderReviewStockItemBean;
import org.bookmarks.controller.validation.StockItemValidator;
import org.bookmarks.domain.Availablity;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemType;
import org.bookmarks.service.AZLookupService;
import org.bookmarks.service.ChipsService;
import org.bookmarks.service.PublisherService;
import org.bookmarks.service.ReadingListService;
import org.bookmarks.service.ReorderReviewService;
import org.bookmarks.service.Service;
import org.bookmarks.service.StockItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

/**
 * azlookup : Goes to AZ with an isbn and see's if it's there
 * @author hal
 *
 */
@Controller
@RequestMapping(value="/stock")
public class StockItemController extends AbstractBookmarksController<StockItem> {

	@Autowired
	private ReorderReviewService reorderReviewService;

	@Autowired
	private AZLookupService azLookupService;

	@Autowired
	private StockItemService stockItemService;

	@Autowired
	private ReadingListService readingListService;

	@Autowired
	private ChipsService chipsService;

	@Autowired
	private StockItemValidator stockItemValidator;

	@Autowired
	private SupplierDeliveryController supplierDeliveryController;


	@Autowired
	private CustomerOrderController customerOrderController;

	private Logger logger = LoggerFactory.getLogger(StockItemController.class);

	public void setStockItemService(StockItemService stockItemService) {
		this.stockItemService = stockItemService;
	}


	@RequestMapping(value="/addImageToStockItem", method=RequestMethod.GET)
	public String addImageToStockItem(Long id, HttpSession session, ModelMap modelMap) {
		StockItem stockItem = stockItemService.get(id);
		modelMap.addAttribute(stockItem);
		return "displayUploadStockItemImage";
	}

	@RequestMapping(value="/addImageToStockItem", method=RequestMethod.POST)
	public String addImageToStockItem(StockItem stockItem, HttpSession session, ModelMap modelMap) {
		StockItem dbStockItem = stockItemService.get(stockItem.getId());
		dbStockItem.setPutOnWebsite(stockItem.getPutOnWebsite());
		dbStockItem.setPutImageOnWebsite(stockItem.getPutImageOnWebsite());

		MultipartFile file = stockItem.getFile();
		String fileName = file.getOriginalFilename();
		Long fileSize = file.getSize();
//		String extension = ".jpg";
//		if(fileName.indexOf(".gif") != -1) extension = ".gif";
//		if(fileName.indexOf(".png") != -1) extension = ".png";

		if(fileName.indexOf(".jpg") == -1 && fileName.indexOf(".jpeg") == -1) {
			addError("Can only upload jpgs", modelMap);
			return "displayUploadStockItemImage";
		}

		if(fileSize > 100000) {
			addError("File too big!", modelMap);
			return "displayUploadStockItemImage";
		}

		if(fileSize < 1000) {
			addError("File too small!", modelMap);
			return "displayUploadStockItemImage";
		}

		try {
			stockItemService.addImageToStockItem(dbStockItem, file);
		} catch (Exception e) {
			logger.error("Cannot add image to stockitem", e);
			addError("Cannot save image!! " + e.getMessage(), modelMap);
			modelMap.addAttribute(stockItem);
			return "displayUploadStockItemImage";
		}

		addSuccess("Have successfully uploaded image " + fileName, modelMap);
		modelMap.addAttribute(stockItem);
		modelMap.addAttribute("closeWindowNoRefresh", true);
		return "closeWindow";
	}


	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public String delete(StockItem stockItem, HttpSession session, ModelMap modelMap) {
		stockItem = stockItemService.get(stockItem.getId());
		stockItemService.delete(stockItem);
		addSuccess("Have deleted " + stockItem.getTitle(), modelMap);
		StockItemSearchBean stockItemSearchBean = (StockItemSearchBean)session.getAttribute("stockItemSearchBean");
		return displaySearch(stockItemSearchBean, session, modelMap);
	}

	@RequestMapping(value="/buildIndex", method=RequestMethod.GET)
	public String buildIndex(HttpSession session, ModelMap modelMap) {
		stockItemService.buildIndex();
		return displaySearch(new StockItemSearchBean(), session, modelMap);
	}

	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String add(HttpSession session, ModelMap modelMap) {
		session.removeAttribute("flow");

		fillStockSearchModel(session, modelMap);

		StockItem stockItem = new StockItem();

		session.setAttribute("sessionStockItem", stockItem);
		modelMap.addAttribute(stockItem);

		return "addStock";
	}

	@RequestMapping(value="/reset",method=RequestMethod.POST)
	public String reset(ModelMap modelMap, HttpSession session) {
		fillStockSearchModel(session, modelMap);
		modelMap.addAttribute(new StockItemSearchBean());
		return "searchStockItems";
	}

	@RequestMapping(value="/add",method=RequestMethod.POST)
//	@Transactional
	public String add(StockItem stockItem, BindingResult bindingResult, HttpSession session, HttpServletRequest request, ModelMap modelMap) {

		logger.info("Attempting to add stock item " + stockItem.getIsbn() + " : " + stockItem.getTitle());

		StockItem sessionStockItem = (StockItem) session.getAttribute("sessionStockItem");
		if(sessionStockItem == null) {
			return "sessionExpired";
		}
		//TODO why????
		if(sessionStockItem.getPublisher() == null || sessionStockItem.getPublisher().getId() == null && sessionStockItem.getPublisher().getName() == null) {
			sessionStockItem.setPublisher(null);
		}

		if(stockItem.getIsbn().isEmpty() && stockItem.getGenerateISBN() == false) {
			addError("A valid isbn is needed, or click generate isbn if an isbn is not available", modelMap);
			fillStockSearchModel(session, modelMap);
		    modelMap.addAttribute(stockItem);
		    return "addStock";
		}

		//Validate
		stockItemValidator.validate(stockItem, bindingResult);
		if(bindingResult.hasErrors()) {
			fillStockSearchModel(session, modelMap);
		    modelMap.addAttribute(stockItem);
		    return "addStock";
		}

		StockItem existingStock = stockItemService.get(stockItem.getIsbn());
		if(existingStock != null) {
			logger.info("Stock item with isbn " + stockItem.getIsbn() + " already exists!! Aborting!");
			//Abort, and add error message
			fillStockSearchModel(session, modelMap);
			addError("ISBN already exists in beans", modelMap);
			modelMap.addAttribute(stockItem);
			return "addStock";
		}

		//Defaults
		stockItem.setSyncedWithAZ(true);
		stockItem.setIsOnAZ(false);
		stockItem.setIsImageOnAZ(false);

		//Transfer from session object
		stockItem.setAuthors(sessionStockItem.getAuthors());
		if(sessionStockItem.getPublisher() != null) { //Can be null if no info at az, ie manual add
			stockItem.setPublisher(sessionStockItem.getPublisher());
		}

		stockItem.setIsImageOnAZ(sessionStockItem.getIsImageOnAZ());
		stockItem.setIsOnAZ(sessionStockItem.getIsOnAZ());

		//Use service to persist
		stockItemService.create(stockItem);

		String flow = (String) session.getAttribute("flow");

		if(flow != null && flow.equals("supplierDelivery")) {
			SupplierDeliverySearchBean supplierDeliverySearchBean = new SupplierDeliverySearchBean();
			supplierDeliverySearchBean.getStockItem().setIsbn(stockItem.getIsbn());
			session.removeAttribute("flow");

			return supplierDeliveryController.addStock(supplierDeliverySearchBean, bindingResult, session, modelMap);
		}
		if(flow != null && flow.equals("customerOrder")) {
			StockItemSearchBean stockItemSearchBean = new StockItemSearchBean();
			stockItemSearchBean.getStockItem().setIsbn(stockItem.getIsbn());
			session.removeAttribute("flow");
			return customerOrderController.searchStockItems(stockItemSearchBean, request, session, modelMap);
		}

		//Sync with website?
		if(stockItem.getPutOnWebsite() == true) {
			try {
				chipsService.syncStockItemWithChips(stockItem);
				addSuccess("Have successfully added " + stockItem.getTitle() + " and put on website", modelMap);
			} catch (Exception e) {
				addError("Have added stock, but not been able to put on chips: " + e.getMessage(), modelMap);
			}
		} else {
			addWarning("Have successfully added " + stockItem.getTitle() + ", but has not been put on website", modelMap);
		}

		modelMap.addAttribute(stockItem);

		StockItemSearchBean stockItemSearchBean = new StockItemSearchBean();
		stockItemSearchBean.getStockItem().setIsbn(stockItem.getIsbn());

		modelMap.addAttribute(stockItemSearchBean);

		session.removeAttribute("sessionStockItem");

		logger.info("Successfully added");

		return search(stockItemSearchBean, session, request, modelMap);
	}

	@RequestMapping(value="/azlookupFromSearchPage", method=RequestMethod.POST)
	public String azlookupFromSearchPage(StockItemSearchBean stockItemSearchBean, BindingResult bindingResult, HttpSession session, ModelMap modelMap) {
		return azLookup(stockItemSearchBean.getStockItem(), bindingResult, null, session, modelMap);
 	}


	@RequestMapping(value="/azlookup", method=RequestMethod.POST)
	public String azLookup(StockItem stockItem, BindingResult bindingResult, String flow, HttpSession session, ModelMap modelMap) {

		//Check validity of isbn
		stockItemValidator.validateISBN(stockItem, bindingResult, "isbn");
		if(bindingResult.hasErrors()) {
			addInfo("A valid isbn is required for a lookup", modelMap);
			modelMap.addAttribute(stockItem);
			fillStockSearchModel(session, modelMap);
			return "addStock";
		}

		if(stockItem.getIsbn().isEmpty() || stockItem.getIsbn().length() < 10) {
			addInfo("A valid isbn is required for a lookup", modelMap);
			modelMap.addAttribute(stockItem);
			fillStockSearchModel(session, modelMap);
			return "addStock";
		}

		//Looup from AZ
		StockItem stockItemFromLookup = null;
		try {
			stockItemFromLookup = azLookupService.lookupWithJSoup(stockItem.getIsbn());
		} catch (Exception e) {
			logger.error("Cannot lookup from AZLookupService", e);
		}

		//Has it been successful?
		if(stockItemFromLookup == null) {
			addError("Cannot find this isbn at AZ", modelMap);
			StockItem sessionStockItem = (StockItem) session.getAttribute("sessionStockItem");
			if(sessionStockItem == null) {
				sessionStockItem = new StockItem();
				session.setAttribute("sessionStockItem", sessionStockItem);
			}

			sessionStockItem.setIsOnAZ(false);
			fillStockSearchModel(session, modelMap);
			stockItem.setType(StockItemType.BOOK);
			stockItem.setAvailability(Availablity.PUBLISHED);
			modelMap.addAttribute(stockItem);
			return "addStock";
		}

		//Does this ISBN already exist
		StockItem exists = stockItemService.get(stockItem.getIsbn());
		if(exists != null) {
			addError("This isbn is already in the database!", modelMap);
		} else {
			addSuccess("Have successfully looked up  " + stockItemFromLookup.getTitle(), modelMap);
		}

		stockItemFromLookup.setId(stockItem.getId()); //In case this lookup is an overwrite

		fillStockSearchModel(session, modelMap);

		//Transfer to the session stockItem
		StockItem sessionStockItem = (StockItem) session.getAttribute("sessionStockItem");
		if(sessionStockItem == null) { //Can be from lookup from stockSearch.jsp of supplierDelivery.jsp
			sessionStockItem = stockItemFromLookup;
			session.setAttribute("sessionStockItem", stockItemFromLookup);
		}
		sessionStockItem.setAuthors(stockItemFromLookup.getAuthors());
		sessionStockItem.setPublisher(stockItemFromLookup.getPublisher());

		modelMap.addAttribute("stockItem", stockItemFromLookup);
		modelMap.addAttribute("focusId", "category.id");

		return "addStock";
	}

	@RequestMapping(value="/getReview", method=RequestMethod.POST)
	public String getReview(StockItem stockItem, BindingResult bindingResult, String flow, HttpSession session, ModelMap modelMap) {
		String result = "editStock";

		//Check validity of isbn
		stockItemValidator.validateISBN(stockItem, bindingResult, "isbn");
		if(bindingResult.hasErrors()) {
			modelMap.addAttribute(stockItem);
			fillStockSearchModel(session, modelMap);
			return "editStock";
		}

		//Looup from AZ
		StockItem stockItemFromLookup = null;
		try {
			stockItemFromLookup = azLookupService.lookupWithJSoup(stockItem.getIsbn());
		} catch (Exception e) {
			logger.error("Cannot lookup from AZLookupService", e);
		}


		//Has it been successful?
		if(stockItemFromLookup == null) {
			addError("Cannot find this isbn at AZ", modelMap);
			fillStockSearchModel(session, modelMap);
			modelMap.addAttribute(stockItem);
			return "editStock";
		}
		if(stockItemFromLookup.getReviewAsText() != null) {
			stockItem.setReviewAsText(stockItemFromLookup.getReviewAsText());
			stockItem.setReviewAsHTML(stockItemFromLookup.getReviewAsHTML());
		}

		addSuccess("Have successfully got review for " + stockItem.getTitle(), modelMap);
<<<<<<< HEAD
<<<<<<< HEAD
		fillStockSearchModel(session, modelMap);
		modelMap.addAttribute(stockItem);
<<<<<<< HEAD
		return "editStock";	
=======
		return "welcome";
>>>>>>> db9ff92... Adding getReview to edit screen
=======
		fillStockSearchModel(session, modelMap);
		modelMap.addAttribute(stockItem);
		return "editStock";	
>>>>>>> 6b7607c... Fixing
	}	
=======
		return "editStock";
	}
>>>>>>> 85f5394... Formatting

	@RequestMapping(value="/getImage", method=RequestMethod.POST)
	public String getImage(StockItem stockItem, BindingResult bindingResult, String flow, HttpSession session, ModelMap modelMap) {
		String result = "editStock";

		logger.info("About to getImage for : {}", stockItem);

		//Check validity of isbn
		stockItemValidator.validateISBN(stockItem, bindingResult, "isbn");
		if(bindingResult.hasErrors()) {
			modelMap.addAttribute(stockItem);
			fillStockSearchModel(session, modelMap);
			return "editStock";
		}

		//Looup from AZ
		StockItem stockItemFromLookup = null;
		try {
			stockItemFromLookup = azLookupService.lookupWithJSoup(stockItem.getIsbn());
		} catch (Exception e) {
			logger.error("Cannot lookup from AZLookupService", e);
		}


		//Has it been successful?
		if(stockItemFromLookup == null) {
			addError("Cannot find this isbn at AZ", modelMap);
			fillStockSearchModel(session, modelMap);
			modelMap.addAttribute(stockItem);
			return "editStock";
		}
		if(stockItemFromLookup.getImageURL() != null) {
			stockItem.setImageURL(stockItemFromLookup.getImageURL());
			stockItem.setImageFilename(stockItemFromLookup.getImageFilename());
			stockItemService.updateImageFilename(stockItem);
		}

		addSuccess("Have successfully got image for " + stockItem.getTitle(), modelMap);

		logger.info("Got image {} for stock item {}", stockItem.getImageFilename(), stockItem);

		return "welcome";
	}


//	@RequestMapping(value="/isbnDBlookup", method=RequestMethod.POST)
//	public String isbnDBLookup(StockItem stockItem, BindingResult bindingResult, HttpSession session, ModelMap modelMap) {
//
//		//Validate
//		stockItemValidator.validateISBN(stockItem.getIsbn(), bindingResult, "");
//		if(bindingResult.hasErrors()) {
//			fillStockSearchModel(session, modelMap);
//			modelMap.addAttribute(stockItem);
//			return "addStock";
//		}
//
//		StockItem stockItemFromLookup = isbNdbLookupService.lookup(stockItem);
//
//		//Has it been successful?
//		if(stockItemFromLookup == null) {
//			modelMap.addAttribute("message", "Cannot find isbn in ISBN DB lookup");
//			modelMap.addAttribute("stockItem", new StockItem());
//			return "addStock";
//		}
//
//		stockItemFromLookup.setId(stockItem.getId());
//
//		fillStockSearchModel(session, modelMap);
//		modelMap.addAttribute("stockItem", stockItemFromLookup);
//		return "addStock";
//	}
	/*
	@RequestMapping(value="/azlookupOnTitle", method=RequestMethod.POST)
	public String azlookupOnTitle(StockItem stockItem, HttpSession session, ModelMap modelMap) {
		StockItem stockItemFromLookup = azLookupService.lookup(stockItem.getTitle());

		if(stockItemFromLookup == null) {
			modelMap.addAttribute(stockItem);
			modelMap.addAttribute("message", "AZ Lookup returned no results");
		} else {
			modelMap.addAttribute("stockItem", stockItemFromLookup);
		}

		fillStockSearchModel(session, modelMap);
		return "addStock";
	}
	*/
	@RequestMapping(value="/getImageURL", method=RequestMethod.POST)
	public String getImageURL(StockItem stockItem, ModelMap modelMap) {

		String imageURL = stockItemService.getImageURL(stockItem);

		if(imageURL == null) {
			addInfo("Cannot find image on web", modelMap);
			modelMap.addAttribute(stockItem);
			return "editStock";
		}

		modelMap.addAttribute(stockItem);
		return "editStock";
	}

	@RequestMapping(value="/removeFromExtras/{id}")
	public String removeFromExtras(@PathVariable("id") Long id) {

		stockItemService.removeFromExtras( id );
		return "redirect:/sale/displayExtras";
	}


	@RequestMapping(value="/searchFromSession")
	public String searchFromSession(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		StockItemSearchBean stockItemSearchBean = (StockItemSearchBean) session.getAttribute("stockItemSearchBean");
		if(stockItemSearchBean == null) { //Just in case, ie from open in parent
			stockItemSearchBean = new StockItemSearchBean();
		}
		stockItemSearchBean.isFromSession(true);
		modelMap.addAttribute(stockItemSearchBean);
		return search(stockItemSearchBean, session, request, modelMap);
	}

	@RequestMapping(value="/search")
	public String search(@ModelAttribute StockItemSearchBean stockItemSearchBean, HttpSession session, HttpServletRequest request, ModelMap modelMap) {

		StockItem stockItem = stockItemSearchBean.getStockItem();

		Collection<StockItem> stockItems = new ArrayList<StockItem>();

		String errorMessage = stockItemSearchBean.checkValidity();  //Remove use stockItemValidator

		convertToISBN13(stockItem);

		if(errorMessage != null) {
			modelMap.addAttribute("message", errorMessage);
			fillStockSearchModel(session, modelMap);
			modelMap.addAttribute(stockItems);
			return "searchStockItems";
		}

		if(!stockItemSearchBean.isFromSession()) {
			setPaginationFromRequest(stockItemSearchBean, request);
		}

		if(stockItemSearchBean.getReorderReview() != null && stockItemSearchBean.getReorderReview() == true) {
 			stockItemSearchBean.setExport(true); //Hack to remove pagination
		}
		if(stockItemSearchBean.getQ() != null && !stockItemSearchBean.getQ().trim().isEmpty()) {
			stockItems = stockItemService.searchIndex(stockItemSearchBean);
		} else {
			stockItems = stockItemService.search(stockItemSearchBean);
		}

		if(stockItemSearchBean.getReorderReview() != null && stockItemSearchBean.getReorderReview() == true) {
 			List<ReorderReviewStockItemBean> beans = reorderReviewService.getReorderReview(stockItems, stockItemSearchBean);
			session.setAttribute("reorderReviewBeans", beans);
			//Go to reorder review
			return "redirect:/reorderReview/start";
		}


		modelMap.addAttribute("searchResultCount", stockItemSearchBean.getSearchResultCount());
		fillStockSearchModel(session, modelMap);
		modelMap.addAttribute(stockItems);
		setPageSize(stockItemSearchBean, modelMap, stockItems.size());
		stockItemSearchBean.isFromSession(false);

		session.setAttribute("stockItemSearchBean", stockItemSearchBean);


		return "searchStockItems";
	}

	private void removeHyphen(StockItem stockItem) {
		if(stockItem.getIsbn().length() == 14) {
			//Could be from AZ eg. 978-0670918164
			String isbn = stockItem.getIsbn().substring(0, 3) + stockItem.getIsbn().substring(4);
			stockItem.setIsbn(isbn);
		}
	}

	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(StockItemSearchBean stockItemSearchBean, HttpSession session, ModelMap modelMap) {
		fillStockSearchModel(session, modelMap);
		modelMap.addAttribute(stockItemSearchBean);
		return "searchStockItems";
	}

	@RequestMapping(value="/view", method=RequestMethod.GET)
	public String view(Long id, String flow, HttpSession session, ModelMap modelMap) {
		//Get stock item to view and place into request map
		StockItem stockItem = stockItemService.get(id);

		logger.debug("About to view : {}", stockItem);

		//Get binding options
		fillStockSearchModel(session, modelMap);
		modelMap.addAttribute(stockItem);
		modelMap.addAttribute("flow", flow);
		return "viewStock";
	}

	@RequestMapping(value="/toggleIsForMarxism", method=RequestMethod.GET)
	public String toggleIsForMarxism(Long id, boolean isForMarxism, HttpSession session, ModelMap modelMap) {
		//Get stock item to edit and place into request map
		stockItemService.toggleIsForMarxism(id, isForMarxism);

		//This is an Ajax call return appropriate image to display

		if(isForMarxism) {
			return "image1";
		} else return "image2";
	}

	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String edit(Long id, String flow, HttpSession session, ModelMap modelMap) {
		//Get stock item to edit and place into request map
		StockItem stockItem = stockItemService.get(id);

		//Get binding options
		fillStockSearchModel(session, modelMap);
		modelMap.addAttribute(getSuppliers(session));
		modelMap.addAttribute(stockItem);
		modelMap.addAttribute("flow", flow);

		session.setAttribute("sessionStockItem", stockItem);
		session.setAttribute("flow", flow);

		logger.debug("About to edit : {}", stockItem);

		return "editStock";
	}

	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(StockItem stockItem, BindingResult bindingResult, String flow, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		//Validate
		stockItemValidator.validate(stockItem, bindingResult);
		if(bindingResult.hasErrors()) {
			fillStockSearchModel(session, modelMap);
		    modelMap.addAttribute(stockItem);
		    modelMap.addAttribute("flow", flow);
		    return "editStock";
		}

		//Get authors from session
		StockItem sessionStockItem = (StockItem) session.getAttribute("sessionStockItem");
		if(sessionStockItem == null) {
			return "sessionExpired";
		}
		//Have fields been overridden?
		if(!stockItem.getSellPrice().equals(sessionStockItem.getSellPrice())) {
			stockItem.setUpdateSellPrice(false);
		}
		if(!stockItem.getTitle().equals(sessionStockItem.getTitle())) {
			stockItem.setUpdateTitle(false);
		}
		if(!stockItem.getAvailability().equals(sessionStockItem.getAvailability())) {
			stockItem.setUpdateAvailablity(false);
		}
		if(!stockItem.getPublisher().getId().equals(sessionStockItem.getPublisher().getId())) {
			stockItem.setUpdatePublisher(false);
		}

		stockItem.setAuthors(sessionStockItem.getAuthors());

		//Use service to update, also syncs with chips
		try {
			stockItemService.saveOrUpdate(stockItem);
		} catch (Exception e) {
			logger.error("Cannot edit stockitem", e);
			addError("Cannot edit : " + e.getMessage(), modelMap);
			fillStockSearchModel(session, modelMap);
		    modelMap.addAttribute(stockItem);
		    modelMap.addAttribute("flow", flow);
		    return "editStock";
		}

		try {
			chipsService.syncStockItemWithChips(stockItem);
		} catch (Exception e) {
			logger.info("Successfully edited : {} but haven been unable to sync with website {}", stockItem,  e.getMessage());
			logger.error("Cannot sync stockitem after edit ", e);
			addWarning("Have successfully edited, but haven been unable to update website : " + e.getMessage(), modelMap);
		  return "welcome";
		}

		logger.info("Successfully edited : {}", stockItem);

		modelMap.addAttribute("closeWindowNoRefresh", true);
		return "closeWindow";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/sell", method=RequestMethod.GET)
	public String displaySellStockItem(ModelMap modelMap, HttpSession session) {
		StockItem stockItem = new StockItem();
		Collection<Sale> saleItems = (Collection<Sale>) session.getAttribute("stockItemsSold");
		if(saleItems == null) {
			saleItems = new ArrayList<Sale>();
			session.setAttribute("stockItemsSold", saleItems);
		}

		//Put into model
		modelMap.addAttribute(saleItems);
		modelMap.addAttribute(stockItem);
		return "sellStockItem";
	}

	@Override
	public Service getService() {
		return stockItemService;
	}


}
