<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<fmt:setLocale value="en_GB"/>
		<form:form modelAttribute="customer" action="edit?flow=${flow}" method="post">
		  	<form:hidden path="id"/>

<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
			<form:label	for="firstName" path="firstName" cssErrorClass="error">First Name</form:label><br/>
			<form:input path="firstName" />
	  	 </div>
		  <div class="column w-33-percent">
	          <form:label for="lastName" path="lastName" id="focus" cssErrorClass="error">Last name</form:label><br/>
              <form:input path="lastName" />
	  	</div>
		  <div class="column w-33-percent">
                <form:label for="contactDetails.email" path="contactDetails.email" cssErrorClass="error">Email</form:label><br/>
                <form:input path="contactDetails.email" /> <form:errors path="contactDetails.email" />
		  </div>

      </div>
      <div class="row">
          <div class="column w-33-percent">
	         <form:label for="address.address1" path="address.address1" cssErrorClass="error">Address Line 1</form:label><br/>
	         <form:input path="address.address1" /> <form:errors path="address.address1" />
	 	 </div>
		  <div class="column w-33-percent">
                <form:label for="address.address2" path="address.address2" cssErrorClass="error">Address Line 2</form:label><br/>
                <form:input path="address.address2" /> <form:errors path="address.address2" />
		  </div>
          <div class="column w-33-percent">
                <form:label for="address.address3" path="address.address3" cssErrorClass="error">Address Line 3</form:label><br/>
                <form:input path="address.address3" /> <form:errors path="address.address3" />
	 	 </div>
	</div>
      <div class="row">
		  <div class="column w-33-percent">
                <form:label for="address.city" path="address.city" cssErrorClass="error">City</form:label><br/>
                <form:input path="address.city" /> <form:errors path="address.city" />
		  </div>
          <div class="column w-33-percent">
                <form:label for="address.postcode" path="address.postcode" cssErrorClass="error">Postcode</form:label><br/>
                <form:input path="address.postcode" /> <form:errors path="address.postcode" />
	 	 </div>

          <div class="column w-33-percent">
            <form:label for="address.country" path="address.country" cssErrorClass="error">Country</form:label><br/>
            <form:input path="address.country" /> <form:errors path="address.country" />
	 	 </div>

	</div>
	<div class="row">

		  <div class="column w-33-percent">
                <form:label for="contactDetails.homeNumber" path="contactDetails.homeNumber" cssErrorClass="error">Home</form:label><br/>
                <form:input path="contactDetails.homeNumber" /> <form:errors path="contactDetails.homeNumber" />
		  </div>
		  <div class="column w-33-percent">
                <form:label for="contactDetails.workNumber" path="contactDetails.workNumber" cssErrorClass="error">Work</form:label><br/>
                <form:input path="contactDetails.workNumber" /> <form:errors path="contactDetails.workNumber" />
		  </div>
		  <div class="column w-33-percent">
                <form:label for="contactDetails.mobileNumber" path="contactDetails.mobileNumber" cssErrorClass="error">Mobile</form:label><br/>
                <form:input path="contactDetails.mobileNumber" /> <form:errors path="contactDetails.mobileNumber" />
		  </div>
	</div>

  <div class="row">
		<div class="column w-33-percent">
			<form:label for="bookmarksAccount.currentBalance" path="bookmarksAccount.currentBalance">Current Balance</form:label><br/>
			&pound;<fmt:formatNumber minFractionDigits="2" maxFractionDigits="2" value="${customer.bookmarksAccount.currentBalance}" />
		</div>

		<div class="column w-33-percent">
          <form:label for="bookmarksAccount.amountPaidInMonthly" path="bookmarksAccount.amountPaidInMonthly">Monthly Payment</form:label><br/>
					&pound;<fmt:formatNumber minFractionDigits="2" maxFractionDigits="2" value="${customer.bookmarksAccount.amountPaidInMonthly}" />
	 	 </div>

		  <div class="column w-33-percent">
	    	<form:label for="customerType" path="customerType" cssErrorClass="error">Customer Type</form:label><br/>
				<form:select path="customerType">
  				<form:options items="${customerTypeList}" itemLabel="displayName"/>
				</form:select>
		  </div>

	</div>

	<div class="row">
		  <div class="column w-33-percent">
                                        <form:label for="bookmarksDiscount" path="bookmarksDiscount" cssErrorClass="error">Bookmarks Discount</form:label><br/>
                                        <form:input path="bookmarksDiscount" /> <form:errors path="bookmarksDiscount" />
		  </div>
		  <div class="column w-33-percent">
                                        <form:label for="nonBookmarksDiscount" path="nonBookmarksDiscount" cssErrorClass="error">Non Bookmarks Discount</form:label><br/>
                                        <form:input path="nonBookmarksDiscount" /> <form:errors path="nonBookmarksDiscount" />
		  </div>
	</div>

      <div class="row">
          <div class="column w-100-percent">
 				<input type="submit" class="btn btn-primary" value="Save Changes" id="editCustomerSubmitButton"></input>
				<a href="${pageContext.request.contextPath}/customerOrder/init?customerId=${customer.id}" class="btn btn-danger">Create New Customer Order</a>
				<a class="btn btn-default" href="${pageContext.request.contextPath}/customerOrderLine/search?customerOrderLine.customer.lastName=${customer.lastName}&customerOrderLine.customer.firstName=${customer.firstName}">Orders</a>
				<a class="btn btn-default" href="${pageContext.request.contextPath}/customerReport/report?customer.id=${customer.id}&customerReportType=INVOICE">Statement</a>
				<a class="btn btn-default" href="${pageContext.request.contextPath}/invoice/init?customerId=${customer.id}">Create Invoice</a>
				<a class="btn btn-default" href="${pageContext.request.contextPath}/invoice/search?invoice.customer.lastName=${customer.lastName}&invoice.customer.firstName=${customer.firstName}">Invoices</a>

 				<c:if test="${flow == 'search' || flow == 'searchCustomers'}">
 					<a href="${pageContext.request.contextPath}/customer/searchFromSession" class="btn btn-danger">Back</a>
 				</c:if>
 				<c:if test="${flow == 'invoiceSearch' || flow == 'customerOrderSearch'}">
 					<a href="closeWindow" class="btn btn-danger">Close</a>
 				</c:if>
	 	 </div>
	</div>
</div>
		</form:form>
