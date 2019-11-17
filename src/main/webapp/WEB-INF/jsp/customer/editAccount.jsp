<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<fmt:setLocale value="en_GB"/>


		<form:form modelAttribute="customer" action="editAccount?flow=${flow}" method="post">
		  	<form:hidden path="id"/>
		  	<form:hidden path="bookmarksAccount.currentBalance"/>
				<form:hidden path="bookmarksAccount.lastPaymentDate"/>
				<form:hidden path="bookmarksAccount.firstPaymentDate"/>
				<form:hidden path="bookmarksAccount.amountPaidInMonthly"/>
		  	<form:hidden path="bookmarksAccount.comment"/>




<div class="rows">

  <div class="row">

		<div class="column w-33-percent">
			<label>Current Balance</label>
			&pound;<fmt:formatNumber minFractionDigits="2" maxFractionDigits="2" value="${customer.bookmarksAccount.currentBalance}" />
		</div>

		<div class="column w-33-percent">
        	<label>Amount Paid In Monthly</label>
        	<div>
						&pound;<fmt:formatNumber minFractionDigits="2" maxFractionDigits="2" value="${customer.bookmarksAccount.amountPaidInMonthly}" />
					</div>
		</div>

	</div>

	<div class="row">

		<div class="column w-33-percent">
			<form:label for="bookmarksAccount.tsbMatch" path="bookmarksAccount.tsbMatch">Primary Match</form:label><br/>
			<form:input path="bookmarksAccount.tsbMatch" /> <form:errors path="bookmarksAccount.tsbMatch" />
		</div>

		<div class="column w-33-percent">
			<form:label for="bookmarksAccount.tsbMatchSecondary" path="bookmarksAccount.tsbMatchSecondary">Secondary Match</form:label><br/>
			<form:input path="bookmarksAccount.tsbMatchSecondary" /> <form:errors path="bookmarksAccount.tsbMatchSecondary" />
		</div>

	</div>

  <div class="row">
		  <div class="column w-100-percent">
		  		&nbsp;<br/>
                 <form:checkbox path="bookmarksAccount.accountHolder" />
				 <form:label id="checkboxLabel" for="bookmarksAccount.accountHolder" path="bookmarksAccount.accountHolder" cssErrorClass="error">Account</form:label>
                 <form:checkbox path="bookmarksAccount.sponsor" />
				 <form:label for="bookmarksAccount.sponsor" path="bookmarksAccount.sponsor" cssErrorClass="error">Sponsor</form:label>
                 <form:checkbox path="bookmarksAccount.paysInMonthly" />
				 <form:label for="bookmarksAccount.paysInMonthly" path="bookmarksAccount.paysInMonthly" cssErrorClass="error">Pays In Monthly</form:label>

		  </div>
	</div>
      <div class="row">
          <div class="column w-100-percent">

 				<input type="submit" class="btn btn-primary" value="Save Changes" id="editCustomerSubmitButton"></input>

				<a class="btn btn-default" href="${pageContext.request.contextPath}/creditNote/add?id=${customer.id}">Add Credit note</a>

	 	 </div>
	</div>
</div>
		</form:form>
