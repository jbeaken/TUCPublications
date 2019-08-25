package org.bookmarks.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Iterator;

import javax.persistence.TypedQuery;

import org.bookmarks.controller.CustomerSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.website.domain.Address;
import org.bookmarks.domain.BookmarksAccount;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.StockItem;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ReorderReviewRepositoryImpl implements ReorderReviewRepository {

    private SessionFactory sessionFactory;
    
    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
	
	@Override
	public List<StockItem> getReorderReview() {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select new StockItem(si.id, " +
						"si.isbn, " +
						"si.title, " +
						"si.note," +
						"si.quantityInStock, " +
						"si.quantityOnOrder, " +
						"si.quantityToKeepInStock, " +
						"si.quantityForMarxism, " +
						"si.publisherPrice, " +
						"si.costPrice, " +
						"si.sellPrice, " +
						"si.twentyTwelveSales, " +
						"si.twentyThirteenSales, " +
						"si.twentyFourteenSales, " +
						"si.publishedDate, " +
						"si.putOnWebsite, " +
						"si.putImageOnWebsite, " +
						"case when ps.id is null then su.id else ps.id end, " +
						"p.name, " +
						"si.imageURL, " +
						"c.id, " +
						"c.name, " +
						"su.name) " +
						"from StockItem as si " +
						"join si.sales as sa " +
						"join si.publisher as p " +
						"join p.supplier as su " +
						"join si.category as c " +
						"left join si.preferredSupplier as ps " +
						"where sa.creationDate > si.lastReorderReviewDate " +
						"group by si");
		List<StockItem> stockItems = query.list();
		return stockItems;
	}
}
