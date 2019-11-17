<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

		<form:form modelAttribute="staffSearchBean" action="search" method="post">
<div class="rows">
        <div class="row">
				<form:label for="staff.name" path="staff.name" cssErrorClass="error">Staff Name</form:label><br/>
				<form:input path="staff.name" id="focus"/> <form:errors path="staff.name" />

      <div class="row" style="padding : 10px;">
 				<input type="submit" class="btn btn-primary" id="searchStaffsButton" value="Search Staff"/>
 				<button type="button" class="btn btn-primary" onclick="javascript:submitForm('reset')">Reset</button>

				<a class="btn btn-default pull-right" href="add">Add Staff</a>
	 	 </div>

</div>
</form:form>
		<br/>
					<display:table name="staffList"
			   requestURI="/staff/search"
				 class="table table-striped table-bordered"
        decorator="org.bookmarks.ui.SearchStaffDecorator"
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
	   <display:setProperty name="export.pdf.filename" value="staff.pdf"/>
       <display:setProperty name="export.csv.filename" value="staff.csv"/>

					 <display:column property="id" sortable="true" sortName="e.id" title="ID"/>
					  <display:column property="name" sortable="true" sortName="e.name" title="Name"/>
					  <display:column property="email" />
					  <display:column property="telephone" />
						<display:column title="Actions" media="html" style="width:10%">
											  	<div class="btn-group">

												  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
												    Action <span class="caret"></span>
												  </button>

												  <ul class="dropdown-menu" role="menu">
												    <li><a href="/staff/view/${searchTable.id}">View</a></li>

												    <li><a href="/staff/edit/${searchTable.id}">Edit</a></li>

												    <li class="divider"></li>

														<li><a onclick="javascript:authoriseSuperUser( '/staff/delete/${searchTable.id}' )">Delete</a></li>
												  </ul>
												</div>
						</display:column>
  					</display:table>
