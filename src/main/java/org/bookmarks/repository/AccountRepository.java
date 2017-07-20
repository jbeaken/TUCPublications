package org.bookmarks.repository;

import java.util.Collection;
import java.util.List;

import org.bookmarks.domain.CreditNote;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
@Transactional
public class AccountRepository {

    private SessionFactory sessionFactory;

    private Logger logger = LoggerFactory.getLogger(AccountRepository.class);

    @Autowired
    private CreditNoteRepository creditNoteRepository;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


	public void processCreditNote(CreditNote creditNote, Boolean incrementAccount) {
		Query query = null;
		
		if(creditNote.getStatus().equals("Primary Matched") || creditNote.getStatus().equals("Potential Primary Match") || creditNote.getStatus().equals("Club Account")) {
			 query = sessionFactory
		 		.getCurrentSession()
		 		.createQuery("update Customer c set c.bookmarksAccount.tsbMatch = :tsbMatch where c.id = :id")
		 		.setParameter("tsbMatch", creditNote.getTransactionDescription())
		 		.setParameter("id", creditNote.getCustomer().getId());
		 }

		if(creditNote.getStatus().equals("Secondary Matched") || creditNote.getStatus().equals("Potential Secondary Match")) {
			 query = sessionFactory
		 		.getCurrentSession()
		 		.createQuery("update Customer c set c.bookmarksAccount.tsbMatchSecondary = :tsbMatch where c.id = :id")
		 		.setParameter("tsbMatch", creditNote.getTransactionDescription())
		 		.setParameter("id", creditNote.getCustomer().getId());
		 }		 

		 query.executeUpdate();

		 logger.info("incrementAccount = {}", incrementAccount);

		 if(incrementAccount) {
 			query = sessionFactory
		 		.getCurrentSession()
		 		.createQuery("update Customer c set c.bookmarksAccount.currentBalance = c.bookmarksAccount.currentBalance + :amount where c.id = :id")
		 		.setParameter("amount", creditNote.getAmount())
		 		.setParameter("id", creditNote.getCustomer().getId());		

		 		 query.executeUpdate(); 	

		 }
		 
		 //Now save creditNote
		 creditNoteRepository.save( creditNote );
		 
		 
	}

	public CreditNote getCreditNote(String transactionReference) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select cn from CreditNote cn where transactionReference = :transactionReference")
				.setParameter("transactionReference", transactionReference);

				return (CreditNote) query.uniqueResult();
	}


	public CreditNote getSOCreditNote(CreditNote cn) {
		Query query = sessionFactory
				.getCurrentSession()
				.createQuery("select cn from CreditNote cn where c = :customer and cn.date = :date")
				.setParameter("customer", cn.getCustomer())
				.setParameter("date", cn.getDate());

				return (CreditNote) query.uniqueResult();
	}

	// @Override
	// public Collection<Author> findByNameLike(String name) {
	// 	Query query = sessionFactory
	// 			.getCurrentSession()
	// 			.createQuery("select a from Author a where name like :name")
	// 			.setParameter("name", name);
	// 	return query.list();
	// }
  //
	// @Override
	// public List<Author> findByStockItem(StockItem stockItem) {
	// 	Query query = sessionFactory
	// 			.getCurrentSession()
	// 			.createQuery("select new Author(a.id, a.name) from Author a join a.stockItems si where si.id = :stockItemId")
	// 			.setParameter("stockItemId", stockItem.getId());
	// 	return query.list();
	// }
  //
	// @Override
	// public void moveAndDelete(Author authorToDelete, Author authorToKeep) {
	// 	Query query = sessionFactory
	// 			.getCurrentSession()
	// 			.createSQLQuery("update stockitem_author set author_id = :idToKeep where author_id = :idToDelete")
	// 			.setParameter("idToKeep", authorToKeep.getId())
	// 			.setParameter("idToDelete", authorToDelete.getId());
	// 	query.executeUpdate();
  //
	// 	sessionFactory.getCurrentSession().delete(authorToDelete);
	// }
}
