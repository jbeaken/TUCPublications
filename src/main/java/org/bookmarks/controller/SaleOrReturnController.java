package org.bookmarks.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bookmarks.controller.validation.SaleOrReturnOrderLineValidator;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.SaleOrReturn;
import org.bookmarks.domain.SaleOrReturnOrderLine;
import org.bookmarks.domain.SaleOrReturnStatus;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierOrder;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.service.CustomerService;
import org.bookmarks.service.SaleOrReturnService;
import org.bookmarks.service.Service;
import org.bookmarks.service.StockItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/saleOrReturn")
public class SaleOrReturnController extends OrderLineController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private StockItemService stockItemService;

	@Autowired
	private SaleOrReturnService saleOrReturnService;

	@Autowired
	private SaleOrReturnOrderLineValidator saleOrReturnOrderLineValidator;


	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/searchStockItems")
	public String searchStockItems(@ModelAttribute StockItemSearchBean stockItemSearchBean, HttpServletRequest request, ModelMap modelMap, HttpSession session) {
		Map<Long, SaleOrReturnOrderLine> orderLineMap = (Map<Long, SaleOrReturnOrderLine>) session.getAttribute("orderLineMap");
//		BigDecimal price = (BigDecimal) session.getAttribute("price");
		SaleOrReturn saleOrReturn = (SaleOrReturn) session.getAttribute("saleOrReturn");

		String errorMessage = stockItemSearchBean.checkValidityForISBN();
		if(errorMessage != null) {
			modelMap.addAttribute("message", errorMessage);
			fillModel(saleOrReturn, stockItemSearchBean, modelMap, orderLineMap);
			return editOrCreateView(saleOrReturn);
		}

		String isbn = stockItemSearchBean.getStockItem().getIsbn();
		StockItem stockItem = null;
		try {
			stockItem = stockItemService.getByISBNAsNumber(isbn);
		} catch(NumberFormatException e) {
			modelMap.addAttribute("message", "Invalid isbn!");
			fillModel(saleOrReturn, stockItemSearchBean, modelMap, orderLineMap);
			return editOrCreateView(saleOrReturn);
		}

		if(stockItem == null) {
			modelMap.addAttribute("message", "Cannot find isbn in database!");
			fillModel(saleOrReturn, stockItemSearchBean, modelMap, orderLineMap);
			return editOrCreateView(saleOrReturn);
		}

		addStockItem(stockItem, modelMap, orderLineMap, saleOrReturn);
		fillModel(saleOrReturn, stockItemSearchBean, modelMap, orderLineMap);
		modelMap.addAttribute(new StockItemSearchBean());
		return editOrCreateView(saleOrReturn);
	}

	private void fillModel(SaleOrReturn saleOrReturn,
			StockItemSearchBean stockItemSearchBean, ModelMap modelMap, Map<Long, SaleOrReturnOrderLine> orderLineMap) {
//		modelMap.addAttribute("price", saleOrReturnService.getPrice(saleOrReturn, orderLineMap));
		saleOrReturn.setSaleOrReturnOrderLines(orderLineMap.values());
		modelMap.addAttribute("totalPrice", saleOrReturn.getTotalPrice());
		modelMap.addAttribute("noOfLines", saleOrReturn.getTotalAmount());		
		modelMap.addAttribute("saleOrReturnOrderLineList", orderLineMap);
		modelMap.addAttribute(saleOrReturn);
		modelMap.addAttribute(stockItemSearchBean);
		modelMap.addAttribute(SaleOrReturnStatus.values());
	}

	private void addStockItem(StockItem stockItem, ModelMap modelMap, Map<Long, SaleOrReturnOrderLine> orderLineMap, SaleOrReturn saleOrReturn) {
		//Check if this stock item is already in map
		SaleOrReturnOrderLine saleOrReturnOrderLineInMap = orderLineMap.get(stockItem.getId());
		if(saleOrReturnOrderLineInMap != null) {
			//Already there, increment
			saleOrReturnOrderLineInMap.setAmount(saleOrReturnOrderLineInMap.getAmount() + 1l);
		} else {
			//New stock, put into sorol
			SaleOrReturnOrderLine sorol = new SaleOrReturnOrderLine(stockItem);
			sorol.setSaleOrReturn(saleOrReturn);
			orderLineMap.put(stockItem.getId(), sorol);
		}
	}

	/**
	 * Save saleOrReturn and then save to file system. Refresh customer saving effort
	 * in hidden form fields
	 * @param saleOrReturn
	 * @param bindingResult
	 * @param modelMap
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/save")
	public String save(@Valid SaleOrReturn saleOrReturn, BindingResult bindingResult, ModelMap modelMap, HttpSession session) {
		Map<Long, SaleOrReturnOrderLine> orderLineMap = (Map<Long, SaleOrReturnOrderLine>) session.getAttribute("orderLineMap");
		
		saleOrReturn.setSaleOrReturnStatus(SaleOrReturnStatus.WITH_CUSTOMER);

		if(orderLineMap.values().isEmpty()) {
			addError("Add stock items!", modelMap);
			fillModel(saleOrReturn, new StockItemSearchBean(), modelMap, orderLineMap);
			return "createSaleOrReturn";
		}
		
		if(bindingResult.hasErrors()) {
			fillModel(saleOrReturn, new StockItemSearchBean(), modelMap, orderLineMap);
			return "createSaleOrReturn";
		}

		saleOrReturn.setSaleOrReturnOrderLines(orderLineMap.values());
		
		saleOrReturnService.save(saleOrReturn);

		fillModel(saleOrReturn, new StockItemSearchBean(), modelMap, orderLineMap);

		session.removeAttribute("saleOrReturn");
		session.removeAttribute("orderLineMap");

		return "viewSaleOrReturn";
	}

	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String displayEdit(Long id, HttpSession session, ModelMap modelMap) {
		SaleOrReturn saleOrReturn = saleOrReturnService.get(id);
		
		Map<Long, SaleOrReturnOrderLine> orderLineMap = new HashMap<Long, SaleOrReturnOrderLine>();
		
		for(SaleOrReturnOrderLine sorol : saleOrReturn.getSaleOrReturnOrderLines()) {
			sorol.setOriginalAmount(sorol.getAmount());
			orderLineMap.put(sorol.getStockItem().getId(), sorol);
		}

		//Place into session
		session.setAttribute("saleOrReturn", saleOrReturn);
		session.setAttribute("orderLineMap", orderLineMap);

		//Place into model
		fillModel(saleOrReturn, new StockItemSearchBean(), modelMap, orderLineMap);
		return "editSaleOrReturn";
	}

	@RequestMapping(value="/view", method=RequestMethod.GET)
	public String view(Long id, String flow, HttpSession session, ModelMap modelMap) {
		SaleOrReturn saleOrReturn = saleOrReturnService.get(id);

		//Need to calculate price

		modelMap.addAttribute(saleOrReturn);
		modelMap.addAttribute("flow", flow);
		modelMap.addAttribute("totalPrice", saleOrReturn.getTotalPrice());
		modelMap.addAttribute("noOfLines", saleOrReturn.getTotalAmount());
		modelMap.addAttribute(saleOrReturn.getSaleOrReturnOrderLines());

		return "viewSaleOrReturn";
	}




	@RequestMapping(value="/print", method=RequestMethod.GET)
	public String print(Long saleOrReturnId, HttpSession session, ModelMap modelMap) {
		SaleOrReturn saleOrReturn = saleOrReturnService.get(saleOrReturnId);

		//Need to calculate price

		modelMap.addAttribute(saleOrReturn);
		modelMap.addAttribute("customer", saleOrReturn.getCustomer());
		modelMap.addAttribute("totalPrice", saleOrReturn.getTotalPrice());
		modelMap.addAttribute("noOfLines", saleOrReturn.getTotalAmount());
		modelMap.addAttribute(saleOrReturn.getSaleOrReturnOrderLines());

		return "printSaleOrReturn";
	}


	@RequestMapping(value="/markAsWithCustomer", method=RequestMethod.GET)
	public String markAsWithCustomer(Long saleOrReturnId, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		SaleOrReturn saleOrReturn = saleOrReturnService.get(saleOrReturnId);

		saleOrReturn.setSaleOrReturnStatus(SaleOrReturnStatus.WITH_CUSTOMER);
		//TO-DO, what if the status is RETURNED? Take out of stock?
		saleOrReturnService.update(saleOrReturn);

		return searchFromSession(session, request, modelMap);
	}


	@RequestMapping(value="/markAsReturned", method=RequestMethod.GET)
	public String markAsReturned(Long saleOrReturnId, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		SaleOrReturn saleOrReturn = saleOrReturnService.get(saleOrReturnId);
		
		if(saleOrReturn.getSaleOrReturnStatus() == SaleOrReturnStatus.WITH_CUSTOMER) {
			saleOrReturnService.markAsReturn(saleOrReturn); //Put back into stock
		} // else nothing to do, it's already returned.
		
		return searchFromSession(session, request, modelMap);
	}
	
	@RequestMapping(value="/return", method=RequestMethod.GET)
	public String returnSaleOrReturn(Long saleOrReturnId, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		SaleOrReturn saleOrReturn = saleOrReturnService.get(saleOrReturnId);
		
		Map<Long, SaleOrReturnOrderLine> orderLineMap = new HashMap<Long, SaleOrReturnOrderLine>();
		
		for(SaleOrReturnOrderLine saleOrReturnOrderLine : saleOrReturn.getSaleOrReturnOrderLines()) {
			saleOrReturnOrderLine.setOriginalAmountSold(saleOrReturnOrderLine.getAmountSold());
			saleOrReturnOrderLine.setOriginalAmount(saleOrReturnOrderLine.getAmount());
			saleOrReturnOrderLine.setOriginalAmountRemainingWithCustomer(saleOrReturnOrderLine.getAmountRemainingWithCustomer());
			orderLineMap.put(saleOrReturnOrderLine.getStockItem().getId(), saleOrReturnOrderLine);
		}

		//Place into session
		session.setAttribute("saleOrReturn", saleOrReturn);
		session.setAttribute("orderLineMap", orderLineMap);

		//Place into model
		fillModel(saleOrReturn, new StockItemSearchBean(), modelMap, orderLineMap);
		modelMap.addAttribute("flow", "return");
		return "sellSaleOrReturn";
	}
	
	@RequestMapping(value="/sell", method=RequestMethod.POST)
	public String sell(Long saleOrReturnId, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		SaleOrReturn saleOrReturn = (SaleOrReturn) session.getAttribute("saleOrReturn");
		Map<Long, SaleOrReturnOrderLine> orderLineMap = (Map<Long, SaleOrReturnOrderLine>) session.getAttribute("orderLineMap");

		request.getAttributeNames();
		Enumeration<String> enumeration = request.getParameterNames();
		List<Long> stockItemIds  = new ArrayList<Long>();
		while(enumeration.hasMoreElements()) {
			String pName = enumeration.nextElement();
			if(pName.startsWith("r")) { //Quantity remaining with customer
				continue;
			}
			stockItemIds.add(Long.parseLong(pName));
		}

		for(Long stockItemId : stockItemIds) {
			String[] amountSoldArray = request.getParameterValues(stockItemId.toString());
			String[] quantityRemainingWithCustomerArray = request.getParameterValues("r" + stockItemId.toString());
			Long amountSold = Long.parseLong(amountSoldArray[0]);
			Long quantityRemainingWithCustomer = 0l;
			try {
				quantityRemainingWithCustomer = Long.parseLong(quantityRemainingWithCustomerArray[0]);
			} catch(NumberFormatException e) {
				
			}

			//Get supplier order
			SaleOrReturnOrderLine saleOrReturnOrderLine = orderLineMap.get(stockItemId);

			saleOrReturnOrderLine.setAmountRemainingWithCustomer(quantityRemainingWithCustomer);
			saleOrReturnOrderLine.setAmountSold(amountSold);
		}
		
		saleOrReturn.setSaleOrReturnOrderLines(orderLineMap.values());

		saleOrReturnService.sell(saleOrReturn);

		return "viewSaleOrReturn";
	}



	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(@Valid SaleOrReturn saleOrReturn, BindingResult bindingResult, HttpSession session, ModelMap modelMap) {
		Map<Long, SaleOrReturnOrderLine> orderLineMap = (Map<Long, SaleOrReturnOrderLine>) session.getAttribute("orderLineMap");

		Collection<SaleOrReturnOrderLine> sorolMap = orderLineMap.values();
		
		if(sorolMap.isEmpty()) {
			bindingResult.rejectValue("returnDate", "invalid", "Must add stock items");
		}
		
		if(bindingResult.hasErrors()) {
			fillModel(saleOrReturn, new StockItemSearchBean(), modelMap, orderLineMap);
			return "editSaleOrReturn";
		}

		saleOrReturn.setSaleOrReturnOrderLines(sorolMap);

		saleOrReturnService.update(saleOrReturn);

		fillModel(saleOrReturn, new StockItemSearchBean(), modelMap, orderLineMap);

		session.removeAttribute("saleOrReturn");
		session.removeAttribute("orderLineMap");

		return "viewSaleOrReturn";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/addStockItem")
	public String addStockItem(Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, SaleOrReturnOrderLine> orderLineMap = (Map<Long, SaleOrReturnOrderLine>) session.getAttribute("orderLineMap");
		SaleOrReturn saleOrReturn = (SaleOrReturn) session.getAttribute("saleOrReturn");

		StockItem stockItem = stockItemService.get(id);

		addStockItem(stockItem, modelMap, orderLineMap, saleOrReturn);

		fillModel(saleOrReturn, new StockItemSearchBean(), modelMap, orderLineMap);

		return editOrCreateView(saleOrReturn);
	}
	
	private String editOrCreateView(SaleOrReturn sor) {
		if(sor.getId() != null) return "editSaleOrReturn"; else return "createSaleOrReturn";
	}
	
	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		SaleOrReturnSearchBean saleOrReturnSearchBean = new SaleOrReturnSearchBean();
		modelMap.addAttribute(saleOrReturnSearchBean);
		return search(saleOrReturnSearchBean, null, request, session, modelMap);
	}	

	@RequestMapping(value="/search")
	public String search(@ModelAttribute SaleOrReturnSearchBean saleOrReturnSearchBean, BindingResult bindingResult, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		if(!saleOrReturnSearchBean.isFromSession()) {
			setPaginationFromRequest(saleOrReturnSearchBean, request);
		}
		saleOrReturnSearchBean.isFromSession(false);

		Collection<SaleOrReturn> saleOrReturns = saleOrReturnService.search(saleOrReturnSearchBean);

		session.setAttribute("searchBean", saleOrReturnSearchBean);

		modelMap.addAttribute(saleOrReturns);
		modelMap.addAttribute(SaleOrReturnStatus.values());
		modelMap.addAttribute("searchResultCount", saleOrReturnSearchBean.getSearchResultCount());

		return "searchSaleOrReturns";
	}

	@RequestMapping(value="/searchFromSession")
	public String searchFromSession(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		SaleOrReturnSearchBean saleOrReturnSearchBean = (SaleOrReturnSearchBean) session.getAttribute("searchBean");
		if(saleOrReturnSearchBean == null) saleOrReturnSearchBean = new SaleOrReturnSearchBean();
		else saleOrReturnSearchBean.isFromSession(true);
		modelMap.addAttribute(saleOrReturnSearchBean);
		return search(saleOrReturnSearchBean, null,  request, session, modelMap);
	}


	@RequestMapping(value="/showSearchStock", method=RequestMethod.GET)
	public String showSearchStock(ModelMap modelMap, HttpSession session) {
		Map<Long, SaleOrReturnOrderLine> orderLineMap = (Map<Long, SaleOrReturnOrderLine>) session.getAttribute("orderLineMap");
		SaleOrReturn saleOrReturn = (SaleOrReturn) session.getAttribute("saleOrReturn");

		modelMap.addAttribute(saleOrReturn);
		modelMap.addAttribute("showSearchStock", true);
		modelMap.addAttribute(SaleOrReturnStatus.values());

		return "createSaleOrReturn";
	}

	/**
	 * id refers to stockItem id, or -1 if 2nd hand
	 * @param id
	 * @param modelMap
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/editSaleOrReturnOrderLine", method=RequestMethod.GET)
	public String editSaleOrReturnOrderLine(Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, SaleOrReturnOrderLine> orderLineMap = (Map<Long, SaleOrReturnOrderLine>) session.getAttribute("orderLineMap");
		SaleOrReturn saleOrReturn = (SaleOrReturn) session.getAttribute("saleOrReturn");

		SaleOrReturnOrderLine saleOrReturnOrderLine = orderLineMap.get(id);
		modelMap.addAttribute("saleOrReturnOrderLine", saleOrReturnOrderLine);
		modelMap.addAttribute("totalPrice", saleOrReturn.getTotalPrice());
		modelMap.addAttribute("noOfLines", saleOrReturn.getTotalAmount());	
		modelMap.addAttribute(saleOrReturn);
		modelMap.addAttribute(SaleOrReturnStatus.values());

		return editOrCreateView(saleOrReturn);
	}

	/**
	 * @param id
	 * @param modelMap
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/editSaleOrReturnOrderLine", method=RequestMethod.POST)
	public String editSaleOrReturnOrderLine(SaleOrReturnOrderLine saleOrReturnOrderLine, BindingResult bindingResult, ModelMap modelMap, HttpSession session) {
		Map<Long, SaleOrReturnOrderLine> orderLineMap = (Map<Long, SaleOrReturnOrderLine>) session.getAttribute("orderLineMap");
		SaleOrReturn saleOrReturn = (SaleOrReturn) session.getAttribute("saleOrReturn");

		//Validate
		saleOrReturnOrderLineValidator.validate(saleOrReturnOrderLine, bindingResult);
		if(bindingResult.hasErrors()) {
			modelMap.addAttribute("totalPrice", saleOrReturn.getTotalPrice());
			modelMap.addAttribute("noOfLines", saleOrReturn.getTotalAmount());	
			modelMap.addAttribute(saleOrReturn);
			modelMap.addAttribute(SaleOrReturnStatus.values());
			modelMap.addAttribute("saleOrReturnOrderLine", saleOrReturnOrderLine);
		    return editOrCreateView(saleOrReturn);
		}
		orderLineMap.put(saleOrReturnOrderLine.getStockItem().getId(), saleOrReturnOrderLine);

		modelMap.addAttribute("totalPrice", saleOrReturn.getTotalPrice());
		modelMap.addAttribute("noOfLines", saleOrReturn.getTotalAmount());	
		modelMap.addAttribute(orderLineMap.values());
		modelMap.addAttribute(saleOrReturn);
		modelMap.addAttribute(new StockItemSearchBean());
		modelMap.addAttribute(SaleOrReturnStatus.values());
		//Why?
		modelMap.remove("saleOrReturnOrderLine");

		return editOrCreateView(saleOrReturn);
	}

	@RequestMapping(value="/deleteSaleOrReturnOrderLine")
	public String deleteSaleOrReturnOrderLine(Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, SaleOrReturnOrderLine> orderLineMap = (Map<Long, SaleOrReturnOrderLine>) session.getAttribute("orderLineMap");
		SaleOrReturn saleOrReturn = (SaleOrReturn) session.getAttribute("saleOrReturn");

		orderLineMap.remove(id);

		modelMap.addAttribute("totalPrice", saleOrReturn.getTotalPrice());
		modelMap.addAttribute("noOfLines", saleOrReturn.getTotalAmount());	
		modelMap.addAttribute(orderLineMap.values());
		modelMap.addAttribute(saleOrReturn);
		modelMap.addAttribute(new StockItemSearchBean());
		modelMap.addAttribute(SaleOrReturnStatus.values());

		return editOrCreateView(saleOrReturn);
	}


	@RequestMapping(value="/delete")
	public String delete(Long id, ModelMap modelMap, HttpSession session, HttpServletRequest request) {
		SaleOrReturn sor = saleOrReturnService.get(id);
		saleOrReturnService.delete(sor);

		return searchFromSession(session, request, modelMap);
	}

	/**
	 * Get the customer for which the saleOrReturn is for
	 * create session objects and then redirect
	 * to /saleOrReturn/createSaleOrReturn.jsp
	 * @param id
	 * @param modelMap
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/init")
	public String init(Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, SaleOrReturnOrderLine> orderLineMap = new HashMap<Long, SaleOrReturnOrderLine>();

		//Create saleOrReturn and attach customer
		Customer customer = customerService.get(id);
		SaleOrReturn saleOrReturn = new SaleOrReturn(customer);

		//Place into session
		session.setAttribute("saleOrReturn", saleOrReturn);
		session.setAttribute("orderLineMap", orderLineMap);

		//Place into model
		fillModel(saleOrReturn, new StockItemSearchBean(), modelMap, orderLineMap);
		return "createSaleOrReturn";
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
		Map<Long, SaleOrReturnOrderLine> orderLineMap =  (Map<Long, SaleOrReturnOrderLine>) session.getAttribute("orderLineMap");
		SaleOrReturn saleOrReturn = (SaleOrReturn) session.getAttribute("saleOrReturn");

		//Place into session
		session.setAttribute("saleOrReturn", saleOrReturn);
		session.setAttribute("orderLineMap", orderLineMap);

		//Place into model
		fillModel(saleOrReturn, new StockItemSearchBean(), modelMap, orderLineMap);
		return editOrCreateView(saleOrReturn);
	}

	@Override
	public Service getService() {
		return saleOrReturnService;
	}

}
