package org.bookmarks.repository;

import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.ReadingListSearchBean;
import org.bookmarks.domain.ReadingList;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ReadingListRepositoryImpl extends AbstractRepository<ReadingList> implements ReadingListRepository {

    private SessionFactory sessionFactory; 

    public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
    	return new StringBuffer("select r from ReadingList as r");
    }

	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(r) from ReadingList as r");
	}

	protected String getDefaultSortColumn() {
		return "r.name";
	}	

	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		ReadingListSearchBean readingListSearchBean = (ReadingListSearchBean) searchBean;
		ReadingList r = readingListSearchBean.getReadingList();
		
		QueryBuilder queryBuilder = new QueryBuilder();

		//Build query
		if(r.getId() != null) {
			queryBuilder.append(r, "r.id");
		} else {
			queryBuilder.append(r.getName(), "r.name");
		}
		
		query.append(queryBuilder.getQuery());
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
		return "org.bookmarks.domain.ReadingList";
	}

	@Override
	public ReadingList findByName(String name) {
		return null;
	}
}
