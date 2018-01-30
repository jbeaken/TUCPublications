<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<script>
	$(function() {
						$( "#date" ).datepicker({
							changeMonth: true,
							changeYear: true,
							dateFormat: 'dd/mm/yy'
						});

					$("#customerAutoComplete").autocomplete( {
						source: "${pageContext.request.contextPath}/customer/autoCompleteSurname?accountHolders=true",
						minLength: 3,
						select: function( event, ui ) {
							$("#customerAutoComplete").val(ui.item.label);
							$("#customerId").val(ui.item.value);
							$("#searchCustomerSubmitButton").click();
							return false;
						},
						focus: function( event, ui ) {
							$("#customerAutoComplete").val(ui.item.label);
							return false;
						}
					});

		});

		function saveCreditNote() {
			var originalCustomerId = $('#originalCustomerId').val()
			var customerId = $('#customerId').val()

			if(originalCustomerId != customerId) {

				var c = confirm("Customer has changed, this will debit/credit accounts, are you sure?")

				if(c == false) return
			}

			document.forms[0].submit();
		}
	</script>


		<form:form modelAttribute="creditNote" action="edit" method="post">
			<form:hidden path="id"/>
				<form:hidden id="customerId" path="customer.id"/>
				<input type="hidden" id="originalCustomerId" value="${creditNote.customer.id}"/>

<div class="rows">

	<div class="rows">
					<div class="demo">
								<label>Change Account Holder</label>
								<input type="text" id="customerAutoComplete"/>
					</div>
		</div>

      <div class="row">
          <div class="column w-100-percent">
						<form:label for="text" path="note" cssErrorClass="error">Note (REMEMBER This will appear on customer statment)</form:label><br/>
						<form:textarea path="note" cols="50" rows="20" required="required"/> <form:errors path="note" />
	 	 </div>
	</div>
      <div class="row">
          <div class="column w-100-percent">
	           Date : ${creditNote.date}<br/>
						 Amount : ${creditNote.amount}<br/>
						 Match : ${creditNote.transactionDescription}<br/>
						 Ref : ${creditNote.transactionReference}<br/>
	 	 </div>
	</div>
      <div class="row">
		  <div class="column w-100-percent">
		  	<input type="button" onclick="javascript:saveCreditNote()" class="btn btn-primary" value="Save Credit Note"/>
		  </div>
	</div>
</div>
</form:form>
