package org.bookmarks.repository;

import org.hibernate.query.Query;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.bookmarks.controller.StaffSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Staff;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class StaffRepositoryImpl extends AbstractRepository<Staff> implements StaffRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
    	return new StringBuffer("select new Staff(e.id, e.name, e.telephone, e.email) " +
    			"from Staff as e ");
    }
	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(e) from Staff as e");
	}

    @Override
	public void appendGroupBy(StringBuffer query, SearchBean searchBean) {
    	query.append(" group by e");
	}



	@Override
	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		StaffSearchBean eventSearchBean = (StaffSearchBean) searchBean;
		QueryBuilder qb = new QueryBuilder();

		qb.appendAndEscape(eventSearchBean.getStaff().getName(), "e.name");

		query.append(qb.getQuery());
	}



	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public String getEntityName() {
		return "org.bookmarks.domain.Staff";
	}
}
