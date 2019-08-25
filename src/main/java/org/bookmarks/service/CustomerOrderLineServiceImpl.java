package org.bookmarks.service;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.bookmarks.controller.SearchBean;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.website.domain.DeliveryType;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.StockItem;
import org.bookmarks.exceptions.BookmarksException;
import org.bookmarks.repository.CustomerOrderLineRepository;
import org.bookmarks.repository.Repository;
import org.bookmarks.scheduler.DailyReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerOrderLineServiceImpl extends
		AbstractService<CustomerOrderLine> implements CustomerOrderLineService {
	@Autowired
	private CustomerOrderLineRepository customerOrderLineRepository;

	@Autowired
	private StockItemService stockItemService;

	@Autowired
	private SaleService saleService;

	@Autowired
	private EmailService emailService;

	private Logger logger = LoggerFactory.getLogger(CustomerOrderLineService.class);
	
	@Override
	public Repository<CustomerOrderLine> getRepository() {
		return customerOrderLineRepository;
	}

	@Override
	@Transactional
	public void complete(CustomerOrderLineStatus status,
			Long customerOrderLineId) {
		CustomerOrderLine col = get(customerOrderLineId);
		col.setStatus(status);
		col.setCompletionDate(new Date());

		// Sells and reduces stockItem.quanitytReadyForCustomer by col.amount
		saleService.sellCustomerOrder(col);

		update(col);
	}

	@Override
	public Collection<CustomerOrderLine> search(SearchBean searchBean) {
		Collection<CustomerOrderLine> customerOrderLine = customerOrderLineRepository
				.search(searchBean);
		return customerOrderLine;
	}

	@Override
	public void updateToOrdered(Map<Long, Long> customerOrderLineMap) {
		for (Long id : customerOrderLineMap.keySet()) {
			customerOrderLineRepository.updateToOrdered(id,
					customerOrderLineMap.get(id));
		}
	}

	@Override
	public void save(Collection<CustomerOrderLine> customerOrderLines) {
		for (CustomerOrderLine col : customerOrderLines) {
			save(col);
		}
	}

	@Override
	public Collection<CustomerOrderLine> findOpenOrdersForStockItem(
			StockItem stockItem) {
		return customerOrderLineRepository
				.findOpenOrdersForStockItem(stockItem);
	}

	@Override
	public void fill(CustomerOrderLine customerOrderLine,
			SupplierDeliveryLine supplierDeliveryLine) {
		// Collection<SupplierDeliveryLine> supplierDeliveryLines =
		// customerOrderLine.getSupplierDeliveryLines();
		//
		// //Why not use contains?
		// //NOT WORKING!!
		// boolean alreadyContains = false;
		// for(SupplierDeliveryLine s : supplierDeliveryLines) {
		// if(s.getStockItem().getId().equals(supplierDeliveryLine.getStockItem().getId()))
		// {
		// alreadyContains = true;
		// }
		// }
		// if(!alreadyContains) {
		// //
		// customerOrderLine.addGoodsIntoStockOrderLine(supplierDeliveryLine);
		// }
		// // if(!supplierDeliveryLines.contains(supplierDeliveryLine)){
		// //
		// customerOrderLine.addGoodsIntoStockOrderLine(supplierDeliveryLine);
		// // }
		fill(customerOrderLine, customerOrderLine.getNewAmount());
	}

	private void fill(CustomerOrderLine customerOrderLine, Long amountToFill) {
		// long amountFilled = (customerOrderLine.getAmountFilled() == null) ? 0
		// : customerOrderLine.getAmountFilled();
		// Long newAmountFilled = amountFilled + amountToFill;
		if (amountToFill > customerOrderLine.getAmount()) {
			// Cannot be
			throw new BookmarksException(
					"Fill amount is higher than order amount");
		}
		customerOrderLine.setAmountFilled(amountToFill);

		// Decrement from stockItems quantityOnOrder
		StockItem stockItem = customerOrderLine.getStockItem();
		Long quantityOnOrder = stockItem.getQuantityOnOrder() - amountToFill;
		stockItem.setQuantityOnOrder(quantityOnOrder < 0 ? 0 : quantityOnOrder);
		stockItem.setQuantityReadyForCustomer(stockItem
				.getQuantityReadyForCustomer() + amountToFill);

		if (customerOrderLine.getDeliveryType() == DeliveryType.MAIL) {
			customerOrderLine.setStatus(CustomerOrderLineStatus.READY_TO_POST);
		} else {
			// Collection
			customerOrderLine
					.setStatus(CustomerOrderLineStatus.INFORM_CUSTOMER_TO_COLLECT);
		}
	}

	@Override
	public void updateStatus(Long id, CustomerOrderLineStatus status) {
		customerOrderLineRepository.updateStatus(id, status);

	}

	@Override
	public void markAsPaid(Long customerOrderLineId) {
		customerOrderLineRepository.markAsPaid(customerOrderLineId);
	}

	/**
	 * Fill from searchCustomerOrders screen, instant fill unlike above fill
	 * methods which just prepare for createCustomerOrder
	 */
	@Override
	public void fill(CustomerOrderLine customerOrderLine) {
		long amountFilled = customerOrderLine.getAmountFilled(); // From post
		customerOrderLine = get(customerOrderLine.getId()); // Horrible amount
															// of sql, must be
															// easier way

		// Special case, if amountFilled is 0, all is out of stock
		if (amountFilled == 0) {
			customerOrderLine.setStatus(CustomerOrderLineStatus.OUT_OF_STOCK);
		} else if (amountFilled < customerOrderLine.getAmount()) {
			// Not fully filled, split
			// Split into two, one fully filled (in stock) one out of stock
			CustomerOrderLine split = customerOrderLine.clone(); // Out of stock
			split.setAmount(customerOrderLine.getAmount() - amountFilled);
			split.setStatus(CustomerOrderLineStatus.OUT_OF_STOCK);
			customerOrderLine.setAmount(amountFilled);
			save(split);
		}
		if (customerOrderLine.getStatus() == CustomerOrderLineStatus.IN_STOCK) {
			// Decrement quantityInStock and increment quantityReadyForCustomer
			stockItemService.updateQuantities(customerOrderLine.getStockItem(),
					amountFilled * -1, null, null, null, amountFilled);
		}
		customerOrderLine.setIsReady(); // Set appropriate status
		update(customerOrderLine);
	}

	@Override
	public void saveOrUpdate(Collection<CustomerOrderLine> customerOrderLines) {
		for (CustomerOrderLine col : customerOrderLines) {
			saveOrUpdate(col);
		}
	}

	/**
	 * Called for a non-account customer order line
	 */
	@Override
	public String complete(CustomerOrderLine customerOrderLine) {

		customerOrderLine.complete();

		saleService.sellCustomerOrder(customerOrderLine);

		update(customerOrderLine);

		if (customerOrderLine.getCustomer().getContactDetails() != null 
				&&customerOrderLine.getCustomer().getContactDetails().getEmail() != null
				&& customerOrderLine.getDeliveryType() == DeliveryType.MAIL) {
			
			try {
				emailService.sendCustomerOrderLinePostedEmail(customerOrderLine);
			} catch(Exception e) {
				logger.error("Cannot send posted email", e);
			}
		}
		return null;
	}

	@Override
	public void wipeCCDetails() {
		customerOrderLineRepository.wipeCCDetails();
	}

	@Override
	public void makeNote(Long id,
			CustomerOrderLineStatus customerOrderLineStatus) {
		CustomerOrderLine col = customerOrderLineRepository.get(id);
		String note = col.getNote();
		if (note == null)
			note = "";
		else
			note = note + "<br/><br/>";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		note = note + customerOrderLineStatus.getDisplayName() + " on "
				+ sdf.format(new Date());
		col.setNote(note);
		update(col);
	}

	@Override
	public void updateHasPrintedLabel(boolean b, Long id) {
		customerOrderLineRepository.updateHasPrintedLabel(b, id);

	}
}
