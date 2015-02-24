<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>	  	
<div class="rows">
     <div class="row">
          <div class="column w-33-percent">
			<label>Sale Or Return No.</label><br/>        
			${saleOrReturn.id}
	  	 </div>     
          <div class="column w-33-percent">
			<label>Customer ID</label><br/>        
			${saleOrReturn.customer.id}
	  	 </div>     
          <div class="column w-33-percent">
			<label>Customer</label><br/>        
			${saleOrReturn.customer.fullName}
	  	 </div>
	</div>
     <div class="row">
          <div class="column w-33-percent">
			<label>Telephone</label><br/>        
			${saleOrReturn.customer.fullPhoneNumberWithBreaks}
	  	 </div>
          <div class="column w-33-percent">
			<label>Address</label><br/>        
			${saleOrReturn.customer.fullAddressWithBreaks}
	  	 </div>
		  <div class="column w-33-percent">
		  	<label>Scheduled Return Date</label><br/> 
		  	<fmt:formatDate value="${saleOrReturn.returnDate}" pattern="dd/MM/yyyy"/>       
	  	  </div>		
	</div>	
	<c:if test="${saleOrReturn.customerReference != null}">
	     <div class="row">
	          <div class="column w-33-percent">
				<label>Customer Reference</label><br/>        
				${saleOrReturn.customerReference}
		  	 </div>		
		</div>	
	</c:if>		 
</div>	
<br/>
<br/>
<br/>
<br/>
<c:if test="${not empty saleOrReturnOrderLineList}">
	<display:table 
		name="saleOrReturnOrderLineList" 
		decorator="org.bookmarks.ui.SaleOrReturnOrderLineDecorator">
	  <display:column property="stockItem.isbn" title="ISBN"/>
	  <display:column property="stockItem.title" title="Title" maxLength="75"/>
	  <display:column property="amount" title="Quantity"/>
	  <display:column property="sellPrice" title="Sell Price"/>
	  <display:column property="price" title="Price"/>
	</display:table>	
</c:if>
<br/>	
<br/>
<br/>
<label>Total Price: </label><fmt:formatNumber value="${totalPrice}" type="currency"/> <br/>
<label>No. of items: </label>${noOfLines}