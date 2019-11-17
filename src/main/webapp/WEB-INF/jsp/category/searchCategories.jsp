<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
		<form:form modelAttribute="categorySearchBean" action="search" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
				<form:label	for="category.name" path="category.name">Category Name</form:label><br/>
				<form:input path="category.name" id="focus"/> <form:errors path="category.name" />			
	  	 </div>
          <div class="column w-33-percent">
				<form:label	for="category.isOnWebsite" path="category.isOnWebsite">Is On Website?</form:label><br/>
				<form:checkbox path="category.isOnWebsite" />			
	  	 </div>	  	
          <div class="column w-33-percent">
				<form:label	for="category.isInSidebar" path="category.isInSidebar">Is In Sidebar?</form:label><br/>
				<form:checkbox path="category.isInSidebar" />			
	  	 </div>	 	  	  
	  	</div>
      <div class="row">
          <div class="column w-70-percent">
 				<input type="submit" class="btn btn-primary"/> 
	 				<a href="/category/displaySearch">
	 					<button type="button" class="btn btn-primary">Reset</button>
	 				</a>
	 	 </div>
	</div>		
</div>		
</form:form>
		<br/>
			<c:if test="${requestScope.categoriesList != null}">
					<display:table name="categoriesList" 
			   requestURI="search" 
        	   decorator="org.bookmarks.ui.SearchCategoriesDecorator"
			   sort="external" 
			   defaultsort="2" 
			   defaultorder="ascending"
			   export="true"
			   partialList="true"
			   size="${searchResultCount}"
			   pagesize="10"
			   id="searchTable">					
					  <display:column property="id" sortable="true" sortName="c.id" title="ID"/>
					  <display:column property="name" sortable="true" sortName="c.name" title="Name"/>
					  <display:column property="parent.name" sortable="true" sortName="p.name" title="Parent Category"/>
 						<display:column title="Actions" media="html" style="width:20%">
						  	<div class="btn-group">
							  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
							    Action <span class="caret"></span>
							  </button>
							  <ul class="dropdown-menu" role="menu">					  						  	
							    <li><a href="/category/edit?id=${searchTable.id}">Edit</a></li>
							    <li><a href="/category/manageStickies?id=${searchTable.id}">Manage Stickies</a></li>
							    <li><a href="displayEditNote?id=${searchTable.id}" target="_blank">Add Note</a></li>
							    <li class="divider"></li>	
							    <li><a onclick='javascript:authoriseUser("delete?id=${searchTable.id}")'>Delete</a></li>
							  </ul>
							</div>
						</display:column>					  					  
  					</display:table>
			</c:if>
