<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form id="stockItemSearchForm" modelAttribute="stockItemSearchBean" action="/bookmarks/stockTakeLine/search" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
				<form:label	for="stockItem.isbn" path="stockItem.isbn" cssErrorClass="error">ISBN</form:label><br/> 
				<form:input path="stockItem.isbn" id="focus" /> <form:errors path="stockItem.isbn" />			
	  	 </div>
          <div class="column w-33-percent">
						<form:label for="stockItem.title" path="stockItem.title" cssErrorClass="error">Title</form:label><br/>
						<form:input path="stockItem.title" /> <form:errors path="stockItem.title" />
	  	 </div>	
	  	</div>
</div>
<div id="rows">
	<div class="row">
          <div class="column w-100-percent"> 
		  <button type="button" class="btn btn-primary" id="searchStockSubmitButton" onclick="javascript:submitForm('/bookmarks/stockTakeLine/search')">Search</button>
          <button type="button" class="btn btn-primary" id="resetButton" accesskey="R">Reset</button>&nbsp;
	 	 </div>
	</div>	
</div>
</form:form>
<c:if test="${stockTakeLineList == null}">
<br/>
<br/>
Nothing Found
</c:if>
<c:if test="${stockTakeLineList != null}">
<display:table name="stockTakeLineList" 
			   requestURI="search" 
			   decorator="org.bookmarks.ui.SearchStockItemsDecorator"
			   sort="external" 
			   defaultsort="1" 
			   defaultorder="ascending"
			   pagesize="${pageSize}"
			   export="true"
			   partialList="true"
			   size="${searchResultCount}"			   
			   id="searchTable"
			   class="smallTextTable">
	   <display:setProperty name="export.pdf" value="true" /> 
	   <display:setProperty name="export.xml" value="false" /> 
	   <display:setProperty name="export.pdf.filename" value="stockTake.pdf"/> 				   
	   <display:setProperty name="export.csv.filename" value="stockTake.txt"/> 				   
  <display:column property="stockItem.isbn" sortable="true" sortName="s.isbn" title="ISBN"/>
  <display:column property="stockItem.title" sortable="true" sortName="s.title" maxLength="100" title="Title" style="width:70%"/>
  <display:column property="quantity" sortable="true" maxLength="10" class="category" sortName="s.category.name" title="Quantity"/>
  <display:column title="Actions" media="html" style="width:15%">
  <a href="/bookmarks/stockTakeLine/edit?id=${searchTable.id}"><img src="../resources/images/write_medium.png" title="Edit" /></a>
  <img src="../resources/images/delete_medium.png" onclick="javascript:authoriseUser('/bookmarks/stockTakeLine/delete?id=${searchTable.id}')" title="Delete" />
  </display:column>
</display:table>
</c:if>
