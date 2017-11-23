<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
		<form:form modelAttribute="eventSearchBean" action="search" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
				<form:label for="event.name" path="event.name" cssErrorClass="error">Event Name</form:label><br/>
				<form:input path="event.name" id="focus"/> <form:errors path="event.name" />
	  </div>
	  <div class="column w-33-percent">
				 <form:label for="event.onWebsite" path="event.onWebsite" cssErrorClass="error">Is On Website?</form:label><br/>
                 <form:checkbox path="event.onWebsite" />
		  </div>
          <div class="column w-33-percent">
			<form:label for="event.type" path="event.type" cssErrorClass="error">Event Type</form:label><br/>
          		<form:select path="event.type">
						<form:option value="" label="-select-" />
            			<form:options items="${typeList}" itemLabel="displayName"/>
        		</form:select>
	  	 </div>
	</div>
      <div class="row">
          <div class="column w-70-percent">
 				<input type="submit" class="btn btn-primary" id="searchEventsButton" value="Search Events"/>
 				<button type="button" class="btn btn-primary" onclick="javascript:submitForm('reset')">Reset</button>
	 	 </div>

	</div>
</div>
</form:form>
		<br/>
			<c:if test="${requestScope.eventList != null}">
					<display:table name="eventList"
			   requestURI="search"
        	   decorator="org.bookmarks.ui.SearchEventDecorator"
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
	   <display:setProperty name="export.pdf.filename" value="events.pdf"/>
       <display:setProperty name="export.csv.filename" value="events.csv"/>

					 <display:column property="id" sortable="true" sortName="e.id" title="ID"/>
					  <display:column property="name" sortable="true" sortName="e.name" title="Name"/>
					  <display:column property="type.displayName" sortable="true" sortName="e.type" title="Type"/>
					  <display:column property="totalSellPrice" sortable="true" sortName="sum(s.sellPrice * s.quantity * (100-s.discount) / 100)" title="Sales"/>
					  <display:column property="startDate" sortable="true" sortName="e.startDate" title="Start Date"/>
					  <display:column property="endDate" sortable="true" sortName="e.endDate" title="End Date"/>
						<display:column title="Actions" media="html" style="width:10%">
											  	<div class="btn-group">

												  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
												    Action <span class="caret"></span>
												  </button>

												  <ul class="dropdown-menu" role="menu">
												    <li><a href="${pageContext.request.contextPath}/events/view/${searchTable.id}">View</a></li>

												    <li><a href="${pageContext.request.contextPath}/events/edit/${searchTable.id}">Edit</a></li>

														<li><a href="${pageContext.request.contextPath}/events/displayEditNote?id=${searchTable.id}" target="_blank">Edit Note</a></li>

												    <li class="divider"></li>

												    <li><a href="${pageContext.request.contextPath}/events/startSelling/${searchTable.id}">Start Selling</a></li>

														<li><a href="${pageContext.request.contextPath}/events/showSales/${searchTable.id}">Show Sales</a></li>

														<li class="divider"></li>

														<li><a href="${pageContext.request.contextPath}/events/delete/${searchTable.id}">Delete</a></li>

														<li><a href='javascript:authoriseUser("uploadSales/${searchTable.id}")'>Upload CSV</a></li>

												  </ul>
												</div>
						</display:column>
  					</display:table>
			</c:if>
