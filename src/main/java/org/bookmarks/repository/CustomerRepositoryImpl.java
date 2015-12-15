package org.bookmarks.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;

import org.bookmarks.controller.CustomerSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.website.domain.Address;
import org.bookmarks.domain.BookmarksAccount;
import org.bookmarks.website.domain.CreditCard;
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
public class CustomerRepositoryImpl extends AbstractRepository<Customer> implements CustomerRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
    	return new StringBuffer("select c from Customer as c");
    }
	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(c) from Customer as c");
	}

	protected String getDefaultSortColumn() {
		return "c.lastName";
	}

	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		CustomerSearchBean customerSearchBean = (CustomerSearchBean) searchBean;
		Customer c = customerSearchBean.getCustomer();
		BookmarksAccount ba = c.getBookmarksAccount();
		Address a = c.getAddress();

		QueryBuilder queryBuilder = new QueryBuilder();

		//Build query
		if(c.getId() != null) {
			queryBuilder.append(c, "c.id");
		} else {
			queryBuilder.append(ba.getSponsor(), "c.bookmarksAccount.sponsor");
			queryBuilder.append(ba.getPaysInMonthly(), "c.bookmarksAccount.paysInMonthly");
			queryBuilder.append(ba.getAccountHolder(), "c.bookmarksAccount.accountHolder");
			queryBuilder.append(c.getCustomerType(), "c.customerType");
			//queryBuilder.appendAndEscape(c.getFirstName(), "c.firstName");
			//queryBuilder.appendAndEscape(c.getLastName(), "c.lastName");
			//queryBuilder.append(c.getEmail(), "c.email");
			//queryBuilder.append(a.getAddress1(), "c.address.address1");
			//queryBuilder.append(a.getCity(), "c.address.city");
			//queryBuilder.append(a.getPostcode(), "c.address.postcode");
			String firstName = c.getFirstName();
			if(!firstName.isEmpty()) {
				String trimmedValue = c.getFirstName().trim().replace("'", "''");
				queryBuilder.append("c.firstName like '%" + trimmedValue + "%' or c.lastName like '%" + trimmedValue + "%'");
			}
			String address = a.getAddress1();
			if(!address.isEmpty()) {
				queryBuilder.append("c.address.address1 like '%" + a.getAddress1() + "%' or c.address.city = '%" + a.getAddress1() + "%' or c.address.postcode like '%" + a.getAddress1() + "%' or c.address.address2 like '%" + a.getAddress1() + "%' or c.address.address2 like '%" + a.getAddress1() + "%'");
			}
		}

		query.append(queryBuilder.getQuery());

	}

  @Override
  public void merge(Customer customerToKeep, Customer customerToDiscard) {
    Query query = sessionFactory
        .getCurrentSession()
        .createSQLQuery("update customer_customerorderline set customer_id = " + customerToKeep.getId() + " where customer_id = " + customerToDiscard.getId());

    query.executeUpdate();

    query = sessionFactory
        .getCurrentSession()
        .createSQLQuery("update SaleOrReturn set customer_id = " + customerToKeep.getId() + " where customer_id = " + customerToDiscard.getId());

    query.executeUpdate();

    query = sessionFactory
        .getCurrentSession()
        .createSQLQuery("update customerorderline set customer_id = " + customerToKeep.getId() + " where customer_id = " + customerToDiscard.getId());

    query.executeUpdate();

    query = sessionFactory
        .getCurrentSession()
        .createSQLQuery("update invoice set customer_id = " + customerToKeep.getId() + " where customer_id = " + customerToDiscard.getId());

    query.executeUpdate();

    query = sessionFactory
        .getCurrentSession()
        .createSQLQuery("delete from customer where id = " + customerToDiscard.getId());

    query.executeUpdate();
  }

	@Override
	public void debitAccount(Customer customer, BigDecimal amountChange) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update Customer c " +
					"set c.bookmarksAccount.currentBalance = c.bookmarksAccount.currentBalance + :amountChange " +
					"where c.id = :id");
	query.setParameter("amountChange", amountChange);
	query.setParameter("id", customer.getId());
	int result = query.executeUpdate();

	}

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public String getEntityName() {
		return "org.bookmarks.domain.Customer";
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Customer> getForAutoComplete(String searchString) {
		searchString = searchString.trim().replace("'", "''");  //trim and escape apostrophe
		int index = searchString.indexOf(",");
		Query query = null;
		//No comma
		if(index == -1) {
			query = sessionFactory
				.getCurrentSession()
				.createQuery("select new Customer(c.id, c.firstName, c.lastName, c.address.postcode) from Customer c " +
					"where c.lastName like '" + searchString + "%'");
		} else
		//Comma but haven't started entering firstname
		if(index == searchString.length() - 1) {
			searchString = searchString.substring(0, searchString.length() - 1);
			query = sessionFactory
				.getCurrentSession()
				.createQuery("select new Customer(c.id, c.firstName, c.lastName, c.address.postcode) from Customer c " +
					"where c.lastName = '" + searchString + "'");
		} else {
			//Comma and has started entering first name
			String surname = searchString.substring(0, index);
			String firstName = searchString.substring(index + 1).trim();
			query = sessionFactory
				.getCurrentSession()
				.createQuery("select new Customer(c.id, c.firstName, c.lastName, c.address.postcode) from Customer c " +
					"where c.lastName = '" + surname + "' and c.firstName like '" + firstName + "%'");

		}
		return query.list();
	}

	@Override
	public Customer getMinimal(Long id) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select new Customer(c.id, c.firstName, c.lastName, c.bookmarksAccount.accountHolder) " +
						"from Customer c where id = :id");
		query.setParameter("id", id);
		return (Customer) query.uniqueResult();
	}

	@Override
	public void updateCreditCard(CreditCard creditCard, Customer customer) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update Customer " +
				"set creditCard.creditCard1 = :creditCard1, " +
				"creditCard.creditCard2 = :creditCard2, " +
				"creditCard.creditCard3 = :creditCard3, " +
				"creditCard.creditCard4 = :creditCard4, " +
				"creditCard.expiryMonth = :expiryMonth, " +
				"creditCard.expiryYear = :expiryYear " +
				"where id = :id");

		query.setParameter("creditCard1", creditCard.getCreditCard1());
		query.setParameter("creditCard2", creditCard.getCreditCard2());
		query.setParameter("creditCard3", creditCard.getCreditCard3());
		query.setParameter("creditCard4", creditCard.getCreditCard4());
		query.setParameter("expiryMonth", creditCard.getExpiryMonth());
		query.setParameter("expiryYear", creditCard.getExpiryYear());
		query.setParameter("id", customer.getId());

		query.executeUpdate();
	}

	@Override
	public void updateBookmarksAccountInfo(Customer customer) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update Customer " +
				"set bookmarksAccount.currentBalance = :currentBalance, " +
				"bookmarksAccount.openingBalance = :openingBalance, " +
				"bookmarksAccount.accountHolder = true " +
				"where id = :id");

		query.setParameter("currentBalance", customer.getBookmarksAccount().getCurrentBalance());
		query.setParameter("openingBalance", customer.getBookmarksAccount().getOpeningBalance());
		query.setParameter("id", customer.getId());

		query.executeUpdate();

	}

	@Override
	@SuppressWarnings("unchecked")
	public Customer getByEmail(String email) {
		StringBuffer query = new StringBuffer("select c from Customer as c where lower(email) = :email");
		List<Customer> customers = getSessionFactory().getCurrentSession().createQuery(query.toString())
				.setParameter("email", email.toLowerCase())
				.list();
		if(customers.size() > 0) {
			return customers.get(0);
		}
		return null;
	}

	@Override
	public void updateEmail(Customer customer) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("update Customer " +
				"set email = :email " +
				"where id = :id");

		query.setParameter("email", customer.getContactDetails().getEmail());
		query.setParameter("id", customer.getId());

		query.executeUpdate();

	}
}
