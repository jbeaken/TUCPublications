<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<script type="text/javascript" src="<c:url value="/resources/js/postcodeLookup.js" />" ></script>
<form:form modelAttribute="customer" action="add" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
			<form:label	for="firstName" path="firstName" cssErrorClass="error">First Name</form:label><br/>
			<form:input path="firstName" autofocus="autofocus" required="required"/>
		</div>
		<div class="column w-33-percent">
			<form:label for="lastName" path="lastName" cssErrorClass="error" required="required">Last name</form:label><br/>
			<form:input path="lastName" required="required"/>                		  
	  	</div>		
		<div class="column w-33-percent">
				<form:label for="address.address1" path="address.address1" cssErrorClass="error">Address Line 1</form:label><br/>
				<form:input id="address1" path="address.address1" /> <form:errors path="address.address1" />                  
	 	 </div>
      </div>
      <div class="row">
		  <div class="column w-33-percent">
                <form:label for="address.address2" path="address.address2" cssErrorClass="error">Address Line 2</form:label><br/>
                <form:input id="address2" path="address.address2" /> <form:errors path="address.address2" />                  
		  </div>
          <div class="column w-33-percent">
                <form:label for="address.address3" path="address.address3" cssErrorClass="error">Address Line 3</form:label><br/>
                <form:input id="address3" path="address.address3" /> <form:errors path="address.address3" />                  
	 	 </div>
		  <div class="column w-33-percent">
                <form:label for="address.city" path="address.city" cssErrorClass="error">City</form:label><br/>
                <form:input id="city" path="address.city" /> <form:errors path="address.city" />                  
		  </div>
	</div>		
      <div class="row">
          <div class="column w-33-percent">
	           <form:label for="address.postcode" path="address.postcode" cssErrorClass="error">Postcode</form:label><br/>
	           <form:input id="postcode" path="address.postcode" /> <form:errors path="address.postcode" />                  
	 	 </div>
          <div class="column w-33-percent">
                                        <form:label for="address.country" path="address.country" cssErrorClass="error">Country</form:label><br/>
                                        <form:input path="address.country" /> <form:errors path="address.postcode" />
	 	 </div>	 	 
		  <div class="column w-33-percent">
               <form:label for="contactDetails.email" path="contactDetails.email" cssErrorClass="error">Email</form:label><br/>
               <form:input path="contactDetails.email" /> <form:errors path="contactDetails.email" />                        
		  </div>

	</div>	
	<div class="row">
		  <div class="column w-33-percent">
                                        <form:label for="contactDetails.workNumber" path="contactDetails.workNumber" cssErrorClass="error">Work</form:label><br/>
                                        <form:input path="contactDetails.workNumber" /> <form:errors path="contactDetails.workNumber" />                        
		  </div>
		  <div class="column w-33-percent">
                                        <form:label for="contactDetails.mobileNumber" path="contactDetails.mobileNumber" cssErrorClass="error">Mobile</form:label><br/>
                                        <form:input path="contactDetails.mobileNumber" /> <form:errors path="contactDetails.mobileNumber" />                        
		  </div>
		  <div class="column w-33-percent">
               <form:label for="contactDetails.homeNumber" path="contactDetails.homeNumber" cssErrorClass="error">Home</form:label><br/>
               <form:input path="contactDetails.homeNumber" /> <form:errors path="contactDetails.homeNumber" />                        
		  </div>		  	
			  	  		  	
	</div>
	<div class="row">
		  <div class="column w-33-percent">
                                        <form:label for="bookmarksDiscount" path="bookmarksDiscount" cssErrorClass="error">Bookmarks Discount</form:label><br/>
                                        <form:input path="bookmarksDiscount" /> <form:errors path="bookmarksDiscount" />                        
		  </div>
		  <div class="column w-33-percent">
                                        <form:label for="nonBookmarksDiscount" path="nonBookmarksDiscount" cssErrorClass="error">Non Bookmarks Discount</form:label><br/>
                                        <form:input path="nonBookmarksDiscount" /> <form:errors path="nonBookmarksDiscount" />                        
		  </div>	
		  <div class="column w-33-percent">
                                        <form:label for="customerType" path="customerType" cssErrorClass="error">Customer Type</form:label><br/>
                                           <form:select path="customerType">
  											<form:options items="${customerTypeList}" itemLabel="displayName"/>
										</form:select>                        
		  </div>		  
			  	  		  	
	</div>			
      <div class="row">
		  <div class="column w-33-percent">
		  		&nbsp;<br/> 
                 <form:checkbox path="bookmarksAccount.accountHolder" />                         
				 <form:label id="checkboxLabel" for="bookmarksAccount.accountHolder" path="bookmarksAccount.accountHolder" cssErrorClass="error">Account</form:label>
                 <form:checkbox path="bookmarksAccount.sponsor" />                         
				 <form:label for="bookmarksAccount.sponsor" path="bookmarksAccount.sponsor" cssErrorClass="error">Sponsor</form:label>
                 <form:checkbox path="bookmarksAccount.paysInMonthly" />                         
				 <form:label for="bookmarksAccount.paysInMonthly" path="bookmarksAccount.paysInMonthly" cssErrorClass="error">Pays In Monthly</form:label>
		  </div>		  		  	
	</div>	
      <div class="row">
          <div class="column w-100-percent">
				<button type="button" class="btn btn-primary" id="addCustomerButton" onclick="javascript:submitForm('/customer/add')">Add</button>
				<button type="button" class="btn btn-danger" id="addCustomerAndCreateCustomerOrderButton" onclick="javascript:submitForm('/customer/addAndCreateCustomerOrder')">Add & Create Customer Order</button>
				<button type="button" class="btn btn-danger" id="addCustomerAndCreateInvoiceButton" onclick="javascript:submitForm('/customer/addAndCreateInvoice')">Add & Create Invoice</button>
	 	 </div>
	 	 
	</div>	

	<div class="row">	
		<div class="panel panel-default">
			
			<div class="panel-heading">
				<h3>Address Lookup</h3>
			</div>
			
			<div class="panel-body">
				<div class="input-group">
				  <input type="text" id="postcodeLookup" class="form-control" />	
				  <span class="input-group-addon btn btn-warning" onclick="javascript:postcodeLookup()">Look Up</span>
				</div>	
				
				<br/><br/>
				
				<select id="dropDown" class="form-control" style="display : none;" onchange="javascript:lookupAddress();"></select>	
			</div>
			
		</div>		
	</div>	
</div>		
</form:form>