package org.bookmarks.repository;

import java.util.Collection;
import java.util.List;

import org.bookmarks.controller.SearchBean;
import org.bookmarks.domain.VTTransaction;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
@Transactional
public class VTTransactionRepository extends AbstractRepository<VTTransaction> {
	
	private SessionFactory sessionFactory;
	
    private Logger logger = LoggerFactory.getLogger(VTTransactionRepository.class);

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
  
	@Override
	public String getEntityName() {
		return "org.bookmarks.domain.VTTransaction";
	}

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
    	return new StringBuffer("select t from VTTransaction as t");
    }

	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(t) from VTTransaction as t");
	}
	
	protected String getDefaultSortColumn() {
		return "t.id";
	}	




}
