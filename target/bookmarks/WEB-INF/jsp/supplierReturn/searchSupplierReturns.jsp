<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="supplierReturnSearchBean" action="search" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
				<form:label for="supplierReturn.supplier.id" path="supplierReturn.supplier.id" cssErrorClass="error">Supplier</form:label><br/>
				<form:select path="supplierReturn.supplier.id">
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
				<form:label for="supplierReturn.id" path="supplierReturn.id" cssErrorClass="error">Return No.</form:label><br/>
				<form:input path="supplierReturn.id"/><form:errors path="supplierReturn.id" />
	  	 </div>
          <div class="column w-33-percent">
				<form:label for="supplierReturn.returnsNumber" path="supplierReturn.returnsNumber" cssErrorClass="error">Returns Number</form:label><br/>
				<form:input path="supplierReturn.returnsNumber"/><form:errors path="supplierReturn.returnsNumber" />
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
<c:if test="${supplierReturnList != null}">
		<display:table
			   name="supplierReturnList"
			   requestURI="search"
			   decorator="org.bookmarks.ui.SearchSupplierReturnDecorator"
			   sort="external"
			   defaultsort="2"
			   defaultorder="ascending"
			   partialList="true"
			   size="${searchResultCount}"
			   pagesize="10"
			   id="searchTable">
			   		<display:column sortable="true" sortProperty="so.id" property="id" title="#"/>
            <display:column property="status" title="Status"/>
					  <display:column sortable="true" sortProperty="so.supplier.name" property="supplier.name" title="Supplier"/>
					  <display:column sortable="true" sortProperty="sd.returnsNumber" property="returnsNumber" title="Returns No."/>
					  <display:column property="supplier.telephone1" title="Telephone"/>
					  <display:column property="supplier.supplierAccount.accountNumber" title="Acc."/>
					  <display:column property="dateSentToSupplier" title="Sent to Supplier"/>
					  <display:column property="noOfLines" title="Lines"/>
					  <display:column property="link" title="Actions"/>
  		</display:table>
</c:if>
