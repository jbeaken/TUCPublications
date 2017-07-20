<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
	<script>
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
	});
	</script>
<form:form modelAttribute="saleReportBean" action="getReorderReview" method="post">
<div class="rows">
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
	  </div>
      <div class="row">
          <div class="column w-70-percent">
 				<input type="submit" class="btn btn-primary"/> 
 				<button type="button" class="btn btn-primary" onclick="javascript:submitForm('reset')">Reset</button>
	 	 </div>
	</div>		
</div>		
</form:form>
<form:form modelAttribute="supplierOrderLineList" action="processReorderReview" method="post">
		<br/>
		<c:if test="${supplierOrderLineList != null}">
<display:table name="supplierOrderLineList" 
			   requestURI="search" 
        	   decorator="org.bookmarks.ui.ReorderReviewDecorator"
			   sort="external" 
			   defaultsort="2" 
			   defaultorder="ascending"
			   export="true"
			   id="searchTable">	
	   <display:setProperty name="export.pdf" value="true" /> 
	   <display:setProperty name="export.xml" value="false" /> 
	   <display:setProperty name="export.pdf.filename" value="sale.pdf"/> 				   
	   <display:setProperty name="export.csv.filename" value="sale.txt"/> 			   				
					  <display:column property="title" title="Title"/>
					  <display:column property="stockItem.category.name" sortable="true" maxLength="10" sortName="s.category.name" title="Category"/>
					  <display:column property="stockItem.quantityInStock" title="In Stock"/>
					  <display:column title="Amount">
					  	<input type="text" name="${searchTable.stockItem.id}" id="${searchTable.stockItem.id}" value="${searchTable.amount}" />
					  </display:column>
					  <display:column title="Supplier">
					  	<select name="s${searchTable.stockItem.id}" id="s${searchTable.stockItem.id}">
					  		<c:forEach items="${supplierList}" var="supplier">
					  			<option value="${supplier.id}" 
					  			<c:if test="${supplier.id == searchTable.stockItem.preferredSupplier.id}">selected</c:if>>
					  			${supplier.name}
					  			</option>
					  		</c:forEach>
					  	</select>
					  </display:column>
</display:table>
<input type="submit" class="btn btn-primary"/> 
</c:if>
</form:form>