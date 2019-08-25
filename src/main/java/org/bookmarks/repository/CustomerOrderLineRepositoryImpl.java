package org.bookmarks.repository;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.bookmarks.controller.CustomerOrderLineSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.domain.BookmarksRole;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.StockItem;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CustomerOrderLineRepositoryImpl extends AbstractRepository<CustomerOrderLine> implements CustomerOrderLineRepository{

    private SessionFactory sessionFactory;

    private Logger logger = LoggerFactory.getLogger(CustomerOrderLineRepositoryImpl.class);

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
		return "org.bookmarks.domain.CustomerOrderLine";
	}

	@Override
	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(col) from CustomerOrderLine as col "
				+ "left join col.stockItem as s join col.customer as cus");
	}

	@Override
	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select new CustomerOrderLine(" +
							"col.id, " +
							"col.havePrintedLabel, " +
							"col.isSecondHand, " +
							"col.creationDate, " +
							"col.amount, " +
							"col.note, " +
							"cus.id, " +
							"cus.firstName," +
							"cus.lastName," +
							"cus.contactDetails.mobileNumber," +
							"cus.contactDetails.workNumber," +
              "cus.contactDetails.homeNumber," +
							"cus.contactDetails.email," +
							"col.status," +
							"col.deliveryType," +
							"col.paymentType," +
							"col.source," +
							"i.id," +
							"s.id," +
							"col.sellPrice," +
							"s.isbn," +
							"s.title," +
							"s.quantityInStock," +
							"s.imageURL," +
							"sup.name," +
							"psup.name," +
//							"case when s.preferredSupplier.id is null then sup.name else s.preferredSupplier.name end," +
							"case when s.preferredSupplier.id is null then sup.id else s.preferredSupplier.id end ) " +
				" from CustomerOrderLine as col" +
				" left join col.stockItem as s join col.customer as cus" +
				" left join col.invoice as i" +
				" left join s.publisher as p" +
				" left join p.supplier as sup" +
				" left join s.preferredSupplier as psup");
	}

	private boolean appendStatus(CustomerOrderLineSearchBean customerOrderSearchBean, StringBuffer query, boolean whereAlreadyAppended) {
		CustomerOrderLine col = customerOrderSearchBean.getCustomerOrderLine();
		BookmarksRole role = customerOrderSearchBean.getBookmarksRole();
		CustomerOrderLineStatus status = col.getStatus();
		Collection<CustomerOrderLineStatus> statuses = null;

		if(role != null) {
			if(role == BookmarksRole.BUYER) {
				statuses = col.getBuyerStatuses();
			} else if(role == BookmarksRole.MAILORDER) {
				statuses = col.getMailorderStatuses();
			}
		}

		if(statuses != null) {
			int i = 0;
			int size = statuses.size() - 1;
			if(whereAlreadyAppended) {
				query.append(" and col.status in (");
			} else {
				query.append(" where col.status in(");
			}
			for(CustomerOrderLineStatus cos : statuses) {
				query.append("'" + cos + "'");
				if(i < size) query.append(",");
				i++;
			}
			query.append(")");

			if(role == BookmarksRole.BUYER) {
				statuses = col.getBuyerStatuses();
			} else if(role == BookmarksRole.MAILORDER) {
				query.append(" and col.deliveryType = 'MAIL'");
			}

			return true;
		}

		if(status != null) {
			if(whereAlreadyAppended) {
				query.append(" and col.status = '" + status + "'");
				return true;
			} else {
				query.append(" where col.status = '" + status + "'");
				return true;
			}
		}
		return whereAlreadyAppended;
	}

	@Override
	public void appendWhere(StringBuffer query, SearchBean searchBean) {
	//TO-DO can throw exception if session has expired
		CustomerOrderLineSearchBean customerOrderSearchBean = (CustomerOrderLineSearchBean) searchBean;
		CustomerOrderLine customerOrderLine = customerOrderSearchBean.getCustomerOrderLine();
		boolean whereAlreadyAppended = false;
		whereAlreadyAppended = appendId(customerOrderLine, query, whereAlreadyAppended);
		if(whereAlreadyAppended) return;
		whereAlreadyAppended = appendCustomer(customerOrderLine, query, whereAlreadyAppended);
		whereAlreadyAppended = appendStatus(customerOrderSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendWebReference(customerOrderLine, query, whereAlreadyAppended);
		whereAlreadyAppended = appendPaymentType(customerOrderLine, query, whereAlreadyAppended);
		whereAlreadyAppended = appendDeliveryType(customerOrderLine, query, whereAlreadyAppended);
		whereAlreadyAppended = appendSource(customerOrderLine, query, whereAlreadyAppended);
		whereAlreadyAppended = appendStockItem(customerOrderLine, query, whereAlreadyAppended);
		whereAlreadyAppended = appendResearchText(customerOrderSearchBean, query, whereAlreadyAppended);
    whereAlreadyAppended = appendDates(customerOrderSearchBean, query, whereAlreadyAppended);
	}

	private boolean appendDates(CustomerOrderLineSearchBean customerOrderLineSearchBean,	StringBuffer query, boolean whereAlreadyAppended) {
		if(customerOrderLineSearchBean.getStartDate() != null) {
      		logger.debug(customerOrderLineSearchBean.getStartDate().toString());

    		java.sql.Timestamp sqlSD = new java.sql.Timestamp(customerOrderLineSearchBean.getStartDate().getTime());

			if(whereAlreadyAppended) {
				query.append(" and col.creationDate >= '" + sqlSD + "' ");
			} else {
				query.append(" where col.creationDate >= '" + sqlSD + "' ");
        		whereAlreadyAppended = true;
			}
		}

    if(customerOrderLineSearchBean.getEndDate() != null) {

    	java.sql.Timestamp sqlED = new java.sql.Timestamp(customerOrderLineSearchBean.getEndDate().getTime());
			if(whereAlreadyAppended) {
				query.append(" and col.creationDate <= '" + sqlED + "' ");
			} else {
				query.append(" where col.creationDate <= '" + sqlED + "' ");
        		whereAlreadyAppended = true;
			}
		}
		return whereAlreadyAppended;
	}

	private boolean appendWebReference(CustomerOrderLine customerOrderLine,	StringBuffer query, boolean whereAlreadyAppended) {
		if(customerOrderLine.getWebReference() != null && !customerOrderLine.getWebReference().trim().isEmpty()) {
			if(whereAlreadyAppended) {
				query.append(" and col.webReference = '" + customerOrderLine.getWebReference() + "' ");
			} else {
				query.append(" where col.webReference = '" + customerOrderLine.getWebReference() + "' ");
			}
			return true;
		}
		return whereAlreadyAppended;
	}

	private boolean appendSource(CustomerOrderLine customerOrderLine, StringBuffer query, boolean whereAlreadyAppended) {
		if(customerOrderLine.getSource() != null) {
			if(whereAlreadyAppended) {
				query.append(" and col.source = '" + customerOrderLine.getSource() + "' ");
			} else {
				query.append(" where col.source = '" + customerOrderLine.getSource() + "' ");
			}
			return true;
		}
		return whereAlreadyAppended;
	}

	private boolean appendResearchText(CustomerOrderLineSearchBean customerOrderSearchBean,	StringBuffer query, boolean whereAlreadyAppended) {
		if(customerOrderSearchBean.getResearchText() != null && !customerOrderSearchBean.getResearchText().trim().isEmpty()) {
			if(whereAlreadyAppended) {
				query.append(" and col.note like '%" + customerOrderSearchBean.getResearchText() + "%' and col.status = 'RESEARCH' ");
			} else {
				query.append(" where col.note like '%" + customerOrderSearchBean.getResearchText() + "%' and col.status = 'RESEARCH' ");
			}
			return true;
		}
		return whereAlreadyAppended;
	}

	private boolean appendId(CustomerOrderLine customerOrderLine,
			StringBuffer query, boolean whereAlreadyAppended) {
		//Special case, if id is set ignore other where conditions
		if(customerOrderLine.getId() != null) {
			query = query.append(" where col.id = " + customerOrderLine.getId());
			return true;
		}
		return false;
	}

	private boolean appendStockItem(CustomerOrderLine customerOrderLine,
			StringBuffer query, boolean whereAlreadyAppended) {
		if(customerOrderLine.getStockItem() == null) return whereAlreadyAppended;
		if(customerOrderLine.getId() != null) {
			if(whereAlreadyAppended) {
				query.append(" and s = " + customerOrderLine.getStockItem().getId());
				return true;
			} else {
				query.append(" where s = " + customerOrderLine.getStockItem().getId());
				return true;
			}
		} else {
			//Don't append title or ibsn if customer id is already set,
			String title = customerOrderLine.getStockItem().getTitle();
			if(title != null && !title.isEmpty()) {
				if(whereAlreadyAppended) {
					query.append(" and s.title like '%" + title + "%'");
					whereAlreadyAppended = true;
				} else {
					query.append(" where s.title like '%" + title + "%'");
					whereAlreadyAppended = true;
				}
			} else { //Don't bother expecting a title and an isbn, a nonsense search
				String isbn = customerOrderLine.getStockItem().getIsbn();
				if(!isbn.isEmpty()) {
					if(whereAlreadyAppended) {
						query.append(" and s.isbn like '%" + isbn + "%'");
						return true;
					} else {
						query.append(" where s.isbn like '%" + isbn + "%'");
						return true;
					}
				}
			}
		}
		return whereAlreadyAppended;
	}


	private boolean appendDeliveryType(CustomerOrderLine customerOrderLine,	StringBuffer query, boolean whereAlreadyAppended) {
		if(customerOrderLine.getDeliveryType() != null) {
			if(whereAlreadyAppended) {
				query.append(" and col.deliveryType = '" + customerOrderLine.getDeliveryType() + "'");
			} else {
				query.append(" where col.deliveryType = '" + customerOrderLine.getDeliveryType() + "'");
			}
			return true;
		}
		return whereAlreadyAppended;
	}

	private boolean appendPaymentType(CustomerOrderLine customerOrderLine,
			StringBuffer query, boolean whereAlreadyAppended) {
		if(customerOrderLine.getPaymentType() != null) {
			if(whereAlreadyAppended) {
				query.append(" and col.paymentType = '" + customerOrderLine.getPaymentType() + "'");
				return true;
			} else {
				query.append(" where col.paymentType = '" + customerOrderLine.getPaymentType() + "'");
				return true;
			}
		}
		return whereAlreadyAppended;
	}

	private boolean appendCustomer(CustomerOrderLine customerOrderLine,	StringBuffer query,
			 boolean whereAlreadyAppended) {
		Customer customer = customerOrderLine.getCustomer();
		if(customer.getId() != null) {
			if(whereAlreadyAppended) {
				query.append(" and col.customer = " + customer.getId());
				return true;
			} else {
				query.append(" where col.customer = " + customer.getId());
				return true;
			}
		} else {
			//Don't append these if customer id is already set,
			if(customer.getFirstName() != null && !customer.getFirstName().isEmpty()) {
				String firstName = customer.getFirstName().trim().replace("'", "''");
				if(whereAlreadyAppended) {
					query.append(" and cus.firstName like '%" + firstName + "%'");
					whereAlreadyAppended = true;
				} else {
					query.append(" where cus.firstName like '%" + firstName + "%'");
					whereAlreadyAppended = true;
				}
			}
			if(customer.getLastName() != null && !customer.getLastName().isEmpty()) {
				String lastName = customer.getLastName().trim().replace("'", "''");
				if(whereAlreadyAppended) {
					query.append(" and cus.lastName like '%" + lastName + "%'");
					return true;
				} else {
					query.append(" where cus.lastName like '%" + lastName + "%'");
					return true;
				}
			}
		}
		return whereAlreadyAppended;
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<CustomerOrderLine> findOpenOrdersForStockItem(StockItem stockItem) {
		StringBuffer buffer = new StringBuffer("select col from CustomerOrderLine as col ")
				.append("where col.stockItem = " + stockItem.getId())
				.append(" and col.status in ('")
				.append(CustomerOrderLineStatus.PENDING_ON_ORDER + "','")
				.append(CustomerOrderLineStatus.IN_STOCK + "','")
				.append(CustomerOrderLineStatus.OUT_OF_STOCK + "','")
				.append(CustomerOrderLineStatus.ON_ORDER + "')");

		Collection<CustomerOrderLine> customerOrderLine = getSessionFactory().
				getCurrentSession().
				createQuery(buffer.toString()).
				list();

		return customerOrderLine;
	}


	@Override
	public void updateStatus(Long id, CustomerOrderLineStatus status) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update CustomerOrderLine set status = :status" +
			" where id = :id");
		query.setParameter("status", status);
		query.setParameter("id", id);
		int result = query.executeUpdate();
	}

	@Override
	public void markAsPaid(Long customerOrderLineId) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update CustomerOrderLine set isPaid = true" +
			" where id = :id");
		query.setParameter("id", customerOrderLineId);
		int result = query.executeUpdate();
	}

	@Override
	public void updateToOrdered(Long customerOrderLineId, Long amount) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update CustomerOrderLine " +
						"set status = 'PENDING_ON_ORDER'," +
						"amount = :amount" +
			" where id = :id");
		query.setParameter("id", customerOrderLineId);
		query.setParameter("amount", amount);
		int result = query.executeUpdate();
	}

	@Override
	public List<CustomerOrderLine> get(CustomerOrderLineStatus status) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select col from CustomerOrderLine where " +
						"status = :status");
		query.setParameter("status", status);
		return query.list();
	}

	@Override
	public List<CustomerOrderLine> get(CustomerOrderLineStatus status,
			int daysOverdue) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select col from CustomerOrderLine col where " +
						"status = :status " +
						"and creationDate < :date");
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, daysOverdue);
		java.sql.Date date = new java.sql.Date(calendar.getTime().getTime());
		query.setParameter("date", date);
		query.setParameter("status", status);
		return query.list();
	}

	@Override
	public void wipeCCDetails() {
		Query query = sessionFactory.getCurrentSession().createQuery("update CustomerOrderLine set creditCard.securityCode = null,creditCard.creditCard4 = null, creditCard.creditCard3 = null "
				+ "where status in ('COMPLETE', 'CANCELLED')");
		query.executeUpdate();
	}

	@Override
	public void updateHasPrintedLabel(boolean havePrintedLabel, Long id) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update CustomerOrderLine set havePrintedLabel = :havePrintedLabel" +
			" where id = :id");
		query.setParameter("id", id);
		query.setParameter("havePrintedLabel", havePrintedLabel);

		int result = query.executeUpdate();

	}

}
