<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<script>
	$(function() {});
</script>
<h1>Summary of Uploaded Accounts</h1>

<div class="alert alert-info">
	<button type="button" class="close" data-dismiss="alert">�</button>
	<h2>Success!</h2>
	Have credited the following accounts : <br />
</div>

<display:table name="creditNoteList">
	id="searchTable">
	<display:column property="date" title="Date" />
	<display:column property="status" title="Status" />
	<display:column property="amount" title="Amount" />
	<display:column property="transactionDescription" title="Description" />
	<display:column property="transactionReference" title="Reference" />
	<display:column property="customer.fullName" title="Customer" />
</display:table>
