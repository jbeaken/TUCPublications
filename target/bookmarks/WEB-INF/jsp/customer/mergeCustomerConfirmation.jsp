<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<div class="rows">
	<div class="row">
          <div class="column w-50-percent">
						<label>Customer To Keep</label><br/>
						${customerToKeep.id}<br/>
						${customerToKeep.fullName}<br/>
						${customerToKeep.contactDetails.email}<br/>
						${customerToKeep.fullAddress}
					</div>
					<div class="column w-50-percent">
						<label>Customer To Discard</label><br/>
						${customerToDiscard.id}<br/>
						${customerToDiscard.fullName}<br/>
						${customerToDiscard.contactDetails.email}<br/>
						${customerToDiscard.fullAddress}
      		</div>

	</div>

      <div class="row">
 				<a href="/customer/mergeConfirmed?customerToKeepId=${customerToKeep.id}&customerToDiscardId=${customerToDiscard.id}" class="btn btn-primary" value="Merge" id="mergeCustomerSubmitButton">Commit Merge</a>
	 	 </div>

	</div>
</div>
