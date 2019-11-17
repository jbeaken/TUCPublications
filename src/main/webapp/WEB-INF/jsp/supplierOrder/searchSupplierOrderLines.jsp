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
<form:form modelAttribute="supplierOrderLineSearchBean" action="search" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
				<form:label for="supplierOrderLine.id" path="supplierOrderLine.id" cssErrorClass="error">Supplier Order No.</form:label><br/>
				<form:input path="supplierOrderLine.id"/><form:errors path="supplierOrderLine.id" />				
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
				<form:label for="supplierOrderLine.id" path="supplierOrderLine.id" cssErrorClass="error">Customer Order No.</form:label><br/>
				<form:input path="customerOrderLine.id"/><form:errors path="customerOrderLine.id" />			
	  	 </div>
          <div class="column w-33-percent">
				<form:label for="supplierOrderLine.supplierOrderLineStatus" path="supplierOrderLine.supplierOrderLineStatus" cssErrorClass="error">Status</form:label><br/>
				<form:select path="supplierOrderLine.supplierOrderLineStatus">
	            	<form:option value="" label="All"/>
            		<form:options items="${supplierOrderLineStatusList}" itemLabel="displayName"/>
            	</form:select>							
	  	 </div>		
          <div class="column w-33-percent">
				<form:label for="supplierOrderLine.supplier.id" path="supplierOrderLine.supplier.id" cssErrorClass="error">Supplier</form:label><br/>
				<form:select path="supplierOrderLine.supplier.id" id="focus">
           			<form:option value="" label="All"/>
          			<form:options items="${supplierList}" itemValue="id" itemLabel="name"/>
          		</form:select>			   
	  	 </div>
	  </div>
        <div class="row">
         	<div class="column w-33-percent">
				<form:label for="supplierOrderLine.priority" path="supplierOrderLine.priority" cssErrorClass="error">Priority</form:label><br/>
				<form:select path="supplierOrderLine.priority" id="focus">
        			<form:option value="" label="All"/>
    				<form:options items="${levelList}" itemLabel="displayName"/>
    			</form:select>	
			</div>
         	<div class="column w-33-percent">
				<form:label for="supplierOrderLine.type" path="supplierOrderLine.type" cssErrorClass="error">Type</form:label><br/>
				<form:select path="supplierOrderLine.type">
        			<form:option value="" label="All"/>
    				<form:options items="${supplierOrderLineTypeList}" itemLabel="displayName"/>
    			</form:select>	
			</div>			
	  </div>
	  <div class="row">
          <div class="column w-100-percent">
				<input type="submit" class="btn btn-primary" value="Search" id="searchsupplierOrderLinesButton"/>
				<button type="button" onclick="reset()" class="btn btn-primary" id="resetButton">Reset</button>
				<a href="${pageContext.request.contextPath}/supplierOrderLine/sendAllToSupplier" class="btn btn-success" id="sendAllToSupplierButton">Send All To Supplier</a>				
				<a href="${pageContext.request.contextPath}/supplierOrderLine/putAllOnHold" class="btn btn-success" id="putAllOnHoldButton">Put All On Hold</a>
				<a href="${pageContext.request.contextPath}/supplierOrderLine/takeAllOffHold" class="btn btn-success" id="takeAllOffHoldButton">Take All Off Hold</a>
				<a href="${pageContext.request.contextPath}/supplierOrderLine/copyISBNs" class="btn btn-primary" target="_blank" id="copyISBNsButton">Copy ISBNs</a>
				<a href="${pageContext.request.contextPath}/supplierOrderLine/createTable" class="btn btn-primary" target="_blank" id="createTableButton">Create Table</a>
	  	 </div>
	  </div>
</div>	
</form:form>
<br/>
<c:if test="${supplierOrderLineList != null}">
		<display:table 
			   name="supplierOrderLineList"
			   requestURI="search" 
			   decorator="org.bookmarks.ui.SearchSupplierOrderLineDecorator"
			   sort="external" 
			   defaultsort="2" 
			   export="true"
			   defaultorder="ascending"
			   partialList="true"
			   size="${searchResultCount}"
			   pagesize="10"
			   id="searchTable">
			   		  <display:column sortable="true" sortName="sol.id" sortProperty="sol.id" property="id" title="No."/>
			   		  <display:column sortable="true" sortName="sol.priority" property="priority.displayName" title="Priority"/>
			   		  <display:column property="amount" title="Amount"/>
			   		  <display:column property="type" title="Type"/>
					  <display:column sortable="true" sortName="sup.name" property="supplier" title="Supplier"/>
					  <display:column sortable="true" sortName="sol.supplierOrderLineStatus" property="supplierOrderLineStatus.displayName" title="Status"/>
					  <display:column sortable="true" sortName="si.title" property="title" title="Title"/>
					  <display:column property="isbn" sortName="si.isbn" title="ISBN"/>
					  <display:column property="sendDate" sortName="sol.sendDate" title="Date Sent"/>
					  <display:column title="Actions" media="html" style="width:10%">
					  	<div class="btn-group">
						  <button type="button" class="btn btn-warning dropdown-toggle" data-toggle="dropdown">
						    Action <span class="caret"></span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
						    <li><a href="${pageContext.request.contextPath}/supplierOrderLine/toggleOnHold?id=${searchTable.id}">Toggle Hold</a></li>
						    <li><a href="${pageContext.request.contextPath}/supplierOrderLine/edit?id=${searchTable.id}" target="_blank">Edit</a></li>
						    <li><a href="${pageContext.request.contextPath}/supplierOrderLine/sendToSupplier?id=${searchTable.id}">Send To Supplier</a></li>
						    <li class="divider"></li>
						    <li><a href="${pageContext.request.contextPath}/supplierOrderLine/delete?id=${searchTable.id}">Delete</a></li>
						    <li><a href="${pageContext.request.contextPath}/supplierOrderLine/displayEditNote?id=${searchTable.id}" target="_blank">Edit Note</a></li>						   
						  </ul>
						</div>
					  </display:column>					  
  		</display:table>
</c:if>		