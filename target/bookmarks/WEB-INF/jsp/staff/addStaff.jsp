<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	<script>
	$(function() {

	});
	</script>
<spring:hasBindErrors name="staff">
    <h2>Errors</h2>
    <div class="formerror">
        <ul>
            <c:forEach var="error" items="${errors.allErrors}" varStatus="index">
            	<li>${error.defaultMessage}</li>
			</c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<form:form modelAttribute="staff" action="add" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-50-percent">
							<form:label for="name" path="name" cssErrorClass="error">Name</form:label><br/>
							<form:input path="name" autofocus="autofocus" required="required" /> <form:errors path="name" />
  	 			</div>
          <div class="column w-50-percent">
						Telephone<br/>
						<form:input path="telephone" required="required" /> <form:errors path="telephone" />
	  	 </div>
      </div>
      <div class="row">
    		  <div class="column w-50-percent">
                  Email<br/>
                  <form:input path="email" required="required"/> <form:errors path="email" />
    		  </div>
          <div class="column w-50-percent">
                  Slack Handle<br/>
                  <form:input path="slackHandle" required="required"/> <form:errors path="slackHandle" />
          </div>          
	     </div>

      <div class="row" style="padding-top : 10px;">
          <div class="column w-70-percent">
 				<input type="submit" class="btn btn-primary" id="addStaffButton" value="Save"/>
	 	 </div>
	</div>

	</div>
</div>
</form:form>
