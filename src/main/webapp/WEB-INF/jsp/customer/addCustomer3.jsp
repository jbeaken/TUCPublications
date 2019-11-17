<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="customer" action="add" method="post">
        <div class="row">
          <div class="col-sm-4"><div class="form-group">
			<form:label	for="firstName" path="firstName">First Name</form:label>
			<form:input class="form-control" path="firstName" autofocus="autofocus" required="required"/>
		</div></div>
		<div class="col-sm-4"><div class="form-group">
			<form:label for="lastName" path="lastName">Last name</form:label>
			<form:input class="form-control" path="lastName" required="required"/>                		  
	  	</div></div>		
		<div class="col-sm-4"><div class="form-group">
				<form:label for="address.address1" path="address.address1">Address Line 1</form:label>
				<form:input class="form-control" path="address.address1" />                   
	 	 </div></div>
      </div>
      <div class="row">
		  <div class="col-sm-4"><div class="form-group">
                <form:label for="address.address2" path="address.address2">Address Line 2</form:label>
                <form:input class="form-control" path="address.address2" />                  
		  </div></div>
          <div class="col-sm-4"><div class="form-group">
                <form:label for="address.address3" path="address.address3">Address Line 3</form:label>
                <form:input class="form-control" path="address.address3" />               
	 	 </div></div>
		  <div class="col-sm-4"><div class="form-group">
                <form:label for="address.city" path="address.city">City</form:label>
                <form:input class="form-control" path="address.city" />               
		  </div></div>
	</div>		
      <div class="row">
          <div class="col-sm-4"><div class="form-group">
              <form:label for="address.postcode" path="address.postcode">Postcode</form:label>
              <form:input class="form-control" path="address.postcode" />                
	 	 </div></div>
		  <div class="col-sm-4"><div class="form-group">
	          <form:label for="contactDetails.email" path="contactDetails.email">Email</form:label>
	          <form:input class="form-control" path="contactDetails.email" />                       
		  </div></div>
		  <div class="col-sm-4"><div class="form-group">
               <form:label for="contactDetails.homeNumber" path="contactDetails.homeNumber">Mobile</form:label>
               <form:input class="form-control" path="contactDetails.homeNumber" />                    
		  </div></div>
	</div>	
	<div class="row">
		  <div class="col-sm-4"><div class="form-group">
              <form:label for="contactDetails.workNumber" path="contactDetails.workNumber">Work</form:label>
              <form:input class="form-control" path="contactDetails.workNumber" />                         
		  </div></div>
		  <div class="col-sm-4"><div class="form-group">
               <form:label for="contactDetails.mobileNumber" path="contactDetails.mobileNumber">Home</form:label>
               <form:input class="form-control" path="contactDetails.mobileNumber" />                      
		  </div></div>	
		  <div class="col-sm-4"><div class="form-group">
			<label>Customer Type</label>
			<form:select path="customerType" class="form-control">
				<form:options items="${customerTypeList}" itemLabel="displayName"/>
			</form:select>                        
		  </div></div>			  	  		  	
	</div>
	<div class="row">
		  <div class="col-sm-4"><div class="form-group">
              <form:label for="bookmarksDiscount" path="bookmarksDiscount">Bookmarks Discount</form:label>
              <form:input class="form-control" path="bookmarksDiscount" />                      
		  </div></div>
		  <div class="col-sm-4"><div class="form-group">
				<label>Non Bookmarks Discount</label>
				<form:input class="form-control" path="nonBookmarksDiscount" />                      
		  </div></div>	
		    <div class="col-sm-4">
			  	<div class="checkbox">	 
					<label>
					  <form:checkbox path="bookmarksAccount.accountHolder" /> Account
					</label>
				</div>
				<div class="checkbox">	 
					<label>
					   <form:checkbox path="bookmarksAccount.sponsor" />Sponsor   
					</label>
				</div>
				<div class="checkbox">					
					<label>
					  <form:checkbox path="bookmarksAccount.paysInMonthly" />Pays In Monthly
					</label>		  
				</div>
			</div>		  		  	
	</div>	
      <div class="row">
				<button type="submit" class="btn btn-primary" id="addCustomerButton">Add</button>
				<button type="button" class="btn btn-danger" id="addCustomerAndCreateCustomerOrderButton" onclick="javascript:submitForm('/customer/addAndCreateCustomerOrder')">Add & Create Customer Order</button>
				<button type="button" class="btn btn-danger" id="addCustomerAndCreateInvoiceButton" onclick="javascript:submitForm('/customer/addAndCreateInvoice')">Add & Create Invoice</button>
	</div>		
</form:form>