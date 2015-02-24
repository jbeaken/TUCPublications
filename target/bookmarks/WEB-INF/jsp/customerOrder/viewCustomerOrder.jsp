<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<div class="rows">
	        <div class="row">
	          <div class="column w-33-percent"> 
	 				<label>Order Date : </label><fmt:formatDate value="${customerOrder.creationDate}" dateStyle="MEDIUM"/>  
		 	 </div>
		  </div>  
</div>
<jsp:include page="../customer/viewCustomerFragment.jsp"/>
<br/>
<br/>
<div class="rows">
     <div class="row">
          <div class="column w-33-percent">
			<label>Payment</label><br/>        
			${customerOrder.paymentType.displayName}
	  	 </div>
          <div class="column w-33-percent">
			<label>Delivery</label><br/>        
			${customerOrder.deliveryType.displayName}
	  	 </div>		
	</div>
	<c:if test="${customerOrder.paymentType == 'CREDIT_CARD'}">
	     <div class="row">
	          <div class="column w-33-percent">
				<label>Credit Card No.</label><br/>        
				${customerOrder.creditCard.creditCard1}&nbsp;${customerOrder.creditCard.creditCard2}&nbsp;${customerOrder.creditCard.creditCard3}&nbsp;${customerOrder.creditCard.creditCard4}
		  	 </div>
	          <div class="column w-33-percent">
				<label>Expiry</label><br/>        
				${customerOrder.creditCard.expiryMonth} / ${customerOrder.creditCard.expiryYear}
		  	 </div>
	          <div class="column w-33-percent">
				<label>Sec Code</label><br/>        
				${customerOrder.creditCard.securityCode}
		  	 </div>
		</div>
	</c:if>
     <div class="row">
          <div class="column w-100-percent">
			<label>Note</label><br/>        
			${customerOrder.note}
	  	 </div>
	</div>
</div>
<br/>
<br/>
<display:table 
	name="customerOrderLineList" 
	requestURI=""
	decorator="org.bookmarks.ui.CustomerOrderLineDecorator">
  <display:column property="id" title="ID"/>	
  <display:column property="customerOrderStatus.displayName" title="Status"/>	
  <display:column property="stockItem.isbn" title="ISBN"/>
  <display:column property="stockItem.title" title="Title"/>
  <display:column property="stockItem.category.name" title="Category"/>
  <display:column property="stockItem.type" title="Type"/>
  <display:column property="stockItem.mainAuthor" title="Authors"/>
  <display:column property="stockItem.quantityInStock" title="In Stock"/>
  <display:column property="amount" title="Amount"/>
		</display:table>
