package org.bookmarks.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bookmarks.controller.helper.ISBNConvertor;
import org.bookmarks.controller.validation.CustomerOrderValidator;
import org.bookmarks.controller.validation.StockItemValidator;
import org.bookmarks.controller.validation.SupplierDeliveryLineValidator;
import org.bookmarks.controller.validation.SupplierInvoiceValidator;
import org.bookmarks.domain.Binding;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.website.domain.DeliveryType;
import org.bookmarks.domain.Invoice;
import org.bookmarks.domain.InvoiceOrderLine;
import org.bookmarks.domain.Review;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.website.domain.PaymentType;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.SupplierDelivery;
import org.bookmarks.domain.SupplierOrder;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.domain.SupplierOrderStatus;
import org.bookmarks.service.AZLookupService;
import org.bookmarks.service.CustomerOrderLineService;
import org.bookmarks.service.CustomerOrderService;
import org.bookmarks.service.CustomerService;
import org.bookmarks.service.Service;
import org.bookmarks.service.StockItemService;
import org.bookmarks.service.SupplierDeliveryService;
import org.bookmarks.service.SupplierOrderService;
import org.bookmarks.service.SupplierService;
import org.bookmarks.ui.comparator.GardnersDeliveryComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping(value="/supplierDelivery")
public class SupplierDeliveryController extends OrderLineController {

	@Autowired
	private StockItemService stockItemService;

	@Autowired
	private StockItemController stockItemController;

	@Autowired
	private StockItemValidator stockItemValidator;

	@Autowired
	private AZLookupService azLookupService;

	@Autowired
	private SupplierService supplierService;

	@Autowired
	private SupplierDeliveryService supplierDeliveryService;

	@Autowired
	private CustomerOrderLineService customerOrderLineService;
	
	@Autowired
	private SupplierDeliveryLineValidator supplierDeliveryLineValidator;

	private Logger logger = LoggerFactory.getLogger(StockItemController.class);

	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		SupplierDeliverySearchBean supplierDeliverySearchBean = new SupplierDeliverySearchBean();
		return search(supplierDeliverySearchBean, request, session, modelMap);
	}


	@RequestMapping(value="/setGlobalDiscount", method=RequestMethod.GET)
	public String setGlobalDiscount(ModelMap modelMap) {
		//Use a SupplierDeliveryLine instance to hold discount
		modelMap.addAttribute(new SupplierDeliveryLine());
		return "setGlobalDiscount";
	}
	
	@RequestMapping(value="/setGlobalDiscount", method=RequestMethod.POST)
	public String setGlobalDiscount(SupplierDeliveryLine supplierDeliveryLine, HttpSession session, ModelMap modelMap) {
		Map<Long, SupplierDeliveryLine> supplierDeliveryLineMap = (Map<Long, SupplierDeliveryLine>) session.getAttribute("supplierDeliveryLinesMap");
		SupplierDelivery supplierDelivery = (SupplierDelivery) session.getAttribute("supplierDelivery");

		for(SupplierDeliveryLine sol : supplierDeliveryLineMap.values()) {
			sol.setDiscount(supplierDeliveryLine.getDiscount());
			//Calculate individual sdl price
			supplierDeliveryService.calculatePrice(supplierDelivery, sol);
		}

		//Sort
		List<SupplierDeliveryLine> list = new ArrayList<SupplierDeliveryLine>(supplierDeliveryLineMap.values());
		Collections.sort(list, new GardnersDeliveryComparator());
		modelMap.addAttribute(list);
		modelMap.addAttribute(supplierDelivery);
		modelMap.addAttribute(new SupplierDeliverySearchBean());

		calculateTotalPrice(supplierDeliveryLineMap.values(), modelMap);	
		modelMap.remove("supplierDeliveryLine");
	
		return "createSupplierDelivery";
	}	
	
	@RequestMapping(value="/search")
	public String search(SupplierDeliverySearchBean supplierDeliverySearchBean, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		setPaginationFromRequest(supplierDeliverySearchBean, request);

		Collection<SupplierDelivery> supplierDeliveries = supplierDeliveryService.search(supplierDeliverySearchBean);

		modelMap.addAttribute(supplierDeliveries);
		modelMap.addAttribute(getSuppliers(session));
		modelMap.addAttribute(supplierDeliverySearchBean);
		modelMap.addAttribute("searchResultCount", supplierDeliverySearchBean.getSearchResultCount());

		//Save for return later
		session.setAttribute("savedSearchBean", supplierDeliverySearchBean);
		return "searchSupplierDeliveries";
	}	
	/**
	 * id refers to stockItem id, or -1 if 2nd hand
	 * @param id
	 * @param modelMap
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/editSupplierDeliveryOrderLine", method=RequestMethod.GET)
	public String editSupplierDeliveryLine(Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, SupplierDeliveryLine> orderLineMap = (Map<Long, SupplierDeliveryLine>) session.getAttribute("supplierDeliveryLinesMap");
		SupplierDelivery supplierDelivery = (SupplierDelivery) session.getAttribute("supplierDelivery");

		SupplierDeliveryLine supplierDeliveryLine = orderLineMap.get(id);

		modelMap.addAttribute(supplierDelivery);
		modelMap.addAttribute("supplierDeliveryLine", supplierDeliveryLine);
		modelMap.addAttribute(new SupplierDeliverySearchBean());
		modelMap.addAttribute(orderLineMap.values());
//		fillStockSearchModel(session, modelMap);

		return "createSupplierDelivery";
	}

	/**
	 * @param id
	 * @param modelMap
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/editSupplierDeliveryOrderLine", method=RequestMethod.POST)
	public String editSupplierDeliveryLine(SupplierDeliveryLine supplierDeliveryLine, BindingResult bindingResult, ModelMap modelMap, HttpSession session) {
		Map<Long, SupplierDeliveryLine> supplierDeliveryLineMap = (Map<Long, SupplierDeliveryLine>) session.getAttribute("supplierDeliveryLinesMap");
		SupplierDelivery supplierDelivery = (SupplierDelivery) session.getAttribute("supplierDelivery");

		//Validate
		supplierDeliveryLineValidator.validate(supplierDeliveryLine, bindingResult);
		if(bindingResult.hasErrors()) {
			modelMap.addAttribute(supplierDelivery);
			modelMap.addAttribute(DeliveryType.values());
			modelMap.addAttribute(supplierDeliveryLineMap.values());
			modelMap.addAttribute(supplierDeliveryLine);
		    return "createSupplierDelivery";
		}

		supplierDeliveryService.calculatePrice(supplierDelivery, supplierDeliveryLine);
		supplierDeliveryLineMap.put(supplierDeliveryLine.getStockItem().getId(), supplierDeliveryLine);

		//Sort
		List<SupplierDeliveryLine> list = new ArrayList<SupplierDeliveryLine>(supplierDeliveryLineMap.values());
		Collections.sort(list, new GardnersDeliveryComparator());
		modelMap.addAttribute(list);
		modelMap.addAttribute(supplierDelivery);
		modelMap.addAttribute(new SupplierDeliverySearchBean());
		modelMap.addAttribute("lastSupplierDeliveryLine", supplierDeliveryLine);

		//Why?
		modelMap.remove("supplierDeliveryLine");
		calculateTotalPrice(supplierDeliveryLineMap.values(), modelMap);

		return "createSupplierDelivery";
	}

	//private void getTotalPrice(orderLineMap)

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/displayCustomerOrderLinesToFill")
	public String displayCustomerOrderLinesToFill(Long stockItemId, ModelMap modelMap, HttpSession session) {
		Map<Long, SupplierDeliveryLine> supplierDeliveryLinesMap = (Map<Long, SupplierDeliveryLine>) session.getAttribute("supplierDeliveryLinesMap");
		SupplierDeliveryLine supplierDeliveryLine = supplierDeliveryLinesMap.get(stockItemId);

		Collection<CustomerOrderLine> customerOrderLinesToFill = getCustomerOrdersToFillMap(supplierDeliveryLine.getStockItem(), session);
		modelMap.addAttribute(customerOrderLinesToFill);
		session.setAttribute("stockItemIdToFill", supplierDeliveryLine.getStockItem().getId());
		return "createSupplierDelivery";
	}

	@RequestMapping(value="/deleteSupplierDeliveryOrderLine")
	public String deleteSupplierDeliveryOrderLine(Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, SupplierDeliveryLine> supplierDeliveryLinesMap = (Map<Long, SupplierDeliveryLine>) session.getAttribute("supplierDeliveryLinesMap");
		SupplierDelivery supplierDelivery = (SupplierDelivery) session.getAttribute("supplierDelivery");

		supplierDeliveryLinesMap.remove(id);

		fillModel(supplierDelivery, supplierDeliveryLinesMap, new SupplierDeliverySearchBean(), modelMap);
		
		return "createSupplierDelivery";
	}
	
	private void fillModel(SupplierDelivery supplierDelivery, Map<Long, SupplierDeliveryLine> supplierDeliveryLinesMap, SupplierDeliverySearchBean supplierDeliverySearchBean, ModelMap modelMap) {
		//Sort
		List<SupplierDeliveryLine> list = new ArrayList<SupplierDeliveryLine>(supplierDeliveryLinesMap.values());
		Collections.sort(list, new GardnersDeliveryComparator());
		modelMap.addAttribute(list);
		modelMap.addAttribute(supplierDeliverySearchBean);

		calculateTotalPrice(supplierDeliveryLinesMap.values(), modelMap);
	}



	/**
	 * Persist supplier delivery to datastore
	 * @param goodsIntoStockSearchBean
	 * @param bindingResult
	 * @param session
	 * @param modelMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public String create(@ModelAttribute SupplierDeliverySearchBean goodsIntoStockSearchBean, BindingResult bindingResult, HttpSession session, ModelMap modelMap) {
		Map<Long, SupplierDeliveryLine> map = (Map<Long, SupplierDeliveryLine>) session.getAttribute("supplierDeliveryLinesMap");
		SupplierDelivery supplierDelivery = (SupplierDelivery) session.getAttribute("supplierDelivery");
		Set<CustomerOrderLine> filledCustomerOrderLines = (Set<CustomerOrderLine>) session.getAttribute("filledCustomerOrderLines");

		if(map == null) {
			return sessionExpired(modelMap);
		}
		Collection<SupplierDeliveryLine> supplierDeliveryLines = map.values();

		if(supplierDeliveryLines.isEmpty()) {
			bindingResult.reject("supplierDelivery.supplierDeliveryLine", "Must add stock");
			bindingResult.rejectValue("supplierDelivery.supplierDeliveryLine", "invalid", "Must add stock");
			modelMap.addAttribute(map.values());
			return "createSupplierDelivery";
		}

		supplierDelivery.setSupplierDeliveryLine(supplierDeliveryLines);

		supplierDeliveryService.create(filledCustomerOrderLines, supplierDelivery);

		return "redirect:supplierDeliverySummary";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/supplierDeliverySummary")
	public String supplierDeliverySummary(HttpSession session, ModelMap modelMap) {
		Map<Long, SupplierDeliveryLine> supplierDeliveryLinesMap = (Map<Long, SupplierDeliveryLine>) session.getAttribute("supplierDeliveryLinesMap");
		Collection<CustomerOrderLine> filledCustomerOrderLines = (Collection<CustomerOrderLine>) session.getAttribute("filledCustomerOrderLines");

		Collection<SupplierDeliveryLine> supplierDeliveryLines = supplierDeliveryLinesMap.values();
		Collection<CustomerOrderLine> filledCustomerOrderLinesToCollect = new ArrayList<CustomerOrderLine>();
		Collection<CustomerOrderLine> filledCustomerOrderLinesForMailOrder = new ArrayList<CustomerOrderLine>();

		for(CustomerOrderLine customerOrderLine : filledCustomerOrderLines) {
			if(customerOrderLine.getStatus() == CustomerOrderLineStatus.INFORM_CUSTOMER_TO_COLLECT) {
				filledCustomerOrderLinesToCollect.add(customerOrderLine);
			}
			if(customerOrderLine.getStatus() == CustomerOrderLineStatus.READY_TO_POST) {
				filledCustomerOrderLinesForMailOrder.add(customerOrderLine);
			}
			//if(customerOrderLine.getDeliveryType() == DeliveryType.MAIL
			//		&& customerOrderLine.getCustomerOrderStatus() == CustomerOrderStatus.RECEIVED_PARTIAL_FILL) {
			//	filledCustomerOrderLinesForMailOrder.add(customerOrderLine);
			//}
			//if(customerOrderLine.getDeliveryType() == DeliveryType.COLLECTION
			//		&& customerOrderLine.getCustomerOrderStatus() == CustomerOrderStatus.RECEIVED_PARTIAL_FILL) {
			//	filledCustomerOrderLinesToCollect.add(customerOrderLine);
			//}
		}
		if(!filledCustomerOrderLinesToCollect.isEmpty()) {
			modelMap.addAttribute(filledCustomerOrderLinesToCollect);
		}
		if(!filledCustomerOrderLinesForMailOrder.isEmpty()) {
			modelMap.addAttribute("customerOrderMailOrderList", filledCustomerOrderLinesForMailOrder);
		}

		List<SupplierDeliveryLine> list = new ArrayList<SupplierDeliveryLine>(supplierDeliveryLinesMap.values());
		Collections.sort(list, new GardnersDeliveryComparator());
		modelMap.addAttribute(list);

		//modelMap.addAttribute(supplierDeliveryLines);
//		clearSession(session);
		session.removeAttribute("supplierDeliveryLinesMap");
		session.removeAttribute("supplierDelivery");
		session.removeAttribute("filledCustomerOrderLines");
		session.removeAttribute("customerOrderLineMap");
		return "supplierDeliverySummary";
	}



	@RequestMapping(value="/view")
	public String view(Long id, HttpSession session, ModelMap modelMap) {
		SupplierDelivery supplierDelivery = supplierDeliveryService.get(id);

		modelMap.addAttribute(supplierDelivery);

		return "viewSupplierDelivery";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/addStock", method=RequestMethod.POST)
	public String addStock(@ModelAttribute SupplierDeliverySearchBean supplierDeliverySearchBean, BindingResult bindingResult,	HttpSession session, ModelMap modelMap) {
		
		//Map of previously entered lines, by key is stock item id
		Map<Long, SupplierDeliveryLine> supplierDeliveryLinesMap = (Map<Long, SupplierDeliveryLine>) session.getAttribute("supplierDeliveryLinesMap");
		
		//The containing supplier delivery domain object
		SupplierDelivery supplierDelivery = (SupplierDelivery) session.getAttribute("supplierDelivery");
		
		//Get stock from isbn
		//Why does this call get supplier and category if it doesn't come from addStock??
		String isbn = supplierDeliverySearchBean.getStockItem().getIsbn();
		StockItem stockItem = stockItemService.get(isbn);
		
		//Must deal with null, not found in database
		if(stockItem == null) {
			//Check isbn is valid
			stockItemValidator.validateISBN(isbn, bindingResult, "stockItem");
			if(bindingResult.hasErrors()) {
				modelMap.addAttribute(supplierDeliveryLinesMap.values());
				modelMap.addAttribute(supplierDeliverySearchBean);
				addError("This isbn is invalid", modelMap);
				return "createSupplierDelivery";
			}
			//Do AZ lookup and direct to addStock.jsp
			try {
				stockItem = azLookupService.lookupWithJSoup(isbn);
				session.setAttribute("sessionStockItem", stockItem);
			} catch (Exception e) {
				logger.error("Cannot lookup from AZLookupService", e);
			}
			if(stockItem != null )	{
				stockItem.setDiscount(new BigDecimal(supplierDelivery.getSupplier().getDefaultDiscount()));
			} else {
				stockItem = new StockItem();
				stockItem.setIsbn(isbn);
				addInfo("Cannot find isbn at AZ", modelMap);
			}
			//This should link to stockItemController.add
			fillStockSearchModel(session, modelMap);
			modelMap.addAttribute(stockItem); 
			
			session.setAttribute("flow", "supplierDelivery");

			return "addStock";
		}

		boolean isNew = false;
		Long stockLevelChange = 0l;
		//Has this stock already been added or is it the first one?
		SupplierDeliveryLine supplierDeliveryLine = supplierDeliveryLinesMap.get(stockItem.getId());
		
		if(supplierDeliveryLine != null) { //Has already been added
			Long amount = supplierDeliveryLine.getAmount();
			stockLevelChange = supplierDeliveryLine.getAmount();
			
			supplierDeliveryLine.setAmount(amount + 1);
			supplierDeliveryService.calculatePrice(supplierDelivery, supplierDeliveryLine);
			
		} else {
			isNew = true;
			supplierDeliveryLine = new SupplierDeliveryLine();
			supplierDeliveryLine.setAmount(1l);
			supplierDeliveryLine.setDiscount(stockItem.getDiscount());
			supplierDeliveryLine.setCostPrice(stockItem.getCostPrice());
			supplierDeliveryLine.setSellPrice(stockItem.getSellPrice());
			supplierDeliveryLine.setPublisherPrice(stockItem.getPublisherPrice());
			supplierDeliveryLine.setStockItem(stockItem);
			supplierDeliveryService.calculatePrice(supplierDelivery, supplierDeliveryLine);
			supplierDeliveryLinesMap.put(stockItem.getId(), supplierDeliveryLine);
		}
		
		//Reset
		supplierDeliverySearchBean.getStockItem().setIsbn("");

		//Does this stock fill a customer order, check in map first then database?
		Collection<CustomerOrderLine> customerOrderLinesToFill = getCustomerOrdersToFillMap(stockItem, session);

		//Check it fills an order, but not if already been filled
		if(!customerOrderLinesToFill.isEmpty() && !supplierDeliveryLine.getHasCustomerOrderLines()) {
			if(isNew) supplierDeliveryLine.setAmount(0l);
			supplierDeliveryLine.setHasCustomerOrderLines(true);
			if(customerOrderLinesToFill.size() == 1) {
				CustomerOrderLine col = customerOrderLinesToFill.iterator().next();
				col.setNewAmount(1l);
			}
			modelMap.addAttribute("customerOrderLineList",  customerOrderLinesToFill);
			session.setAttribute("stockItemIdToFill", stockItem.getId());
			return "createSupplierDelivery";
		}
		
		//Persist sdl
//		supplierDeliveryLineService.save(supplierDeliveryLineFromMap);
		
		//Change stock record
		//update stockrecord = sdl.amount - stockLevelChange		

		//Sort
		List<SupplierDeliveryLine> list = new ArrayList<SupplierDeliveryLine>(supplierDeliveryLinesMap.values());
		Collections.sort(list, new GardnersDeliveryComparator());
		
		modelMap.addAttribute(list);
		modelMap.addAttribute(supplierDeliverySearchBean);
		modelMap.addAttribute("lastSupplierDeliveryLine", supplierDeliveryLine);

		calculateTotalPrice(supplierDeliveryLinesMap.values(), modelMap);

		return "createSupplierDelivery";
	}



	private void calculateTotalPrice(Collection<SupplierDeliveryLine> supplierDeliveryLines,
			ModelMap modelMap) {
		double totalPrice = 0;
		double retailPrice = 0;
		for(SupplierDeliveryLine line : supplierDeliveryLines) {
			totalPrice = totalPrice + line.getPrice().doubleValue();  //line.getPrice returns price * amount where price = discount * publisherPrice
			retailPrice += line.getPublisherPrice().doubleValue() * line.getAmount();
		}
		modelMap.addAttribute("totalPrice", totalPrice);
		modelMap.addAttribute("retailPrice", retailPrice);
	}

	@SuppressWarnings("unchecked")
	private Collection<CustomerOrderLine> getCustomerOrdersToFillMap(StockItem stockItem,
			HttpSession session) {
		Map<Long, Collection<CustomerOrderLine>> customerOrdersToFillMap
			= (Map<Long, Collection<CustomerOrderLine>>) session.getAttribute("customerOrdersToFillMap");
		Collection<CustomerOrderLine> customerOrderLinesToFill = customerOrdersToFillMap.get(stockItem.getId());
		if(customerOrderLinesToFill == null) {
			customerOrderLinesToFill = customerOrderLineService.findOpenOrdersForStockItem(stockItem);
			customerOrdersToFillMap.put(stockItem.getId(), customerOrderLinesToFill);
		}

		//Remove filled orders
//		Collection<CustomerOrderLine> filteredCollection = new ArrayList<CustomerOrderLine>();
//		if(!customerOrderLinesToFill.isEmpty()) {
//			for(CustomerOrderLine c : customerOrderLinesToFill) {
////				if(!c.canBeFilled()) continue;
//				if(c.getAmount() > c.getAmountFilled()) {
//					filteredCollection.add(c);
//				}
//			}
//		}
//		return filteredCollection;
		return customerOrderLinesToFill;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/fillStock", method=RequestMethod.POST) 
	public String fillStock(HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		//Get the customerOrderLine to fill, if not in map get from database
		Map<Long, Collection<CustomerOrderLine>> customerOrdersToFillMap = (Map<Long, Collection<CustomerOrderLine>>) session.getAttribute("customerOrdersToFillMap");
		
		Long stockItemId = (Long) session.getAttribute("stockItemIdToFill");
		
		Collection<CustomerOrderLine> customerOrderLinesToFill = customerOrdersToFillMap.get(stockItemId);
		
		Collection<CustomerOrderLine> filledCustomerOrderLines =  (Collection<CustomerOrderLine>) session.getAttribute("filledCustomerOrderLines");

//		//Get the supplierDeliveryLine that will do the filling
		Map<Long, SupplierDeliveryLine> supplierDeliveryLinesMap = (Map<Long, SupplierDeliveryLine>) session.getAttribute("supplierDeliveryLinesMap");
		SupplierDeliveryLine supplierDeliveryLine = supplierDeliveryLinesMap.get(stockItemId);

		//Go through request getting customerOrderLine ids and corresponding fill amounts
		Long amountFilled = 0l;
		Enumeration<String> enumeration = request.getParameterNames();
		while(enumeration.hasMoreElements()) {
			String pName = enumeration.nextElement();
			Long customerOrderLineId = Long.parseLong(pName);
			String[] amountArray = request.getParameterValues(customerOrderLineId.toString());
			Long amount = 0l;
			if(!amountArray[0].isEmpty()) { //If empty, treat as 0
				try {
					amount = Long.parseLong(amountArray[0]);
				} catch(NumberFormatException e) {
					addInfo(amountArray[0] + " is invalid!", modelMap);
					modelMap.addAttribute(customerOrderLinesToFill);
					return "createSupplierDelivery";
				}
			}

			if(amount == 0) continue; //No fill

			//Find corresponding customer order line and see if valid and fill
			for(CustomerOrderLine col : customerOrderLinesToFill) {
				if(col.getId().equals(customerOrderLineId)) {
					col.setNewAmount(amount);
					if(amount > col.getAmount()) {
						//Have filled too much
						addInfo("Order Id : " + col.getId() + " is over filled, please adjust", modelMap);
						modelMap.addAttribute(customerOrderLinesToFill);
						return "createSupplierDelivery";
					}
					//Find appropriate supplierDeliveryLine that does the filling and set amount
//					SupplierDeliveryLine supplierDeliveryLine = supplierDeliveryLinesMap.get(stockItemId);
					amountFilled += amount;
					//Valid so fill, this may split the col if amount < col.amount
					customerOrderLineService.fill(col, supplierDeliveryLine);

					//Put into session object for retrieval at create, will add only once
					filledCustomerOrderLines.add(col);
				}
			}
		}

		//Set how much is into stock on supplier delivery line
		if(amountFilled > supplierDeliveryLine.getAmount()) {
			supplierDeliveryLine.setAmount(amountFilled);
		}


		//Fill model
		SupplierDelivery supplierDelivery = (SupplierDelivery) session.getAttribute("supplierDelivery");
		modelMap.addAttribute(new SupplierDeliverySearchBean(supplierDelivery));
		modelMap.addAttribute(supplierDeliveryLinesMap.values());
		session.removeAttribute("stockItemIdToFill"); //
		return "createSupplierDelivery";

	}

	@RequestMapping(value="/selectSupplier", method=RequestMethod.POST)
	public String init(@ModelAttribute @Valid SupplierDelivery supplierDelivery, BindingResult bindingResult, HttpSession session, ModelMap modelMap) {

		if(bindingResult.hasErrors()) {
			modelMap.addAttribute(getSuppliers());
			return "displaySelectSupplier";
		}

		//Get supplier in case default discount hasn't been set
		Supplier supplier = supplierService.get(supplierDelivery.getSupplier().getId());
//		supplier.setDefaultDiscount(supplierDelivery.getSupplier().getDefaultDiscount());
		supplierDelivery.setSupplier(supplier);

		Map<Long, SupplierDeliveryLine> supplierDeliveryLinesMap = new HashMap<Long, SupplierDeliveryLine>();
		Map<Long, List<CustomerOrderLine>> customerOrdersToFillMap = new HashMap<Long, List<CustomerOrderLine>>();
		Set<CustomerOrderLine> filledCustomerOrderLines = new HashSet<CustomerOrderLine>();

		session.setAttribute("supplierDeliveryLinesMap", supplierDeliveryLinesMap);
		session.setAttribute("customerOrdersToFillMap", customerOrdersToFillMap);
		session.setAttribute("supplierDelivery", supplierDelivery);
		session.setAttribute("filledCustomerOrderLines", filledCustomerOrderLines);

		modelMap.addAttribute(new SupplierDeliverySearchBean(supplierDelivery));
		modelMap.addAttribute(supplierDeliveryLinesMap.values());

		return "createSupplierDelivery";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/selectSupplier", method=RequestMethod.GET)
	public String displaySelectSupplier(ModelMap modelMap, HttpSession session) {
		SupplierDelivery supplierDelivery = (SupplierDelivery) session.getAttribute("supplierDelivery");
		if(supplierDelivery != null) {
			Map<Long, SupplierDeliveryLine> map = (Map<Long, SupplierDeliveryLine>) session.getAttribute("supplierDeliveryLinesMap");
			modelMap.addAttribute(new SupplierDeliverySearchBean(supplierDelivery));
			modelMap.addAttribute(map.values());
			return "createSupplierDelivery";
		}

		//Otherwise new request, direct to select supplier
		modelMap.addAttribute(new SupplierDelivery());
		modelMap.addAttribute(getSuppliers());
		return "displaySelectSupplier";
	}

	@RequestMapping(value="/cancel", method=RequestMethod.GET)
	public String cancel(ModelMap modelMap, HttpSession session) {
		session.removeAttribute("supplierDelivery");
		session.removeAttribute("supplierDeliveryLinesMap");
		addSuccess("Have Cancelled Supplier Delivery", modelMap);
		return "welome";
	}

	@Override
	public Service getService() {
		return supplierDeliveryService;
	}
}
