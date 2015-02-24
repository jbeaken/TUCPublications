package org.bookmarks.repository;

import java.util.Collection;

import org.bookmarks.domain.Category;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.StockItemSales;
import org.bookmarks.controller.SearchBean;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class StockItemSalesRepositoryImpl extends AbstractRepository<StockItemSales> implements StockItemSalesRepository {

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
		return "org.bookmarks.domain.StockItemSales";
	}
	
	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
    	return new StringBuffer("select s from StockItemSales as s");
    }
	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(s) from StockItemSales as s");
	}  
	
//	
//	
//	public void appendWhere(StringBuffer query, SearchBean searchBean) {
//		StockItemSalesSearchBean supplierSearchBean = (StockItemSalesSearchBean) searchBean;
//		StockItemSales s = supplierSearchBean.getStockItemSales();
//		QueryBuilder queryBuilder = new QueryBuilder();
//
//		//Build query
//		queryBuilder.append(s.getName(), "s.name");
//		
//		query.append(queryBuilder.getQuery());
//
//	}
}
