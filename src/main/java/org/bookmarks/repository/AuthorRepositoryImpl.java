package org.bookmarks.repository;

import java.util.Collection;
import java.util.List;

import org.bookmarks.controller.AuthorSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.domain.Author;
import org.bookmarks.domain.StockItem;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AuthorRepositoryImpl extends AbstractRepository<Author> implements AuthorRepository {

    private SessionFactory sessionFactory; 

    public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
    	return new StringBuffer("select a from Author as a");
    }

	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(a) from Author as a");
	}

	protected String getDefaultSortColumn() {
		return "a.name";
	}	

	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		AuthorSearchBean categorySearchBean = (AuthorSearchBean) searchBean;
		Author author = categorySearchBean.getAuthor();
		
		QueryBuilder queryBuilder = new QueryBuilder();

		//Build query
		if(author.getId() != null) {
			queryBuilder.append(author, "a.id");
		} else {
			queryBuilder.append(author.getName(), "a.name");
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
		return "org.bookmarks.domain.Author";
	}

	@Override
	public Author findByName(String name) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select a from Author a where name = :name")
				.setParameter("name", name);
		return (Author) query.uniqueResult();
	}

	@Override
	public Collection<Author> findByNameLike(String name) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select a from Author a where name like :name")
				.setParameter("name", name);
		return query.list();
	}

	@Override
	public List<Author> findByStockItem(StockItem stockItem) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select new Author(a.id, a.name) from Author a join a.stockItems si where si.id = :stockItemId")
				.setParameter("stockItemId", stockItem.getId());
		return query.list();
	}

	@Override
	public void moveAndDelete(Author authorToDelete, Author authorToKeep) {
		Query query = sessionFactory
				.getCurrentSession()
				.createSQLQuery("update stockitem_author set author_id = :idToKeep where author_id = :idToDelete")
				.setParameter("idToKeep", authorToKeep.getId())
				.setParameter("idToDelete", authorToDelete.getId());
		query.executeUpdate();
		
		sessionFactory.getCurrentSession().delete(authorToDelete);
	}
}
