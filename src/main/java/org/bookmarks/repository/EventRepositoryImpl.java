package org.bookmarks.repository;

import org.hibernate.Query;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.bookmarks.controller.EventSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Event;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class EventRepositoryImpl extends AbstractRepository<Event> implements EventRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    
	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
    	return new StringBuffer("select new Event(e.id, e.name, e.type, e.startDate, e.endDate, e.note, sum(s.sellPrice * s.quantity)) " +
    			"from Event as e " +
    			"left join e.sales as s");
    }
	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(e) from Event as e");
	}    
	
    @Override
	public void appendGroupBy(StringBuffer query, SearchBean searchBean) {
    	query.append(" group by e");
	}
    
    

	@Override
	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		EventSearchBean eventSearchBean = (EventSearchBean) searchBean;
		QueryBuilder qb = new QueryBuilder();
		
		qb.append(eventSearchBean.getEvent().getName(), "e.name");
		qb.append(eventSearchBean.getEvent().getType());
		
		query.append(qb.getQuery());
	}



	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public String getEntityName() {
		return "org.bookmarks.domain.Event";
	}

	@Override
	public Collection<Event> getChipsEvents()  {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select e from Event e where e.startDate > :now and onWebsite = true");
		query.setParameter("now", cal.getTime());
		return query.list();
	}


}
