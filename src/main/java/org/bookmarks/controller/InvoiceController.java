package org.bookmarks.controller;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.bookmarks.controller.validation.InvoiceValidator;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.website.domain.DeliveryType;
import org.bookmarks.domain.Event;
import org.bookmarks.domain.Invoice;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.service.InvoiceService;
import org.bookmarks.service.Service;
import org.bookmarks.service.StockItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.bookmarks.exceptions.BookmarksException;

@Controller
@RequestMapping("/invoice")
public class InvoiceController extends AbstractBookmarksController<Invoice> {

	@Autowired
	private StockItemService stockItemService;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private InvoiceValidator invoiceValidator;

	@Value("#{applicationProperties['vatNumber']}")
	private String vatNumber;

	private Logger logger = LoggerFactory.getLogger(InvoiceController.class);

	/**
	 * From createInvoice.jsp
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/searchStockItems")
	public String searchStockItems(@ModelAttribute StockItemSearchBean stockItemSearchBean, HttpServletRequest request, ModelMap modelMap, HttpSession session) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");
		Invoice invoice = (Invoice) session.getAttribute("invoice");

		String errorMessage = stockItemSearchBean.checkValidityForISBN();
		if(errorMessage != null) {
			addError(errorMessage, modelMap);
			fillModel(invoice, saleMap, modelMap);
			modelMap.addAttribute(stockItemSearchBean);
			if(session.getAttribute("isEditInvoice") != null) {
				return "editInvoice";
			}
			return "createInvoice";
		}
		String isbn = stockItemSearchBean.getStockItem().getIsbn();
		StockItem stockItem = null;
		try {
			stockItem = stockItemService.getByISBNAsNumber(isbn);
		} catch(NumberFormatException e) {
			modelMap.addAttribute("message", "Invalid isbn!");
			fillModel(invoice, saleMap, modelMap);
			if(session.getAttribute("isEditInvoice") != null) {
				return "editInvoice";
			}
			return "createInvoice";
		}

		if(stockItem == null) {
			modelMap.addAttribute("message", "Cannot find isbn in database!");
			fillModel(invoice, saleMap, modelMap);
			if(session.getAttribute("isEditInvoice") != null) {
				return "editInvoice";
			}
			return "createInvoice";
		}

		invoiceService.addStockItem(stockItem, saleMap, invoice);

		fillModel(invoice, saleMap, modelMap);

		if(session.getAttribute("isEditInvoice") != null) {
			return "editInvoice";
		}
		return "createInvoice";
	}

	private void fillModel(Invoice invoice,	Map<Long, Sale> saleMap, ModelMap modelMap) {
		fillModel(invoice, saleMap, modelMap, true);
	}

	private void fillModel(Invoice invoice,	Map<Long, Sale> saleMap, ModelMap modelMap, boolean calculateDiscount) {
		logger.debug("In fillModel");

		invoice.calculate(saleMap.values(), calculateDiscount);

		if(!invoice.getIsProforma()) {
			if(invoice.getCustomer().getBookmarksAccount() != null && invoice.getCustomer().getBookmarksAccount().getCurrentBalance() != null) {
				modelMap.addAttribute("newBalance", invoice.getCustomer().getBookmarksAccount().getCurrentBalance().subtract(invoice.getTotalPrice()));
			} else {
				modelMap.addAttribute("newBalance", "0");
			}
		}
		modelMap.addAttribute(saleMap.values());
		modelMap.addAttribute(DeliveryType.values());
		modelMap.addAttribute(invoice);
		modelMap.addAttribute(new StockItemSearchBean());
	}

	//Persist the new invoice
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/save")
	public String save(@Valid Invoice invoice, BindingResult bindingResult, ModelMap modelMap, HttpServletRequest request, HttpSession session) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");
		Map<Long, CustomerOrderLine> customerOrderLineMapForInvoice = (Map<Long, CustomerOrderLine>) session.getAttribute("customerOrderLineMapForInvoice");

		Customer customer = invoice.getCustomer();

		//Need a validator
		if(invoice.getPaid() == false
				&& customer.getBookmarksAccount().getAccountHolder() == false
				&& invoice.getIsProforma() == false) {
			fillModel(invoice, saleMap, modelMap, false); //Already been calculated

			modelMap.addAttribute(new StockItemSearchBean());

			addInfo("Customer " + customer.getFullName() + " does not have an account and invoice is unpaid!", modelMap);

			logger.info("Customer {} does not have an account and invoice is unpaid! Not saving!", customer.getFullName());

			if(session.getAttribute("isEditInvoice") != null) {
				return "editInvoice";
			}

			return "createInvoice";
		}
		if(bindingResult.hasErrors()) {
			fillModel(invoice, saleMap, modelMap);
			modelMap.addAttribute(new StockItemSearchBean());
			if(session.getAttribute("isEditInvoice") != null) {
				return "editInvoice";
			}
			return "createInvoice";
		}

		Event event = (Event) session.getAttribute("event");

		try {
			invoiceService.save(invoice, saleMap.values(), customerOrderLineMapForInvoice, event);
		} catch(BookmarksException e) {

			fillModel(invoice, saleMap, modelMap, false); //Already been calculated

			modelMap.addAttribute(new StockItemSearchBean());

			addInfo("Cannot save. Reason is : " + e.getMessage(), modelMap);

			logger.error("Cannot save invoice", e);

			if(session.getAttribute("isEditInvoice") != null) {
				return "editInvoice";
			}
			return "createInvoice";
		}

		modelMap.addAttribute(invoice);
		modelMap.addAttribute(saleMap.values());

		session.removeAttribute("orderLineMap");
		session.removeAttribute("customerOrderLineMapForInvoice");
		session.removeAttribute("invoice");
		session.removeAttribute("isEditInvoice");
		session.removeAttribute("originalInvoicePrice");

		InvoiceSearchBean invoiceSearchBean = new InvoiceSearchBean();
		invoiceSearchBean.getInvoice().setCustomer(customer);
		invoiceSearchBean.setSortOrder("DESC");
		invoiceSearchBean.setSortColumn("i.id");
		session.setAttribute("invoiceSearchBean", invoiceSearchBean);

		addSuccess("Invoice successfully created for " + customer.getFullName(), modelMap);

		if(customer.getBookmarksAccount().getAccountHolder() == true) {
			logger.info("New Balance : {}", customer.getBookmarksAccount().getCurrentBalance());
		}

		logger.info("Invoice successfully created for {}", customer.getFullName());
		logger.info("*****************************");

		return "redirect:searchFromSession";
	}

	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String edit(Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, Sale> saleMap = new HashMap<Long, Sale>();

		//Place into session
		Invoice invoice = invoiceService.get(id);
		Customer customer = invoice.getCustomer();

		for(Sale sale : invoice.getSales()) {
			sale.setDiscountHasBeenOverridden(true);
			saleMap.put(sale.getStockItem().getId(), sale);
		}
		session.setAttribute("invoice", invoice);
		session.setAttribute("orderLineMap", saleMap);
		session.setAttribute("isEditInvoice", true);
		session.setAttribute("originalInvoicePrice", invoice.getTotalPrice());

		logger.info("Initialisation of edit invoice for {}", customer);

		//Place into model
		fillModel(invoice, saleMap, modelMap, false);
		return "editInvoice";
	}

	@RequestMapping(value="/setAsPaid", method=RequestMethod.GET)
	public String setAsPaid(boolean paid, HttpSession session, ModelMap modelMap) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");
		Invoice invoice = (Invoice) session.getAttribute("invoice");

		invoice.setPaid(paid);

		logger.info("Have set paid flag to " + paid);

		//Place into model
		fillModel(invoice, saleMap, modelMap, false);
		if(session.getAttribute("isEditInvoice") != null) {
			return "editInvoice";
		}
		return "createInvoice";
	}

	@RequestMapping(value="/setAsProforma", method=RequestMethod.GET)
	public String setAsProforma(boolean isProforma, HttpSession session, ModelMap modelMap) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");
		Invoice invoice = (Invoice) session.getAttribute("invoice");

		invoice.setIsProforma(isProforma);

		logger.info("Have set isProforma flag to " + isProforma);

		//Place into model
		fillModel(invoice, saleMap, modelMap, false);
		return "createInvoice";
	}


	@RequestMapping(value="/setAsUpdateStock", method=RequestMethod.GET)
	public String setUpdateStock(boolean updateStock, HttpSession session, ModelMap modelMap) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");
		Invoice invoice = (Invoice) session.getAttribute("invoice");

		invoice.setUpdateStock(updateStock);

		logger.info("Have set updateStock flag to " + updateStock);

		//Place into model
		fillModel(invoice, saleMap, modelMap, false);
		return "createInvoice";
	}

//	@RequestMapping(value="/redoStockItemQuantity", method=RequestMethod.GET)
//	public String redoStockItemQuantity(Long id, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
//		Invoice invoice = invoiceService.get(id);
//
//		for(Sale sale : invoice.getSales()) {
//			stockItemService.updateQuantities(sale.getStockItem(), sale.getQuantity() * -1, null, null, null, null);
//		}
//
//		return searchFromSession(session, request, modelMap);
//	}

	@RequestMapping(value="/setAsCollection", method=RequestMethod.GET)
	public String setAsCollection(Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");

		Invoice invoice = (Invoice) session.getAttribute("invoice");
		invoice.setDeliveryType(DeliveryType.COLLECTION);

		logger.info("Have set invoice delivery flag to " + DeliveryType.COLLECTION);

		//Place into model
		fillModel(invoice, saleMap, modelMap);
		return "createInvoice";
	}

	public String raiseCustomerOrderInvoice(HttpSession session, ModelMap modelMap) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");

		Invoice invoice = (Invoice) session.getAttribute("invoice");

		//Place into model
		fillModel(invoice, saleMap, modelMap);
		return "createInvoice";
	}


	@RequestMapping(value="/setAsMail", method=RequestMethod.GET)
	public String setAsMail(Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");
		Invoice invoice = (Invoice) session.getAttribute("invoice");
		invoice.setDeliveryType(DeliveryType.MAIL);

		logger.info("Have set invoice delivery flag to " + DeliveryType.MAIL);

		//Place into model
		fillModel(invoice, saleMap, modelMap);
		if(session.getAttribute("isEditInvoice") != null) {
				return "editInvoice";
			}
			return "createInvoice";
	}



	@RequestMapping(value="/searchFromSession")
	public String searchFromSession(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		InvoiceSearchBean invoiceSearchBean = (InvoiceSearchBean) session.getAttribute("invoiceSearchBean");
		invoiceSearchBean.isFromSession(true);
		modelMap.addAttribute(invoiceSearchBean);
		return search(invoiceSearchBean, request, session, modelMap);
	}

	@RequestMapping(value="/view", method=RequestMethod.GET)
	public String view(Long id, String flow, Long customerOrderLineId, HttpSession session, ModelMap modelMap) {
		Invoice invoice = invoiceService.get(id);

		//if refresh, still need invoice in session, how to get around it?
		modelMap.addAttribute(invoice);
		modelMap.addAttribute("flow", flow);
		modelMap.addAttribute("customer", invoice.getCustomer());
		modelMap.addAttribute("customerOrderLineId", customerOrderLineId);
		modelMap.addAttribute("totalPrice", invoice.getTotalPrice());

		return "viewInvoice";
	}

	@RequestMapping(value="/print", method=RequestMethod.GET)
	public String print(Long invoiceId, ModelMap modelMap) {
		Invoice invoice = invoiceService.get(invoiceId);

		//Need to calculate price
//		invoice.calculate();

		//String vatNumber = PropertiesUtil.getProperty("vatNumber");

		modelMap.addAttribute(invoice);
		modelMap.addAttribute("customer", invoice.getCustomer());
		modelMap.addAttribute("totalPrice", invoice.getTotalPrice());
		modelMap.addAttribute("vatNumber", vatNumber);

		return "printInvoice";
	}

	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public String delete(Long id, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		Invoice invoice = invoiceService.get(id);
		try {
			invoiceService.delete(invoice);
			addSuccess("Invoice has been deleted", modelMap);
		} catch (Exception e) {
			//Most likely due to this invoice being referenced from col
			addError("Cannot delete! Perhaps this invoice is part of a customer order, contact admin (Jack) to sort it out", modelMap);
		}

		InvoiceSearchBean invoiceSearchBean = (InvoiceSearchBean) session.getAttribute("invoiceSearchBean");
		return search(invoiceSearchBean, request, session, modelMap);
	}

	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(@Valid Invoice invoice, BindingResult bindingResult, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");
		Map<Long, CustomerOrderLine> customerOrderLineMapForInvoice = (Map<Long, CustomerOrderLine>) session.getAttribute("customerOrderLineMapForInvoice");

		if(bindingResult.hasErrors()) {
			fillModel(invoice, saleMap, modelMap);
			modelMap.addAttribute(new StockItemSearchBean());
			return "createInvoice";
		}

		BigDecimal originalInvoicePrice = (BigDecimal) session.getAttribute("originalInvoicePrice");

		invoiceService.update(invoice, saleMap.values(), customerOrderLineMapForInvoice, originalInvoicePrice);

		modelMap.addAttribute(invoice);
		modelMap.addAttribute(saleMap.values());

		session.removeAttribute("orderLineMap");
		session.removeAttribute("customerOrderLineMapForInvoice");
		session.removeAttribute("invoice");
		session.removeAttribute("isEditInvoice");
		session.removeAttribute("originalInvoicePrice");

		addSuccess("Invoice has been edited", modelMap);

		logger.info("Edit oft invoice for {} successful", invoice.getCustomer());

		return searchFromSession(session, request, modelMap);
	}

	@RequestMapping(value="/addStockItem")
	public String addStockItem(Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");
		Invoice invoice = (Invoice) session.getAttribute("invoice");

		invoiceService.addStockItem(id, saleMap, invoice);

		fillModel(invoice, saleMap, modelMap);
		modelMap.addAttribute(new StockItemSearchBean());

		if(session.getAttribute("isEditInvoice") != null) {
			return "editInvoice";
		}
		return "createInvoice";
	}

	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		return search(new InvoiceSearchBean(), request, session, modelMap);
	}

	@RequestMapping(value="/search")
	public String search(InvoiceSearchBean invoiceSearchBean, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		if(invoiceSearchBean.isFromSession() == false) { //Pagination etc. already set
			setPaginationFromRequest(invoiceSearchBean, request);
		}

		Collection<Invoice> invoices = invoiceService.search(invoiceSearchBean);

		modelMap.addAttribute(invoices);
		modelMap.addAttribute(DeliveryType.values());
		modelMap.addAttribute("customerOrderStatusOptions", CustomerOrderLineStatus.values());
		modelMap.addAttribute("searchResultCount", invoiceSearchBean.getSearchResultCount());

		//Place in session for later access
		session.setAttribute("invoiceSearchBean", invoiceSearchBean);
		modelMap.addAttribute(invoiceSearchBean);

		return "searchInvoices";
	}

	@RequestMapping(value="/showAddAdditionalCharges", method=RequestMethod.GET)
	public String showAddAdditionalCharges(ModelMap modelMap, HttpSession session) {
		Map<Long, Sale> orderLineMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");
		Invoice invoice = (Invoice) session.getAttribute("invoice");

		modelMap.addAttribute(invoice);
		modelMap.addAttribute("secondHandPrice", invoice.getSecondHandPrice());

		if(session.getAttribute("isEditInvoice") != null) {
			return "editInvoice";
		}
		return "createInvoice";
	}

	@RequestMapping(value="/addAdditionalCharges", method=RequestMethod.POST)
	public String addAddtionalCharges(Invoice invoice, BindingResult bindingResult, ModelMap modelMap, HttpSession session) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");
		//invoiceValidator.validateAdditionalCharges(invoice, bindingResult);
		if(bindingResult.hasErrors()) {
			modelMap.addAttribute(invoice);
			modelMap.addAttribute("secondHandPrice", invoice.getSecondHandPrice());
			if(session.getAttribute("isEditInvoice") != null) {
				return "editInvoice";
			}
		    return "createInvoice";
		}

		//Quick fix, if either are null, set to 0
		if(invoice.getSecondHandPrice() == null) {
			invoice.setSecondHandPrice(new BigDecimal(0));
		}
		if(invoice.getServiceCharge() == null) {
			invoice.setServiceCharge(new BigDecimal(0));
		}
		session.setAttribute("invoice", invoice);

		//invoice.calculate(saleMap.values(), true);
		fillModel(invoice, saleMap, modelMap);

		logger.info("Adding additional charges for {}. Second Hand : {}, Service Charge {}", invoice.getCustomer(), invoice.getSecondHandPrice(), invoice.getServiceCharge());

		if(session.getAttribute("isEditInvoice") != null) {
			return "editInvoice";
		}
		return "createInvoice";
	}


	/**
	 * id refers to stockItem id, or -1 if 2nd hand
	 * @param id
	 * @param modelMap
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/editInvoiceOrderLine", method=RequestMethod.GET)
	public String editInvoiceOrderLine(Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");
		Invoice invoice = (Invoice) session.getAttribute("invoice");

		Sale saleToEdit = saleMap.get(id);
		saleToEdit.setNewDiscount(saleToEdit.getDiscount());
		modelMap.addAttribute("saleToEdit", saleToEdit);

//		fillModel(invoice, saleMap, modelMap);
		if(session.getAttribute("isEditInvoice") != null) {
			return "editInvoice";
		}
		return "createInvoice";
	}

	/**
	 * @param id
	 * @param modelMap
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/editInvoiceOrderLine", method=RequestMethod.POST)
	public String editInvoiceOrderLine(Sale sale, BindingResult bindingResult, ModelMap modelMap, HttpSession session) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");
		Invoice invoice = (Invoice) session.getAttribute("invoice");

		//Need to check whether discount has been overridden
		if(sale.getDiscount().floatValue() != sale.getNewDiscount().floatValue()) {
			sale.setDiscountHasBeenOverridden(Boolean.TRUE);
			sale.setDiscount(sale.getNewDiscount());
		}

			//Validate
//		invoiceOrderLineValidator.validate(sale, bindingResult);
		if(bindingResult.hasErrors()) {
			fillModel(invoice, saleMap, modelMap);
			modelMap.addAttribute("saleToEdit", sale);
			if(session.getAttribute("isEditInvoice") != null) {
				return "editInvoice";
			}
		    return "createInvoice";
		}
//		invoice.calculate(saleMap.values()); //Recalculate, changes may have been made
		saleMap.put(sale.getStockItem().getId(), sale);

		fillModel(invoice, saleMap, modelMap);
		modelMap.addAttribute(new StockItemSearchBean());

		//Why?
		modelMap.remove("saleToEdit");

		if(session.getAttribute("isEditInvoice") != null) {
			return "editInvoice";
		}
		return "createInvoice";
	}

	@RequestMapping(value="/deleteInvoiceOrderLine")
	public String deleteInvoiceOrderLine(Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");
		Invoice invoice = (Invoice) session.getAttribute("invoice");

		saleMap.remove(id);

		//invoice.calculate(saleMap.values(), true);
		fillModel(invoice, saleMap, modelMap);

		modelMap.addAttribute(new StockItemSearchBean());

		if(session.getAttribute("isEditInvoice") != null) {
			return "editInvoice";
		}
		return "createInvoice";
	}

	/**
	 * Get the customer for which the invoice is for
	 * create session objects and then redirect
	 * to /invoice/createInvoice.jsp
	 * @param customerId
	 * @param modelMap
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/init")
	public String init(Long customerId, ModelMap modelMap, HttpSession session) {
		Map<Long, Sale> saleMap = new HashMap<Long, Sale>();

		//Place into session
		Invoice invoice = invoiceService.getNewInvoice(customerId);
		Customer customer = invoice.getCustomer();
		session.setAttribute("invoice", invoice);
		session.setAttribute("orderLineMap", saleMap);
		session.removeAttribute("isEditInvoice");
		logger.info("*****************************");
		logger.info("Starting initialisation of invoice for " + customer.getId() + " : " + customer.getFullName());

		//Check if customer has an account, otherwise warn must be paid or a proforma
		if(customer.getBookmarksAccount().getAccountHolder() == false) {
			logger.info(customer.getFullName() + " is not an account holder");
			addInfo(customer.getFullName() + " is not an account holder, either edit customer and check account box, or 'set at paid' if you are certain customer has paid", modelMap);
		} else if(customer.getBookmarksAccount().getCurrentBalance().floatValue() < 0) {
			logger.info(customer.getFullName() + " is not in credit");
			addError(customer.getFullName() + " is not in credit! Do not save this invoice without approval!", modelMap);
		} else {
			logger.info(customer.getFullName() + " has balance " + customer.getBookmarksAccount().getCurrentBalance().floatValue());
		}

		//Place into model
		fillModel(invoice, saleMap, modelMap);

		logger.info("Successfully initiated invoice for " + customer.getFullName());

		return "createInvoice";
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
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");
		Invoice invoice = (Invoice) session.getAttribute("invoice");

		//Place into model
		fillModel(invoice, saleMap, modelMap);
		modelMap.addAttribute(new StockItemSearchBean());
		return "createInvoice";
	}

	@RequestMapping(value="/cancel")
	public String cancel(ModelMap modelMap, HttpServletRequest request, HttpSession session) {
		Invoice invoice = (Invoice) session.getAttribute("invoice");

		session.removeAttribute("invoice");
		session.removeAttribute("orderLineMap");
		if(session.getAttribute("customerOrder") == null)  {
			session.removeAttribute("customerOrderLineMapForInvoice");
		}
		session.removeAttribute("isEditInvoice");
		session.removeAttribute("originalInvoicePrice");

		logger.info("User has cancelled invoice for " + invoice.getCustomer().getFullName());
		return search(new InvoiceSearchBean(), request, session, modelMap);
	}

	@Override
	public Service getService() {
		return invoiceService;
	}

}
