package org.bookmarks.repository;

import java.util.Collection;
import java.util.List;

import org.bookmarks.controller.InvoiceSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.bean.CustomerReportBean;
import org.bookmarks.domain.Customer;
import org.bookmarks.report.bean.InvoiceReportBean;
import org.bookmarks.website.domain.DeliveryType;
import org.bookmarks.domain.Invoice;
import org.bookmarks.domain.StockItem;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class InvoiceRepositoryImpl extends AbstractRepository<Invoice> implements InvoiceRepository {

    private SessionFactory sessionFactory;

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
		return "org.bookmarks.domain.Invoice";
	}

	@Override
	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
//		return new StringBuffer("select 1");
		return new StringBuffer("select count(i) from Invoice as i "
				+ "left join i.sales as sal left join sal.stockItem as si join i.customer as cus");
	}

	@Override
	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select new Invoice(" +
				"i.id, " +
				"i.creationDate, " +
				"i.isProforma," +
				"i.paid," +
				"i.deliveryType," +
				"i.note, " +
				"i.totalPrice, " +
				"i.secondHandPrice, " +
				"i.serviceCharge, " +
				"cus.id, " +
				"cus.firstName, " +
				"cus.lastName, " +
				"cus.contactDetails.mobileNumber, " +
				"cus.contactDetails.workNumber, " +
				"cus.contactDetails.homeNumber) " +
				"from Invoice as i " +
				"left join i.sales as sal " +
				"left join sal.stockItem as si " +
				"left join i.customer as cus");
	}

    @Override
	public void appendGroupBy(StringBuffer query, SearchBean searchBean) {
    		query.append(" group by i.id");
	}

	@Override
	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		InvoiceSearchBean invoiceSearchBean = (InvoiceSearchBean) searchBean;
		boolean whereAlreadyAppended = false;
		whereAlreadyAppended = appendId(invoiceSearchBean, query, whereAlreadyAppended);
		if(whereAlreadyAppended) {
			//Don't add other criteria if a valid invoice number is being searched for
			return;
		}
		whereAlreadyAppended = appendCustomer(invoiceSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendStockItem(invoiceSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendIsPaid(invoiceSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendIsProforma(invoiceSearchBean, query, whereAlreadyAppended);
	}

private boolean appendIsPaid(InvoiceSearchBean invoiceSearchBean, StringBuffer query, boolean whereAlreadyAppended) {
		Boolean paid = invoiceSearchBean.getInvoice().getPaid();
		if(paid != null && paid) {
			if(whereAlreadyAppended) {
				query.append(" and i.paid = " + paid);
			} else {
				query.append(" where i.paid = " + paid);
			}
			return true;
		}
		return false;
	}

private boolean appendIsProforma(InvoiceSearchBean invoiceSearchBean, StringBuffer query, boolean whereAlreadyAppended) {
		Boolean isProforma = invoiceSearchBean.getInvoice().getIsProforma();
		if(isProforma != null && isProforma) {
			if(whereAlreadyAppended) {
				query.append(" and i.isProforma = " + isProforma);
			} else {
				query.append(" where i.isProforma = " + isProforma);
			}
			return true;
		}
		return false;
	}

private boolean appendId(InvoiceSearchBean invoiceSearchBean, StringBuffer query, boolean whereAlreadyAppended) {
		Long id = invoiceSearchBean.getInvoice().getId();
		if(id != null) {
			query.append(" where i.id = " + id);
			return true;
		}
		return false;
	}
	private boolean appendStockItem(InvoiceSearchBean invoiceSearchBean, StringBuffer query, boolean whereAlreadyAppended) {
		StockItem stockItem = invoiceSearchBean.getStockItem();
		if(stockItem.getId() != null) {
			if(whereAlreadyAppended) {
				query.append(" and si = " + stockItem.getId());
				return true;
			} else {
				query.append(" where si = " + stockItem.getId());
				return true;
			}
		} else {
			//Don't append title or ibsn if id is already set,
			String title = stockItem.getTitle();
			if(!title.isEmpty()) {
				if(whereAlreadyAppended) {
					query.append(" and si.title like '%" + title + "%'");
					whereAlreadyAppended = true;
				} else {
					query.append(" where si.title like '%" + title + "%'");
					whereAlreadyAppended = true;
				}
			} else { //Don't bother expecting a title and an isbn, a nonsense search
				String isbn = stockItem.getIsbn();
				if(!isbn.isEmpty()) {
					if(whereAlreadyAppended) {
						query.append(" and si.isbn like '%" + isbn + "%'");
						return true;
					} else {
						query.append(" where si.isbn like '%" + isbn + "%'");
						return true;
					}
				}
			}
		}
		return whereAlreadyAppended;
	}


	private boolean appendCustomer(InvoiceSearchBean invoiceSearchBean,	StringBuffer query,
			 boolean whereAlreadyAppended) {
		Customer customer = invoiceSearchBean.getInvoice().getCustomer();
		if(customer == null) return whereAlreadyAppended;
		if(customer.getId() != null) {
			if(whereAlreadyAppended) {
				query.append(" and i.customer = " + customer.getId());
				return true;
			} else {
				query.append(" where i.customer = " + customer.getId());
				return true;
			}
		} else {
			//Don't append these if customer id is already set,
			if(customer.getFirstName() != null && !customer.getFirstName().isEmpty()) {
				if(whereAlreadyAppended) {
					query.append(" and cus.firstName like '%" + customer.getFirstName().replace("'", "''") + "%'");
					whereAlreadyAppended = true;
				} else {
					query.append(" where cus.firstName like '%" + customer.getFirstName().replace("'", "''") + "%'");
					whereAlreadyAppended = true;
				}
			}
			if(customer.getFirstName() != null && !customer.getLastName().isEmpty()) {
				if(whereAlreadyAppended) {
					query.append(" and cus.lastName like '%" + customer.getLastName().replace("'", "''") + "%'");
					return true;
				} else {
					query.append(" where cus.lastName like '%" + customer.getLastName().replace("'", "''") + "%'");
					return true;
				}
			}
		}
		return whereAlreadyAppended;
	}

	@Override
	public Collection<Invoice> getInvoiceReport(CustomerReportBean customerReportBean) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select i " +
						"from Invoice i " +
						"join i.sales as sales " +
						"where i.creationDate between :startDate and :endDate " +
						"and i.customer = :customer " +
						"and paid = false group by i");
		query.setParameter("customer", customerReportBean.getCustomer());
		query.setParameter("startDate", customerReportBean.getStartDate());
		query.setParameter("endDate", customerReportBean.getEndDate());
		return query.list();
	}

	@Override
	public Collection<InvoiceReportBean> getInvoiceReportBeans(InvoiceSearchBean invoiceSearchBean) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select new org.bookmarks.report.bean.InvoiceReportBean(c.id, "
						+ "c.firstName, "
						+ "c.lastName, "
						+ "case when sum(s.quantity * (1 - s.discount/100) * s.sellPrice) + (i.secondHandPrice) + (i.serviceCharge) is null "
						+ "then (i.secondHandPrice + i.serviceCharge) "
						+ "else (sum(s.quantity * (1- s.discount/100) * s.sellPrice) + i.secondHandPrice + i.serviceCharge) end, "
						+ "sum((case when s.sellPrice is null then 0.0 else s.sellPrice end) * (case when s.vat is null then 0.0 else s.vat end) / 100.0 ) * quantity)"
						+ "from Invoice i  join i.customer c left join i.sales as s "
						+ "where i.creationDate between :startDate and :endDate "
						+ "and i.paid = false and i.isProforma = false "
						+ "group By c order by c.lastName");
		query.setParameter("startDate", invoiceSearchBean.getStartDate());
		query.setParameter("endDate", invoiceSearchBean.getEndDate());
		return query.list();
	}

	@Override
	public List<Invoice> getAllForCsv() {
		Query query = sessionFactory.getCurrentSession()
				.createQuery("select 'I', sa.quantity, sa.discount, sa.sellPrice, sa.vat,  sa.creationDate, si.id, i.customer.id, i.creationDate from Invoice i join i.sales sa join sa.stockItem si");
		return query.list();
	}

}
