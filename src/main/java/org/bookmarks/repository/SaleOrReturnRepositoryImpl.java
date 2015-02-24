package org.bookmarks.repository;

import java.sql.Date;

import org.bookmarks.controller.SaleOrReturnSearchBean;
import org.bookmarks.controller.SaleOrReturnSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.domain.Customer;
import org.bookmarks.website.domain.DeliveryType;
import org.bookmarks.domain.SaleOrReturn;
import org.bookmarks.domain.SaleOrReturn;
import org.bookmarks.domain.SaleOrReturnStatus;
import org.bookmarks.domain.StockItem;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SaleOrReturnRepositoryImpl extends AbstractRepository<SaleOrReturn> implements SaleOrReturnRepository {


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
	public StringBuffer getCountClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select count(s) from SaleOrReturn as s "
				+ "left join s.saleOrReturnOrderLines as sor left join sor.stockItem as st join s.customer as cus");
	}

	@Override
	public StringBuffer getSelectClauseHQL(SearchBean searchBean) {
		return new StringBuffer("select new SaleOrReturn(s.id, s.creationDate, s.saleOrReturnStatus, s.returnDate, s.note, cus, sum(sor.sellPrice * sor.amount), sum(sor.amount)) "
				+ "from SaleOrReturn as s "
				+ "left join s.saleOrReturnOrderLines as sor "
				+ "left join sor.stockItem as st "
				+ "left join s.customer as cus ");
	}

    @Override
	public void appendGroupBy(StringBuffer query, SearchBean searchBean) {
    	query.append(" group by s");
	}

	@Override
	public void appendWhere(StringBuffer query, SearchBean searchBean) {
		SaleOrReturnSearchBean saleOrReturnSearchBean = (SaleOrReturnSearchBean) searchBean;
		boolean whereAlreadyAppended = false;

		whereAlreadyAppended = appendId(saleOrReturnSearchBean.getSaleOrReturn(), query, whereAlreadyAppended);
		if(whereAlreadyAppended == true) return;

		whereAlreadyAppended = appendCustomer(saleOrReturnSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendStockItem(saleOrReturnSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendOverdue(saleOrReturnSearchBean, query, whereAlreadyAppended);
		whereAlreadyAppended = appendStatus(saleOrReturnSearchBean, query, whereAlreadyAppended);
	}

	private boolean appendId(SaleOrReturn entity,
		StringBuffer query, boolean whereAlreadyAppended) {
		if(entity.getId() != null) {
			//Overwrite all other queries
			query.append(" where " + getEntityAlias() + "id = " + entity.getId());
			return true;
		}
		return false;
	}


	private boolean appendStatus(SaleOrReturnSearchBean saleOrReturnSearchBean,
			StringBuffer query, boolean whereAlreadyAppended) {
		SaleOrReturnStatus status = saleOrReturnSearchBean.getSaleOrReturn().getSaleOrReturnStatus();
		if(saleOrReturnSearchBean.getOverdue() != null && saleOrReturnSearchBean.getOverdue() == true) {
			query.append(" and s.saleOrReturnStatus = '" + SaleOrReturnStatus.WITH_CUSTOMER + "'");
			return true;
		}
		if(status != null) {
			if(whereAlreadyAppended) {
				query.append(" and s.saleOrReturnStatus = '" + status + "'");
				return true;
			} else {
				query.append(" where s.saleOrReturnStatus = '" + status + "'");
				return true;
			}
		}
		return whereAlreadyAppended;
	}

	private boolean appendOverdue(
			SaleOrReturnSearchBean saleOrReturnSearchBean, StringBuffer query,
			boolean whereAlreadyAppended) {
		if(saleOrReturnSearchBean.getOverdue() != null && saleOrReturnSearchBean.getOverdue() == true) {
			if(whereAlreadyAppended) {
				query.append(" and s.returnDate < " + new Date(new java.util.Date().getTime()));
				return true;
			} else {
				query.append(" where s.returnDate < '" + new Date(new java.util.Date().getTime()) + "'");
				return true;
			}
		}
		return whereAlreadyAppended;
	}

	private boolean appendStockItem(SaleOrReturnSearchBean saleOrReturnSearchBean, StringBuffer query, boolean whereAlreadyAppended) {
		StockItem stockItem = saleOrReturnSearchBean.getStockItem();
		if(stockItem == null) return whereAlreadyAppended;
		String isbn = stockItem.getIsbn();
		if(!isbn.isEmpty()) {
			if(whereAlreadyAppended) {
				query.append(" and st.isbnAsNumber = " + isbn );
				return true;
			} else {
				query.append(" where st.isbnAsNumber = " + isbn);
				return true;
			}
		}
		return whereAlreadyAppended;
	}


	private boolean appendCustomer(SaleOrReturnSearchBean saleOrReturnSearchBean,	StringBuffer query,
			 boolean whereAlreadyAppended) {
		Customer customer = saleOrReturnSearchBean.getSaleOrReturn().getCustomer();
		if(customer == null) return whereAlreadyAppended;
		if(customer.getId() != null) {
			if(whereAlreadyAppended) {
				query.append(" and s.customer = " + customer.getId());
				return true;
			} else {
				query.append(" where s.customer = " + customer.getId());
				return true;
			}
		} else {
			//Don't append these if customer id is already set,
			if(customer.getFirstName() != null && !customer.getFirstName().isEmpty()) {
				if(whereAlreadyAppended) {
					query.append(" and cus.firstName like '%" + customer.getFirstName() + "%'");
					whereAlreadyAppended = true;
				} else {
					query.append(" where cus.firstName like '%" + customer.getFirstName() + "%'");
					whereAlreadyAppended = true;
				}
			}
			if(customer.getFirstName() != null && !customer.getLastName().isEmpty()) {
				if(whereAlreadyAppended) {
					query.append(" and cus.lastName like '%" + customer.getLastName() + "%'");
					return true;
				} else {
					query.append(" where cus.lastName like '%" + customer.getLastName() + "%'");
					return true;
				}
			}
		}
		return whereAlreadyAppended;
	}

	@Override
	public String getEntityName() {
		return "org.bookmarks.domain.SaleOrReturn";
	}

	@Override
	public String getEntityAlias() {
		return "s.";
	}

}
