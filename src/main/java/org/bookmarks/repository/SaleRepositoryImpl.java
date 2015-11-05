package org.bookmarks.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bookmarks.controller.CustomerSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.bean.SaleReportBean;
import org.bookmarks.controller.bean.SaleTotalBean;
import org.bookmarks.website.domain.Address;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.BookmarksAccount;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SaleRepositoryImpl extends AbstractRepository<Sale> implements SaleRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
		SaleReportBean saleSearchBean = (SaleReportBean) searchBean;

		if(saleSearchBean.getGroupBy()) {
			if(saleSearchBean.getSortColumn() ==  null) saleSearchBean.setSortColumn("sum(s.quantity)");
		//System.out.println("***************** " + searchBean.getSortColumn());
		//System.out.println("***************** " + searchBean.getSortOrder());
			return new StringBuffer("select " +
					"new Sale(si.title, " +
					"si.isbn, " +
					"sum(s.quantity), " +
					"sum(s.sellPrice * s.quantity * (100-s.discount)/100), " +
					"p.name) " +
					"from Sale as s " +
					"join s.stockItem as si " +
					"join si.publisher as p");
		}

    	return new StringBuffer("select " +
    			"new Sale(s.id, " +
    			"s.creationDate, " +
    			"s.quantity, " +
				"s.discount, " +
    			"s.sellPrice, " +
    			"si.title, " +
    			"si.isbn, " +
    			"p.name, " +
    			"e.name) " +
    			"from Sale as s " +
    			"left join s.event as e " +
    			"join s.stockItem as si " +
				"join si.publisher as p");
    }
	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		SaleReportBean saleSearchBean = (SaleReportBean) searchBean;

		if(saleSearchBean.getGroupBy()) {
			return new StringBuffer("select sum(si) from Sale as s join s.stockItem as si");
		}

		return new StringBuffer("select count(s) from Sale as s join s.stockItem as si");
	}

	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		SaleReportBean saleSearchBean = (SaleReportBean) searchBean;

		QueryBuilder queryBuilder = new QueryBuilder();

		//Build query
		if(saleSearchBean.getIsDateAgnostic() == Boolean.FALSE) {
			queryBuilder.appendBeetween(saleSearchBean.getStartDate(), saleSearchBean.getEndDate(),  "s.creationDate");
		}

		//Set event if appropriate
		if(saleSearchBean.getSale().getEvent().getId() != null) {
			queryBuilder.append(saleSearchBean.getSale().getEvent(), "s.event.id");
		}
//		else {
//			queryBuilder.appendIsNull("s.event.id");
//		}

		//Set stockitem if appropriate
		if(saleSearchBean.getSale().getStockItem().getIsbn() != null) {
			queryBuilder.append(saleSearchBean.getSale().getStockItem().getIsbn(), "si.isbn");
		}

		if(saleSearchBean.getSale().getStockItem().getType() != null) {
			queryBuilder.append(saleSearchBean.getSale().getStockItem().getType().toString(), "si.type");
		}

    if(saleSearchBean.getStatus() != null) {
      switch( saleSearchBean.getStatus() ) {
         case 1 :
           	queryBuilder.appendExact("BOOK", "si.type");
           break;
        case 2 :
          queryBuilder.appendNotEqual("'BOOK'", "si.type");
          break;
      }
    }
//		else {
//			queryBuilder.appendIsNull("s.event.id");
//		}

		//Set stockitem if appropriate
//		if(saleSearchBean.getSale().getStockItem().getPublisher().getId() != null) {
//			queryBuilder.append(saleSearchBean.getSale().getStockItem().getPublisher(), "si.publisher");
//		}

		queryBuilder.append(saleSearchBean.getSale().getStockItem().getPublisher(), "si.publisher");
		queryBuilder.append(saleSearchBean.getCategory(), "si.category.id");

		query.append(queryBuilder.getQuery());

	}

    @Override
	public void appendGroupBy(StringBuffer query, SearchBean searchBean) {
    	if(searchBean.getGroupBy()) {
    		query.append(" group by si");
    	}
	}

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public String getEntityName() {
		return "org.bookmarks.domain.Sale";
	}

	@Override
	public Collection<Sale> get(Date startDate, Date endDate) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select new Sale(sa.creationDate) from Sale sa " +
						"where sa.creationDate between :startDate and :endDate " +
						"order by creationDate ASC");
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		return query.list();
	}

	@Override
	public SaleTotalBean getSaleTotalBean(SearchBean searchBean) {
		StringBuffer buffer = new StringBuffer("select " +
				"new org.bookmarks.controller.bean.SaleTotalBean(sum(s.quantity * s.sellPrice * (100 -  s.discount) / 100), sum(s.quantity)) " +
				"from Sale as s " +
				"left join s.stockItem as si");
		appendWhere(buffer, searchBean);
//		buffer.append(" group by s");
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(buffer.toString());
		SaleTotalBean saleTotalBean = new SaleTotalBean();
		if(query.list().iterator().hasNext()) {
			 saleTotalBean = (SaleTotalBean) query.list().iterator().next();
		}
		return saleTotalBean;
	}

	@Override
	public Collection<Sale> getFull(Date startDate, Date endDate) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select new Sale(sa.creationDate, sa.quantity, sa.sellPrice, c.name) from Sale sa " +
						"join sa.stockItem s " +
						"join s.category c " +
						"where sa.creationDate between :startDate and :endDate " +
						"order by c.name ASC");
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		return query.list();
	}

	@Override
	public Collection<Sale> get(Long stockItemID, Date startDate, Date endDate) {
		java.sql.Timestamp sqlStartDate = new java.sql.Timestamp(startDate.getTime());
		java.sql.Timestamp sqlEndDate = new java.sql.Timestamp(endDate.getTime());
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select new Sale(sa.creationDate) from Sale sa " +
						"where sa.creationDate between '" + sqlStartDate + "' and '" + sqlEndDate + "' " +
						"and sa.stockItem.id = :stockItemID " +
						"order by sa.creationDate ASC");
//		query.setTimestamp("startDate", sqlStartDate);
//		query.setTimestamp("endDate", sqlEndDate);
		query.setParameter("stockItemID", stockItemID);
		return query.list();
	}

	@Override
	public Long getTotalQuantityForPeriod(StockItem stockItem, Date startDate,
			Date endDate) {
		java.sql.Timestamp sqlStartDate = new java.sql.Timestamp(startDate.getTime());
		java.sql.Timestamp sqlEndDate = new java.sql.Timestamp(endDate.getTime());
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select sum(sa.quantity) from Sale sa " +
//						"join sa.stockItem s " +
						"where sa.creationDate between :startDate and :endDate and sa.stockItem.id = :stockItemId");
		query.setParameter("startDate", sqlStartDate);
		query.setParameter("endDate", sqlEndDate);
		query.setParameter("stockItemId", stockItem.getId());
		Long result = (Long) query.uniqueResult();
		return result;
	}

	@Override
	public List getAllForCsv() {
		Query query = sessionFactory.getCurrentSession()
				.createQuery("select 'S', sa.quantity, sa.discount, sa.sellPrice, sa.vat, sa.creationDate, si.id from Invoice i right join i.sales sa join sa.stockItem si where i is null");
		return query.list();
	}
}
