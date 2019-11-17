<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<c:if test="${supplierOrderLine == null}">
<form:form action="sendToSupplier" modelAttribute="supplierOrder">
<form:hidden path="id"/>
			<form:hidden path="note"/>
			<form:hidden path="creationDate"/>
<form:hidden path="supplierOrderStatus"/>
<form:hidden path="supplier.name"/>
<form:hidden path="supplier.id"/>
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
				<label>Supplier</label><br/>${supplierOrder.supplier.name}
	  </div>	
          <div class="column w-33-percent">
			<label>Telephone No.</label><br/>
          		${supplierOrder.supplier.telephone1}
          </div>
          <div class="column w-33-percent">
			<label>Account Number</label><br/>
          		${supplierOrder.supplier.supplierAccount.accountNumber}
          </div>		  
	</div>	
        <div class="row">
          <div class="column w-100-percent">
				<c:if test="${supplierOrder.supplierOrderStatus == 'PENDING'}">
					<button class="btn btn-primary" type="button" id="sendToSupplierButton"
						onclick="javascript:confirmation('sendToSupplier', 'Are you sure you want to send order to ${supplierOrder.supplier.name}?')">
						Send Order To ${supplierOrder.supplier.name}
					</button> 
				</c:if>
					&nbsp;
					<a href="${pageContext.request.contextPath}/supplierOrder/searchFromSession">
						<button type="button" class="btn btn-primary">Back To Search</button>
					</a>
					<a href="${pageContext.request.contextPath}/supplierOrder/markAllForHold">
						<button type="button" class="btn btn-primary">All on Hold</button>
					</a>
					<a href="${pageContext.request.contextPath}/supplierOrder/markAllForReadyToSend">
						<button type="button" class="btn btn-primary">All Ready To Send</button>
					</a>
					<a href="${pageContext.request.contextPath}/supplierOrder/markCustomerOrdersReadyToSend">
						<button type="button" class="btn btn-primary">Customer Orders Ready To Send</button>
					</a>
	 	 </div>
	  </div> 
</div>
</form:form>
</c:if>
<c:if test="${supplierOrderLine != null}">
<label>Edit ${supplierOrderLine.stockItem.title}</label>
<br/>
<form:form modelAttribute="supplierOrderLine" action="editSupplierOrderLine" method="post">
	<form:hidden path="id"/>
	<form:hidden path="supplierOrderLineStatus"/>
	<form:hidden path="stockItem.title"/>
	<form:hidden path="stockItem.isbn"/>
	<form:hidden path="stockItem.id"/>
	<form:hidden path="stockItem.sellPrice"/>
	<form:hidden path="customerOrderLine.id"/>
		<div class="rows">
		      <div class="row">
		          <div class="column w-33-percent">
		 				<form:label for="amount" path="amount" id="focus">Amount</form:label><br/>
		 				<form:input for="amount" path="amount"/>&nbsp;<form:errors path="amount" />
			 	 </div>
		          <div class="column w-33-percent">
								<form:label for="supplier.id" path="supplier.id" cssErrorClass="error">Supplier</form:label><br/>
								<form:select path="supplier.id">
		            				<form:options items="${supplierList}" itemValue="id" itemLabel="name"/>
		            			</form:select>					
			  	 </div>
			  </div>
			  <div class="row">
		          <div class="column w-100-percent">
		 				<br/><input type="submit" id="editSupplierOrderLineSubmitButton" class="btn btn-primary"/>
			 	 </div>
			</div>		
		</div>					
</form:form>
</c:if>
<c:if test="${supplierOrderLine == null}">
<br/>
<br/>
<display:table 
		name="supplierOrder.supplierOrderLines" 
		decorator="org.bookmarks.ui.SupplierOrderLineDecorator">
	  <display:column property="creationDate" title="Date Added"/>
	  <display:column property="status" title="Status"/>
	  <display:column property="stockItem.isbn" title="ISBN"/>
	  <display:column property="title" title="Title"/>
	  <display:column property="customerOrder" title="Order No."/>
	  <display:column property="stockItem.quantityInStock" title="In Stock"/>
	  <display:column property="amount" title="Quantity"/>
	  <display:column property="stockItem.sellPrice" title="Sell Price"/>
	  <display:column property="link" title="Actions" />
</display:table>
</c:if>