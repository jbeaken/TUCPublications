<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<form:form action="subscribe" method="post">

	 <div class="row">
        <div class="form-group">
		     <label>Email</label>
		     <input type="email" name="email" required="required" class="form-control"/>
  	    </div>
        <div class="form-group">
		     <label>First Name</label>
		     <input type="text" name="firstname" class="form-control"/>
  	    </div>  	
        <div class="form-group">
		     <label>Last Name</label>
		     <input type="text" name="lastname" class="form-control"/>
  	    </div>   	        
	</div>

	<input type="submit" class="btn btn-primary" value="Subscribe" />

</form:form>
