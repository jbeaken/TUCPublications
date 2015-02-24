<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<form:form modelAttribute="saleOrReturnOrderLineList" action="/bookmarks/saleOrReturn/sell" method="post">
	<display:table 
		name="saleOrReturnOrderLineList" 
		decorator="org.bookmarks.ui.SaleOrReturnOrderLineDecorator"
		id="searchTable">>
	  <display:column property="stockItem.isbn" title="ISBN"/>
	  <display:column property="stockItem.title" title="Title" maxLength="50"/>
	  <display:column property="amount" title="Quantity"/>
	  <display:column property="amountSold" title="Quantity Sold"/>
	  <display:column title="Quantity Sold">
	  	<input type="text" name="${searchTable.stockItem.id}" id="${searchTable.stockItem.id}" value="${searchTable.amountSold}" />
 	  </display:column>		  
	  <display:column title="With Customer">
	  	<input type="text" name="r${searchTable.stockItem.id}" id="r${searchTable.stockItem.id}" value="${searchTable.amountRemainingWithCustomer}" />
 	  </display:column>		  
	  <display:column property="sellPrice" title="Sell Price"/>
	  <display:column property="price" title="Price"/>
	  <display:column property="link" title="Actions" />
	</display:table>
<br/>
<input type="submit" class="btn btn-primary"/>
</form:form>
