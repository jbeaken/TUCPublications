<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<form:form modelAttribute="supplierReturn" action="selectSupplier" method="post">
	        
	        <div class="row">
          		<form:label for="supplier.id" path="supplier.id">Supplier</form:label><br/>
				<form:select path="supplier.id" id="focus" required="required" class="select2">
					<form:option value="" label="--- Select ---"/>
          			<form:options items="${supplierList}" itemValue="id" itemLabel="name"/>
          		</form:select>	
		     </div>
		     
		     <div class="row">
	          	<form:label	for="returnsNumber" path="returnsNumber" cssErrorClass="error">Returns Number</form:label><br/>
				<form:input path="returnsNumber" required="required"/>&nbsp;<form:errors path="returnsNumber" />
	          </div>
	          
	        <div class="row">
	          	<form:label	for="note" path="note">Note</form:label><br/>
	          	<form:textarea rows="3" cols="99" path="note"/>
	         </div>
	         
	        <div class="row">
	          	<input type="submit" class="btn btn-primary" id="selectSupplierSubmitButton" value="Add Stock To Supplier Return"/>
		  	</div>
		  	
</form:form>