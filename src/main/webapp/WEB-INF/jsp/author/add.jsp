<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<form:form modelAttribute="author" action="add" method="post">

	 <div class="row">
        <div class="form-group">
		     <label>Author Name</label>
		     <form:input path="name" autofocus="autofocus" required="required" class="form-control"/>
  	    </div>         
	</div>
	         
	<input type="submit" class="btn btn-primary" value="Add" id="addButton"/> 
	
</form:form>