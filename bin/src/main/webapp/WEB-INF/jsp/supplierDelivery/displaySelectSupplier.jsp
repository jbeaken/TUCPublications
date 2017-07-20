<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="supplierDelivery" action="selectSupplier" method="post">
	<div class="rows">
	        <div class="row">
	          <div class="column w-33-percent">
	          	<form:label for="supplier.id" path="supplier.id">Supplier</form:label><br/>
					<form:select path="supplier.id" id="focus" required="required">
						<form:option value="" label="--- Select ---"/>
           				<form:options items="${supplierList}" itemValue="id" itemLabel="name"/>
           			</form:select>	
		  	 </div>
	          <div class="column w-33-percent">
	          	<form:label	for="invoiceNumber" path="invoiceNumber" cssErrorClass="error">Invoice Number</form:label><br/>
				<form:input path="invoiceNumber" required="required"/>&nbsp;<form:errors path="invoiceNumber" />
	          </div>
		  	</div>		
	        <div class="row">
	          <div class="column w-100-percent">
	          	<form:label	for="note" path="note">Note</form:label><br/>
	          	<form:textarea rows="3" cols="99" path="note"/>
	          </div>
		  	</div>
	        <div class="row">
	          <div class="column w-100-percent">
	          	<input type="submit" class="btn btn-primary" id="selectSupplierSubmitButton" value="Add Stock"/>
	          </div>
		  	</div>
	</div>
</form:form>