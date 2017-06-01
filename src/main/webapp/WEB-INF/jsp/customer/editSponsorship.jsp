<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<fmt:setLocale value="en_GB"/>
<script>
$(function() {
	$( "#startDate" ).datepicker({
		changeMonth: true,
		changeYear: true,
		dateFormat: 'dd/mm/yy'
	});
});
</script>

		<form:form modelAttribute="customer" action="editSponsorship?flow=${flow}" method="post">
		  	<form:hidden path="id"/>

<div class="rows">

  <div class="row">

		<div class="column w-33-percent">
			<label>Sponsorship Amount</label><br/>
			<form:input path="sponsorshipDetails.amount" /> <form:errors path="sponsorshipDetails.amount" />
		</div>

		<div class="column w-33-percent">
        	<form:label for="sponsorshipDetails.startDate" path="sponsorshipDetails.startDate" cssErrorClass="error">Start Date</form:label><br/>
        	<form:input path="sponsorshipDetails.startDate" id="startDate" /> <form:errors path="sponsorshipDetails.startDate" />
		</div>

		<div class="column w-33-percent">
        	<form:label for="sponsorshipDetails.endDate" path="sponsorshipDetails.endDate" cssErrorClass="error">End Date</form:label><br/>
        	<form:input path="sponsorshipDetails.endDate" id="endDate" /> <form:errors path="sponsorshipDetails.endDate" />
		</div>		

	</div>

	<div class="row">

		<div class="column w-33-percent">
			<form:label for="sponsorshipDetails.comment" path="sponsorshipDetails.comment">Comments</form:label>
			<br/>
			<textarea name="sponsorshipDetails.comment" rows="10" cols="80">${customer.sponsorshipDetails.comment}</textarea>
		</div>

	</div>


      <div class="row">
          <div class="column w-100-percent">

 				<input type="submit" class="btn btn-primary" value="Save Changes"></input>

	 	 </div>
	</div>
</div>
		</form:form>
