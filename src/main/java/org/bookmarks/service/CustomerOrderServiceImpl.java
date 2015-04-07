package org.bookmarks.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.bookmarks.domain.BookmarksAccount;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.CustomerType;
import org.bookmarks.domain.Source;
import org.bookmarks.website.domain.CreditCard;
import org.bookmarks.website.domain.DeliveryType;
import org.bookmarks.website.domain.OrderLine;
import org.bookmarks.website.domain.PaymentType;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.repository.CustomerOrderLineRepository;
import org.bookmarks.repository.CustomerRepository;
import org.jasypt.util.text.StrongTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerOrderServiceImpl implements CustomerOrderService {

	@Autowired
	private StockItemService stockItemService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomerOrderLineService customerOrderLineService;

	@Autowired
	private CustomerRepository customerRepository;

	@Value("#{ applicationProperties['thingy'] }")
	private String passwd;

	@Override
	public CustomerOrder selectCustomer(Long id) {
		//First get customer
		Customer customer = (Customer) customerService.get(id);

		//Create customer order
		CustomerOrder customerOrder = new CustomerOrder();
		customerOrder.setCustomer(customer);

		return customerOrder;
	}


	@Override
	public CustomerOrder selectStockItemForCustomerOrder(Long id) {
		//First get stockItem
		StockItem stockItem = (StockItem) stockItemService.get(id);

		//Create customer order
		CustomerOrder customerOrder = new CustomerOrder();
//		customerOrder.addStockItem(stockItem);

		return customerOrder;
	}

	@Override
	public void addStockItem(CustomerOrder customerOrder, Long id) {
		//First get stockItem
		StockItem stockItem = (StockItem) stockItemService.get(id);

		customerOrder.addStockItem(stockItem);
	}

	/**
	 * For beans, as opposed to chips
	 */
	@Override
	public void save(CustomerOrder customerOrder, Collection<CustomerOrderLine> customerOrderLines) {
		customerOrder.setCustomerOrderline(customerOrderLines);
		CustomerOrderLine split = null; //If partial fill

		//If this order has credit card details, copy to customer but not securityCode!
//		if(customerOrder.getPaymentType() == PaymentType.CREDIT_CARD) {
//			customerService.updateCreditCard(customerOrder.getCreditCard(), customerOrder.getCustomer());
//		}

		if(customerOrderLines.isEmpty()) {
			//Research note
			saveResearchNote(customerOrder);
			return;
		}

		//Loop through customer order lines persisting
		for(CustomerOrderLine customerOrderLine : customerOrderLines){

			//Transfer cc details, deliveryType etc, set haveBeenPrinted
			setDetails(customerOrder, customerOrderLine);

			if(customerOrderLines.size() > 1) {
				customerOrderLine.setIsMultipleOrder(true);
			} else {
				customerOrderLine.setIsMultipleOrder(false);
			}

			//Check if it is a research customer order
			if(customerOrderLine.getIsResearch() == Boolean.TRUE) {
				customerOrderLine.setStatus(CustomerOrderLineStatus.RESEARCH);
				customerOrderLineService.save(customerOrderLine);
				continue;
			}

			//Not research
			long quantityInStock = customerOrderLine.getStockItem().getQuantityInStock();
			if(quantityInStock > 0){  //In stock, is fully filled?
				if(quantityInStock >= customerOrderLine.getAmount()) {
					customerOrderLine.setStatus(CustomerOrderLineStatus.IN_STOCK);
				} else {
					//Split into two, one fully filled (in stock) one out of stock
					split = customerOrderLine.clone();
					split.setAmount(customerOrderLine.getAmount() - quantityInStock);
					split.setStatus(CustomerOrderLineStatus.OUT_OF_STOCK);
					customerOrderLine.setAmount(quantityInStock);
					customerOrderLine.setStatus(CustomerOrderLineStatus.IN_STOCK);
					//SupplierOrderLine supplierOrderLine = new SupplierOrderLine(split);

					//TEMP, rewrite later
					//StockItem si = stockItemService.get(customerOrderLine.getStockItem());
					//supplierOrderLine.setSupplier(si.getPublisher().getSupplier());

					customerOrderLineService.save(split);
				}
				//TO-DO Check keep in stock, check if partial fill
			} else {
				//Out of stock
				customerOrderLine.setStatus(CustomerOrderLineStatus.OUT_OF_STOCK);
				//TO-DO Create order automatically
			}

			//Create corresponding supplier order line

			//SupplierOrderLine supplierOrderLine = new SupplierOrderLine(customerOrderLine);
			//TEMP, rewrite later
			//StockItem si = stockItemService.get(customerOrderLine.getStockItem());
			//supplierOrderLine.setSupplier(si.getPublisher().getSupplier());

			//Persist, which also persists supplierorderline
			customerOrderLineService.save(customerOrderLine);

			//Deal with stock item
			StockItem stockItem = customerOrderLine.getStockItem();
			Long quantityForCustomerOrder = (split == null ? customerOrderLine.getAmount() : customerOrderLine.getAmount() + split.getAmount());
			/*Long quantityForCustomerOrder = 0l;
			if(split == null) {
				quantityForCustomerOrder = customerOrderLine.getAmount();
				stockItem.setQuantityForCustomerOrder(customerOrderLine.getAmount() + stockItem.getQuantityForCustomerOrder());
			} else {
				quantityForCustomerOrder = customerOrderLine.getAmount() + split.getAmount();
			}*/
			//stockItem.setQuantityForCustomerOrder(quantityForCustomerOrder +  stockItem.getQuantityForCustomerOrder());
			stockItemService.updateQuantities(stockItem, null, null, null, quantityForCustomerOrder, null);
		}//end of customerorderline for loop

		//Email customer if they have an email address

	}


	private void setDetails(CustomerOrder customerOrder, CustomerOrderLine customerOrderLine) {
		customerOrderLine.setCustomer(customerOrder.getCustomer());

		if(customerOrder.getSource() != Source.WEB) { //Transfer over address from customer
			customerOrderLine.setAddress(customerOrder.getCustomer().getAddress());
			customerOrderLine.setSellPrice(customerOrderLine.getStockItem().getSellPrice());
		}

		//Temp, as setting to web manually should not happen after new release
		if(customerOrder.getSource() == Source.WEB && customerOrderLine.getSellPrice() == null) { //Transfer over address from customer
			customerOrderLine.setSellPrice(customerOrderLine.getStockItem().getSellPrice());
		}

		if(customerOrderLine.getDeliveryType() == DeliveryType.MAIL) {
			customerOrderLine.setHavePrintedLabel(false);
		} else {
			customerOrderLine.setHavePrintedLabel(true);
		}

		customerOrderLine.setCreditCard(customerOrder.getCreditCard());
		customerOrderLine.setPaymentType(customerOrder.getPaymentType());
		customerOrderLine.setDeliveryType(customerOrder.getDeliveryType());
		customerOrderLine.setNote(customerOrder.getNote());
		customerOrderLine.setSource(customerOrder.getSource());

	}


	private void saveResearchNote(CustomerOrder customerOrder) {
		CustomerOrderLine col = new CustomerOrderLine();
		col.setStockItem(stockItemService.getResearchStockItem());
		setDetails(customerOrder, col);
		col.setStatus(CustomerOrderLineStatus.RESEARCH);
		customerOrderLineService.save(col);
	}


	@Override
	public void saveChipsOrders(List<org.bookmarks.website.domain.Customer> chipsCustomers) {
		//Check if customers exist, using email
				for(org.bookmarks.website.domain.Customer chipsCustomer : chipsCustomers) { //Cycle through chips customer
					org.bookmarks.domain.Customer beansCustomer = customerRepository.getByEmail(chipsCustomer.getContactDetails().getEmail());

					//The customer order mechanism used by beans
					CustomerOrder customerOrder = new CustomerOrder();

					if(beansCustomer == null) {
						beansCustomer = new org.bookmarks.domain.Customer();
						//Set is from web
						beansCustomer.setCustomerType(CustomerType.WEB);
						beansCustomer.setFirstName(chipsCustomer.getFirstName());
						beansCustomer.setLastName(chipsCustomer.getLastName());
						beansCustomer.setAddress(chipsCustomer.getAddress());
						BookmarksAccount account = new BookmarksAccount();
						beansCustomer.setBookmarksAccount(account);
					}

					beansCustomer.setContactDetails(chipsCustomer.getContactDetails());
					beansCustomer.setWebAddress(chipsCustomer.getAddress());

					//TODO What to do about name? Maybe need webname
					//TODO need webContactDetails


					customerRepository.saveOrUpdate(beansCustomer);

					//Decrypt firstname and lastname
					//Rest of fields are encrypted on customerOrderLine.edit()
					StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
					textEncryptor.setPassword(passwd);

					String decyptedLastname = textEncryptor.decrypt(chipsCustomer.getLastName());
					String decyptedFirstname = textEncryptor.decrypt(chipsCustomer.getFirstName());

					chipsCustomer.setFirstName(decyptedFirstname);
					chipsCustomer.setLastName(decyptedLastname);

					Address address = customerOrderLine.getAddress();

					if(address != null) {
						if(address.getAddress1() != null) {
							String temp = textEncryptor.decrypt(address.getAddress1());
							address.setAddress1(temp);
						}
						if(address.getAddress2() != null) {
							String temp = textEncryptor.decrypt(address.getAddress2());
							address.setAddress2(temp);
						}
						if(address.getAddress3() != null) {
							String temp = textEncryptor.decrypt(address.getAddress3());
							address.setAddress3(temp);
						}
						if(address.getCountry() != null) {
							String temp = textEncryptor.decrypt(address.getCountry());
							address.setCountry(temp);
						}
						if(address.getCity() != null) {
							String temp = textEncryptor.decrypt(address.getCity());
							address.setCity(temp);
						}
					}

					CreditCard creditCard = customerOrderLine.getCreditCard();

					if(creditCard != null) {
						if(creditCard.getCreditCard1() != null) {
							String temp = textEncryptor.decrypt(creditCard.getCreditCard1());
							creditCard.setCreditCard1(temp);
						}
						if(creditCard.getCreditCard2() != null) {
							String temp = textEncryptor.decrypt(creditCard.getCreditCard2());
							creditCard.setCreditCard2(temp);
						}
						if(creditCard.getCreditCard3() != null) {
							String temp = textEncryptor.decrypt(creditCard.getCreditCard3());
							creditCard.setCreditCard3(temp);
						}
						if(creditCard.getSecurityCode() != null) {
							String temp = textEncryptor.decrypt(creditCard.getSecurityCode());
							creditCard.setSecurityCode(temp);
						}
						if(creditCard.getExpiryMonth() != null) {
							String temp = textEncryptor.decrypt(creditCard.getExpiryMonth());
							creditCard.setExpiryMonth(temp);
						}
						if(creditCard.getExpiryYear() != null) {
							String temp = textEncryptor.decrypt(creditCard.getExpiryYear());
							creditCard.setExpiryYear(temp);
						}
					}

					//Build up Beans CustomerOrderLines
					beansCustomer.setCustomerOrderLines(new HashSet<CustomerOrderLine>());

					for(OrderLine chipsOl : chipsCustomer.getOrders()) {
						CustomerOrderLine beansOl = new CustomerOrderLine();
						beansOl.setIsEncrypted(true);

						beansOl.setAmount(new Long(chipsOl.getQuantity()));
						beansOl.setSellPrice(chipsOl.getSellPrice());
						beansOl.setPostage(chipsOl.getPostage());
						beansOl.setWebReference(chipsOl.getWebReference());

						if(chipsCustomer.getOrders().size() > 1) {
							beansOl.setIsMultipleOrder(true);
						} else {
							beansOl.setIsMultipleOrder(false);
						}

						StockItem stockItem = stockItemService.get(chipsOl.getStockItem().getId());

						beansOl.setStockItem(stockItem);
						beansOl.setAddress(chipsCustomer.getAddress());

						beansCustomer.getCustomerOrderLines().add(beansOl);

					}

					customerOrder.setPaymentType(chipsCustomer.getPaymentType()); //TODO sort this mess out, come up with a plan where the transfer is seemless
					customerOrder.setDeliveryType(chipsCustomer.getDeliveryType()); //TODO sort this mess out, come up with a plan where the transfer is seemless
					customerOrder.setCreditCard(chipsCustomer.getCreditCard());
					customerOrder.setCustomer(beansCustomer);
					customerOrder.setSource(Source.WEB);

					save(customerOrder, beansCustomer.getCustomerOrderLines());
				}//end for

	}



//	@Override
//	public Collection<CustomerOrderLine> getCustomerOrderLines(Long customerId) {
//		Collection<CustomerOrderLine> customerOrderLines = customerOrderLineRepository.getCustomerOrderLines(customerId);
//		return customerOrderLines;
//	}
}
