<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
          	<label>Supplier</label><br/>
          	${supplierDelivery.supplier.name}
	  	 </div>
          <div class="column w-33-percent">
			<label>Order Number</label><br/>
          	${supplierDelivery.id}
          </div>
		  <div class="column w-33-percent">
			<label>Date Received</label><br/>
          	<fmt:formatDate value="${supplierDelivery.creationDate}" pattern="dd/MMM/yyyy"/>
          </div>
	  	</div>		
        <div class="row">
          <div class="column w-33-percent">
			<label>Status</label><br/>
          </div>
	  	</div>
</div>
<br/>
<br/>
<display:table 
		name="supplierDelivery.supplierDeliveryLine" 
		decorator="org.bookmarks.ui.SupplierDeliveryLineDecorator">
	  <display:column property="stockItem.isbn" title="ISBN"/>
	  <display:column property="title" title="Title"/>
	  <display:column property="stockItem.quantityInStock" title="In Stock"/>
	  <display:column property="amount" title="Quantity"/>
	  <display:column property="stockItem.sellPrice" title="Sell Price"/>
</display:table>
<br/>
<a href="${pageContext.request.contextPath}/supplierDelivery/searchFromSession"><button class="btn btn-primary">Back To Search</button></a>
