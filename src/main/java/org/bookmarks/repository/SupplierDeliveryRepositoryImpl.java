package org.bookmarks.repository;

import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.SupplierDeliverySearchBean;
import org.bookmarks.domain.SupplierDelivery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;

@Repository
//@Transactional
public class SupplierDeliveryRepositoryImpl extends AbstractRepository<SupplierDelivery> implements SupplierDeliveryRepository {

    private SessionFactory sessionFactory;
    
    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select new SupplierDelivery(sd.id, sd.creationDate, sd.invoiceNumber, sd.supplier.name, sup.telephone1, sup.supplierAccount.accountNumber, count(sdl)) from SupplierDelivery as sd "
						+ "join sd.supplierDeliveryLine sdl "
						+ "join sd.supplier sup "
						+ "join sdl.stockItem si ");
    }
	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(sd) from SupplierDelivery as sd join sd.supplierDeliveryLine sdl join sdl.stockItem si");
	}	

	@Override
	public void appendGroupBy(StringBuffer query, SearchBean searchBean) {
    	query.append(" group by sd ");
	}
	
	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		SupplierDeliverySearchBean deliverySearchBean = (SupplierDeliverySearchBean) searchBean;

		QueryBuilder queryBuilder = new QueryBuilder();

		//Build query
		queryBuilder.append(deliverySearchBean.getSupplierDelivery(), "sd.id");
		queryBuilder.append(deliverySearchBean.getSupplierDelivery().getInvoiceNumber(), "sd.invoiceNumber");
		queryBuilder.append(deliverySearchBean.getStockItem().getTitle(), "si.title");
		queryBuilder.append(deliverySearchBean.getStockItem().getIsbn(), "si.isbn");
		queryBuilder.append(deliverySearchBean.getSupplierDelivery().getSupplier(), "sd.supplier");

		query.append(queryBuilder.getQuery());

	}	

	@Override
	public String getEntityName() {
		return "org.bookmarks.domain.SupplierDelivery";
	}

}
