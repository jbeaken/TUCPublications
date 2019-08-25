package org.bookmarks.controller;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bookmarks.controller.validation.CustomerOrderValidator;
import org.bookmarks.controller.validation.StockItemValidator;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.Source;
import org.bookmarks.website.domain.DeliveryType;
import org.bookmarks.domain.Invoice;
import org.bookmarks.website.domain.PaymentType;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.service.AZLookupService;
import org.bookmarks.service.CustomerOrderLineService;
import org.bookmarks.service.CustomerOrderService;
import org.bookmarks.service.CustomerService;
import org.bookmarks.service.EmailService;
import org.bookmarks.service.StockItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;

@Controller
@RequestMapping(value="/customerOrder")
public class CustomerOrderController extends AbstractBookmarksController {

	@Autowired
	private CustomerOrderService customerOrderService;

	@Autowired
	private SupplierDeliveryController supplierDeliveryController;

	@Autowired
	private CustomerOrderLineService customerOrderLineService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private AZLookupService azLookupService;

	@Autowired
	private StockItemValidator stockItemValidator;

	@Autowired
	private CustomerOrderValidator customerOrderValidator;

	private Logger logger = LoggerFactory.getLogger(CustomerOrderController.class);

	@Autowired
	private EmailService emailService;


	@Autowired
	private StockItemService stockItemService;

	@InitBinder(value="customerOrder")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	/**
	 * Get stuff from session
	 * @param id
	 * @param modelMap
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/continue")
	public String continueFromSession(ModelMap modelMap, HttpSession session) {
		Map<Long , CustomerOrderLine> customerOrderLineMap = (Map<Long , CustomerOrderLine>) session.getAttribute("customerOrderLineMap");
		CustomerOrder customerOrder = (CustomerOrder) session.getAttribute("customerOrder");

		fillModelForAddStock(customerOrder, customerOrderLineMap, session, modelMap);

		return "selectStockItemsForCustomerOrder";
	}

	/**
	 * Most likely coming from search customer screen, about to display initial
	 * screen for customer order
	 * @param customerId
	 * @param session
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/init")
	public String init(Long customerId, HttpSession session, ModelMap modelMap) {

		// First get customer
		Customer customer = customerService.get( customerId );

		// Create customer order
		CustomerOrder customerOrder = new CustomerOrder();
		customerOrder.setCustomer(customer);


		//Container for customer order lines
		Map<Long , CustomerOrderLine> customerOrderLineMap = new HashMap<Long, CustomerOrderLine>();

		session.setAttribute("customerOrder", customerOrder);
		session.setAttribute("customerOrderLineMap", customerOrderLineMap);


		fillModelForAddStock(customerOrder, customerOrderLineMap, session, modelMap);

		addInfo("Add stock items to customer order, when complete click proceed to checkout", modelMap);

		logger.info("Successfully initialised customer order creation for {} " + customerOrder.getCustomer());
		if(customerOrder.getCustomer().getContactDetails() != null) {
			logger.info("Customer has email");
			logger.debug("Email : {}", customerOrder.getCustomer().getContactDetails().getEmail());
		} else {
			logger.info("No contact details given");
		}
		logger.debug("Customer : {}", customerOrder.getCustomer());

		return "selectStockItemsForCustomerOrder";
	}

	private void fillModelForCreateCustomerOrder(CustomerOrder customerOrder,
			HttpSession session, ModelMap modelMap) {
		modelMap.addAttribute(customerOrder);
		modelMap.addAttribute(PaymentType.values());
		modelMap.addAttribute(DeliveryType.values());
	}


	/**
	 * @param id
	 * @param session
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/partialInform")
	public String partialInform(Long id, HttpSession session, ModelMap modelMap) {
		//Initialise a new customer order into session, replace previous order if necessary
		CustomerOrder customerOrder = customerOrderService.selectCustomer(id);

		Map<Long , CustomerOrderLine> customerOrderLineMap = new HashMap<Long, CustomerOrderLine>();

		session.setAttribute("customerOrder", customerOrder);
		session.setAttribute("customerOrderLineMap", customerOrderLineMap);

		modelMap.addAttribute(customerOrder);
		modelMap.addAttribute(PaymentType.values());
		modelMap.addAttribute(DeliveryType.values());

		return "createCustomerOrder";
	}

	@RequestMapping(value="/cancel")
	public String cancel(HttpSession session, ModelMap modelMap) {

		CustomerOrder customerOrder = (CustomerOrder) session.getAttribute("customerOrder");

		session.removeAttribute("customerOrder");
		session.removeAttribute("customerOrderLineMap");

		logger.info("Cancelled creating order for - " + customerOrder.getCustomer().getId() + " : " + customerOrder.getCustomer().getFullName());

		return "welcome";
	}

	@RequestMapping(value="/searchStockItems")
	public String searchStockItems(StockItemSearchBean stockItemSearchBean, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		CustomerOrder customerOrder = (CustomerOrder) session.getAttribute("customerOrder");
		Map<Long , CustomerOrderLine> customerOrderLineMap = (Map<Long, CustomerOrderLine>) session.getAttribute("customerOrderLineMap");
		Collection<StockItem> stockItems = new ArrayList<StockItem>();
		String errorMessage = stockItemSearchBean.checkValidity();

		if(errorMessage == null) {
			setPaginationFromRequest(stockItemSearchBean, request);
			stockItems = stockItemService.search(stockItemSearchBean);
			modelMap.addAttribute("searchResultCount", stockItemSearchBean.getSearchResultCount());
		} else {
			modelMap.addAttribute("message", errorMessage);
			fillModelForAddStock(customerOrder, customerOrderLineMap, session, modelMap);
			modelMap.addAttribute(stockItemSearchBean);
			return "selectStockItemsForCustomerOrder";
		}

		if(stockItems.size() == 1) {
			//Add to map if not already there
			StockItem stockItem = stockItems.iterator().next();
			if(!customerOrderLineMap.containsKey(stockItem.getId())) {
				customerOrderLineMap.put(stockItem.getId(), new CustomerOrderLine(stockItem));
			}
			addInfo("Stock item added to order, add more stock or go to check out", modelMap);
			logger.info("Added stock item to customer order : " + stockItem.getTitle());
		} else if(stockItems.size() > 1){
			addWarning("More than one result found, select correct one by clicking A", modelMap);
			modelMap.addAttribute(stockItems);
		} else if(stockItems.size() == 0) {
			//Check isbn is valid
			String isbn = stockItemSearchBean.getStockItem().getIsbn();
			if(isbn.isEmpty()) { //Cannot do isbn lookup
				addError("Cannot find stock in database", modelMap);
				fillModelForAddStock(customerOrder, customerOrderLineMap, session, modelMap);
				modelMap.addAttribute(stockItemSearchBean);
				return "selectStockItemsForCustomerOrder";
			}

			errorMessage = stockItemValidator.validateISBN(isbn);
			if(errorMessage != null) { //Cannot do isbn lookup
				addError(errorMessage, modelMap);
				fillModelForAddStock(customerOrder, customerOrderLineMap, session, modelMap);
				modelMap.addAttribute(stockItemSearchBean);
				return "selectStockItemsForCustomerOrder";
			}

			//Lookup isbn and send to stock/addStock.jsp
			StockItem stockItem = null;
			try {
				stockItem = azLookupService.lookupWithJSoup(isbn);
				session.setAttribute("sessionStockItem", stockItem);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(stockItem == null) {
				stockItem = new StockItem();
				stockItem.setIsbn(isbn);
				addInfo("Cannot find isbn at AZ, please add manually", modelMap);
				logger.info("Cannot find stock item, needs to be added manually");
			}

			//This should link to stockItemController.displayAdd
			fillStockSearchModel(session, modelMap);

			modelMap.addAttribute(stockItem);
			modelMap.addAttribute("focusId", "category.id");
			session.setAttribute("flow", "customerOrder");

			return "addStock";
		}

		fillModelForAddStock(customerOrder, customerOrderLineMap, session, modelMap);

		return "selectStockItemsForCustomerOrder";
	}

	private void fillModelForAddStock(CustomerOrder customerOrder, Map<Long, CustomerOrderLine> customerOrderLineMap, HttpSession session, ModelMap modelMap) {
		modelMap.addAttribute(new StockItemSearchBean());
		modelMap.addAttribute(getCategories(session));
		modelMap.addAttribute(getPublishers(session));
		modelMap.addAttribute(customerOrder);
		modelMap.addAttribute(customerOrderLineMap.values());
	}

	@RequestMapping(value="/addStock")
	public String addStock(CustomerOrder customerOrder, HttpSession session, ModelMap modelMap) {
		Map<Long , CustomerOrderLine> customerOrderLineMap = (Map<Long, CustomerOrderLine>) session.getAttribute("customerOrderLineMap");
		session.setAttribute("customerOrder", customerOrder); //Info in note may have been added so save to session
		fillModelForAddStock(customerOrder, customerOrderLineMap, session, modelMap);
		return "selectStockItemsForCustomerOrder";
	}

	/**
	 * Returning from selectStockItemsForCustomerOrder screen, so just prepare model
	 * @param session
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/displayCustomerOrder", method=RequestMethod.GET)
	public String displayCustomerOrder(HttpSession session, ModelMap modelMap) {
		CustomerOrder customerOrder = (CustomerOrder) session.getAttribute("customerOrder");
		Map<Long , CustomerOrderLine> customerOrderLineMap = (Map<Long, CustomerOrderLine>) session.getAttribute("customerOrderLineMap");
		if(customerOrderLineMap.size() == 0) {
			addWarning("No stock items selected for order, are you sure you want a research customer order? If not, click add stock to add stock items", modelMap);
		} else {
			addInfo("Select payment and delivery type, add an optional note if neccessary, click create order to save", modelMap);
		}

		modelMap.addAttribute(customerOrder);
		modelMap.addAttribute(customerOrderLineMap.values());
		modelMap.addAttribute(PaymentType.values());
		modelMap.addAttribute(DeliveryType.values());
		modelMap.addAttribute(Source.values());
		return "createCustomerOrder";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/addSingleStockItem")
	public String addSingleStockItem(Long id, HttpSession session, ModelMap modelMap) {
		CustomerOrder customerOrder = (CustomerOrder) session.getAttribute("customerOrder");
		Map<Long , CustomerOrderLine> customerOrderLineMap = (Map<Long, CustomerOrderLine>) session.getAttribute("customerOrderLineMap");
		StockItem stockItem = stockItemService.get(id);

		CustomerOrderLine customerOrderLine = customerOrderLineMap.get(stockItem.getId());
		if(customerOrderLine == null) {
			customerOrderLine = new CustomerOrderLine(stockItem);
			customerOrderLineMap.put(stockItem.getId(), customerOrderLine);
		} else {
			customerOrderLine.setAmount(customerOrderLine.getAmount() + 1);
		}

		addInfo("Stock item added to order, add more stock or go to check out", modelMap);
		fillModelForAddStock(customerOrder, customerOrderLineMap, session, modelMap);

		return "selectStockItemsForCustomerOrder";
	}

	@RequestMapping(value="/editCustomerOrderLine", method=RequestMethod.GET)
	public String displayEditCustomerOrderLine(Long stockItemId, HttpSession session, ModelMap modelMap) {
		Map<Long , CustomerOrderLine> customerOrderLineMap = (Map<Long, CustomerOrderLine>) session.getAttribute("customerOrderLineMap");
		CustomerOrder customerOrder = (CustomerOrder) session.getAttribute("customerOrder");

		CustomerOrderLine customerOrderLine = customerOrderLineMap.get(stockItemId);

		//fillModelForAddStock(customerOrder, customerOrderLineMap, session, modelMap);

		modelMap.addAttribute(customerOrderLine);

		return "selectStockItemsForCustomerOrder";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/editCustomerOrderLine", method=RequestMethod.POST)
	public String editCustomerOrderLine(CustomerOrderLine customerOrderLine, BindingResult bindingResult, HttpSession session, ModelMap modelMap) {
		Map<Long , CustomerOrderLine> customerOrderLineMap = (Map<Long, CustomerOrderLine>) session.getAttribute("customerOrderLineMap");
		CustomerOrder customerOrder = (CustomerOrder) session.getAttribute("customerOrder");

		if(customerOrderLine.getAmount() < 0) {
			bindingResult.rejectValue("amount", "invalid", "Amount must be above zero");
		}

		if(bindingResult.hasErrors()) {
			modelMap.addAttribute(customerOrderLine);
		} else {
			CustomerOrderLine customerOrderLineFromMap = customerOrderLineMap.get(customerOrderLine.getStockItem().getId());
			customerOrderLineFromMap.setAmount(customerOrderLine.getAmount());
			customerOrderLineFromMap.setIsSecondHand(customerOrderLine.getIsSecondHand());
			//Why do I have to do this?
			modelMap.remove("customerOrderLine");
		}

		fillModelForAddStock(customerOrder, customerOrderLineMap, session, modelMap);

		return "selectStockItemsForCustomerOrder";
	}

	@RequestMapping(value="/deleteCustomerOrderLine")
	public String deleteCustomerOrderLine(Long stockItemId, HttpSession session, ModelMap modelMap) {
		Map<Long , CustomerOrderLine> customerOrderLineMap = (Map<Long, CustomerOrderLine>) session.getAttribute("customerOrderLineMap");
		CustomerOrder customerOrder = (CustomerOrder) session.getAttribute("customerOrder");

		customerOrderLineMap.remove(stockItemId);

		fillModelForAddStock(customerOrder, customerOrderLineMap, session, modelMap);
		addInfo("Stock item removed from order", modelMap);
		return "selectStockItemsForCustomerOrder";
	}

	@RequestMapping(value="/setAsResearch")
	public String setAsResearch(Long stockItemId, HttpSession session, ModelMap modelMap) {
		Map<Long , CustomerOrderLine> customerOrderLineMap = (Map<Long, CustomerOrderLine>) session.getAttribute("customerOrderLineMap");
		CustomerOrder customerOrder = (CustomerOrder) session.getAttribute("customerOrder");

		CustomerOrderLine customerOrderLine =  customerOrderLineMap.get(stockItemId);
		if(customerOrderLine.getIsResearch() == Boolean.TRUE) {
			customerOrderLine.setIsResearch(Boolean.FALSE);
		} else {
			customerOrderLine.setIsResearch(Boolean.TRUE);
		}

		fillModelForAddStock(customerOrder, customerOrderLineMap, session, modelMap);

		return "selectStockItemsForCustomerOrder";
	}

	@RequestMapping(value="/viewCustomerOrder")
	public String viewCustomerOrder(HttpSession session, ModelMap modelMap) {
		//Two options, from id or from session
		CustomerOrder customerOrder = (CustomerOrder) session.getAttribute("customerOrder");

		modelMap.addAttribute(customerOrder);
		modelMap.addAttribute(customerOrder.getCustomer());
		modelMap.addAttribute(customerOrder.getCustomerOrderline());

		//Clean up session, (refersh causes id needed for service error though)
		session.removeAttribute("customerOrder");
		addInfo("Successfully created order for " + customerOrder.getCustomer().getFirstName(), modelMap);

		return "viewCustomerOrder";
	}


	@RequestMapping(value="/create", method=RequestMethod.POST)
	public String create(@Valid CustomerOrder customerOrder, BindingResult bindingResult,  HttpSession session, ModelMap modelMap) {
		Map<Long , CustomerOrderLine> customerOrderLineMap = (Map<Long, CustomerOrderLine>) session.getAttribute("customerOrderLineMap");

		//Validator
		customerOrderValidator.validate(customerOrder, bindingResult);

		Customer customer = customerOrder.getCustomer();

    	if(customerOrderLineMap.values().isEmpty() && (customerOrder.getNote() == null || customerOrder.getNote().trim().isEmpty())) {
    		addError("If the order has no stock items, it is a research order, please add a note describing what should be ordered, or add stock.", modelMap);
		    modelMap.addAttribute(customerOrder);
		    modelMap.addAttribute(customerOrderLineMap.values());
			modelMap.addAttribute(PaymentType.values());
			modelMap.addAttribute(DeliveryType.values());
		    return "createCustomerOrder";
    	}
		if(bindingResult.hasErrors()) {
			addError("Please correct errors", modelMap);
		    modelMap.addAttribute(customerOrder);
		    modelMap.addAttribute(customerOrderLineMap.values());
			modelMap.addAttribute(PaymentType.values());
			modelMap.addAttribute(DeliveryType.values());
		    return "createCustomerOrder";
		}

		//Persist customer order
		customerOrderService.save(customerOrder, customerOrderLineMap.values());

		String email = customer.getContactDetails().getEmail();


		//Send confirmation email

		logger.debug("Email for customer order is '{}'", email);

		if(email != null) {
			//could have been updated in order screen
			customerService.updateEmail(customerOrder.getCustomer());
			try {
				emailService.sendCustomerOrderConfirmationEmail(customerOrder);
				logger.info("Successfully sent confirmation email");
			} catch (Exception e) {
				logger.error("Cannot send confirmation email to " + email, e);
			}
		} else {
			logger.info("Cannot send confirmation email as customer has no email address");
		}

		//Clean up session
		session.removeAttribute("customerOrderLineMap");
		session.removeAttribute("customerOrder");
		session.setAttribute("success", "Successfully created order for " + customerOrder.getCustomer().getFullName());

		logger.info("Successfully created order for - " + customerOrder.getCustomer().getId() + " : " + customerOrder.getCustomer().getFullName());
		logger.info( ReflectionToStringBuilder.toString(customerOrder) );

		//Redirect to search from session
		session.setAttribute("customerOrderSearchBean", new CustomerOrderLineSearchBean(customerOrder.getCustomer()));
		return "redirect:/customerOrderLine/searchFromSession";
	}

	@Override
	public org.bookmarks.service.Service getService() {
		// TODO Auto-generated method stub
		return null;
	}

}
