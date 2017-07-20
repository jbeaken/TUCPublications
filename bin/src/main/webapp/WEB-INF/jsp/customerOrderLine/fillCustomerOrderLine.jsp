<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
		<form:form modelAttribute="customerOrderLine" action="fill" method="post">
		  	<form:hidden path="id"/>
			<form:hidden path="note"/>
		  	<form:hidden path="amount"/>
		  	<form:hidden path="deliveryType"/>
		  	<form:hidden path="paymentType"/>
		  	<form:hidden path="stockItem.id"/>
		  	<form:hidden path="stockItem.title"/>
		  	<form:hidden path="customer.id"/>
		  	<form:hidden path="customer.firstName"/>
		  	<form:hidden path="customer.lastName"/>
<div class="rows">
        <div class="row"> 
          <div class="column w-70-percent">
			<label class="smallCaps">Customer: </label>${customerOrderLine.customer.firstName} ${customerOrderLine.customer.lastName}<br/>
			<label class="smallCaps">Order Id: </label>${customerOrderLine.id}<br/>
			<label class="smallCaps">Stock: </label>${customerOrderLine.stockItem.title}<br/>		
			<label class="smallCaps">Order Amount: </label>${customerOrderLine.amount}<br/>		
	  	 </div>		
      </div>
      <div class="row">
          <div class="column w-70-percent">
	           <form:label for="amountFilled" path="amountFilled" cssErrorClass="error">Amount Filled</form:label><br/>
	           <form:input path="amountFilled" /> <form:errors path="note" />                  
	 	 </div>
	</div>
      <div class="row">
		  <div class="column w-30-percent">
		  	<input type="submit" class="btn btn-primary"/>
								<a href="/bookmarks/customerOrderLine/searchFromSession">
								 	<button type="button" class="btn btn-primary">Back To Search</button>
								</a>		  	
		  </div>
	</div>
</div>		
</form:form>