<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<h1>Add stock for type : ${sessionScope.typeToManage.displayName}</h1>
<form:form modelAttribute="stockItem" action="${pageContext.request.contextPath}/bouncies/addSticky" method="post">
	<div class="rows">
	        <div class="row">
	          <div class="column w-33-percent">
					<form:label	for="isbn" path="isbn">ISBN</form:label><br/>
					<form:input path="isbn" required="required" autofocus="autofocus"/>			
					
		  	 </div>
		  	</div>
	      <div class="row">
	          <div class="column w-33-percent">
	 				<input type="submit" class="btn btn-primary" value="Add ISBN to Bouncies"/> 
	 				<a class="btn btn-danger" href="${pageContext.request.contextPath}/bouncies/save">Save Bouncies</a> 
		 	 </div>		 	 
		</div>		
	</div>		
</form:form>
		<br/><br/>
		<display:table name="sessionScope.stickies" 
			   requestURI="search" 
			   sort="external" 
			   export="true"
			   id="searchTable">		
					  <display:column title="Image">
					    <c:if test="${searchTable.imageURL != null}">
					      <img src="${searchTable.imageURL}" style="max-width : 80%"/>
					    </c:if>
					    <c:if test="${searchTable.imageURL == null}">
					      No Image
					    </c:if>    
					</display:column>		
					  <display:column property="id" title="ID"/>
					  <display:column property="isbn" title="ISBN"/>
					  <display:column property="title" title="Title"/>
					  <display:column title="Actions" media="html">
					  	<div class="btn-group">
						  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
						    Action <span class="caret"></span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
						    <li><a href="${pageContext.request.contextPath}/bouncies/moveUp?index=${searchTable_rowNum - 1}">Move Up</a></li>
						    <li><a href="${pageContext.request.contextPath}/bouncies/moveDown?index=${searchTable_rowNum - 1}">Move Down</a></li>
						    <li><a href="${pageContext.request.contextPath}/bouncies/remove?index=${searchTable_rowNum - 1}">Remove</a></li>
						    <li class="divider"></li>						   
						    <li><a href="${pageContext.request.contextPath}/stock/edit?id=${searchTable.id}&flow=stickyStockItemType"  target="_blank"">Edit Stock</a></li>
						  </ul>
						</div>
					  </display:column>								  
  		</display:table>

