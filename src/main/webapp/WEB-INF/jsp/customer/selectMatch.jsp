<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<script>
	$(function() {
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
		$('#customerId').val(''); //clear customer id
		$('#customerAutoComplete').focus();
	});

	function match( priority, transactionDescription ) {
		var customerId = $('#customerId').val();
		//alert( customerId + " " + transactionDescription);
		window.location.href = "/bookmarks/customer/match?priority=" + priority + "&customerId=" + customerId + "&transactionDescription=" + encodeURI( transactionDescription );
	}

</script>
<h1>Select Match</h1>

    <div class="rows">
    		Primary Match : ${customer.bookmarksAccount.tsbMatch}
    		<br/>		
    		Secondary Match : ${customer.bookmarksAccount.tsbMatchSecondary}
    		<br/>
    		<a href="/bookmarks/customer/selectMatch?priority=1&customerId=${customer.id}&transactionDescription=${transactionDescription}" class="btn btn-danger">Save To Primary</a>
    		<br/>
    		<a href="/bookmarks/customer/selectMatch?priority=2&customerId=${customer.id}&transactionDescription=${transactionDescription}" class="btn btn-danger">Save To Secondary</a>
      </div>

        
