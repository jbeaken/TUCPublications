<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
		<form:form modelAttribute="eventSearchBean" action="search" method="post">
<div class="rows">
        <div class="row">
          <div class="col-sm-4">
				<form:label for="event.name" path="event.name" cssErrorClass="error">Event Name</form:label><br/>
				<form:input path="event.name" id="focus"/> <form:errors path="event.name" />					
	  </div>
	  <div class="col-sm-4">
				 <form:label for="event.onWebsite" path="event.onWebsite" cssErrorClass="error">On Website</form:label><br/>
                 <form:checkbox path="event.onWebsite" />                    
		  </div>
          <div class="col-sm-4">
			<form:label for="event.type" path="event.type" cssErrorClass="error">Event Type</form:label><br/>
          		<form:select path="event.type">
						<form:option value="" label="-select-" />
            			<form:options items="${typeList}" itemLabel="displayName"/>
        		</form:select>		
	  	 </div>		  
	</div>
      <div class="row">
          <div class="col-sm-12">
 				<input type="submit" class="btn btn-primary" id="searchEventsButton" value="Search Events"/> 
 				<button type="button" class="btn btn-primary" onclick="javascript:submitForm('reset')">Reset</button>
	 	 </div>
		 
	</div>		
</div>		
</form:form>
		<br/>
			<c:if test="${requestScope.eventList != null}">
		<display:table 
				name="eventList" 
			   requestURI="search" 
        	   decorator="org.bookmarks.ui.SearchEventDecorator"
			   sort="external" 
			   defaultsort="2" 
			   class="table table-striped table-bordered table-hover table-condensed"
			   defaultorder="ascending"
			   export="true"
			   partialList="true"
			   size="${searchResultCount}"
			   pagesize="10"
			   id="searchTable">	
	   <display:setProperty name="export.pdf" value="true" /> 
	   <display:setProperty name="export.xml" value="false" /> 
	   <display:setProperty name="export.pdf.filename" value="events.pdf"/> 	
					 <display:column property="id" sortable="true" sortName="e.id" title="ID"/>	
					  <display:column property="name" sortable="true" sortName="e.name" title="Name"/>
					  <display:column property="type.displayName" sortable="true" sortName="e.type" title="Type"/>
					  <display:column property="totalSellPrice" sortable="true" sortName="sum(s.sellPrice * s.quantity * (100-s.discount) / 100)" title="Sales"/>
					  <display:column property="startDate" sortable="true" sortName="e.startDate" title="Start Date"/>
					  <display:column property="endDate" sortable="true" sortName="e.endDate" title="End Date"/>
					  <display:column property="link" title="Actions" media="html" />
  					</display:table>
			</c:if>
