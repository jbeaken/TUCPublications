package org.bookmarks.repository;

import org.bookmarks.domain.Category;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class GoodsIntoStockOrderLineRepositoryImpl extends AbstractRepository<SupplierDeliveryLine> implements GoodsIntoStockOrderLineRepository {

    private SessionFactory sessionFactory;
    
    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public String getEntityName() {
		return "org.bookmarks.domain.GoodsIntoStockOrderLine";
	}
}
