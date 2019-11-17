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
<div class="rows">
      <div class="row">
          <div class="column w-100-percent">
          	Create a Supplier order for stock : <label>${supplierOrderLine.stockItem.title}</label>
          </div>
       </div>
</div>
		<form:form modelAttribute="supplierOrderLine" action="/supplierOrderLine/create?flow=${flow}" method="post">
			<form:hidden path="id"/>
			<form:hidden path="stockItem.id"/>
			<form:hidden path="stockItem.title"/>
			<form:hidden path="customerOrderLine.id"/>
			<form:hidden path="supplier.name"/>
			<form:hidden path="type"/>

			<spring:hasBindErrors name="supplierOrderLine">
			    <h2>Errors</h2>
			    <div class="formerror">
			        <ul>
			            <c:forEach var="error" items="${errors.allErrors}" varStatus="index">
			            	<li>${error.defaultMessage}</li>
						</c:forEach>
			        </ul>
			    </div>
			</spring:hasBindErrors>	
<div class="rows">
      <div class="row">
          <div class="column w-33-percent">
	           <form:label for="amount" path="amount" cssErrorClass="error">Amount</form:label><br/>
	           <form:input type="number" min="0" path="amount" required="required" /> <form:errors path="amount" />                  
	 	 </div>
          <div class="column w-33-percent">
				<form:label for="supplier.id" path="supplier.id" cssErrorClass="error">Supplier</form:label><br/>
				<form:select path="supplier.id" required="required" >
    				<form:options items="${supplierList}" itemValue="id" itemLabel="name"/>
    			</form:select>					
	  	 </div>
          <div class="column w-33-percent">
	           <form:checkbox path="changePreferredSupplier" /> <form:errors path="amount" />                  
	           <form:label for="text" path="changePreferredSupplier" cssErrorClass="error">Change Preferred Supplier</form:label>
	           <br/>
	           <form:checkbox path="sendDirectToSupplier" /> <form:errors path="amount" />                  
	           <form:label for="text" path="sendDirectToSupplier" cssErrorClass="error">Send Direct</form:label>
	 	 </div>		 
	</div>
      <div class="row">
          <div class="column w-33-percent">
	           <form:label for="priority" path="priority" cssErrorClass="error">Priority</form:label><br/>
				<form:select id="prioritySelect" path="priority">
	          	<form:options items="${levelList}" itemLabel="displayName"/>
	          	</form:select>	                 
	 	 </div>
          <div class="column w-33-percent">
	           <form:label for="userInitials" path="userInitials" cssErrorClass="error">User Initials</form:label><br/>
	           <form:input type="text" path="userInitials" required="required" autofocus="autofocus"/> <form:errors path="userInitials" />                  
	 	 </div>	 	   	 	 	 	 
	</div>
      <div class="row">
		  <div class="column w-100-percent">
		  	<input type="submit" class="btn btn-primary" id="createSupplierOrderSubmitButton" value="Create Order"/>
		  	<button onclick="javascript:window.close();" class="btn btn-primary">Cancel</button>
		  </div>
	</div>
	<div class="row">
          <div class="column w-100-percent">
	           <form:label for="text" path="note" cssErrorClass="error">Note</form:label><br/>
	           <form:textarea path="note" cols="50" rows="20" />               
	 </div>
	</div>
</div>		
</form:form>