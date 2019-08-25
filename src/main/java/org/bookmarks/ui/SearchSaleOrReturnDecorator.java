package org.bookmarks.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.bookmarks.website.domain.Address;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.SaleOrReturn;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.TelephoneDirectory;
import org.displaytag.decorator.TableDecorator;

public class SearchSaleOrReturnDecorator extends AbstractBookmarksTableDecorator {
	
	public String getLink()	{
        SaleOrReturn saleOrReturn = (SaleOrReturn)getCurrentRowObject();
//        return getImageAnchor("delete?id=" + saleOrReturn.getId(),
//				"green-write.gif",
//				"Delete", false)
		return getImageAnchor("view?id=" + saleOrReturn.getId() + "&flow=searchSaleOrReturn",
						"view.png",
						"View", false)
				+ getImageAnchor("markAsWithCustomer?saleOrReturnId=" + saleOrReturn.getId(),
	        				"blue-user.png",
	        				"Mark As With Customer", false)
	        				+ getImageAnchor("markAsReturned?saleOrReturnId=" + saleOrReturn.getId(),
	        						"blue-thumb up.png",
	        						"Mark As Returned", false)
//				+ getImageAnchor("return?saleOrReturnId=" + saleOrReturn.getId(),
//	        				"blue-thumb up.png",
//	        				"Return", false)
	        	+ getImageAnchor("print?saleOrReturnId=" + saleOrReturn.getId(),
	        						"blue-printer.png",
	        						"Print", true)
				+ confirmation("/bookmarks/saleOrReturn/delete?id=" + saleOrReturn.getId(), "delete_blue.png")
	        	+ showEditNote(saleOrReturn);
	}

	public String getId() {
		SaleOrReturn saleOrReturn = (SaleOrReturn)getCurrentRowObject();
		return saleOrReturn.getId().toString();
	}	

	public String getCustomerName() {
		SaleOrReturn saleOrReturn = (SaleOrReturn)getCurrentRowObject();
		Customer customer = saleOrReturn.getCustomer();
		return customer.getFirstName() + " " + customer.getLastName();
	}
	
	public String getTotalPrice() {
		SaleOrReturn saleOrReturn = (SaleOrReturn)getCurrentRowObject();
		return CurrencyStyleFormatter.print(saleOrReturn.getTotalPrice(), Locale.UK);
	}

	public String getReturnDate() {
		SaleOrReturn saleOrReturn = (SaleOrReturn)getCurrentRowObject();
		return dateFormatter.print(saleOrReturn.getReturnDate(), Locale.UK);
	}


	public String getAddress() {
		SaleOrReturn saleOrReturn = (SaleOrReturn)getCurrentRowObject();
		Address address = saleOrReturn.getCustomer().getAddress();
		StringBuffer addressBuffer = new StringBuffer(100);
		addressBuffer.append(address.getAddress1() + ",");
		addressBuffer.append(address.getAddress2() == null ? "" : address.getAddress2() + ", ");
		addressBuffer.append(address.getCity() == null ? "" : address.getCity() + ", ");
		addressBuffer.append(address.getPostcode());

		return addressBuffer.toString();
	}

	public String getTelephoneNumber() {
		SaleOrReturn saleOrReturn = (SaleOrReturn)getCurrentRowObject();
		return getTelephoneNumber(saleOrReturn.getCustomer().getContactDetails());
	}
}
