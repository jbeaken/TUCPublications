<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
		<form:form modelAttribute="sale" action="edit" method="post">
		  	<form:hidden path="id"/>
			<form:hidden path="creationDate"/>
			<form:hidden path="note"/>
			<form:hidden path="vat"/>
			<form:hidden path="originalQuantity"/>
			<form:hidden path="event.id"/>
			<form:hidden path="stockItem.id"/>
			<form:hidden path="stockItem.isbn"/>
			<form:hidden path="stockItem.title"/>
			<form:hidden path="stockItem.quantityToKeepInStock"/>
			<form:hidden path="stockItem.quantityInStock"/>
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
			<form:label	for="quantity" path="quantity" cssErrorClass="error">Quantity</form:label><br/>
			<form:input path="quantity" type="number" min="1" required="required" autofocus="autofocues"/>			
	  	 </div>
		  <div class="column w-33-percent">
			          <form:label for="discount" path="discount" cssErrorClass="error">Discount</form:label><br/>
                      <form:input path="discount" required="required"/>
	  	  </div>		
          <div class="column w-33-percent">
          		<label>Sell Price</label><br/>
                <form:input path="sellPrice" required="required"/>
		</div>
      </div>
        <div class="row">		
          <div class="column w-33-percent">
          	<br/>
            <input type="submit" class="btn btn-primary" id="editSaleSubmitButton" value="Save Changes"/>
		</div>
      </div>      
</div>
</form:form>


