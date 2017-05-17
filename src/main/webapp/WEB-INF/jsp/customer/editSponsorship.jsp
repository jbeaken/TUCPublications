<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<fmt:setLocale value="en_GB"/>


		<form:form modelAttribute="customer" action="editSponsorship?flow=${flow}" method="post">
		  	<form:hidden path="id"/>

<div class="rows">

  <div class="row">

		<div class="column w-33-percent">
			<label>Sponsorship Type</label><br/>
			<form:select path="sponsorshipDetails.type">
				<form:option value="" label="-select-" />
							<form:options items="${sponsorshipTypeList}" itemLabel="displayName"/>
			</form:select>
		</div>

		<div class="column w-33-percent">
        	<form:label for="sponsorshipDetails.startDate" path="sponsorshipDetails.startDate" cssErrorClass="error">Start Date</form:label><br/>
        	<form:input path="sponsorshipDetails.startDate" /> <form:errors path="sponsorshipDetails.startDate" />
		</div>

	</div>

	<div class="row">

		<div class="column w-33-percent">
			<form:label for="sponsorshipDetails.comment" path="sponsorshipDetails.comment">Comments</form:label>
			<br/>
			<textarea name="sponsorshipDetails.comment" rows="10" cols="80">
				${customer.sponsorshipDetails.comment}
				<textarea>
		</div>

	</div>


      <div class="row">
          <div class="column w-100-percent">

 				<input type="submit" class="btn btn-primary" value="Save Changes" id="editCustomerSubmitButton"></input>

	 	 </div>
	</div>
</div>
		</form:form>
