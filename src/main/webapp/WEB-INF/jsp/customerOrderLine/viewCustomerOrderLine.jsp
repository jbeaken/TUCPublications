<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<div class="rows">
     <div class="row">
          <div class="column w-50-percent">
			<label>Order No.</label><br/>        
			${customerOrderLine.id}
	  	 </div>		
	</div>
</div>
<jsp:include page="../customer/viewCustomerFragment.jsp"/>
<jsp:include page="../stockItem/viewStockItemFragment.jsp"/>
<br/>
<br/>
				<fieldset>
					<legend>Payment Details</legend>
					<table>
						<tr>
							<td>Payment:</td>
							<td>${customerOrderLine.paymentType.displayName}</td>
							<c:if test="${customerOrderLine.paymentType == 'CREDIT_CARD'}">						
								<td>Credit Card No:</td>
								<td>${customerOrderLine.creditCard.creditCard1}&nbsp;${customerOrderLine.creditCard.creditCard2}&nbsp;${customerOrderLine.creditCard.creditCard3}&nbsp;${customerOrderLine.creditCard.creditCard4}</td>
								<td>Expiry:</td>
								<td>${customerOrderLine.creditCard.expiryMonth}&nbsp;${customerOrderLine.creditCard.expiryYear}</td>
								<td>Sec Code:</td>
								<td>${customerOrderLine.creditCard.securityCode}</td>
							</c:if>
						</tr>
						<tr>
							<td>Note:</td>
							<td rowspan="5">${customerOrderLine.note}</td>
						</tr>
					</table>
				</fieldset>