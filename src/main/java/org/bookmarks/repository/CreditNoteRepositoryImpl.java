package org.bookmarks.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;

import org.bookmarks.controller.CreditNoteSearchBean;
import org.bookmarks.controller.CustomerSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.bean.CustomerReportBean;
import org.bookmarks.website.domain.Address;
import org.bookmarks.domain.BookmarksAccount;
import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.StockItem;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CreditNoteRepositoryImpl extends AbstractRepository<CreditNote> implements CreditNoteRepository {

	private SessionFactory sessionFactory;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public String getEntityName() {
		return "org.bookmarks.domain.CreditNote";
	}

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select cn from CreditNote as cn join cn.customer c");
	}

	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(cn) from CreditNote as cn join cn.customer c");
	}

	protected String getDefaultSortColumn() {
		return "cn.date";
	}

	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		CreditNoteSearchBean customerSearchBean = (CreditNoteSearchBean) searchBean;
		CreditNote cn = customerSearchBean.getCreditNote();
		Customer c = cn.getCustomer();

		QueryBuilder queryBuilder = new QueryBuilder();

		// Build query
		if (c != null && c.getId() != null) {
			queryBuilder.append(c.getId(), "c.id");
		} else {
			// queryBuilder.append(ba.getPaysInMonthly(),
			// "c.bookmarksAccount.paysInMonthly");
			// queryBuilder.append(ba.getAccountHolder(),
			// "c.bookmarksAccount.accountHolder");
			// queryBuilder.append(c.getCustomerType(), "c.customerType");
			// queryBuilder.appendAndEscape(c.getFirstName(), "c.firstName");
			// queryBuilder.appendAndEscape(c.getLastName(), "c.lastName");
			// queryBuilder.append(c.getEmail(), "c.email");
			// queryBuilder.append(a.getAddress1(), "c.address.address1");
			// queryBuilder.append(a.getCity(), "c.address.city");
			// queryBuilder.append(a.getPostcode(), "c.address.postcode");

			if (c != null) {
				String firstName = c.getFirstName();
				String lastName = c.getLastName();

				if (firstName != null && !firstName.isEmpty()) {
					String trimmedValue = c.getFirstName().trim().replace("'", "''");
					queryBuilder.append("c.firstName like '%" + trimmedValue + "%'");
				}

				if (lastName != null && !lastName.isEmpty()) {
					String trimmedValue = lastName.trim().replace("'", "''");
					queryBuilder.append("c.lastName like '%" + trimmedValue + "%'");
				}
			}
		}
		
		if(cn.getTransactionReference() != null && !cn.getTransactionReference().isEmpty()) {
			String trimmedValue = cn.getTransactionReference().trim().replace("'", "''");
			queryBuilder.append("cn.transactionReference like '%" + trimmedValue + "%'");
		}
		
		if(cn.getTransactionDescription() != null && !cn.getTransactionDescription().isEmpty()) {
			String trimmedValue = cn.getTransactionDescription().trim().replace("'", "''");
			queryBuilder.append("cn.transactionDescription like '%" + trimmedValue + "%'");
		}		

		query.append(queryBuilder.getQuery());
	}

	@Override
	public Collection<CreditNote> getCreditNotes(CustomerReportBean customerReportBean) {
		Query query = sessionFactory.getCurrentSession().createQuery("select cn from CreditNote cn " + "where cn.date between :startDate and :endDate " + "and cn.customer = :customer)");
		query.setParameter("customer", customerReportBean.getCustomer());
		query.setParameter("startDate", customerReportBean.getStartDate());
		query.setParameter("endDate", customerReportBean.getEndDate());
		return query.list();
	}

	@Override
	public BigDecimal getOutgoings() {
		Query query = sessionFactory.getCurrentSession().createQuery("select sum(amount) from CreditNote cn " + "where cn.customer.id = 31245");
		return (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getIncomings() {
		Query query = sessionFactory.getCurrentSession().createQuery("select sum(amount) from CreditNote cn " + "where cn.customer.id != 31245");
		return (BigDecimal) query.uniqueResult();
	}
}
