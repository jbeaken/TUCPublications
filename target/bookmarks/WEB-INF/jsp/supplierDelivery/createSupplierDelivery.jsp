<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
          	<label>Supplier Delivery for </label>${supplierDelivery.supplier.name} <br/>
          	<label>Invoice No. </label>${supplierDelivery.invoiceNumber}	
	  	 </div>
<c:if test="${lastSupplierDeliveryLine != null}">		 
          <div class="column w-70-percent">
          	<label>Last Line </label>${lastSupplierDeliveryLine.stockItem.title}<br/>
          	<label>Pub. Price </label>${lastSupplierDeliveryLine.publisherPrice}<br/>
          	<label>Discount </label>${lastSupplierDeliveryLine.discount}<br/>
			<label>Amount </label>${lastSupplierDeliveryLine.amount}			
	  	 </div>		
</c:if>		 
	  	</div>		
</div>
<c:if test="${supplierDeliveryLine != null}">
<br/>
<br/>
<legend>${supplierDeliveryLine.stockItem.title}</legend>
<form:form modelAttribute="supplierDeliveryLine" action="../supplierDelivery/editSupplierDeliveryOrderLine" method="post">
	<form:hidden path="hasCustomerOrderLines"/>
	<form:hidden path="stockItem.id"/>
	<form:hidden path="stockItem.type"/>
	<form:hidden path="stockItem.isbn"/>
	<form:hidden path="stockItem.mainAuthor"/>
	<form:hidden path="stockItem.category.name"/>
	<form:hidden path="stockItem.title"/>
	<form:hidden path="stockItem.quantityInStock"/>	
	<form:hidden path="stockItem.quantityOnLoan"/>	
	<form:hidden path="stockItem.quantityForCustomerOrder"/>
	<form:hidden path="stockItem.quantityReadyForCustomer"/>
	<form:hidden path="stockItem.quantityToKeepInStock"/>	
		<div class="rows">
		      <div class="row">
		          <div class="column w-33-percent">
		 				<form:label for="publisherPrice" path="publisherPrice" cssErrorClass="error">Publisher Price</form:label><br/>
		 				<form:input for="publisherPrice" required="required" id="publisherPrice" path="publisherPrice" onkeyup="javascript:calculatePricesFromPublisherPrice('costPrice')"/>&nbsp;<form:errors path="publisherPrice" />
			 	 </div>
		          <div class="column w-33-percent">
		 				<form:label for="sellPrice" path="sellPrice" cssErrorClass="error">Sell Price</form:label><br/>
		 				<form:input for="sellPrice" required="required" id="sellPrice" path="sellPrice"/>&nbsp;<form:errors path="sellPrice" />
			 	 </div>
		          <div class="column w-33-percent">
		 				<form:label for="costPrice" path="costPrice" cssErrorClass="error">Cost Price</form:label><br/>
		 				<form:input for="costPrice" id="costPrice" path="costPrice" onkeyup="javascript:calculatePricesFromCostPrice('costPrice')"/>&nbsp;<form:errors path="costPrice" />
			 	 </div>
			</div>
			<div class="row">
		          <div class="column w-33-percent">
		 				<form:label for="discount" path="discount" cssErrorClass="error">Discount</form:label><br/>
		 				<form:input for="discount" required="required" id="discount" path="discount" onkeyup="javascript:calculatePricesFromDiscount('discount')"/>&nbsp;<form:errors path="discount" />
			 	 </div>
		          <div class="column w-33-percent">
		 				<form:label for="amount" path="amount" cssErrorClass="error">Amount</form:label><br/>
		 				<form:input for="amount" required="required" path="amount" id="focus"/>&nbsp;<form:errors path="amount" />
			 	 </div>		          
				 <div class="column w-33-percent">
		 				<form:checkbox for="updateStockItemDiscount" path="updateStockItemDiscount"/>&nbsp;
		 				<form:label for="updateStockItemDiscount" path="updateStockItemDiscount">Update Stock Item with new Discount</form:label><br/>
		 				<form:checkbox for="updateStockItemCostPrice" path="updateStockItemCostPrice"/>&nbsp;
		 				<form:label for="updateStockItemCostPrice" path="updateStockItemCostPrice">Update Stock Item with new Cost Price</form:label><br/>
		 				<form:checkbox for="updateStockItemSellPrice" path="updateStockItemSellPrice"/>&nbsp;
		 				<form:label for="updateStockItemSellPrice" path="updateStockItemSellPrice">Update Stock Item with new Sell Price</form:label><br/>
		 				<form:checkbox for="updateStockItemPublisherPrice" path="updateStockItemPublisherPrice"/>&nbsp;
		 				<form:label for="updateStockItemPublisherPrice" path="updateStockItemPublisherPrice">Update Stock Item with new Publisher Price</form:label><br/>
               	</div>
			</div>		
		      <div class="row">
		          <div class="column w-33-percent">
		 				<input type="submit" class="btn btn-primary" value="Save" id="editSupplierDeliveryLineSubmitButton"/>
			 	 </div>
			</div>		
		</div>					
</form:form>
</c:if>
<br/>
<br/>
		<c:if test="${customerOrderLineList != null }">
				<form:form modelAttribute="customerOrderLineList" action="fillStock" method="post">				
			<fieldset>		
				<legend>Orders For Fill - Please select order to fill</legend>
					<display:table name="customerOrderLineList" 
						requestURI="" 
						id="searchTable"
						decorator="org.bookmarks.ui.SupplierDeliveryCustomerOrderLineDecorator">
					  <display:column sortable="true" property="id" title="Order Id"/>
					  <display:column sortable="true" property="customerName" title="Customer"/>
					  <display:column sortable="true" property="customerOrderStatus" title="Status"/>
					  <display:column sortable="true" property="stockItem.isbn" maxLength="13" title="ISBN"/>
					  <display:column sortable="true" property="stockItem.title" maxLength="35" title="Title"/>
					  <display:column sortable="true" property="amount" title="Amount"/>
					  <display:column title="Amount To Fill">
					  	<input type="text" name="${searchTable.id}" autofocus="autofocus" id="${searchTable.stockItem.id}" value="${searchTable.newAmount}" />
					  </display:column>
  					</display:table>
			</fieldset>
			<br/>
			<input type="submit" class="btn btn-primary"/>	
  				</form:form>
		</c:if>
<c:if test="${customerOrderLineList == null && supplierDeliveryLine == null}">	
	<form:form modelAttribute="supplierDeliverySearchBean" action="../supplierDelivery/addStock" method="post">
		<form:hidden path="supplierDelivery.supplier.name"/>
		<form:hidden path="supplierDelivery.invoiceNumber"/>
		<form:hidden path="supplierDelivery.supplier.id"/>
		<div class="rows">
	        <div class="row">
	          <div class="column w-100-percent">					
				<form:label	for="stockItem.isbn" path="stockItem.isbn" cssErrorClass="error">ISBN</form:label><br/>
				<form:input path="stockItem.isbn" autofocus="autofocus" required="required"/>&nbsp;<form:errors path="stockItem.isbn" />&nbsp;<form:errors path="supplierDelivery.supplierDeliveryLine" />
				<input type="submit" class="btn btn-primary" value="Add" id="addStockToSupplierDeliveryButton"/>&nbsp;
				<a href="${pageContext.request.contextPath}/supplierDelivery/editSupplierDeliveryOrderLine?id=${lastSupplierDeliveryLine.stockItem.id}">
					<button type="button" class="btn btn-danger" id="editLastLine">Edit Last Line</button>
				</a>
				<a href="${pageContext.request.contextPath}/supplierDelivery/setGlobalDiscount">
					<button type="button" class="btn btn-danger" id="setGlobalDiscountButton">Set Global Discount</button>
				</a>				
			</div>
		</div>
	</div>
	</form:form>		
<br/>	
<br/>	
<c:if test="${not empty supplierDeliveryLineList}">
	<display:table name="supplierDeliveryLineList" requestURI="" decorator="org.bookmarks.ui.SupplierDeliveryLineDecorator">
		  <display:column property="stockItem.isbn" title="ISBN"/>
		  <display:column property="title" maxLength="50" title="Title"/>
		  <display:column property="stockItem.category.name" title="Category"/>
		  <display:column property="stockItem.mainAuthor" title="Authors"/>
		  <display:column property="publisherPrice" title="Price"/>
		  <display:column property="discount" title="Discount"/>
		  <display:column property="totalPrice" title="Total Price"/>
		  <display:column property="amount" title="New Stock"/>
		  <display:column property="link" title="Actions"/>
	</display:table>
</c:if>  					
  					<br/>
  					<c:if test="${not empty totalPrice}">
  					Total Price : <fmt:formatNumber value="${totalPrice}" type="currency" currencyCode="GBP"/>
  					<br/><br/>
  					Retail Price : <fmt:formatNumber value="${retailPrice}" type="currency" currencyCode="GBP"/>
  					</c:if>
  					<br/><br/>
		<button class="btn btn-primary" type="button" onclick="javscript:submitForm('${pageContext.request.contextPath}/supplierDelivery/create')" id="placeIntoStockButton">Place in stock</button>
		<a href="${pageContext.request.contextPath}/supplierDelivery/cancel"><button class="btn btn-danger" type="button">Cancel</button></a>
</c:if>			
