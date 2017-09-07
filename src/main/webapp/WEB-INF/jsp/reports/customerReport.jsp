<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
	<script>

	function filterInvoiceReportLineList() {
		var filter = $('select#transactionTypeSelect').val()
		$('input#filter').val(filter)
		
		document.forms[0].submit()
	}
	$(function() {
		$( "#startDate" ).datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: 'dd-MM-yy'
		});
		$( "#endDate" ).datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: 'dd-MM-yy'
		});
		$('form input:checkbox').click(function() {
			$('#saleReportSubmitButton').click();
		});
	});
	
	$(function() {
		$("#customerAutoComplete").autocomplete( {
			source: "/bookmarks/customer/autoCompleteSurname",
			minLength: 3,
			select: function( event, ui ) {
				$("#customerAutoComplete").val(ui.item.label);
				$("#customerId").val(ui.item.value);
				return false;
			},
			focus: function( event, ui ) {
				$("#customerAutoComplete").val(ui.item.label);
				return false;
			}
		});
		//$('#customerId').val(''); //clear customer id
		$('#customerAutoComplete').focus();
	});	
	</script>
	<script>
	$(function(){
		 $("#publisherSelect").chosen();
	});
</script>
<form:form modelAttribute="customerReportBean" action="/bookmarks/customerReport/report" method="post">
<form:hidden path="customer.id" id="customerId"/>
<form:hidden path="filter" />
<form:errors></form:errors>
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
			<div class="row">
	          <div class="column w-33-percent">
				<form:label for="customerReportType" path="customerReportType" cssErrorClass="error">Type</form:label><br/>        
	               <form:select path="customerReportType">
		  				<form:options items="${customerReportTypeList}" itemLabel="displayName"/>
					</form:select> 
		  	 </div>	
			  <div class="column w-33-percent">

			 </div>	
          <div class="column w-33-percent">
	 	 </div>	  			 
		</div>
        <div class="row">
          <div class="column w-20-percent">
				<form:label	for="startDate" path="startDate" cssErrorClass="error">Start Date</form:label><br/>
				<form:input path="startDate" /> <form:errors path="startDate" />			
	  	 </div>
          <div class="column w-20-percent">
				<form:label	for="startHour" path="startHour" cssErrorClass="error">Start Time</form:label><br/>
				<form:input path="startHour" size="2"/> : <form:input path="startMinute" size="2"/><form:errors path="startHour" />			
	  	 </div>
          <div class="column w-20-percent">
				<form:label	for="endDate" path="endDate" cssErrorClass="error">End Date</form:label><br/>
				<form:input path="endDate" /> <form:errors path="endDate" />			
	  	 </div>
          <div class="column w-20-percent">
				<form:label	for="endHour" path="endHour" cssErrorClass="error">End Time</form:label><br/>
				<form:input path="endHour" size="2"/> : <form:input path="endMinute" size="2"/><form:errors path="endHour" />			
	  	 </div>
          <div class="column w-20-percent">
				<form:checkbox path="isDateAgnostic" /> <form:errors path="isDateAgnostic" />	
				<form:label	for="isDateAgnostic" path="isDateAgnostic" cssErrorClass="error">Date Agnostic</form:label>
				<br/>		
	  	 </div>      
	  </div>
      <div class="row">
		          <div class="column w-33-percent">
			  	 </div>
          <div class="column w-100-percent">
				<button type="button" class="btn btn-danger" id="saleReportSubmitButton" onclick="javascript:submitForm('/bookmarks/customerReport/report')" id="focus">Show Report</button>
 				<button type="button" class="btn btn-primary" onclick="javascript:submitForm('reset')">Reset</button>
	 	 </div>
	</div>		
</div>		
</form:form>

		<br/>

Opening Balance : ${customer.bookmarksAccount.openingBalance}		
<br/>
Current Balance : ${customer.bookmarksAccount.currentBalance}		


<c:if test="${invoiceReportLineList != null}">

<select id="transactionTypeSelect" onchange="javascript:filterInvoiceReportLineList()">
	<option value="">All</option>
	<option value="DEBITS">Debits</option>
	<option value="CREDITS">Credits</option>
</select>

<display:table name="invoiceReportLineList" 
			   requestURI="/bookmarks/saleReport/report" 
        	   decorator="org.bookmarks.ui.InvoiceReportDecorator"
			   export="true"
			   id="searchTable"
			   class="smallTextTable">	
	   <display:setProperty name="export.pdf" value="true" /> 
	   <display:setProperty name="export.xml" value="false" /> 
	   <display:setProperty name="export.pdf.filename" value="sale.pdf"/> 				   
	   <display:setProperty name="export.csv.filename" value="sale.txt"/> 			   				
					  <display:column property="date" sortable="true" sortName="s.creationDate" title="Sale Date"/>
					  <display:column property="isbn" title="ISBN"/>
					  <display:column property="ref" title="Ref"/>
					  <display:column property="sale.quantity" title="Quantity"/>
					  <display:column property="discount" sortable="true" sortName="s.sellPrice" title="Discount"/>
					  <display:column property="deliveryType" title="Delivery"/>
					  <display:column property="totalPrice" title="Total Price"/>
					  <display:column property="currentBalance" title="Balance"/>
					  <display:column property="credit" title="Credit"/>
  					</display:table>
 </c:if>
