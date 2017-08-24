<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<script>
	$(function() {

	});

	function match( priority, transactionDescription ) {
		var customerId = $('#customerId').val();
		//alert( customerId + " " + transactionDescription);
		window.location.href = "/bookmarks/tsb/match?priority=" + priority + "&customerId=" + customerId + "&transactionDescription=" + encodeURI( transactionDescription );
	}

</script>
<h1>Select Match for &quot;${transactionDescription}&quot;</h1>

<div class="alert alert-info">
	<button type="button" class="close" data-dismiss="alert">x</button>

		<div>Because TSB sometimes uses difference references for the same person, account holders have ability to store two references that can be
			used to match. Select below whether to store in primary or secondary. Please try to store in empty section if possible.
</div>

    <div class="rows">
    		Primary Match : ${customer.bookmarksAccount.tsbMatch}
    		<br/>
    		Secondary Match : ${customer.bookmarksAccount.tsbMatchSecondary}
    		<br/>
    		<a href="/bookmarks/tsb/selectMatch?priority=1&customerId=${customer.id}&transactionDescription=${transactionDescription}" class="btn btn-danger">Save To Primary</a>
    		<br/>
    		<a href="/bookmarks/tsb/selectMatch?priority=2&customerId=${customer.id}&transactionDescription=${transactionDescription}" class="btn btn-danger">Save To Secondary</a>
      </div>
