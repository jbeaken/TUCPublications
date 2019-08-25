package org.bookmarks.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.PricedOrderLine;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierReturn;
import org.bookmarks.domain.SupplierReturnLine;
import org.bookmarks.domain.SupplierReturn;
import org.bookmarks.repository.Repository;
import org.bookmarks.repository.SupplierReturnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierReturnServiceImpl extends AbstractService<SupplierReturn> implements SupplierReturnService {

	@Autowired
	private SupplierReturnRepository supplierReturnRepository;

	@Autowired
	private CustomerOrderLineService customerOrderLineService;

	@Autowired
	private StockItemService stockItemService;
	
	@Autowired
	private SupplierOrderLineService supplierOrderLineService;

	@Override
	public Repository<SupplierReturn> getRepository() {
		return supplierReturnRepository;
	}

	@Override
	@Transactional
	public void create(SupplierReturn supplierReturn) {
		save(supplierReturn);
	}
}
