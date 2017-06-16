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
import org.bookmarks.domain.StockItem;
import org.bookmarks.repository.CustomerRepository;
import org.bookmarks.website.domain.Address;
import org.bookmarks.website.domain.ContactDetails;
import org.bookmarks.website.domain.CreditCard;
import org.bookmarks.website.domain.DeliveryType;
import org.bookmarks.website.domain.OrderLine;
import org.jasypt.util.text.StrongTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

<<<<<<< HEAD
	@Value("#{ applicationProperties['thingy'] }")
	private String passwd;
=======
	private final Logger logger = LoggerFactory.getLogger(CustomerOrderServiceImpl.class);
>>>>>>> a420287... Adding logging to customer order

	@Override
	public CustomerOrder selectCustomer(Long id) {
		// First get customer
		Customer customer = (Customer) customerService.get(id);

		// Create customer order
		CustomerOrder customerOrder = new CustomerOrder();
		customerOrder.setCustomer(customer);

		return customerOrder;
	}

	@Override
	public CustomerOrder selectStockItemForCustomerOrder(Long id) {
		// First get stockItem
		StockItem stockItem = (StockItem) stockItemService.get(id);

		// Create customer order
		CustomerOrder customerOrder = new CustomerOrder();
		// customerOrder.addStockItem(stockItem);

		return customerOrder;
	}

	@Override
	public void addStockItem(CustomerOrder customerOrder, Long id) {
		// First get stockItem
		StockItem stockItem = (StockItem) stockItemService.get(id);

		customerOrder.addStockItem(stockItem);
	}

	/**
	 * For beans, as opposed to chips
	 */
	@Override
	public void save(CustomerOrder customerOrder, Collection<CustomerOrderLine> customerOrderLines) {
		customerOrder.setCustomerOrderline(customerOrderLines);
		CustomerOrderLine split = null; // If partial fill

		// If this order has credit card details, copy to customer but not
		// securityCode!
		// if(customerOrder.getPaymentType() == PaymentType.CREDIT_CARD) {
		// customerService.updateCreditCard(customerOrder.getCreditCard(),
		// customerOrder.getCustomer());
		// }

		if (customerOrderLines.isEmpty()) {
			// Research note
			saveResearchNote(customerOrder);
			return;
		}

		// Loop through customer order lines persisting
		for (CustomerOrderLine customerOrderLine : customerOrderLines) {

			// Transfer cc details, deliveryType etc, set haveBeenPrinted
			setDetails(customerOrder, customerOrderLine);

			if (customerOrderLines.size() > 1) {
				customerOrderLine.setIsMultipleOrder(true);
			} else {
				customerOrderLine.setIsMultipleOrder(false);
			}

			// Check if it is a research customer order
			if (customerOrderLine.getIsResearch() == Boolean.TRUE) {
				customerOrderLine.setStatus(CustomerOrderLineStatus.RESEARCH);
				customerOrderLineService.save(customerOrderLine);
				continue;
			}

			// Not research
			long quantityInStock = customerOrderLine.getStockItem().getQuantityInStock();
			if (quantityInStock > 0) { // In stock, is fully filled?
				if (quantityInStock >= customerOrderLine.getAmount()) {
					customerOrderLine.setStatus(CustomerOrderLineStatus.IN_STOCK);
				} else {
					// Split into two, one fully filled (in stock) one out of
					// stock
					split = customerOrderLine.clone();
					split.setAmount(customerOrderLine.getAmount() - quantityInStock);
					split.setStatus(CustomerOrderLineStatus.OUT_OF_STOCK);
					customerOrderLine.setAmount(quantityInStock);
					customerOrderLine.setStatus(CustomerOrderLineStatus.IN_STOCK);
					// SupplierOrderLine supplierOrderLine = new
					// SupplierOrderLine(split);

					// TEMP, rewrite later
					// StockItem si =
					// stockItemService.get(customerOrderLine.getStockItem());
					// supplierOrderLine.setSupplier(si.getPublisher().getSupplier());

					customerOrderLineService.save(split);
				}
				// TO-DO Check keep in stock, check if partial fill
			} else {
				// Out of stock
				customerOrderLine.setStatus(CustomerOrderLineStatus.OUT_OF_STOCK);
				// TO-DO Create order automatically
			}

			// Create corresponding supplier order line

			// SupplierOrderLine supplierOrderLine = new
			// SupplierOrderLine(customerOrderLine);
			// TEMP, rewrite later
			// StockItem si =
			// stockItemService.get(customerOrderLine.getStockItem());
			// supplierOrderLine.setSupplier(si.getPublisher().getSupplier());

			// Persist, which also persists supplierorderline
			customerOrderLineService.save(customerOrderLine);

			// Deal with stock item
			StockItem stockItem = customerOrderLine.getStockItem();
			Long quantityForCustomerOrder = (split == null ? customerOrderLine.getAmount() : customerOrderLine.getAmount() + split.getAmount());
			/*
			 * Long quantityForCustomerOrder = 0l; if(split == null) {
			 * quantityForCustomerOrder = customerOrderLine.getAmount();
			 * stockItem
			 * .setQuantityForCustomerOrder(customerOrderLine.getAmount() +
			 * stockItem.getQuantityForCustomerOrder()); } else {
			 * quantityForCustomerOrder = customerOrderLine.getAmount() +
			 * split.getAmount(); }
			 */
			// stockItem.setQuantityForCustomerOrder(quantityForCustomerOrder +
			// stockItem.getQuantityForCustomerOrder());
			stockItemService.updateQuantities(stockItem, null, null, null, quantityForCustomerOrder, null);
		} // end of customerorderline for loop

		// Email customer if they have an email address

	}

	private void setDetails(CustomerOrder customerOrder, CustomerOrderLine customerOrderLine) {
		customerOrderLine.setCustomer(customerOrder.getCustomer());

		if (customerOrder.getSource() != Source.WEB) { // Transfer over address
														// from customer
			customerOrderLine.setAddress(customerOrder.getCustomer().getAddress());
			customerOrderLine.setSellPrice(customerOrderLine.getStockItem().getSellPrice());
		}

		// Temp, as setting to web manually should not happen after new release
		if (customerOrder.getSource() == Source.WEB && customerOrderLine.getSellPrice() == null) { // Transfer
																									// over
																									// address
																									// from
																									// customer
			customerOrderLine.setSellPrice(customerOrderLine.getStockItem().getSellPrice());
		}

		if (customerOrderLine.getDeliveryType() == DeliveryType.MAIL) {
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
<<<<<<< HEAD

	@Override
	public void saveChipsOrders(List<org.bookmarks.website.domain.Customer> chipsCustomers) {

		// Decrypt
		StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
		textEncryptor.setPassword(passwd);

		// Check if customers exist, using email
		for (org.bookmarks.website.domain.Customer chipsCustomer : chipsCustomers) { // Cycle
																						
			org.bookmarks.domain.Customer beansCustomer = customerRepository
					.getByEmail(chipsCustomer.getContactDetails().getEmail());

			// The customer order mechanism used by beans
			CustomerOrder customerOrder = new CustomerOrder();
			
			// Decrypt contact details
			ContactDetails descryptedContactDetails = chipsCustomer.getContactDetails();
			if (descryptedContactDetails != null) {
				if (descryptedContactDetails.getEmail() != null) {
					String temp = textEncryptor.decrypt(descryptedContactDetails.getEmail());
					descryptedContactDetails.setEmail(temp);
				}
				if (descryptedContactDetails.getHomeNumber() != null) {
					String temp = textEncryptor.decrypt(descryptedContactDetails.getHomeNumber());
					descryptedContactDetails.setHomeNumber(temp);
				}
				if (descryptedContactDetails.getWorkNumber() != null) {
					String temp = textEncryptor.decrypt(descryptedContactDetails.getWorkNumber());
					descryptedContactDetails.setWorkNumber(temp);
				}
				if (descryptedContactDetails.getMobileNumber() != null) {
					String temp = textEncryptor.decrypt(descryptedContactDetails.getMobileNumber());
					descryptedContactDetails.setMobileNumber(temp);
				}
			}

			//Decrypt creditcard
			CreditCard decryptedCreditCard = chipsCustomer.getCreditCard();

					if(decryptedCreditCard != null) {
						if(decryptedCreditCard.getCreditCard1() != null) {
							String temp = textEncryptor.decrypt(decryptedCreditCard.getCreditCard1());
							decryptedCreditCard.setCreditCard1(temp);
						}
						if(decryptedCreditCard.getCreditCard2() != null) {
							String temp = textEncryptor.decrypt(decryptedCreditCard.getCreditCard2());
							decryptedCreditCard.setCreditCard2(temp);
						}
						if(decryptedCreditCard.getCreditCard3() != null) {
							String temp = textEncryptor.decrypt(decryptedCreditCard.getCreditCard3());
							decryptedCreditCard.setCreditCard3(temp);
						}
						if(decryptedCreditCard.getSecurityCode() != null) {
							String temp = textEncryptor.decrypt(decryptedCreditCard.getSecurityCode());
							decryptedCreditCard.setSecurityCode(temp);
						}
						if(decryptedCreditCard.getExpiryMonth() != null) {
							String temp = textEncryptor.decrypt(decryptedCreditCard.getExpiryMonth());
							decryptedCreditCard.setExpiryMonth(temp);
						}
						if(decryptedCreditCard.getExpiryYear() != null) {
							String temp = textEncryptor.decrypt(decryptedCreditCard.getExpiryYear());
							decryptedCreditCard.setExpiryYear(temp);
						}
					}

			
			// Decrypt address
			Address decryptedAddress = chipsCustomer.getAddress();
			if (decryptedAddress != null) {
				if (decryptedAddress.getAddress1() != null) {
					String temp = textEncryptor.decrypt(decryptedAddress.getAddress1());
					decryptedAddress.setAddress1(temp);
				}
				if (decryptedAddress.getAddress2() != null) {
					String temp = textEncryptor.decrypt(decryptedAddress.getAddress2());
					decryptedAddress.setAddress2(temp);
				}
				if (decryptedAddress.getAddress3() != null) {
					String temp = textEncryptor.decrypt(decryptedAddress.getAddress3());
					decryptedAddress.setAddress3(temp);
				}
				if (decryptedAddress.getCountry() != null) {
					String temp = textEncryptor.decrypt(decryptedAddress.getCountry());
					decryptedAddress.setCountry(temp);
				}
				if (decryptedAddress.getCity() != null) {
					String temp = textEncryptor.decrypt(decryptedAddress.getCity());
					decryptedAddress.setCity(temp);
				}
			}			

			//Customer can't be matched by email, create new beans customer
			if (beansCustomer == null) {

				String decyptedLastname = textEncryptor.decrypt(chipsCustomer
						.getLastName());
				String decyptedFirstname = textEncryptor.decrypt(chipsCustomer
						.getFirstName());

				beansCustomer = new org.bookmarks.domain.Customer();
				// Set is from web
				beansCustomer.setCustomerType(CustomerType.WEB);
				beansCustomer.setFirstName(decyptedFirstname);
				beansCustomer.setLastName(decyptedLastname);
				beansCustomer.setAddress(decryptedAddress);
				
				BookmarksAccount account = new BookmarksAccount();
				beansCustomer.setBookmarksAccount(account);
			}

			beansCustomer.setContactDetails(descryptedContactDetails);

			beansCustomer.setWebAddress(decryptedAddress);

			// TODO What to do about name? Maybe need webname

			customerRepository.saveOrUpdate(beansCustomer);

			// Build up Beans CustomerOrderLines
			beansCustomer.setCustomerOrderLines(new HashSet<CustomerOrderLine>());

			for (OrderLine chipsOl : chipsCustomer.getOrders()) {
				CustomerOrderLine beansOl = new CustomerOrderLine();
				beansOl.setIsEncrypted(true);

				beansOl.setAmount(new Long(chipsOl.getQuantity()));
				beansOl.setSellPrice(chipsOl.getSellPrice());
				beansOl.setPostage(chipsOl.getPostage());
				beansOl.setWebReference(chipsOl.getWebReference());

				if (chipsCustomer.getOrders().size() > 1) {
					beansOl.setIsMultipleOrder(true);
				} else {
					beansOl.setIsMultipleOrder(false);
				}

				StockItem stockItem = stockItemService.get(chipsOl.getStockItem().getId());

				beansOl.setStockItem(stockItem);
				beansOl.setAddress(chipsCustomer.getAddress());

				beansCustomer.getCustomerOrderLines().add(beansOl);

				logger.info("******");
				logger.info("StockItem : " + stockItem.getTitle());
				logger.info("Quantity : " + beansOl.getAmount());
				logger.info("Web ref : " + beansOl.getWebReference());
				logger.info("******");

			}

			customerOrder.setPaymentType(chipsCustomer.getPaymentType()); 
																	
			customerOrder.setDeliveryType(chipsCustomer.getDeliveryType()); 
																	
			customerOrder.setCreditCard(decryptedCreditCard);
			
			customerOrder.setCustomer(beansCustomer);
			
			customerOrder.setSource(Source.WEB);

			save(customerOrder, beansCustomer.getCustomerOrderLines());
		}// end for

	}

	// @Override
	// public Collection<CustomerOrderLine> getCustomerOrderLines(Long
	// customerId) {
	// Collection<CustomerOrderLine> customerOrderLines =
	// customerOrderLineRepository.getCustomerOrderLines(customerId);
	// return customerOrderLines;
	// }
=======
>>>>>>> 7fc11cb... getOrders now working
}
