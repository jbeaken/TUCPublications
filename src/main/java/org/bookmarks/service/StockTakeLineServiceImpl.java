package org.bookmarks.service;

import org.bookmarks.domain.StockTakeLine;
import org.bookmarks.repository.Repository;
import org.bookmarks.repository.StockTakeLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StockTakeLineServiceImpl extends AbstractService<StockTakeLine> implements StockTakeLineService{

	@Autowired
	private StockTakeLineRepository stockTakeLineRepository;
	
	@Override
	public Repository<StockTakeLine> getRepository() {
		return stockTakeLineRepository;
	}
	
	@Override
	public StockTakeLine getByStockItemId(Long id) {
		return stockTakeLineRepository.getByStockItemId(id);
	}

	@Override
	public void commit(boolean resetQuantityInStock) {
		stockTakeLineRepository.commit(resetQuantityInStock);
	}

	@Override
	public void reset() {
		stockTakeLineRepository.reset();
	}
}
