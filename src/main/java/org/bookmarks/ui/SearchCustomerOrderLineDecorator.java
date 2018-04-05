package org.bookmarks.ui;

import org.bookmarks.domain.Customer;
import org.bookmarks.website.domain.DeliveryType;
import org.bookmarks.website.domain.PaymentType;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.Source;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Invoice;
import org.bookmarks.domain.Supplier;
import org.springframework.format.datetime.DateFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.math.BigDecimal;

public class SearchCustomerOrderLineDecorator extends AbstractBookmarksTableDecorator {
	protected DateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yy");


	public String getLink()	{
        CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
        return showEdit(customerOrderLine)
        		+ showFilled(customerOrderLine)
//        		+ showMarkAsPosted(customerOrderLine)
//        		+ showMarkAsCollected(customerOrderLine)
        		+ completeAndGo(customerOrderLine)
				+ completeAndStay(customerOrderLine)
        		+ showEditNote(customerOrderLine)
        		+ showSupplierOrder(customerOrderLine)
        		//+ showChangeISBN(customerOrderLine)
				+ showCreateSupplierOrder(customerOrderLine);
	}

	private String showChangeISBN(CustomerOrderLine customerOrderLine) {
		if(!customerOrderLine.isComplete()){
		return getImageAnchor(contextPath  + "/customerOrderLine/changeISBN" +
				"?customerOrderLineId=" + customerOrderLine.getId(),
				"supplierOrder.png",
				"Change ISBN", true);
		}
		return "";
	}

	public String getInvoiceId() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		Invoice invoice = customerOrderLine.getInvoice();
		if(invoice.getId() == null) return "-";
		return getAnchor(contextPath  + "/invoice/view?id=" + invoice.getId() + "&flow=searchCustomerOrderLines", invoice.getId().toString(), "Edit", true, false);
	}

	private String showSupplierOrder(CustomerOrderLine customerOrderLine) {
		if(customerOrderLine.hasSupplierOrder()){
		return getImageAnchor(contextPath  + "/supplierOrderLine/showSupplierOrderForCustomerOrder" +
				"?customerOrderLineId=" + customerOrderLine.getId(),
				"supplierOrder.png",
				"Show Supplier Order ", true);
		}
		return "";
	}

	private String showFilled(CustomerOrderLine customerOrderLine) {
		if(customerOrderLine.canBeFilled()){
		return getImageAnchor(contextPath + "/customerOrderLine/fill?customerOrderLineId=" + customerOrderLine.getId(),
				"fill.png",
				"Fill", false);
		}
		return "";
	}

//	public String getIsbn() {
//		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
//		StockItem stockItem = customerOrderLine.getStockItem();
//		if(customerOrderLine.getCustomerOrderStatus() == CustomerOrderStatus.RESEARCH) {
//			return stockItem.getIsbn();
//		}
//		return getAnchor("/bookmarks/stock/edit?id=" + stockItem.getId(),
//				stockItem.getIsbn(),"ISBN", true, false);
//	}

	public String getTitle() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		StockItem stockItem = customerOrderLine.getStockItem();
		if(customerOrderLine.getStatus() == CustomerOrderLineStatus.RESEARCH) {
			return "RESEARCH";
		}
		return getStockItemTitleLink(stockItem, contextPath + "/customerOrderLine/searchCustomerOrders");

	}

	public String getSupplier() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		Supplier supplier = customerOrderLine.getStockItem().getPreferredSupplier();
		return getAnchor(contextPath + "/supplier/view?id=" + supplier.getId() +"&flow=searchCustomerOrderLines",
				supplier.getName(),"Supplier", true, false);
	}

	public String getType() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		DeliveryType dt = customerOrderLine.getDeliveryType();
		PaymentType pt = customerOrderLine.getPaymentType();
		Source sc = customerOrderLine.getSource();
		StringBuffer buffer = new StringBuffer(10);
		if(dt.equals(DeliveryType.MAIL)) {
			buffer.append("<span style='color:red'>MO</span>");
		} else if(dt.equals(DeliveryType.COLLECTION)) {
			buffer.append("<span style='color:blue'>COL</span>");
		} else if(dt.equals(DeliveryType.SPONSORSHIP)) {
			buffer.append("<span style='color:black'>SPO</span>");
		}
		buffer.append("-");
		if(pt.equals(PaymentType.ACCOUNT)) {
			buffer.append("<span style='color:blue'>ACC</span>");
		} else if(pt.equals(PaymentType.CASH)) {
			buffer.append("<span style='color:green'>CSH</span>");
		} else if(pt.equals(PaymentType.PAID)) {
			buffer.append("<span style='color:yellow'>PD</span>");
		} else if(pt.equals(PaymentType.CREDIT_CARD)) {
			buffer.append("CC");
		} else if(pt.equals(PaymentType.INSTITUTION)) {
			buffer.append("INT");
		} else if(pt.equals(PaymentType.CHEQUE)) {
			buffer.append("<span style='color:blue'>CHQ</span>");
		}
		if(sc == Source.WEB) {
			buffer.append("-<span style='color:blue'>WEB</span>");
		} else if(sc == Source.PHONE) {
			buffer.append("-<span style='color:red'>PHONE</span>");
		} else if(sc == Source.IN_PERSON) {
			buffer.append("-<span style='color:black'>PERSON</span>");
		}

		if(customerOrderLine.getIsSecondHand() == null || customerOrderLine.getIsSecondHand() == false) {
			buffer.append("-<span style='color:red'>NEW</span>");
		} else buffer.append("-<span style='color:black'>2nd</span>");

		return buffer.toString();

	}

	public String getRawType() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		DeliveryType dt = customerOrderLine.getDeliveryType();
		PaymentType pt = customerOrderLine.getPaymentType();
		StringBuffer buffer = new StringBuffer(10);
		if(dt.equals(DeliveryType.MAIL)) {
			buffer.append("MO");
		} else if(dt.equals(DeliveryType.COLLECTION)) {
			buffer.append("COL");
		}
		buffer.append("-");
		if(pt.equals(PaymentType.ACCOUNT)) {
			buffer.append("ACC");
		} else if(pt.equals(PaymentType.CASH)) {
			buffer.append("CSH");
		} else if(pt.equals(PaymentType.PAID)) {
			buffer.append("PD");
		} else if(pt.equals(PaymentType.CREDIT_CARD)) {
			buffer.append("CC");
		}
		return buffer.toString();

	}


	public String getRawId() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		StockItem stockItem = customerOrderLine.getStockItem();
		return stockItem.getId().toString();
	}


	public String getRawTitle() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		StockItem stockItem = customerOrderLine.getStockItem();
		return stockItem.getTitle();
	}

	public String getCreationDate() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		Date creationDate = customerOrderLine.getCreationDate();
		return shortDateFormatter.print(creationDate, Locale.UK);
	}


	public String getPrice() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		return CurrencyStyleFormatter.print(customerOrderLine.getSellPrice(), Locale.UK) ;
	}


	private String showCreateSupplierOrder(CustomerOrderLine customerOrderLine) {
		if(customerOrderLine.canCreateSupplierOrder()){
		return getImageAnchor(contextPath + "/supplierOrderLine/displayCreateForCustomerOrder" +
				"?customerOrderLineId=" + customerOrderLine.getId()
				+ "&stockItemId=" + customerOrderLine.getStockItem().getId()
				+ "&amount=" + customerOrderLine.getAmount()
				+ "&flow=searchCustomerOrder"
				+ "&supplierId=" + customerOrderLine.getStockItem().getPreferredSupplier().getId(),
				"supplier.png",
				"Create Supplier Order Line", true);
		}
		return "";
	}

	private String completeAndGo(CustomerOrderLine customerOrderLine) {
		if(customerOrderLine.getCanComplete()){
			String title = null;
			if(customerOrderLine.getPaymentType() == PaymentType.ACCOUNT) {
					title = "Raise Invoice & Complete & Go";
			} else {
					title = "Sell & Complete";
			}

			return getImageAnchor(contextPath + "/customerOrderLine/complete?customerOrderLineId=" + customerOrderLine.getId()
	//				+ "&customerId=" + customerOrderLine.getCustomer().getId()
	//				+ "&stockItemId=" + customerOrderLine.getStockItem().getId()
					+ "&flow=" + "searchCustomerOrderLinesGo",
					"pound_medium.png",
					title, false);
		}
		return "";
	}

	private String completeAndStay(CustomerOrderLine customerOrderLine) {
		if(customerOrderLine.getCanComplete() && customerOrderLine.getPaymentType() == PaymentType.ACCOUNT){
			return getImageAnchor(contextPath + "/customerOrderLine/complete?customerOrderLineId=" + customerOrderLine.getId()
	//				+ "&customerId=" + customerOrderLine.getCustomer().getId()
	//				+ "&stockItemId=" + customerOrderLine.getStockItem().getId()
					+ "&flow=" + "searchCustomerOrderLinesStay",
					"pound_medium.png",
					"Raise Invoice & Complete & Stay", false);
		}
		return "";
	}

	private String showMarkAsPosted(CustomerOrderLine customerOrderLine) {
		if(customerOrderLine.getCanPost()){
		return getImageAnchor(contextPath + "/customerOrderLine/markAsPosted?customerOrderLineId=" + customerOrderLine.getId(),
				"blue-travel.png",
				"Mark As Posted", false);
		}
		return "";
	}

	private String showMarkAsCollected(CustomerOrderLine customerOrderLine) {
		if(customerOrderLine.canMarkAsCollected()){
		return getImageAnchor(contextPath + "/customerOrderLine/markAsCollected?id=" + customerOrderLine.getId(),
				"blue-copyright.png",
				"Mark As Collected", false);
		}
		return "";
	}

	public String getCustomerName() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		Customer customer = customerOrderLine.getCustomer();
		return getCustomerName(customer, "customerOrderSearch");
	}

	public String getCustomer() {
		return getCustomerName() + " " + getTelephoneNumber();
	}

	public String getRawCustomerName() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		Customer customer = customerOrderLine.getCustomer();
		return customer.getFullName();
	}


	public String getAddress() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		return getAddress(customerOrderLine.getCustomer());
	}

	public String getTelephoneNumber() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		return getTelephoneNumber(customerOrderLine.getCustomer().getContactDetails());
	}

	public String getId() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		String id = customerOrderLine.getId().toString();
		return getAnchor(contextPath + "/customerOrderLine/edit?id=" + id + "&flow=searchCustomerOrderLines", id, "Edit", true, false);
	}
}
