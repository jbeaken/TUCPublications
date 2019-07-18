<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>	  	
<div class="rows">
     <div class="row">
          <div class="column w-50-percent">
			<label>Customer</label><br/>        
			${saleOrReturn.customer.fullName}
	  	 </div>
		  <div class="column w-50-percent">
		  	<label>Scheduled Return Date</label><br/> 
		  	<fmt:formatDate value="${saleOrReturn.returnDate}" pattern="dd/MM/yyyy"/>       
	  	  </div>		
	</div>
     <div class="row">
          <div class="column w-50-percent">
			<label>Telephone</label><br/>        
			${saleOrReturn.customer.fullPhoneNumberWithBreaks}
	  	 </div>
          <div class="column w-50-percent">
			<label>Address</label><br/>        
			${saleOrReturn.customer.fullAddressWithBreaks}
	  	 </div>
	</div>
		  
    <div class="row">
          <div class="column w-50-percent">
			<label>Status</label><br/>        
			${saleOrReturn.saleOrReturnStatus.displayName}
	  	 </div>	
	  	 <div class="column w-50-percent">
			<label>Customer Reference</label><br/>        
			${saleOrReturn.customerReference}
	  	 </div>	
	 </div>
	 <div class="row">
		  <div class="column w-50-percent">
		  	<c:if test="${flow == 'searchSaleOrReturn'}">
				<a href="/saleOrReturn/searchFromSession">
					<button class="btn btn-primary">Return To Search</button>
				</a>
		  	 </c:if>
				<a href="/saleOrReturn/print?saleOrReturnId=${saleOrReturn.id}" target="_blank">
					<button type="button" class="btn btn-primary">Print</button>
				</a>		  	 
		  </div>
	</div>		
		 
</div>	
<br/>
<br/>
	<display:table 
		name="saleOrReturnOrderLineList" 
		decorator="org.bookmarks.ui.SaleOrReturnOrderLineDecorator">
	  <display:column property="stockItem.isbn" title="ISBN"/>
	  <display:column property="stockItem.title" title="Title" maxLength="75"/>
	  <display:column property="amount" title="Quantity"/>
	  <display:column property="amountSold" title="Quantity Sold"/>
	  <display:column property="amountRemainingWithCustomer" title="With Customer"/>
	  <display:column property="sellPrice" title="Sell Price"/>
	  <display:column property="price" title="Price"/>
	</display:table>			

<br/>	
<br/>
<br/>
<label>Total Price: </label><fmt:formatNumber value="${totalPrice}" type="currency"/> <br/>
<label>No. of items: </label>${noOfLines}