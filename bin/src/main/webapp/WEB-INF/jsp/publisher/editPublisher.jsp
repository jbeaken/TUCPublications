<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="publisher" action="/bookmarks/publisher/edit?flow=${flow}" method="post">
<form:hidden path="id"/>
			<form:hidden path="note"/>
			<form:hidden path="creationDate"/>
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
			<form:label	for="name" path="name" cssErrorClass="error">Publisher Name</form:label><br/>
			<form:input path="name" /> <form:errors path="name" />				
	  	 </div>
          <div class="column w-33-percent">
			<form:label	for="telephone1" path="telephone1" cssErrorClass="error">Telephone 1</form:label><br/>
			<form:input path="telephone1" /> <form:errors path="telephone1" />				
	  	 </div>
          <div class="column w-33-percent">
			<form:label	for="telephone2" path="telephone2" cssErrorClass="error">Telephone 2</form:label><br/>
			<form:input path="telephone2" /> <form:errors path="telephone2" />				
	  	 </div>		 
      </div>
      <div class="row">
          <div class="column w-33-percent">
                                        <form:label for="address.address1" path="address.address1" cssErrorClass="error">Address Line 1</form:label><br/>
                                        <form:input path="address.address1" /> <form:errors path="address.address1" />                  
	 	 </div>
		  <div class="column w-33-percent">
                                        <form:label for="address.address2" path="address.address2" cssErrorClass="error">Address Line 2</form:label><br/>
                                        <form:input path="address.address2" /> <form:errors path="address.address2" />                  
		  </div>
          <div class="column w-33-percent">
                                        <form:label for="address.address3" path="address.address3" cssErrorClass="error">Address Line 3</form:label><br/>
                                        <form:input path="address.address3" /> <form:errors path="address.address3" />                  
	 	 </div>
	</div>		
      <div class="row">		 
		  <div class="column w-33-percent">
                                        <form:label for="address.city" path="address.city" cssErrorClass="error">City</form:label><br/>
                                        <form:input path="address.city" /> <form:errors path="address.city" />                  
		  </div>
          <div class="column w-33-percent">
                                        <form:label for="address.postcode" path="address.postcode" cssErrorClass="error">Postcode</form:label><br/>
                                        <form:input path="address.postcode" /> <form:errors path="address.postcode" />                  
	 	 </div>
		  <div class="column w-33-percent">
                                        <form:label for="email" path="email" cssErrorClass="error">Email</form:label><br/>
                                        <form:input path="email" /> <form:errors path="email" />                        
		  </div>
	</div>		
      <div class="row">		  
	          <div class="column w-33-percent">
	          	<form:label	for="supplier.id" path="supplier.id">Supplier</form:label><br/>
					<form:select path="supplier.id">
           				<form:options items="${supplierList}" itemValue="id" itemLabel="name"/>
           			</form:select>	
		  	 </div>	
		  <div class="column w-33-percent">
              <form:label for="contactName" path="contactName" cssErrorClass="error">Contact Name</form:label><br/>
              <form:input path="contactName" /> <form:errors path="contactName" />                  
	 	 </div>
	</div>		
      <div class="row">
          <div class="column w-70-percent">
 				<input type="submit" class="btn btn-primary"/> 
				<c:if test="${flow == 'editStock'}">
				<button type="button" class="btn btn-primary" onclick="javascript:window.close()">Close Window</button>		
				</c:if>
				<c:if test="${flow != 'editStock'}">				
					<a href="/bookmarks/publisher/searchFromSession"><button type="button" class="btn btn-primary">Back to	search</button></a>
				</c:if>
	 	 </div>
	</div>		
</div>		
</form:form>