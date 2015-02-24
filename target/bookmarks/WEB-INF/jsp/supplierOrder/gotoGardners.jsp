<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script type="text/javascript">

 $(function() {
  $('form#gardnersForm').submit();
});
</script>

<form action="http://gardners.com/gardners/StockSearch.aspx" id="gardnersForm">
	<input type="hidden" name="qu" value="${isbn}"/>
</form>