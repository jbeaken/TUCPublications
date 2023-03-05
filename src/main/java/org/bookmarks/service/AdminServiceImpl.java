package org.bookmarks.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.bookmarks.domain.Customer;

//import au.com.bytecode.opencsv.CSVReader;


@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private CustomerService customerService;

	/**
	 * Load account details from CSV
	 * Check if credit notes exist, if not create, if so update
	 * Read in current balance and opening balance
	 *
	 *
"Customers: Kirkwood, Richard J",,,,,,,,
37607,,,,,,,,
01/09/2011,,,,Balance brought forward,,,,0
30/09/2011,JRN 000001,D,,"Opening balances - A/C Customers: Kirkwood, Richard J",,,328.5,-328.5
03/10/2011,REC 000029,,,Club - s/o,,,10,
15/10/2011,SIN 000067,,,40381,,35.08,,-303.42
01/11/2011,REC 000250,,,Club - s/o,,,10,-313.42
01/12/2011,REC 000339,,,Club - s/o,,,10,-323.42
03/01/2012,REC 000430,,,Club - s/o,,,10,-333.42
01/02/2012,REC 000587,,,Club - s/o,,,10,-343.42
01/03/2012,REC 000663,,,Club - s/o,,,10,-353.42
02/04/2012,REC 000771,,,Club - s/o,,,10,
30/04/2012,SIN 000940,,,April sales,,22.5,,-340.92
01/05/2012,REC 000880,,,Club - s/o,,,10,-350.92

30/06/2012,,,,Balance carried forward,,,,-350.92
	 */

	@Override
	public List<String> uploadAccounts() throws IOException {
		List<String> messages = new ArrayList<String>();
		List<Customer> customerWithAccount= new ArrayList<Customer>();

		//Populate list with customers and their account details
		List<String> lines = FileUtils.readLines(new File("c:\\accounts.csv"),"UTF-8");
		Integer index = 0;
		while(index < lines.size()) {
			String currentLine = lines.get(index);
			if(currentLine.indexOf("\"Customers:") != -1) {
				Customer customer = getCustomerAccountInfo(lines, index, messages);
				if(customer != null) {
					customerWithAccount.add(customer);
				}
			}
			index++;
		}


		//Update entities
		for(Customer customer : customerWithAccount) {
			//Get customer from database
			Customer dbCustomer = customerService.getMinimal(customer.getId());
			if(dbCustomer == null) {
				//Not found in databsae
				messages.add("Cannot find customer " + customer.getFullName() + " " + customer.getId() + " in database");
				continue;
			}
			if(dbCustomer.getBookmarksAccount().getAccountHolder() == null || dbCustomer.getBookmarksAccount().getAccountHolder() == false) {
				messages.add(customer.getFullName() +  " isn't account holder");
			}

			//Update
			if(!customer.getFirstName().equals(dbCustomer.getFirstName()) || !customer.getLastName().equals(dbCustomer.getLastName())) {
				messages.add("Names don't match: " + customer.getFullName() + " : " + dbCustomer.getFullName() + " ID:" + customer.getId());
			}
			customerService.updateBookmarksAccountInfo(customer);
		}
		return messages;
	}

	private Customer getCustomerAccountInfo(List<String> lines, Integer index, List<String> messages) {
		Customer customer = getCustomerName(lines, index);
		System.out.print(customer.getFullName());

		//Next line contains customer id
		index++;
		String customerIdLine = lines.get(index);
		String customerId = customerIdLine.substring(0, customerIdLine.indexOf(","));
		try {
			customer.setId(Long.parseLong(customerId));
		} catch(NumberFormatException e) {
			System.out.println("No id for " + customer.getFullName());
			messages.add("No id in VT for " + customer.getFullName());
			return null;
		}
		System.out.print(" " + customer.getId());

		//Next line is not useful (yet)
		index++;

		//Next line is opening balance
		index++;
		BigDecimal openingBalance = getOpeningBalance(customer.getFullName(), lines, index);
		if(openingBalance == null) {
			//Try next line, wierd
			index++;
			openingBalance = getOpeningBalance(customer.getFullName(), lines, index);
			if(openingBalance == null) {
				//Didn't work, reset
//				openingBalance = new BigDecimal(0);
				index--;
			}
		}
		openingBalance = openingBalance.multiply(new BigDecimal(-1));
		customer.getBookmarksAccount().setOpeningBalance(openingBalance);

		//Next lines are credits/debits, do later
		setCurrentBalance(customer, lines, index);

		return customer;
	}

	private void setCurrentBalance(Customer customer, List<String> lines,
			Integer index) {
		while(true) {
			String line = lines.get(index++);
			if(line.indexOf("Balance carried forward") != -1) {
				String currentBalanceStr = line.substring(41);
				if(currentBalanceStr.indexOf("\"") != -1) {
					currentBalanceStr = currentBalanceStr.substring(1, currentBalanceStr.length() -1); //shit, VT is shit!
				}
				currentBalanceStr = currentBalanceStr.replace(",", ""); //Stupid formating
				BigDecimal currentBalance = new BigDecimal(Float.parseFloat(currentBalanceStr));
				currentBalance = currentBalance.multiply(new BigDecimal(-1));
				customer.getBookmarksAccount().setCurrentBalance(currentBalance);
				System.out.println("Current Balance = " + customer.getBookmarksAccount().getCurrentBalance());
				break;
			}
		}
	}

	private Customer getCustomerName(List<String> lines, Integer index) {
		//Get name for sanity check
		Customer customer = new Customer();
		String nameLine = lines.get(index);
		int commaIndex = nameLine.indexOf(",");
		int endOfNameIndex = nameLine.indexOf(",,");
		String lastName = nameLine.substring(12, commaIndex);
		String firstName = nameLine.substring(commaIndex + 2, endOfNameIndex - 1);
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		return customer;
	}

	private BigDecimal getOpeningBalance(String fullName, List<String> lines, int index) {
		BigDecimal openingBalance = null;
		String openingBalanceLine = lines.get(index);

		try {
			int openingBalanceIndex = openingBalanceLine.indexOf("Opening balances") + 23;
			openingBalanceLine = openingBalanceLine.substring(openingBalanceIndex);
			openingBalanceIndex = openingBalanceLine.indexOf("\"");
			openingBalanceLine = openingBalanceLine.substring(openingBalanceIndex);
			int lastCommaIndex = openingBalanceLine.lastIndexOf(",");
			String openingBalanceString = openingBalanceLine.substring(lastCommaIndex + 1, openingBalanceLine.length());
			try {
				System.out.print(" openingBalanceString = " + openingBalanceString + " ");
				//Strange, some are enclosed by quotes.
				int quoteIndex = openingBalanceString.indexOf("\"");
				if(quoteIndex != -1) {
					openingBalanceString = openingBalanceString.substring(0, openingBalanceString.length() - 1);
				}
				openingBalance = new BigDecimal(Float.parseFloat(openingBalanceString));
			} catch(NumberFormatException e) {
//				e.printStackTrace();
				System.out.println("Cannot get opening balance for " + fullName);
			}
		} catch(StringIndexOutOfBoundsException e) {
			System.out.print(" No opening balance avaliable ");
			openingBalance = new BigDecimal(0);
		}
		System.out.println(" " + openingBalance);
		return openingBalance;
	}
}
