package org.bookmarks.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;

import org.bookmarks.controller.CustomerSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.bean.CustomerReportBean;
import org.bookmarks.website.domain.Address;
import org.bookmarks.domain.BookmarksAccount;
import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.StockItem;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CreditNoteRepositoryImpl extends AbstractRepository<CreditNote> implements CreditNoteRepository {

    private SessionFactory sessionFactory;
    
    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
  
	@Override
	public String getEntityName() {
		return "org.bookmarks.domain.CreditNote";
	}

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
    	return new StringBuffer("select cn from CreditNote as cn");
    }

	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(cn) from CreditNote as cn");
	}
	
	protected String getDefaultSortColumn() {
		return "cn.date";
	}		
	

	@Override
	public Collection<CreditNote> getCreditNotes(
			CustomerReportBean customerReportBean) {
		Query query = sessionFactory.getCurrentSession().createQuery("select cn from CreditNote cn " +
				"where cn.creationDate between :startDate and :endDate " +
						"and cn.customer = :customer)");
		query.setParameter("customer", customerReportBean.getCustomer());
		query.setParameter("startDate", customerReportBean.getStartDate());
		query.setParameter("endDate", customerReportBean.getEndDate());
		return query.list();
	}
}
