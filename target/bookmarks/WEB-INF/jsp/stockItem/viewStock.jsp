<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<div class="rows">
        <div class="row">
          <div class="column w-100-percent">
			<label>Title</label><br/>        
			${stockItem.title}
	  	 </div>
	  	</div>
		<div class="row">	
		  <div class="column w-33-percent">
		          <label>ISBN</label><br/>
		          ${stockItem.isbn}
	  	</div>		
          <div class="column w-33-percent">
                   <label>Authors</label><br/>
                   ${stockItem.mainAuthor}
	 	 </div>		  	
          <div class="column w-33-percent">
                   <label>Publisher</label><br/>
                   ${stockItem.publisher.name}   
	 	 </div>
      </div>
      <div class="row">
		  <div class="column w-33-percent">
                 <label>Publisher Date</label><br/>
                 ${stockItem.publishedDate}
		  </div>
          <div class="column w-33-percent">
                   <label>Category</label><br/>
                   ${stockItem.category.name}   
	 	 </div>
		  <div class="column w-33-percent">
                   <label>Binding</label><br/>
                   ${stockItem.binding.displayName}   
		  </div>
	</div>		
      <div class="row">
		  <div class="column w-33-percent">
					<label>Type</label><br/>
                   ${stockItem.type.displayName}  										
		  </div>	  
          <div class="column w-33-percent">
					<label>Discount</label><br/>
                   ${stockItem.discount}% 
	 	 </div>	
          <div class="column w-33-percent">
				<label>Margin</label><br/>
                   ${stockItem.margin}% 
	 	 </div>			 

	</div>	
      <div class="row">
          <div class="column w-33-percent">
					<label>Publisher price</label><br/>
                   &pound;${stockItem.publisherPrice} 
	 	 </div>
		  <div class="column w-33-percent">
					<label>Sell price</label><br/>
                   &pound;${stockItem.sellPrice} 
		  </div>	  
          <div class="column w-33-percent">
					<label>Cost price</label><br/>
                   &pound;${stockItem.costPrice}  
	 	 </div>	  
	</div>				
      <div class="row">
		  <div class="column w-33-percent">
					<label>Quantity In Stock</label><br/>
                   ${stockItem.quantityToKeepInStock}  		  
		  </div>
		  <div class="column w-33-percent">
					<label>Quantity On Loan</label><br/>
                   ${stockItem.quantityOnLoan}  		  
		  </div>
		  <div class="column w-33-percent">
					<label>Quantity For Customer Order</label><br/>
                   ${stockItem.quantityForCustomerOrder}  		  
		  </div>
	</div>				
      <div class="row">		  
		  <div class="column w-33-percent">
					<label>Quantity Ready For Customer Order</label><br/>
                   ${stockItem.quantityReadyForCustomer}  		  
		  </div>
		  <div class="column w-33-percent">
					<label>Staff Pick</label><br/>
                   	<c:if test="${stockItem.isStaffPick == true}">
						<img src="<c:url value="/resources/images/yes.png" />" />
					</c:if>
					<c:if test="${stockItem.isStaffPick == false}">
						<img src="<c:url value="/resources/images/no.png" />" />
					</c:if>
		 </div>
		  <div class="column w-33-percent">
					<label>In Brochure</label><br/>
					<c:if test="${stockItem.isInBrochure == true}">
						<img src="<c:url value="/resources/images/yes.png" />" />
					</c:if>
					<c:if test="${stockItem.isInBrochure == false}">
						<img src="<c:url value="/resources/images/no.png" />" />
					</c:if>
	 	 </div>
	</div>				
      <div class="row">		 
		  <div class="column w-33-percent">
					<label>Availablity</label><br/>
                   ${stockItem.availability.displayName}  										
		  </div>		  
	</div>					
	<div class="row">
		  <div class="column w-100-percent">
							<c:if test="${flow == 'search'}">
								<a href="/stock/searchFromSession">
									<button type="button" class="btn btn-primary">Back To Search</button>
								</a>
							</c:if>
		  </div>
	</div>
	<c:if test="${stockItem.websiteInfo.review.review1 != null}">
		<div class="row">
			  <div class="column w-100-percent">
											<label>Review</label><br/>
											${stockItem.websiteInfo.review.review1}
			  </div>
		</div>
	</c:if>
	<c:if test="${stockItem.imageURL != null}">
	<br/><br/>
               		<img src="${stockItem.imageURL}"/> 
	</c:if>
</div>


