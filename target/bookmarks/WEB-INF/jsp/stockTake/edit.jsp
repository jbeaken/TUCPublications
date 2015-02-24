<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<legend>${stockTakeLine.stockItem.title}</legend>
<form:form modelAttribute="stockTakeLine" action="edit" method="post">
	<form:hidden path="id"/>
	<form:hidden path="stockItem.id"/>
	<form:hidden path="stockItem.isbn"/>
	<form:hidden path="stockItem.title"/>
		<div class="rows">
		      <div class="row">
		          <div class="column w-70-percent">
		 				<form:label for="quantity" path="quantity" cssErrorClass="error">Quantity</form:label><br/>
		 				<form:input for="quantity" id="focus" path="quantity" required="required"/>&nbsp;<form:errors path="quantity" />
			 	 </div>
			</div>	
		      <div class="row">
		          <div class="column w-70-percent">
		 				<input type="submit" class="btn btn-primary" value="Save" id="editSupplierDeliveryLineSubmitButton"/>
			 	 </div>
			</div>		
		</div>					
</form:form>