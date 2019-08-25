package org.bookmarks.service;

import java.util.Collection;

import org.bookmarks.controller.SearchBean;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.StockItem;
import org.bookmarks.repository.Repository;

public interface Service<E>{
	void save(E e);
	
	void saveOrUpdate(E e);
	
	void saveNote(E e);
	
	void update(E e);
	
	E merge(E e);
	
	E getMinimal(Long id);
	
	E get(E e);
	
	E get(Long id);
	
	E getByName(String name);
	
	void delete(E e);
	
	Collection<E> getAll();
	
	Collection<E> getAllSorted(String columnName, boolean isAscending);
	
	Collection<E> search(SearchBean searchBean);
	
	Collection<E> searchByAjax(SearchBean searchBean);
	
	Repository<E> getRepository();
}
