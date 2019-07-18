<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
    <script src="${pageContext.request.contextPath}/resources/ckeditor/ckeditor.js"></script>
	<script>
	$(function() {
		$( "#publishedDate" ).datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: 'dd-mm-yy'
		});
		$("#authorAutoComplete").autocomplete( {
             source: "/author/autoCompleteName",
             minLength: 3,
             select: function( event, ui ) {
                     $('#authorTable').load("/author/addAuthorToStock?authorId=" + ui.item.value);
                     return false;
             },
             focus: function( event, ui ) {
                     return false;
             }
     	});
		$('#authorTable').load("/author/loadAuthorTable");
	});

	function getImage() {
		submitForm('${pageContext.request.contextPath}/stock/getImage?flow=editStock');
	}
	function getReview() {
		submitForm('${pageContext.request.contextPath}/stock/getReview?flow=editStock');
	}
	</script>
		<form:form modelAttribute="stockItem" action="edit" method="post">
		  	<form:hidden path="id"/>
			<form:hidden path="note"/>
		  	<form:hidden path="creationDate"/>

		  	<form:hidden path="imageURL"/>
		  	<form:hidden path="imageFilename"/>

		  	<form:hidden path="syncedWithAZ"/>

		  	<form:hidden path="isOnAZ"/>
		  	<form:hidden path="isImageOnAZ"/>
		  	<form:hidden path="isReviewOnAZ"/>

		  	<form:hidden path="reviewAsText"/>


		  	<form:hidden path="bouncyIndex"/>
		  	<form:hidden path="stickyTypeIndex"/>
		  	<form:hidden path="stickyCategoryIndex"/>

		  	<form:hidden path="hasNewerEdition"/>

		  	<form:hidden path="gardnersStockLevel"/>
		  	<form:hidden path="isAvailableAtSuppliers"/>

		  	<form:hidden path="priceThirdPartySecondHand"/>
		  	<form:hidden path="priceThirdPartyCollectable"/>
		  	<form:hidden path="priceThirdPartyNew"/>
		  	<form:hidden path="priceAtAZ"/>

		  	<form:hidden path="originalTitle"/>
		  	<form:hidden path="lastReorderReviewDate"/>
		  	<form:hidden path="quantityInStockForMarxism2012"/>
		  	<form:hidden path="marxism2012SaleAmount"/>
		  	<form:hidden path="twentyTwelveSales"/>
		  	<form:hidden path="twentyThirteenSales"/>

<spring:hasBindErrors name="stockItem">
    <h2>Errors</h2>
    <div class="formerror">
        <ul>
            <c:forEach var="error" items="${errors.allErrors}" varStatus="index">
            	<li>${error.defaultMessage}</li>
			</c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<div class="rows">
        <div class="row">
          <div class="column w-70-percent">
			<label>Title</label><br/>
			<form:input path="title" size="100"/>
	  	 </div>
	  	</div>
		<div class="row">
		  <div class="column w-33-percent">
		          <label>ISBN</label><br/>
                   <form:input path="isbn" />
	  	</div>
          <div class="column w-66-percent">
               <label for="publisher.id" path="publisher.id">Publisher</label><br/>
               <form:select path="publisher.id">
			  				<form:option value="" label="Please select"/>
			  				<form:options items="${publisherList}" itemValue="id" itemLabel="name"/>
				</form:select>
				<a href="${pageContext.request.contextPath}/publisher/add?flow=editStock">Add</a>

	 	 </div>
      </div>
      <div class="row">
		  <div class="column w-33-percent">
                 <label>Publisher Date</label><br/>
                 <form:input path="publishedDate" />
		  </div>
          <div class="column w-33-percent">
               <label for="category.id" path="category.id">Category</label><br/>
									<form:select path="category.id" onkeypress="javascript:submitForm('${pageContext.request.contextPath}/stock/edit')">
				            			<form:option value="" label="Please select"/>
				            			<form:options items="${categoryList}" itemValue="id" itemLabel="name"/>
				        			</form:select>
	 	 </div>
		  <div class="column w-33-percent">
                                    <label>Binding</label><br/>
                                        <form:select path="binding">
										<form:option value="" label="Please select"/>
										<form:options items="${bindingOptions}" itemLabel="displayName"/>
									</form:select>
		  </div>
	</div>
      <jsp:include page="pricesFragment.jsp"></jsp:include>

      <div class="row">
		  <div class="column w-33-percent">
               <label>Quantity In Stock</label><br/>
					<form:input path="quantityInStock" />
		  </div>
		  <div class="column w-33-percent">
               <label>Quantity On Loan</label><br/>
					<form:input path="quantityOnLoan" />
		  </div>
		  <div class="column w-33-percent">
               <label>Quantity Ready For Customer</label><br/>
					<form:input path="quantityReadyForCustomer" />
		  </div>
	</div>
	<div class="row">
		  <div class="column w-33-percent">
               <label>Quantity For Customer Order</label><br/>
					<form:input path="quantityForCustomerOrder" />
		  </div>
		  <div class="column w-33-percent">
               <label>Quantity On Order</label><br/>
					<form:input path="quantityOnOrder" />
		  </div>
		  <div class="column w-33-percent">
			       <label for="preferredSupplier.id" path="preferredSupplier.id">Pref Supplier</label><br/>
			       <form:select path="preferredSupplier.id">
			  				<form:option value="" label="Please select"/>
			  				<form:options items="${supplierList}" itemValue="id" itemLabel="name"/>
				</form:select>
	 	 </div>
	</div>
	<div class="row">
		  <div class="column w-33-percent">
               <label>Keep In Stock</label><br/>
					<form:input path="quantityToKeepInStock" />
		  </div>
		  <div class="column w-33-percent">
                                    <label>Keep In Stock level</label><br/>
                                        <form:select path="keepInStockLevel">
										<form:option value="" label="Please select"/>
										<form:options items="${levelList}" itemLabel="displayName"/>
									</form:select>
		  </div>
		  <div class="column w-33-percent">
                                    <label>Availablity</label><br/>
                                        <form:select path="availability">
										<form:option value="" label="Please select"/>
										<form:options items="${availabilityOptions}" itemLabel="displayName"/>
									</form:select>
		  </div>
	 </div>
	 <div class="row">
	 	<div class="column w-33-percent">
            <label>Type</label><br/>
                <form:select path="type">
										<form:option value="" label="Please select"/>
										<form:options items="${stockItemTypeOptions}" itemLabel="displayName"/>
									</form:select>
		  </div>
	<div class="column w-33-percent">
			   <form:checkbox path="putOnWebsite" />
               <label>Put Stock Item On Website</label>
               <br/>
				<form:checkbox path="putImageOnWebsite" />
               <label>Put Image On Website</label>
               <br/>
			   <form:checkbox path="putReviewOnWebsite" />
               <label>Put Review On Website</label>
               <br/>
			   <form:checkbox path="isStaffPick" />
               <label>Is Staff Pick</label>
               <br/>
			   <form:checkbox path="alwaysInStock" />
               <label>Is Always In Stock</label>
				<br/>
			    <form:checkbox path="isOnExtras" />
		           <label>Is On Extras Page</label>
	</div>

	<div class="column w-33-percent">
				<form:checkbox path="updateAvailablity" />
               <label>Update Availability</label>
               <br/>
				<form:checkbox path="updateSellPrice" />
               <label>Update Sell Price</label>
               <br/>
			   <form:checkbox path="updateReview" />
               <label>Update Review</label>
               <br/>
			   <form:checkbox path="updatePublisher" />
               <label>Update Publisher</label>
               <br/>
			   <form:checkbox path="updateAuthors" />
               <label>Update Authors</label>
               <br/>
			   <form:checkbox path="updateTitle" />
               <label>Update Title</label>
   </div>

	  		<div class="row">
          <div class="column w-33-percent">
            <label>Marxism Quantity</label><br/>
                <form:input path="quantityForMarxism" type="number" />
          </div>
          <div class="column w-33-percent">
            <label>Turnaround ebook url</label><br/>
                <form:input path="ebookTurnaroundUrl" type="text" />
          </div>
	</div>

	 </div>
	 <!-- Authors -->
     <div class="row">
		  <div class="column w-100-percent">
		  		Search Author <input type="text" id="authorAutoComplete"/><a onclick="javascript:addAuthor()">Add</a><br/>
		  		<div id="authorMessage"/>
		  		<div id="authorTable"/>
		  </div>
    </div>
      <div class="row">
          <div class="column w-100-percent">
               <button type="button" onclick="javascript:submitForm('${pageContext.request.contextPath}/stock/edit')" class="btn btn-danger">Save Changes</button>&nbsp;
				<button type="button" onclick="window.close();" class="btn btn-primary">Back to search</button>
				<button class="btn btn-success" onclick="javascript:getImage()">Get Image</button>
				<button class="btn btn-success" onclick="javascript:getReview()">Get Review</button>
				<button type="button" onclick="javascript:authoriseSuperUser('${pageContext.request.contextPath}/stock/delete')" class="btn btn-primary">Delete</button>
	 </div>
    </div>
     <div class="row">
          <div class="column w-30-percent">
				<c:if test="${stockItem.imageURL == null}">
					No image available, click Download Image to get one
				</c:if>
				<c:if test="${stockItem.imageURL != null}">
               		<img src="${stockItem.imageURL}"/>
				</c:if>
	 	</div>
          <div class="column w-70-percent">
			<textarea name="reviewAsHTML" id="editor1" rows="10" cols="80">
                ${stockItem.reviewAsHTML}
            </textarea>
 			<script>
                // Replace the <textarea id="editor1"> with a CKEditor
                // instance, using default configuration.
                CKEDITOR.replace( 'editor1' );
            </script>
	 	</div>
    </div>

     <div class="row">
		  <div class="column w-50-percent">
				<label>Image filename</label><br/>
				${stockItem.imageFilename}
		  </div>

    </div>
</div>
</form:form>
