package org.bookmarks.repository;

import java.util.List;

import org.bookmarks.domain.Category;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.SupplierDelivery;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.domain.SupplierOrderLineStatus;
import org.bookmarks.domain.SupplierOrderLineType;

import org.hibernate.query.Query;
import org.hibernate.SessionFactory;

import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.SupplierOrderLineSearchBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SupplierOrderLineRepositoryImpl extends AbstractRepository<SupplierOrderLine> implements SupplierOrderLineRepository {

    private SessionFactory sessionFactory;
    
    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
    	//return new StringBuffer("select so from SupplierOrder as so left join fetch so.supplierOrderLines sol left join sol.stockItem si");
		return new StringBuffer("select new SupplierOrderLine(sol.id, sol.amount, si.id, si.title, si.isbn, sol.priority, sol.supplierOrderLineStatus, sol.sendDate, sup.name, sup.id, sol.type, sol.note) " +
				"from SupplierOrderLine sol " +
				" join sol.stockItem si join sol.supplier sup left join sol.customerOrderLine col");
    }

	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(sol) from SupplierOrderLine as sol join sol.stockItem si join sol.supplier sup left join sol.customerOrderLine col");
	}

	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		SupplierOrderLineSearchBean sob = (SupplierOrderLineSearchBean) searchBean;
		SupplierOrderLine sol = sob.getSupplierOrderLine();

		QueryBuilder queryBuilder = new QueryBuilder();

		//Build query
		if(sob.getCustomerOrderLine() != null && sob.getCustomerOrderLine().getId() !=  null) {
			queryBuilder.append(sob.getCustomerOrderLine(), "col.id");
		} else {
			queryBuilder.append(sol, "sol.id");
			queryBuilder.append(sob.getStockItem().getTitle(), "si.title");
			queryBuilder.append(sob.getStockItem().getIsbn(), "si.isbn");
			queryBuilder.append(sol.getSupplierOrderLineStatus(), "sol.supplierOrderLineStatus");
			queryBuilder.append(sol.getType(), "sol.type");
			queryBuilder.append(sol.getPriority(), "sol.priority");
			queryBuilder.append(sol.getSupplier(), "sol.supplier");
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
		return "org.bookmarks.domain.SupplierOrderLine";
	}

	@Override
	public SupplierOrderLine getByStockItemId(Long id) {
		if(id == null) return null;
		Query query = sessionFactory.getCurrentSession().createQuery("select sol from SupplierOrderLine sol " + 
				"join fetch sol.supplier sup left join fetch sol.customerOrderLine col " + 
				"where sol.stockItem.id = :id and sol.supplierOrderLineStatus = 'READY_TO_SEND' and col is null");
		query.setParameter("id", id);
		//Should return at most one sol, but you never know!
		List<SupplierOrderLine> list = query.list();
		
		//Get first of list
		SupplierOrderLine sol = null;
		if(list.size() != 0) sol = list.get(0);
		
		return sol;
	}

	@Override
	public SupplierOrderLine getByStockItemId(Long id, SupplierOrderLineType type) {
		if(id == null) return null;
		Query query = sessionFactory.getCurrentSession().createQuery("select sol from SupplierOrderLine sol " + 
				"join fetch sol.supplier sup left join fetch sol.customerOrderLine col " + 
				"where sol.stockItem.id = :id and sol.supplierOrderLineStatus = 'READY_TO_SEND' and sol.type = :type");
		query.setParameter("id", id);
		query.setParameter("type", type);
		//Should return at most one sol, but you never know!
		List<SupplierOrderLine> list = query.list();
		
		//Get first of list
		SupplierOrderLine sol = null;
		if(list.size() != 0) sol = list.get(0);
		
		return sol;
	}	

	@Override
	public SupplierOrderLine getByCustomerOrderLineId(Long customerOrderLineId) {
		Query query = sessionFactory.getCurrentSession().createQuery("select sol from SupplierOrderLine sol " + 
				"join sol.supplier sup left join sol.customerOrderLine col " + 
				"where sol.supplierOrderLineStatus = :status and col.id = :id");
		query.setParameter("id", customerOrderLineId);
		query.setParameter("status", SupplierOrderLineStatus.READY_TO_SEND);
		//Should return at most one sol, but you never know!
		//List<SupplierOrderLine> list = query.list();
		
		//Get first of list
		//SupplierOrderLine sol = null;
		//if(list.size() != 0) sol = list.get(0);
		
		return (SupplierOrderLine) query.uniqueResult();
	}

	@Override
	public void deleteKeepInStockSupplierOrderLine(StockItem stockItem) {
		removeSupplierOrderLine(stockItem, SupplierOrderLineType.KEEP_IN_STOCK);
	}

	@Override
	public void deleteAllKeepInStockSupplierOrderLines() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeSupplierOrderLine(StockItem stockItem, SupplierOrderLineType type) {
		Query query = sessionFactory.getCurrentSession().createQuery("delete from SupplierOrderLine sol where sol.stockItem.id = :stockItemId and sol.type = :type and sol.supplierOrderLineStatus = :status");
		query.setParameter("stockItemId", stockItem.getId());
		query.setParameter("status", SupplierOrderLineStatus.READY_TO_SEND);
		query.setParameter("type", type);
		query.executeUpdate();
	}
}
