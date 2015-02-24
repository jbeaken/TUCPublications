<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<script>
$(function() {
	$('#resetButton').click(function() {
		$('#stockItemSearchForm input').val('');
		$('#categorySelect').val('');
		$('#availabilitySelect').val('');
		$('#stockItemTypeSelect').val('');
		$('#publisherId').val('');
		$('#bindingSelect').val('');
		$('#focus').focus();
	});
	
	$("#authorAutoComplete").autocomplete( {
        source: "/author/autoCompleteName",
        minLength: 3,
        select: function( event, ui ) {
        		$("#authorAutoComplete").value = ui.item.label;
				$("#authorId").val(ui.item.value);
				$("#authorName").val(ui.item.label);
                return false;
        },
        focus: function( event, ui ) {
        		$("#authorAutoComplete").val(ui.item.label);
                return false;
        }
	});	
	
	$("#publisherAutoComplete").autocomplete( {
			source: "${pageContext.request.contextPath}/publisher/autoCompletePublisherName",
			minLength: 3,
			select: function( event, ui ) {
				$("#publisherAutoComplete").value = ui.item.label;
				$("#publisherId").val(ui.item.value);
				return false;
			},
			focus: function( event, ui ) {
				$("#publisherAutoComplete").val(ui.item.label);
				return false;
			}	
	});
	$("#supplierAutoComplete").autocomplete( {
		source: "${pageContext.request.contextPath}/supplier/autoCompleteSupplierName",
		minLength: 3,
		select: function( event, ui ) {
			$("#supplierAutoComplete").value = ui.item.label;
			$("#supplierId").val(ui.item.value);
			return false;
		},
		focus: function( event, ui ) {
			$("#supplierAutoComplete").val(ui.item.label);
			return false;
		}
	});
	$(function(){
		$('#stockItemSearchForm input').keyup(function(event) {
			if(event.which == 13) {
				document.forms[0].submit();
			}
		});
	});
	$('#publisherId').val('${stockItemSearchBean.stockItem.publisher.id}');
	$('#publisherAutoComplete').val('${stockItemSearchBean.stockItem.publisher.name}');
	$('#supplierId').val('${stockItemSearchBean.stockItem.publisher.supplier.id}');
	$('#supplierAutoComplete').val('${stockItemSearchBean.stockItem.publisher.supplier.name}');	
});
</script>
<form:hidden path="stockItem.publisher.supplier.id" id="supplierId"/>
<form:hidden path="stockItem.publisher.id" id="publisherId"/>
<form:hidden path="authorId" id="authorId"/>
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
			<form:label	for="stockItem.isbn" path="stockItem.isbn" cssErrorClass="error">ISBN</form:label><br/> 
			<form:input path="stockItem.isbn" id="focus" /> <form:errors path="stockItem.isbn" />			
	  	 </div>
          <div class="column w-33-percent">
			<form:label for="authors" path="authors" cssErrorClass="error">Author</form:label><br/>
			<form:input path="authorName" id="authorAutoComplete"/> 
	  	 </div>
          <div class="column w-33-percent">
			<form:label for="stockItem.title" path="stockItem.title" cssErrorClass="error">Title</form:label><br/>
			<form:input path="stockItem.title" /> 
	  	 </div>		 
	  	</div>
        <div class="row">
          <div class="column w-33-percent">
			<form:label for="stockItem.category.id" path="stockItem.category.id" cssErrorClass="error">Category</form:label><br/>
			<form:select id="categorySelect" path="stockItem.category.id">
	   			<form:option value="" label="All"/>
	   			<form:options items="${categoryList}" itemValue="id" itemLabel="name"/>
			</form:select>
	  	 </div>		
          <div class="column w-33-percent">
				<form:label for="stockItem.availability" path="stockItem.availability" cssErrorClass="error">Availability</form:label><br/>
				<form:select id="availabilitySelect" path="stockItem.availability">
          		<form:option value="" label="All"/>
         		<form:options items="${availablityList}" itemLabel="displayName"/></form:select>					
	  	 </div>
          <div class="column w-33-percent">
			<form:label for="stockItem.publisher.id" path="stockItem.publisher.id" cssErrorClass="error">Publisher</form:label><br/>
			<input type="text" name="stockItem.publisher.name" id="publisherAutoComplete"/>	
	  	 </div>
	  	</div><!--  End Row -->
        <div class="row">
          <div class="column w-33-percent">
						<label for="stockItem.binding">Binding</label><br/>
						<form:select id="bindingSelect" path="stockItem.binding" onkeypress="javascript:submitForm('${pageContext.request.contextPath}/stock/search')">
	            			<form:option value="" label="All"/>
            			<form:options items="${bindingList}" itemLabel="displayName"/></form:select>				
	  	 </div>
		  <div class="column w-33-percent">
					<form:label for="stockItem.type" path="stockItem.type" cssErrorClass="error">Type</form:label><br/>
						<form:select id="stockItemTypeSelect" path="stockItem.type" onkeypress="javascript:submitForm('${pageContext.request.contextPath}/stock/search')">
	            			<form:option value="" label="All"/>
            			<form:options items="${stockItemTypeList}" itemLabel="displayName"/></form:select>				
		  </div>	
          <div class="column w-33-percent">
			<form:label for="stockItem.publisher.supplier.id" path="stockItem.publisher.supplier.id" cssErrorClass="error">Supplier</form:label><br/>
			<input type="text" name="stockItem.publisher.supplier.name" id="supplierAutoComplete"/>	
	  	 </div>		
		</div><!--  End Row -->
        <div class="row">	
          	<div class="column w-33-percent">
					 <form:label for="stockItem.putOnWebsite" path="stockItem.putOnWebsite" cssErrorClass="error">Is On Website</form:label><br/>
					<form:select id="stockItem.putOnWebsite" path="stockItem.putOnWebsite">
	            			<form:option value="" label="All"/>
	            			<form:option value="true" label="Is On Website"/>
	            			<form:option value="false" label="Not On Website"/>
            		</form:select>						 			          
		  </div>        	  
		  <div class="column w-33-percent">
					<form:label for="stockLevel" path="stockLevel" cssErrorClass="error">Stock Level</form:label><br/>
						<form:select id="stockLevelSelect" path="stockLevel" onkeypress="javascript:submitForm('${pageContext.request.contextPath}/stock/search')">
	            			<form:option value="" label="All"/>
            			<form:options items="${stockLevelList}" itemLabel="displayName"/></form:select>				
		  </div>
		  <div class="column w-33-percent">
					<form:label for="keepInStockLevel" path="keepInStockLevel" cssErrorClass="error">Keep In Stock Priority</form:label><br/>
						<form:select id="keepInStockLevelSelect" path="keepInStockLevel">
	            			<form:option value="" label="All"/>
            			<form:options items="${levelList}" itemLabel="displayName"/></form:select>				
		  </div>		  		  
	 </div><!--  End Row -->
	 
     <div class="row">	
          	<div class="column w-33-percent">
					 <label>Marxism</label><br/>
					<form:select path="marxismStatus">
	            			<form:option value="" label="All"/>
	            			<form:option value="1" label="For Marxism"/>
	            			<form:option value="2" label="Not In Stock"/>
	            			<form:option value="3" label="Not For Marxism"/>
	            			<form:option value="4" label="For Marxism and Undecided"/>
	            			<form:option value="5" label="Undecided"/>
	            			<form:option value="6" label="For Marxism 5 or more"/>
            		</form:select>						 			          
		  </div>         	  
           	<div class="column w-33-percent">
			 		 <form:checkbox path="stockItem.isStaffPick" />                         
					 <form:label for="stockItem.isStaffPick" path="stockItem.isStaffPick" cssErrorClass="error">Staff Pick</form:label>
					 <br/>
				 	 <form:checkbox path="keepInStock" />                         
					 <form:label for="keepInStock" path="keepInStock" cssErrorClass="error">Keep In Stock</form:label>
					 <br/>
				 	 <form:checkbox path="alwaysInStock" />                         
					 <form:label for="alwaysInStock" path="alwaysInStock">Always In Stock</form:label>
					 <br/>
				 	 <form:checkbox path="hideBookmarks" />                         
					 <form:label for="hideBookmarks" path="hideBookmarks">Hide Bookmark's Titles</form:label>
		  </div> 	  		  
	 </div><!--  End Row -->	 
</div>