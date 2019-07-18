<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
	<script>
	$(function() {
		$( "#completionDate" ).datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: 'dd-mm-yy'
		});
		$("#receivedIntoStockDate" ).datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: 'dd-mm-yy'
		});
		$("#onOrderDate" ).datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: 'dd-mm-yy'
		});		
	});
	</script>
<div class="rows">
     <div class="row">
          <div class="column w-33-percent">
			<label>Order No.</label>&nbsp;&nbsp;${customerOrderLine.id}
	  	 </div>		
          <div class="column w-33-percent">
			<label>Order Date</label>&nbsp;&nbsp;${customerOrderLine.creationDate}
	  	 </div>
	  	 <c:if test="${customerOrderLine.webReference != null}">
	          <div class="column w-33-percent">
				<label>Web Reference</label>&nbsp;&nbsp;${customerOrderLine.webReference}
		  	 </div>		
	  	 </c:if>  	 		
	</div>
</div>
<div class="rows">
     <div class="row">
          <div class="column w-33-percent">
			<label>Customer ID</label><br/>        
			${customer.id}
	  	 </div>
          <div class="column w-33-percent">
			<label>Customer Name</label><br/>        
			${customer.fullName}
	  	 </div>
		  <div class="column w-33-percent">
		  	<label>Type of Customer</label><br/> 
		  	${customer.customerType.displayName}     
	  	  </div>		
	</div>
     <div class="row">
          <div class="column w-33-percent">
			<label>Telephone</label><br/>        
			${customer.fullPhoneNumberWithBreaks}
	  	 </div>
          <div class="column w-33-percent">
			<label>Address</label><br/>        
			${customerOrderLine.fullAddressWithBreaks}
	  	 </div>
	  	 <div class="column w-33-percent">
			<label>Is Part of Multiple Order?</label><br/>        
			${customerOrderLine.isMultipleOrder}
	  	 </div>
	</div>
</div>
<jsp:include page="../stockItem/viewStockItemFragment.jsp"/>
<br/>
<hr/>
				<form:form modelAttribute="customerOrderLine" action="edit" method="post">
					<form:hidden path="id"/>
					<form:hidden path="creationDate"/>					
					<form:hidden path="isPaid"/>
					
					<form:hidden path="stockItem.id"/>
					<form:hidden path="stockItem.isbn"/>
					<form:hidden path="stockItem.title"/>
					<form:hidden path="stockItem.mainAuthor"/>

					<form:hidden path="supplierOrderLine.id"/>

					<form:hidden path="customer.id"/>
					<form:hidden path="customer.firstName"/>
					<form:hidden path="customer.lastName"/>
					<form:hidden path="customer.address.address1"/>
					<form:hidden path="customer.address.address2"/>
					<form:hidden path="customer.address.address3"/>
					<form:hidden path="customer.address.city"/>
					<form:hidden path="customer.address.postcode"/>
					<form:hidden path="customer.contactDetails.mobileNumber"/>
					<form:hidden path="customer.contactDetails.workNumber"/>
					<form:hidden path="customer.contactDetails.homeNumber"/>
<div class="rows">
	<div class="row">
          <div class="column w-33-percent">
			<form:label	for="paymentType" path="paymentType" cssErrorClass="error">Payment</form:label><br/>        
							<form:select path="paymentType">
            					<form:options items="${paymentTypeOptions}" itemLabel="displayName"/>
        					</form:select>
	  	 </div>
          <div class="column w-33-percent">
			<label>Delivery</label><br/>        
							<form:select path="deliveryType">
            					<form:options items="${deliveryTypeOptions}" itemLabel="displayName"/>
        					</form:select>
	  	 </div>
		  <div class="column w-33-percent">
		  	<label>Status</label><br/> 
		  		<form:select path="status">
	            	<form:options items="${statusList}" itemLabel="displayName"/>
	        	</form:select>     
	  	  </div>		
	</div>
	<div class="row">
		  <div class="column w-33-percent">
		  	<form:label	for="creditCard.creditCard1" path="creditCard.creditCard1" cssErrorClass="error">Credit Card No:</form:label><br/>
		  	<form:errors path="creditCard.creditCard1" /> 
		  	<form:input path="creditCard.creditCard1" size="4" maxlength="4"/>&nbsp;
		  	<form:input path="creditCard.creditCard2" size="4" maxlength="4"/>&nbsp;
		  	<form:input path="creditCard.creditCard3" size="4" maxlength="4"/>&nbsp;
		  	<form:input path="creditCard.creditCard4" size="4" maxlength="4"/>     
	  	  </div>		
          <div class="column w-33-percent">
			<form:label	for="creditCard.expiryMonth" path="creditCard.expiryMonth" cssErrorClass="error">Expiry</form:label><br/>        
			<form:errors path="creditCard.expiryMonth" /> 
			<form:input path="creditCard.expiryMonth" maxlength="2" size="1"/>&nbsp;
			<form:input path="creditCard.expiryYear" maxlength="2"  size="1"/>
	  	 </div>
          <div class="column w-33-percent">
			<form:label	for="creditCard.securityCode" path="creditCard.securityCode" cssErrorClass="error">Security Code</form:label><br/>         
			<form:errors path="creditCard.securityCode" /> 
			<form:input path="creditCard.securityCode" maxlength="3" size="3"/>
	  	 </div>
	</div>
	<hr/>
	<div class="row">
		  <div class="column w-33-percent">
		  	<form:label	for="completionDate" path="completionDate" cssErrorClass="error">Completion Date</form:label><br/>
		  	<form:errors path="completionDate" /> 
		  	<form:input path="completionDate" />&nbsp;   
	  	  </div>		
  		  <div class="column w-33-percent">
		  	<form:label	for="onOrderDate" path="onOrderDate" cssErrorClass="error">On Order Date</form:label><br/>
		  	<form:errors path="onOrderDate" /> 
		  	<form:input path="onOrderDate" />&nbsp;   
	  	  </div>	
		  <div class="column w-33-percent">
		  	<form:label	for="receivedIntoStockDate" path="receivedIntoStockDate" cssErrorClass="error">Received Into Stock Date</form:label><br/>
		  	<form:errors path="receivedIntoStockDate" /> 
		  	<form:input path="receivedIntoStockDate" />&nbsp;   
	  	  </div>	
	</div>	
	
	<div class="row">
          <div class="column w-33-percent">
			<label>Amount</label><br/>        
			<form:input path="amount" />
	  	 </div>
          <div class="column w-33-percent">
			<form:label	for="source" path="source" cssErrorClass="error">Source</form:label><br/>        
				<form:select path="source">
         			<form:options items="${sourceOptions}" itemLabel="displayName"/>
     			</form:select>
	  	 </div>	  	 
		  <div class="column w-33-percent">
		  	<form:label	for="sellPrice" path="sellPrice" cssErrorClass="error">Sell Price</form:label><br/>
		  	<form:errors path="sellPrice" /> 
		  	<form:input path="sellPrice" required="required"/> 
	  	  </div>
	</div>  
	
	<div class="row">
          <div class="column w-33-percent">
			<label>Is Second Hand?</label><br/>        
			<form:checkbox for="isSecondHand" path="isSecondHand" />
	  	 </div>
          <div class="column w-33-percent">
			<label>Printed Label?</label><br/>        
			<form:checkbox for="havePrintedLabel" path="havePrintedLabel" />
	  	 </div>	  	 
          <div class="column w-33-percent">
			<label>Postage</label><br/>        
			<form:input path="postage" />
	  	 </div>	  	 
	</div> 
	
	<div class="row">
          <div class="column w-100-percent">
							<input type="submit" id="editCustomerOrderLineButton" class="btn btn-primary" value="Save Customer Order"/>&nbsp;
							<c:if test="${customerOrderLine.canComplete == true}">
								<a href="/customerOrderLine/complete?customerOrderLineId=${customerOrderLine.id}&flow=fromEditCustomerOrderLine">
								 	<button type="button" class="btn btn-primary">Complete</button>
								</a>
							</c:if>
							<c:if test="${customerOrderLine.canPost == true}">
								<a href="/customerOrderLine/markAsPosted?customerOrderLineId=${customerOrderLine.id}&customerId=${customerOrderLine.customer.id}">
								 	<button type="button" class="btn btn-primary">Post</button>
								</a>
							</c:if>
							<c:if test="${customerOrderLine.hasInvoice == true}">
								<a href="/invoice/view?id=${customerOrderLine.invoice.id}&flow=editCustomerOrderLine&customerOrderLineId=${customerOrderLine.id}">
								 	<button type="button" class="btn btn-primary">View Invoice</button>
								</a>
							</c:if>
							<c:if test="${flow != 'supplierOrderEdit'}">
								<a href="/customerOrderLine/searchFromSession">
								 	<button type="button" class="btn btn-primary">Back To Search</button>
								</a>
							</c:if>
							<c:if test="${flow == 'supplierOrderEdit'}">
								<a href="/customerOrderLine/searchFromSession">
								 	<button type="button" onclick="javascript:window.close();" class="btn btn-primary">Close</button>
								</a>
							</c:if>
	  	 </div>
	</div>
	<div class="row">
          <div class="column w-50-percent">
			<br/><br/>        
			<form:textarea path="note" cols="100" rows="10"/>
	  	 </div>		
	</div>
	
</div>
</form:form>
