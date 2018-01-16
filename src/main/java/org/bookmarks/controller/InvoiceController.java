package org.bookmarks.controller;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.Locale;

import java.text.SimpleDateFormat;

import org.springframework.format.number.CurrencyStyleFormatter;
import org.springframework.format.number.PercentStyleFormatter;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.bookmarks.report.bean.Rotate;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPage;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

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
import org.bookmarks.service.CustomerService;
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
import org.springframework.web.bind.annotation.ResponseBody;

import org.bookmarks.exceptions.BookmarksException;

@Controller
@RequestMapping("/invoice")
public class InvoiceController extends AbstractBookmarksController<Invoice> {

	@Autowired
	private StockItemService stockItemService;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private InvoiceValidator invoiceValidator;

	@Value("#{applicationProperties['vatNumber']}")
	private String vatNumber;

	@Value("#{applicationProperties['telephoneNumber']}")
	private String telephoneNumber;

	@Value("#{applicationProperties['email']}")
	private String email;

	private Logger logger = LoggerFactory.getLogger(InvoiceController.class);


	private void addParagraph(String text, Document doc, Font font) throws DocumentException {
		Paragraph paragraph = new Paragraph(text, font);
		paragraph.setAlignment(Element.ALIGN_CENTER);
		paragraph.setSpacingAfter(10);
		doc.add(paragraph);
	}

	private void addParagraph(String text, Document doc) throws DocumentException {
		addParagraph(text, doc, new Font(Font.FontFamily.COURIER, 16));
	}

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

	@RequestMapping(value="/generatePdf")
	public @ResponseBody void generatePdf(Long invoiceId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Invoice invoice = invoiceService.get( invoiceId );

		Customer customer = customerService.get(invoice.getCustomer());

		//PDF

		// Page variables
		float fixedHeight = 125f;
		float marginTop = 0f;
		float marginLeft = 10f;
		String nl = System.getProperty("line.separator");

		CurrencyStyleFormatter currencyFormatter = new CurrencyStyleFormatter();
		PercentStyleFormatter percentFormatter = new PercentStyleFormatter();

		Font font = new Font(Font.FontFamily.COURIER, 16, Font.NORMAL);

		Document doc = new Document(PageSize.A4.rotate());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PdfWriter writer = PdfWriter.getInstance(doc, baos);

		doc.open();

		Rotate rotation = new Rotate();
		writer.setPageEvent(rotation);

		doc.newPage();

		rotation.setRotation(PdfPage.LANDSCAPE);

		//Customer name
		addParagraph("Invoice For " + customer.getFullName() + " " + new SimpleDateFormat("dd MMM yyyy").format(new Date()), doc);

		//balance
		Paragraph invoiceDate = new Paragraph("Invoice Date : " + new SimpleDateFormat("dd/MM/yyyy").format( invoice.getCreationDate() ), font);
		invoiceDate.setAlignment(Element.ALIGN_LEFT);
		invoiceDate.setSpacingAfter(10);

		doc.add( new Chunk( "Invoice Date : " + new SimpleDateFormat("dd/MM/yyyy").format( invoice.getCreationDate() ) ));
		doc.add( new Chunk( nl ));
		doc.add( new Chunk("Invoice No. " + invoice.getId().toString()) );
		doc.add( new Chunk( nl ));
		doc.add( new Chunk("VAT No. " + vatNumber) );
		doc.add( new Chunk( nl ));
		doc.add( new Chunk("Telephone No. " + telephoneNumber) );
		doc.add( new Chunk( nl ));
		doc.add( new Chunk("Email " + email) );

		//Customer details table
		PdfPTable customerDetailstable = new PdfPTable(2);
		customerDetailstable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

		StringBuilder labelText = new StringBuilder();
		labelText.append(customer.getFullName());
		labelText.append(nl);
		labelText.append(customer.getFullAddress());

		PdfPCell cell = new PdfPCell(new Phrase(labelText.toString(), font));
		customerDetailstable.addCell( cell );
		customerDetailstable.addCell( new SimpleDateFormat("dd/MM/yyyy").format(new Date()) );

		// doc.add( customerDetailstable );

		//Invoice lines table
		PdfPTable table = new PdfPTable( 7 );
		table.setWidths(new int[] { 8, 1, 2, 2, 2, 2, 2 });

		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		table.setWidthPercentage(100);

		//header
		table.addCell( new Phrase("Stock", font) );
		table.addCell( new Phrase("Qty", font) );
		table.addCell( new Phrase("Discount", font) );
		table.addCell( new Phrase("VAT %", font) );
		table.addCell( new Phrase("VAT £", font) );
		table.addCell( new Phrase("Price", font) );
		table.addCell( new Phrase("Total", font) );

		for (Sale s : invoice.getSales()) {

			table.addCell( s.getStockItem().getTitle() );

			table.addCell( s.getQuantity() + "" );

			table.addCell( percentFormatter.print(s.getDiscount().divide( new BigDecimal(100) ), Locale.UK) );

			table.addCell( percentFormatter.print(s.getVat().divide( new BigDecimal(100) ), Locale.UK) );

			table.addCell( currencyFormatter.print(s.getVatAmount(), Locale.UK) );

			table.addCell( currencyFormatter.print(s.getSellPrice(), Locale.UK) );

			table.addCell( currencyFormatter.print(s.getTotalPrice(), Locale.UK) );
		}

		//Second hand
		if(invoice.getSecondHandPrice() != null && invoice.getSecondHandPrice().floatValue() != 0f) {
			PdfPCell secondHandCell = new PdfPCell(new Phrase("Second Hand"));
			secondHandCell.setBorder(Rectangle.NO_BORDER);
      secondHandCell.setColspan(6);
			table.addCell( secondHandCell );
			PdfPCell secondHandPriceCell = new PdfPCell( new Phrase( currencyFormatter.print(invoice.getSecondHandPrice(), Locale.UK) ));
			secondHandPriceCell.setBorder(Rectangle.NO_BORDER);
			table.addCell( secondHandPriceCell );
		}

		//Service Charge
		if(invoice.getServiceCharge() != null && invoice.getServiceCharge().floatValue() != 0f) {
			PdfPCell serviceChargeCell = new PdfPCell(new Phrase("Service Charge"));
			serviceChargeCell.setBorder(Rectangle.NO_BORDER);
      serviceChargeCell.setColspan(6);
			table.addCell( serviceChargeCell );
			PdfPCell serviceChargePriceCell = new PdfPCell( new Phrase( currencyFormatter.print(invoice.getServiceCharge(), Locale.UK) ));
			serviceChargePriceCell.setBorder(Rectangle.NO_BORDER);
			table.addCell( serviceChargePriceCell );
		}

		doc.add(table);

		doc.close();

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=\"account.pdf\"");
		response.setContentLength(baos.size());

		OutputStream os = response.getOutputStream();
		baos.writeTo(os);
		os.flush();
		os.close();
	}

	private void fillModel(Invoice invoice,	Map<Long, Sale> saleMap, ModelMap modelMap) {
		fillModel(invoice, saleMap, modelMap, true);
	}

	private void fillModel(Invoice invoice,	Map<Long, Sale> saleMap, ModelMap modelMap, boolean calculateDiscount) {

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

		//check for null (another invoice might have been started which would null previous one)
		if(saleMap == null) {
			addError("This invoice no longer exists, please start again or check invoice hasn't already been saved", modelMap);
			return "welcome";
		}
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
			BigDecimal newBalance = customer.getBookmarksAccount().getCurrentBalance().subtract( invoice.getTotalPrice() );
			logger.info("New Balance : {}", newBalance);
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
		modelMap.addAttribute("telephoneNumber", telephoneNumber);
		modelMap.addAttribute("email", email);

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

		return "redirect:searchFromSession";
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
