package org.bookmarks.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.ParseException;
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
import org.bookmarks.exceptions.BookmarksException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/tsb")
public class TSBController extends AbstractBookmarksController {

	@Value("#{ applicationProperties['accounts.upload.increment'] }")
	private Boolean incrementAccount;

	@Autowired
	private CustomerService customerService;

	// @Autowired
	// private EmailService emailService;

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
	public String saveAccountsFromTSB(@RequestParam("credit") Boolean credit, ModelMap modelMap, HttpSession session) throws IOException {

		CreditNoteHolder holder = (CreditNoteHolder) session.getAttribute("creditNoteHolder");

		logger.info("About to process credit notes, credit = {}", credit);

		for (CreditNote creditNote : holder.getCreditNoteMap().values()) {

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

		return "redirect:/tsb/displayUploadedAccountsSummary";
	}

	@RequestMapping(value = "/displayUploadedAccountsSummary", method = RequestMethod.GET)
	public String displayUploadAccountsSummary(HttpSession session, ModelMap modelMap) {
		CreditNoteHolder holder = (CreditNoteHolder) session.getAttribute("creditNoteHolder");

		modelMap.addAttribute("noOfMatched", holder.getNoMatched());
		modelMap.addAttribute(holder.getMatched());

		return "displayUploadedAccountsSummary";
	}



	@RequestMapping(value = "/uploadCSVFiles", method = RequestMethod.POST)
	public String uploadCSVFiles(@RequestParam("csvFiles") MultipartFile[] csvFiles, ModelMap modelMap, HttpSession session) throws IOException {

		CreditNoteHolder holder = new CreditNoteHolder();

		for (MultipartFile csvFile : csvFiles) {
			try {
				processCSVFile(holder, csvFile);
			} catch (Exception e) {
				logger.error("Problem uploading csv files from file " + csvFile.getOriginalFilename(), e);
			}
		}

		//Sanity check, all lines should have been uploaded
		assert holder.getCreditNoteMap().values().size() == holder.getNoOfLines();

		session.setAttribute("creditNoteHolder", holder);

		populateCreditNoteModel(holder, modelMap);

		return "confirmUploadAccounts";
	}

	private void processCSVFile(CreditNoteHolder holder, MultipartFile csvFile) throws IOException, ParseException {
		String fileName = csvFile.getOriginalFilename();

		logger.info("Uploading tsb bank lines from csv {}", csvFile.getOriginalFilename());

		int count = 0;

		// Validate the uploaded file
		if (fileName.indexOf(".csv") == -1) {
			logger.info("Not a csv file, exiting!");
			throw new BookmarksException(csvFile.getOriginalFilename() + " is not a csv file!");
		}

		Reader reader = new InputStreamReader(csvFile.getInputStream());
		CSVParser parser = CSVFormat.DEFAULT.withHeader().withQuote(null).parse(reader);
		List<CSVRecord> records = parser.getRecords();

		logger.info("Have records of size " + records.size());

		for (CSVRecord record : records) {

			logger.info(record.toString());

			holder.incrementNoOfLines();
			count++;

			CreditNote cn = new CreditNote();

			Date transactionDate = null;
			String transactionType = null;
			String sortCode = null;
			String accountNumber = null;
			String transactionDescription = null;
			String amount = null;
			String transactionReference = null;
			String transactionDateStr = record.get(0);

			try {
				transactionDate = new SimpleDateFormat("dd/MM/yyyy").parse(transactionDateStr);
				transactionType = record.get(1);
				sortCode = record.get(2);
				accountNumber = record.get(3);
				transactionDescription = record.get(4);
				amount = record.get(6);
			} catch (java.text.ParseException e) {
				transactionDate = new SimpleDateFormat("dd MMM yy").parse(transactionDateStr);
				transactionDescription = record.get(1);
				transactionType = record.get(2);
				sortCode = record.get(2);
				accountNumber = record.get(3);
				amount = record.get(3);
			}

			// is this a club account transfer into the main bookmarks account?
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
					throw new BookmarksException(csvFile.getOriginalFilename() + " : Could not find transaction reference in " + transactionDescription);
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
				} else {
					cn.setStatus("Secondary Matched");
					cn.setCustomer(matchedCustomer);
				}
			} else {
				cn.setStatus("Primary Matched");
				cn.setCustomer(matchedCustomer);
			}

			if (transactionType.equals("SO")) {
				transactionReference = transactionDateStr + "-SO-" + transactionDescription;
			}

			if (cn.isClubAccount()) {
				cn.setStatus("Club Account");
				amount = "-" + record.get(5);
				transactionReference = transactionDescription + "-" + transactionDateStr + "-CLUB" + amount;
				transactionDescription = transactionReference;
				Customer clubAccountCustomer = customerService.get(31245l);
				cn.setCustomer(clubAccountCustomer);
			}

			// Check that this transaction hasn't already been processed, look
			// up based on transactionReference
			CreditNote matchedCreditNote = accountRepository.getCreditNote(transactionReference);

			if (matchedCreditNote != null) {
				cn.setStatus("Already Processed");
			}

			if (logger.isDebugEnabled()) {
				logger.debug("**********************");
				logger.debug("Is clubAccount = {}", cn.isClubAccount());
				logger.debug("" + transactionDate);
				logger.debug(transactionType);
				logger.debug(sortCode);
				logger.debug(accountNumber);
				logger.debug("transactionDescription {}", transactionDescription);
				logger.debug("transactionReference {}", transactionReference);
				logger.debug("tsbMatch {}", tsbMatch);
				logger.debug(amount);
			}

			if (!cn.isClubAccount()) {
				transactionDescription = tsbMatch;
			}

			cn.setDate(transactionDate);
			cn.setAmount(new BigDecimal(amount));
			cn.setTransactionDescription(transactionDescription);
			cn.setTransactionType(TransactionType.TFR);
			cn.setTransactionReference(transactionReference);

			holder.getCreditNoteMap().put(transactionReference, cn);
		}

		logger.info("{} contained {} lines", csvFile.getOriginalFilename(), count);

	}

	@RequestMapping(value = "/selectMatch", method = RequestMethod.GET)
	public String selectMatch(Integer priority, Long customerId, String transactionDescription, ModelMap modelMap, HttpSession session) throws IOException {

		logger.debug("Have selected " + priority + " match for " + customerId + " transactionDescription : " + transactionDescription);

		Customer customer = customerService.get(customerId);

		CreditNoteHolder holder = (CreditNoteHolder) session.getAttribute("creditNoteHolder");

		CreditNote cn = holder.getCreditNote( transactionDescription );

		cn.setCustomer(customer);

		if (priority == 1) {
			cn.setStatus("Potential Primary Match");
		} else if (priority == 2) {
			cn.setStatus("Potential Secondary Match");
		}

		addSuccess("Secondary Matched " + customer.getFullName() + " to " + transactionDescription, modelMap);

		populateCreditNoteModel(holder, modelMap);

		return "confirmUploadAccounts";
	}

	@RequestMapping(value = "/match", method = RequestMethod.GET)
	public String match(Long customerId, String transactionDescription, ModelMap modelMap, HttpSession session) throws IOException {

		logger.debug("Finding match for " + customerId + " transactionDescription : " + transactionDescription);

		CreditNoteHolder holder = (CreditNoteHolder) session.getAttribute("creditNoteHolder");

		Map<String, CreditNote> creditNoteMap = holder.getCreditNoteMap();

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

		populateCreditNoteModel(holder, modelMap);

		return "confirmUploadAccounts";
	}

	@RequestMapping(value = "/uploadAccountsFromTSB", method = RequestMethod.GET)
	public String uploadAccountsFromTSB(ModelMap modelMap) throws IOException {
		modelMap.addAttribute("creditNote", new CreditNote());
		return "uploadAccountsFromTSB";
	}


	private void populateCreditNoteModel(CreditNoteHolder holder, ModelMap modelMap) {

		Map<String, CreditNote> creditNoteMap = holder.getCreditNoteMap();

		modelMap.addAttribute("creditNoteList", creditNoteMap.values().stream().sorted((p1, p2) -> p2.getStatus().compareTo(p1.getStatus())).collect(Collectors.toList()));

		modelMap.addAttribute("noOfLines", holder.getNoOfLines());
		modelMap.addAttribute("noOfCreditNotes", creditNoteMap.values().size());
		modelMap.addAttribute("noOfUnmatched", holder.getNoUnmatched());
		modelMap.addAttribute("noOfMatched", holder.getNoMatched());
		modelMap.addAttribute("noOfClubAccountsUnprocessed", holder.getNoOfClubAccountsUnprocessed());
		modelMap.addAttribute("noOfAlreadyProcessed", holder.getNoOfAlreadyProcessed());

	}

	@Override
	public Service<Customer> getService() {
		return customerService;
	}
}

class CreditNoteHolder {

	private Map<String, CreditNote> creditNoteMap = new HashMap<>();

	private Integer noOfLines = 0;

	public Map<String, CreditNote> getCreditNoteMap() {
		return creditNoteMap;
	}

	public Object getNoOfAlreadyProcessed() {
		return creditNoteMap.values().stream().filter(cn -> cn.getStatus().equals("Already Processed")).count();
	}

	public Long getNoUnmatched() {
		return creditNoteMap.values().stream().filter(cn -> cn.getStatus().equals("Unmatched")).count();
	}

	public int getNoMatched() {
		return getMatched().size();
	}

	public void incrementNoOfLines() {
		noOfLines++;
	}

	public Integer getNoOfLines() {
		return noOfLines;
	}

	public double getTotalAmountInPounds() {
		return creditNoteMap.values().stream().mapToDouble(i -> i.getAmount().doubleValue()).sum();
	}

	public List<CreditNote> getMatched() {
		//Must match Secondary Match and Primary March
		return creditNoteMap.values().stream().filter(cn -> cn.getStatus().equals("Primary Matched") || cn.getStatus().equals("Secondary Matched")).collect(Collectors.toList());
	}


	public List<CreditNote> getAlreadyProcessed() {
		return creditNoteMap.values().stream().filter(cn -> cn.getStatus().equals("Already Processed")).collect(Collectors.toList());
	}



	public List<CreditNote> getClubAccounts() {
		return creditNoteMap.values().stream().filter(cn -> cn.isClubAccount()).collect(Collectors.toList());
	}

	public int getNoOfClubAccounts() {
		return getClubAccounts().size();
	}

	public int getNoOfClubAccountsUnprocessed() {
		return getClubAccounts().stream().filter(cn -> !cn.getStatus().equals("Already Processed")).collect(Collectors.toList()).size();
	}

	public CreditNote getCreditNote(String transactionDescription) {
			return creditNoteMap.get(transactionDescription);
	}


}
