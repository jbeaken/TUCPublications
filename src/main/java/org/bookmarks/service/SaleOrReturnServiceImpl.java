package org.bookmarks.service;


import java.math.BigDecimal;
import java.util.Collection;

import org.bookmarks.domain.SaleOrReturn;
import org.bookmarks.domain.SaleOrReturnOrderLine;
import org.bookmarks.domain.SaleOrReturnStatus;
import org.bookmarks.repository.Repository;
import org.bookmarks.repository.SaleOrReturnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaleOrReturnServiceImpl extends AbstractService<SaleOrReturn> implements SaleOrReturnService {

	@Autowired
	private SaleOrReturnRepository saleOrReturnRepository;


	@Autowired
	private StockItemService stockItemService;
	
	@Autowired
	private SaleService saleService;


	@Override
	public Repository<SaleOrReturn> getRepository() {
		return saleOrReturnRepository;
	}

//	@Override
//	public BigDecimal getPrice(SaleOrReturn saleOrReturn,
//			Map<Long, SaleOrReturnOrderLine> orderLineMap) {
//		return getPrice(saleOrReturn, orderLineMap.values());
//	}


	@Override
	@Transactional
	public void save(SaleOrReturn saleOrReturn) {
		//Update onloan and quantity in stock in stock
		for(SaleOrReturnOrderLine sorol : saleOrReturn.getSaleOrReturnOrderLines()) {
			sorol.setAmountSold(0l);
			sorol.setSaleOrReturn(saleOrReturn);  //TODO why do I have to do this
			//Update stock record
			stockItemService.updateQuantities(sorol.getStockItem(), sorol.getAmount() * -1,	sorol.getAmount(), null, null, null);
		}
		super.save(saleOrReturn);
	}
	

	@Override
	@Transactional
	public void update(SaleOrReturn saleOrReturn) {
		//Update onloan and quantity in stock in stock
		for(SaleOrReturnOrderLine sorol : saleOrReturn.getSaleOrReturnOrderLines()) {
			sorol.setAmountSold(0l);
//			sorol.setSaleOrReturn(saleOrReturn);  //TODO why do I have to do this
			//Update stock record
			Long amountChanged = sorol.getOriginalAmount() - sorol.getAmount();
			stockItemService.updateQuantities(sorol.getStockItem(), amountChanged,	amountChanged * -1l, null, null, null);
		}
		super.update(saleOrReturn);
	}	
	
	@Override
	@Transactional
	public void markAsReturn(SaleOrReturn saleOrReturn) {
		//Update onloan and quantity in stock
		putBackIntoStock(saleOrReturn);
		
					
		saleOrReturn.setSaleOrReturnStatus(SaleOrReturnStatus.RETURNED);
		super.update(saleOrReturn);
	}
	
	private void putBackIntoStock(SaleOrReturn saleOrReturn) {
		for(SaleOrReturnOrderLine sorol : saleOrReturn.getSaleOrReturnOrderLines()) {
				Long quantityChange = sorol.getAmount();
				stockItemService.updateQuantities(sorol.getStockItem(), quantityChange, quantityChange * -1, null, null, null);
			}
	}

	@Override
	@Transactional
	public void delete(SaleOrReturn saleOrReturn) {
		//Need to update onloan and quantity in stock in stock
		putBackIntoStock(saleOrReturn);
		
		super.delete(saleOrReturn);
	}

//	@Override
//	public BigDecimal getPrice(SaleOrReturn saleOrReturn) {
//		return getPrice(saleOrReturn, saleOrReturn.getSaleOrReturnOrderLines());
//	}

	public BigDecimal getPrice(SaleOrReturn saleOrReturn, Collection<SaleOrReturnOrderLine> saleOrReturnOrderLines) {
		float totalPrice = 0;
		for(SaleOrReturnOrderLine saleOrReturnOrderLine : saleOrReturnOrderLines){
			//float sellPrice = saleOrReturnOrderLine.getSellPrice().floatValue();
			//long amount = saleOrReturnOrderLine.getAmount();

			//BigDecimal price = new BigDecimal(sellPrice);
			//price = price.setScale(2, BigDecimal.ROUND_HALF_DOWN);

			totalPrice += saleOrReturnOrderLine.getPrice().floatValue();
			//Set values
			//saleOrReturnOrderLine.setPrice(new BigDecimal(price.floatValue() * amount).setScale(2, BigDecimal.ROUND_HALF_DOWN));
		}
		return new BigDecimal(totalPrice);
	}

	@Override
	public void sell(SaleOrReturn saleOrReturn) {
		//saleOrReturn.setSaleOrReturnStatus(SaleOrReturnStatus.SOLD_OUT);
		
		//Create sales which deal with 
		saleService.sell(saleOrReturn);
		
		update(saleOrReturn);
		
	}


}
