package org.bookmarks.repository;

import java.util.Collection;

import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.Customer;

import java.time.LocalDate;
import java.time.ZoneId;

import java.util.Date;

import javax.transaction.Transactional;

import java.math.BigDecimal;

import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class AccountRepository {

	private SessionFactory sessionFactory;

	@Autowired
	private CreditNoteRepository creditNoteRepository;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Collection<Customer> getAccountCustomers() {
		Query query = sessionFactory.getCurrentSession().createQuery("select c from Customer c where c.bookmarksAccount.accountHolder = true");

		return query.list();
	}

	public void processCreditNote(CreditNote creditNote, Boolean incrementAccount) {

		Query query = null;

		if (creditNote.getStatus().equals("Primary Matched") || creditNote.getStatus().equals("Potential Primary Match") || creditNote.getStatus().equals("Club Account")) {
			query = sessionFactory.getCurrentSession().createQuery("update Customer c set c.bookmarksAccount.tsbMatch = :tsbMatch where c.id = :id").setParameter("tsbMatch", creditNote.getTransactionDescription()).setParameter("id", creditNote.getCustomer().getId());
		}

		if (creditNote.getStatus().equals("Secondary Matched") || creditNote.getStatus().equals("Potential Secondary Match")) {
			query = sessionFactory.getCurrentSession().createQuery("update Customer c set c.bookmarksAccount.tsbMatchSecondary = :tsbMatch where c.id = :id").setParameter("tsbMatch", creditNote.getTransactionDescription()).setParameter("id", creditNote.getCustomer().getId());
		}

		query.executeUpdate();

		if (incrementAccount) {
			query = sessionFactory.getCurrentSession().createQuery("update Customer c set c.bookmarksAccount.currentBalance = c.bookmarksAccount.currentBalance + :amount where c.id = :id").setParameter("amount", creditNote.getAmount()).setParameter("id", creditNote.getCustomer().getId());

			query.executeUpdate();

		}

		creditNoteRepository.save(creditNote);
	}

	public void resetMonthlyPayments() {
		Query query = sessionFactory.getCurrentSession().createQuery("update Customer c set c.bookmarksAccount.amountPaidInMonthly = null");

		query.executeUpdate();
	}

	public void updateMonthlyPayments(Customer customer, BigDecimal amount, Date lastPaymentDate, Date firstPaymentDate) {
		Query query = sessionFactory.getCurrentSession().createQuery("update Customer c set c.bookmarksAccount.amountPaidInMonthly = :amount, c.bookmarksAccount.lastPaymentDate = :lastPaymentDate, c.bookmarksAccount.firstPaymentDate = :firstPaymentDate where c.id = :customerId").setParameter("amount", amount).setParameter("lastPaymentDate", lastPaymentDate)
				.setParameter("firstPaymentDate", firstPaymentDate).setParameter("customerId", customer.getId());

		query.executeUpdate();
	}

	public Date getLastPaymentDate(Customer customer) {
		Query query = sessionFactory.getCurrentSession().createQuery("select max(cn.date) from CreditNote cn where cn.customer.id = :customerId").setParameter("customerId", customer.getId());
		return (Date) query.uniqueResult();
	}

	public Date getLastCreditNoteDate() {
		Query query = sessionFactory.getCurrentSession().createQuery("select max(cn.date) from CreditNote cn where cn.transactionType = 'TRF'");
		return (Date) query.uniqueResult();
	}

	public Date getFirstPaymentDate(Customer customer) {
		Query query = sessionFactory.getCurrentSession().createQuery("select min(cn.date) from CreditNote cn where cn.customer.id = :customerId").setParameter("customerId", customer.getId());

		return (Date) query.uniqueResult();
	}

	public BigDecimal getMonthlyPayment(Customer customer, LocalDate startDate, LocalDate endDate) {
		Query query = sessionFactory.getCurrentSession().createQuery("select sum(cn.amount) from CreditNote cn where date between :startDate and :endDate and customer.id = :customerId").setParameter("startDate", Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant())).setParameter("endDate", Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
				.setParameter("customerId", customer.getId());

		return (BigDecimal) query.uniqueResult();
	}

	public CreditNote getCreditNote(String transactionReference) {
		Query query = sessionFactory.getCurrentSession().createQuery("select cn from CreditNote cn where transactionReference = :transactionReference").setParameter("transactionReference", transactionReference);

		return (CreditNote) query.uniqueResult();
	}

	public CreditNote getSOCreditNote(CreditNote cn) {
		Query query = sessionFactory.getCurrentSession().createQuery("select cn from CreditNote cn where c = :customer and cn.date = :date").setParameter("customer", cn.getCustomer()).setParameter("date", cn.getDate());

		return (CreditNote) query.uniqueResult();
	}

	// @Override
	// public Collection<Author> findByNameLike(String name) {
	// Query query = sessionFactory
	// .getCurrentSession()
	// .createQuery("select a from Author a where name like :name")
	// .setParameter("name", name);
	// return query.list();
	// }
	//
	// @Override
	// public List<Author> findByStockItem(StockItem stockItem) {
	// Query query = sessionFactory
	// .getCurrentSession()
	// .createQuery("select new Author(a.id, a.name) from Author a join a.stockItems
	// si where si.id = :stockItemId")
	// .setParameter("stockItemId", stockItem.getId());
	// return query.list();
	// }
	//
	// @Override
	// public void moveAndDelete(Author authorToDelete, Author authorToKeep) {
	// Query query = sessionFactory
	// .getCurrentSession()
	// .createNativeQuery("update stockitem_author set author_id = :idToKeep where
	// author_id = :idToDelete")
	// .setParameter("idToKeep", authorToKeep.getId())
	// .setParameter("idToDelete", authorToDelete.getId());
	// query.executeUpdate();
	//
	// sessionFactory.getCurrentSession().delete(authorToDelete);
	// }
}
