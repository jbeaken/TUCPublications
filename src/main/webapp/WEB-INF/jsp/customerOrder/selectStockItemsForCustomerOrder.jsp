<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<script>
$(function(){
	$('#stockItemSearchForm input').keyup(function(event) {
		if(event.which == 13) {
			searchStockSubmitButton.click();
		}
	});
});
</script>
<span class="customerName">
${customerOrder.customer.fullName}
</span>
<c:if test="${stockItemList == null && customerOrderLine == null}">			
			<form:form modelAttribute="stockItemSearchBean" id="stockItemSearchForm" action="/customerOrder/searchStockItems" method="post">
				<jsp:include page="../searchStockItemFragment.jsp" />
</div>
</form:form>
<div id="rows">
	<div class="row">
          <div class="column w-100-percent"> 
		  <button type="button" class="btn btn-danger" id="searchStockSubmitButton" onclick="javascript:submitForm('/customerOrder/searchStockItems')" accesskey="S">Add Stock</button>
          <button type="button" class="btn btn-primary" id="resetButton" accesskey="R">Reset</button>&nbsp;
	 	 </div>
	</div>	
</div>			
</c:if>			
<c:if test="${customerOrderLine != null}">
				<form:form modelAttribute="customerOrderLine" action="editCustomerOrderLine" method="post">
					<form:hidden path="id"/>
					<form:hidden path="stockItem.id"/>
					<form:hidden path="stockItem.title"/>
					<form:hidden path="status"/>
					<table>
						<tr>
							<td>Title</td>
							<td>${customerOrderLine.stockItem.title}</td>
						</tr>
						<tr>
							<td>Amount</td>
							<td><form:input for="amount" path="amount" id="focus"/>&nbsp;<form:errors path="amount" /></td>
						</tr>
						<tr>
							<td>Is Second Hand?<form:checkbox for="isSecondHand" path="isSecondHand" />&nbsp;<form:errors path="isSecondHand" /></td>
						</tr>						
						<tr>
							<td><input type="submit" class="btn btn-primary" id="editCustomerOrderLineButton"/></td>
						</tr>
					</table>
				</form:form>
</c:if>
	<c:if test="${stockItemList != null}">
		<display:table name="stockItemList" 
			   requestURI="searchStockItems" 
			   decorator="org.bookmarks.ui.CustomerOrderStockItemSearchDecorator"
			   sort="external" 
			   defaultsort="1" 
			   defaultorder="ascending"
			   pagesize="10"
			   partialList="true"
			   size="${searchResultCount}"			   
			   id="searchTable">
				  <display:column property="isbn" sortable="true" sortName="s.isbn" title="ISBN"/>
				  <display:column property="title" sortable="true" sortName="s.title" maxLength="12" title="Title"/>
				  <display:column property="category.name" sortable="true" sortName="s.category.name" title="Category"/>
				  <display:column property="publisher.name" sortable="true" sortName="s.publisher.name" maxLength="12" title="Publisher"/>
				  <display:column property="prices" title="Sell/Pub/Cost"/>
				  <display:column property="info" title="Info"/>
				  <display:column property="mainAuthor" title="Authors"/>
				  <display:column property="quantityInStock" title="In Stock"/>
				  <display:column property="quantityOnOrder" title="On Order"/>
				  <display:column property="link" title="Actions" />
		</display:table>
</c:if>		
<c:if test="${stockItemList == null && customerOrderLine == null && customerOrderLineList != null}">		
					<display:table name="customerOrderLineList" requestURI="" decorator="org.bookmarks.ui.CustomerOrderLineDecorator">
					  <display:column sortable="true" property="stockItem.isbn" title="ISBN"/>
					  <display:column sortable="true" property="stockItem.title" title="Title"/>
					  <display:column sortable="true" property="prices" title="Prices"/>
					  <display:column sortable="true" property="stockItem.category.name" title="Category"/>
					  <display:column sortable="true" property="stockItem.type" title="Type"/>
					  <display:column sortable="true" property="stockItem.mainAuthor" title="Authors"/>
					  <display:column sortable="true" property="stockItem.quantityInStock" title="In Stock"/>
					  <display:column sortable="true" property="amount" title="Amount"/>
					  <display:column property="link" title="Actions"/>
  					</display:table>
</c:if>			
<br/>
<br/>
<c:if test="${stockItemList == null && customerOrderLine == null}">
<a href="/customerOrder/displayCustomerOrder"><button id="proceedToCheckoutButton" class="btn btn-danger">Proceed To Checkout</button></a>
<a href="/customerOrder/cancel" class="btn btn-success">Cancel</a>
</c:if>
