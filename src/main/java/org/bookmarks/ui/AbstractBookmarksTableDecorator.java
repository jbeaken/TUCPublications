package org.bookmarks.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.servlet.jsp.PageContext;

import org.bookmarks.domain.AbstractEntity;
import org.bookmarks.website.domain.Address;
import org.bookmarks.website.domain.ContactDetails;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierOrder;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.domain.TelephoneDirectory;
import org.displaytag.decorator.TableDecorator;
import org.displaytag.model.TableModel;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.number.CurrencyStyleFormatter;
import org.springframework.format.number.CurrencyStyleFormatter;
import org.springframework.format.number.NumberStyleFormatter;

public abstract class AbstractBookmarksTableDecorator extends TableDecorator {

//	protected DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
	protected DateFormatter dateFormatter = new DateFormatter("dd/MMM/yyyy");
	protected DateFormatter shortDateFormatter = new DateFormatter("dd/MM/yy");
	protected DateFormatter dateTimeFormatter = new DateFormatter("dd/MM/yy HH:mm");
	protected NumberStyleFormatter numberStyleFormatter = new NumberStyleFormatter("###.##");
	protected CurrencyStyleFormatter CurrencyStyleFormatter = new CurrencyStyleFormatter();
	protected String contextPath;

	public void init(PageContext pageContext, Object decorated, TableModel tableModel) {
         super.init(pageContext, decorated, tableModel);
         contextPath = getPageContext().getServletContext().getContextPath();
    }

	protected String getImage(String src, String title) {
		return "<img src=\"../resources/images/" + src + "\" title=\"" + title + "\" />";
	}

	public String getCreationDate() {
		AbstractEntity e = (AbstractEntity)getCurrentRowObject();
		return dateFormatter.print(e.getCreationDate(), Locale.UK);
	}

	protected String getStockItemTitleLink(StockItem stockItem, String flow) {
		return getAnchor(contextPath + "/stock/edit?id=" + stockItem.getId() +"&flow=" + flow,
				stockItem.getTitle() + "-" + stockItem.getMainAuthor(),"Title", true, false);
	}

	protected String getStockItemTitleLinkNoAuthor(StockItem stockItem, String flow) {
		return getAnchor("/bookmarks/stock/edit?id=" + stockItem.getId() +"&flow=" + flow,
				stockItem.getTitle(),"Title", true, false);
	}

	protected String getStockTitleLink(StockItem stockItem, String flow) {
		return getAnchor("/bookmarks/stock/edit?id=" + stockItem.getId() +"&flow=" + flow,
				stockItem.getTitle(),"Title", true, false);
	}

//	protected String getImageAnchor(String aUrl, String imgUrl, String title) {
//		return "<a href=\"" + aUrl + "\">" + getImage(imgUrl, title) + "</a>";
//	}

	protected String getImageAnchor(String aUrl, String imgUrl, String title, boolean openInParent) {
		return getImageAnchor(aUrl, imgUrl, title, openInParent, false);
	}

	protected String showSell(StockItem stockItem) {
		return getImageAnchor("../sale/sellByISBN?isbn=" + stockItem.getIsbn(), "pound_medium.png", "Sell", false);
	}

	protected String confirmation(String url, String imgUrl) {
		return "<img src='../resources/images/" + imgUrl + "' onclick='javascript:authoriseUser(\"" + url + "\")' />";
	}

	protected String getImageAnchor(String aUrl, String imgUrl, String title, boolean openInParent, boolean isDisabled) {
		StringBuffer buffer = null;
		if(isDisabled) {
			buffer = new StringBuffer();
		} else {
			buffer = new StringBuffer("<a href=\"" + aUrl + "\"");
			if(openInParent) {
				buffer.append(" target=\"_blank\"");
			}
			buffer.append(">");
		}
		buffer.append(getImage(imgUrl, title));
		if(!isDisabled) {
			buffer.append("</a>");
		}
		return buffer.toString();
	}

	protected String getAnchor(String aUrl, String text, String title, boolean openInParent, boolean isDisabled) {
		StringBuffer buffer = null;
		if(isDisabled) {
			buffer = new StringBuffer();
		} else {
			buffer = new StringBuffer("<a href=\"" + aUrl + "\"");
			if(openInParent) {
				buffer.append(" target=\"_blank\"");
			}
			buffer.append(">");
		}
		buffer.append(text);
		if(!isDisabled) {
			buffer.append("</a>");
		}
		return buffer.toString();
	}

	protected String getImage(String src) {
		return "<img src=\"../resources/images/" + src + "\" title=\"title\" />";
	}

	protected  String showEditNote(AbstractEntity e) {
		if(e.getNote() != null) return showEditNote(e, "note_available.jpg"); //Change colour for a live note
		return showEditNote(e, "note.png");
	}

	protected  String showEditNote(AbstractEntity e, String imageFileName) {
		long id = e.getId();
		String note = e.getNote();
		if(note == null || note.trim().isEmpty()) note = "Add note";
		return getImageAnchor("displayEditNote?id=" + id,
				imageFileName,
				note, true, false);
	}

	protected String showEdit(AbstractEntity e) {
		return showEdit(e, false);
	}


	protected String showEdit(AbstractEntity e,	boolean isDisabled) {
		return showEdit(e, isDisabled, "write_medium.png");
	}

	protected String showEdit(AbstractEntity e,	boolean isDisabled, String imageFileName) {
		return getImageAnchor("edit?id=" + e.getId() + "&flow=search",
				imageFileName,
				"Edit", false, isDisabled);
	}

	protected  String showDelete(AbstractEntity e) {
		return showDelete(e, false);
	}

	protected  String showDelete(AbstractEntity e, boolean isDisabled) {
		return getImageAnchor("delete?id=" + e.getId(),
				"delete_medium.png",
				"Delete", false, isDisabled);
	}

	protected  String showView(AbstractEntity e) {
		return 	getImageAnchor("view?id=" + e.getId() + "&flow=search",
				"view.png",
				"View", false, false);
	}

	protected String getAddress(Address address) {
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

	protected String getContactDetails(Customer customer) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getTelephoneNumber(customer.getContactDetails()));
		buffer.append("/");
		buffer.append(getEmail(customer.getContactDetails().getEmail()));
		return buffer.toString();
	}

	protected String getTelephoneNumber(ContactDetails contactDetails) {
		if(contactDetails ==  null) return "None";
		if(contactDetails.getHomeNumber() ==  null
				&& contactDetails.getMobileNumber() ==  null
				&& contactDetails.getWorkNumber() ==  null ) return "None";

		StringBuffer telephoneBuffer = new StringBuffer(100);

		if(contactDetails.getMobileNumber() !=  null  && !contactDetails.getMobileNumber().isEmpty()) {
			telephoneBuffer.append(contactDetails.getMobileNumber());
		}
		if(contactDetails.getHomeNumber() !=  null  && !contactDetails.getHomeNumber().isEmpty()) {
			if(telephoneBuffer.length() != 0) telephoneBuffer.append(", ");
			telephoneBuffer.append(contactDetails.getHomeNumber());
		}
		if(contactDetails.getWorkNumber() !=  null  && !contactDetails.getWorkNumber().isEmpty()) {
			if(telephoneBuffer.length() != 0) telephoneBuffer.append(", ");
			telephoneBuffer.append(contactDetails.getWorkNumber());
		}

		if(telephoneBuffer.length() == 0) {
			telephoneBuffer.append("None");
		}

		return telephoneBuffer.toString();
	}

	protected String getCustomerName(Customer customer) {
		return getAnchor("/bookmarks/customer/edit?id=" + customer.getId(), getName(customer), "Edit", true, false);
	}

	protected String getCustomerName(Customer customer, String flow) {
		return getAnchor("/bookmarks/customer/edit?id=" + customer.getId() + "&flow=" + flow, getName(customer), "Edit", true, false);
	}

	public String getName(Customer customer) {
		return customer.getFirstName() + " " + customer.getLastName();
	}

	protected String getEmail(String email) {
		if(email == null ||email.isEmpty()) {
			return "No email";
		}
		return email;
	}

	protected String getContactName(String contactName) {
		if(contactName == null || contactName.isEmpty()) {
			return "No contact name";
		}
		return contactName;
	}

	protected String getAddress(Customer customer) {
		return getAddress(customer.getAddress());
	}

	protected String getPhoneNumber(Customer customer) {
		ContactDetails telephoneDirectory = customer.getContactDetails();

		StringBuffer telephoneBuffer = new StringBuffer(100);
		telephoneBuffer.append(telephoneDirectory.getMobileNumber() + ", ");
		telephoneBuffer.append("H: " + (telephoneDirectory.getHomeNumber() == null ? "" : telephoneDirectory.getHomeNumber()) + ", ");
		telephoneBuffer.append("W: " + (telephoneDirectory.getWorkNumber() == null ? "" : telephoneDirectory.getWorkNumber()) + ", ");

		return telephoneBuffer.toString();
	}

	protected String getBinding() {
		StockItem stockItem = (StockItem)getCurrentRowObject();
		if(stockItem.getBinding() == null) return "N/A";
		switch(stockItem.getBinding()) {
		case HARDBACK:
			return "hb";
		case PAPERBACK:
			return "pb";
		}
		return "N/A";
	}
}
