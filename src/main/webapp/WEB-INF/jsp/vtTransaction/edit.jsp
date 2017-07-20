<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="author" action="edit" method="post">
	<form:hidden path="id"/>
	<form:hidden path="note"/>
	<form:hidden path="creationDate"/>
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
			     <label>Author Name</label>
			     <form:input path="name" autofocus="autofocus" required="required" />
	  	    </div>         
	 	     </div>	 
         
      <div class="row">
          <div class="column w-100-percent">
 				   <input type="submit" class="btn btn-primary" value="Add" id="addButton"/> 
	 	     </div>
	   </div>		
</div>			
</form:form>


