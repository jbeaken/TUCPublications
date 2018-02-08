<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<script>

$(function() {
				$( "#startDate" ).datepicker({
					changeMonth: true,
					changeYear: true,
					dateFormat: 'dd/mm/yy'
				});
				$( "#endDate" ).datepicker({
					changeMonth: true,
					changeYear: true,
					dateFormat: 'dd/mm/yy'
				});
	});

	function printLabels() {
		$('form#customerOrderLineForm').attr("action", '${pageContext.request.contextPath}/customerOrderLine/printLabels');
		$('form#customerOrderLineForm').submit();
		$('form#customerOrderLineForm').attr("action", '${pageContext.request.contextPath}/customerOrderLine/search');
	}
</script>
<form:form modelAttribute="customerOrderLineSearchBean" action="search" method="post" id="customerOrderLineForm">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
				<form:label	for="customerOrderLine.customer.firstName" path="customerOrderLine.customer.firstName" cssErrorClass="error">First Name</form:label><br/>
				<form:input path="customerOrderLine.customer.firstName" id="focus"/><form:errors path="customerOrderLine.customer.firstName" />
	  	 </div>
		  <div class="column w-33-percent">
	          <form:label for="customerOrderLine.customer.lastName" path="customerOrderLine.customer.lastName" cssErrorClass="error">Last Name</form:label><br/>
	          <form:input path="customerOrderLine.customer.lastName"/>
	  	</div>
		  <div class="column w-33-percent">
	          <form:label for="customerOrderLine.id" path="customerOrderLine.id" cssErrorClass="error">Order ID</form:label><br/>
	          <form:input path="customerOrderLine.id"/>
	  	</div>
      </div>
        <div class="row">
          <div class="column w-33-percent">
				<form:label	for="customerOrderLine.stockItem.title" path="customerOrderLine.stockItem.title" cssErrorClass="error">Stock Title</form:label><br/>
				<form:input path="customerOrderLine.stockItem.title"/>
	  	 </div>
		  <div class="column w-33-percent">
	          <form:label for="customerOrderLine.stockItem.isbn" path="customerOrderLine.stockItem.isbn" cssErrorClass="error">ISBN</form:label><br/>
	          <form:input path="customerOrderLine.stockItem.isbn"/>
	  	</div>
          <div class="column w-33-percent">
		         <form:label for="customerOrderLine.paymentType" path="customerOrderLine.paymentType" cssErrorClass="error">Payment Type</form:label><br/>
       			<form:select path="customerOrderLine.paymentType">
        			<form:option value="" label="All"/>
        			<form:options items="${paymentTypeList}" itemLabel="displayName"/>
    			</form:select>
	 	 </div>
      </div>
	  <div class="row">
          <div class="column w-33-percent">
		         <form:label for="customerOrderLine.deliveryType" path="customerOrderLine.deliveryType" cssErrorClass="error">Delivery Type</form:label><br/>
       			<form:select path="customerOrderLine.deliveryType">
        			<form:option value="" label="All"/>
        			<form:options items="${deliveryTypeList}" itemLabel="displayName"/>
    			</form:select>
	  	 </div>
          <div class="column w-33-percent">
		         <form:label for="customerOrderLine.status" path="customerOrderLine.status" cssErrorClass="error">Status</form:label><br/>
       			<form:select path="customerOrderLine.status">
        			<form:option value="" label="All"/>
        			<form:options items="${statusOptions}" itemLabel="displayName"/>
    			</form:select>
	 	 </div>
          <div class="column w-33-percent">
		         <form:label for="bookmarksRole" path="bookmarksRole" cssErrorClass="error">Role</form:label><br/>
       			<form:select path="bookmarksRole">
        			<form:option value="" label="All"/>
        			<form:options items="${bookmarksRoleOptions}" itemLabel="displayName"/>
    			</form:select>
	 	 </div>
	  </div>
	  <div class="row">
          <div class="column w-33-percent">
		  	<form:label for="researchText" path="researchText" cssErrorClass="error">Research</form:label><br/>
       		<form:input path="researchText"/>
	  	 </div>
          <div class="column w-33-percent">
		  	<form:label for="customerOrderLine.source" path="customerOrderLine.source" cssErrorClass="error">Source</form:label><br/>
       		<form:select path="customerOrderLine.source">
        			<form:option value="" label="All"/>
        			<form:options items="${sourceOptions}" itemLabel="displayName"/>
    		</form:select>
	  	 </div>
          <div class="column w-33-percent">
		  	<form:label for="customerOrderLine.isSecondHand" path="customerOrderLine.isSecondHand" cssErrorClass="error">Is Second Hand?</form:label><br/>
        	<form:checkbox path="customerOrderLine.isSecondHand"/>
	  	 </div>
	  </div>
	  <div class="row">
          <div class="column w-33-percent">
		  	<form:label for="customerOrderLine.webReference" path="customerOrderLine.webReference">Web Reference</form:label><br/>
       		<form:input path="customerOrderLine.webReference" type="text"/>
	  	 </div>

			 <div class="column w-33-percent">
		 <form:label for="startDate" path="startDate">Start Date</form:label><br/>
			 <form:input path="startDate" type="text"/>
		</div>

		<div class="column w-33-percent">
	<form:label for="endDate" path="endDate">End Date</form:label><br/>
		<form:input path="endDate" type="text"/>
 </div>

	  </div>
	  <div class="row">
          <div class="column w-100-percent">
				<input type="submit" class="btn btn-primary"/>
				<a href="${pageContext.request.contextPath}/customerOrderLine/reset">
					<button type="button" class="btn btn-primary">Reset</button>
				</a>
				<c:if test="${customerOrderLineSearchBean.customerOrderLine.status == 'OUT_OF_STOCK'}">
						<a href="${pageContext.request.contextPath}/customerOrderLine/copyISBNs" target="_blank">
							<button type="button" class="btn btn-success">Copy ISBNs</button>
						</a>
				</c:if>
				<button type="button" class="btn btn-success" onclick="javascript:printLabels()">Print Labels</button>
	  	 </div>
	  </div>
</div>
</form:form>
<br/>
<c:if test="${customerOrderLineList != null}">
<c:if test="${customerOrderLineSearchBean.customerOrderLine.status == 'OUT_OF_STOCK'}">
<form:form modelAttribute="customerOrderLineList" action="processSupplierOrders" method="post">
					<display:table
			   name="customerOrderLineList"
			   requestURI="search"
			   decorator="org.bookmarks.ui.SearchCustomerOrderLineDecorator"
			   sort="external"
			   defaultsort="5"
			   defaultorder="ascending"
			   id="searchTable">
					  <display:column sortable="true" sortName="col.id"  property="id" title="No."/>
					  <display:column title="Image" style="max-width : 30px">
					    <c:if test="${searchTable.stockItem.imageURL != null}">
					      <img src="${searchTable.stockItem.imageURL}" style="max-width : 100%"/>
					    </c:if>
					    <c:if test="${searchTable.stockItem.imageURL == null}">
					      No Image
					    </c:if>
					</display:column>
					  <display:column sortable="true" sortName="col.creationDate"  property="creationDate" title="Date"/>
					  <display:column sortable="true" sortName="col.customer.lastName" property="customer" title="Customer"/>
					  <display:column sortable="true" sortName="col.stockItem.isbn" property="stockItem.isbn" title="ISBN"/>
					  <display:column property="type" title="Type" media="html"/>
					  <display:column property="price" title="Sell Price"/>
					  <display:column sortable="true" sortName="col.stockItem.title" maxLength="35" property="title" title="Title"/>
					  <display:column sortable="true" sortName="col.amount" property="amount" title="Amount"/>
					  <display:column sortable="true" sortName="psup.name" property="supplier" title="Supplier"/>
					  <display:column property="note" title="Note" style="width:10%"/>
					  <display:column title="Actions" media="html" style="width:10%">
					  	<div class="btn-group">
						  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
						    Action <span class="caret"></span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
						  	<c:if test="${searchTable.canBeFilled}">
						    	<li><a href="${pageContext.request.contextPath}/customerOrderLine/fill?customerOrderLineId=${searchTable.id}">Fill</a></li>
						  	</c:if>
						  	<c:if test="${searchTable.canComplete}">
						    	<li><a href="${pageContext.request.contextPath}/customerOrderLine/complete?flow=searchCustomerOrderLinesGo&customerOrderLineId=${searchTable.id}">Raise Invoice &amp; Go</a></li>
						  		<c:if test="${searchTable.paymentType != 'ACCOUNT'}">
						    		<li><a href="${pageContext.request.contextPath}/customerOrderLine/complete?customerOrderLineId=${searchTable.id}">Sell Out</a></li>
						  		</c:if>
						  	</c:if>
						    <li><a href="${pageContext.request.contextPath}/customerOrderLine/complete?flow=searchCustomerOrderLinesStay&customerOrderLineId=${searchTable.id}">Raise Invoice &amp; Stay</a></li>
						    <li class="divider"></li>
						    <li><a href="${pageContext.request.contextPath}/supplierOrderLine/viewSupplierOrderLine?customerOrderLine.id=${searchTable.id}">View Supplier Order</a></li>
						    <li><a href="${pageContext.request.contextPath}/supplierOrderLine/displayCreateForCustomerOrder?customerOrderLineId=${searchTable.id}&amount=${searchTable.amount}&stockItemId=${searchTable.stockItem.id}&supplierId=${searchTable.stockItem.preferredSupplier.id}&flow=searchCustomerOrder" target="_blank">Create Supplier Order</a></li>
								<c:if test="${searchTable.customer.contactDetails.email != null}">
									<li><a href="mailto:${searchTable.customer.contactDetails.email}?Subject=Your%20Bookmarks%20Bookshop%20Order&body=${searchTable.stockItem.title}">Email Customer</a></li>
								</c:if>
						    <li class="divider"></li>
						    <li><a href="${pageContext.request.contextPath}/customerOrderLine/displayEditNote?id=${searchTable.id}" target="_blank">Edit Note</a></li>
						    <li><a href="${pageContext.request.contextPath}/customerOrderLine/edit?id=${searchTable.id}&flow=search">Edit</a></li>
						    <li><a href="${pageContext.request.contextPath}/customerOrderLine/view?id=${searchTable.id}&flow=search">View</a></li>
						    <li class="divider"></li>
							<c:if test="${searchTable.canCancel}">
						    	<li><a href="javascript:confirmationWithUrl('${pageContext.request.contextPath}/customerOrderLine/cancel?flow=searchCustomerOrderLinesStay&customerOrderLineId=${searchTable.id}', 'Are you sure? This will put stock back into stock.')">Cancel</a></li>
						  	</c:if>						    
						  </ul>
						</div>
					  </display:column>
  					</display:table>
</form:form>
</c:if>
<c:if test="${customerOrderLineSearchBean.customerOrderLine.status != 'OUT_OF_STOCK'}">
	<display:table
			   name="customerOrderLineList"
			   requestURI="search"
			   decorator="org.bookmarks.ui.SearchCustomerOrderLineDecorator"
			   sort="external"
			   export="true"
			   defaultsort="4"
			   defaultorder="ascending"
			   partialList="true"
			   size="${searchResultCount}"
			   pagesize="10"
			   id="searchTable">
	   <display:setProperty name="export.pdf" value="true" />
	   <display:setProperty name="export.xml" value="false" />
	   <display:setProperty name="export.pdf.filename" value="customerOrders.pdf"/>
	   <display:setProperty name="export.csv.filename" value="customerOrders.txt"/>
					  <display:column sortable="true" sortName="col.id" property="id" title="No." media="html"/>
					  <display:column title="Image" style="max-width: 40px">
						    <c:if test="${searchTable.stockItem.imageURL != null}">
						      <img src="${searchTable.stockItem.imageURL}" style="max-width : 100%"/>
						    </c:if>
						    <c:if test="${searchTable.stockItem.imageURL == null}">
						      No Image
						    </c:if>
					</display:column>
					  <display:column sortable="true" sortName="col.id" property="rawId" title="No." media="pdf"/>
					  <display:column sortable="true" sortName="col.creationDate"  property="creationDate" title="Date"/>
					  <display:column sortable="true" sortName="col.customer.lastName" maxLength="15" property="customerName" title="Customer" media="html"/>
					  <display:column maxLength="15" property="rawCustomerName" title="Customer" media="pdf"/>
					  <display:column property="telephoneNumber" maxLength="14" title="Telephone"/>
					  <display:column sortable="true" sortName="col.status" maxLength="15" property="status.displayName" title="Status"/>
					  <display:column property="invoiceId" title="Invoice"/>
					  <display:column sortable="true" sortName="col.deliveryType" property="type" title="Type" media="html"/>
					  <display:column property="rawType" title="Type" media="pdf"/>
					  <%--
					  <display:column sortable="true" sortName="col.deliveryType" property="deliveryType.displayName" title="Delivery Type"/>
					  <display:column sortable="true" sortName="col.paymentType" property="paymentType.displayName" title="Payment Type"/>
					   --%>
					  <display:column sortable="true" sortName="col.stockItem.isbn" property="stockItem.isbn" title="ISBN"/>
					  <display:column sortable="true" sortName="col.stockItem.title" property="title" title="Title" media="html"/>
					   <display:column maxLength="30" property="rawTitle" title="Title" media="pdf"/>
					  <display:column sortable="true" sortName="col.amount" property="amount" title="Amount"/>
					  <display:column property="note" title="Note" />
					  <display:column title="Actions" media="html" >
					  	<div class="btn-group">
						  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
						    Action <span class="caret"></span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
						  	<c:if test="${searchTable.canBeFilled}">
						    	<li><a href="${pageContext.request.contextPath}/customerOrderLine/fill?customerOrderLineId=${searchTable.id}">Fill</a></li>
						  	</c:if>
						  	<c:if test="${searchTable.canComplete}">
						  		<c:if test="${searchTable.paymentType == 'ACCOUNT'}">
						    		<li><a href="${pageContext.request.contextPath}/customerOrderLine/complete?flow=searchCustomerOrderLinesGo&customerOrderLineId=${searchTable.id}">Raise Invoice & Go</a></li>
						  		</c:if>
						  		<c:if test="${searchTable.paymentType != 'ACCOUNT'}">
						    		<li><a href="${pageContext.request.contextPath}/customerOrderLine/complete?customerOrderLineId=${searchTable.id}">Sell Out</a></li>
						  		</c:if>
						  	</c:if>
						  	<c:if test="${searchTable.canComplete && searchTable.paymentType == 'ACCOUNT'}">
						    	<li><a href="${pageContext.request.contextPath}/customerOrderLine/complete?flow=searchCustomerOrderLinesStay&customerOrderLineId=${searchTable.id}">Raise Invoice & Stay</a></li>
						  	</c:if>
						    <li><a href="${pageContext.request.contextPath}/supplierOrderLine/viewSupplierOrderLine?id=${searchTable.id}" target="_blank">View Supplier Order</a></li>
						    <li><a href="${pageContext.request.contextPath}/supplierOrderLine/displayCreateForCustomerOrder?customerOrderLineId=${searchTable.id}&amount=${searchTable.amount}&stockItemId=${searchTable.stockItem.id}&supplierId=${searchTable.stockItem.preferredSupplier.id}&flow=searchCustomerOrder" target="_blank">Create Supplier Order</a></li>
								<c:if test="${searchTable.customer.contactDetails.email != null}">
									<li><a href="mailto:${searchTable.customer.contactDetails.email}?Subject=Your%20Bookmarks%20Bookshop%20Order&body=${searchTable.stockItem.title}">Email Customer</a></li>
								</c:if>
						    <li class="divider"></li>
						    <li><a href="${pageContext.request.contextPath}/customerOrderLine/displayEditNote?id=${searchTable.id}" target="_blank">Edit Note</a></li>
						    <li><a href="${pageContext.request.contextPath}/customerOrderLine/edit?id=${searchTable.id}&flow=search">Edit</a></li>
						    <li><a href="${pageContext.request.contextPath}/customerOrderLine/view?id=${searchTable.id}&flow=search">View</a></li>
							<c:if test="${searchTable.canCancel}">
						    	<li><a href="javascript:confirmationWithUrl('${pageContext.request.contextPath}/customerOrderLine/cancel?flow=searchCustomerOrderLinesStay&customerOrderLineId=${searchTable.id}', 'Are you sure? This will put stock back into stock.')">Cancel</a></li>
						  	</c:if>							    
						  </ul>
						</div>
					  </display:column>
  	</display:table>
</c:if>
</c:if>
