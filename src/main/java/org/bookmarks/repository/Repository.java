package org.bookmarks.repository;

import java.util.Collection;

import org.bookmarks.controller.SearchBean;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierDelivery;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;

public interface Repository<E> {
	void save(E e);
	
	void saveOrUpdate(E e);
	
	void saveNote(E e);
	
	void update(E e);
	
	E merge(E e);
	
	E get(E e);
	
	E get(Long id);
	
	E getByName(String name);
	
	void delete(E e);
	
	Collection<E> getAll();
	
	Collection<E> getAllSorted(String columnName, boolean isAscending);
	
	Collection<E> search(SearchBean searchBean);
	
	SessionFactory getSessionFactory();
	
	String getEntityName();
	
	void appendWhere(StringBuffer query, SearchBean searchBean);
	
	StringBuffer getCountClauseHQL(SearchBean searchBean);
	
	StringBuffer getSelectClauseHQL(SearchBean searchBean);
	
	Integer getSearchResultCount(SearchBean customerSearchBean);

	void appendGroupBy(StringBuffer query, SearchBean searchBean);

	String getEntityAlias();

	Collection<E> searchByAjax(SearchBean searchBean);

	E getMinimal(Long id);
}
