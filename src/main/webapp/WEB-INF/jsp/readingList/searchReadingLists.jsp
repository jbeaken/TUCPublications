<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
		<form:form modelAttribute="readingListSearchBean" action="search" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
				<form:label	for="readingList.name" path="readingList.name" cssErrorClass="error">Reading List Name</form:label><br/>
				<form:input path="readingList.name" autofocus="autofocus" />			
	  	 </div>
	  	</div>
      <div class="row">
          <div class="column w-70-percent">
 				<input type="submit" class="btn btn-primary"/> 
	 			<a href="/readingList/add" class="btn btn-danger">Add New Reading List</a>
	 	 </div>
	</div>		
</div>		
</form:form>
		<br/>
			<c:if test="${requestScope.readingListList != null}">
					<display:table name="readingListList" 
			   requestURI="search" 
			   sort="external" 
			   defaultsort="2" 
			   defaultorder="ascending"
			   export="true"
			   partialList="true"
			   size="${searchResultCount}"
			   pagesize="10"
			   id="searchTable">					
					  <display:column property="id" sortable="true" sortName="rl.id" title="ID"/>
					  <display:column property="name" sortable="true" sortName="rl.name" title="Name"/>
					  <display:column property="isOnWebsite" sortable="true" sortName="rl.isOnWebsite" title="Is On Website"/>
					  <display:column property="isOnSidebar" sortable="true" sortName="rl.isOnSidebar" title="Is On Sidebar"/>
 						<display:column title="Actions" media="html" style="width:20%">
						  	<div class="btn-group">
							  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
							    Action <span class="caret"></span>
							  </button>
							  <ul class="dropdown-menu" role="menu">					  						  	
							    <li><a href="/readingList/edit?id=${searchTable.id}">Edit</a></li>
							    <li><a href="/readingList/manage?id=${searchTable.id}">Manage Stock</a></li>
							    <li class="divider"></li>	
							    <li><a onclick='javascript:authoriseUser("/readingList/delete?id=${searchTable.id}")'>Delete</a></li>
							  </ul>
							</div>
						</display:column>
  					</display:table>
			</c:if>
