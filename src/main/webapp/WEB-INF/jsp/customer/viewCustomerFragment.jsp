<div class="rows">
     <div class="row">
          <div class="column w-33-percent">
			<label>Customer ID</label><br/>        
			${customer.id}
	  	 </div>
          <div class="column w-33-percent">
			<label>Customer Name</label><br/>        
			${customer.fullName}
	  	 </div>
		  <div class="column w-33-percent">
		  	<label>Type of Customer</label><br/> 
		  	${customer.customerType.displayName}     
	  	  </div>		
	</div>
     <div class="row">
          <div class="column w-33-percent">
			<label>Telephone</label><br/>        
			${customer.fullPhoneNumberWithBreaks}
	  	 </div>
          <div class="column w-33-percent">
			<label>Address</label><br/>        
			${customer.fullAddressWithBreaks}
	  	 </div>
	</div>
</div>