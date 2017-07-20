<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
	<script>
	$(function() {
		$( "#startDate" ).datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: 'dd-MM-yy'
		});
$( "#endDate" ).datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: 'dd-MM-yy'
		});

	});
	</script>
<form:form modelAttribute="stockItem" action="process" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
			<form:label for="websiteInfo.titleOnWebsite" path="websiteInfo.titleOnWebsite" cssErrorClass="error">Title On Website</form:label><br/>
			<form:input path="websiteInfo.titleOnWebsite" /> <form:errors path="websiteInfo.titleOnWebsite" />			
				
	  	 </div>
          <div class="column w-33-percent">
			<form:label for="websiteInfo.authorOnWebsite" path="websiteInfo.authorOnWebsite" cssErrorClass="error">Author On Website</form:label><br/>
			<form:input path="websiteInfo.authorOnWebsite" /> <form:errors path="websiteInfo.authorOnWebsite" />			
	  	 </div>
          <div class="column w-33-percent">
			<form:label for="websiteInfo.bookOfTheMonth" path="websiteInfo.bookOfTheMonth" cssErrorClass="error">Book of the Month</form:label><br/>
			<form:checkbox path="websiteInfo.bookOfTheMonth" /> <form:errors path="websiteInfo.bookOfTheMonth" />			
	  	 </div>
      </div>
      <div class="row">
          <div class="column w-33-percent">
			<form:label for="websiteInfo.frontPageIndex" path="websiteInfo.frontPageIndex" cssErrorClass="error">Front Page Index</form:label><br/>
			<form:input path="websiteInfo.frontPageIndex" /> <form:errors path="websiteInfo.frontPageIndex" />			
	  	 </div>
	</div>		
      <div class="row">
          <div class="column w-70-percent">
 				<input type="submit" class="btn btn-primary" value="Upload To Website"/> 
	 	 </div>
	</div>		
</div>		
</form:form>