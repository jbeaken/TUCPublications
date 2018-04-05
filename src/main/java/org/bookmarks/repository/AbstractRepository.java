package org.bookmarks.repository;

import java.util.Collection;
import java.util.List;



import org.bookmarks.controller.SaleOrReturnSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.AbstractEntity;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.StockItem;
import org.hibernate.query.Query;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractRepository<E> implements Repository<E> {

	protected boolean isCount = false;
	
	@Override
	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		
	}
	
	protected String escapeText(String text) {
		return text.replace("'", "''");
	}		
	
	@Override
	public String getEntityAlias() {
		return "";
	}	
	
	private StringBuffer selectClauseHQL = new StringBuffer("select e from " + getEntityName() + " as e");
	private StringBuffer countClauseHQL = new StringBuffer("select count(e) from " + getEntityName() + " as e");

    @SuppressWarnings("unchecked")
	@Override
    public Collection<E> search(SearchBean searchBean) {
    	isCount = true;
    	Integer searchResultCount = getSearchResultCount(searchBean);
    	searchBean.setSearchResultCount(searchResultCount);
    	isCount = false;
    	
        return getSearchQuery(searchBean).list();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Collection<E> searchByAjax(SearchBean searchBean) {
    	return null;
    }
    
    @Override
    public E getMinimal(Long id) {
    	return null;
    }
    
    
	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return countClauseHQL;
	}
	
	protected void appendSorting(StringBuffer query, SearchBean searchBean) {
		String sortColumn = searchBean.getSortColumn() == null ? getDefaultSortColumn() : searchBean.getSortColumn();
		
		query.append(" order by " 
				+ sortColumn 
				+ " "
				+ searchBean.getSortOrder());
	}	
	
	protected String getDefaultSortColumn() {
		return "1";
	}


	protected int getFirstResult(SearchBean searchBean) {
        int firstResult = 0;
        if(searchBean.getPage() != 1) {
        	firstResult = (searchBean.getPage() - 1) * searchBean.getPageSize();
        }
		return firstResult;
	}	
	
    protected Query getSearchQuery(SearchBean searchBean) {
    	StringBuffer query = getSelectClauseHQL(searchBean);
    	appendWhere(query, searchBean);
    	appendGroupBy(query, searchBean);
    	appendSorting(query, searchBean);
        Query q =
        		getSessionFactory().
        		getCurrentSession().
        		createQuery(query.toString());
        
        //Group By

        //Pagination
        appendPagination(q, searchBean);
		return q;
	}	

    @Override
	public void appendGroupBy(StringBuffer query, SearchBean searchBean) {
    	
	}


	private void appendPagination(Query query, SearchBean searchBean) {
		if(!searchBean.isExport()){
			query.setFirstResult(getFirstResult(searchBean));
	        query.setMaxResults(searchBean.getPageSize());    	
		}
	}


	@Override
	public Integer getSearchResultCount(SearchBean searchBean){
    	StringBuffer query = new StringBuffer(getCountClauseHQL(searchBean));
    	appendWhere(query, searchBean);
    	appendGroupBy(query, searchBean);
        Query q =
        		getSessionFactory().
        		getCurrentSession().
        		createQuery(query.toString());

        if(searchBean.getGroupBy()) {
        	return q.list().size();
        }
        
       Long noSearchResults = (Long) q.uniqueResult();
	   if(noSearchResults == null) noSearchResults = new Long(0);
       return noSearchResults.intValue();
	}
		
	

	@Override
	public void save(E e) {
		getSessionFactory().getCurrentSession().save(e);
	}
	
	@Override
	public E merge(E e) {
		return (E)getSessionFactory().getCurrentSession().merge(e);
	}
	
	@Override
	public void saveNote(E e) {
		AbstractEntity entity = (AbstractEntity) e;
		Query query = getSessionFactory()
				.getCurrentSession()
				.createQuery("update " + getEntityName() 
						+ " set note = :note" 
						+ " where id = :id");
		query.setParameter("note", entity.getNote());
		query.setParameter("id", entity.getId());
		int result = query.executeUpdate();
	}
	

	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
		return selectClauseHQL;
	}

	@Override
	public void saveOrUpdate(E e) {
		getSessionFactory().getCurrentSession().saveOrUpdate(e);
	}
	
	@Override
	public void update(E e) {
		getSessionFactory().getCurrentSession().update(e);
	}

	@Override
	public E get(E e) {
		AbstractEntity ae = (AbstractEntity) e;
		return (E) getSessionFactory().getCurrentSession().get(e.getClass().getName(), ae.getId());
	}
	
	@SuppressWarnings("unchecked")
	public E getByName(String name) {
		Query query = getSessionFactory().getCurrentSession().createQuery("select e from " + getEntityName()
				+ " as e where e.name = :name")
		.setParameter("name", name);
		
		List<E> items = query.list();
		
		if(items.isEmpty()) return null;
		return items.get(0);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public E get(Long id) {
		return (E) getSessionFactory().getCurrentSession().get(getEntityName(), id);
	}

	@Override
	public void delete(E e) {
		getSessionFactory().getCurrentSession().delete(e);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<E> getAll() {
		StringBuffer query = new StringBuffer("select e from " + getEntityName() + " as e");
		Collection<E> items = getSessionFactory().getCurrentSession()
		.createQuery(query.toString())
		.list();
		return items;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Collection<E> getAllSorted(String columnName, boolean isAscending) {
		StringBuffer query = new StringBuffer("select e from " + getEntityName()
				+ " as e order by " + columnName);
		if(isAscending) {
			query.append(" ASC");
		} else {
			query.append(" DESC");
		}
		Collection<E> items = getSessionFactory().getCurrentSession()
		.createQuery(query.toString())
		.list();
		return items;
	}


}
