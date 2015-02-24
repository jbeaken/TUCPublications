<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<div class="rows">
        <div class="row">
          <div class="column w-70-percent">
				<strong>${sessionScope.lastSale.stockItem.title}</strong>
	  	 </div>
	          <div class="column w-30-percent">
					<label>Last Sale Price:</label>
					<h3><fmt:formatNumber type="currency" currencyCode="GBP" value="${sessionScope.lastSale.sellPrice}"/></h3> 
		  	 </div>
	  	</div>
	     <div class="row">
	          <div class="column w-70-percent">
					<label>Total Price:</label>
					<h2><fmt:formatNumber type="currency" currencyCode="GBP" value="${totalPrice}"/></h2> 
		  	</div>
			<c:if test="${sessionScope.event != null}">
			<div class="column w-30-percent">
					<label>Selling for event </label><br/>${sessionScope.event.name}
		  	</div>
			</c:if> 
	  </div>	  
</div>      
<br/>

<form:form modelAttribute="stockItemSearchBean" action="${pageContext.request.contextPath}/sale/sellByISBN" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
				<form:label	for="stockItem.isbn" path="stockItem.isbn" cssErrorClass="error">ISBN</form:label><br/>
				<form:input path="stockItem.isbn" autofocus="autofocus" required="required"/> <form:errors path="stockItem.isbn" />
	  	 </div>
	  	</div>
      <div class="row">
          <div class="column w-100-percent">
 				<input type="submit" id="sellStockSubmitButton" class="btn btn-primary" value="Sell"/>
 				 <c:if test="${lastSale != null}"> 
	 				<a href="edit?id=${lastSale.id}&flow=search" tabindex="3">
	 					<button type="button" class="btn btn-warning" id="editLastSaleButton">Edit Last Sale</button>
	 				</a>
	 			</c:if>
				<a href="${pageContext.request.contextPath}/sale/sellSecondHand" tabindex="4">
	 				<button type="button" id="sellSecondHandButton" class="btn btn-danger">Sell Second Hand</button>
	 			</a>	 			
	 	 </div>
	</div>		
</div>				
</form:form>
<br/>	
<p>
	<display:table name="saleList" decorator="org.bookmarks.ui.SaleDecorator" id="saleList">
	  <display:column property="stockItem.isbn" title="ISBN" class="isbn"/>
	  <display:column property="title" title="Title" class="title"/>
	  <display:column property="discount" title="Discount"/>
	  <display:column property="quantity" title="Quantity"/>
	  <display:column property="sellPrice" title="Sell Price"/>
	  <display:column property="link" title="Actions" />
	</display:table>
</p>