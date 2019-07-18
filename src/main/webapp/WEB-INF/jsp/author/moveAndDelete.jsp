<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<script>
$(function() {
	
	$("#authorToDeleteAutoComplete").autocomplete( {
        source: "/author/autoCompleteName",
        minLength: 3,
        select: function( event, ui ) {
        		$("#authorToDeleteAutoComplete").value = ui.item.label;
				$("#authorToDeleteId").val(ui.item.value);
				$("#authorName").val(ui.item.label);
                return false;
        },
        focus: function( event, ui ) {
        		$("#authorToDeleteAutoComplete").val(ui.item.label);
                return false;
        }
	});	
	
	$("#authorToKeepAutoComplete").autocomplete( {
        source: "/author/autoCompleteName",
        minLength: 3,
        select: function( event, ui ) {
        		$("#authorToKeepAutoComplete").value = ui.item.label;
				$("#authorToKeepId").val(ui.item.value);
				$("#authorName").val(ui.item.label);
                return false;
        },
        focus: function( event, ui ) {
        		$("#authorToKeepAutoComplete").val(ui.item.label);
                return false;
        }
	});	
})
</script>
<form:form modelAttribute="author" action="moveAndDelete" method="post">
	<input type="hidden" name="authorToDeleteId" id ="authorToDeleteId" />
	<input type="hidden" name="authorToKeepId" id ="authorToKeepId" />

	 <div class="row">
        <div class="form-group">
		     <label>Author To Delete</label>
		     <input type="text" name="authorToDeleteName" id="authorToDeleteAutoComplete" value="${authorToDeleteId}" autofocus="autofocus" required="required" class="form-control"/>
  	    </div>         
	</div>
	 <div class="row">
	          	<div class="form-group">
				     <label>Author To Keep (and move to)</label>
				     <input type="text" name="authorToKeepAutoComplete" id="authorToKeepAutoComplete" required="required" class="form-control"/>
		  	    </div>         
	</div>
	         
		<input type="submit" class="btn btn-primary" value="Add" id="addButton"/> 
	
</form:form>