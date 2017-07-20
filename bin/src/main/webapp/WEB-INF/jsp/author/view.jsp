<fieldset>		
	<legend>View Supplier</legend>
	<p>
		${supplier.name}
	</p>
	<p>
		Telephone : ${supplier.telephone1}
	</p>
	<p>
		Email : ${supplier.email}
	</p>
	<p>
		Contact Name : ${supplier.contactName}
	</p>
	<hr/>	
	<p>
		Account Number : ${supplier.supplierAccount.accountNumber}
	</p>
	<p>
		Min Order Quantity : ${supplier.supplierAccount.minimumOrderQuantity}
	</p>
	<p>
		Min Order Price ${supplier.supplierAccount.minimumOrderPrice}
	</p>	
	<hr/>								
	<p>
		${supplier.address.address1}
	</p>
	<p>
		${supplier.address.address2}
	</p>				
	<p>
		${supplier.address.address3}
	</p>				
	<p>
		${supplier.address.city}
	</p>				
	<p>
		${supplier.address.postcode}
	</p>				
</fieldset>

