package org.bookmarks.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.bookmarks.controller.CustomerSearchBean;
import org.bookmarks.controller.bean.ReorderReviewBean;
import org.bookmarks.controller.bean.SaleReportBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.SupplierOrderSearchBean;

import org.bookmarks.website.domain.Address;
import org.bookmarks.domain.BookmarksAccount;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierOrder;
import org.bookmarks.domain.SupplierOrderLine;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SupplierOrderRepositoryImpl extends AbstractRepository<SupplierOrder> implements SupplierOrderRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
    	//return new StringBuffer("select so from SupplierOrder as so left join fetch so.supplierOrderLines sol left join sol.stockItem s");
		return new StringBuffer("select new SupplierOrder(so.id, so.supplierOrderStatus, so.sendDate, so.supplier.name, so.supplier.telephone1, so.supplier.supplierAccount.accountNumber, count(sol)) " +
				"from SupplierOrder as so "
						+ "left join so.supplierOrderLines sol "
						+ "left join sol.stockItem s");
    }
	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select sum(so) from SupplierOrder as so left join so.supplierOrderLines sol left join sol.stockItem s");
	}
	
    @Override
	public void appendGroupBy(StringBuffer query, SearchBean searchBean) {
    	query.append(" group by so");
	}

	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		SupplierOrderSearchBean sob = (SupplierOrderSearchBean) searchBean;
		SupplierOrder s = sob.getSupplierOrder();

		QueryBuilder queryBuilder = new QueryBuilder();

		//Build query
		if(sob.getCustomerOrderLine() != null && sob.getCustomerOrderLine().getId() !=  null) {
			queryBuilder.append(sob.getCustomerOrderLine(), "sol.customerOrderLine.id");
		} else {
			queryBuilder.append(s, "so.id");
			queryBuilder.append(sob.getStockItem().getTitle(), "s.title");
			queryBuilder.append(sob.getStockItem().getIsbn(), "s.isbn");
			queryBuilder.append(s.getSupplierOrderStatus(), "so.supplierOrderStatus");
			queryBuilder.append(s.getSupplier(), "so.supplier");
			if(sob.getContainsCustomerOrderLines() != null && sob.getContainsCustomerOrderLines() == true) {
				queryBuilder.appendIsNotNull("sol.customerOrderLine");
			}
		}

		query.append(queryBuilder.getQuery());

	}

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public String getEntityName() {
		return "org.bookmarks.domain.SupplierOrder";
	}

	@Override
	public SupplierOrder getPending(Long id) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select so from SupplierOrder as so left join fetch so.supplierOrderLines where so.supplier.id = :id and so.supplierOrderStatus = 'PENDING'");
		query.setParameter("id", id);
		List list = query.list();
		if(list.isEmpty()) {
			return null;
		}
		return (SupplierOrder) list.iterator().next();
	}
	
	@Override
	public SupplierOrderLine getPendingSupplierOrderLineFromStockItem(StockItem stockItem) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select sol from SupplierOrderLine sol join sol.stockItem si where si.id = :stockItemId and (sol.supplierOrderLineStatus = 'READY_TO_SEND' or sol.supplierOrderLineStatus = 'ON_HOLD')");
		query.setParameter("stockItemId", stockItem.getId());
		return (SupplierOrderLine) query.uniqueResult();
	}


	@Override
	public Collection<SupplierOrder> getPending() {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select so from SupplierOrder as so left join fetch so.supplierOrderLines where so.supplierOrderStatus = 'PENDING'");
		List<SupplierOrder> list = query.list();
		
		return list;
	}


	@Override
	public Collection<StockItem> getReorderReview(
			SaleReportBean saleSearchBean) {
		java.sql.Timestamp sqlStartDate = new java.sql.Timestamp(saleSearchBean.getStartDate().getTime());
		java.sql.Timestamp sqlEndDate = new java.sql.Timestamp(saleSearchBean.getEndDate().getTime());
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select distinct s from Sale as sa " +
						"join sa.stockItem as s " +
						"join s.publisher as p " +
						"join p.supplier as su " +
						"where sa.creationDate between :startDate and :endDate");
//				.createQuery("select new StockItem(si.id, si.isbn, si.title, si.quantityInStock, si.publisherPrice, si.costPrice, si.sellPrice, si.preferredSupplier, su.name, su.id, si.sum(s.quantity)) from StockItem as si right join Sale as s join si.publisher as p join p.supplier as su where sa.creationDate between :startDate and :endDate group by si");
		query.setParameter("startDate", sqlStartDate);
		query.setParameter("endDate", sqlEndDate);
		Collection<StockItem> stockItems = query.list();
		return stockItems;
	}

	@Override
	public Collection<StockItem> getReorderReview(ReorderReviewBean reorderReviewBean) {
		java.sql.Timestamp sqlStartDate = new java.sql.Timestamp(reorderReviewBean.getStartDate().getTime());
		java.sql.Timestamp sqlEndDate = new java.sql.Timestamp(reorderReviewBean.getEndDate().getTime());
		QueryBuilder queryBuilder = new QueryBuilder();
		StringBuffer buffer = new StringBuffer("select distinct s from Sale as sa " +
						"join sa.stockItem as s " +
						"join s.publisher as p " +
						"join p.supplier as su ");

		QueryBuilder q = new QueryBuilder();
		if(reorderReviewBean.getIsDateAgnostic() == Boolean.FALSE) {
			q.appendBeetween(reorderReviewBean.getStartDate(), reorderReviewBean.getEndDate(), "sa.creationDate");
		}
		if(reorderReviewBean.getSupplier() != null) {
			q.append(reorderReviewBean.getSupplier().getId().toString(), "su.id");
		}
		buffer.append(q.getQuery());
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(buffer.toString());
		Collection<StockItem> stockItems = query.list();
		return stockItems;
	}


}
