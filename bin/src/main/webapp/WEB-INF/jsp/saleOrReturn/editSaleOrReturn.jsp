<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="en_GB" scope="session"/>
	<script>
	$(function() {
		$( "#returnDate" ).datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: 'dd/mm/yy'
		});
	});
	</script>
<c:if test="${saleOrReturnOrderLine == null}">
<form:form modelAttribute="saleOrReturn" action="edit" method="post">
<form:hidden path="id"/>
			<form:hidden path="note"/>
			<form:hidden path="creationDate"/>
			<form:hidden path="saleOrReturnStatus"/>
			<form:hidden path="customer.id"/>
			<form:hidden path="customer.firstName"/>
			<form:hidden path="customer.lastName"/>
			<form:hidden path="customer.customerType"/>
<div class="rows">
        <div class="row">
          <div class="column w-100-percent">
				<label>Sale or return for </label>${saleOrReturn.customer.fullName}
	  	 </div>
	  </div>
      <div class="row">
          <div class="column w-33-percent">
				<form:label for="returnDate" path="returnDate">Return Date</form:label><br/>
		 		<form:input for="returnDate" path="returnDate"/>&nbsp;<form:errors path="returnDate" />
	 	 </div>
	</div>
</div>

<div class="rows">
      <div class="row">
          <div class="column w-100-percent">
 				<input type="submit" class="btn btn-primary" value="Save"/>
	 	 </div>
	</div>
</div>
</form:form>
</c:if>
<br/>
<br/>
<hr/>
<c:if test="${saleOrReturnOrderLine != null}">
<br/>
<legend>${saleOrReturnOrderLine.stockItem.title}</legend>
<form:form modelAttribute="saleOrReturnOrderLine" action="editSaleOrReturnOrderLine" method="post">
	<form:hidden path="id"/>
	<form:hidden path="sellPrice"/>
	<form:hidden path="originalAmount"/>
	<form:hidden path="amountSold"/>
	<form:hidden path="stockItem.type"/>
	<form:hidden path="stockItem.title"/>
	<form:hidden path="stockItem.isbn"/>
	<form:hidden path="stockItem.id"/>
		<div class="rows">
		      <div class="row">
		          <div class="column w-33-percent">
		 				<form:label for="amount" path="amount">Amount</form:label><br/>
		 				<form:input for="amount" path="amount" id="focus"/>&nbsp;<form:errors path="amount" />
			 	 </div>
		          <div class="column w-33-percent">
		 				 <input type="submit" class="btn btn-warning"/>
			 	 </div>
			</div>
		</div>
</form:form>
</c:if>

<c:if test="${saleOrReturnOrderLine == null}">
<form:form modelAttribute="stockItemSearchBean" action="searchStockItems" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
				<form:label for="stockItem.isbn" path="stockItem.isbn" cssErrorClass="error">ISBN</form:label><br/>
				<form:input path="stockItem.isbn" id="focus"/> <form:errors path="stockItem.isbn" />
	  	 </div>
          <div class="column w-33-percent">
          <br/>
				<input type="submit" class="btn btn-warning" value="Add ISBN to Sale Or Return"/>
	  	 </div>
	  	</div>
</div>
</form:form>
</c:if>
<br/>
<br/>
<br/>
<c:if test="${saleOrReturnOrderLineList != null && flow != 'return'}">
	<display:table
		name="saleOrReturnOrderLineList"
		decorator="org.bookmarks.ui.SaleOrReturnOrderLineDecorator">
	  <display:column property="stockItem.isbn" title="ISBN"/>
	  <display:column property="stockItem.title" title="Title" maxLength="50"/>
	  <display:column property="amount" title="Quantity"/>
	  <display:column property="sellPrice" title="Sell Price"/>
	  <display:column property="price" title="Price"/>
	  <display:column property="link" title="Actions" />
	</display:table>
<br/>
<br/>
<br/>
<label>Total Price: </label><fmt:formatNumber value="${totalPrice}" type="currency"/> <br/>
<label>No. of items: </label>${noOfLines}
</c:if>
<c:if test="${saleOrReturnOrderLineList != null && flow == 'return'}">
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
	  <display:column property="sellPrice" title="Sell Price"/>
	  <display:column property="price" title="Price"/>
	  <display:column property="link" title="Actions" />
	</display:table>
</form:form>
</c:if>
