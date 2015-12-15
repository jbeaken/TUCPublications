<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
		<form:form modelAttribute="customerMergeFormObject" action="merge" method="post">
 <form:errors path="*" cssClass="error" />
<div class="rows">
      <div class="row">
          <div class="column w-33-percent">
						<label>Customer To Keep</label><br/>
						<form:input path="customerToKeep.id" />
					</div>
					<div class="column w-33-percent">
						<label>Customer To Discard</label><br/>
						<form:input path="customerToDiscard.id" />
      </div>

      <div class="row">
				<div class="column w-100-percent">
 				<input type="submit" class="btn btn-primary" value="Merge" id="mergeCustomerSubmitButton"></input>
	 	 </div>

	</div>
</div>

</form:form>
