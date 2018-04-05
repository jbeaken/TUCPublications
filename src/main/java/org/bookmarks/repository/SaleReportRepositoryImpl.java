package org.bookmarks.repository;

import java.util.Collection;
import java.util.Date;

import org.bookmarks.controller.CustomerSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.bean.SaleReportBean;
import org.bookmarks.website.domain.Address;
import org.bookmarks.domain.BookmarksAccount;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.SupplierDelivery;
import org.bookmarks.report.bean.CategoryStockTakeBean;
import org.bookmarks.report.bean.PublisherStockTakeBean;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SaleReportRepositoryImpl implements SaleReportRepository {

    private SessionFactory sessionFactory;
    
    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

	@Override
	public SupplierDeliveryLine getLastSupplierDeliveryLine(StockItem stockItem) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select sdl " +
						"from SupplierDeliveryLine as sdl join sdl.stockItem as si " +
						"where si.id = :id order by sdl.id desc")
				.setParameter("id", stockItem.getId())
				.setMaxResults(1);
		return (SupplierDeliveryLine)query.uniqueResult();	
	}	

	@Override
	public SupplierDelivery getLastSupplierDelivery(StockItem stockItem) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select sd " +
						"from SupplierDelivery sd " +
						"join sd.supplierDeliveryLine as sdl " +
						"join sdl.stockItem as si " +
						"join sd.supplier as sup " +
						"where si.id = :id order by sd.id desc")
				.setParameter("id", stockItem.getId())
				.setMaxResults(1);
		return (SupplierDelivery)query.uniqueResult();	
	}	

	@Override
	public Collection<Date> getLastSaleDates(StockItem stockItem) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select sa.creationDate " +
						"from Sale sa join sa.stockItem as si " +
						"where si.id = :id order by sa.creationDate desc")
				.setParameter("id", stockItem.getId())
				.setMaxResults(3);
		return query.list();
	}    


	@Override
	public Collection<Sale> getLastSales(StockItem stockItem) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select sa " +
						"from Sale sa join sa.stockItem as si " +
						"left join sa.event as e " +
						"where si.id = :id order by sa.creationDate desc")
				.setParameter("id", stockItem.getId())
				.setMaxResults(3);
		return query.list();
	}	
    
	@SuppressWarnings("unchecked")
	@Override
	public Collection<CategoryStockTakeBean> getCategoryStockTakeBeans() {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select new org.bookmarks.report.bean.CategoryStockTakeBean(" +
						"c.name, sum(si.quantityInStock), " +
						"sum(si.quantityInStock * si.publisherPrice) as totalPublisherPrice, " +
						"sum(si.quantityInStock * si.sellPrice ) as totalSellPrice) " +
						"from StockItem si " +
						"join si.category c " +
						"group by c " +
						"having sum(si.quantityInStock) > 0");
		return query.list();
	}
	
    
	@SuppressWarnings("unchecked")
	@Override
	public Collection<PublisherStockTakeBean> getPublisherStockTakeBeans() {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select new org.bookmarks.report.bean.PublisherStockTakeBean(" +
						"p.name, sum(si.quantityInStock), " +
						"sum(si.quantityInStock * si.publisherPrice) as totalPublisherPrice, " +
						"sum(si.quantityInStock * si.sellPrice ) as totalSellPrice) " +
						"from StockItem si " +
						"join si.publisher p " +
						"group by p " +
						"having sum(si.quantityInStock) > 0 " +
						"order by p.name ");
		return query.list();
	}

	@Override
	public Collection<SupplierDeliveryLine> getLastSupplierDeliveryLines(StockItem stockItem) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select sdl " +
						"from SupplierDeliveryLine as sdl join sdl.stockItem as si " +
						"where si.id = :id order by sdl.id desc")
				.setParameter("id", stockItem.getId())
				.setMaxResults(10);
		return  query.list();	
	}	
}
