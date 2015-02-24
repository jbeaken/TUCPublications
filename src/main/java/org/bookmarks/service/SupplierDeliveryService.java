package org.bookmarks.service;

import java.util.Collection;

import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.SupplierDelivery;
import org.bookmarks.domain.SupplierDeliveryLine;


public interface SupplierDeliveryService extends Service<SupplierDelivery>{

	void calculatePrice(SupplierDelivery supplierDelivery, SupplierDeliveryLine supplierDeliveryLine);

	void create(Collection<CustomerOrderLine> filledCustomerOrderLines,	SupplierDelivery supplierDelivery);
}
