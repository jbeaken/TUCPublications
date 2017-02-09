package org.bookmarks.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;

import java.math.BigDecimal;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVPrinter;

import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerType;
import org.bookmarks.domain.TransactionType;
import org.bookmarks.service.CustomerService;
import org.bookmarks.service.EmailService;
import org.bookmarks.service.Service;
import org.bookmarks.repository.AccountRepository;
import org.bookmarks.website.domain.Address;
import org.bookmarks.controller.bean.CustomerMergeFormObject;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


@Controller
@RequestMapping("/customer")
public class CustomerController extends AbstractBookmarksController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private CustomerOrderController customerOrderController;

	@Autowired
	private AccountRepository accountRepository;

	private Logger logger = LoggerFactory.getLogger(CustomerController.class);

	private static final String postcodeLookupKey = "BH89-YF22-ZU91-EE62";

	@InitBinder(value="customer")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	@Autowired
	private InvoiceController invoiceController;

	@RequestMapping(value = "/saveAccountsFromTSB", method = RequestMethod.GET)
	public String saveAccountsFromTSB(ModelMap modelMap, HttpSession session) throws IOException {

		Map<String, CreditNote> creditNoteMap = (Map<String, CreditNote>) session.getAttribute("creditNoteMap");

		for(CreditNote creditNote : creditNoteMap.values()) {
//			if(creditNote.getCustomer() == null) {
//
//				addError("Please match customer " + creditNote.getTransactionDescription(), modelMap);
//
//				return "confirmUploadAccounts";
//			}
		}

		for(CreditNote creditNote : creditNoteMap.values()) {
			
			if(creditNote.getStatus().equals("Unmatched")) {
				continue;
			}			

			if(creditNote.getStatus().equals("Already Processed")) {
				continue;
			}

			accountRepository.processCreditNote( creditNote );
		}

		addSuccess("All Saved!", modelMap);

		return "confirmUploadAccounts";
	}

	@RequestMapping(value = "/match", method = RequestMethod.GET)
	public String match(Long customerId, String transactionDescription, ModelMap modelMap, HttpSession session) throws IOException {
		Customer customer = customerService.get( customerId );

		Map<String, CreditNote> creditNoteMap = (Map<String, CreditNote>)session.getAttribute("creditNoteMap");
		CreditNote cn = creditNoteMap.get( transactionDescription );

		if(cn.getStatus().equals("Already Processed") || cn.getStatus().equals("Matched")) {
				addError("Cannot match this row", modelMap);
				return "confirmUploadAccounts";
		}

		cn.setCustomer( customer );
		cn.setStatus( "Potential Match" );

		addSuccess("Matched!", modelMap);

		return "confirmUploadAccounts";
	}

	@RequestMapping(value = "/uploadAccountsFromTSB", method = RequestMethod.GET)
	public String uploadAccountsFromTSB(ModelMap modelMap) throws IOException {
		modelMap.addAttribute("creditNote", new CreditNote());
		return "uploadAccountsFromTSB";
	}

/**
	 * Text file to upload from mini beans, for extennal event 1) CSV file
	 * contains with sales, followed by invoice sales. 2) Create event 3) Create
	 * sales and invoices
	 **/
	@RequestMapping(value = "/uploadAccountsFromTSB", method = RequestMethod.POST)
	public String uploadAccountsFromTSB(CreditNote creditNote, HttpSession session, ModelMap modelMap) throws IOException, java.text.ParseException {

		MultipartFile file = creditNote.getFile();
		String fileName = file.getOriginalFilename();
		Long fileSize = file.getSize();

		float total = 0;

		logger.info("Uploading customer accounts ");

		if (fileName.indexOf(".csv") == -1) {
			addError("The file must be a csv file ", modelMap);
			return "confirmUploadAccounts";
		}

		if (fileSize > 100000) {
			addError("File too big! Size is " + (fileSize / 1000) + " Kb", modelMap);
			return "confirmUploadAccounts";
		}

		Reader reader = new InputStreamReader(file.getInputStream());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().withQuote(null).parse(reader);

		Map<String, CreditNote> creditNoteMap = new HashMap<>();

		for (CSVRecord record : records) {

			CreditNote cn = new CreditNote();

			Date transactionDate = new SimpleDateFormat("dd/MM/yyyy").parse( record.get(0) );
			String transactionType = record.get(1);
			String sortCode = record.get(2);
			String accountNumber = record.get(3);
			String transactionDescription = record.get(4);
			String amount = record.get(6);
			String transactionReference =  null;

			//Exception for transfers from club account to main bank account
			if(transactionDescription.startsWith( "I S BOOKS LTD" ) || transactionDescription.startsWith( "TO 30932900089719" )) {
				cn.setClubAccount(true);
			}

			//Sort out double quotes in transactionDescription
			if(transactionDescription.contains("\"")) {
				transactionDescription = record.get(4) + record.get(5);
				transactionDescription = transactionDescription.replace("\"", "");
				amount = record.get(7);
			}

			String tsbMatch = transactionDescription;

			//Find transcation reference (SO and club account do not have one)
			if(!transactionType.equals( "SO" ) && cn.isClubAccount() == false) {
				String pattern = "[A-Z0-9]{16}";

				// Create a Pattern object
				Pattern r = Pattern.compile(pattern);

				Matcher m = r.matcher(transactionDescription);
				if (m.find( )) {
						transactionReference = m.group(0);
						System.out.println("transactionReference : " + transactionReference );

				 } else {
						addError("Could not find transaction reference in " + transactionDescription, modelMap);
						return "confirmUploadAccounts";
				 }

				int indexOfTransactionReference = transactionDescription.indexOf( transactionReference );
				tsbMatch = transactionDescription.substring( 0, indexOfTransactionReference );
			}

			//Find customer match if possible
			Customer matchedCustomer = customerService.findMatchedCustomer( tsbMatch );

			if(matchedCustomer == null) {
				cn.setStatus( "Unmatched" );
			} else {
				cn.setStatus( "Matched" );
				cn.setCustomer(matchedCustomer);
			}
			
			if(cn.isClubAccount()) {
				cn.setStatus("Club Account");
				amount = "-" + record.get(5);
				transactionReference = record.get(0) + amount;
				Customer clubAccountCustomer = customerService.get( 31245l );
				cn.setCustomer(clubAccountCustomer);
			}

			//Check that this transaction hasn't already been processed
			CreditNote matchedCreditNote = accountRepository.getCreditNote( transactionReference );
			if(matchedCreditNote != null) {
				cn.setStatus("Already Processed");
			}

			// System.out.println( "**********************" );
			// System.out.println( transactionDate );
			// System.out.println( transactionType );
			// System.out.println( sortCode );
			// System.out.println( accountNumber );



//				System.out.println( tsbMatch );
				// System.out.println( amount );
				transactionDescription = tsbMatch;
				//Get customer


				cn.setDate(transactionDate);
				cn.setAmount(new BigDecimal(amount));
				cn.setTransactionDescription( transactionDescription );
				cn.setTransactionType(TransactionType.TFR);
				cn.setTransactionReference( transactionReference );


				creditNoteMap.put(transactionDescription, cn);
			}

		session.setAttribute("creditNoteMap", creditNoteMap);
		addSuccess("Have found sales of value Press confirm to save", modelMap);

		return "confirmUploadAccounts";
	}


	@RequestMapping(value="/lookupPostcode", method=RequestMethod.GET)
	public @ResponseBody String lookupPostcode(String postcode) {

		//Now post up to chips
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet("http://api.postcodefinder.royalmail.com/CapturePlus/Interactive/Find/v2.00/json3ex.ws?Key=" + postcodeLookupKey + "&Country=GBR&SearchTerm=" + postcode + "&LanguagePreference=en&LastId=&SearchFor=Everything&$block=true&$cache=true");
		//httpPost.addHeader("Cookie", "PHPSESSID=sc8g4n5rhvl7bq5p3s8r1k6pi6; __utma=38167458.116586123.1410943363.1410943363.1410945307.2; __utmb=38167458.6.10.1410945307; __utmc=38167458; __utmz=38167458.1410943363.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)");
		httpGet.addHeader("Referer", "http://www.royalmail.com/find-a-postcode");
		httpGet.addHeader("Origin", "http://www.royalmail.com");


		CloseableHttpResponse response =  null;
		try {
			response = httpclient.execute(httpGet);

			try {
			    HttpEntity entity = response.getEntity();
			    String returnCode = EntityUtils.toString(entity);
			    //if(EntityUtils.toString(entity) != "success") throw new BookmarksException("Cannot update chips with filename information for stockitem " + stockItem.getId());
			    EntityUtils.consume(entity);
			    logger.info("address lookup : " + returnCode);
			    return returnCode;
			} finally {
			    response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "error";
	}




	@RequestMapping(value="/lookupAddress", method=RequestMethod.GET)
	public @ResponseBody String lookupAddress(String encodedId) {

		//Now post up to chips
		CloseableHttpClient httpclient = HttpClients.createDefault();

		String url = "http://api.postcodefinder.royalmail.com/CapturePlus/Interactive/RetrieveFormatted/v2.00/json3ex.ws?Key=BH89-YF22-ZU91-EE62&Id=" + encodedId + "&Source=&$cache=true&field1format=%7BLatitude%7D&field2format=%7BLongitude%7D";
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Referer", "http://www.royalmail.com/find-a-postcode");
		httpGet.addHeader("Origin", "http://www.royalmail.com");
		httpGet.addHeader("Host", "api.postcodefinder.royalmail.com");


		CloseableHttpResponse response =  null;
		try {
			response = httpclient.execute(httpGet);

			try {
			    HttpEntity entity = response.getEntity();
			    String returnCode = EntityUtils.toString(entity);
			    //if(EntityUtils.toString(entity) != "success") throw new BookmarksException("Cannot update chips with filename information for stockitem " + stockItem.getId());
			    EntityUtils.consume(entity);
			    logger.info("address lookup : " + returnCode);
			    return returnCode;
			} finally {
			    response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "error";
	}


	@RequestMapping(value="/search")
	public String search(CustomerSearchBean customerSearchBean, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		if(customerSearchBean.isFromSession() == false) { //Pagination etc. already set
			setPaginationFromRequest(customerSearchBean, request);
		} else {
			//
		}

		Collection<Customer> customers = customerService.search(customerSearchBean);

		//Don't like, fix for shitty export
		setPageSize(customerSearchBean, modelMap, customers.size());

		//Add to session for later search
		session.setAttribute("customerSearchBean", customerSearchBean);

		modelMap.addAttribute(customers);
		modelMap.addAttribute("searchResultCount", customerSearchBean.getSearchResultCount());
		modelMap.addAttribute(CustomerType.values());

		//setFocus("customerAutoComplete", modelMap);

		return "searchCustomers";
	}

	@RequestMapping(value="/addCredit", method=RequestMethod.GET)
	public String addCredit(Long customerId, ModelMap modelMap) {
		CreditNote creditNote = new CreditNote(customerId);
		modelMap.addAttribute(creditNote);
		return "addCredit";
	}

	@ResponseBody
	@RequestMapping(value="/autoCompleteSurname", method=RequestMethod.GET)
	public String autoCompleteSurname(String term, Boolean accountHolders, HttpServletRequest request, ModelMap modelMap) {

		Collection<Customer> customers = customerService.getForAutoComplete(term, accountHolders);

		StringBuffer buffer = new StringBuffer("[ ");

		for(Customer c : customers) {
			String postcode = c.getAddress().getPostcode();
			buffer.append(" { \"label\": \"" + c.getLastName() + ", " + c.getFirstName() + " " + (postcode != null ? postcode : "") + "\", \"value\": \"" + c.getId() + "\" }");
			buffer.append(", ");
		}
		String json = buffer.toString();
		json = json.substring(0, json.length() - 2) + "  ]";
		return json;
	}

	@RequestMapping(value="/addCustomerFromSearch", method=RequestMethod.POST)
	public String addCustomerFromSearch(CustomerSearchBean customerSearchBean, HttpSession session, HttpServletRequest request,ModelMap modelMap){
		modelMap.addAttribute(customerSearchBean.getCustomer());
		modelMap.addAttribute(CustomerType.values());
		return "addCustomer";
	}

	@RequestMapping(value="/addAndCreateCustomerOrder", method=RequestMethod.POST)
	public String addAndCreateCustomerOrder(@Valid Customer customer, BindingResult bindingResult, HttpSession session, ModelMap modelMap) {
		//Check for errors
		if(bindingResult.hasErrors()){
			return "addCustomer";
		}

		customerService.save(customer);

		return customerOrderController.init(customer.getId(), session, modelMap);
	}


	@RequestMapping(value="/addAndCreateInvoice", method=RequestMethod.POST)
	public String addAndCreateInvoice(@Valid Customer customer, BindingResult bindingResult, HttpSession session, ModelMap modelMap) {
		//Check for errors
		if(bindingResult.hasErrors()){
			return "addCustomer";
		}

		customerService.save(customer);

		return invoiceController.init(customer.getId(), modelMap, session);
	}

	@RequestMapping(value="/addCredit", method=RequestMethod.POST)
	public String addCredit(CreditNote creditNote, ModelMap modelMap) {
		customerService.debitAccount(creditNote);

		modelMap.addAttribute("closeWindow", "not null");
		return "closeWindow";
	}

	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(ModelMap modelMap) {

		modelMap.addAttribute(new CustomerSearchBean());
		modelMap.addAttribute(CustomerType.values());
		modelMap.addAttribute("focusId", "customerId");
		return "searchCustomers";
	}

	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public String delete(Long id, ModelMap modelMap) {
		Customer customer = customerService.get(id);
		try {
			customerService.delete(customer);
			addSuccess(customer.getFullName() + " has been deleted", modelMap);
		} catch (Exception e) {
			//Most likely due to this invoice being referenced from col
			addError("Cannot delete! Perhaps this customer has invoices or customer orders, contact admin (Jack) to sort it out", modelMap);
		}

		return displaySearch(modelMap);
	}

	@RequestMapping(value="/merge", method=RequestMethod.GET)
	public String merge(ModelMap modelMap) {

		CustomerMergeFormObject customerMergeFormObject = new CustomerMergeFormObject();

		modelMap.addAttribute(customerMergeFormObject);
		addInfo("Please enter ids of customers to merge", modelMap);

		return "mergeCustomer";
	}


	@RequestMapping(value="/mergeFromSearchToKeep", method=RequestMethod.GET)
	public String mergeFromSearchToKeep(Long id, ModelMap modelMap) {

		Customer customerToKeep = customerService.get( id );
		CustomerMergeFormObject customerMergeFormObject = new CustomerMergeFormObject();
		customerMergeFormObject.setCustomerToKeep( customerToKeep );

		modelMap.addAttribute(customerMergeFormObject);

		addInfo("Please enter id of customers to discard", modelMap);

		return "mergeCustomer";
	}

	@RequestMapping(value="/mergeFromSearchToDiscard", method=RequestMethod.GET)
	public String mergeFromSearchToDiscard(Long id, ModelMap modelMap) {

		Customer customerToKeep = customerService.get( id );
		CustomerMergeFormObject customerMergeFormObject = new CustomerMergeFormObject();
		customerMergeFormObject.setCustomerToDiscard( customerToKeep );

		modelMap.addAttribute(customerMergeFormObject);

		addInfo("Please enter id of customers to keep", modelMap);

		return "mergeCustomer";
	}




		@RequestMapping(value="/merge", method=RequestMethod.POST)
		@Transactional
		public String merge(@Valid CustomerMergeFormObject customerMergeFormObject, BindingResult bindingResult, ModelMap modelMap) {

			//Check for errors
			if(bindingResult.hasErrors()){
				return "mergeCustomer";
			}

			Customer customerToKeep = customerService.get( customerMergeFormObject.getCustomerToKeep().getId() );
			Customer customerToDiscard = customerService.get( customerMergeFormObject.getCustomerToDiscard().getId() );

			if(customerToKeep == null) {
				bindingResult.rejectValue("customerToKeep", "error", "Customer to keep with id " + customerMergeFormObject.getCustomerToKeep().getId() + " does not exist!" );
				modelMap.addAttribute(customerMergeFormObject);
				return "mergeCustomer";
			}
			if(customerToDiscard == null) {
				bindingResult.rejectValue("customerToDiscard", "error", "Customer to discard with id " + customerMergeFormObject.getCustomerToDiscard().getId() + " does not exist!" );
				modelMap.addAttribute(customerMergeFormObject);
				return "mergeCustomer";
			}
			if(customerToDiscard.getBookmarksAccount() != null && customerToDiscard.getBookmarksAccount().getAccountHolder() != null && customerToDiscard.getBookmarksAccount().getAccountHolder() == true) {
				addError("Customer To Discard has an account!! Cannot proceed", modelMap);
				modelMap.addAttribute(customerMergeFormObject);
				return "mergeCustomer";
			}

			modelMap.addAttribute("customerToDiscard", customerToDiscard);
			modelMap.addAttribute("customerToKeep", customerToKeep);

			if(!customerToKeep.getFullName().equals(customerToDiscard.getFullName())) {
				addWarning("Customer names do not match! Are you sure??", modelMap);
			}

			return "mergeCustomerConfirmation";
		}

		@RequestMapping(value="/mergeConfirmed", method=RequestMethod.GET)
		@Transactional
		public String mergeConfirmed(Long customerToKeepId, Long customerToDiscardId, ModelMap modelMap, RedirectAttributes redirectAttributes) {

			Customer customerToKeep = customerService.get( customerToKeepId );
			Customer customerToDiscard = customerService.get( customerToDiscardId );

			redirectAttributes.addFlashAttribute("info", "Success! There can be only one");

			logger.info("Successfully merged customers");
			logger.info("Kept : " + + customerToKeep.getId() + " : " + customerToKeep.getFullName());
			logger.info("Discarded : " + + customerToDiscard.getId() + " : " + customerToDiscard.getFullName());

			customerService.merge(customerToKeep, customerToDiscard);

			return "redirect:/";
		}

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String add(@Valid Customer customer, BindingResult bindingResult, HttpSession session, HttpServletRequest request, ModelMap modelMap) {

		//Check for errors
		if(bindingResult.hasErrors()){
			return "addCustomer";
		}

		//if(customer.getContactDetails().getEmail().isEmpty()) customer.getContactDetails().setEmail( null );

		customerService.save(customer);

		logger.info("Successfully added customer - " + customer.getId() + " : " + customer.getFullName());

		CustomerSearchBean csb = new CustomerSearchBean();
		csb.setCustomer(customer);
		csb.setPage("1");
		session.setAttribute("customerSearchBean", csb);

		return "redirect:searchFromSession";
	}

	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String displayAdd(ModelMap modelMap) {
		modelMap.addAttribute(new Customer());
		modelMap.addAttribute(CustomerType.values());
		return "addCustomer";
	}

	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(@Valid Customer customer, BindingResult bindingResult, String flow, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		//Check for errors
		if(bindingResult.hasErrors()){
			modelMap.addAttribute(CustomerType.values());
			modelMap.addAttribute("flow", flow);
			return "editCustomer";
		}

		customerService.update(customer);

		logger.info("Successfully edited customer - " + customer.getId() + " : " + customer.getFullName());

		if(flow.equals("invoiceSearch") || flow.equals("customerOrderSearch")) {
			modelMap.addAttribute("closeWindow", "not null");
			return "closeWindow";
		}

		return searchFromSession(session, request, modelMap);
	}

	@RequestMapping(value="/printLabels/{noOfLabels}")
	public @ResponseBody void printLabels(@ModelAttribute CustomerSearchBean customerSearchBean, @PathVariable("noOfLabels") Integer noOfLabels, BindingResult bindingResult, HttpSession session, HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws DocumentException {

		//Page variables
		float fixedHeight = 125f;
		float marginTop = 0f;
		float marginLeft = 10f;

		if( noOfLabels == 16) {
			fixedHeight = 96f;
			marginTop = 45f;
		}

		setPaginationFromRequest(customerSearchBean, request);

		customerSearchBean.setExport(true); //No pagination

		Collection<Customer> customers = customerService.search(customerSearchBean);

		Document doc = new Document();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //Here We are writing the pdf contents to the output stream via pdfwriter
		//doc will contain the all the format and pages generated by the iText

		PdfWriter writer = PdfWriter.getInstance(doc, baos);
		//doc.setPageSize(PageSize.A4);
		doc.setMargins(marginLeft, 0, marginTop, 0);
//		doc.setMarginMirroring(true);

		PdfPTable table = new PdfPTable(2);
		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		table.setWidthPercentage(100);
		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		doc.open();


		String nl = System.getProperty("line.separator");

		for(Customer customer : customers) {
			Customer c = customerService.get(customer.getId());

			 StringBuilder labelText = new StringBuilder();

			 Address a = c.getAddress();
			 if(a != null) {
		    	 labelText.append(c.getFullName() + nl);
		    	 if(a.getAddress1() != null && !a.getAddress1().trim().isEmpty()) labelText.append(a.getAddress1() + nl);
		    	 if(a.getAddress2() != null && !a.getAddress2().trim().isEmpty()) labelText.append(a.getAddress2() + nl);
		    	 if(a.getAddress3() != null && !a.getAddress3().trim().isEmpty()) labelText.append(a.getAddress3() + nl);
					 if(a.getCity() != null && !a.getCity().trim().isEmpty()) labelText.append(a.getCity() + nl);
		    	 if(a.getPostcode() != null && !a.getPostcode().trim().isEmpty()) labelText.append(a.getPostcode() + nl);
//		    	 logger.info(labelText.toString());
		    	// pl.add(labelText.toString());//, "45140-8778");  //regular label with postnet barcode

		    	 Font DOC_FONT = new Font (Font.FontFamily.COURIER, 12, Font.NORMAL);

		    	 PdfPCell cell = new PdfPCell (new Phrase(labelText.toString(), DOC_FONT));
		    	 cell.setBorder(Rectangle.NO_BORDER);
		    	 cell.setFixedHeight( fixedHeight );

//		         cell.setNoWrap(true);

		         cell.setPaddingLeft(18.0f);
		         cell.setPaddingTop(5.0f);

//		         cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//		         cell.setHorizontalAlignment(Element.ALIGN_LEFT);

		    	 table.addCell(cell);
			 }
		}

//		pl.finish();

		doc.add(table);

		try {
			doc.close();


		 response.setContentType("application/pdf");
		 response.setHeader("Content-Disposition","attachment; filename=\"labels.pdf\"");
		 response.setContentLength(baos.size());

		 OutputStream os = response.getOutputStream();
		 baos.writeTo(os);
		 os.flush();
		 os.close();

		} catch(IOException e) {
			//Probably no labels, no page exeption
		}

	}

	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String edit(Long id, String flow, ModelMap modelMap) {

		Customer customer = customerService.get(id);

		logger.info("About to edit customer " + customer.getId() + " : " + customer.getFullName());

		modelMap.addAttribute(customer);
		modelMap.addAttribute("flow", flow);
		modelMap.addAttribute(CustomerType.values());

		return "editCustomer";
	}


	@RequestMapping(value="/searchFromSession")
	public String searchFromSession(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		CustomerSearchBean customerSearchBean = (CustomerSearchBean) session.getAttribute("customerSearchBean");
		customerSearchBean.isFromSession(true);
		modelMap.addAttribute(customerSearchBean);
		return search(customerSearchBean, request, session, modelMap);
	}

	@RequestMapping(value="/view", method=RequestMethod.GET)
	public String displayView(Long id, ModelMap modelMap) {
		Customer customer = customerService.get(id);
		modelMap.addAttribute(customer);
		return "viewCustomer";
	}

	@RequestMapping(value="/select")
	public String select(Long id, ModelMap modelMap, HttpSession session) {
		//Must find out what this selection is for
		//1) Customer order
		//2) Invoice
		CustomerOrder customerOrder = (CustomerOrder) session.getAttribute("customerOrder");
		if(customerOrder!= null) {
			//Customer Order, attach customer
			Customer customer = customerService.get(id);
			customerOrder.setCustomer(customer);
		}
		modelMap.addAttribute(customerOrder);
		return "displayCustomerOrder";
	}

	@Override
	public Service<Customer> getService() {
		return customerService;
	}
}
