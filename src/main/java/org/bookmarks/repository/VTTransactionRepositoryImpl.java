package org.bookmarks.repository;

import org.bookmarks.controller.SearchBean;
import org.bookmarks.domain.VTTransaction;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class VTTransactionRepositoryImpl extends AbstractRepository<VTTransaction> implements VTTransactionRepository {
	
	private SessionFactory sessionFactory;
	
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
