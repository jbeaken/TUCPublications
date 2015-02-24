<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<script>
	$(function() {
		$("#customerAutoComplete").autocomplete( {
			source: "${pageContext.request.contextPath}/customer/autoCompleteSurname",
			minLength: 3,
			select: function( event, ui ) {
				$("#customerAutoComplete").val(ui.item.label);
				$("#customerId").val(ui.item.value);
				$("#searchCustomerSubmitButton").click();
				return false;
			},
			focus: function( event, ui ) {
				$("#customerAutoComplete").val(ui.item.label);
				return false;
			}
		});
		$('#customerId').val(''); //clear customer id
		$('#customerAutoComplete').focus();
	});
</script>
<form:form modelAttribute="customerSearchBean" action="${pageContext.request.contextPath}/customer/search" method="post">
<form:hidden path="customer.id" id="customerId"/>
<div class="row">
        <div class="col-sm-4">
	        <div class="form-group">	  
				<label>Customer</label>
				<input type="text" id="customerAutoComplete" class="form-control"/>
			</div> 
		</div>
          <div class="col-sm-4">
          	<div class="form-group">	 
				<label>Wide Search</label>
				<form:input path="customer.firstName" class="form-control"/>
			</div> 		
	  	 </div>
          <div class="col-sm-4">
          	<div class="form-group">	 
		         <label>Address</label>
		         <form:input path="customer.address.address1" class="form-control"/>
		    </div>                
	 	 </div>
</div>
        <div class="row">
		  <div class="col-sm-4">
		  	<div class="form-group">
				<label>Type</label>
				<form:select path="customer.customerType" class="form-control">
        			<form:option value="" label="All"/>
        			<form:options items="${customerTypeOptions}" itemLabel="displayName"/>
    			</form:select>      
    		</div>                 
	 	 </div>      	 
		  <div class="col-sm-4">
		  	<div class="checkbox">	 
				<label>
				  <form:checkbox path="customer.bookmarksAccount.accountHolder" />Account
				</label>
			</div>
			<div class="checkbox">	 
				<label>
				   <form:checkbox path="customer.bookmarksAccount.sponsor" />Sponsor   
				</label>
			</div>
			<div class="checkbox">					
				<label>
				  <form:checkbox path="customer.bookmarksAccount.paysInMonthly" />Pays In Monthly
				</label>		  
			</div>
		  </div>
		 </div>
		<div class="row">
          <div class="button-row">
 				<button id="searchCustomerSubmitButton" type="button" class="btn btn-primary" onclick="javascript:submitForm('${pageContext.request.contextPath}/customer/search')">Search</button>
	 				<a href="${pageContext.request.contextPath}/customer/displaySearch">
	 					<button type="button" class="btn btn-primary">Reset</button>
	 				</a>
	 				<button type="button" class="btn btn-primary" onclick="javascript:submitForm('${pageContext.request.contextPath}/customer/addCustomerFromSearch')">Add Customer</button>
	 	 </div>
	</div>		
</form:form>

<br/>
<c:if test="${customerList != null}">
            <display:table name="customerList"
               requestURI="search"
               decorator="org.bookmarks.ui.SearchCustomersDecorator"
               sort="external"
               class="table table-striped table-bordered table-hover table-condensed"
               defaultsort="2"
               defaultorder="ascending"
               export="true"
               partialList="true"
               pagesize="${pageSize}"
               size="${searchResultCount}"
               id="searchTable">
       <display:setProperty name="export.pdf" value="true" />
       <display:setProperty name="export.xml" value="false" />
       <display:setProperty name="export.pdf.filename" value="customer.pdf"/>
       <display:setProperty name="export.csv.filename" value="customer.txt"/>
                      <display:column property="id" title="ID" />
                      <display:column property="name" sortable="true" sortName="c.lastName"  maxLength="25" title="Name"/>
                      <display:column property="address" sortable="true" sortName="c.address.address1"  maxLength="40"  title="Address"/>
                      <display:column property="phoneNumber" maxLength="12" title="Phone"/>
                      <display:column property="contactDetails.email" sortable="true" sortName="c.contactDetails.email" maxLength="19" title="Email"/>
                      <display:column property="account" sortable="true" sortName="c.bookmarksAccount.amount"  title="Account" />

<display:column title="Actions" media="html" style="width:10%">
					  	<div class="btn-group">
						  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
						    Action <span class="caret"></span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
						    <li><a href="${pageContext.request.contextPath}/customerOrder/init?customerId=${searchTable.id}">Create Order</a></li>
						    <li><a href="${pageContext.request.contextPath}/customerOrderLine/searchByCustomerID?id=${searchTable.id}">View Orders</a></li>
						    <li><a href="${pageContext.request.contextPath}/invoice/init?customerId=${searchTable.id}">Create Invoice</a></li>
						    <li><a href="${pageContext.request.contextPath}/customer/edit?id=${searchTable.id}&flow=search">Edit</a></li>
						    
  							<li><a onclick='javascript:authoriseUser("delete?id=${searchTable.id}")'>Delete</a></li>
						    
						    <li class="divider"></li>
						    
						    <li><a href="${pageContext.request.contextPath}/saleOrReturn/init?id=${searchTable.id}">Sale Or Return</a></li>
						    <li><a href="displayEditNote?id=${searchTable.id}" target="_blank">Add Note</a></li>
						  </ul>
						</div>
</display:column>

                      </display:table>
 </c:if>
 
