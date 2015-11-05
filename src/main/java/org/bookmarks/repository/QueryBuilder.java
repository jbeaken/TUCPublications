package org.bookmarks.repository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bookmarks.domain.AbstractEntity;
import org.bookmarks.domain.CustomerType;
import org.bookmarks.domain.Level;
import org.bookmarks.domain.SupplierOrderLineType;
import org.bookmarks.domain.SupplierOrderLineStatus;
import org.bookmarks.domain.SupplierOrderStatus;
import org.bookmarks.domain.EventType;

public class QueryBuilder {
	private StringBuffer query = new StringBuffer();
	private boolean whereAlreadyAppended = false;

	public StringBuffer getQuery() {
		return query;
	}

	public void appendToQuery(String str) {
		query.append(str);
	}

	public void append(String str) {
		str = str.trim();
		if(str.isEmpty()) return;
		if(whereAlreadyAppended) {
			query.append(" and (" + str + ")");
		} else {
			query.append(" where (" + str + ")");
			whereAlreadyAppended = true;
		}
	}
	public void append(Boolean value, String property) {
		if(value == false) return; //For search, false means return both types
		if(whereAlreadyAppended) {
			query.append(" and " + property + " = " + value);
		} else {
			query.append(" where " + property + " = " + value);
			whereAlreadyAppended = true;
		}
	}

	public void appendNotEqual(String value, String property) {
		value = value.trim();
		if(value.isEmpty()) return;
		if(whereAlreadyAppended) {
			query.append(" and " + property + " != " + value);
		} else {
			query.append(" where " + property + " != " + value);
			whereAlreadyAppended = true;
		}
	}

	public void append(EventType type) {
		if(type == null) return;
		if(whereAlreadyAppended) {
			query.append(" and e.type = '" + type + "'");
		} else {
			query.append(" where e.type = '" + type + "'");
			whereAlreadyAppended = true;
		}
	}

	public void append(String value, String property) {
		if(value == null) return;
	//Replace with String trimmedValue = value.trim().replace("'", "''"); and delete appendAndEscape
		String trimmedValue = value.trim();

		if(!trimmedValue.isEmpty()) {
			if(whereAlreadyAppended) {
				query.append(" and " + property + " like '%" + trimmedValue + "%'");
			} else {
				query.append(" where " + property + " like '%" + trimmedValue + "%'");
				whereAlreadyAppended = true;
			}
		}
	}

	public void appendAndEscape(String value, String property) {
		String trimmedValue = value.trim().replace("'", "''");
		if(!trimmedValue.isEmpty()) {
			if(whereAlreadyAppended) {
				query.append(" and " + property + " like '%" + trimmedValue + "%'");
			} else {
				query.append(" where " + property + " like '%" + trimmedValue + "%'");
				whereAlreadyAppended = true;
			}
		}
	}

	public void appendExact(String value, String property) {
	//Escape at some point
		if(value != null && !value.isEmpty()) {
			if(whereAlreadyAppended) {
				query.append(" and " + property + " = '" + value + "'");
			} else {
				query.append(" where " + property + " = '" + value + "'");
				whereAlreadyAppended = true;
			}
		}
	}

	public void appendBooks(boolean notIn) {
			String in = " in ";
			if(notIn) in = " not in ";
			if(whereAlreadyAppended) {
				query.append(" and si.type" + in + "('BOOK', 'PAMPHLET', 'NEWSPAPER')");
			} else {
				query.append(" where si.type" + in + "('BOOK', 'PAMPHLET', 'NEWSPAPER')");
				whereAlreadyAppended = true;
			}
	}

	public void append(CustomerType customerType, String property) {
		if(customerType != null) {
			if(whereAlreadyAppended) {
				query.append(" and " + property + " = '" + customerType + "'");
			} else {
				query.append(" where " + property + " = '" + customerType + "'");
				whereAlreadyAppended = true;
			}
		}
	}

	public void append(SupplierOrderStatus supplierOrderStatus, String property) {
		if(supplierOrderStatus != null) {
			if(whereAlreadyAppended) {
				query.append(" and " + property + " = '" + supplierOrderStatus + "'");
			} else {
				query.append(" where " + property + " = '" + supplierOrderStatus + "'");
				whereAlreadyAppended = true;
			}
		}
	}

	public void append(SupplierOrderLineStatus supplierOrderLineStatus, String property) {
		if(supplierOrderLineStatus != null) {
			if(whereAlreadyAppended) {
				query.append(" and " + property + " = '" + supplierOrderLineStatus + "'");
			} else {
				query.append(" where " + property + " = '" + supplierOrderLineStatus + "'");
				whereAlreadyAppended = true;
			}
		}
	}




	public void appendIsNotNull(String property) {
		if(whereAlreadyAppended) {
				query.append(" and " + property + " is not null");
			} else {
				query.append(" where " + property + " is not null");
				whereAlreadyAppended = true;
			}
	}

	public void appendIsNull(String property) {
		if(whereAlreadyAppended) {
				query.append(" and " + property + " is null");
			} else {
				query.append(" where " + property + " is null");
				whereAlreadyAppended = true;
			}
	}

	public void append(AbstractEntity e, String property) {
		System.out.println("id " + e.getId());
		if(e != null && e.getId() != null) {
			if(whereAlreadyAppended) {
				query.append(" and " + property + " = " + e.getId());
			} else {
				query.append(" where " + property + " = " + e.getId());
				whereAlreadyAppended = true;
			}
		}

	}

	public void appendBeetween(Date startDate, Date endDate, String property) {
			java.sql.Timestamp sqlStartDate = new java.sql.Timestamp(startDate.getTime());
			java.sql.Timestamp sqlEndDate = new java.sql.Timestamp(endDate.getTime());
			if(whereAlreadyAppended) {
				query.append(" and " + property + " between '" + sqlStartDate + "' and '" + sqlEndDate + "'");
			} else {
				query.append(" where " + property + " between '" + sqlStartDate + "' and '" + sqlEndDate + "'");
				whereAlreadyAppended = true;
			}
//			if(whereAlreadyAppended) {
//				query.append(" and " + property + " between '" + dateFormat.format(sqlStartDate) + "' and '" + dateFormat.format(sqlEndDate) + "'");
//			} else {
//				query.append(" where " + property + " between '" + dateFormat.format(sqlStartDate) + "' and '" + dateFormat.format(sqlEndDate) + "'");
//				whereAlreadyAppended = true;
//			}

	}

	public void append(SupplierOrderLineType type, String property) {
		if(type != null) {
			if(whereAlreadyAppended) {
				query.append(" and " + property + " = '" + type + "'");
			} else {
				query.append(" where " + property + " = '" + type + "'");
				whereAlreadyAppended = true;
			}
		}
	}

	public void append(Level priority, String property) {
		if(priority != null) {
			if(whereAlreadyAppended) {
				query.append(" and " + property + " = '" + priority + "'");
			} else {
				query.append(" where " + property + " = '" + priority + "'");
				whereAlreadyAppended = true;
			}
		}

	}

}
