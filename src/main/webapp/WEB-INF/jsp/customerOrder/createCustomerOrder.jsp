<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<head>
	<script>
		function saveCustomerOrder() {
			
			if($('#email').val() == '') {
				var r = confirm("Customer email is empty! Are you sure you want to save without an email?");
				if (r == true) {
				} else {
				    return;
				}
			}
			
			submitForm('${pageContext.request.contextPath}/customerOrder/create');
		}
	</script>
</head>
<span class="customerName">
	${customerOrder.customer.firstName}
	${customerOrder.customer.lastName}
</span>
<form:form action="${pageContext.request.contextPath}/customerOrder/create" modelAttribute="customerOrder">
	<form:hidden path="customer.id" />
	<form:hidden path="customer.firstName" />
	<form:hidden path="customer.lastName" />
	<div class="rows">
	        <div class="row">
	          <div class="column w-33-percent">
					<form:label	for="paymentType" path="paymentType" class="smallCaps" cssErrorClass="error">Payment Type</form:label><br/>
					<form:select path="paymentType" required="required">
						<form:option value="" label="Select One"/>
						<form:options items="${paymentTypeList}" itemLabel="displayName"/>
					</form:select>
					<form:errors path="paymentType" />				
		  	 </div>
			  <div class="column w-33-percent">
				    <form:label for="deliveryType" path="deliveryType" class="smallCaps" cssErrorClass="error">Delivery Type</form:label><br/>
					<form:select path="deliveryType" required="required">
						<form:option value="" label="Select One"/>
						<form:options items="${deliveryTypeList}" itemLabel="displayName"/>
					</form:select>
		  	</div>
			  <div class="column w-33-percent">
				    <form:label for="source" path="source" class="smallCaps" cssErrorClass="error">Source</form:label><br/>
					<form:select path="source" required="required">
						<form:option value="" label="Select One"/>
						<form:options items="${sourceList}" itemLabel="displayName"/>
					</form:select>
		  	</div>				  			
	      </div>
	</div>
	<div class="rows" id="creditCardDetails">
	        <div class="row">
	          <div class="column w-33-percent">
					<form:label path="creditCard.creditCard1" cssErrorClass="error">Credit Card</form:label><br/> 
					<form:input path="creditCard.creditCard1" size="3" maxlength="4"/><form:errors path="creditCard.creditCard1" />
					<form:input path="creditCard.creditCard2" size="3" maxlength="4"/><form:errors path="creditCard.creditCard2" />
					<form:input path="creditCard.creditCard3" size="3" maxlength="4"/><form:errors path="creditCard.creditCard3" />
					<form:input path="creditCard.creditCard4" size="3" maxlength="4"/><form:errors path="creditCard.creditCard4" />
		 	 </div>
	          <div class="column w-33-percent">
					<form:label	for="creditCard.expiryMonth" path="creditCard.expiryMonth" cssErrorClass="error">Expiry</form:label><br/>
					<form:input path="creditCard.expiryMonth" size="2" maxlength="2"/><form:errors path="creditCard.expiryMonth" />
					<form:input path="creditCard.expiryYear" size="2" maxlength="2"/><form:errors path="creditCard.expiryYear" />
		  	 </div>
			  <div class="column w-33-percent">
					<form:label	for="creditCard.securityCode" path="creditCard.securityCode" cssErrorClass="error">Security Code</form:label><br/>
					<form:input path="creditCard.securityCode" size="3" maxlength="3"/><form:errors path="creditCard.securityCode" />
		  	</div>		
	      </div>
	</div>
	<div class="rows" id="creditCardDetails">
	        <div class="row">
	          <div class="column w-33-percent">
					<form:label path="customer.contactDetails.email" cssErrorClass="error">Email</form:label><br/> 
					<form:input path="customer.contactDetails.email" id="email" type="email"/><form:errors path="creditCard.securityCode" />
		 	 </div>
	          <div class="column w-33-percent">
		  	 </div>
			  <div class="column w-33-percent">
		  	</div>		
	      </div>
	</div>	
	<div class="rows">
	        <div class="row">
	          <div class="column w-100-percent">
					<form:label	for="note" path="note" cssErrorClass="error">Note</form:label><br/> 
					<form:textarea path="note" cols="90" rows="8"/><form:errors path="note" />
		 	 </div>
	      </div>
	        <div class="row">
	          <div class="column w-100-percent"> 
	          		<br/>
					<button type="button" class="btn btn-danger" id="createCustomerOrderButton" onclick="javascript:saveCustomerOrder()">Create Order</button>
					<button type="button" id="gotoAddStockToCustomerOrderButton" onclick="javascript:submitForm('${pageContext.request.contextPath}/customerOrder/addStock')" class="btn btn-primary">Add stock</button>
					<a href="${pageContext.request.contextPath}/customerOrder/cancel" class="btn btn-success">Cancel</a>
		 	 </div> 
	      </div>
	</div>
</form:form>
				
			<c:if test="${customerOrderLineList != null}">
	          		<br/>
	          		<br/>
					<display:table name="customerOrderLineList" requestURI="" decorator="org.bookmarks.ui.CustomerOrderLineDecorator">
					  <display:column sortable="true" group="1" property="stockItem.isbn" title="ISBN"/>
					  <display:column sortable="true" group="2" property="stockItem.title" title="Title"/>
					  <display:column sortable="true" group="2" property="stockItem.category.name" title="Category"/>
					  <display:column sortable="true" property="stockItem.type" title="Type"/>
					  <display:column sortable="true" property="stockItem.mainAuthor" title="Authors"/>
					  <display:column sortable="true" property="stockItem.quantityInStock" title="In Stock"/>
					  <display:column sortable="true" property="amount" title="Amount"/>
  					</display:table>
			</c:if>		
