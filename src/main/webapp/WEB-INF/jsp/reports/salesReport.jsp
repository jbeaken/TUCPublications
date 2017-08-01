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
		$('form input:checkbox').click(function() {
			$('#saleReportSubmitButton').click();
		});
	});
	</script>
	<script>
	$(function(){
		 $("#publisherSelect").chosen();
	});
</script>
<form:form modelAttribute="saleReportBean" action="${pageContext.request.contextPath}/saleReport/report" method="post">
	<form:hidden path="sale.event.id"/>
	<form:hidden path="sale.event.name"/>
<div class="rows">
			<div class="row">
	          <div class="column w-33-percent">
				<form:label for="salesReportType" path="salesReportType" cssErrorClass="error">Type</form:label><br/>
	               <form:select path="salesReportType">
		  				<form:options items="${salesReportTypeList}" itemLabel="displayName"/>
					</form:select>
		  	 </div>
			  <div class="column w-33-percent">
				<form:label for="category.id" path="category.id" cssErrorClass="error">Category</form:label><br/>
				<form:select path="category.id">
					<form:option value="" label="All"/>
					<form:options items="${categoryList}" itemValue="id" itemLabel="name"/>
				</form:select>
			 </div>
          <div class="column w-33-percent">
				<form:label	for="sale.stockItem.isbn" path="sale.stockItem.isbn" cssErrorClass="error">ISBN</form:label><br/>
				<form:input path="sale.stockItem.isbn" /> <form:errors path="sale.stockItem.isbn" />
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
				<form:checkbox path="groupBy" /> <form:errors path="groupBy" />
				<form:label	for="groupBy" path="groupBy" cssErrorClass="error">Group Sales</form:label>
				<br/>
				<form:checkbox path="isDateAgnostic" /> <form:errors path="isDateAgnostic" />
				<form:label	for="isDateAgnostic" path="isDateAgnostic" cssErrorClass="error">Date Agnostic</form:label>
				<br/>
	  	 </div>
	  </div>
      <div class="row">
				  <div class="column w-33-percent">
						<label>Event</label><br/>
						${saleReportBean.sale.event.name}
				 </div>
				  <div class="column w-33-percent">
							<form:label for="sale.stockItem.type" path="sale.stockItem.type" cssErrorClass="error">Type</form:label><br/>
								<form:select id="stockItemTypeSelect" path="sale.stockItem.type" onkeypress="javascript:submitForm('${pageContext.request.contextPath}/saleReport/report')">
			            			<form:option value="" label="All"/>
		            			<form:options items="${stockItemTypeList}" itemLabel="displayName"/></form:select>
				  </div>
					<div class="column w-33-percent">
							<form:label for="status" path="status" cssErrorClass="error">Books/Non-Books</form:label><br/>
								<form:select id="statusSelect" path="status" onkeypress="javascript:submitForm('${pageContext.request.contextPath}/saleReport/report')">
			            			<form:option value="" label="All"/>
												<form:option value="1" label="Books"/>
												<form:option value="2" label="Non-Books"/>
		            </form:select>
				  </div>
	  </div>
      <div class="row">
          <div class="column w-100-percent">
				<label>Publisher</label><br/>
						<form:select path="sale.stockItem.publisher.id" id="publisherSelect">
	            			<form:option value="" label="All"/>
            				<form:options items="${publisherList}" itemValue="id" itemLabel="name"/>
            			</form:select>
            			<br/>
		            	<br/>
	  	 </div>
 	   </div>
      <div class="row">
          <div class="column w-100-percent">
				<button type="button" class="btn btn-danger" id="saleReportSubmitButton" onclick="javascript:submitForm('${pageContext.request.contextPath}/saleReport/report')">Show Report</button>
 				<button type="button" class="btn btn-primary" onclick="javascript:submitForm('reset')">Reset</button>
	 	 </div>
	</div>
</div>
</form:form>
		<br/>
<c:if test="${saleList != null}">
<label>Total Price :</label><fmt:formatNumber type="currency" value="${saleTotalBean.totalPrice}" /><br/>
<label>Total Quantity :</label>${saleTotalBean.totalQuantity}
<br/>
			<c:if test="${saleReportBean.groupBy == true }">
<display:table name="saleList"
			   requestURI="${pageContext.request.contextPath}/saleReport/report"
        	   decorator="org.bookmarks.ui.SaleReportDecorator"
			   sort="external"
			   defaultsort="3"
			   defaultorder="descending"
			   export="true"
			   partialList="true"
			   size="${searchResultCount}"
			   pagesize="${pageSize}"
			   id="searchTable"
			   class="smallTextTable">
	   <display:setProperty name="export.pdf" value="true" />
	   <display:setProperty name="export.xml" value="false" />
	   <display:setProperty name="export.pdf.filename" value="sale.pdf"/>
	   <display:setProperty name="export.csv.filename" value="sale.csv"/>
					  <display:column property="stockItem.isbn" sortable="true" sortName="si.isbn" title="ISBN"/>
					  <display:column property="stockItem.title" sortable="true" sortName="si.title" title="Title"/>
					  <display:column property="stockItem.publisher.name" sortable="true" sortName="p.name" title="Publisher"/>
					  <display:column property="quantity" sortable="true" sortName="sum(s.quantity)" title="Quantity"/>
					  <display:column property="sellPrice" sortable="true" sortName="sum(s.sellPrice * s.quantity * (100-s.discount) / 100)" title="Total Sell Price"/>

  					</display:table>
			</c:if>
			<c:if test="${saleReportBean.groupBy == false}">
					<display:table name="saleList"
			   requestURI="${pageContext.request.contextPath}/saleReport/report"
        	   decorator="org.bookmarks.ui.SaleReportDecorator"
			   sort="external"
			   defaultsort="3"
			   defaultorder="descending"
			   export="true"
			   partialList="true"
			   size="${searchResultCount}"
			   pagesize="${pageSize}"
			   id="searchTable"
			   class="smallTextTable">
	   <display:setProperty name="export.pdf" value="true" />
	   <display:setProperty name="export.xml" value="false" />
	   <display:setProperty name="export.pdf.filename" value="sale.pdf"/>
	   <display:setProperty name="export.csv.filename" value="sale.csv"/>
					  <display:column property="stockItem.isbn" sortable="true" sortName="si.isbn" title="ISBN"/>
					  <display:column property="stockItem.title" sortable="true" sortName="si.title" title="Title"/>
					  <display:column property="stockItem.publisher.name" sortable="true" sortName="p.name" title="Publisher"/>
					  <display:column property="quantity" sortable="true" sortName="s.quantity" title="Quantity"/>
					  <display:column property="event.name" sortable="true" sortName="e.name" title="Event"/>
					  <display:column property="sellPrice" sortable="true" sortName="s.sellPrice" title="Sell Price"/>
					  <display:column property="totalPrice" title="Total Sell Price"/>
					  <display:column property="creationDate" sortable="true" sortName="s.creationDate" title="Sale Date"/>
					  <display:column property="link" title="Actions" media="html"/>
  					</display:table>
			</c:if>
</c:if>

<c:if test="${vatList != null}">
	<br/>
	<br/>
	<display:table name="vatList"
				   requestURI="${pageContext.request.contextPath}/saleReport/report"
				   export="true"
				   size="${vatList.size()}"
				   id="searchTable"
				   class="smallTextTable">
		   <display:setProperty name="export.pdf" value="true" />
		   <display:setProperty name="export.xml" value="false" />
		   <display:setProperty name="export.pdf.filename" value="invoices.pdf"/>
		   <display:setProperty name="export.csv.filename" value="invoices.csv"/>
			 <display:column property="customer.id" title="Customer ID"/>
			 <display:column property="customer.fullName" title="Customer"/>
			 <display:column property="formattedTotal" title="Total"/>
			 <display:column property="formattedVat" title="VAT"/>
	</display:table>
</c:if>

<c:if test="${invoiceReportBeanList != null}">
	<br/>
	<br/>
	<display:table name="invoiceReportBeanList"
				   requestURI="${pageContext.request.contextPath}/saleReport/report"
				   export="true"
				   size="${invoiceReportBeanList.size()}"
				   id="searchTable"
				   class="smallTextTable">
		   <display:setProperty name="export.pdf" value="true" />
		   <display:setProperty name="export.xml" value="false" />
		   <display:setProperty name="export.pdf.filename" value="invoices.pdf"/>
		   <display:setProperty name="export.csv.filename" value="invoices.csv"/>
			 <display:column property="customer.id" title="Customer ID"/>
			 <display:column property="customer.fullName" title="Customer"/>
			 <display:column property="formattedTotal" title="Total"/>
			 <display:column property="formattedVat" title="VAT"/>
	</display:table>
</c:if>

<c:if test="${showCategoryReport != null}">
      <div class="row">
          <div class="column w-100-percent">
				<img src="categoryReport.png"/>
	 	 </div>
	 </div>
</c:if>

<c:if test="${showTimeOfDayReport != null}">
      <div class="row">
          <div class="column w-100-percent">
				<img src="showTimeOfDayReport.png"/>
	 	 </div>
	 </div>
</c:if>

<c:if test="${stockItemsList != null}">
<display:table name="stockItemsList"
			   requestURI="report"
			   export="true"
			   id="searchTable">
	   <display:setProperty name="export.pdf" value="true" />
	   <display:setProperty name="export.xml" value="false" />
	   <display:setProperty name="export.pdf.filename" value="categoryStockTake.pdf"/>
	   <display:setProperty name="export.csv.filename" value="categoryStockTake.csv"/>
			  <display:column property="category.name" sortName="c.name" title="Category"/>
			  <display:column property="quantityInStock" sortName="si.quantityInStock" title="Quantity In Stock"/>
			  <display:column property="isbn" sortName="totalPublisherPrice" title="ISBN"/>
			  <display:column property="title" sortName="totalSellPrice" title="Title"/>
</display:table>
</c:if>

<c:if test="${categoryStockTakeBeanList != null}">
<display:table name="categoryStockTakeBeanList"
			   decorator="org.bookmarks.ui.ReportCategoryStockTakeDecorator"
			   requestURI="report"
			   export="true"
			   id="searchTable">
	   <display:setProperty name="export.pdf" value="true" />
	   <display:setProperty name="export.xml" value="false" />
	   <display:setProperty name="export.pdf.filename" value="categoryStockTake.pdf"/>
	   <display:setProperty name="export.csv.filename" value="categoryStockTake.csv"/>
					  <display:column property="category.name" sortName="c.name" title="Category"/>
					  <display:column property="quantityInStock" sortName="si.quantityInStock" title="Quantity In Stock"/>
					  <display:column property="totalPublisherPrice" sortName="totalPublisherPrice" title="Total Publisher Price"/>
					  <display:column property="totalSellPrice" sortName="totalSellPrice" title="Total Sell Price"/>
  					</display:table>
</c:if>

<c:if test="${publisherStockTakeBeanList != null}">
<display:table name="publisherStockTakeBeanList"
			   decorator="org.bookmarks.ui.ReportPublisherStockTakeDecorator"
			   requestURI="report"
			   export="true"
			   id="searchTable">
	   <display:setProperty name="export.pdf" value="true" />
	   <display:setProperty name="export.xml" value="false" />
	   <display:setProperty name="export.pdf.filename" value="publisherStockTake.pdf"/>
	   <display:setProperty name="export.csv.filename" value="publisherStockTake.csv"/>
					  <display:column property="publisher.name" sortName="c.name" title="Publisher"/>
					  <display:column property="quantityInStock" sortName="si.quantityInStock" title="Quantity In Stock"/>
					  <display:column property="totalPublisherPrice" sortName="totalPublisherPrice" title="Total Publisher Price"/>
					  <display:column property="totalSellPrice" sortName="totalSellPrice" title="Total Sell Price"/>
  					</display:table>
</c:if>
