package org.bookmarks.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.CascadeType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import java.math.BigDecimal;

import org.bookmarks.exceptions.BookmarksException;
import org.bookmarks.website.domain.Address;
import org.bookmarks.website.domain.CreditCard;
import org.bookmarks.website.domain.DeliveryType;
import org.bookmarks.website.domain.PaymentType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

@Entity
@Table(name="customerorderline")
public class CustomerOrderLine extends OrderLine {

	//From web orders, shared by containing website.CustomerOrder
    private String webReference;
    
	@ManyToMany(fetch = FetchType.EAGER)
	private Collection<SupplierDeliveryLine> supplierDeliveryLines;
	
	@Enumerated(EnumType.STRING)
	@NotNull
	private Source source;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name="paymentType")
	private PaymentType paymentType;
	
	private Boolean isSecondHand = false;
	
	private Boolean isEncrypted = false;
	
	public Boolean getIsEncrypted() {
		return isEncrypted;
	}
	public void setIsEncrypted(Boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}
	private Boolean havePrintedLabel = false;

	public Boolean getHavePrintedLabel() {
		return havePrintedLabel;
	}
	public void setHavePrintedLabel(Boolean havePrintedLabel) {
		this.havePrintedLabel = havePrintedLabel;
	}
	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name="deliveryType")
	private DeliveryType deliveryType;
	
	@Embedded
	private Address address;
	
	private Boolean isMultipleOrder;
	
	@Min(value=0)
	@NotNull
	@NumberFormat(pattern="#.##")
	@Column(name="sell_price")
	private BigDecimal sellPrice;
	
	@Min(value=0)
	@NumberFormat(pattern="#.##")
	private BigDecimal postage;

	@OneToOne(cascade = CascadeType.ALL)
	private Sale sale;

	@Embedded
	private CreditCard creditCard;

	@ManyToOne
	@NotNull
	@JoinColumn(name="customer_id")
	private Customer customer;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name="customerOrderStatus")
	private CustomerOrderLineStatus status;

	@Transient
	private Boolean isResearch = false;

	@NotNull
	@Column(name="isPaid")
	private Boolean isPaid = false;

	@ManyToOne
	@JoinColumn(name="invoice_id")
	private Invoice invoice;

	@Transient
	private Long amountFilled = 0l;

	@Transient
	private Long newAmount;

	@OneToOne(cascade=CascadeType.ALL)
	private SupplierOrderLine supplierOrderLine;

	//Dates
	@DateTimeFormat(pattern="dd-MM-yy")
	@Column(name="completionDate")
	private Date completionDate;

	@DateTimeFormat(pattern="dd-MM-yy")
	@Column(name="onOrderDate")
	private Date onOrderDate;

	@DateTimeFormat(pattern="dd-MM-yy")
	@Column(name="receivedIntoStockDate")
	private Date receivedIntoStockDate;
	
	public CustomerOrderLine() {
		super();
		setCustomer(new Customer());
	}
//From search
	public CustomerOrderLine(Long id,
			Boolean havePrintedLabel,
			Boolean isSecondHand,
			Date creationDate,
			Long amount,
			String note,
			Long customerId,
			String firstName,
			String lastName,
			String mobile,
			String work,
			String home,
      String email,
			CustomerOrderLineStatus customerOrderStatus,
			DeliveryType deliveryType,
			PaymentType paymentType,
			Source source,
			Long invoiceId,
			Long stockItemId,
			BigDecimal sellPrice,
			String isbn,
			String title,
			Long quantityInStock,
			String imageURL,
			String supplierName,
			String prefSupplierName,
			Long supplierId) {

		Customer customer = new Customer(firstName, lastName, mobile, work, home, email);
		customer.setId(customerId);
		Supplier supplier = new Supplier(supplierId);
		if(prefSupplierName != null) supplierName = prefSupplierName;
		supplier.setName(supplierName);
//		Publisher publisher = new Publisher(supplier);

		setIsSecondHand(isSecondHand);
		setHavePrintedLabel(havePrintedLabel);
		setCustomer(customer);
		setStatus(customerOrderStatus);
		setDeliveryType(deliveryType);
		setPaymentType(paymentType);
		setSource(source);

		StockItem stockItem = new StockItem(stockItemId, isbn, title, quantityInStock, null);
		setSellPrice(sellPrice);
		stockItem.setImageURL(imageURL);
		stockItem.setPreferredSupplier(supplier);
		setStockItem(stockItem);
		
		Invoice invoice = new Invoice();
		invoice.setId(invoiceId);
		setInvoice(invoice);

		setAmount(amount);
		setId(id);
		setNote(note);
		setCreationDate(creationDate);
	}	

	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public Source getSource() {
		return source;
	}
	public void setSource(Source source) {
		this.source = source;
	}
	public Boolean getIsResearch() {
		return isResearch;
	}

	public void setIsResearch(Boolean isResearch) {
		this.isResearch = isResearch;
	}	

	public Date getCompletionDate() {
		return completionDate;
	}
	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;
	}

	public Date getOnOrderDate() {
		return onOrderDate;
	}
	public void setOnOrderDate(Date onOrderDate) {
		this.onOrderDate = onOrderDate;
	}

	public Date getReceivedIntoStockDate() {
		return receivedIntoStockDate;
	}
	public void setReceivedIntoStockDate(Date receivedIntoStockDate) {
		this.receivedIntoStockDate = receivedIntoStockDate;
	}

	public SupplierOrderLine getSupplierOrderLine() {
		return supplierOrderLine;
	}

	public void setSupplierOrderLine(SupplierOrderLine supplierOrderLine) {
		this.supplierOrderLine = supplierOrderLine;
	}

	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Sale getSale() {
		return sale;
	}
	public void setSale(Sale sale) {
		this.sale = sale;
	}	

	public boolean isComplete() {
		CustomerOrderLineStatus status = getStatus();
		if(status == CustomerOrderLineStatus.COMPLETE) return true;
		return false;
	}

	public Long getNewAmount() {
		return newAmount;
	}
	public void setNewAmount(Long newAmount) {
		this.newAmount = newAmount;
	}
	public Long getAmountFilled() {
		return amountFilled;
	}
	public void setAmountFilled(Long amountFilled) {
		this.amountFilled = amountFilled;
	}

	public Boolean getIsPaid() {
		return isPaid;
	}
	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}

	public Collection<SupplierDeliveryLine> getSupplierDeliveryLines() {
		return supplierDeliveryLines;
	}
	public void setSupplierDeliveryLines(
			Collection<SupplierDeliveryLine> supplierDeliveryLines) {
		this.supplierDeliveryLines = supplierDeliveryLines;
	}
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}



	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public DeliveryType getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(DeliveryType deliveryType) {
		this.deliveryType = deliveryType;
	}

	public CustomerOrderLine(StockItem stockItem) {
		this();
		setStockItem(stockItem);
	}

	//From searchCustomerOrderLines.jps CustoemrOrderLineContoller.sendToSupplier
	public CustomerOrderLine(Long customerOrderLineId, Long stockItemId, Long supplierId,
			Long amount) {
		setId(customerOrderLineId);
		StockItem stockItem = new StockItem(stockItemId);
		Supplier supplier = new Supplier(supplierId);
		stockItem.setPreferredSupplier(supplier);
		setStockItem(stockItem);
		setAmount(amount);
	}
	
	public CustomerOrderLine(Long customerOrderLineId) {
		this();
		setId(customerOrderLineId);
	}
	public CustomerOrderLineStatus getStatus() {
		return status;
	}

	public void setStatus(CustomerOrderLineStatus customerOrderStatus) {
		this.status = customerOrderStatus;
	}

	public void addSupplierDeliveryLine(SupplierDeliveryLine supplierDeliveryLines) {
		this.supplierDeliveryLines.add(supplierDeliveryLines);

	}
	public CustomerOrderLine clone() {
		CustomerOrderLine customerOrderLine = new CustomerOrderLine();
		customerOrderLine.setCustomer(this.getCustomer());
		customerOrderLine.setCreditCard(this.getCreditCard());
		customerOrderLine.setPaymentType(this.getPaymentType());
		customerOrderLine.setDeliveryType(this.getDeliveryType());
		customerOrderLine.setStockItem(this.getStockItem());
		customerOrderLine.setStatus(this.getStatus());
		customerOrderLine.setSource(this.getSource());
		customerOrderLine.setSellPrice(this.getSellPrice());
		customerOrderLine.setAddress(this.getAddress());
		customerOrderLine.setCreationDate(this.getCreationDate());
		customerOrderLine.setWebReference(this.getWebReference());

		return customerOrderLine;
	}

	public boolean getCanPost() {
		if(getDeliveryType() == DeliveryType.MAIL
				&& getStatus() == CustomerOrderLineStatus.READY_TO_POST)
//				&& getIsPaid() == true) {
				{
			return true;
		}
		return false;
	}

	public boolean getHasInvoice() {
		if(getInvoice() != null) return true; else return false;
	}

	public boolean getCanComplete() {
		if(getStatus() == CustomerOrderLineStatus.COMPLETE 
				|| getStatus() == CustomerOrderLineStatus.RESEARCH
				|| getStatus() == CustomerOrderLineStatus.OUT_OF_STOCK
				|| getStatus() == CustomerOrderLineStatus.IN_STOCK) {
			return false;
		}
		return true;
	}

	public boolean getCanCancel() {
		if(getStatus() == CustomerOrderLineStatus.COMPLETE) {
			return false;
		}
		return true;
	}

	public boolean canMarkAsPaid() {
		if(getIsPaid() == false) return true;
		return false;
	}

	public void setIsReady() {
		if(getDeliveryType() == DeliveryType.MAIL) setStatus(CustomerOrderLineStatus.READY_TO_POST);
		else setStatus(CustomerOrderLineStatus.INFORM_CUSTOMER_TO_COLLECT);
	}

	//This is replicated in Customerorderlinerepostiroy findOpenOrdersForStockItem
	public boolean canBeFilled() {
		CustomerOrderLineStatus[] statuses = getCustomerOrderStatusesThatCanBeFilled();
		for(CustomerOrderLineStatus status : statuses) {
			if(getStatus() == status) return true;
		}
		return false;
	}
<<<<<<< HEAD
	
=======

	public boolean canRaiseInvoice() {
		if(getStatus() == CustomerOrderLineStatus.COMPLETE) {
			return false;
		}
		return true;
	}

>>>>>>> 175de40... Added cancel and ability to raise non account invoices
	public boolean getCanBeFilled() {
		return canBeFilled();
	}	

	public CustomerOrderLineStatus[] getCustomerOrderStatusesThatCanBeFilled() {
		CustomerOrderLineStatus[] statuses = {CustomerOrderLineStatus.IN_STOCK,
				CustomerOrderLineStatus.OUT_OF_STOCK,
				CustomerOrderLineStatus.ON_ORDER,
				CustomerOrderLineStatus.PENDING_ON_ORDER};
		return statuses;
	}


	public boolean canCreateSupplierOrder() {
		if(getStatus() == CustomerOrderLineStatus.OUT_OF_STOCK) {
			return true;
		}
		return false;
	}
	public boolean canMarkAsCollected() {
		if(getStatus() == CustomerOrderLineStatus.AWAITING_COLLECTION
				|| getStatus() == CustomerOrderLineStatus.LEFT_PHONE_MESSAGE
				|| getStatus() == CustomerOrderLineStatus.EMAILED_CUSTOMER) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object that) {
		// Default to comparing ids
		OrderLine orderLine = (OrderLine) that;
		if(this.getStockItem() == null || this.getStockItem().getId() == null || orderLine.getStockItem() == null || orderLine.getStockItem().getId() == null) {
			return false;
		}
		if(this.getStockItem().getId().longValue() == orderLine.getStockItem().getId().longValue())
			return true;
		return false;
	}

	public void complete() {
		if(!getCanComplete()) throw new BookmarksException("Cannot complete order id " + getId());
		setStatus(CustomerOrderLineStatus.COMPLETE);
		setCompletionDate(new Date());
	}
	
	public Collection<CustomerOrderLineStatus> getBuyerStatuses() {
		Collection<CustomerOrderLineStatus> list = new ArrayList<CustomerOrderLineStatus>();
		list.add(CustomerOrderLineStatus.OUT_OF_STOCK);
		list.add(CustomerOrderLineStatus.RESEARCH);
		list.add(CustomerOrderLineStatus.OUT_OF_STOCK_CUSTOMER);
		list.add(CustomerOrderLineStatus.OUT_OF_STOCK_NOT_YET_PUBLISHED);
		return list;
	}
	
	public Collection<CustomerOrderLineStatus> getMailorderStatuses() {
		Collection<CustomerOrderLineStatus> list = new ArrayList<CustomerOrderLineStatus>();
		list.add(CustomerOrderLineStatus.READY_TO_POST);
		list.add(CustomerOrderLineStatus.IN_STOCK);
		return list;
	}
	public boolean hasSupplierOrder() {
		if(getStatus() != CustomerOrderLineStatus.IN_STOCK
				&& getStatus() != CustomerOrderLineStatus.OUT_OF_STOCK
				&& getStatus() != CustomerOrderLineStatus.CANCELLED
				&& getStatus() != CustomerOrderLineStatus.RESEARCH
				) {
			return true;
		}
		return false;
	}
	
	public String getFullAddressWithBreaks() {
		if(getDeliveryType() == DeliveryType.COLLECTION) {
			return "Collection";
		}
		StringBuffer buffer = new StringBuffer();
		Address address = getAddress();
		if(address == null) {
			address = getCustomer().getAddress();
		}
		//Should never happen
		if(address == null) {
			return "No address supplied";
		}		
		if(address.getAddress1() != null && !address.getAddress1().isEmpty()) {
			buffer.append(address.getAddress1());
		}
		if(address.getAddress2() != null && !address.getAddress2().isEmpty()) {
			buffer.append("<br/>" + address.getAddress2());
		}
		if(address.getAddress3() != null && !address.getAddress3().isEmpty()) {
			buffer.append("<br/>" + address.getAddress3());
		}
		if(address.getCity() != null && !address.getCity().isEmpty()) {
			buffer.append("<br/>" + address.getCity());
		}
		if(address.getCountry() != null && !address.getCountry().isEmpty()) {
			buffer.append("<br/>" + address.getCountry());
		}
		if(address.getPostcode() != null && !address.getPostcode().isEmpty()) {
			buffer.append("<br/>" + address.getPostcode());
		}
		if(buffer.length() == 0) return "No address supplied";
		return buffer.toString();
	}
	
	//METHODS
	public BigDecimal getTotalPrice() {
		return getSellPrice().multiply(new BigDecimal(getAmount()));
	}
	
	public BigDecimal getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}
	public BigDecimal getPostage() {
		return postage;
	}
	public void setPostage(BigDecimal postage) {
		this.postage = postage;
	}
	public Boolean getIsMultipleOrder() {
		return isMultipleOrder;
	}
	public void setIsMultipleOrder(Boolean isMultipleOrder) {
		this.isMultipleOrder = isMultipleOrder;
	}
	public String getWebReference() {
		return webReference;
	}
	public void setWebReference(String webReference) {
		this.webReference = webReference;
	}
	public Boolean getIsSecondHand() {
		return isSecondHand;
	}
	public void setIsSecondHand(Boolean isSecondHand) {
		this.isSecondHand = isSecondHand;
	}
}
