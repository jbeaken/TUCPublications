<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="publisher" action="add" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-70-percent">
			<form:label	for="name" path="name" cssErrorClass="error">First Name</form:label><br/>
			<form:input path="name" /> <form:errors path="name" />			
				
	  	 </div>
      </div>
      <div class="row">
          <div class="column w-70-percent">
                                        <form:label for="address.address1" path="address.address1" cssErrorClass="error">Address Line 1</form:label><br/>
                                        <form:input path="address.address1" /> <form:errors path="address.address1" />                  
	 	 </div>
		  <div class="column w-30-percent">
                                        <form:label for="address.address2" path="address.address2" cssErrorClass="error">Address Line 2</form:label><br/>
                                        <form:input path="address.address2" /> <form:errors path="address.address2" />                  
		  </div>
	</div>		
      <div class="row">
          <div class="column w-70-percent">
                                        <form:label for="address.address3" path="address.address3" cssErrorClass="error">Address Line 1</form:label><br/>
                                        <form:input path="address.address3" /> <form:errors path="address.address3" />                  
	 	 </div>
		  <div class="column w-30-percent">
                                        <form:label for="address.city" path="address.city" cssErrorClass="error">City</form:label><br/>
                                        <form:input path="address.city" /> <form:errors path="address.city" />                  
		  </div>
	</div>		
      <div class="row">
          <div class="column w-70-percent">
                                        <form:label for="address.postcode" path="address.postcode" cssErrorClass="error">Postcode</form:label><br/>
                                        <form:input path="address.postcode" /> <form:errors path="address.postcode" />                  
	 	 </div>
		  <div class="column w-30-percent">
                                        <form:label for="email" path="email" cssErrorClass="error">Email</form:label><br/>
                                        <form:input path="email" /> <form:errors path="email" />                        
		  </div>
	</div>		
      <div class="row">
          <div class="column w-70-percent">
 				<input type="submit" class="btn btn-primary"/> <button type="button"  class="btn btn-primary" onclick="javascript:reset()">Reset</button>
	 	 </div>
	</div>		
</div>		
</form:form>