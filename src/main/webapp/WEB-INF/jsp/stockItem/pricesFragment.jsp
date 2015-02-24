<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<div class="row">
          <div class="column w-33-percent">
               <form:label for="publisherPrice" path="publisherPrice" cssErrorClass="error">Publisher Price</form:label><br/>
					<form:input path="publisherPrice" required="required" onkeyup="calculatePricesFromPublisherPrice('costPrice')"/>  
	 	 </div>
          <div class="column w-33-percent">
               <form:label for="discount" path="discount" cssErrorClass="error">Discount</form:label><br/>
					<form:input id="discount" path="discount" required="required" onkeyup="javascript:calculatePricesFromDiscount('discount')"/>
	 	 </div>	
          <div class="column w-33-percent">
               <form:label for="costPrice" path="costPrice" cssErrorClass="error">Cost Price</form:label><br/>
					<form:input id="costPrice" path="costPrice" onkeyup="calculatePricesFromCostPrice('costPrice')"/>  
	 	 </div>	  
 	 	 

	</div>	
      <div class="row">
		  <div class="column w-33-percent">
               <form:label for="sellPrice" path="sellPrice" cssErrorClass="error">Sell Price</form:label><br/>
					<form:input path="sellPrice" required="required"/>
		  </div>	  
          <div class="column w-33-percent">
               <form:label for="margin" path="margin" cssErrorClass="error">Margin</form:label><br/>
					<form:input path="margin" onkeyup="javascript:calculatePricesFromMargin()"/>
	 	 </div>
	 	<div class="column w-33-percent">
               <form:label for="postage" path="postage" cssErrorClass="error">Postage</form:label><br/>
				<form:input id="postage" path="postage" />  
	 	 </div>	 
	</div>
	 	 
