package org.bookmarks.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import java.math.BigDecimal;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVParser;

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
import org.bookmarks.domain.SponsorshipDetails;
import org.bookmarks.domain.SponsorshipType;

import org.bookmarks.service.EmailService;
import org.bookmarks.service.Service;
import org.bookmarks.repository.AccountRepository;
import org.bookmarks.website.domain.Address;
import org.bookmarks.controller.bean.CustomerMergeFormObject;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.format.number.CurrencyStyleFormatter;

import org.springframework.beans.factory.annotation.Value;

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

	@Value("#{ applicationProperties['accounts.upload.increment'] }")
    private Boolean incrementAccount;

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

	@InitBinder(value = "customer")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	@Autowired
	private InvoiceController invoiceController;

	@RequestMapping(value = "/lookupPostcode", method = RequestMethod.GET)
	public @ResponseBody String lookupPostcode(String postcode) {

		// Now post up to chips
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(
				"http://api.postcodefinder.royalmail.com/CapturePlus/Interactive/Find/v2.00/json3ex.ws?Key="
						+ postcodeLookupKey + "&Country=GBR&SearchTerm=" + postcode
						+ "&LanguagePreference=en&LastId=&SearchFor=Everything&$block=true&$cache=true");
		// httpPost.addHeader("Cookie", "PHPSESSID=sc8g4n5rhvl7bq5p3s8r1k6pi6;
		// __utma=38167458.116586123.1410943363.1410943363.1410945307.2;
		// __utmb=38167458.6.10.1410945307; __utmc=38167458;
		// __utmz=38167458.1410943363.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)");
		httpGet.addHeader("Referer", "http://www.royalmail.com/find-a-postcode");
		httpGet.addHeader("Origin", "http://www.royalmail.com");

		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpGet);

			try {
				HttpEntity entity = response.getEntity();
				String returnCode = EntityUtils.toString(entity);
				// if(EntityUtils.toString(entity) != "success") throw new
				// BookmarksException("Cannot update chips with filename
				// information for stockitem " + stockItem.getId());
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

	@RequestMapping(value = "/lookupAddress", method = RequestMethod.GET)
	public @ResponseBody String lookupAddress(String encodedId) {

		// Now post up to chips
		CloseableHttpClient httpclient = HttpClients.createDefault();

		String url = "http://api.postcodefinder.royalmail.com/CapturePlus/Interactive/RetrieveFormatted/v2.00/json3ex.ws?Key=BH89-YF22-ZU91-EE62&Id="
				+ encodedId + "&Source=&$cache=true&field1format=%7BLatitude%7D&field2format=%7BLongitude%7D";
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Referer", "http://www.royalmail.com/find-a-postcode");
		httpGet.addHeader("Origin", "http://www.royalmail.com");
		httpGet.addHeader("Host", "api.postcodefinder.royalmail.com");

		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpGet);

			try {
				HttpEntity entity = response.getEntity();
				String returnCode = EntityUtils.toString(entity);
				// if(EntityUtils.toString(entity) != "success") throw new
				// BookmarksException("Cannot update chips with filename
				// information for stockitem " + stockItem.getId());
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

	@RequestMapping(value = "/search")
	public String search(CustomerSearchBean customerSearchBean, HttpServletRequest request, HttpSession session,
			ModelMap modelMap) {
		if (customerSearchBean.isFromSession() == false) { // Pagination etc.
															// already set
			setPaginationFromRequest(customerSearchBean, request);
		} else {
			//
		}

		Collection<Customer> customers = customerService.search(customerSearchBean);

		// Don't like, fix for shitty export
		setPageSize(customerSearchBean, modelMap, customers.size());

		// Add to session for later search
		session.setAttribute("customerSearchBean", customerSearchBean);

		modelMap.addAttribute(customers);
		modelMap.addAttribute("searchResultCount", customerSearchBean.getSearchResultCount());
		modelMap.addAttribute(CustomerType.values());

		// setFocus("customerAutoComplete", modelMap);

		return "searchCustomers";
	}

	@RequestMapping(value = "/addCredit", method = RequestMethod.GET)
	public String addCredit(Long customerId, ModelMap modelMap) {

		CreditNote creditNote = new CreditNote(customerId);

		creditNote.setDate( new Date() );

		modelMap.addAttribute(creditNote);

		return "addCredit";
	}

	@ResponseBody
	@RequestMapping(value = "/autoCompleteSurname", method = RequestMethod.GET)
	public String autoCompleteSurname(String term, Boolean accountHolders, HttpServletRequest request,
			ModelMap modelMap) {

				CurrencyStyleFormatter currencyFormatter = new CurrencyStyleFormatter();

		Collection<Customer> customers = customerService.getForAutoComplete(term, accountHolders);

		StringBuffer buffer = new StringBuffer("[ ");

		for (Customer c : customers) {
			String postcode = c.getAddress().getPostcode();
			BigDecimal currentBalance = c.getBookmarksAccount().getCurrentBalance();

			String label =  c.getLastName() + ", " + c.getFirstName() + " "	+ (postcode != null ? postcode : "");
			if( currentBalance != null ) label += " " +  currencyFormatter.print(currentBalance, Locale.UK);

			buffer.append(" { \"label\": \"" + label + "\", \"value\": \"" + c.getId() + "\" }");
			buffer.append(", ");
		}
		String json = buffer.toString();
		json = json.substring(0, json.length() - 2) + "  ]";
		return json;
	}

	@RequestMapping(value = "/addCustomerFromSearch", method = RequestMethod.POST)
	public String addCustomerFromSearch(CustomerSearchBean customerSearchBean, HttpSession session,
			HttpServletRequest request, ModelMap modelMap) {
		modelMap.addAttribute(customerSearchBean.getCustomer());
		modelMap.addAttribute(CustomerType.values());
		return "addCustomer";
	}

	@RequestMapping(value = "/addAndCreateCustomerOrder", method = RequestMethod.POST)
	public String addAndCreateCustomerOrder(@Valid Customer customer, BindingResult bindingResult, HttpSession session,
			ModelMap modelMap) {
		// Check for errors
		if (bindingResult.hasErrors()) {
			return "addCustomer";
		}

		customerService.save(customer);

		return customerOrderController.init(customer.getId(), session, modelMap);
	}

	@RequestMapping(value = "/addAndCreateInvoice", method = RequestMethod.POST)
	public String addAndCreateInvoice(@Valid Customer customer, BindingResult bindingResult, HttpSession session,
			ModelMap modelMap) {
		// Check for errors
		if (bindingResult.hasErrors()) {
			return "addCustomer";
		}

		customerService.save(customer);

		return invoiceController.init(customer.getId(), modelMap, session);
	}

	@RequestMapping(value = "/addCredit", method = RequestMethod.POST)
	public String addCredit(CreditNote creditNote, ModelMap modelMap) {

		logger.info( "Saving creditNote : {}", creditNote );

		customerService.debitAccount( creditNote );

		modelMap.addAttribute("closeWindow", "not null");
		return "closeWindow";
	}

	@RequestMapping(value = "/displaySearch", method = RequestMethod.GET)
	public String displaySearch(ModelMap modelMap) {

		modelMap.addAttribute(new CustomerSearchBean());
		modelMap.addAttribute(CustomerType.values());
		modelMap.addAttribute("focusId", "customerId");
		return "searchCustomers";
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String delete(Long id, ModelMap modelMap) {
		Customer customer = customerService.get(id);
		try {
			customerService.delete(customer);
			addSuccess(customer.getFullName() + " has been deleted", modelMap);
		} catch (Exception e) {
			// Most likely due to this invoice being referenced from col
			addError(
					"Cannot delete! Perhaps this customer has invoices or customer orders, contact admin (Jack) to sort it out",
					modelMap);
		}

		return displaySearch(modelMap);
	}

	@RequestMapping(value = "/merge", method = RequestMethod.GET)
	public String merge(ModelMap modelMap) {

		CustomerMergeFormObject customerMergeFormObject = new CustomerMergeFormObject();

		modelMap.addAttribute(customerMergeFormObject);
		addInfo("Please enter ids of customers to merge", modelMap);

		return "mergeCustomer";
	}

	@RequestMapping(value = "/mergeFromSearchToKeep", method = RequestMethod.GET)
	public String mergeFromSearchToKeep(Long id, ModelMap modelMap) {

		Customer customerToKeep = customerService.get(id);
		CustomerMergeFormObject customerMergeFormObject = new CustomerMergeFormObject();
		customerMergeFormObject.setCustomerToKeep(customerToKeep);

		modelMap.addAttribute(customerMergeFormObject);

		addInfo("Please enter id of customers to discard", modelMap);

		return "mergeCustomer";
	}

	@RequestMapping(value = "/mergeFromSearchToDiscard", method = RequestMethod.GET)
	public String mergeFromSearchToDiscard(Long id, ModelMap modelMap) {

		Customer customerToKeep = customerService.get(id);
		CustomerMergeFormObject customerMergeFormObject = new CustomerMergeFormObject();
		customerMergeFormObject.setCustomerToDiscard(customerToKeep);

		modelMap.addAttribute(customerMergeFormObject);

		addInfo("Please enter id of customers to keep", modelMap);

		return "mergeCustomer";
	}

	@RequestMapping(value = "/merge", method = RequestMethod.POST)
	@Transactional
	public String merge(@Valid CustomerMergeFormObject customerMergeFormObject, BindingResult bindingResult,
			ModelMap modelMap) {

		// Check for errors
		if (bindingResult.hasErrors()) {
			return "mergeCustomer";
		}

		Customer customerToKeep = customerService.get(customerMergeFormObject.getCustomerToKeep().getId());
		Customer customerToDiscard = customerService.get(customerMergeFormObject.getCustomerToDiscard().getId());

		if (customerToKeep == null) {
			bindingResult.rejectValue("customerToKeep", "error", "Customer to keep with id "
					+ customerMergeFormObject.getCustomerToKeep().getId() + " does not exist!");
			modelMap.addAttribute(customerMergeFormObject);
			return "mergeCustomer";
		}
		if (customerToDiscard == null) {
			bindingResult.rejectValue("customerToDiscard", "error", "Customer to discard with id "
					+ customerMergeFormObject.getCustomerToDiscard().getId() + " does not exist!");
			modelMap.addAttribute(customerMergeFormObject);
			return "mergeCustomer";
		}
		if (customerToDiscard.getBookmarksAccount() != null
				&& customerToDiscard.getBookmarksAccount().getAccountHolder() != null
				&& customerToDiscard.getBookmarksAccount().getAccountHolder() == true) {
			addError("Customer To Discard has an account!! Cannot proceed", modelMap);
			modelMap.addAttribute(customerMergeFormObject);
			return "mergeCustomer";
		}

		modelMap.addAttribute("customerToDiscard", customerToDiscard);
		modelMap.addAttribute("customerToKeep", customerToKeep);

		if (!customerToKeep.getFullName().equals(customerToDiscard.getFullName())) {
			addWarning("Customer names do not match! Are you sure??", modelMap);
		}

		return "mergeCustomerConfirmation";
	}

	@RequestMapping(value = "/mergeConfirmed", method = RequestMethod.GET)
	@Transactional
	public String mergeConfirmed(Long customerToKeepId, Long customerToDiscardId, ModelMap modelMap,
			RedirectAttributes redirectAttributes) {

		Customer customerToKeep = customerService.get(customerToKeepId);
		Customer customerToDiscard = customerService.get(customerToDiscardId);

		redirectAttributes.addFlashAttribute("info", "Success! There can be only one");

		logger.info("Successfully merged customers");
		logger.info("Kept : " + +customerToKeep.getId() + " : " + customerToKeep.getFullName());
		logger.info("Discarded : " + +customerToDiscard.getId() + " : " + customerToDiscard.getFullName());

		customerService.merge(customerToKeep, customerToDiscard);

		return "redirect:/";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(@Valid Customer customer, BindingResult bindingResult, HttpSession session,
			HttpServletRequest request, ModelMap modelMap) {

		// Check for errors
		if (bindingResult.hasErrors()) {
			return "addCustomer";
		}

		// if(customer.getContactDetails().getEmail().isEmpty())
		// customer.getContactDetails().setEmail( null );

		customerService.save(customer);

		logger.info("Successfully added customer - " + customer.getId() + " : " + customer.getFullName());

		CustomerSearchBean csb = new CustomerSearchBean();
		csb.setCustomer(customer);
		csb.setPage("1");
		session.setAttribute("customerSearchBean", csb);

		return "redirect:searchFromSession";
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String displayAdd(ModelMap modelMap) {
		modelMap.addAttribute(new Customer());
		modelMap.addAttribute(CustomerType.values());
		return "addCustomer";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String edit(@Valid Customer customer, BindingResult bindingResult, String flow, HttpSession session,
			HttpServletRequest request, ModelMap modelMap) {

		// Check for errors
		if (bindingResult.hasErrors()) {
			modelMap.addAttribute(CustomerType.values());
			modelMap.addAttribute("flow", flow);
			return "editCustomer";
		}

		Customer dbCustomer = customerService.get( customer.getId() );

		dbCustomer.setFirstName( customer.getFirstName() );
		dbCustomer.setLastName( customer.getLastName() );
		dbCustomer.setContactDetails( customer.getContactDetails() );
		dbCustomer.setAddress( customer.getAddress() );
		dbCustomer.setCustomerType( customer.getCustomerType() );
		dbCustomer.setBookmarksDiscount( customer.getBookmarksDiscount() );
		dbCustomer.setNonBookmarksDiscount( customer.getNonBookmarksDiscount() );

		customerService.update( dbCustomer );

		logger.info("Successfully edited customer - " + dbCustomer.getId() + " : " + customer.getFullName());

		if (flow.equals("invoiceSearch") || flow.equals("customerOrderSearch")) {
			modelMap.addAttribute("closeWindow", "not null");
			return "closeWindow";
		}

		return searchFromSession(session, request, modelMap);
	}

	@RequestMapping(value = "/printLabels/{noOfLabels}")
	public @ResponseBody void printLabels(@ModelAttribute CustomerSearchBean customerSearchBean,
			@PathVariable("noOfLabels") Integer noOfLabels, BindingResult bindingResult, HttpSession session,
			HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws DocumentException {

		// Page variables
		float fixedHeight = 125f;
		float marginTop = 0f;
		float marginLeft = 10f;

		if (noOfLabels == 16) {
			fixedHeight = 96f;
			marginTop = 45f;
		}

		setPaginationFromRequest(customerSearchBean, request);

		customerSearchBean.setExport(true); // No pagination

		Collection<Customer> customers = customerService.search(customerSearchBean);

		Document doc = new Document();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// Here We are writing the pdf contents to the output stream via
		// pdfwriter
		// doc will contain the all the format and pages generated by the iText

		PdfWriter writer = PdfWriter.getInstance(doc, baos);
		// doc.setPageSize(PageSize.A4);
		doc.setMargins(marginLeft, 0, marginTop, 0);
		// doc.setMarginMirroring(true);

		PdfPTable table = new PdfPTable(2);
		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		table.setWidthPercentage(100);
		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		doc.open();

		String nl = System.getProperty("line.separator");

		for (Customer customer : customers) {
			Customer c = customerService.get(customer.getId());

			StringBuilder labelText = new StringBuilder();

			Address a = c.getAddress();
			if (a != null) {
				labelText.append(c.getFullName() + nl);
				if (a.getAddress1() != null && !a.getAddress1().trim().isEmpty())
					labelText.append(a.getAddress1() + nl);
				if (a.getAddress2() != null && !a.getAddress2().trim().isEmpty())
					labelText.append(a.getAddress2() + nl);
				if (a.getAddress3() != null && !a.getAddress3().trim().isEmpty())
					labelText.append(a.getAddress3() + nl);
				if (a.getCity() != null && !a.getCity().trim().isEmpty())
					labelText.append(a.getCity() + nl);
				if (a.getPostcode() != null && !a.getPostcode().trim().isEmpty())
					labelText.append(a.getPostcode() + nl);
				// logger.info(labelText.toString());
				// pl.add(labelText.toString());//, "45140-8778"); //regular
				// label with postnet barcode

				Font DOC_FONT = new Font(Font.FontFamily.COURIER, 12, Font.NORMAL);

				PdfPCell cell = new PdfPCell(new Phrase(labelText.toString(), DOC_FONT));
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setFixedHeight(fixedHeight);

				// cell.setNoWrap(true);

				cell.setPaddingLeft(18.0f);
				cell.setPaddingTop(5.0f);

				// cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				// cell.setHorizontalAlignment(Element.ALIGN_LEFT);

				table.addCell(cell);
			}
		}

		// pl.finish();

		doc.add(table);

		try {
			doc.close();

			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=\"labels.pdf\"");
			response.setContentLength(baos.size());

			OutputStream os = response.getOutputStream();
			baos.writeTo(os);
			os.flush();
			os.close();

		} catch (IOException e) {
			// Probably no labels, no page exeption
		}

	}
	@RequestMapping(value = "/editAccount", method = RequestMethod.POST)
	public String editAccount(Customer customer, BindingResult bindingResult, String flow, HttpSession session,
			HttpServletRequest request, ModelMap modelMap) {

		Customer dbCustomer = customerService.get( customer.getId() );

		dbCustomer.setBookmarksAccount( customer.getBookmarksAccount() );

		customerService.update( dbCustomer );

		logger.info("Successfully edited customer acount - " + dbCustomer.getId() + " : " + customer.getFullName());

		return searchFromSession(session, request, modelMap);
	}

	@RequestMapping(value = "/editAccount", method = RequestMethod.GET)
	public String editAccount(Long id, String flow, ModelMap modelMap) {

		Customer customer = customerService.get(id);

		logger.info("About to edit customer account " + customer.getId() + " : " + customer.getFullName());

		modelMap.addAttribute(customer);
		modelMap.addAttribute("flow", flow);
		modelMap.addAttribute(CustomerType.values());

		return "editCustomerAccount";
	}


		@RequestMapping(value = "/editSponsorship", method = RequestMethod.GET)
		public String editSponsorship(Long id, String flow, ModelMap modelMap) {

			Customer customer = customerService.get(id);

			logger.info("About to edit customer sponsorship " + customer.getId() + " : " + customer.getFullName());

			modelMap.addAttribute(customer);
			modelMap.addAttribute("flow", flow);
			modelMap.addAttribute(SponsorshipType.values());

			return "editSponsorship";
		}

		@RequestMapping(value = "/editSponsorship", method = RequestMethod.POST)
		public String editSponsorship(Customer customer, BindingResult bindingResult, String flow, HttpSession session,
				HttpServletRequest request, ModelMap modelMap) {

			if(bindingResult.hasErrors()) {
				return "editSponsorship";
			}

			Customer dbCustomer = customerService.get( customer.getId() );

			dbCustomer.setSponsorshipDetails( customer.getSponsorshipDetails() );

			customerService.update( dbCustomer );

			logger.info("Successfully edited customer sponsorship - " + dbCustomer.getId() + " : " + customer.getFullName());

			return searchFromSession(session, request, modelMap);
		}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, String flow, ModelMap modelMap) {

		Customer customer = customerService.get(id);

		logger.info("About to edit customer " + customer.getId() + " : " + customer.getFullName());

		modelMap.addAttribute(customer);
		modelMap.addAttribute("flow", flow);
		modelMap.addAttribute(CustomerType.values());

		return "editCustomer";
	}

	@RequestMapping(value = "/searchFromSession")
	public String searchFromSession(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		CustomerSearchBean customerSearchBean = (CustomerSearchBean) session.getAttribute("customerSearchBean");
		customerSearchBean.isFromSession(true);
		modelMap.addAttribute(customerSearchBean);
		return search(customerSearchBean, request, session, modelMap);
	}

	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String displayView(Long id, ModelMap modelMap) {
		Customer customer = customerService.get(id);
		modelMap.addAttribute(customer);
		return "viewCustomer";
	}

	@RequestMapping(value = "/select")
	public String select(Long id, ModelMap modelMap, HttpSession session) {
		// Must find out what this selection is for
		// 1) Customer order
		// 2) Invoice
		CustomerOrder customerOrder = (CustomerOrder) session.getAttribute("customerOrder");
		if (customerOrder != null) {
			// Customer Order, attach customer
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
