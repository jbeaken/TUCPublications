package org.bookmarks.repository;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.bookmarks.controller.InvoiceSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Availablity;
import org.bookmarks.domain.Binding;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemSales;
import org.bookmarks.domain.StockItemType;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.StockLevel;
import org.bookmarks.domain.Level;
import org.bookmarks.report.bean.DailyReportBean;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.bookmarks.controller.StockItemSearchBean;

@Repository
@Transactional
public class StockItemRepositoryImpl extends AbstractRepository<StockItem> implements StockItemRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

	@Override
	public Collection<StockItem> getKeepInStockItems() {
		Query query = sessionFactory
			.getCurrentSession()
			.createQuery("select si from StockItem si where si.quantityToKeepInStock > 0");
		return query.list();
	}   

	@Override
	public void updateForReorderReview(StockItem stockItem) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si "
						+ "set quantityInStock = :quantityInStock, "
						+ "quantityForMarxism = :quantityForMarxism, "
						+ "quantityToKeepInStock = :quantityToKeepInStock, "
						+ "quantityOnOrder = :quantityOnOrder, "
						+ "keepInStockLevel = :keepInStockLevel, "
						+ "putOnWebsite = :putOnWebsite, "
						+ "putImageOnWebsite = :putImageOnWebsite, "
						+ "category = :category, "
						+ "lastReorderReviewDate = :lastReorderReviewDate" +
						" where si.id = :id")
				.setParameter("id", stockItem.getId())
				.setParameter("quantityToKeepInStock", stockItem.getQuantityToKeepInStock())
				.setParameter("quantityOnOrder", stockItem.getQuantityOnOrder())
				.setParameter("quantityForMarxism", stockItem.getQuantityForMarxism())
				.setParameter("keepInStockLevel", stockItem.getKeepInStockLevel())
				.setParameter("putImageOnWebsite", stockItem.getPutImageOnWebsite())
				.setParameter("putOnWebsite", stockItem.getPutOnWebsite())
				.setParameter("category", stockItem.getCategory())
				.setParameter("keepInStockLevel", stockItem.getKeepInStockLevel())
				.setParameter("lastReorderReviewDate", new Date())
				.setParameter("quantityInStock", stockItem.getQuantityInStock());

		int result = query.executeUpdate();	
	} 
    
    public void updateImageFilename(StockItem stockItem) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si set imageFilename = :imageFilename, imageURL = :imageURL, putOnWebsite = :putOnWebsite, putImageOnWebsite = :putImageOnWebsite where si.id = :id")
				.setParameter("id", stockItem.getId())
				.setParameter("imageURL", stockItem.getImageURL())
				.setParameter("putImageOnWebsite", stockItem.getPutImageOnWebsite())
				.setParameter("putOnWebsite", stockItem.getPutOnWebsite())
				.setParameter("imageFilename", stockItem.getImageFilename());

		int result = query.executeUpdate();	    	
    }
    @Override
    public void buildIndex() {
    	Session session = sessionFactory.getCurrentSession();
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		try {
			fullTextSession.createIndexer().startAndWait();		
		} catch(InterruptedException e) {
			//Do nothing
		}
    }

	@Override
	public void updateTwentyTwelveSales(StockItem stockItem) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si set twentyTwelveSales = :twentyTwelveSales" +
						" where si.id = :id")
				.setParameter("id", stockItem.getId())
				.setParameter("twentyTwelveSales", stockItem.getTwentyTwelveSales());
		int result = query.executeUpdate();	
	}   
	
	@Override
	public void updateTwentyThirteenSales(StockItem stockItem) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si set twentyThirteenSales = :twentyThirteenSales" +
						" where si.id = :id")
				.setParameter("id", stockItem.getId())
				.setParameter("twentyThirteenSales", stockItem.getTwentyThirteenSales());
		int result = query.executeUpdate();	
	}  
	
	@Override
	public void updateTwentyFourteenSales(StockItem stockItem) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si set twentyFourteenSales = :twentyFourteenSales" +
						" where si.id = :id")
				.setParameter("id", stockItem.getId())
				.setParameter("twentyFourteenSales", stockItem.getTwentyFourteenSales());
		int result = query.executeUpdate();	
	}   	
	
	
	@Override
	public Collection<StockItem> searchIndex(StockItemSearchBean searchBean) {
		String q = searchBean.getQ();
		Session session = sessionFactory.getCurrentSession();
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		//Transaction tx = fullTextSession.beginTransaction();

		// create native Lucene query unsing the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextSession.getSearchFactory()
			.buildQueryBuilder().forEntity( StockItem.class ).get();
		org.apache.lucene.search.Query query = qb
		  .keyword()
		  //.onFields("title", "subtitle", "authors.name")
		  .onFields("title")
		  .matching(q)
		  .createQuery();

		// wrap Lucene query in a org.hibernate.Query
		org.hibernate.Query hibQuery = fullTextSession.createFullTextQuery(query, StockItem.class);

		// execute search
		List result = hibQuery.list();
		
    	searchBean.setSearchResultCount(100);		
		  
		//tx.commit();
		//session.close();
		
		return result;
	}
	
	@Override
	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		StockItemSearchBean stockItemSearchBean = (StockItemSearchBean) searchBean;
		StringBuffer clause = new StringBuffer("select count(distinct s) from StockItem as s ")
		.append("join s.category as cat ")
		.append("join s.publisher as pub ")
		.append("left join s.authors as a");
		if(stockItemSearchBean.getStockItem().getPublisher().getSupplier().getId() != null) {
			clause.append(" join pub.supplier su");
		}
		return clause;
	}

	@Override
	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
		StockItemSearchBean stockItemSearchBean = (StockItemSearchBean) searchBean; 
		StringBuffer clause =  new StringBuffer("select new StockItem(" +
				"s.id, " +
				"s.imageURL, " +
				"s.title, " +
				"s.note, " +
				"s.isbn, " +
				"s.binding, " +
				"s.publisherPrice, " +
				"s.sellPrice, " +
				"s.costPrice, " +
				"s.putOnWebsite, " +
				"s.putReviewOnWebsite, " +
				"s.putImageOnWebsite, " +
				"s.quantityInStock, " +
				"s.quantityOnLoan, " +
				"s.quantityOnOrder, " +
				"s.quantityForCustomerOrder, " +
				"s.quantityReadyForCustomer, " +
				"s.quantityToKeepInStock, " +
				"s.quantityForMarxism, " +
				"s.type, " +
				"s.twentyTwelveSales, " +
				"s.twentyThirteenSales, " +
				"s.publishedDate, " +
				"pub.id, " +
				"pub.name, " +
				"cat.id, " +
				"cat.name)" +
				" from StockItem as s " +
				"join s.category as cat " +
				"join s.publisher as pub " +
				"left join s.authors a");
		if(stockItemSearchBean.getStockItem().getPublisher().getSupplier().getId() != null) {
			clause.append(" join pub.supplier su");
		}
		return clause;
	}
	
    @Override
	public void appendGroupBy(StringBuffer query, SearchBean searchBean) {
    	if(!isCount) query.append(" group by s.id");
	}

	private boolean appendTitle(StockItemSearchBean stockItemSearchBean,
			StringBuffer query, boolean whereAlreadyAppended) {
		String title = escapeText(stockItemSearchBean.getStockItem().getTitle().trim());

		if(title != null && !title.isEmpty() && whereAlreadyAppended) {
			query.append(" and s.title like '%" + title + "%'");
		} else if(title != null && !title.isEmpty()){
			query.append(" where s.title like '%" + title + "%'");
			whereAlreadyAppended = true;
		}
		return whereAlreadyAppended;
	}

	private boolean appendAuthors(StockItemSearchBean stockItemSearchBean, 	StringBuffer query, boolean whereAlreadyAppended) {
		Long authorId = stockItemSearchBean.getAuthorId();
		String name = stockItemSearchBean.getAuthorName();
		if(name == null || name.isEmpty()) {
			stockItemSearchBean.setAuthorName(null);
			return whereAlreadyAppended; 
		}		
		if(authorId != null && whereAlreadyAppended) {
			query.append(" and a.id = " + authorId);
			return true;
		} else if(authorId != null){
			query.append(" where a.id = " + authorId);
			whereAlreadyAppended = true;
		}
		return whereAlreadyAppended;
	}

	private boolean appendBinding(StockItemSearchBean stockItemSearchBean, StringBuffer query, boolean whereAlreadyAppended) {
		Binding binding = stockItemSearchBean.getStockItem().getBinding();
		if(binding == null) return whereAlreadyAppended;
		if(whereAlreadyAppended) {
			query.append(" and s.binding = '" + binding + "'");
		} else {
			query.append(" where s.binding = '" + binding + "'");
			return true;
		}
		return whereAlreadyAppended;
	}

	private boolean appendAvailablity(Availablity availablity, StringBuffer query,
			boolean whereAlreadyAppended) {
		if(availablity == null) return whereAlreadyAppended;
		if(whereAlreadyAppended) {
			query.append(" and s.availability = '" + availablity + "'");
		} else {
			query.append(" where s.availability = '" + availablity + "'");
			return true;
		}
		return whereAlreadyAppended;
	}

	private boolean appendCategory(StockItemSearchBean stockItemSearchBean,
			StringBuffer query, boolean whereAlreadyAppended) {
		Long id = stockItemSearchBean.getStockItem().getCategory().getId();
		if(id == null) return whereAlreadyAppended;

		//Category criteria has been selected
		if(whereAlreadyAppended) {
			query.append(" and cat = " + id);
		} else {
			query.append(" where cat = " + id);
			return true;
		}
		return whereAlreadyAppended;
	}

	private boolean appendPublisher(StockItemSearchBean stockItemSearchBean, StringBuffer query, boolean whereAlreadyAppended) {
		Long id = stockItemSearchBean.getStockItem().getPublisher().getId();
		String name = stockItemSearchBean.getStockItem().getPublisher().getName();
		if(name == null || name.isEmpty()) {
			stockItemSearchBean.getStockItem().getPublisher().setId(null);
			return whereAlreadyAppended; 
		}
		if(id == null) return whereAlreadyAppended; //All is represented by -1

		//Category criteria has been selected
		if(whereAlreadyAppended) {
			query.append(" and pub = " + id);
		} else {
			query.append(" where pub = " + id);
			return true;
		}
		return whereAlreadyAppended;
	}
	
	private boolean appendSupplier(StockItemSearchBean stockItemSearchBean, StringBuffer query, boolean whereAlreadyAppended) {
		Long id = stockItemSearchBean.getStockItem().getPublisher().getSupplier().getId();
		String name = stockItemSearchBean.getStockItem().getPublisher().getSupplier().getName();
		if(name == null || name.isEmpty()) {
			stockItemSearchBean.getStockItem().getPublisher().getSupplier().setId(null);
			return whereAlreadyAppended; 
		}
		if(id == null) return whereAlreadyAppended; //All is represented by -1

		//Category criteria has been selected
		if(whereAlreadyAppended) {
			query.append(" and ((su = " + id + " and s.preferredSupplier.id is null) or s.preferredSupplier.id = " + id + ") ");
		} else {
			query.append(" where ((su = " + id + " and s.preferredSupplier.id is null) or s.preferredSupplier.id = " + id + ") ");
			return true;
		}
		return whereAlreadyAppended;
	}
	

	private boolean appendIsbn(StockItemSearchBean stockItemSearchBean, StringBuffer query) {
		String isbn = stockItemSearchBean.getStockItem().getIsbn();
		String stockItemSearchType = stockItemSearchBean.getIsbnSearchType();
		if(!isbn.isEmpty()) {
			if(isbn.length() == 13) {
				query.append(" where s.isbnAsNumber = " + isbn);
			} else {
				query.append(" where s.isbn like '%" + isbn + "'");
			} 
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<StockItem> getSupplierStockItems(Long supplierId, StockLevel stockLevel) {
		StringBuffer buffer = new StringBuffer("select si from StockItem as si left join si.publisher as p " +
				"where (p.supplier.id = :supplierId or si.preferredSupplier.id = :supplierId)");
		switch(stockLevel) {
			case IN_STOCK :
				buffer.append(" and si.quantityInStock > 0");
				break;
			case OUT_OF_STOCK :
				buffer.append(" and si.quantityInStock < 1");
				break;
			case BELOW_KEEP_IN_STOCK_LEVEL :
				buffer.append(" and si.quantityInStock < si.quantityToKeepInStock");
				break;								
		}

		Query query = sessionFactory.getCurrentSession()
				.createQuery(buffer.toString());
		query.setParameter("supplierId", supplierId);
		return query.list();
	}

	//Remove this!!
	@Override
	public StockItem get(Long id) {
		return (StockItem) sessionFactory.getCurrentSession().get(StockItem.class, id);

	}

	@SuppressWarnings("unchecked")
	public StockItem get(String isbn) {
		List<StockItem> list = sessionFactory.getCurrentSession()
			.createQuery("select s from StockItem as s where s.isbn = '" + isbn + "'")
			.list();
		StockItem stockItem = null;
		if(list.size() > 0) {
			stockItem = (StockItem)list.get(0);
		}
		return stockItem;
	}

	/**
	 * Why does this do three selects, one each
	 * on stockitem, category and publisher;
	 */
	@SuppressWarnings("unchecked")
	@Override
	public StockItem getByISBNAsNumber(Long isbn) {
		List<StockItem> list = sessionFactory.getCurrentSession()
				.createQuery("select new StockItem(s.id, s.imageURL, s.title, s.note, s.publisherPrice, s.sellPrice, s.type, s.keepInStockLevel, s.quantityToKeepInStock, s.quantityInStock, p.id, p.name) from StockItem as s join s.publisher as p where s.isbnAsNumber = " + isbn)
				.list();
		if(list.isEmpty()) return null;
		StockItem stockItem = list.get(0);
		stockItem.setIsbn(isbn.toString());
		return stockItem;
	}


	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public String getEntityName() {
		return "StockItem";
	}

	@Override
	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		StockItemSearchBean stockItemSearchBean = (StockItemSearchBean) searchBean;
		boolean whereAlreadyAppended = false;
		whereAlreadyAppended = appendIsbn(stockItemSearchBean, query);
		whereAlreadyAppended = appendCategory(stockItemSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendPublisher(stockItemSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendSupplier(stockItemSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendBinding(stockItemSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendAvailablity(stockItemSearchBean.getStockItem().getAvailability(), query, whereAlreadyAppended);
		whereAlreadyAppended = appendAuthors(stockItemSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendTitle(stockItemSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendKeepInStock(stockItemSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendAlwaysInStock(stockItemSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendKeepInStockLevel(stockItemSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendStaffPick(stockItemSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendIsOnWebsite(stockItemSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendStockType(stockItemSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendStockLevel(stockItemSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendMarxismStatus(stockItemSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendHideBookmarksPublications(stockItemSearchBean, query, whereAlreadyAppended);
	}
	
	private boolean appendMarxismStatus(StockItemSearchBean stockItemSearchBean, StringBuffer query, boolean whereAlreadyAppended) {
		Integer marxismStatus = stockItemSearchBean.getMarxismStatus();
		
		if(marxismStatus == null) return whereAlreadyAppended;
		
		if(marxismStatus == 1) { //For Marxism
			if(whereAlreadyAppended) {
				query.append(" and s.quantityForMarxism > 0");
			} else {
				query.append(" where s.quantityForMarxism > 0");
				return true;
			}
		}
		
		if(marxismStatus == 2) { //Wanted, but hasn't arrived, or fallen below stock
			if(whereAlreadyAppended) {
				query.append(" and (s.quantityForMarxism > 0 and s.quantityForMarxism > s.quantityInStock)");
			} else {
				query.append(" where (s.quantityForMarxism > 0 and s.quantityForMarxism > s.quantityInStock)");
				return true;
			}
		}
		if(marxismStatus == 3) { //Not for marxism (already decided not going to Marxism)
			if(whereAlreadyAppended) {
				query.append(" and s.quantityForMarxism < 0");
			} else {
				query.append(" where s.quantityForMarxism < 0");
				return true;
			}
		}	
		
		if(marxismStatus == 4) { //Undecided plus going to Marxism
			if(whereAlreadyAppended) {
				query.append(" and (s.quantityForMarxism is null or s.quantityForMarxism > -1)");
			} else {
				query.append(" where (s.quantityForMarxism is null or s.quantityForMarxism > -1)");
				return true;
			}
		}	
		
		if(marxismStatus == 5) { //Undecided only
			if(whereAlreadyAppended) {
				query.append(" and (s.quantityForMarxism is null or s.quantityForMarxism = 0)");
			} else {
				query.append(" where (s.quantityForMarxism is null or s.quantityForMarxism = 0)");
				return true;
			}
		}	
		
		if(marxismStatus == 6) { //Undecided only
			if(whereAlreadyAppended) {
				query.append(" and (s.quantityForMarxism > 4)");
			} else {
				query.append(" where (s.quantityForMarxism > 4)");
				return true;
			}
		}			

		return whereAlreadyAppended;
	}	


	private boolean appendAlwaysInStock(StockItemSearchBean stockItemSearchBean, StringBuffer query, boolean whereAlreadyAppended) {
		boolean alwaysInStock = stockItemSearchBean.isAlwaysInStock();
		if(alwaysInStock == false) return whereAlreadyAppended;
		if(whereAlreadyAppended) {
			query.append(" and s.alwaysInStock = true");
		} else {
			query.append(" where s.alwaysInStock = true");
			return true;
		}
		return whereAlreadyAppended;
	}
	
	private boolean appendHideBookmarksPublications(StockItemSearchBean stockItemSearchBean, StringBuffer query, boolean whereAlreadyAppended) {
		boolean hideBookmarksPublications = stockItemSearchBean.isHideBookmarks();
		if(hideBookmarksPublications == false) return whereAlreadyAppended;
		if(whereAlreadyAppended) {
			query.append(" and pub.id != 725");
		} else {
			query.append(" where pub.id != 725");
			return true;
		}
		return whereAlreadyAppended;
	}	

	private boolean appendIsOnWebsite(StockItemSearchBean stockItemSearchBean, StringBuffer query, boolean whereAlreadyAppended) {
		Boolean putOnWebsite = stockItemSearchBean.getStockItem().getPutOnWebsite();
		if(putOnWebsite == null) return whereAlreadyAppended;
		if(whereAlreadyAppended) {
			query.append(" and s.putOnWebsite = " + putOnWebsite);
		} else {
			query.append(" where s.putOnWebsite = " + putOnWebsite);
			return true;
		}
		return whereAlreadyAppended;
	}

	private boolean appendStockType(StockItemSearchBean stockItemSearchBean, StringBuffer query, boolean whereAlreadyAppended) {
		StockItemType type = stockItemSearchBean.getStockItem().getType();
		if(type == null) return whereAlreadyAppended;
		if(whereAlreadyAppended) {
			query.append(" and s.type = '" + type + "'");
		} else {
			query.append(" where s.type = '" + type + "'");
			return true;
		}
		return whereAlreadyAppended;
	}

	private boolean appendStockLevel(StockItemSearchBean stockItemSearchBean, StringBuffer query, boolean whereAlreadyAppended) {
		StockLevel stockLevel = stockItemSearchBean.getStockLevel();
		if(stockLevel == null) return whereAlreadyAppended;
		String join = null;
		if(whereAlreadyAppended) {
			join = " and ";
		} else {
			join = " where ";
		}
		switch(stockLevel) {
			case IN_STOCK :
				query .append(join + "s.quantityInStock > 0");
				break;
			case OUT_OF_STOCK :
				query .append(join + "s.quantityInStock < 1");
				break;
			case BELOW_KEEP_IN_STOCK_LEVEL :
				query .append(join + "s.quantityInStock < s.quantityToKeepInStock");
				break;								
		}			
		return true;
	}

	private boolean appendKeepInStockLevel(StockItemSearchBean stockItemSearchBean,	StringBuffer query, boolean whereAlreadyAppended) {
		//boolean keepInStock = stockItemSearchBean.isKeepInStock();
		//if(keepInStock == false) return whereAlreadyAppended;

		Level level = stockItemSearchBean.getKeepInStockLevel();
		if(level == null) return whereAlreadyAppended;
		if(whereAlreadyAppended) {
			query.append(" and s.keepInStockLevel = '" + level + "' ");
		} else {
			query.append(" where s.keepInStockLevel = '" + level + "' ");
		}		
		return true;
	}	


	private boolean appendKeepInStock(StockItemSearchBean stockItemSearchBean, StringBuffer query, boolean whereAlreadyAppended) {
		boolean keepInStock = stockItemSearchBean.isKeepInStock();
		if(keepInStock == false) return whereAlreadyAppended;
		if(whereAlreadyAppended) {
			query.append(" and s.quantityToKeepInStock > 0 ");
		} else {
			query.append(" where s.quantityToKeepInStock > 0");
			return true;
		}
		return whereAlreadyAppended;
	}
	



	private boolean appendStaffPick(StockItemSearchBean stockItemSearchBean,
			StringBuffer query, boolean whereAlreadyAppended) {
		boolean staffPick = stockItemSearchBean.getStockItem().getIsStaffPick();
		if(staffPick == false) return whereAlreadyAppended;
		if(whereAlreadyAppended) {
			query.append(" and s.isStaffPick = " + staffPick);
		} else {
			query.append(" where s.isStaffPick = " + staffPick);
			return true;
		}
		return whereAlreadyAppended;
	}
	
	public void toggleIsForMarxism(Long id, boolean isForMarxism) {
		Query query = sessionFactory
					.getCurrentSession()
					.createQuery("update StockItem set isForMarxism = :isForMarxism" +
				" where id = :id");
		query.setParameter("isForMarxism", isForMarxism);
		query.setParameter("id", id);
		int result = query.executeUpdate();
	}
/*
	private boolean appendIsForMarxism(StockItemSearchBean stockItemSearchBean,
			StringBuffer query, boolean whereAlreadyAppended) {
		boolean isForMarxism = stockItemSearchBean.getStockItem().getIsForMarxism();
		if(isForMarxism == false) return whereAlreadyAppended;
		if(whereAlreadyAppended) {
			query.append(" and s.isForMarxism= " + isForMarxism);
		} else {
			query.append(" where s.isisForMarxism = " + isForMarxism);
			return true;
		}
		return whereAlreadyAppended;
	}*/


	@Override
	public void updateQuantityInStock(StockItem stockItem, Long quantityChange) {
		Query query = sessionFactory
					.getCurrentSession()
					.createQuery("update StockItem set quantityInStock = quantityInStock + :quantityChange" +
				" where id = :id");
		query.setParameter("quantityChange", quantityChange);
		query.setParameter("id", stockItem.getId());
		int result = query.executeUpdate();
	}


	@Override
	public void updateImageURL(StockItem stockItem, String imageURL) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem set imageURL = :imageURL" +
			" where id = :id");
	query.setParameter("imageURL", imageURL);
	query.setParameter("id", stockItem.getId());
	int result = query.executeUpdate();

	}


	@Override
	public Long getNextID() {
		Long id = (Long)sessionFactory.getCurrentSession()
				.createQuery("select max(id) from StockItem")
				.uniqueResult();
		return id + 1;
	}


	@Override
	public void updateQuantityOnOrder(StockItem stockItem, Long quantityChange) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem set quantityOnOrder = :quantityOnOrder" +
			" where id = :id");
		query.setParameter("quantityOnOrder", stockItem.getQuantityOnOrder() + quantityChange);
		query.setParameter("id", stockItem.getId());
		int result = query.executeUpdate();
	}

	@Override
	public void updateQuantityOnOrderAbsolutely(StockItem stockItem, Long quantity) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem set quantityOnOrder = :quantityOnOrder" +
			" where id = :id");
		query.setParameter("quantityOnOrder", quantity);
		query.setParameter("id", stockItem.getId());
		int result = query.executeUpdate();
	}	

	@Override
	/**
	* Updates quanitities using relative amounts
	*/
	public void updateQuantities(StockItem stockItem,
			Long quantityInStock,
			Long quantityOnLoan,
			Long quantityOnOrder,
			Long quantityForCustomerOrder,
			Long quantityReadyForCustomer) {
		if(quantityInStock == null) quantityInStock = 0l;
		if(quantityOnLoan == null) quantityOnLoan = 0l;
		if(quantityOnOrder == null) quantityOnOrder = 0l;
		if(quantityForCustomerOrder == null) quantityForCustomerOrder = 0l;
		if(quantityReadyForCustomer == null) quantityReadyForCustomer = 0l;

		//For each except quantity, prevent going below 0
		if(quantityOnLoan + stockItem.getQuantityOnLoan() < 0) quantityOnLoan = 0l;
		if(quantityOnOrder + stockItem.getQuantityOnOrder() < 0) quantityOnOrder = 0l;
		if(quantityForCustomerOrder + stockItem.getQuantityForCustomerOrder() < 0) quantityForCustomerOrder = 0l;
		if(quantityReadyForCustomer + stockItem.getQuantityReadyForCustomer() < 0) quantityReadyForCustomer = 0l;

		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem " +
						"set quantityOnOrder = quantityOnOrder + :quantityOnOrder" +
						", quantityOnLoan = quantityOnLoan + :quantityOnLoan" +
						", quantityInStock = quantityInStock + :quantityInStock" +
						", quantityForCustomerOrder = quantityForCustomerOrder + :quantityForCustomerOrder" +
						", quantityReadyForCustomer = quantityReadyForCustomer + :quantityReadyForCustomer" +
						" where id = :id");
		query.setParameter("quantityInStock", quantityInStock);
		query.setParameter("quantityOnLoan", quantityOnLoan);
		query.setParameter("quantityOnOrder", quantityOnOrder);
		query.setParameter("quantityReadyForCustomer", quantityReadyForCustomer);
		query.setParameter("quantityForCustomerOrder", quantityForCustomerOrder);
		query.setParameter("id", stockItem.getId());
		query.executeUpdate();
	}


	@Override
	public void updateForSupplierDelivery(StockItem stockItem) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem set " +
						"quantityInStock = :quantityInStock " +
						",quantityOnOrder = :quantityOnOrder " +
						",quantityForCustomerOrder = :quantityForCustomerOrder " +
						",quantityReadyForCustomer = :quantityReadyForCustomer " +
						",discount = :discount " +
						",sellPrice = :sellPrice " +
						",costPrice = :costPrice " +
						",publisherPrice = :publisherPrice " +
			" where id = :id");
	query.setParameter("quantityInStock", stockItem.getQuantityInStock());
	query.setParameter("quantityOnOrder", stockItem.getQuantityOnOrder());
	query.setParameter("quantityForCustomerOrder", stockItem.getQuantityForCustomerOrder());
	query.setParameter("quantityReadyForCustomer", stockItem.getQuantityReadyForCustomer());
	query.setParameter("discount", stockItem.getDiscount());
	query.setParameter("sellPrice", stockItem.getSellPrice());
	query.setParameter("costPrice", stockItem.getCostPrice());
	query.setParameter("publisherPrice", stockItem.getPublisherPrice());
	query.setParameter("id", stockItem.getId());
	int result = query.executeUpdate();

	}


	@Override
	public StockItem getResearchStockItem() {
		Query query = sessionFactory.getCurrentSession().createQuery("select si from StockItem where si.id= 1000");
		return (StockItem) query.uniqueResult();
	}


	@Override
	public List<StockItem> getFrontPageStockItems() {
		Query query = sessionFactory.getCurrentSession().createQuery("select si from" +
				" StockItem as si where frontPageIndex is not null");
		return query.list();
	}


	@Override
	public StockItem getBookOfMonth() {
		Query query = sessionFactory.getCurrentSession().createQuery("select si from" +
				" StockItem as si join si.websiteInfo as w where w.bookOfTheMonth = true");
		return (StockItem) query.uniqueResult();
	}


	@Override
	public void updateLastReorderReviewDate(StockItem stockItem, Date date) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem set lastReorderReviewDate = :lastReorderReviewDate" +
			" where id = :id");
		query.setParameter("lastReorderReviewDate", date);
		query.setParameter("id", stockItem.getId());
		int result = query.executeUpdate();
	}


	@Override
	public void updatePreferredSupplier(StockItem stockItem, Supplier supplier) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem set preferredSupplier = :preferredSupplier" +
			" where id = :id");
	query.setParameter("preferredSupplier", supplier);
	query.setParameter("id", stockItem.getId());
	int result = query.executeUpdate();
		
	}


	@Override
	public void setQuantityInStock(StockItem stockItem, Long quantityInStock) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem set quantityInStock = :quantityInStock" +
			" where id = :id");
	query.setParameter("quantityInStock", quantityInStock);
	query.setParameter("id", stockItem.getId());
	int result = query.executeUpdate();
		
	}


	@Override
	public StockItem getByISBNAsNumberForStockTake(Long isbn) {
		StockItem stockItem = (StockItem) sessionFactory.getCurrentSession()
				.createQuery("select new StockItem(s.id, s.title, c.name) from StockItem as s " +
						"join s.category as c where s.isbnAsNumber = :isbn")
				.setParameter("isbn", isbn)
				.uniqueResult();
		stockItem.setIsbn(isbn.toString());
		return stockItem;
	}
	
	@Override
	public Collection<StockItem> getStockItemsBelowKeepInStockLevel() {
		Query query = sessionFactory.getCurrentSession().createQuery("select si from" +
				" StockItem as si where quantityToKeepInStock > quantityInStock and quantityToKeepInStock > 0 order by si.title");
		return query.list();
	}

	@Override
	public void resetAZSync() {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si set syncedWithAZ = false");
		query.executeUpdate();
		
	}


	@Override
	public void updateSyncedWithAZ(StockItem si, boolean syncedWithAZ) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si set syncedWithAZ = :syncedWithAZ where si.id = :id")
				.setParameter("syncedWithAZ", syncedWithAZ)
				.setParameter("id", si.getId());
		query.executeUpdate();
	}

	@Override
	public Collection<StockItem> getUnsynchedWithAZAndIsOnWebsite(Integer offset,	Integer noOfResults) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select si from StockItem si where syncedWithAZ = false and putOnWebsite = true")
				.setFirstResult(offset)
				.setMaxResults(noOfResults);
		Collection<StockItem> list = query.list();
		return list;
	}
	
	@Override
	public Collection<StockItem> getUnsynchedWithAZ(Integer offset,	Integer noOfResults) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select si from StockItem si where syncedWithAZ = false")
				.setFirstResult(offset)
				.setMaxResults(noOfResults);
		Collection<StockItem> list = query.list();
		return list;
	}	

	@Override
	public Collection<StockItem> getNoAZAuthors(Integer offset,	Integer noOfResults) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select si from StockItem si left join si.authors a where a is null")
				.setFirstResult(offset)
				.setMaxResults(noOfResults);
		Collection<StockItem> list = query.list();
		return list;
	}

	@Override
	public Collection<StockItem> getNoAZPublishers(Integer offset,	Integer noOfResults) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select si from StockItem si left join si.azPublisher p where p.id = 1")
				.setFirstResult(offset)
				.setMaxResults(noOfResults);
		Collection<StockItem> list = query.list();
		return list;
	}

	@Override
	public Collection<String> getISBNsWithPagination(Integer offset, Integer noOfResults) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select si.isbn from StockItem si")
				.setFirstResult(offset)
				.setMaxResults(noOfResults);
		Collection<String> list = query.list();
		return list;
	}

	@Override
	public Collection<StockItem> getStockItemsWithPagination(Integer offset, Integer noOfResults) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select si from StockItem si")
				.setFirstResult(offset)
				.setMaxResults(noOfResults);
		Collection<StockItem> list = query.list();
		return list;
	}

	@Override
	public StockItem getFullStockItemByISBNAsNumber(String isbn) {
		List<StockItem> list = sessionFactory.getCurrentSession()
				.createQuery("select s from StockItem as s where s.isbnAsNumber = " + isbn)
				.list();
		if(list.isEmpty()) return null;
		return list.get(0);
	}

	@Override
	public List<StockItem> getStickies(StockItemType type) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select si from StockItem si where type = :type and si.stickyTypeIndex is not null order by si.stickyTypeIndex desc")
				.setParameter("type", type);
		List<StockItem> list = query.list();
		return list;
	}

	@Override
	public List<StockItem> getBounciesAndStickies() {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select si from StockItem si where si.bouncyIndex is not null or si.stickyCategoryIndex is not null or si.stickyTypeIndex is not null");
		List<StockItem> list = query.list();
		return list;
	}

	@Override
	public void saveSticky(StockItem stockItem, Long index) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si set stickyTypeIndex = :stickyTypeIndex, putOnWebsite = true where si.id = :id")
				.setParameter("id", stockItem.getId())
				.setParameter("stickyTypeIndex", index);

		int result = query.executeUpdate();	
	}

	@Override
	public void resetStickies(StockItemType type) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si set stickyTypeIndex = null where si.type = :type")
				.setParameter("type", type);
		int result = query.executeUpdate();	
	}

	@Override
	public void resetBouncies() {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si set bouncyIndex = null");
		int result = query.executeUpdate();	
		
	}

	@Override
	public void saveBouncy(StockItem stockItem, Long index) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si set bouncyIndex = :bouncyIndex, putOnWebsite = true where si.id = :id")
				.setParameter("id", stockItem.getId())
				.setParameter("bouncyIndex", index);

		int result = query.executeUpdate();	
		
	}

	@Override
	public void removeFromWebsite(Long id) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si set putOnWebsite = false where si.id = :id")
				.setParameter("id", id);
		int result = query.executeUpdate();	
	}

	@Override
	public void putOnWebsite(Long id) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si set putOnWebsite = true where si.id = :id")
				.setParameter("id", id);
		int result = query.executeUpdate();	
	}

	@Override
	public DailyReportBean getDailyReportBean() {
		DailyReportBean dailyReportBean = new DailyReportBean();
		
		Query query = sessionFactory.getCurrentSession().createQuery("select count(*) from StockItem");
		Long noOfItemsInDatabase = (Long) query.uniqueResult();
		dailyReportBean.setNoOfItemsInDatabase(noOfItemsInDatabase);
		
		query = sessionFactory.getCurrentSession().createQuery("select count(*) from StockItem where putOnWebsite = true");
		Long noOfItemsOnWebsite = (Long) query.uniqueResult();
		dailyReportBean.setNoOfItemsOnWebsite(noOfItemsOnWebsite);
		
		query = sessionFactory.getCurrentSession().createQuery("select count(*) from StockItem where imageFilename is not null");
		Long noOfItemsWithImages = (Long) query.uniqueResult();
		dailyReportBean.setNoOfItemsWithImages(noOfItemsWithImages);		
		
		query = sessionFactory.getCurrentSession().createQuery("select sum(col.amount * si.sellPrice) from CustomerOrderLine col join col.stockItem si where col.source = 'WEB'");
		BigDecimal webOrderTotal = (BigDecimal) query.uniqueResult();
		dailyReportBean.setWebOrderTotal(webOrderTotal);	
		
		return dailyReportBean;
	}

	@Override
	public Collection<StockItem> getBouncies() {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select si from StockItem si where si.bouncyIndex is not null order by bouncyIndex desc");
		List<StockItem> list = query.list();
		return list;
	}

	@Override
	public Long getTotalSales(StockItem stockItem) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select sum(s.quantity) from Sale s where s.stockItem = :si")
				.setParameter("si", stockItem);
		Long total = (Long) query.uniqueResult();
		if(total == null) total = 0l;
		return total;
	}

	@Override
	public Long getSalesLastYear(StockItem stockItem) {
		Calendar yearAgo = new GregorianCalendar();
		yearAgo.setTime(new Date());
		yearAgo.add(Calendar.YEAR, -1);
		Date oneYearAgo = yearAgo.getTime();
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select sum(s.quantity) from Sale s where s.stockItem = :si and s.creationDate >= :oneYearAgo")
				.setParameter("oneYearAgo", oneYearAgo)
				.setParameter("si", stockItem);
		Long total = (Long) query.uniqueResult();
		if(total == null) total = 0l;
		return total;
	}

	@Override
	public boolean exists(Long id) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select id from StockItem si where si.id = :id")
				.setParameter("id", id);
		Long exists = (Long) query.uniqueResult();
		if(exists != null) return true;
		return false;
	}

	@Override
	public boolean exists(String isbn) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select id from StockItem si where si.isbnAsNumber = :isbnAsNumber")
				.setParameter("isbnAsNumber", Long.parseLong(isbn));
		Long exists = (Long) query.uniqueResult();
		if(exists != null) return true;
		return false;
	}

	@Override
	public void setIsAvailableAtSuppliers(String isbn, boolean availability) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si set availableAtSuppliers = :availableAtSuppliers" +
						" where si.isbnAsNumber = :isbnAsNumber")
				.setParameter("isbnAsNumber", Long.parseLong(isbn))
				.setParameter("availableAtSuppliers", availability);

		int result = query.executeUpdate();	
	}
	
	@Override
	public void setGardnersStockLevel(String isbn, Long gardnersStockLevel) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si set gardnersStockLevel = :gardnersStockLevel" +
						" where si.isbnAsNumber = :isbnAsNumber")
				.setParameter("isbnAsNumber", Long.parseLong(isbn))
				.setParameter("gardnersStockLevel", gardnersStockLevel);

		int result = query.executeUpdate();	
	}

	@Override
	public void resetGardnersStockLevel() {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem si set gardnersStockLevel = 0");
		int result = query.executeUpdate();	
	}

	@Override
	public Collection<StockItem> getExtras() {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select new StockItem(si.id, si.isbn, si.title, si.type) from StockItem si where si.isOnExtras = true order by si.type, si.title");
		return query.list();
	}

	@Override
	public void resetNewReleases() {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem set isNewRelease = false where isNewRelease = true");
		int result = query.executeUpdate();
	}

	@Override
	public void setNewRelease(String isbn, boolean isNewRelease) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update StockItem set isNewRelease = :isNewRelease" +
			" where isbnAsNumber = :isbn");
		query.setParameter("isNewRelease", isNewRelease);
		query.setParameter("isbn", Long.parseLong(isbn));
		int result = query.executeUpdate();
	}

	@Override
	public Collection<StockItem> unsold(InvoiceSearchBean invoiceSearchBean) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, -6);
		Date sixMonthsAgo = new Date();
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select si from Sale s join s.stockItem si where si.quantityInStock > 0  group by si having count(s) = 0")
				;//.setParameter("date", sixMonthsAgo);
		return query.list();
	}

	@Override
	public Collection<StockItem> getMerchandise() {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select si from StockItem si where si.merchandiseIndex is not null order by merchandiseIndex desc");
		List<StockItem> list = query.list();
		return list;
	}

	@Override
	public List<StockItemSales> getStockItemSales(StockItem stockItem) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select si from StockItemSales si where si.stockItem = :si");
		query.setParameter("si", stockItem);
			return query.list();
		
	}
}


