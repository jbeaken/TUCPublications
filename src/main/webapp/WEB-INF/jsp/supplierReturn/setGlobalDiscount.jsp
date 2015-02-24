<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="supplierDeliveryLine" action="setGlobalDiscount" method="post">
	<div class="rows">
	        <div class="row">
	          <div class="column w-33-percent">
	          	<form:label	for="discount" path="discount" cssErrorClass="error">Discount</form:label><br/>
				<form:input type="number" min="0" max="100" step="any" path="discount" autofocus="autofocus" required="required"/>&nbsp;<form:errors path="discount"/>
	          </div>
		  	 </div>
		  	</div>		
	        <div class="row">
	          <div class="column w-100-percent">
	          	<input type="submit" class="btn btn-primary" id="setGlobalDiscountSubmitButton" value="Set Global Discount"/>
	          </div>
		  	</div>
	</div>
</form:form>