<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
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

	function calculateDiscount() {
		var discount = document.getElementById('discount').value;
		var publisherPrice = document.getElementById('publisherPrice').value;
		if(isNaN(parseFloat(discount))) {
			alert("Invalid discount");
			return;
		}
		if(isNaN(parseFloat(publisherPrice))) {
			alert("Invalid publisher Price");
			return;
		}
		var costPrice = publisherPrice / 100 * (100 - discount);
		costPrice = Math.round(costPrice * Math.pow(10, 2)) / Math.pow(10, 2);
		document.getElementById('costPrice').value = costPrice;
		document.getElementById('sellPrice').value = publisherPrice;
	}


	function calculateMargin() {
		var margin = document.getElementById('margin').value;
		var costPrice = document.getElementById('costPrice').value;
		if(isNaN(parseFloat(margin))) {
			alert("Invalid margin");
			return;
		}
		if(isNaN(parseFloat(costPrice))) {
			alert("Invalid cost Price");
			return;
		}
		var sellPrice = costPrice / ((100 - margin) / 100);
		sellPrice = Math.round(sellPrice * Math.pow(10, 2)) / Math.pow(10, 2);
		document.getElementById('sellPrice').value = sellPrice;
	}

	function checkAZLookUp(e) {
		var keynum

		if(window.event) // IE
		{
		keynum = e.keyCode
		}
		else if(e.which) // Netscape/Firefox/Opera
		{
		keynum = e.which
		}
		if(keynum == 13) {
			submitForm('${pageContext.request.contextPath}/stock/azlookup');
		}
		//alert(keynum);
	}

	function lookUpFromAZUsingIsbn() {
		var isbn = $('input#isbn').val()
		console.log( "Looing up isbn " + isbn)
		window.location.href = "/stock/azlookupUsingIsbn?isbn=" + isbn
	}

	function lookUpFromAZ() {
		submitForm('${pageContext.request.contextPath}/stock/azlookup');
	}
	</script>
		<form:form modelAttribute="stockItem" action="${pageContext.request.contextPath}/stock/add" method="post">
		  	<form:hidden path="id"/>

		  	<form:hidden path="imageURL"/>
		  	<form:hidden path="imageFilename"/>
		  	<form:hidden path="syncedWithAZ"/>

		  	<form:hidden path="priceThirdPartySecondHand"/>
		  	<form:hidden path="priceThirdPartyCollectable"/>
		  	<form:hidden path="priceThirdPartyNew"/>
		  	<form:hidden path="priceAtAZ"/>

		  	<form:hidden path="reviewAsHTML"/>

		  	<form:hidden path="isOnAZ"/>
		  	<form:hidden path="isImageOnAZ"/>
		  	<form:hidden path="isReviewOnAZ"/>

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
          <div class="column w-100-percent">
			<label>Title</label><br/>
			<form:input path="title" size="100" required="required" />
	  	 </div>
	  	</div>
		<div class="row">
		  <div class="column w-33-percent">
		          <label>ISBN</label><br/>
                   <form:input path="isbn" autofocus="autofocus"/>
				<form:checkbox path="generateISBN" />
               <label>Generate New ISBN</label>

	  	</div>
          <div class="column w-33-percent">
               <label for="publisher.id" path="publisher.id">Publisher</label><br/>
               <form:select path="publisher.id" required="required" >
			  				<form:option value="" label="Please select"/>
			  				<form:options items="${publisherList}" itemValue="id" itemLabel="name"/>
				</form:select>
				<a href="${pageContext.request.contextPath}/publisher/add?flow=addStock">Add</a>
	 	 </div>
      </div>
      <div class="row">
		<div class="column w-33-percent">
            <label>Publisher Date</label><br/>
        	<form:input path="publishedDate" />
		</div>
        <div class="column w-33-percent">
			<label for="category.id" path="category.id">Category</label><br/>
			<form:select path="category.id" required="required">
    			<form:option value="" label="Please select"/>
    			<form:options items="${categoryList}" itemValue="id" itemLabel="name"/>
			</form:select>
	 	</div>
		  	<div class="column w-33-percent">
                <label>Binding</label><br/>
                <form:select path="binding" required="required">
					<form:options items="${bindingOptions}" itemLabel="displayName"/>
				</form:select>
		  	</div>
	</div>
    <jsp:include page="pricesFragment.jsp"></jsp:include>
	      <div class="row">
			  <div class="column w-33-percent">
				<label>Keep In Stock Level</label><br/>
					<form:select path="keepInStockLevel" required="required">
					<form:options items="${levelList}" itemLabel="displayName"/>
				</form:select>
			  </div>
			  <div class="column w-33-percent">
				<label>Availability</label><br/>
					<form:select path="availability" required="required">
					<form:options items="${availablityList}" itemLabel="displayName"/>
				</form:select>
			  </div>
			  <div class="column w-33-percent">
             		<label>Keep In Stock</label><br/>
					<form:input path="quantityToKeepInStock" required="required" type="number" />
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
            <label>Put Stock On Website</label>
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
	</div>
	<div class="row">
		<div class="column w-33-percent">
            	<label>Marxism Quantity</label><br/>
				<form:input path="quantityForMarxism" type="number" />
  		  </div>
	</div>
	 <!-- Authors -->
     <div class="row">
		  <div class="column w-100-percent">
		  		Search Author <input type="text" id="authorAutoComplete"/><a onclick="javascript:addAuthor()" class="btn btn-success">Add</a><br/>
		  		<div id="authorMessage"/>
		  		<div id="authorTable"/>
		  </div>
    </div>
	<div class="row">
		<div class="column w-50-percent">
			<label>Review</label><br/>
			<form:textarea path="reviewAsText" cols="60" rows="5"/><br/><br/><br/>
			<input type="submit" class="btn btn-primary" value="Add" />
			<button class="btn btn-success" type="button" onclick="javascript:lookUpFromAZUsingIsbn()">Lookup at AZ</button>
			<c:if test="${sessionScope.flow == 'customerOrder'}">
				<a href="${pageContext.request.contextPath}/customerOrder/continue" class="btn btn-success">Go Back to Customer Order</a>
			</c:if>
		</div>
        <div class="column w-50-percent">
			<c:if test="${stockItem.imageURL != null}">
				<img src="${stockItem.imageURL}"/>
			</c:if>
	 	</div>
	</div>
</form:form>
