package org.bookmarks.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.PricedOrderLine;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierDelivery;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.repository.Repository;
import org.bookmarks.repository.SupplierDeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierDeliveryServiceImpl extends AbstractService<SupplierDelivery> implements SupplierDeliveryService {

	@Autowired
	private SupplierDeliveryRepository supplierDeliveryRepository;

	@Autowired
	private CustomerOrderLineService customerOrderLineService;

	@Autowired
	private StockItemService stockItemService;
	
	@Autowired
	private SupplierOrderLineService supplierOrderLineService;

	@Override
	public Repository<SupplierDelivery> getRepository() {
		return supplierDeliveryRepository;
	}
	
	/**
	* Two ways of calculating :
	* (price * discount) * quantity : Marston & Turnaround
	* (price * quantity) * discount : gardners
	*/
	@Override
	public void calculatePrice(SupplierDelivery supplierDelivery, SupplierDeliveryLine supplierDeliveryLine) {
		Long supplierId = supplierDelivery.getSupplier().getId();
		BigDecimal stockItemPrice = supplierDeliveryLine.getPublisherPrice();
		Float discount = supplierDeliveryLine.getDiscount().floatValue();
		float amount = supplierDeliveryLine.getAmount();
		BigDecimal costPrice = new BigDecimal(stockItemPrice.floatValue() * (1 - (discount / 100)));
		
		//costPrice = costPrice.setScale(2, BigDecimal.ROUND_CEILING);

		//For Marstons (11) * Turnaround
		//For Gardners (1)
		//For Wiley (3), rounds price down
		if(supplierId == 1) {
		costPrice = costPrice.setScale(2, BigDecimal.ROUND_HALF_DOWN);
//		totalPrice += (price.floatValue() * amount);
		//Set values
//		orderLine.setDiscount(new BigDecimal(discount));
		supplierDeliveryLine.setCostPrice(costPrice);
		supplierDeliveryLine.setPrice(new BigDecimal(costPrice.floatValue() * amount).setScale(2, BigDecimal.ROUND_HALF_DOWN));
		} else if(supplierId == 3) {
			supplierDeliveryLine.setCostPrice(costPrice);
			BigDecimal priceUnrounded = new BigDecimal(costPrice.floatValue() * amount);
			supplierDeliveryLine.setPrice(priceUnrounded.setScale(2, BigDecimal.ROUND_FLOOR));
		} else {
			supplierDeliveryLine.setCostPrice(costPrice);
			BigDecimal priceUnrounded = new BigDecimal(costPrice.floatValue() * amount);
			supplierDeliveryLine.setPrice(priceUnrounded.setScale(2, BigDecimal.ROUND_CEILING));
		}
		

			
	}
	
	private void calculateGardnersPrice(PricedOrderLine orderLine, BigDecimal stockItemPrice) {
		Float discount = orderLine.getStockItem().getDiscount().floatValue();
		float amount = orderLine.getAmount();
		
		BigDecimal costPrice = new BigDecimal(stockItemPrice.floatValue() * (1 - (discount / 100)));
		costPrice = costPrice.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		
//		totalPrice += (price.floatValue() * amount);
		//Set values
//		orderLine.setDiscount(new BigDecimal(discount));
		orderLine.getStockItem().setCostPrice(costPrice);
		orderLine.setPrice(new BigDecimal(costPrice.floatValue() * amount).setScale(2, BigDecimal.ROUND_HALF_DOWN));
	
	}		

	@Override
	@Transactional
	public void create(Collection<CustomerOrderLine> filledCustomerOrderLines,	SupplierDelivery supplierDelivery) {

		save(supplierDelivery);

		for(SupplierDeliveryLine supplierDeliveryLine : supplierDelivery.getSupplierDeliveryLine()) {
			//StockItem stockItem = stockItemService.get(supplierDeliveryLine.getStockItem().getId());
			StockItem stockItem = supplierDeliveryLine.getStockItem();

			Long totalAmountFilled = 0l;

			//Get total fill amount
			for(CustomerOrderLine c : filledCustomerOrderLines) {
				if(c.getStockItem().getId().equals(stockItem.getId())) {
					totalAmountFilled += c.getNewAmount();
				}
			}

			//quantity in stock
			stockItem.setQuantityInStock(supplierDeliveryLine.getAmount() + stockItem.getQuantityInStock() - totalAmountFilled);

			//quantity on order
			Long quantityOnOrder = stockItem.getQuantityOnOrder() - supplierDeliveryLine.getAmount();
			if(quantityOnOrder < 0) quantityOnOrder = 0l;
			stockItem.setQuantityOnOrder(quantityOnOrder);
			
			//quantity for customer
			Long quantityForCustomerOrder = stockItem.getQuantityForCustomerOrder() - totalAmountFilled;
			if(quantityForCustomerOrder < 0) quantityForCustomerOrder = 0l;
			stockItem.setQuantityForCustomerOrder(quantityForCustomerOrder);
			
			//quantity ready for customer
			Long quantityReadyForCustomer = stockItem.getQuantityReadyForCustomer() + totalAmountFilled;
			stockItem.setQuantityReadyForCustomer(quantityReadyForCustomer);
			
			if(supplierDeliveryLine.getUpdateStockItemDiscount()) {
				stockItem.setDiscount(supplierDeliveryLine.getDiscount());
			}
			if(supplierDeliveryLine.getUpdateStockItemCostPrice()) {
				stockItem.setCostPrice(supplierDeliveryLine.getCostPrice());
			}
			if(supplierDeliveryLine.getUpdateStockItemSellPrice()) {
				stockItem.setSellPrice(supplierDeliveryLine.getSellPrice());
			}
			if(supplierDeliveryLine.getUpdateStockItemPublisherPrice()) {
				stockItem.setPublisherPrice(supplierDeliveryLine.getPublisherPrice());
			}

			stockItemService.updateForSupplierDelivery(stockItem);
			
			supplierOrderLineService.reconcileKeepInStock(stockItem, false);
		}//end for

		//Update customer order lines
		for(CustomerOrderLine c : filledCustomerOrderLines) {
			for(SupplierDeliveryLine supplierDeliveryLine : supplierDelivery.getSupplierDeliveryLine()) {
				if(supplierDeliveryLine.getStockItem().getId().equals(c.getStockItem().getId())) {
					c.addSupplierDeliveryLine(supplierDeliveryLine);
				}
			}
			c.setReceivedIntoStockDate(new Date());
			if(c.getAmountFilled() != 0 && (c.getAmountFilled() < c.getAmount())) { //Partial fill
				//split
				CustomerOrderLine split = c.clone();
				split.setAmount(c.getAmount() - c.getAmountFilled());
				split.setStatus(CustomerOrderLineStatus.OUT_OF_STOCK);
				customerOrderLineService.save(split);
				c.setAmount(c.getAmountFilled());
			}
			customerOrderLineService.update(c);
		}//end for
	}
}
