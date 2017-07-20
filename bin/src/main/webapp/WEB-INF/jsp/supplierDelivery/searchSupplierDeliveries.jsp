<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="supplierDeliverySearchBean" action="search" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
				<form:label for="supplierDelivery.supplier.id" path="supplierDelivery.supplier.id" cssErrorClass="error">Supplier</form:label><br/>
						<form:select path="supplierDelivery.supplier.id">
	            			<form:option value="" label="All"/>
            				<form:options items="${supplierList}" itemValue="id" itemLabel="name"/>
            			</form:select>							
	  	 </div>
          <div class="column w-33-percent">
				<form:label for="stockItem.title" path="stockItem.title" cssErrorClass="error">Stock Title</form:label><br/>
				<form:input path="stockItem.title"/><form:errors path="stockItem.title" />			
	  	 </div>
		  <div class="column w-33-percent">
	          <form:label for="stockItem.isbn" path="stockItem.isbn" cssErrorClass="error">ISBN</form:label><br/>
	          <form:input path="stockItem.isbn"/><form:errors path="stockItem.isbn" />
	  	</div>		
	  </div>
        <div class="row">
          <div class="column w-33-percent">
				<form:label for="supplierDelivery.id" path="supplierDelivery.id" cssErrorClass="error">Delivery No.</form:label><br/>
				<form:input path="supplierDelivery.id"/><form:errors path="supplierDelivery.id" />			
	  	 </div>
          <div class="column w-33-percent">
				<form:label for="supplierDelivery.invoiceNumber" path="supplierDelivery.invoiceNumber" cssErrorClass="error">Invoice Number</form:label><br/>
				<form:input path="supplierDelivery.invoiceNumber"/><form:errors path="supplierDelivery.invoiceNumber" />			
	  	 </div>	  	 
	  </div>
	  <div class="row">
          <div class="column w-100-percent">
				<input type="submit" class="btn btn-primary"/>
				<button type="button" onclick="javascript:reset()" class="btn btn-primary">Reset</button>
	  	 </div>
	  </div>
</div>	 
</form:form>
<br/>
<c:if test="${supplierDeliveryList != null}">
		<display:table 
			   name="supplierDeliveryList"
			   requestURI="search" 
			   decorator="org.bookmarks.ui.SearchSupplierDeliveryDecorator"
			   sort="external" 
			   defaultsort="2" 
			   defaultorder="ascending"
			   partialList="true"
			   size="${searchResultCount}"
			   pagesize="10"
			   id="searchTable">
			   		  <display:column sortable="true" sortProperty="so.id" property="id" title="No."/>
					  <display:column sortable="true" sortProperty="so.supplier.name" property="supplier.name" title="Supplier"/>
					  <display:column sortable="true" sortProperty="sd.invoiceNumber" property="invoiceNumber" title="Invoice No."/>
					  <display:column property="supplier.telephone1" title="Telephone"/>
					  <display:column property="supplier.supplierAccount.accountNumber" title="Acc."/>
					  <display:column property="creationDate" title="Delivery Date"/>
					  <display:column property="noOfLines" title="Lines"/>
					  <display:column property="link" title="Actions"/>
  		</display:table>
</c:if>		