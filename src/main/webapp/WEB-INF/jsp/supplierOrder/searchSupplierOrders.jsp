<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<script>
	$(function(){
		 $("#focus").chosen();
	});
</script>
<form:form modelAttribute="supplierOrderSearchBean" action="search" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
				<form:label for="customerOrderLine.id" path="customerOrderLine.id" cssErrorClass="error">Customer Order No.</form:label><br/>
				<form:input path="customerOrderLine.id"/><form:errors path="customerOrderLine.id" />				
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
				<form:label for="supplierOrder.id" path="supplierOrder.id" cssErrorClass="error">Order No.</form:label><br/>
				<form:input path="supplierOrder.id"/><form:errors path="supplierOrder.id" />			
	  	 </div>
          <div class="column w-33-percent">
				<form:label for="supplierOrder.supplierOrderStatus" path="supplierOrder.supplierOrderStatus" cssErrorClass="error">Status</form:label><br/>
				<form:select path="supplierOrder.supplierOrderStatus">
	            			<form:option value="" label="All"/>
            				<form:options items="${supplierOrderStatusList}" itemLabel="displayName"/>
            			</form:select>							
	  	 </div>		
          <div class="column w-33-percent">
				<form:label for="supplierOrder.supplier.id" path="supplierOrder.supplier.id" cssErrorClass="error">Supplier</form:label><br/>
						<form:select path="supplierOrder.supplier.id" id="focus">
	            			<form:option value="" label="All"/>
            				<form:options items="${supplierList}" itemValue="id" itemLabel="name"/>
            			</form:select>			   
	  	 </div>		
	  </div>
        <div class="row">
          <div class="column w-33-percent">
				<form:checkbox path="containsCustomerOrderLines"/>
				<form:label for="containsCustomerOrderLines" path="containsCustomerOrderLines" cssErrorClass="error">Customer Orders</form:label>
				<br/>
				<form:checkbox path="supplierOrder.supplier.isMarxismSupplier" />
               <form:label for="supplierOrder.supplier.isMarxismSupplier" path="supplierOrder.supplier.isMarxismSupplier" cssErrorClass="error">Marxism Order</form:label>
				<br/> 	  			
	  	 </div>
	  </div>
	  <div class="row">
          <div class="column w-100-percent">
				<input type="submit" class="btn btn-primary" value="Search" id="searchSupplierOrdersButton"/>
				<button type="button" onclick="javascript:reset()" class="btn btn-primary">Reset</button>
	  	 </div>
	  </div>
</div>	
</form:form>
<br/>
<c:if test="${supplierOrderList != null}">
		<display:table 
			   name="supplierOrderList"
			   requestURI="search" 
			   decorator="org.bookmarks.ui.SearchSupplierOrderDecorator"
			   sort="external" 
			   defaultsort="2" 
			   defaultorder="ascending"
			   partialList="true"
			   size="${searchResultCount}"
			   pagesize="10"
			   id="searchTable">
			   		  <display:column sortable="true" sortProperty="so.id" property="id" title="No."/>
					  <display:column sortable="true" sortProperty="so.supplier.name" property="supplier.name" title="Supplier"/>
					  <display:column sortable="true" sortProperty="so.supplierOrderStatus" property="supplierOrderStatus.displayName" title="Status"/>
					  <display:column property="supplier.telephone1" title="Telephone"/>
					  <display:column property="supplier.supplierAccount.accountNumber" title="Acc."/>
					  <display:column property="noOfLines" title="No. Of Lines"/>
					  <display:column property="sendDate" title="Date Sent To Supplier"/>
					  <display:column property="link" title="Actions"/>
  		</display:table>
</c:if>		