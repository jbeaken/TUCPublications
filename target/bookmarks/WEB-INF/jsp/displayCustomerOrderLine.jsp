<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
		  	<fieldset>		
				<legend>Customer Order</legend>
				<table>
					<tr>
						<td>Customer:</td>
						<td>${customerOrderLine.customer.firstName} ${customerOrderLine.customer.lastName}</td>
						<td>Address1:</td>
						<td>${customerOrderLine.customer.address.address1}</td>
						<td>Address2:</td>
						<td>${customerOrderLine.customer.address.address2}</td>
					</tr>
					<tr>
						<td>Payment:</td>
						<td>${customerOrderLine.paymentType}</td>
						<td>Delivery:</td>
						<td>${customerOrderLine.deliveryType}</td>
						<td>Phone:</td>
						<td>${customerOrderLine.customer.telephoneDirectory.mobileNumber}</td>
					</tr>
					<tr>
						<td>Credit Card No:</td>
						<td>${customerOrderLine.creditCard.creditCard1}&nbsp;${customerOrderLine.creditCard.creditCard2}&nbsp;${customerOrderLine.creditCard.creditCard3}&nbsp;${customerOrderLine.creditCard.creditCard4}</td>
						<td>Expiry:</td>
						<td>${customerOrderLine.creditCard.expiryMonth}&nbsp;${customerOrderLine.creditCard.expiryYear}</td>
						<td>Sec Code:</td>
						<td>${customerOrderLine.creditCard.securityCode}</td>
					</tr>
					<tr>
						<td>Note:</td>
						<td rowspan="5">${customerOrderLine.note}</td>
					</tr>
				</table>
			</fieldset>