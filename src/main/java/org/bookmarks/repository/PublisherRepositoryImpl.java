package org.bookmarks.repository;

import java.util.Collection;
import java.util.List;

import org.bookmarks.controller.CustomerSearchBean;
import org.bookmarks.controller.PublisherSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.website.domain.Address;
import org.bookmarks.domain.BookmarksAccount;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.bookmarks.domain.Invoice;

@Repository
@Transactional
public class PublisherRepositoryImpl extends AbstractRepository<Publisher> implements PublisherRepository {

    private SessionFactory sessionFactory;
    
	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
    	return new StringBuffer("select p from Publisher as p left join p.supplier as s");
    }
	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(p) from Publisher as p");
	}    
    
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
		return "org.bookmarks.domain.Publisher";
	}
	
	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		PublisherSearchBean publisherSearchBean = (PublisherSearchBean) searchBean;
		Publisher p = publisherSearchBean.getPublisher();
		QueryBuilder queryBuilder = new QueryBuilder();

		//Build query
		queryBuilder.append(p.getName(), "p.name");
		
		query.append(queryBuilder.getQuery());

	}
	@Override
	public Publisher findBySimilarName(String publisherName) {
		Query query = getSessionFactory()
				.getCurrentSession()
				.createQuery("select new Publisher(p.id, p.name) from Publisher p where p.name like :publisherName");
		query.setParameter("publisherName", "%" + publisherName + "%");
		List<Publisher> publisherList = query.list();
		if(publisherList.isEmpty()) {
			return null;
		}
		return publisherList.get(0);
	}	
	
    @SuppressWarnings("unchecked")
    @Override
    public Collection<Publisher> searchByAjax(SearchBean searchBean) {
    	PublisherSearchBean publisherSearchBean = (PublisherSearchBean) searchBean;
    	Query query = sessionFactory.getCurrentSession()
    			.createQuery("select new Publisher(p.id, p.name) from Publisher p " +
    					"where p.name like '%:name$'");
    		query.setParameter("name", publisherSearchBean.getPublisher().getName());
    	return query.list();
    }	

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Publisher> getForAutoComplete(String searchString) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select new Publisher(p.id, p.name) from Publisher p " +
					"where p.name like '%" + searchString.trim() + "%'");
		return query.list();
	}
	
}
