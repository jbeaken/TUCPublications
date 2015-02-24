<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>	
<fmt:setLocale value="en_GB" scope="application"/>

        <div class="row">
	          <div class="column w-20-percent">
	 				<label>2nd Hand :</label>
		 	 </div>
	          <div class="column w-10-percent">
	 				<fmt:formatNumber value="${invoice.secondHandPrice}" type="currency" />
		 	 </div>
	  </div>  
        <div class="row">
	          <div class="column w-20-percent">
	 				<label>Service Charge :</label>
		 	 </div>
	          <div class="column w-10-percent">
	 				<fmt:formatNumber value="${invoice.serviceCharge}" type="currency" />
		 	 </div>
	  </div>  
        <div class="row">
	          <div class="column w-20-percent">
	 				<label>Stock Charge :</label>
		 	 </div>
	          <div class="column w-10-percent">
	 				<fmt:formatNumber value="${invoice.stockItemCharges}" type="currency" />
		 	 </div>
	  </div>  
       <div class="row">
	          <div class="column w-20-percent">
	          		----------------------------<br/>
	 				<label>Total Price :</label>
		 	 </div>
	          <div class="column w-10-percent">
	          ----------------<br/>
	 				<fmt:formatNumber value="${invoice.totalPrice}" type="currency" />
		 	 </div>
	  </div>  
       <div class="row">
	          <div class="column w-20-percent">
	 				<label>VAT Payable</label>
		 	 </div>
	          <div class="column w-10-percent">
	 				<fmt:formatNumber value="${invoice.vatPayable}" type="currency" />
		 	 </div>
	  </div>  
