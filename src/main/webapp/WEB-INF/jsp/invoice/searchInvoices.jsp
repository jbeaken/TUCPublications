<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="invoiceSearchBean" action="search" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
				<form:label	for="invoice.customer.firstName" path="invoice.customer.firstName" cssErrorClass="error">First Name</form:label><br/>
				<form:input path="invoice.customer.firstName"/><form:errors path="invoice.customer.firstName" />
	  	 </div>
		  <div class="column w-33-percent">
	          <form:label for="invoice.customer.lastName" path="invoice.customer.lastName" cssErrorClass="error">Last Name</form:label><br/>
	          <form:input path="invoice.customer.lastName"/><form:errors path="invoice.customer.lastName" />
	  	</div>
		<div class="column w-33-percent">
	          <form:label for="invoice.id" path="invoice.id" cssErrorClass="error">Invoice No.</form:label><br/>
	          <form:input path="invoice.id"/><form:errors path="invoice.id" />
	  	</div>
      </div>
        <div class="row">
          <div class="column w-33-percent">
				<form:label	for="stockItem.title" path="stockItem.title" cssErrorClass="error">Stock Title</form:label><br/>
				<form:input path="stockItem.title"/><form:errors path="stockItem.title" />
	  	 </div>
		  <div class="column w-33-percent">
	          <form:label for="stockItem.isbn" path="stockItem.isbn" cssErrorClass="error">ISBN</form:label><br/>
	          <form:input path="stockItem.isbn"/><form:errors path="stockItem.isbn" />
	  	</div>
		  <div class="column w-33-percent">
			<form:checkbox path="invoice.paid" />
					 <form:label for="invoice.paid" path="invoice.paid" cssErrorClass="error">Is Paid</form:label>
					 <br/>
			 <form:checkbox path="invoice.isProforma" />
					 <form:label for="invoice.isProforma" path="invoice.isProforma" cssErrorClass="error">Is Proforma</form:label>
					 <br/>
	  	</div>
	  </div>
        <div class="row">
          <div class="column w-33-percent">
				<label>Start Date</label><br/>
	  	 </div>
		  <div class="column w-33-percent">
	          <label>End Date</label><br/>
	  	</div>
	  </div>
	  <div class="row">
          <div class="column w-100-percent">
				<input type="submit" class="btn btn-success" value="Search Invoices"/>
				<button type="button" onclick="javascript:alert('I don't work yet')" class="btn btn-primary">Reset</button>
	  	 </div>
	  </div>
</div>
</form:form>
<br/>
<c:if test="${invoiceList != null}">
					<display:table
			   name="invoiceList"
			   requestURI="search"
			   decorator="org.bookmarks.ui.SearchInvoiceDecorator"
			   sort="external"
			   export="true"
			   defaultsort="2"
			   defaultorder="ascending"
			   partialList="true"
			   size="${searchResultCount}"
			   pagesize="10"
			   id="searchTable">
	   <display:setProperty name="export.pdf" value="true" />
	   <display:setProperty name="export.xml" value="false" />
	   <display:setProperty name="export.pdf.filename" value="invoices.pdf"/>
	   <display:setProperty name="export.csv.filename" value="invoices.txt"/>
					  <display:column sortable="true" sortName="i.id" maxLength="15" property="id" title="No."/>
					  <display:column sortable="true" sortName="i.creationDate" property="creationDate" title="Date"/>
					  <display:column sortable="true" sortName="i.customer.lastName" maxLength="15" property="customerName" title="Customer"/>
					  <display:column property="telephoneNumber" maxLength="15" title="Telephone"/>
					  <display:column sortable="true" sortName="i.secondHandPrice" property="secondHandPrice" title="2nd Hand"/>
					  <display:column sortable="true" sortName="i.serviceCharge" property="serviceCharge" title="Service Charge"/>
					  <display:column sortable="true" sortName="i.totalPrice" property="totalPrice" title="Total Price"/>
					  <display:column sortable="true" sortName="i.isProforma" property="isProforma" title="Is Proforma?"/>
					  <display:column sortable="true" sortName="i.paid" property="paid" title="Is Paid?"/>

  <display:column title="Actions" media="html" style="width:10%">
  					  	<div class="btn-group">
  						  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
  						    Action <span class="caret"></span>
  						  </button>
  						  <ul class="dropdown-menu" role="menu">
  						    <li><a href="/invoice/edit?id=${searchTable.id}">Edit</a></li>

  						    <li><a href="/invoice/view?id=${searchTable.id}&flow=searchInvoices">View</a></li>

  						    <li class="divider"></li>

  						    <li><a href="/invoice/print?invoiceId=${searchTable.id}" target="_blank">Print</a></li>

    							<li><a href="/invoice/generatePdf?invoiceId=${searchTable.id}">Generate PDF</a></li>

  						    <li class="divider"></li>

  						    <li><a href='javascript:authoriseUser("/invoice/delete?id=${searchTable.id}")'>Delete
  						    </a></li>

  						  </ul>
  						</div>
  </display:column>
  					</display:table>
</c:if>

<script>
$(function() {
    $('#startDate').datePicker();
    $('#endDate').datePicker();
});

</script>
