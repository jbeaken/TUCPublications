<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<script src="<c:url value="/resources/js/jQuery-File-Upload-9.8.1/js/vendor/jquery.ui.widget.js" />"></script>
<script src="<c:url value="/resources/js/jQuery-File-Upload-9.8.1/js/jquery.iframe-transport.js" />"></script>
<script src="<c:url value="/resources/js/jQuery-File-Upload-9.8.1/js/jquery.fileupload.js" />"></script>

<!-- CSS to style the file input field as button and adjust the Bootstrap progress bars -->
<link rel="stylesheet" href="<c:url value="/resources/js/jQuery-File-Upload-9.8.1/css/jquery.fileupload.css" /> ">
<link rel="stylesheet" href="<c:url value="/resources/js/jQuery-File-Upload-9.8.1/css/jquery.fileupload-ui.css" />" />

<script src="${pageContext.request.contextPath}/resources/ckeditor/ckeditor.js"></script>
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
		
		 $('#fileupload').fileupload({
		        dataType: 'json',
		        url : '/bookmarks/events/upload',
		        formData: [ { name : 'eventId', value : ${event.id} }], 
		        done: function (e, data) {
		            $.each(data.result.files, function (index, file) {
		                $('<p/>').text(file.name).appendTo(document.body);
		            });
		        }
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
	
	                
                <span class="btn btn-success fileinput-button">
                    <i class="glyphicon glyphicon-plus"></i>
                    <span>Add files...</span>
                    <input name="file" multiple="" type="file">
                </span>
                
    <div class="row">
          <div class="column w-70-percent">
 			<input id="fileupload" type="file" class="fileinput-button" name="files" data-url="/bookmarks/events/upload?eventId=$(event.id})" multiple> 
	 	 </div>
	</div>	
		
<form:form modelAttribute="event" action="edit" method="post">
<form:hidden path="id"/>
<form:hidden path="creationDate"/>
<form:hidden path="note"/>
<div class="rows">
        <div class="row">
          <div class="column w-50-percent">
			<form:label for="name" path="name" cssErrorClass="error">Event Name</form:label><br/>
			<form:input path="name" id="focus"/> <form:errors path="name" />			
	  	 </div>
          <div class="column w-50-percent">
			<form:label for="name" path="name" cssErrorClass="error">Event Type</form:label><br/>
          		<form:select path="type">
            			<form:options items="${typeList}" itemLabel="displayName"/>
        		</form:select>		
	  	 </div>	
      </div>
      <div class="row">
          <div class="column w-50-percent">
             <form:label for="startDate" path="startDate" cssErrorClass="error">Start Date</form:label><br/>
             <form:input path="startDate" /> <form:errors path="startDate" />                  
	 	 </div>
		  <div class="column w-50-percent">
            <form:label for="endDate" path="endDate" cssErrorClass="error">End Date</form:label><br/>
            <form:input path="endDate" /> <form:errors path="endDate" />                  
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
 			<input type="submit" class="btn btn-primary" id="editEventButton" value="Save"/> 
			<button onclick="" class="btn btn-warning">Back To Search</button>
	 	 </div>
	</div>	
	
	<div class="row">
			<textarea name="description" id="editor1" rows="20" cols="80">
                ${event.description}
            </textarea>	
 			<script>
                // Replace the <textarea id="editor1"> with a CKEditor
                // instance, using default configuration.
                CKEDITOR.replace( 'editor1', {
                	extraPlugins = 'div';
                } );
            </script>  	 	 	 	 
	</div>	
</div>		
</form:form>