package org.bookmarks.controller;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.bookmarks.controller.validation.CustomerOrderLineValidator;
import org.bookmarks.domain.BookmarksRole;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.Invoice;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.Source;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierOrder;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.exceptions.BookmarksException;
import org.bookmarks.service.CustomerOrderLineService;
import org.bookmarks.service.CustomerService;
import org.bookmarks.service.InvoiceService;
import org.bookmarks.service.StockItemService;
import org.bookmarks.service.SupplierOrderLineService;
import org.bookmarks.service.SupplierOrderService;
import org.bookmarks.util.PDFLabels;
import org.bookmarks.website.domain.Address;
import org.bookmarks.website.domain.CreditCard;
import org.bookmarks.website.domain.DeliveryType;
import org.bookmarks.website.domain.PaymentType;
import org.jasypt.util.text.StrongTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Controller
@RequestMapping(value="/customerOrderLine")
public class CustomerOrderLineController extends OrderLineController {

	@Autowired
	private CustomerOrderLineService customerOrderLineService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private SupplierOrderService supplierOrderService;

	@Autowired
	private SupplierOrderLineService supplierOrderLineService;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private StockItemService stockItemService;

	@Autowired
	private InvoiceController invoiceController;

	@Autowired
	private CustomerOrderLineValidator customerOrderLineValidator;

	private Logger logger = LoggerFactory.getLogger(CustomerOrderLineController.class);

	/**
	 * @param id
	 * @param session
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/informCustomer")
	public String informCustomer(Long id, CustomerOrderLineStatus customerOrderStatus, HttpSession session, ModelMap modelMap) {

		customerOrderLineService.updateStatus(id, customerOrderStatus);

		customerOrderLineService.makeNote(id, customerOrderStatus);

		modelMap.addAttribute("closeWindowNoRefresh", "not null");

		return "closeWindow";
	}

	@RequestMapping(value="/processSupplierOrders", method=RequestMethod.POST)
	public String processSupplierOrders(ArrayList<SupplierOrderLine> supplierOrderLines, BindingResult bindingResult, HttpServletRequest request, HttpSession session, ModelMap modelMap) {

		//Map<Long, SupplierOrder> supplierOrderMap = new HashMap<Long, SupplierOrder>();
		Map<Long, SupplierOrder> supplierOrderMap = supplierOrderService.getPendingAsMap();
		Collection<CustomerOrderLine> customerOrderLinesToSave = new ArrayList<CustomerOrderLine>();
		//Map<Long, SupplierOrderLine> supplierOrderLineMap = new HashMap<Long, SupplierOrderLine>();

		//Need to get col's from session to get access to stockItem type, or send up as parameter??

//		request.getAttributeNames();
		Enumeration<String> enumeration = request.getParameterNames();
		List<Long> customerOrderLineIds  = new ArrayList<Long>();
//		Map<Long, Long> orderedCustomerOrderLineMap  = new HashMap<Long, Long>();
		while(enumeration.hasMoreElements()) {
			String pName = enumeration.nextElement();
			if(pName.startsWith("s") || pName.startsWith("st") || pName.startsWith("a")) { //Supplier id or stock item id, just want col ids
				continue;
			}
			customerOrderLineIds.add(Long.parseLong(pName));
		}

		String errorMessage = null;

		for(Long customerOrderLineId : customerOrderLineIds) {
			String[] supplierIdArray = request.getParameterValues("s" + customerOrderLineId);
			String[] stockItemIdArray = request.getParameterValues("st" + customerOrderLineId);
			String[] amountArray = request.getParameterValues("a" + customerOrderLineId);
			String[] amountFilledArray = request.getParameterValues(customerOrderLineId.toString());

			Long amountFilled = Long.parseLong(amountFilledArray[0]);
			Long supplierId = Long.parseLong(supplierIdArray[0]);
			Long stockItemId = Long.parseLong(stockItemIdArray[0]);
			Long amount = Long.parseLong(amountArray[0]);

			if(amountFilled == 0) continue; //No effect
			if(amountFilled > amount){ //Error
				errorMessage = "Order No " + customerOrderLineId + " : Filled amount exceeds order amount!";
				break;
			}

			System.out.println("col id = " + customerOrderLineId
				+ " st id = " + stockItemId
				+ " amountFilled = " + amountFilled
				+ " amount = " + amount
				+ " suplierID= "  + supplierId);


			//Get supplier order
			SupplierOrder supplierOrder = supplierOrderMap.get(supplierId);
			if(supplierOrder == null) {
				//Not in map so get pending
				supplierOrder = supplierOrderService.getPendingFromRepository(supplierId); //This is verbose, fix!!

				//Put into map
				supplierOrderMap.put(supplierId, supplierOrder);
			}

//			SupplierOrderLine supplierOrderLine = null;
			CustomerOrderLine customerOrderLine = customerOrderLineService.get(customerOrderLineId);
			//Get supplier order line, check it doesn't already exist in pending from previous order
//			for(SupplierOrderLine sol : supplierOrder.getSupplierOrderLines()) {
//				if(sol.getStockItem().getId().equals(stockItemId)) {
//					supplierOrderLine = sol;
//				}
//			}
//			if(supplierOrderLine == null) {
				SupplierOrderLine supplierOrderLine =new SupplierOrderLine();
				supplierOrderLine.setStockItem(new StockItem(stockItemId));
				supplierOrderLine.setAmount(supplierOrderLine.getAmount() + amountFilled);
				supplierOrderLine.setCustomerOrderLine(customerOrderLine);
				supplierOrder.addSupplierOrderLine(supplierOrderLine);
//			}


			customerOrderLine.setSupplierOrderLine(supplierOrderLine);
			customerOrderLine.setAmount(amountFilled);
			customerOrderLine.setStatus(CustomerOrderLineStatus.PENDING_ON_ORDER);
			//customerOrderLine.setOnOrderDate(new Date()); //this isn't right, this is the date it is pending on //order, not sent to supplier.
//			customerOrderLinesToSaveOrUpdate.add(customerOrderLine);

			//check for a partial fill
			if(amountFilled < amount){
				//Split! Clone original customerOrderLine
				CustomerOrderLine split = customerOrderLine.clone();
				split.setStatus(CustomerOrderLineStatus.OUT_OF_STOCK);
				split.setAmount(amount - amountFilled);
				customerOrderLinesToSave.add(split);
			}

			//Save for update later
//			orderedCustomerOrderLineMap.put(customerOrderLineId, amountFilled);
		}
		if(errorMessage != null) {
			addInfo(errorMessage, modelMap);
			return searchFromSession(session, request, modelMap);
		}

		//customerOrderLineService.saveOrUpdate(customerOrderLinesToSaveOrUpdate);
		customerOrderLineService.save(customerOrderLinesToSave);
//		customerOrderLineService.updateToOrdered(orderedCustomerOrderLineMap);
		supplierOrderService.processReorderReview(supplierOrderMap.values());
		supplierOrderService.save(supplierOrderMap.values());

		addInfo("Successfully created pending orders", modelMap);
		return searchFromSession(session, request, modelMap);
	}

	@RequestMapping(value="/copyISBNs", method=RequestMethod.GET)
	public String copyISBNs(HttpSession session, ModelMap modelMap) {
		CustomerOrderLineSearchBean customerOrderLineSearchBean = (CustomerOrderLineSearchBean) session.getAttribute("customerOrderSearchBean");
		Collection<CustomerOrderLine> customerOrderLines = customerOrderLineService.search(customerOrderLineSearchBean);

		int rows = 0;
		StringBuffer b = new StringBuffer();
		for(CustomerOrderLine col : customerOrderLines) {
			StockItem stockItem = col.getStockItem();
			b.append(stockItem.getIsbn() + "\n");
			rows++;
		}
		modelMap.addAttribute("someText", b.toString());
		modelMap.addAttribute("rows", rows);

		return "showText";
	}

	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String edit(Long id, String flow, ModelMap modelMap) {
		CustomerOrderLine customerOrderLine = customerOrderLineService.get(id);

		modelMap.addAttribute(PaymentType.values());
		modelMap.addAttribute(DeliveryType.values());
		modelMap.addAttribute("flow", flow);

		modelMap.addAttribute(customerOrderLine);
		modelMap.addAttribute(customerOrderLine.getCustomer());
		modelMap.addAttribute(customerOrderLine.getStockItem());
		modelMap.addAttribute(CustomerOrderLineStatus.values());

		logger.info("About to edit customer order line " + customerOrderLine.getId() + " with status " + customerOrderLine.getStatus());
		logger.info(ReflectionToStringBuilder.toString( customerOrderLine ));

		return "editCustomerOrderLine";
	}

	@RequestMapping(value="/gotoGardners", method=RequestMethod.GET)
	public String gotoGardners(String isbn, ModelMap modelMap) {
		modelMap.addAttribute("isbn", isbn);
		return "gotoGardners";
	}


	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(@Valid CustomerOrderLine customerOrderLine, BindingResult bindingResult, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		//Validate
		customerOrderLineValidator.validate(customerOrderLine, bindingResult);

		if(bindingResult.hasErrors()) {
			modelMap.addAttribute(PaymentType.values());
			modelMap.addAttribute(Source.values());
			modelMap.addAttribute(DeliveryType.values());
			modelMap.addAttribute(customerOrderLine);
			modelMap.addAttribute(customerOrderLine.getCustomer());
			modelMap.addAttribute(customerOrderLine.getStockItem());
			modelMap.addAttribute(CustomerOrderLineStatus.values());
			addError(bindingResult.getAllErrors(), modelMap);
			return "editCustomerOrderLine";
		}

		if(customerOrderLine.getSupplierOrderLine().getId() == null) {
			customerOrderLine.setSupplierOrderLine(null);
		} else {
			SupplierOrderLine sol = supplierOrderLineService.get(customerOrderLine.getSupplierOrderLine().getId());
			customerOrderLine.setSupplierOrderLine(sol);
		}

		customerOrderLineService.update(customerOrderLine);
		modelMap.addAttribute(customerOrderLine);

		logger.info("Edited customer order line " + customerOrderLine.getId() + " new status " + customerOrderLine.getStatus());
		logger.info(ReflectionToStringBuilder.toString( customerOrderLine ));

		return "redirect:searchFromSession";
		//return searchFromSession(session, request, modelMap);
	}



	@RequestMapping(value="/delete")
	public String delete(Long id, HttpSession session, ModelMap modelMap) {

		modelMap.addAttribute(new StockItemSearchBean());
		modelMap.addAttribute(getCategories(session));
		modelMap.addAttribute(getPublishers(session));

		customerOrderLineService.delete(new CustomerOrderLine(id));

		return "selectStockItemsForCustomerOrder";
	}

	@RequestMapping(value="/view")
	public String view(Long id, String flow, ModelMap modelMap) {

		CustomerOrderLine customerOrderLine = customerOrderLineService.get(id);

		modelMap.addAttribute(PaymentType.values());
		modelMap.addAttribute(DeliveryType.values());
//		modelMap.addAttribute("flow", flow);

		modelMap.addAttribute(customerOrderLine);
		modelMap.addAttribute(customerOrderLine.getCustomer());
		modelMap.addAttribute(customerOrderLine.getStockItem());
		modelMap.addAttribute(CustomerOrderLineStatus.values());

		return "viewCustomerOrderLine";
	}

	@RequestMapping(value="/search")
	public String search(@ModelAttribute CustomerOrderLineSearchBean customerOrderLineSearchBean, BindingResult bindingResult, HttpSession session, HttpServletRequest request, ModelMap modelMap) {

		if(bindingResult != null && bindingResult.hasErrors()) {
			modelMap.addAttribute(PaymentType.values());
			modelMap.addAttribute(DeliveryType.values());
			modelMap.addAttribute(BookmarksRole.values());
			modelMap.addAttribute("customerOrderStatusOptions", CustomerOrderLineStatus.values());
		    return "searchCustomerOrderLines";
		}

		setPaginationFromRequest(customerOrderLineSearchBean, request);
		if(customerOrderLineSearchBean.getCustomerOrderLine().getStatus() == CustomerOrderLineStatus.OUT_OF_STOCK) {
			//Because this is a form, pagination will lose input so display all results (up to a hundred, more than a hundred is
			//unlikely and can be done in sets anyhow
			customerOrderLineSearchBean.setPageSize(100);
		}

		Collection<CustomerOrderLine> customerOrderLines = customerOrderLineService.search(customerOrderLineSearchBean);

		//Don't like, fix for shitty export
		setPageSize(customerOrderLineSearchBean, modelMap, customerOrderLines.size());

		session.setAttribute("customerOrderSearchBean", customerOrderLineSearchBean);
		session.setAttribute("request", request);

		addSuccess((String) session.getAttribute("success"), modelMap);
		session.removeAttribute("success");

		modelMap.addAttribute(customerOrderLines);
		modelMap.addAttribute(PaymentType.values());
		modelMap.addAttribute(DeliveryType.values());
		modelMap.addAttribute(BookmarksRole.values());
		modelMap.addAttribute(Source.values());
		modelMap.addAttribute(customerOrderLineSearchBean);
		modelMap.addAttribute("customerOrderStatusOptions", CustomerOrderLineStatus.values());
		modelMap.addAttribute("searchResultCount", customerOrderLineSearchBean.getSearchResultCount());

		if(customerOrderLineSearchBean.getCustomerOrderLine().getStatus() == CustomerOrderLineStatus.OUT_OF_STOCK) {
			modelMap.addAttribute(getSuppliers(session));
			//Go through customer order lines setting preferred supplier if necessary
			for(CustomerOrderLine c : customerOrderLines) {
				if(c.getStockItem().getPreferredSupplier() == null) {
					c.getStockItem().setPreferredSupplier(c.getStockItem().getPublisher().getSupplier());
					c.setAmountFilled(0l);
				}

			}
		}

		return "searchCustomerOrderLines";
	}


	@RequestMapping(value="/printLabels")
	public @ResponseBody void printLabels(@ModelAttribute CustomerOrderLineSearchBean customerOrderLineSearchBean, BindingResult bindingResult, HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws DocumentException {

		setPaginationFromRequest(customerOrderLineSearchBean, request);

		customerOrderLineSearchBean.setExport(true); //Override

		Collection<CustomerOrderLine> customerOrderLines = customerOrderLineService.search(customerOrderLineSearchBean);

		Document doc = new Document();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //Here We are writing the pdf contents to the output stream via pdfwriter
		//doc will contain the all the format and pages generated by the iText

		PdfWriter writer = PdfWriter.getInstance(doc, baos);
		//doc.setPageSize(PageSize.A4);
		doc.setMargins(0, 0, 0, 0);
//		doc.setMarginMirroring(true);

		PdfPTable table = new PdfPTable(2);
		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		table.setWidthPercentage(100);
		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		doc.open();


		String nl = System.getProperty("line.separator");

		Set<Long> ids = new HashSet<Long>();

		List<Customer> customersForLabels = new ArrayList<Customer>();

		for(CustomerOrderLine col : customerOrderLines) {
			Customer c = customerService.get(col.getCustomer().getId());

			 //Duplication?
			 if(ids.contains(c.getId())) {
				 continue;
			 }

			 //Alread been printed
			 if(col.getHavePrintedLabel() != null && col.getHavePrintedLabel() == true) {
				 continue;
			 }
			 ids.add(c.getId());

			 customersForLabels.add(c);
			 StringBuilder labelText = new StringBuilder();

			 Address a = c.getAddress();
			 if(a != null) {
		    	 labelText.append(c.getFullName() + nl);
		    	 if(a.getAddress1() != null && !a.getAddress1().isEmpty()) labelText.append(a.getAddress1() + nl);
		    	 if(a.getAddress2() != null && !a.getAddress2().isEmpty()) labelText.append(a.getAddress2() + nl);
		    	 if(a.getAddress3() != null && !a.getAddress3().isEmpty()) labelText.append(a.getAddress3() + nl);
		    	 if(a.getPostcode() != null && !a.getPostcode().isEmpty()) labelText.append(a.getPostcode() + nl);
//		    	 logger.info(labelText.toString());
		    	// pl.add(labelText.toString());//, "45140-8778");  //regular label with postnet barcode

		    	 Font DOC_FONT = new Font (Font.FontFamily.COURIER, 12, Font.NORMAL);

		    	 PdfPCell cell = new PdfPCell (new Phrase(labelText.toString(), DOC_FONT));
		    	 cell.setBorder(Rectangle.NO_BORDER);
		    	 cell.setFixedHeight(125f);

//		         cell.setNoWrap(true);

		         cell.setPaddingLeft(18.0f);
		         cell.setPaddingTop(5.0f);

//		         cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//		         cell.setHorizontalAlignment(Element.ALIGN_LEFT);

		    	 table.addCell(cell);
			 }
			 customerOrderLineService.updateHasPrintedLabel(true, col.getId());
		}

//		pl.finish();

		doc.add(table);

		try {
			doc.close();


		 response.setContentType("application/pdf");
		 response.setHeader("Content-Disposition","attachment; filename=\"ringAround.pdf\"");
		 response.setContentLength(baos.size());

		 OutputStream os = response.getOutputStream();
		 baos.writeTo(os);
		 os.flush();
		 os.close();

		} catch(IOException e) {
			//Probably no labels, no page exeption
		}

	}

	@RequestMapping(value="/reset", method=RequestMethod.GET)
	public String reset(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		return search(new CustomerOrderLineSearchBean(), null, session, request, modelMap);
	}

	@RequestMapping(value="/searchFromSession")
	public String searchFromSession(HttpSession session, HttpServletRequest request, ModelMap modelMap) {

		CustomerOrderLineSearchBean customerOrderLineSearchBean = (CustomerOrderLineSearchBean) session.getAttribute("customerOrderSearchBean");

		if(customerOrderLineSearchBean == null) {
			customerOrderLineSearchBean = new CustomerOrderLineSearchBean();
		}

		HttpServletRequest sessionRequest = (HttpServletRequest) session.getAttribute("request");

		if(sessionRequest == null) { sessionRequest = request;}

		customerOrderLineSearchBean.isFromSession(true);

		modelMap.addAttribute(customerOrderLineSearchBean);

		return search(customerOrderLineSearchBean, null, session, sessionRequest, modelMap);
	}

/**
 * Used to add non-account col to invoice
 **/
	@RequestMapping(value="/addToInvoice", method=RequestMethod.GET)
	public String addToInvoice(Long customerOrderLineId, String flow, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
	  	CustomerOrderLine customerOrderLine = customerOrderLineService.get(customerOrderLineId);

			Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");
			Map<Long, CustomerOrderLine> customerOrderLineMapForInvoice = (Map<Long, CustomerOrderLine>) session.getAttribute("customerOrderLineMapForInvoice");
			Invoice invoice = (Invoice) session.getAttribute("invoice");

			//Check this customerOrderLine hasn't already been added
			if(customerOrderLineMapForInvoice != null && customerOrderLineMapForInvoice.get(customerOrderLine.getId()) != null) {
				addInfo("Customer Order ID: " + customerOrderLine.getId() + " has already been added to an invoice!", modelMap);
		//			if(flow.equals("searchCustomerOrderLines")){
		//				return searchFromSession(session, request, modelMap);
		//			} else return edit(customerOrderLine.getId(), flow, modelMap);
				return searchFromSession(session, request, modelMap);
			}
			//New invoice
			if(invoice == null) {
				invoice = invoiceService.getNewInvoice(customerOrderLine.getCustomer().getId());
				invoice.setDeliveryType(customerOrderLine.getDeliveryType());
				saleMap = new HashMap<Long, Sale>();
				customerOrderLineMapForInvoice = new HashMap<Long, CustomerOrderLine>();
				session.setAttribute("orderLineMap", saleMap);
				session.setAttribute("customerOrderLineMapForInvoice", customerOrderLineMapForInvoice);
				session.setAttribute("invoice", invoice);
			} else {
				//Check the customer's match
				if(!invoice.getCustomer().getId().equals(customerOrderLine.getCustomer().getId())) {
					addInfo("An invoice for " + invoice.getCustomer().getFullName() + " is already in progress, deal with this first!", modelMap);
					return searchFromSession(session, request, modelMap);
				}
			}
			//System.out.println("map : " + customerOrderLineMapForInvoice);
			//System.out.println("customerOrderLine : " + customerOrderLine);
			//Add customerorderline to invoice
			customerOrderLineMapForInvoice.put(customerOrderLine.getId(), customerOrderLine);

			invoiceService.addStockItem(customerOrderLine, saleMap, invoice);

			modelMap.addAttribute("price", invoice.getTotalPrice());
			modelMap.addAttribute(saleMap.values());
			modelMap.addAttribute(invoice);
			modelMap.addAttribute(new StockItemSearchBean());

			if(flow.equals("searchCustomerOrderLinesStay")){
				return searchFromSession(session, request, modelMap);
			} else return invoiceController.raiseCustomerOrderInvoice(session, modelMap);
	}

	/**
	 * Non-account
	 */
	@RequestMapping(value="/sellOut", method=RequestMethod.GET)
	public String sellOut(Long customerOrderLineId, String flow, HttpServletRequest request, HttpSession session, ModelMap modelMap) {

		CustomerOrderLine customerOrderLine = customerOrderLineService.get(customerOrderLineId);

		try {
			customerOrderLineService.complete(customerOrderLine);
		} catch(BookmarksException e) {
			addError("Cannot complete this order, has it already been completed?", modelMap);
		}

		return searchFromSession(session, request, modelMap);
	}

	@RequestMapping(value="/cancel", method=RequestMethod.GET)
	public String cancel(Long customerOrderLineId, String flow, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		//Two variations, if an invoice has already been created, add this stock, else create a new stock
		//If invoice already exists, check customer is the same otherwise show message

		CustomerOrderLine customerOrderLine = customerOrderLineService.get(customerOrderLineId);

		if(customerOrderLine.getCanCancel() == false) {
			return "error";
		}

		if(customerOrderLine.canBeFilled() == false) {
			//Put back into stock
			stockItemService.updateQuantityInStock(customerOrderLine.getStockItem(), customerOrderLine.getAmount());
		}

		customerOrderLineService.updateStatus(customerOrderLineId, CustomerOrderLineStatus.CANCELLED);

		return searchFromSession(session, request, modelMap);
	}

	@SuppressWarnings("unchecked")
	private String raiseInvoice(CustomerOrderLine customerOrderLine, HttpServletRequest request, HttpSession session, String flow, ModelMap modelMap) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("orderLineMap");
		Map<Long, CustomerOrderLine> customerOrderLineMapForInvoice = (Map<Long, CustomerOrderLine>) session.getAttribute("customerOrderLineMapForInvoice");
		Invoice invoice = (Invoice) session.getAttribute("invoice");

		//Check this customerOrderLine hasn't already been added
		if(customerOrderLineMapForInvoice != null && customerOrderLineMapForInvoice.get(customerOrderLine.getId()) != null) {
			addInfo("Customer Order ID: " + customerOrderLine.getId() + " has already been added to an invoice!", modelMap);
//			if(flow.equals("searchCustomerOrderLines")){
//				return searchFromSession(session, request, modelMap);
//			} else return edit(customerOrderLine.getId(), flow, modelMap);
			return searchFromSession(session, request, modelMap);
		}
		//New invoice
		if(invoice == null) {
			invoice = invoiceService.getNewInvoice(customerOrderLine.getCustomer().getId());
			invoice.setDeliveryType(customerOrderLine.getDeliveryType());
			saleMap = new HashMap<Long, Sale>();
			customerOrderLineMapForInvoice = new HashMap<Long, CustomerOrderLine>();
			session.setAttribute("orderLineMap", saleMap);
			session.setAttribute("customerOrderLineMapForInvoice", customerOrderLineMapForInvoice);
			session.setAttribute("invoice", invoice);
		} else {
			//Check the customer's match
			if(!invoice.getCustomer().getId().equals(customerOrderLine.getCustomer().getId())) {
				addInfo("An invoice for " + invoice.getCustomer().getFullName() + " is already in progress, deal with this first!", modelMap);
				return searchFromSession(session, request, modelMap);
			}
		}
		//System.out.println("map : " + customerOrderLineMapForInvoice);
		//System.out.println("customerOrderLine : " + customerOrderLine);
		//Add customerorderline to invoice
		customerOrderLineMapForInvoice.put(customerOrderLine.getId(), customerOrderLine);

		invoiceService.addStockItem(customerOrderLine, saleMap, invoice);

		modelMap.addAttribute("price", invoice.getTotalPrice());
		modelMap.addAttribute(saleMap.values());
		modelMap.addAttribute(invoice);
		modelMap.addAttribute(new StockItemSearchBean());

		if(flow.equals("searchCustomerOrderLinesStay")){
			return searchFromSession(session, request, modelMap);
		} else return invoiceController.raiseCustomerOrderInvoice(session, modelMap);
	}

	@RequestMapping(value="/fill", method=RequestMethod.GET)
	public String fill(Long customerOrderLineId, HttpServletRequest request, HttpSession session, ModelMap modelMap){
		CustomerOrderLine customerOrderLine = customerOrderLineService.get(customerOrderLineId);
		customerOrderLine.setAmountFilled(customerOrderLine.getAmount());

		modelMap.addAttribute(customerOrderLine);

		return "fillCustomerOrderLine";
	}

	/**
	From searchCustomerOrderLines.jsp shopping basket icon
	THIS REPLACED sy supplierordercontroller.displayCreateSupplierOrderLineForCustomerOrder
	*/
//	@RequestMapping(value="/sendToSupplier", method=RequestMethod.GET)
//	public String sendToSupplier(Long customerOrderLineId, Long stockItemId, Long supplierId, Long amount, String flow, HttpServletRequest request, HttpSession session, ModelMap modelMap){
//		CustomerOrderLine customerOrderLine = customerOrderLineService.get(customerOrderLineId);
//		Supplier supplier = new Supplier(supplierId);
//		customerOrderLine.getStockItem().setPreferredSupplier(supplier);
//
//		supplierOrderService.sendToSupplier(customerOrderLine, true);
//
//		addMessage("Succesfully sent order to supplier", modelMap);
//
//		return searchFromSession(session, request, modelMap);
//	}

	@RequestMapping(value="/fill", method=RequestMethod.POST)
	public String fill(CustomerOrderLine customerOrderLine, BindingResult bindingResult, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		if(bindingResult.hasErrors()) {
			modelMap.addAttribute(customerOrderLine);
			return "fillCustomerOrderLine";
		}

		customerOrderLineService.fill(customerOrderLine);

		return searchFromSession(session, request, modelMap);
	}

	/**
	 * Most likely come from searchCustomers.jsp
	 * @param id
	 * @param session
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/searchByCustomerID", method=RequestMethod.GET)
	public String searchByCustomerID(Long id, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		Customer customer = customerService.get(id);

		CustomerOrderLineSearchBean customerOrderLineSearchBean = new CustomerOrderLineSearchBean(customer);
		modelMap.addAttribute(customerOrderLineSearchBean); //Why do I need to add this, and not in next search method?
		return search(customerOrderLineSearchBean, null, session, request, modelMap);
	}

	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(ModelMap modelMap) {
		modelMap.addAttribute("customerOrderStatusOptions", CustomerOrderLineStatus.values());
		modelMap.addAttribute(new CustomerOrderLineSearchBean());
		return "searchCustomerOrderLines";
	}

	@Override
	public org.bookmarks.service.Service getService() {
		return customerOrderLineService;
	}
}
