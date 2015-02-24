<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="supplier" action="edit" method="post">
			<form:hidden path="id"/>
			<form:hidden path="note"/>
			<form:hidden path="creationDate"/>
<div class="rows">
                <div class="row">
          <div class="column w-33-percent">
			<form:label	for="name" path="name" cssErrorClass="error">Supplier Name</form:label><br/>
			<form:input path="name" /> <form:errors path="name" />			
	  	 </div>
          <div class="column w-33-percent">
			<form:label	for="defaultDiscount" path="defaultDiscount" cssErrorClass="error">Default Discount</form:label><br/>
			<form:input path="defaultDiscount" /> <form:errors path="defaultDiscount" />			
	  	 </div>
          <div class="column w-33-percent">
               <form:label for="email" path="email" cssErrorClass="error">Email</form:label><br/>
               <form:input path="email" /> <form:errors path="email" />                  
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
                <form:label for="supplierAccount.accountNumber" path="supplierAccount.accountNumber" cssErrorClass="error">Account No.</form:label><br/>
                <form:input path="supplierAccount.accountNumber" /> <form:errors path="supplierAccount.accountNumber" />                  
	 	 </div>	 	 	 	 
	</div>		
      <div class="row">
          <div class="column w-33-percent">
              <form:label for="telephone1" path="telephone1" cssErrorClass="error">Telephone 1</form:label><br/>
              <form:input path="telephone1" /> <form:errors path="telephone1" />                  
	 	 </div>
          <div class="column w-33-percent">
              <form:label for="telephone2" path="telephone2" cssErrorClass="error">Telephone 2</form:label><br/>
              <form:input path="telephone2" /> <form:errors path="telephone2" />                  
	 	 </div>
          <div class="column w-33-percent">
              <form:label for="contactName" path="contactName" cssErrorClass="error">Contact Name</form:label><br/>
              <form:input path="contactName" /> <form:errors path="contactName" />                  
	 	 </div>		 
	</div>		
      <div class="row">

          <div class="column w-33-percent">
              <form:label for="supplierAccount.minimumOrderPrice" path="supplierAccount.minimumOrderPrice" cssErrorClass="error">Min. Order Price</form:label><br/>
              <form:input path="supplierAccount.minimumOrderPrice" /> <form:errors path="supplierAccount.minimumOrderPrice" />                  
	 	 </div>		 
            <div class="column w-33-percent">
                <form:label for="supplierAccount.minimumOrderQuantity" path="supplierAccount.minimumOrderQuantity" cssErrorClass="error">Min. Order Qty</form:label><br/>
                <form:input path="supplierAccount.minimumOrderQuantity" /> <form:errors path="supplierAccount.minimumOrderQuantity" />                  
	 	 </div>	 			 	 	 
	</div>
      <div class="row">
          <div class="column w-100-percent">
 				<input type="submit" class="btn btn-success" id="editSupplierButton" value="Update Supplier"/> 
 				<button type="button" class="btn btn-primary" onclick="javascript:reset()">Reset</button>
				<c:if test="${flow == 'search'}">
					<a href="/bookmarks/supplier/searchFromSession"><button type="button" class="btn btn-primary">Back to search</button></a>
				</c:if>
	 	 </div>
	</div>		
</div>		
		</form:form>


