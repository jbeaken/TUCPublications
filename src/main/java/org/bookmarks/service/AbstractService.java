package org.bookmarks.service;

import java.io.IOException;
import java.util.Collection;

import org.apache.http.client.ClientProtocolException;
import org.bookmarks.controller.SearchBean;
import org.springframework.transaction.annotation.Transactional;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

@Transactional
public abstract class AbstractService<E> implements Service<E>{
	
	public E get(E e) {
		return getRepository().get(e);
	}
	
	public E get(Long id) {
		return getRepository().get(id);
	}
	
	public E getByName(String name) {
		return getRepository().getByName(name);
	}
	
	public E getMinimal(Long id) {
		return getRepository().getMinimal(id);
	}
	
	public void saveNote(E e){
		getRepository().saveNote(e);
	}
	
	public void save(E e) {
		getRepository().save(e);
	}
	
	public E merge(E e) {
		return getRepository().merge(e);
	}	
	public void saveOrUpdate(E e) {
		getRepository().saveOrUpdate(e);
	}
	
	public void update(E e) {
		getRepository().update(e);
	}
	
	public Collection<E> search(SearchBean searchBean) {
		return getRepository().search(searchBean);
	}	
	
	public Collection<E> searchByAjax(SearchBean searchBean) {
		return getRepository().searchByAjax(searchBean);
	}	
	
	public Collection<E> getAllSorted(String columnName, boolean isAscending) {
		return getRepository().getAllSorted(columnName, isAscending);
	}
	
	public void delete(E e) {
		getRepository().delete(e);
	}	

	public Collection<E> getAll() {
		return getRepository().getAll();
	}	
}
