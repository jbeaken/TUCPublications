<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="authorSearchBean" action="search" method="post" role="form">
	<div class="row">
		<div class="col-sm-3">
			<div class="form-group">	  
				<label>Name</label>
				<form:input path="author.name" id="focus" class="form-control"/>		
			</div>
		</div>
	</div>
			<input type="submit" class="btn btn-danger" id="searchAuthorsButton"/> 
			<a href="/bookmarks/author/displaySearch" class="btn btn-danger">Reset</a>
</form:form>

<br/>
<c:if test="${authorList != null}">
	<display:table name="authorList" 
	   			   requestURI="search" 
		     	   decorator="org.bookmarks.ui.SearchAuthorsDecorator"
				   sort="external" 
				   defaultsort="2" 
				   defaultorder="ascending"
				   class="table table-striped table-bordered table-hover table-condensed"
				   export="true"
				   partialList="true"
				   pagesize="${pageSize}"
				   size="${searchResultCount}"
				   id="searchTable">	
			   <display:setProperty name="export.pdf" value="true" /> 
			   <display:setProperty name="export.xml" value="false" /> 
			   <display:setProperty name="export.pdf.filename" value="authors.pdf"/> 				   
			   <display:setProperty name="export.csv.filename" value="authors.txt"/> 					   				
			  <display:column property="id" sortable="true" sortName="s.id" title="ID"/>	
			  <display:column property="name" sortable="true" sortName="s.name" title="Name"/>
<display:column title="Actions" media="html" style="width:10%">
					  	<div class="btn-group">
						  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
						    Action <span class="caret"></span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
						    <li><a href="${pageContext.request.contextPath}/author/edit?id=${searchTable.id}&flow=search" target="_blank">Edit</a></li>
						    <li><a href="${pageContext.request.contextPath}/author/displayEditNote?id=${searchTable.id}" target="_blank">Edit Note</a></li>
							<li><a href="${pageContext.request.contextPath}/author/moveAndDelete?id=${searchTable.id}" target="_blank">Move and Delete</a></li>
						    <li class="divider"></li>
						    
						    <li><a href="${pageContext.request.contextPath}/author/delete?id=${searchTable.id}">Delete</a></li>
						  </ul>
						</div>
					  </display:column>	  
	</display:table>
</c:if>
