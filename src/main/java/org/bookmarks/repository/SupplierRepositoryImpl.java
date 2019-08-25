package org.bookmarks.repository;

import java.util.Collection;

import org.bookmarks.domain.Category;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.Supplier;
import org.bookmarks.controller.SupplierSearchBean;
import org.bookmarks.controller.SearchBean;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SupplierRepositoryImpl extends AbstractRepository<Supplier> implements SupplierRepository {

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
		return "org.bookmarks.domain.Supplier";
	}
	
	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
    	return new StringBuffer("select s from Supplier as s");
    }
	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(s) from Supplier as s");
	}  
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Supplier> getForAutoComplete(String searchString) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select new Supplier(s.id, s.name) from Supplier s " +
					"where s.name like '%" + searchString.trim() + "%'");
		return query.list();
	}	
	
	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		SupplierSearchBean supplierSearchBean = (SupplierSearchBean) searchBean;
		Supplier s = supplierSearchBean.getSupplier();
		QueryBuilder queryBuilder = new QueryBuilder();

		//Build query
		queryBuilder.append(s.getName(), "s.name");
		
		query.append(queryBuilder.getQuery());

	}

	@Override
	public void updateMarxismSupplier(Supplier supplier, Supplier marxismSupplier) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update Supplier " +
						"set marxismSupplier = :marxismSupplier " +
						"where id = :id");
		query.setParameter("marxismSupplier", marxismSupplier);
		query.setParameter("id", supplier.getId());
		query.executeUpdate();
	}	
	
	@Override
	public Collection<Supplier> getAllSortedExcludingMarxismSuppliers() {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select s " +
						"from Supplier s  " +
						" order by name ASC");
		return query.list();
	}
}
