package org.bookmarks.ui;

import java.math.BigDecimal;
import java.util.Locale;

import org.bookmarks.website.domain.Address;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.Invoice;
import org.bookmarks.domain.TelephoneDirectory;
import org.bookmarks.website.domain.ContactDetails;

public class SearchCustomersDecorator extends AbstractBookmarksTableDecorator {
	public String getLink() {
	        Customer customer = (Customer)getCurrentRowObject();
	        long id = customer.getId();

	        return
	        		getImageAnchor("../customerOrder/init?customerId=" + id,
	        				"order.png",
	        				"Create Order", false)
	        		+ getImageAnchor("../customerOrderLine/searchByCustomerID?id=" + id,
	        				"allOrders.png",
	        				"View Orders", false)
    				+ getImageAnchor("../invoice/init?customerId=" + id,
    						"invoice.png",
    						"Create Invoice", false)
//					+ getImageAnchor("edit?id=" + id,
//							"write_medium.png",
//							"Edit", false)
//					+ getImageAnchor("view?id=" + id,
//							"view.png",
//							"View", false)
    				+ showEdit(customer)
					+ showView(customer)
					+ confirmation("delete?id=" + customer.getId(), "delete_medium.png")
					+ showSaleOrReturn(customer)
					+ showAddCredit(customer)
					+ showEditNote(customer);
//					+ getImageAnchor("displayEditNote?id=" + id,
//							"note.png",
//							"Edit Note", false)


	}

	protected String showAddCredit(Customer customer) {
		return getImageAnchor("/bookmarks/customer/addCredit?customerId=" + customer.getId(),
				"credit.png",
				"Add Credit", true);
	}

	protected String getContactDetails(Customer customer) {
		StringBuffer buffer = new StringBuffer();
		ContactDetails td = customer.getContactDetails();
		if(td != null) {
			if(td.getHomeNumber() != null && !td.getHomeNumber().isEmpty()) {
				buffer.append("Home : " + td.getHomeNumber());
			}
			if(td.getMobileNumber() != null && !td.getMobileNumber().isEmpty()) {
				buffer.append(" Mobile : " + td.getMobileNumber());
			}
			if(td.getWorkNumber() != null && !td.getWorkNumber().isEmpty()) {
				buffer.append(" Work : " + td.getWorkNumber());
			}
			if(customer.getContactDetails().getEmail() != null) {
				buffer.append(" Email : " + customer.getContactDetails().getEmail());
			}
		}

		if(buffer.length() == 0) {
			return "No contact Details";
		}

		return buffer.toString();
	}


	private String showSaleOrReturn(Customer customer) {
		return getImageAnchor("../saleOrReturn/init?id=" + customer.getId(),
				"saleOrReturn.png",
				"Sale or return", false);
	}


	public String getName() {
		Customer customer = (Customer)getCurrentRowObject();
		return getName(customer);
	}

	public String getName(Customer customer) {
		return customer.getFirstName() + " " + customer.getLastName();
	}

	protected String getCustomerName(Customer customer) {
		return getAnchor("edit?id=" + customer.getId(), getName(customer), "Edit", true, false);
	}

	public String getSponsor() {
		Customer customer = (Customer)getCurrentRowObject();
		boolean isSponsor = customer.getBookmarksAccount().getSponsor();
		if(isSponsor) {
			return getImage("blue-ok.png", "Yes");
		}
		return getImage("blue-nuke.png", "No");
	}

	public String getAccount() {
		Customer customer = (Customer)getCurrentRowObject();
		boolean isAccountHolder = customer.getBookmarksAccount().getAccountHolder();
		BigDecimal amountPaidInMonthly = customer.getBookmarksAccount().getAmountPaidInMonthly() == null ? new BigDecimal(0) : customer.getBookmarksAccount().getAmountPaidInMonthly();
		BigDecimal currentBalance = customer.getBookmarksAccount().getCurrentBalance() == null ? new BigDecimal(0) : customer.getBookmarksAccount().getCurrentBalance();
	String lastPaymentDate = customer.getBookmarksAccount().getLastPaymentDate() == null ? "Never" : shortDateFormatter.print(customer.getBookmarksAccount().getLastPaymentDate(), Locale.UK);
		if(isAccountHolder) {
			return CurrencyStyleFormatter.print(currentBalance, Locale.UK)
					+ "/" + CurrencyStyleFormatter.print(amountPaidInMonthly, Locale.UK)
					+ " " + lastPaymentDate ;
		}
		return getImage("blue-nuke.png", "No");
	}

	public String getAddress() {
		Customer customer = (Customer)getCurrentRowObject();
		return getFullAddress(customer);
	}

	protected String getFullAddress(Customer customer) {
		Address address = customer.getAddress();
		if(address == null || address.getAddress1() == null) return "No address supplied";
		if(address.getAddress1().isEmpty()
				&& address.getAddress2().isEmpty()
				&& address.getAddress3().isEmpty()
				&& address.getCity().isEmpty()
				&& address.getPostcode().isEmpty()) return "No address supplied";
		StringBuffer addressBuffer = new StringBuffer(100);
		addressBuffer.append(address.getAddress1() + ","); //Cannot be null so no check
		addressBuffer.append(address.getAddress2() == null || address.getAddress2().isEmpty() ? "" : address.getAddress2() + ", ");
		addressBuffer.append(address.getCity() == null || address.getCity().isEmpty() ? "" : address.getCity());
		addressBuffer.append(address.getPostcode() == null ? "" : ", " + address.getPostcode());

		return addressBuffer.toString();
	}

	protected String getEmail(Customer customer) {
		if(customer.getContactDetails().getEmail() == null || customer.getContactDetails().getEmail().isEmpty()) {
			return "No email";
		}
		return customer.getContactDetails().getEmail();
	}

	public String getEmail() {
		Customer customer = (Customer)getCurrentRowObject();
		return getEmail(customer);
	}

	protected String getId(Customer customer) {
		String id = customer.getId().toString();
		return getAnchor("/bookmarks/customer/edit?id=" + id + "&flow=searchCustomers", id, "Edit", false, false);
	}


	public String getId() {
		Customer customer = (Customer)getCurrentRowObject();
		return getId(customer);
	}


	public String getPhoneNumber() {
		Customer customer = (Customer)getCurrentRowObject();
		return getTelephoneNumber(customer);
	}

	protected String getTelephoneNumber(Customer customer) {
		ContactDetails telephoneDirectory = customer.getContactDetails();
		if(telephoneDirectory ==  null) return "No phone number";
		if(telephoneDirectory.getHomeNumber() ==  null
				&& telephoneDirectory.getMobileNumber() ==  null
				&& telephoneDirectory.getWorkNumber() ==  null ) return "No phone number";

		StringBuffer telephoneBuffer = new StringBuffer(100);

		if(telephoneDirectory.getMobileNumber() !=  null  && !telephoneDirectory.getMobileNumber().isEmpty()) {
			telephoneBuffer.append(telephoneDirectory.getMobileNumber());
		}
		if(telephoneDirectory.getHomeNumber() !=  null  && !telephoneDirectory.getHomeNumber().isEmpty()) {
			if(telephoneBuffer.length() != 0) telephoneBuffer.append(", ");
			telephoneBuffer.append(telephoneDirectory.getHomeNumber());
		}
		if(telephoneDirectory.getWorkNumber() !=  null  && !telephoneDirectory.getWorkNumber().isEmpty()) {
			if(telephoneBuffer.length() != 0) telephoneBuffer.append(", ");
			telephoneBuffer.append(telephoneDirectory.getWorkNumber());
		}

		if(telephoneBuffer.length() == 0) {
			telephoneBuffer.append("No phone number");
		}

		return telephoneBuffer.toString();
	}
}
