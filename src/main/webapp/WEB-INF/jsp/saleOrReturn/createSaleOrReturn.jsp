<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
<form:form modelAttribute="saleOrReturn" action="save" method="post">
<form:hidden path="customer.id"/>
<form:hidden path="customer.firstName"/>
<form:hidden path="customer.lastName"/>
<form:hidden path="customer.customerType"/>
<form:hidden path="saleOrReturnStatus" value="WITH_CUSTOMER"/>

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
          <div class="column w-33-percent">
				<form:label for="customerReference" path="customerReference">Customer Reference</form:label><br/>
		 		<form:input for="customerReference" path="customerReference"/>
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
	<form:hidden path="stockItem.type"/>
	<form:hidden path="amountRemainingWithCustomer"/>
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
		 				<form:label for="sellPrice" path="sellPrice">Sell Price</form:label><br/>
		 				<form:input for="sellPrice" path="sellPrice" id="focus"/>&nbsp;<form:errors path="sellPrice" />
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
				<input type="submit" class="btn btn-warning" value="Add Stock To SoR"/>		
	  	 </div>
	  	</div>
</div>
</form:form>
</c:if>
<br/>	
<br/>	
<br/>	
<c:if test="${saleOrReturnOrderLineList != null}">
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