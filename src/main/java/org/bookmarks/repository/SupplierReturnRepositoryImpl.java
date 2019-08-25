package org.bookmarks.repository;

import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.SupplierReturnSearchBean;
import org.bookmarks.domain.SupplierReturn;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;

@Repository
//@Transactional
public class SupplierReturnRepositoryImpl extends AbstractRepository<SupplierReturn> implements SupplierReturnRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select new SupplierReturn(sd.id, sd.status, sd.creationDate, sd.dateSentToSupplier, sd.returnsNumber, sd.supplier.name, sup.telephone1, sup.supplierAccount.accountNumber, count(sdl)) from SupplierReturn as sd "
						+ "left join sd.supplierReturnLine sdl "
            + "left join sdl.stockItem si "
						+ "join sd.supplier sup ");
    }
	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(sd) from SupplierReturn as sd join sd.supplierReturnLine sdl join sdl.stockItem si");
	}

	@Override
	public void appendGroupBy(StringBuffer query, SearchBean searchBean) {
    	query.append(" group by sd ");
	}

	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		SupplierReturnSearchBean deliverySearchBean = (SupplierReturnSearchBean) searchBean;

		QueryBuilder queryBuilder = new QueryBuilder();

		//Build query
		queryBuilder.append(deliverySearchBean.getSupplierReturn(), "sd.id");
		queryBuilder.append(deliverySearchBean.getSupplierReturn().getReturnsNumber(), "sd.returnsNumber");
		queryBuilder.append(deliverySearchBean.getStockItem().getTitle(), "si.title");
		queryBuilder.append(deliverySearchBean.getStockItem().getIsbn(), "si.isbn");
		queryBuilder.append(deliverySearchBean.getSupplierReturn().getSupplier(), "sd.supplier");

		query.append(queryBuilder.getQuery());

	}

	@Override
	public String getEntityName() {
		return "org.bookmarks.domain.SupplierReturn";
	}

}
