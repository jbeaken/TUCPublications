package org.bookmarks.repository;

import org.hibernate.Query;
import java.util.Collection;

import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.StockTakeLine;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class StockTakeLineRepositoryImpl extends AbstractRepository<StockTakeLine> implements StockTakeLineRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    
	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
    	return new StringBuffer("select new StockTakeLine(stl.id, stl.quantity, s.isbn, s.title) " +
    			"from StockTakeLine as stl " +
    			"left join stl.stockItem as s");
    }
	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(stl) from StockTakeLine as stl " +
				"left join stl.stockItem as s");
	}    
	
    
	@Override
	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		StockItemSearchBean stockItemSearchBean = (StockItemSearchBean) searchBean;
		boolean whereAlreadyAppended = false;
		whereAlreadyAppended = appendIsbn(stockItemSearchBean, query);
		whereAlreadyAppended = appendTitle(stockItemSearchBean, query, whereAlreadyAppended);
	}
	
	private boolean appendTitle(StockItemSearchBean stockItemSearchBean,
			StringBuffer query, boolean whereAlreadyAppended) {
		String title = escapeText(stockItemSearchBean.getStockItem().getTitle().trim());

		if(title != null && !title.isEmpty() && whereAlreadyAppended) {
			query.append(" and s.title like '%" + title + "%'");
		} else if(title != null && !title.isEmpty()){
			query.append(" where s.title like '%" + title + "%'");
			whereAlreadyAppended = true;
		}
		return whereAlreadyAppended;
	}
	
	private boolean appendIsbn(StockItemSearchBean stockItemSearchBean, StringBuffer query) {
		String isbn = stockItemSearchBean.getStockItem().getIsbn();
		if(!isbn.isEmpty()) {
			if(isbn.length() == 13) {
				query.append(" where s.isbnAsNumber = " + isbn);
			} else {
				query.append(" where s.isbn like '%" + isbn + "'");
			} 
			return true;
		}
		return false;
	}	



	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public String getEntityName() {
		return "org.bookmarks.domain.StockTakeLine";
	}


	@Override
	public StockTakeLine getByStockItemId(Long id) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("from StockTakeLine stl where stl.stockItem.id = :id");
		query.setParameter("id", id);
		return (StockTakeLine)query.uniqueResult();
	}


	@Override
	public void commit(boolean resetQuantityInStock) {
		Query query = null;

		if(resetQuantityInStock) { //Reset quantity in stock if necessary, but not for bookmarks publications
			query = sessionFactory
					.getCurrentSession()
					.createQuery("update StockItem si set si.quantityInStock = 0 "
							+ "where si.publisher.id not in (725,729) "
							+ "and si.type not in ('DVD','CARD','POSTER','BAG') and  category.id != 69");
			query.executeUpdate();			
		}

		//Update any stock referenced by a StockTakeLine
		//Except bookmarks, redwords publisher
		//And merchandies
		query = sessionFactory
				.getCurrentSession()
				.createSQLQuery("update stockitem si, StockTakeLine stl " +
						"set si.quantityInStock = stl.quantity " +
						"where si.id = stl.stockItem_id and si.publisher_id not in (725,729) " +
						"and si.stockItemType not in ('DVD','CARD','POSTER','BAG') and  category_id != 69 ");
		query.executeUpdate();
	}


	@Override
	public void reset() {
		Query query = sessionFactory.getCurrentSession().createQuery("delete from StockTakeLine");
		query.executeUpdate();
	}
}
