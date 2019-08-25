<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<legend>${stockTakeLine.stockItem.title}</legend>
<form:form modelAttribute="stockTakeLine" action="increment" method="post">
	<form:hidden path="id"/>
	<form:hidden path="stockItem.id"/>
	<form:hidden path="stockItem.isbn"/>
	<form:hidden path="stockItem.title"/>
		<div class="rows">
		      <div class="row">
		          <div class="column w-70-percent">
		 				<form:label for="amountToIncrement" path="amountToIncrement" cssErrorClass="error">Amount To Increment</form:label><br/>
		 				<form:input for="amountToIncrement" autofocus="autofocus" path="amountToIncrement" required="required"/>&nbsp;<form:errors path="amountToIncrement" />
			 	 </div>
			</div>	
		      <div class="row">
		          <div class="column w-70-percent">
		 				<input type="submit" class="btn btn-primary" value="Save" id="incrementSupplierDeliveryLineSubmitButton"/>
			 	 </div>
			</div>		
		</div>					
</form:form>