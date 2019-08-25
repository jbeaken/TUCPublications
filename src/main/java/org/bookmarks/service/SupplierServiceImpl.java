package org.bookmarks.service;

import java.util.Collection;

import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.SupplierDelivery;
import org.bookmarks.repository.Repository;
import org.bookmarks.repository.CategoryRepository;
import org.bookmarks.repository.SupplierDeliveryRepository;
import org.bookmarks.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupplierServiceImpl extends AbstractService<Supplier> implements SupplierService {

	@Autowired
	private SupplierRepository supplierRepository;
	

	@Override
	public Repository<Supplier> getRepository() {
		return supplierRepository;
	}
	
	@Override
	public Collection<Supplier> getForAutoComplete(String searchString) {
		return supplierRepository.getForAutoComplete(searchString);
	}


	@Override
	public void updateMarxismSupplier(Supplier supplier,
			Supplier marxismSupplier) {
		supplierRepository.updateMarxismSupplier(supplier,
				marxismSupplier);
		
	}
	@Override
	public Collection<Supplier> getAllSortedExcludingMarxismSuppliers() {
		return supplierRepository.getAllSortedExcludingMarxismSuppliers();
	}


	@Override
	public Supplier getSupplier(Publisher publisher) {
		Supplier supplier = new Supplier();
		supplier.setName(publisher.getName());
		supplier.setAddress(publisher.getAddress());
		supplier.setContactName(publisher.getContactName());
		supplier.setEmail(publisher.getEmail());
		supplier.setTelephone1(publisher.getTelephone1());
		supplier.setTelephone2(publisher.getTelephone2());
		return supplier;
	}
}
