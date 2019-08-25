package org.bookmarks.repository;

import java.util.Date;
import java.util.List;





import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.CategorySearchBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.StockItem;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CategoryRepositoryImpl extends AbstractRepository<Category> implements CategoryRepository {

    private SessionFactory sessionFactory; 

    public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
    	return new StringBuffer("select c from Category as c left join c.parent as p");
    }

	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(c) from Category as c");
	}

	protected String getDefaultSortColumn() {
		return "c.name";
	}	

	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		CategorySearchBean categorySearchBean = (CategorySearchBean) searchBean;
		Category c = categorySearchBean.getCategory();
		
		QueryBuilder queryBuilder = new QueryBuilder();

		//Build query
		if(c.getId() != null) {
			queryBuilder.append(c, "c.id");
		} else {
			queryBuilder.append(c.getName(), "c.name");
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
		return "org.bookmarks.domain.Category";
	}

	@Override
	public List<StockItem> getStickies(Long id) {
		Query query = sessionFactory.getCurrentSession().createQuery("select si "
				+ " from StockItem si where si.stickyCategoryIndex is not null and si.category.id = :id order by si.stickyCategoryIndex desc")
				.setParameter("id", id);
		return query.list();
	}

	@Override
	public void saveSticky(StockItem stockItem, Long index) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si set stickyCategoryIndex = :stickyCategoryIndex where si.id = :id")
				.setParameter("id", stockItem.getId())
				.setParameter("stickyCategoryIndex", index);

		int result = query.executeUpdate();	
	}

	@Override
	public void resetStickies(Category category) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si set stickyCategoryIndex = null where si.category = :category")
				.setParameter("category", category);

		int result = query.executeUpdate();	
	}
}
