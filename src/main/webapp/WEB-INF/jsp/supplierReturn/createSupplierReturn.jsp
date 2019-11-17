<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
          	<label>Supplier Return for </label>${supplierReturn.supplier.name} <br/>
          	<label>Returns No. </label>${supplierReturn.returnsNumber}
	  	 </div>

<c:if test="${lastSupplierReturnLine != null}">
          <div class="column w-70-percent">
          	<label>Last Line </label>${lastSupplierReturnLine.stockItem.title}<br/>
          	<label>Pub. Price </label>${lastSupplierReturnLine.stockItem.publisherPrice}<br/>
			<label>Amount </label>${lastSupplierReturnLine.amount}
	  	 </div>
</c:if>
	  	</div>
</div>

<c:if test="${supplierReturnLine != null}">

<br/>
<br/>

<legend>${supplierReturnLine.stockItem.title}</legend>
<form:form modelAttribute="supplierReturnLine" action="../supplierReturn/editSupplierReturnOrderLine" method="post">
  <form:hidden path="id"/>
	<form:hidden path="stockItem.id"/>
		<div class="rows">
		  <div class="row">

			</div>
			<div class="row">
		     <div class="column w-33-percent">
		 				<form:label for="amount" path="amount" cssErrorClass="error">Amount</form:label><br/>
		 				<form:input for="amount" required="required" path="amount" id="focus"/>&nbsp;<form:errors path="amount" />
			 	 </div>
			</div>
		      <div class="row">
		          <div class="column w-33-percent">
		 				<input type="submit" class="btn btn-primary" value="Save" id="editSupplierReturnLineSubmitButton"/>
			 	 </div>
			</div>
		</div>
</form:form>
</c:if>
<br/>
<br/>

<c:if test="${supplierReturnLine == null}">
	<form:form modelAttribute="supplierReturnSearchBean" action="../supplierReturn/addStock" method="post">
		<form:hidden path="supplierReturn.supplier.name"/>
		<form:hidden path="supplierReturn.returnsNumber"/>
		<form:hidden path="supplierReturn.supplier.id"/>
		<div class="rows">
	        <div class="row">
	          <div class="column w-100-percent">
				<form:label	for="stockItem.isbn" path="stockItem.isbn" cssErrorClass="error">ISBN</form:label><br/>

				<form:input path="stockItem.isbn" autofocus="autofocus" required="required"/>&nbsp;<form:errors path="stockItem.isbn" />&nbsp;<form:errors path="supplierReturn.supplierReturnLine" />
				<input type="submit" class="btn btn-primary" value="Add" id="addStockToSupplierReturnButton"/>&nbsp;

				<a href="/supplierReturn/editSupplierReturnOrderLine?id=${lastSupplierReturnLine.stockItem.id}">
					<button type="button" class="btn btn-danger" id="editLastLine">Edit Last Line</button>
				</a>
			</div>
		</div>
	</div>
	</form:form>

<br/>
<br/>

<c:if test="${not empty supplierReturnLineList}">
	<display:table name="supplierReturnLineList" requestURI="" decorator="org.bookmarks.ui.SupplierReturnLineDecorator">
		  <display:column property="stockItem.isbn" title="ISBN"/>
		  <display:column property="title" maxLength="50" title="Title"/>
		  <display:column property="stockItem.category.name" title="Category"/>
		  <display:column property="stockItem.mainAuthor" title="Authors"/>
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
		<button class="btn btn-primary" type="button" onclick="javascript:submitForm('/supplierReturn/create')" id="placeIntoStockButton">Save Supplier Return</button>
		<a href="/supplierReturn/cancel"><button class="btn btn-danger" type="button">Cancel</button></a>
</c:if>
