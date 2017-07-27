package org.bookmarks.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.TransactionType;
import org.bookmarks.domain.VTTransaction;
import org.bookmarks.repository.AccountRepository;
import org.bookmarks.repository.VTTransactionRepository;
import org.bookmarks.service.CustomerService;
import org.bookmarks.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/tsb")
public class TSBController extends AbstractBookmarksController {

	@Value("#{ applicationProperties['accounts.upload.increment'] }")
	private Boolean incrementAccount;

	@Autowired
	private CustomerService customerService;

//	@Autowired
//	private EmailService emailService;

	@Autowired
	private VTTransactionRepository vtTransactionRepository;

	@Autowired
	private AccountRepository accountRepository;

	private Logger logger = LoggerFactory.getLogger(TSBController.class);

	/**
	 * File(s) have been uploaded, converted into credit notes, now process them
	 */
	@RequestMapping(value = "/saveAccountsFromTSB", method = RequestMethod.GET)
	@Transactional
	public String saveAccountsFromTSB(Boolean credit, ModelMap modelMap, HttpSession session) throws IOException {

		Map<String, CreditNote> creditNoteMap = (Map<String, CreditNote>) session.getAttribute("creditNoteMap");

		logger.info("About to process credit notes, credit = {}", credit);

		for (CreditNote creditNote : creditNoteMap.values()) {

			logger.debug("CreditNote : {}", creditNote);

			if (creditNote.getStatus().equals("Unmatched")) {
				continue;
			}

			if (creditNote.getStatus().equals("Already Processed")) {
				continue;
			}

			if (creditNote.isClubAccount()) {
				logger.debug("Found club account, saving vt transaction");

				VTTransaction transaction = new VTTransaction();

				transaction.setTotal(creditNote.getAmount().floatValue());
				transaction.setDate(creditNote.getDate());
				transaction.setPrimaryAccount("Club Account");

				vtTransactionRepository.save(transaction);
			}

			accountRepository.processCreditNote(creditNote, credit);
		}

		addSuccess("All Saved!", modelMap);

		return "confirmUploadAccounts";
	}

	@RequestMapping(value = "/selectMatch", method = RequestMethod.GET)
	public String selectMatch(Integer priority, Long customerId, String transactionDescription, ModelMap modelMap, HttpSession session) throws IOException {

		logger.debug("Have selected " + priority + " match for " + customerId + " transactionDescription : " + transactionDescription);

		Customer customer = customerService.get(customerId);

		Map<String, CreditNote> creditNoteMap = (Map<String, CreditNote>) session.getAttribute("creditNoteMap");
		CreditNote cn = creditNoteMap.get(transactionDescription);

		cn.setCustomer(customer);

		if (priority == 1) {
			cn.setStatus("Potential Primary Match");
		} else if (priority == 2) {
			cn.setStatus("Potential Secondary Match");
		}

		addSuccess("Secondary Matched " + customer.getFullName() + " to " + transactionDescription, modelMap);

		populateCreditNoteModel(creditNoteMap, modelMap);

		return "confirmUploadAccounts";
	}

	@RequestMapping(value = "/match", method = RequestMethod.GET)
	public String match(Long customerId, String transactionDescription, ModelMap modelMap, HttpSession session) throws IOException {

		logger.debug("Finding match for " + customerId + " transactionDescription : " + transactionDescription);

		Map<String, CreditNote> creditNoteMap = (Map<String, CreditNote>) session.getAttribute("creditNoteMap");
		CreditNote cn = creditNoteMap.get(transactionDescription);

		Customer customer = customerService.get(customerId);

		if (cn.getStatus().equals("Already Processed") || cn.getStatus().equals("Primary Matched") || cn.getStatus().equals("Secondary Matched")) {
			addError("Cannot match this row as has status " + cn.getStatus(), modelMap);
			return "confirmUploadAccounts";
		}

		// Check if customer is already matched
		if (customer.getBookmarksAccount().getTsbMatch() != null) {
			logger.debug("Customer already has primary match");
			addInfo("This customer already has a primary match. Either overwrite or select as a secondary match", modelMap);
			modelMap.addAttribute("customer", customer);
			modelMap.addAttribute("transactionDescription", transactionDescription);
			return "selectMatch";
		}

		// Primary match
		cn.setCustomer(customer);
		cn.setStatus("Potential Primary Match");

		addSuccess("Primary match for " + customer.getFullName() + " to " + transactionDescription, modelMap);

		populateCreditNoteModel(creditNoteMap, modelMap);

		return "confirmUploadAccounts";
	}

	@RequestMapping(value = "/uploadAccountsFromTSB", method = RequestMethod.GET)
	public String uploadAccountsFromTSB(ModelMap modelMap) throws IOException {
		modelMap.addAttribute("creditNote", new CreditNote());
		return "uploadAccountsFromTSB";
	}

	/**
	 * Text file to upload from mini beans, for external event 1) CSV file
	 * contains with sales, followed by invoice sales. 2) Create event 3) Create
	 * sales and invoices
	 **/
	@RequestMapping(value = "/uploadAccountsFromTSB", method = RequestMethod.POST)
	public String uploadAccountsFromTSB(CreditNote creditNote, HttpSession session, ModelMap modelMap) throws IOException, java.text.ParseException {

		MultipartFile file = creditNote.getFile();
		String fileName = file.getOriginalFilename();
		Long fileSize = file.getSize();

		float total = 0;
		int count = 0;
		String message = "Successfully uploaded bank text file from TSB!";
		boolean allMatched = true;

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
		CSVParser parser = CSVFormat.DEFAULT.withHeader().withQuote(null).parse(reader);
		List<CSVRecord> records = parser.getRecords();
		// Long size = records.spliterator().getExactSizeIfKnown();
		logger.info("Have records of size " + records.size());

		// if(records.size() == 0) {
		// reader = new InputStreamReader(file.getInputStream());
		// parser = CSVFormat.TDF.withQuote(null).parse(reader);
		// records = parser.getRecords();
		// logger.info("Have records of size " + records.size() );
		// }

		Map<String, CreditNote> creditNoteMap = new HashMap<>();

		for (CSVRecord record : records) {

			logger.info(record.toString());

			CreditNote cn = new CreditNote();

			Date transactionDate = null;
			String transactionType = null;
			String sortCode = null;
			String accountNumber = null;
			String transactionDescription = null;
			String amount = null;
			String transactionReference = null;

			try {
				transactionDate = new SimpleDateFormat("dd/MM/yyyy").parse(record.get(0));
				transactionType = record.get(1);
				sortCode = record.get(2);
				accountNumber = record.get(3);
				transactionDescription = record.get(4);
				amount = record.get(6);
				transactionReference = null;
			} catch (java.text.ParseException e) {
				transactionDate = new SimpleDateFormat("dd MMM yy").parse(record.get(0));
				transactionDescription = record.get(1);
				transactionType = record.get(2);
				sortCode = record.get(2);
				accountNumber = record.get(3);

				amount = record.get(3);
				transactionReference = null;
			}

			// Exception for transfers from club account to main bank account
			if (transactionDescription.startsWith("I S BOOKS LTD") || transactionDescription.startsWith("TO 30932900089719")) {
				cn.setClubAccount(true);
			}

			// Sort out double quotes in transactionDescription
			if (transactionDescription.contains("\"")) {
				transactionDescription = record.get(4) + record.get(5);
				transactionDescription = transactionDescription.replace("\"", "");
				amount = record.get(7);
			}

			// Sort out ampersand and single quote in transactionDescription
			transactionDescription = transactionDescription.replace("&", "");
			transactionDescription = transactionDescription.replace("'", "");
			transactionDescription = transactionDescription.replace("+", "");
			transactionDescription = transactionDescription.replace(",", "");
			transactionDescription = transactionDescription.replace("  ", " ");
			transactionDescription = transactionDescription.trim();

			String tsbMatch = transactionDescription;

			// Find transaction reference (SO and club account do not have one)
			if (!transactionType.equals("SO") && cn.isClubAccount() == false) {
				String pattern = "[A-Z0-9]{16}";

				// Create a Pattern object
				Pattern r = Pattern.compile(pattern);

				Matcher m = r.matcher(transactionDescription);
				if (m.find()) {
					transactionReference = m.group(0);
					logger.debug("transactionReference : " + transactionReference);

				} else {
					addError("Could not find transaction reference in " + transactionDescription, modelMap);
					populateCreditNoteModel(creditNoteMap, modelMap);
					return "confirmUploadAccounts";
				}

				int indexOfTransactionReference = transactionDescription.indexOf(transactionReference);
				tsbMatch = transactionDescription.substring(0, indexOfTransactionReference).trim();
			}

			transactionReference = transactionDescription;

			// Find customer match if possible
			Customer matchedCustomer = customerService.findMatchedCustomer(tsbMatch);

			if (matchedCustomer == null) {
				matchedCustomer = customerService.findSecondaryMatchedCustomer(tsbMatch);
				if (matchedCustomer == null) {
					cn.setStatus("Unmatched");
					allMatched = false;
				} else {
					cn.setStatus("Secondary Matched");
					cn.setCustomer(matchedCustomer);
				}
			} else {
				cn.setStatus("Primary Matched");
				cn.setCustomer(matchedCustomer);
			}

			if (transactionType.equals("SO")) {
				transactionReference = record.get(0) + "-SO-" + transactionDescription;
			}

			if (cn.isClubAccount()) {
				cn.setStatus("Club Account");
				amount = "-" + record.get(5);
				transactionReference = record.get(0) + "-CLUB-" + amount;
				transactionDescription = transactionReference + " " + transactionDate;
				Customer clubAccountCustomer = customerService.get(31245l);
				cn.setCustomer(clubAccountCustomer);
			}

			// Check that this transaction hasn't already been processed
			CreditNote matchedCreditNote = accountRepository.getCreditNote(transactionReference);

			if (matchedCreditNote != null)
				cn.setStatus("Already Processed");

			if (logger.isDebugEnabled()) {
				logger.debug("**********************");
				logger.debug("" + transactionDate);
				logger.debug(transactionType);
				logger.debug(sortCode);
				logger.debug(accountNumber);

				logger.debug(tsbMatch);
				logger.debug(amount);
			}

			transactionDescription = tsbMatch;

			cn.setDate(transactionDate);
			cn.setAmount(new BigDecimal(amount));
			cn.setTransactionDescription(transactionDescription);
			cn.setTransactionType(TransactionType.TFR);
			cn.setTransactionReference(transactionReference);

			creditNoteMap.put(transactionDescription, cn);

			total += cn.getAmount().floatValue();
			count++;
		}

		session.setAttribute("creditNoteMap", creditNoteMap);

		populateCreditNoteModel(creditNoteMap, modelMap);

		if (allMatched == true) {
			message += ". All matched!";
		}

		if (count == 150) {
			addWarning("Upload from bank text file successful! Have got " + count + " lines. There may be more lines!! Check paper statements", modelMap);
		} else {
			addSuccess("Upload from bank text file successful! Have got " + count + " lines.", modelMap);
		}

		return "confirmUploadAccounts";
	}

	private void populateCreditNoteModel(Map<String, CreditNote> creditNoteMap, ModelMap modelMap) {
		modelMap.addAttribute("creditNoteList", creditNoteMap.values().stream().sorted((p1, p2) -> p2.getStatus().compareTo(p1.getStatus())).collect(Collectors.toList()));

	}

	@Override
	public Service<Customer> getService() {
		return customerService;
	}
}
