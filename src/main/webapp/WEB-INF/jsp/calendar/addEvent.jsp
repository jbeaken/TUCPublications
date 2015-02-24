<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	<script>
	$(function() {
		$( "#startDate" ).datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: 'dd/mm/yy'
		});
		$("#endDate" ).datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: 'dd/mm/yy'
		});

	});
	</script>
<spring:hasBindErrors name="event">
    <h2>Errors</h2>
    <div class="formerror">
        <ul>
            <c:forEach var="error" items="${errors.allErrors}" varStatus="index">
            	<li>${error.defaultMessage}</li>
			</c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>		
<form:form modelAttribute="event" action="add" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-50-percent">
			<form:label for="name" path="name" cssErrorClass="error">Event Name</form:label><br/>
			<form:input path="name" autofocus="autofocus" required="required" /> <form:errors path="name" />			
				
	  	 </div>
          <div class="column w-50-percent">
			<form:label for="name" path="name" cssErrorClass="error">Event Type</form:label><br/>
          		<form:select path="type" required="required">
						<form:option value="" label="-select-" />
            			<form:options items="${typeList}" itemLabel="displayName"/>
        		</form:select>		
	  	 </div>		 
      </div>
      <div class="row">
          <div class="column w-50-percent">
                                        <form:label for="startDate" path="startDate" cssErrorClass="error">Start Date</form:label><br/>
                                        <form:input path="startDate" required="required" /> <form:errors path="startDate" />                  
	 	 </div>
		  <div class="column w-50-percent">
                                        <form:label for="endDate" path="endDate" cssErrorClass="error">End Date</form:label><br/>
                                        <form:input path="endDate" required="required"/> <form:errors path="endDate" />                  
		  </div>
	</div>			
      <div class="row">
          <div class="column w-50-percent">
                                        <form:label for="stockItem.isbn" path="stockItem.isbn" cssErrorClass="error">ISBN</form:label><br/>
                                        <form:input path="stockItem.isbn" /> <form:errors path="stockItem.isbn" />                  
	 	 </div>
		  <div class="column w-50-percent">
            <form:checkbox path="onWebsite" />                         
			<form:label for="onWebsite" path="onWebsite" cssErrorClass="error">Put On Website</form:label><br/>
			<form:checkbox path="showAuthor" />                         
			<form:label for="showAuthor" path="showAuthor">Show Author</form:label>                 		  
		  </div>	 	 
	</div>
      <div class="row">
          <div class="column w-50-percent">
	         <form:label for="startTime" path="startTime" cssErrorClass="error">Start Time</form:label><br/>
	         <form:input path="startTime" /> <form:errors path="startTime" />                  
	 	 </div>	
          <div class="column w-50-percent">
	         <form:label for="endTime" path="endTime" cssErrorClass="error">End Time</form:label><br/>
	         <form:input path="endTime" /> <form:errors path="endTime" />                  
	 	 </div>		 	  	 
	</div>	
      <div class="row">
          <div class="column w-50-percent">
	         <form:label for="entrancePrice" path="entrancePrice" cssErrorClass="error">Entrance Price</form:label><br/>
	         <form:input path="entrancePrice" /> <form:errors path="entrancePrice" />                  
	 	 </div>	 	 	 	 
	</div>		
      <div class="row">
          <div class="column w-70-percent">
 				<input type="submit" class="btn btn-primary" id="addEventButton" value="Add Event"/> 
 				<button type="button" class="btn btn-primary" onclick="javascript:reset()">Reset</button>
	 	 </div>
	</div>	
      <div class="row">
          <div class="column w-100-percent">
	          <form:label for="description" path="description" cssErrorClass="error">Description</form:label><br/>
	          <form:textarea rows="20" cols="80" path="description" /> <form:errors path="description" />                  
	 	 </div>	 	 
	</div>		
</div>		
</form:form>