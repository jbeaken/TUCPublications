<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
	<script>
	$(function() {
		$( "#startDate" ).datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: 'dd-MM-yy'
		});
		$( "#endDate" ).datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: 'dd-MM-yy'
		});
	});
	</script>
<div class="rows">
   <div class="row">
        <div class="column w-100-percent">
			<label>No. ${index + 1} of ${size}</label>&nbsp;<a href="${pageContext.request.contextPath}/stock/edit?id=${stockItem.id}" target="_blank" class="reorder-review-title">${stockItem.title}</a>
		</div>
	</div>
</div>	
<form:form action="/process" modelAttribute="reorderReviewStockItemBean">
<div class="rows">
     <div class="row">
		  <div class="column w-33-percent">
		  	<label>ISBN</label><br/> 
		  	${stockItem.isbn}     
	  	  </div>
          <div class="column w-33-percent">
			<label>Author(s)</label><br/>        
			${stockItem.mainAuthor}
	  	 </div>		 
     	 <div class="column w-33-percent">
			<label>Publisher</label><br/>        
			${stockItem.publisher.name}
	  	 </div>	  	  
	</div>
     <div class="row">	  	  		
          <div class="column w-33-percent">
			<label>Category</label><br/> 
			
			<form:select id="categorySelect" path="stockItem.category.id">
	   			<form:option value="" label="All"/>
	   			<form:options items="${categoryList}" itemValue="id" itemLabel="name"/>
			</form:select>
			
	  	 </div>
          <div class="column w-33-percent">
			<label>Sell Price</label><br/>        
			&pound;${stockItem.sellPrice}
	  	 </div>
          <div class="column w-33-percent">
			<label>Publisher Price</label><br/>        
			&pound;${stockItem.publisherPrice}
	  	 </div>		 
	</div>
     <div class="row">	  	  		
          <div class="column w-33-percent">
			<label>Publisher Date</label><br/>        
			<fmt:formatDate value="${stockItem.publishedDate}" /> 
	  	 </div>	 
	</div>	
</div>

<jsp:include page="../reports/stockItemMonthlySaleReportFragment.jsp"/>
<form:hidden path="originalSupplierOrderAmount"/>
<form:hidden path="originalSupplier.id"/>
<form:hidden path="supplierOrderLine.id"/>
<form:hidden path="supplierOrderLine.type"/>
<form:hidden path="supplierOrderLine.supplierOrderLineStatus"/>
<form:hidden path="populated"/>
<form:hidden path="stockItem.id"/>
<form:hidden path="stockItem.title"/>
<form:hidden path="stockItem.imageURL"/>
<form:hidden path="supplierOrderLine.stockItem.id"/>
<div class="rows">
   <div class="row">
          <div class="column w-25-percent">
	             <form:label for="supplierOrderLine.amount" path="supplierOrderLine.amount" cssErrorClass="error">Amount</form:label><br/>
	             <form:input path="supplierOrderLine.amount" autofocus="autofocus" type="number" required="required"/> <form:errors path="supplierOrderLine.amount" />                  
	 	 </div>
          <div class="column w-25-percent">
	             <form:label for="stockItem.quantityOnOrder" path="stockItem.quantityOnOrder" cssErrorClass="error">Quantity On Order</form:label><br/>
	             <form:input path="stockItem.quantityOnOrder" id="focus"/> <form:errors path="stockItem.quantityOnOrder" />                  
	 	 </div>
          <div class="column w-25-percent">
				<form:label for="stockItem.quantityInStock" path="stockItem.quantityInStock" cssErrorClass="error">Quanity In Stock</form:label><br/>
	            <form:input path="stockItem.quantityInStock" /> <form:errors path="stockItem.quantityInStock" /> 			
	  	 </div>		 	 
          <div class="column w-25-percent">
          	<form:label	for="supplierOrderLine.supplier.id" path="supplierOrderLine.supplier.id">Supplier</form:label><br/>
				<form:select path="supplierOrderLine.supplier.id">
          			<form:options items="${supplierList}" itemValue="id" itemLabel="name"/>
          		</form:select>	
	  	 </div>		
	</div>	
   <div class="row">
          <div class="column w-25-percent">
	             <form:label for="stockItem.keepInStockLevel" path="stockItem.keepInStockLevel" cssErrorClass="error">Keep In Stock Level</form:label><br/>
	             <form:select path="stockItem.keepInStockLevel">
          			<form:options items="${levelList}" itemLabel="displayName"/>
          		</form:select>	                  
	 	 </div>
          <div class="column w-25-percent">
	             <form:label for="stockItem.quantityToKeepInStock" path="stockItem.quantityToKeepInStock" cssErrorClass="error">Quantity Keep In Stock</form:label><br/>
	             <form:input path="stockItem.quantityToKeepInStock" /> <form:errors path="stockItem.quantityToKeepInStock" />                  
	 	 </div>
          <div class="column w-25-percent">
          	<lable>Quantity For Marxism<lable><br/>
	            <form:input path="stockItem.quantityForMarxism" />
	  	 </div>		 	 
          <div class="column w-25-percent">
          	   <form:checkbox path="stockItem.putOnWebsite" />
               <label>Put Stock Item On Website</label>
               <br/>  
				<form:checkbox path="stockItem.putImageOnWebsite" />
               <label>Put Image On Website</label>
               <br/>  
	  	 </div>		
	</div>	
</div>
</form:form>
<br/><br/>
<div class="rows">
   <div class="row">
          <div class="column w-33-percent">
	  	 <c:if test="${index - 20 >= 0}"> 
			<button class="btn btn-warning" id="previous20Button" onclick="javascript:submitForm('${pageContext.request.contextPath}/reorderReview/process?index=${index}&flow=previous20')">Previous 20</button>
	  	</c:if>	          
          <c:if test="${index != 0}">
			<button class="btn btn-primary" id="previousButton" onclick="javascript:submitForm('${pageContext.request.contextPath}/reorderReview/process?index=${index}&flow=previous')">Previous</button>
		  </c:if>
          <c:if test="${index == 0}">
			<button class="btn btn-primary" id="previousButton"  accesskey="p" onclick="javascript:alert('You are at the first item!')">Previous</button>
		  </c:if>		  
	 	 </div>
	 	 
          <div class="column w-33-percent">
			<button class="btn btn-primary" id="summaryButton" onclick="javascript:submitForm('${pageContext.request.contextPath}/reorderReview/process?index=${index}&flow=summary')">Summary</button>
	  	 </div>		
	  	 
	  	  <div class="column w-33-percent">
	  	 <c:if test="${index < size - 1}"> 
			<button class="btn btn-primary" id="nextButton" accesskey="n" onclick="javascript:submitForm('${pageContext.request.contextPath}/reorderReview/process?index=${index}&flow=next')">Next</button>
			<button class="btn btn-primary" id="removeFromMarxismButton" onclick="javascript:submitForm('${pageContext.request.contextPath}/reorderReview/process?index=${index}&flow=removeFromMarxism')">Next &amp; Remove</button>
	  	</c:if>
	  	 <c:if test="${index < size - 20}"> 
			<button class="btn btn-warning" id="next20Button" onclick="javascript:submitForm('${pageContext.request.contextPath}/reorderReview/process?index=${index}&flow=next20')">Next 20</button>
	  	</c:if>	
	  	 <c:if test="${index < size - 100}"> 
			<button class="btn btn-danger" id="next100Button" onclick="javascript:submitForm('${pageContext.request.contextPath}/reorderReview/process?index=${index}&flow=next100')">Next 100</button>
	  	</c:if>		  		  		
	  	 <c:if test="${index == size - 1}">
			<button class="btn btn-warning" id="nextButton"  onclick="javascript:alert('You are at the last item!')">Next</button>
	  	</c:if>
	  	</div>
	</div>	
</div>
<br/>
<div class="rows">
   <div class="row">
        <div class="column w-100-percent">
			<c:if test="${stockItem.imageURL != null}">
				<img src="${stockItem.imageURL}"/> 
			</c:if>  				        
		</div>
	</div>	
</div>