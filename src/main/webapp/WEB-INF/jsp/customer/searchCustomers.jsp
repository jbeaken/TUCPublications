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
	function printLabels(noOfLabels) {
		$('form#customerForm').attr("action", '${pageContext.request.contextPath}/customer/printLabels/' + noOfLabels);
		$('form#customerForm').submit();
		$('form#customerForm').attr("action", '${pageContext.request.contextPath}/customer/search');
	}
</script>
<form:form modelAttribute="customerSearchBean" id="customerForm" action="${pageContext.request.contextPath}/customer/search" method="post">
<form:hidden path="customer.id" id="customerId"/>
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
					<div class="demo">
						<div class="ui-widget">
							<form:label	for="customer.id" path="customer.id" cssErrorClass="error">Customer</form:label><br/>
							<input type="text" id="customerAutoComplete"/> <form:errors path="customer.id" />
						</div>
					</div>
		</div>
          <div class="column w-33-percent">
				<form:label	for="customer.firstName" path="customer.firstName" cssErrorClass="error">Wide Search</form:label><br/>
				<form:input path="customer.firstName" /> <form:errors path="customer.firstName" />

	  	 </div>
          <div class="column w-33-percent">
		         <form:label for="customer.address.address1" path="customer.address.address1" cssErrorClass="error">Address</form:label><br/>
		         <form:input path="customer.address.address1" /> <form:errors path="customer.address.address1" />
	 	 </div>
	</div>
        <div class="row">
		  <div class="column w-33-percent">
				<form:label for="customer.customerType" path="customer.customerType" cssErrorClass="error">Type</form:label><br/>
				<form:select path="customer.customerType">
        			<form:option value="" label="All"/>
        			<form:options items="${customerTypeOptions}" itemLabel="displayName"/>
    			</form:select>
	 	 </div>
		  <div class="column w-33-percent">
                 <form:checkbox path="customer.bookmarksAccount.accountHolder" />
				 <form:label id="checkboxLabel" for="customer.bookmarksAccount.accountHolder" path="customer.bookmarksAccount.accountHolder" cssErrorClass="error">Account</form:label>
				 <br/>
                 <form:checkbox path="customer.bookmarksAccount.sponsor" />
				 <form:label for="customer.bookmarksAccount.sponsor" path="customer.bookmarksAccount.sponsor" cssErrorClass="error">Sponsor</form:label>
				<br/>
        <form:checkbox path="customer.bookmarksAccount.paysInMonthly" />
				 <form:label for="customer.bookmarksAccount.paysInMonthly" path="customer.bookmarksAccount.paysInMonthly" cssErrorClass="error">Pays In Monthly</form:label>
		  </div>
			<div class="column w-33-percent">
				 <form:label for="customerId" path="customerId" cssErrorClass="error">ID</form:label><br/>
				 <form:input path="customerId" /> <form:errors path="customerId" />
 		 </div>
		</div><!-- /row -->
		<div class="row">
          <div class="column w-100-percent">
 				<button id="searchCustomerSubmitButton" type="button" class="btn btn-primary" onclick="javascript:submitForm('${pageContext.request.contextPath}/customer/search')">Search</button>
	 				<a href="${pageContext.request.contextPath}/customer/displaySearch">
	 					<button type="button" class="btn btn-primary">Reset</button>
	 				</a>
	 				<button type="button" class="btn btn-primary" onclick="javascript:submitForm('${pageContext.request.contextPath}/customer/addCustomerFromSearch')">Add Customer</button>
	 				<button type="button" class="btn btn-success" onclick="javascript:printLabels( 12 )">Print Labels (12)</button>
					<button type="button" class="btn btn-success" onclick="javascript:printLabels( 16 )">Print Labels (16)</button>
	 	 </div>
	</div>
</div>
</form:form>
<br/>
<c:if test="${customerList != null}">
            <display:table name="customerList"
               requestURI="search"
               decorator="org.bookmarks.ui.SearchCustomersDecorator"
               sort="external"
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
                      <display:column property="id" title="ID" media="excel csv html" />
                      <display:column property="name" sortable="true" sortName="c.lastName"  maxLength="25" title="Name"/>
                      <display:column property="address" sortable="true" sortName="c.address.address1"  maxLength="13"  title="Address"/>
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

						    <li><a href="${pageContext.request.contextPath}/customerOrderLine/search?customerOrderLine.customer.lastName=${searchTable.lastName}&customerOrderLine.customer.firstName=${searchTable.firstName}">View Orders</a></li>

						    <li class="divider"></li>

						    <li><a href="${pageContext.request.contextPath}/invoice/init?customerId=${searchTable.id}" target="_blank">Create Invoice</a></li>

  							<li><a href="${pageContext.request.contextPath}/invoice/search?invoice.customer.lastName=${searchTable.lastName}&invoice.customer.firstName=${searchTable.firstName}">View Invoices</a></li>

  							<li class="divider"></li>

  							<li><a href="${pageContext.request.contextPath}/saleOrReturn/init?id=${searchTable.id}">Create Sale Of Return</a></li>

  							<li class="divider"></li>


						    <li><a href="${pageContext.request.contextPath}/customer/edit?id=${searchTable.id}&flow=search" target="_blank">Edit</a></li>

						    <li><a href="${pageContext.request.contextPath}/customer/editAccount?id=${searchTable.id}&flow=search">Edit Account</a></li>

						    <li><a href="${pageContext.request.contextPath}/customer/editSponsorship?id=${searchTable.id}&flow=search">Edit Sponsorship</a></li>

						    <li class="divider"></li>

								<li><a href="${pageContext.request.contextPath}/customerReport/report?customer.id=${searchTable.id}&customerReportType=INVOICE">View Statement</a></li>

						    <li><a href="${pageContext.request.contextPath}/customer/addCredit?customerId=${searchTable.id}">Add Credit</a></li>

						    <li><a href="displayEditNote?id=${searchTable.id}" target="_blank">Edit Note</a></li>

						    <li class="divider"></li>

						    <li><a href="mergeFromSearchToKeep?id=${searchTable.id}" target="_blank">Merge and Keep</a></li>

						    <li><a href="mergeFromSearchToDiscard?id=${searchTable.id}" target="_blank">Merge and Discard</a></li>

						    <li class="divider"></li>

						    <li><a href='javascript:authoriseUser("delete?id=${searchTable.id}")'>Delete
						    </a></li>

						  </ul>
						</div>
</display:column>



                      </display:table>
 </c:if>
