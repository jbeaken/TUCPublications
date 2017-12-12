<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<form:form action="add" method="post">

	 <div class="row">
        <div class="form-group">
		     <label>Email</label>
		     <input type="text" name="email" required="required" class="form-control"/>
  	    </div>
	</div>

	<input type="submit" class="btn btn-primary" value="Add" id="addButton"/>

</form:form>
