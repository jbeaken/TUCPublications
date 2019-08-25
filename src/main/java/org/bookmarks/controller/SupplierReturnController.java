package org.bookmarks.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bookmarks.controller.validation.StockItemValidator;
import org.bookmarks.controller.validation.SupplierReturnLineValidator;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.SupplierReturn;
import org.bookmarks.domain.SupplierReturnLine;
import org.bookmarks.domain.SupplierReturnStatus;
import org.bookmarks.service.AZLookupService;
import org.bookmarks.service.CustomerOrderLineService;
import org.bookmarks.service.Service;
import org.bookmarks.service.StockItemService;
import org.bookmarks.service.SupplierReturnService;
import org.bookmarks.service.SupplierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="/supplierReturn")
public class SupplierReturnController extends OrderLineController {

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
	private SupplierReturnService supplierReturnService;

	@Autowired
	private CustomerOrderLineService customerOrderLineService;

	@Autowired
	private SupplierReturnLineValidator supplierReturnLineValidator;

	private Logger logger = LoggerFactory.getLogger(StockItemController.class);

	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		SupplierReturnSearchBean supplierReturnSearchBean = new SupplierReturnSearchBean();
		return search(supplierReturnSearchBean, request, session, modelMap);
	}

	@RequestMapping(value="/searchFromSession")
	public String searchFromSession(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		SupplierReturnSearchBean supplierReturnSearchBean = (SupplierReturnSearchBean) session.getAttribute("supplierReturnSearchBean");
		supplierReturnSearchBean.isFromSession(true);
		modelMap.addAttribute(supplierReturnSearchBean);
		return search(supplierReturnSearchBean, request, session, modelMap);
	}

	@RequestMapping(value="/search")
	public String search(SupplierReturnSearchBean supplierReturnSearchBean, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		setPaginationFromRequest(supplierReturnSearchBean, request);

		Collection<SupplierReturn> supplierReturns = supplierReturnService.search(supplierReturnSearchBean);

		modelMap.addAttribute(supplierReturns);
		modelMap.addAttribute(getSuppliers(session));
		modelMap.addAttribute(supplierReturnSearchBean);
		modelMap.addAttribute("searchResultCount", supplierReturnSearchBean.getSearchResultCount());

		//Save for return later
		session.setAttribute("supplierReturnSearchBean", supplierReturnSearchBean);
		return "searchSupplierReturns";
	}



	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String edit(Long id, ModelMap modelMap, HttpSession session) {

		SupplierReturn supplierReturn = supplierReturnService.get(id);

		Map<Long, SupplierReturnLine> supplierReturnLinesMap = new HashMap<Long, SupplierReturnLine>();

		for(SupplierReturnLine srl : supplierReturn.getSupplierReturnLine()) {
			supplierReturnLinesMap.put(srl.getStockItem().getId(), srl);
		}
		session.setAttribute("supplierReturn", supplierReturn);
		session.setAttribute("supplierReturnLinesMap", supplierReturnLinesMap);

		modelMap.addAttribute(new SupplierReturnSearchBean(supplierReturn));
		modelMap.addAttribute(supplierReturnLinesMap.values());

		return "createSupplierReturn";
	}

	/**
	* id refers to stockItem id,
	*/
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/editSupplierReturnOrderLine", method=RequestMethod.GET)
	public String editSupplierReturnOrderLine(Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, SupplierReturnLine> supplierReturnLineMap = (Map<Long, SupplierReturnLine>) session.getAttribute("supplierReturnLinesMap");
		SupplierReturn supplierReturn = (SupplierReturn) session.getAttribute("supplierReturn");

		SupplierReturnLine supplierReturnLine = supplierReturnLineMap.get(id);
		StockItem stockItem = stockItemService.get(id);
		supplierReturnLine.setStockItem(stockItem);

		modelMap.addAttribute(supplierReturn);
		modelMap.addAttribute("supplierReturnLine", supplierReturnLine);
		modelMap.addAttribute(new SupplierReturnSearchBean());
		modelMap.addAttribute(supplierReturnLineMap.values());
//		fillStockSearchModel(session, modelMap);

		return "createSupplierReturn";
	}

	@RequestMapping(value="/editSupplierReturnOrderLine", method=RequestMethod.POST)
	public String editSupplierReturnLine(SupplierReturnLine supplierReturnLine, BindingResult bindingResult, ModelMap modelMap, HttpSession session) {
		Map<Long, SupplierReturnLine> supplierReturnLineMap = (Map<Long, SupplierReturnLine>) session.getAttribute("supplierReturnLinesMap");
		SupplierReturn supplierReturn = (SupplierReturn) session.getAttribute("supplierReturn");

		//Get stock item
		StockItem stockItem = stockItemService.get(supplierReturnLine.getStockItem().getId());
		supplierReturnLine.setStockItem(stockItem);

		//Validate
		supplierReturnLineValidator.validate(supplierReturnLine, bindingResult);
		if(bindingResult.hasErrors()) {
			modelMap.addAttribute(supplierReturn);
//			modelMap.addAttribute(ReturnType.values());
			modelMap.addAttribute(supplierReturnLineMap.values());
			modelMap.addAttribute(supplierReturnLine);
		    return "createSupplierReturn";
		}

		supplierReturnLineMap.put(supplierReturnLine.getStockItem().getId(), supplierReturnLine);

		//Sort
		List<SupplierReturnLine> list = new ArrayList<SupplierReturnLine>(supplierReturnLineMap.values());
//		Collections.sort(list, new GardnersReturnComparator());
		modelMap.addAttribute(list);
		modelMap.addAttribute(supplierReturn);
		modelMap.addAttribute(new SupplierReturnSearchBean());
		modelMap.addAttribute("lastSupplierReturnLine", supplierReturnLine);

		//Why?
		modelMap.remove("supplierReturnLine");
		//calculateTotalPrice(supplierReturnLineMap.values(), modelMap);

		return "createSupplierReturn";
	}


	@RequestMapping(value="/deleteSupplierReturnOrderLine")
	public String deleteSupplierReturnOrderLine(Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, SupplierReturnLine> supplierReturnLinesMap = (Map<Long, SupplierReturnLine>) session.getAttribute("supplierReturnLinesMap");
		SupplierReturn supplierReturn = (SupplierReturn) session.getAttribute("supplierReturn");

		supplierReturnLinesMap.remove(id);

		fillModel(supplierReturn, supplierReturnLinesMap, new SupplierReturnSearchBean(), modelMap);

		return "createSupplierReturn";
	}

	private void fillModel(SupplierReturn supplierReturn, Map<Long, SupplierReturnLine> supplierReturnLinesMap, SupplierReturnSearchBean supplierReturnSearchBean, ModelMap modelMap) {
		//Sort
		List<SupplierReturnLine> list = new ArrayList<SupplierReturnLine>(supplierReturnLinesMap.values());
		modelMap.addAttribute(list);
		modelMap.addAttribute(supplierReturnSearchBean);

		calculateTotalPrice(supplierReturnLinesMap.values(), modelMap);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public String create(@ModelAttribute SupplierReturnSearchBean goodsIntoStockSearchBean, BindingResult bindingResult, HttpSession session, ModelMap modelMap) {

		Map<Long, SupplierReturnLine> map = (Map<Long, SupplierReturnLine>) session.getAttribute("supplierReturnLinesMap");
		SupplierReturn supplierReturn = (SupplierReturn) session.getAttribute("supplierReturn");

		if(map == null) {
			return sessionExpired(modelMap);
		}

		Collection<SupplierReturnLine> supplierReturnLines = map.values();

		if(supplierReturnLines.isEmpty()) {
			bindingResult.reject("supplierReturn.supplierReturnLine", "Must add stock");
			bindingResult.rejectValue("supplierReturn.supplierReturnLine", "invalid", "Must add stock");
			modelMap.addAttribute(map.values());
			return "createSupplierReturn";
		}

		supplierReturn.setSupplierReturnLine(supplierReturnLines);

		supplierReturnService.create(supplierReturn);

	  session.removeAttribute("supplierReturnLinesMap");
		session.removeAttribute("supplierReturn");

		return "redirect:displaySearch";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/supplierReturnSummary")
	public String supplierReturnSummary(HttpSession session, ModelMap modelMap) {
		Map<Long, SupplierReturnLine> supplierReturnLinesMap = (Map<Long, SupplierReturnLine>) session.getAttribute("supplierReturnLinesMap");

		Collection<SupplierReturnLine> supplierReturnLines = supplierReturnLinesMap.values();

		modelMap.addAttribute(supplierReturnLines);

		session.removeAttribute("supplierReturnLinesMap");
		session.removeAttribute("supplierReturn");

		return "supplierReturnSummary";
	}

	@RequestMapping(value="/view")
	public String view(Long id, HttpSession session, ModelMap modelMap) {
		SupplierReturn supplierReturn = supplierReturnService.get(id);

		modelMap.addAttribute(supplierReturn);

		return "viewSupplierReturn";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/addStock", method=RequestMethod.POST)
	public String addStock(@ModelAttribute SupplierReturnSearchBean supplierReturnSearchBean, BindingResult bindingResult,	HttpSession session, ModelMap modelMap) {

		//Map of previously entered lines, by key is stock item id
		Map<Long, SupplierReturnLine> supplierReturnLinesMap = (Map<Long, SupplierReturnLine>) session.getAttribute("supplierReturnLinesMap");

		if(supplierReturnLinesMap == null) {
			return sessionExpired(modelMap);
		}
		//The containing supplier return domain object
		SupplierReturn supplierReturn = (SupplierReturn) session.getAttribute("supplierReturn");

		//Get stock from isbn
		//Why does this call get supplier and category if it doesn't come from addStock??
		String isbn = supplierReturnSearchBean.getStockItem().getIsbn();
		StockItem stockItem = stockItemService.get(isbn);

		//Must deal with null, not found in database
		if(stockItem == null) {

			addError("Cannot find stock on system", modelMap);
			List<SupplierReturnLine> list = new ArrayList<SupplierReturnLine>(supplierReturnLinesMap.values());


			modelMap.addAttribute(list);
			modelMap.addAttribute(supplierReturnSearchBean);

			return "createSupplierReturn";
		}

		//Has this stock already been added or is it the first one?
		SupplierReturnLine supplierReturnLine = supplierReturnLinesMap.get(stockItem.getId());

		if(supplierReturnLine != null) { //Has already been added
			Long amount = supplierReturnLine.getAmount();
			supplierReturnLine.setAmount(amount + 1);
		} else {
			supplierReturnLine = new SupplierReturnLine(stockItem);
			supplierReturnLine.setAmount(1l);
			supplierReturnLine.setStockItem(stockItem);


			supplierReturnLinesMap.put(stockItem.getId(), supplierReturnLine);
		}

		//Reset
		supplierReturnSearchBean.getStockItem().setIsbn("");

		//Sort
		List<SupplierReturnLine> list = new ArrayList<SupplierReturnLine>(supplierReturnLinesMap.values());

		modelMap.addAttribute(list);
		modelMap.addAttribute(supplierReturnSearchBean);
		modelMap.addAttribute("lastSupplierReturnLine", supplierReturnLine);

		calculateTotalPrice(supplierReturnLinesMap.values(), modelMap);

		return "createSupplierReturn";
	}



	private void calculateTotalPrice(Collection<SupplierReturnLine> supplierReturnLines,
			ModelMap modelMap) {
		double totalPrice = 0;
		double retailPrice = 0;
		for(SupplierReturnLine line : supplierReturnLines) {
			Double price = line.getAmount() * line.getStockItem().getPublisherPrice().doubleValue();
			//line.setPrice(new BigDecimal(price));
			totalPrice = totalPrice + price;
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

//		//Get the supplierReturnLine that will do the filling
		Map<Long, SupplierReturnLine> supplierReturnLinesMap = (Map<Long, SupplierReturnLine>) session.getAttribute("supplierReturnLinesMap");
		SupplierReturnLine supplierReturnLine = supplierReturnLinesMap.get(stockItemId);

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
					return "createSupplierReturn";
				}
			}

			if(amount == 0) continue; //No fill

		}

		//Set how much is into stock on supplier return line
		if(amountFilled > supplierReturnLine.getAmount()) {
			supplierReturnLine.setAmount(amountFilled);
		}


		//Fill model
		SupplierReturn supplierReturn = (SupplierReturn) session.getAttribute("supplierReturn");
		modelMap.addAttribute(new SupplierReturnSearchBean(supplierReturn));
		modelMap.addAttribute(supplierReturnLinesMap.values());
		session.removeAttribute("stockItemIdToFill"); //
		return "createSupplierReturn";

	}

	@RequestMapping(value="/selectSupplier", method=RequestMethod.POST)
	public String selectSupplier(@ModelAttribute @Valid SupplierReturn supplierReturn, BindingResult bindingResult, HttpSession session, ModelMap modelMap) {

		if(bindingResult.hasErrors()) {
			modelMap.addAttribute(getSuppliers());
			return "displaySelectSupplier";
		}

		//Get supplier in case default discount hasn't been set
		Supplier supplier = supplierService.get(supplierReturn.getSupplier().getId());
//		supplier.setDefaultDiscount(supplierReturn.getSupplier().getDefaultDiscount());
		supplierReturn.setSupplier(supplier);

		Map<Long, SupplierReturnLine> supplierReturnLinesMap = new HashMap<Long, SupplierReturnLine>();

		session.setAttribute("supplierReturn", supplierReturn);
		session.setAttribute("supplierReturnLinesMap", supplierReturnLinesMap);

		modelMap.addAttribute(new SupplierReturnSearchBean(supplierReturn));
		modelMap.addAttribute(supplierReturnLinesMap.values());

		return "createSupplierReturn";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/selectSupplier", method=RequestMethod.GET)
	public String selectSupplier(ModelMap modelMap, HttpSession session) {

		SupplierReturn supplierReturn = (SupplierReturn) session.getAttribute("supplierReturn");

		if(supplierReturn != null) {
			Map<Long, SupplierReturnLine> map = (Map<Long, SupplierReturnLine>) session.getAttribute("supplierReturnLinesMap");
			modelMap.addAttribute(new SupplierReturnSearchBean(supplierReturn));
			modelMap.addAttribute(map.values());
			return "createSupplierReturn";
		}

		//Otherwise new request, direct to select supplier
		modelMap.addAttribute(new SupplierReturn());
		modelMap.addAttribute(getSuppliers());

		addInfo("Please select a supplier for this return", modelMap);

		return "selectSupplierForSupplierReturn";
	}

	@RequestMapping(value="/markAsReceivedCredit", method=RequestMethod.GET)
	public String markAsReceivedCredit(Long id, ModelMap modelMap, HttpServletRequest request, HttpSession session) {
		SupplierReturn supplierReturn = supplierReturnService.get(id);

		supplierReturn.setStatus(SupplierReturnStatus.COMPLETE);

		supplierReturnService.update(supplierReturn);

		return displaySearch(request, session, modelMap);

	}

		@RequestMapping(value="/sendToSupplier", method=RequestMethod.GET)
		public String sendToSupplier(Long id, ModelMap modelMap, HttpServletRequest request, HttpSession session) {
			SupplierReturn supplierReturn = supplierReturnService.get(id);

			for(SupplierReturnLine srl : supplierReturn.getSupplierReturnLine()) {
				stockItemService.updateQuantityInStock(srl.getStockItem(), srl.getAmount() * -1);
			}

			supplierReturn.setStatus(SupplierReturnStatus.AWAITING_CREDIT);
			supplierReturn.setDateSentToSupplier(new Date());

			supplierReturnService.update(supplierReturn);

			return displaySearch(request, session, modelMap);

		}

	@RequestMapping(value="/cancel", method=RequestMethod.GET)
	public String cancel(ModelMap modelMap,  HttpServletRequest request, HttpSession session) {
		session.removeAttribute("supplierReturn");
		session.removeAttribute("supplierReturnLinesMap");
		return  displaySearch(request, session, modelMap);
	}

	@Override
	public Service getService() {
		return supplierReturnService;
	}
}
