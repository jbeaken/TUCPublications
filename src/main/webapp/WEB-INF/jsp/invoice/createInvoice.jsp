<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<style type="text/css">
thead tr {
	background-color: #b0bf23;
}
th.sorted {
	background-color: #9ebf0a;
}
tr.tableRowEven,tr.even {
	background-color: #fff5c0;
}
</style>
<c:if test="${secondHandPrice == null && saleToEdit == null}">
<div class="rows">
        <div class="row">
          <div class="column w-100-percent">
				<label><c:if test="${invoice.customer.bookmarksAccount.accountHolder == false}">Proforma</c:if> Invoice for</label> ${invoice.customer.fullName}&nbsp;&nbsp;
				<c:if test="${invoice.customer.bookmarksAccount.accountHolder == true}">
					<label>Current Balance :</label> &pound;${invoice.customer.bookmarksAccount.currentBalance}&nbsp;&nbsp;
				</c:if>
				<c:if test="${sessionScope.event != null}">
					<label>For Event :</label>${sessionScope.event.name}&nbsp;&nbsp;
				</c:if>

				<label>Customer Type :</label> ${invoice.customer.customerType.displayName}<br/>
				<label>Delivery Type :</label> ${invoice.deliveryType.displayName}<br/>
	  	 </div>
	  	</div>
</div>
<form:form modelAttribute="invoice" action="${pageContext.request.contextPath}/invoice/save" method="post">
<form:hidden path="customer.id"/>
<form:hidden path="customer.firstName"/>
<form:hidden path="customer.lastName"/>
<form:hidden path="customer.customerType"/>
<form:hidden path="secondHandPrice"/>
<form:hidden path="serviceCharge"/>
<form:hidden path="stockItemCharges"/>
<form:hidden path="deliveryType"/>
<form:hidden path="paid"/>
<form:hidden path="isProforma"/>
<form:hidden path="customer.bookmarksAccount.openingBalance"/>
<form:hidden path="customer.bookmarksAccount.currentBalance"/>
<form:hidden path="customer.bookmarksAccount.accountHolder"/>
<div class="rows">
      <div class="row">
          <div class="column w-100-percent">
 				<input type="submit" class="btn btn-primary" value="Save Invoice"/>
		 		<a href="${pageContext.request.contextPath}/invoice/showAddAdditionalCharges">
		 			<button class="btn btn-primary" type="button" id="addAdditionalChargesButton">Add Additional Charges</button>
		 		</a>
		 		<c:if test="${invoice.deliveryType == 'MAIL'}">
					<a href="${pageContext.request.contextPath}/invoice/setAsCollection">
						<button class="btn btn-primary" type="button">Set As Collection</button>
					</a>
				</c:if>
		 		<c:if test="${invoice.deliveryType == 'COLLECTION'}">
					<a href="${pageContext.request.contextPath}/invoice/setAsMail">
						<button class="btn btn-primary" type="button">Set As Mail Delivery</button>
					</a>
				</c:if>
		 		<c:if test="${invoice.paid == true}">
					<a href="${pageContext.request.contextPath}/invoice/setAsPaid?paid=false">
						<button class="btn btn-danger" type="button">Set As Unpaid</button>
					</a>
				</c:if>
		 		<c:if test="${invoice.paid == false}">
					<a href="${pageContext.request.contextPath}/invoice/setAsPaid?paid=true">
						<button class="btn btn-primary" type="button">Set As Paid</button>
					</a>
				</c:if>
		 		<c:if test="${invoice.updateStock == false}">
					<a href="${pageContext.request.contextPath}/invoice/setUpdateStock?updateStock=true">
						<button class="btn btn-primary" type="button">Set As Don't Update stock</button>
					</a>
				</c:if>
		 		<c:if test="${invoice.updateStock == true}">
					<a href="${pageContext.request.contextPath}/invoice/setUpdateStock?updateStock=false">
						<button class="btn btn-danger" type="button">Set As Update Stock</button>
					</a>
				</c:if>
		 		<a href="${pageContext.request.contextPath}/invoice/cancel">
		 			<button class="btn btn-primary" type="button">Cancel</button>
		 		</a>
	 	 </div>
	</div>
</div>
</form:form>
</c:if>
<c:if test="${secondHandPrice != null}">
<label class="smallCaps">Second Hand</label>
<form:form modelAttribute="invoice" action="${pageContext.request.contextPath}/invoice/addAdditionalCharges" method="post">
<form:hidden path="customer.id"/>
<form:hidden path="customer.firstName"/>
<form:hidden path="customer.lastName"/>
<form:hidden path="customer.customerType"/>
<form:hidden path="paid"/>
<form:hidden path="isProforma"/>
<form:hidden path="updateStock"/>
<form:hidden path="stockItemCharges"/>
<form:hidden path="customer.bookmarksAccount.currentBalance"/>
<form:hidden path="customer.bookmarksAccount.accountHolder"/>
<form:hidden path="customer.bookmarksAccount.amountPaidInMonthly"/>
<form:hidden path="customer.bookmarksDiscount"/>
<form:hidden path="customer.nonBookmarksDiscount"/>
		<div class="rows">
		      <div class="row">
		          <div class="column w-50-percent">
		 				<form:label for="secondHandPrice" path="secondHandPrice">Second Hand</form:label><br/>
		 				<form:input for="secondHandPrice" path="secondHandPrice" id="focus"/>&nbsp;<form:errors path="secondHandPrice" />
			 	 </div>
		          <div class="column w-50-percent">
		 				<form:label for="serviceCharge" path="serviceCharge">Service Charge</form:label><br/>
		 				<form:input for="serviceCharge" path="serviceCharge"/>&nbsp;<form:errors path="serviceCharge" />
			 	 </div>
			</div>
		      <div class="row">
		          <div class="column w-100-percent">
		 				<input type="submit" class="btn btn-primary"/>
			 	 </div>
			</div>
		</div>
</form:form>
</c:if>
<c:if test="${saleToEdit != null}">
<br/>
<legend>${saleToEdit.stockItem.title}</legend>
<form:form modelAttribute="saleToEdit" action="${pageContext.request.contextPath}/invoice/editInvoiceOrderLine" method="post">
	<form:hidden path="stockItem.id"/>
	<form:hidden path="stockItem.type"/>
	<form:hidden path="stockItem.quantityInStock"/>
	<form:hidden path="stockItem.quantityToKeepInStock"/>
	<form:hidden path="stockItem.keepInStockLevel"/>
	<form:hidden path="stockItem.title"/>
	<form:hidden path="stockItem.isbn"/>
	<form:hidden path="stockItem.publisher.id"/>
	<form:hidden path="stockItem.publisher.name"/>
	<form:hidden path="discountHasBeenOverridden"/>
	<form:hidden path="discount"/>
	<form:hidden path="discountedPrice"/>
	<form:hidden path="vat"/>
	<form:hidden path="vatAmount"/>
		<div class="rows">
		      <div class="row">
		          <div class="column w-33-percent">
		 				<form:label for="quantity" path="quantity">Quantity</form:label><br/>
		 				<form:input for="quantity" path="quantity" id="focus"/>&nbsp;<form:errors path="quantity" />
			 	 </div>
		          <div class="column w-33-percent">
		 				<form:label for="sellPrice" path="sellPrice">Sell Price</form:label><br/>
		 				<form:input for="sellPrice" path="sellPrice"/>&nbsp;<form:errors path="sellPrice" />
			 	 </div>
		          <div class="column w-33-percent">
		 				<form:label for="newDiscount" path="newDiscount">Discount</form:label><br/>
		 				<form:input for="newDiscount" path="newDiscount"/>&nbsp;<form:errors path="newDiscount" />
			 	 </div>
			 	</div>
		      <div class="row">
		          <div class="column w-100-percent">
		          		<br/>
		 				<input type="submit" class="btn btn-primary"/>
			 	 </div>
			</div>
		</div>
</form:form>
</c:if>
<c:if test="${saleToEdit == null && secondHandPrice == null}">
<form:form modelAttribute="stockItemSearchBean" action="${pageContext.request.contextPath}/invoice/searchStockItems" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
				<form:label	for="stockItem.isbn" path="stockItem.isbn" cssErrorClass="error">ISBN</form:label><br/>
				<form:input path="stockItem.isbn" id="focus" />
				<form:errors path="stockItem.isbn" />
				<input type="submit" class="btn btn-primary"/>
	  	 </div>
	  	</div>
</div>
</form:form>
<br/>
<br/>
</c:if>
<c:if test="${saleToEdit == null && secondHandPrice == null && saleList != null}">
	<display:table
		name="saleList"
		decorator="org.bookmarks.ui.InvoiceOrderLineDecorator">
	  <display:column property="stockItem.isbn" title="ISBN"/>
	  <display:column property="stockItem.title" title="Title"/>
	  <display:column property="quantity" title="Quantity"/>
	  <display:column property="vat" title="VAT"/>
	  <display:column property="vatAmount" title="VAT Payable"/>
	  <display:column property="sellPrice" title="Sell Price"/>
	  <display:column property="discount" title="Discount"/>
	  <display:column property="discountedPrice" title="Discount Price"/>
	  <display:column property="totalPrice" title="Total Price"/>
	  <display:column property="link" title="Actions" />
	</display:table>
</c:if>
<br/>
<c:if test="${saleToEdit == null && secondHandPrice == null}">
<div class="rows">
<jsp:include page="pricesFragment.jsp"/>
<c:if test="${invoice.customer.bookmarksAccount.accountHolder == true}">
	<div class="row">
	          <div class="column w-20-percent">
	 				<label>New Balance :</label>
		 	 </div>
	          <div class="column w-10-percent">
	 				<fmt:formatNumber value="${newBalance}" type="currency" />
		 	 </div>
	  </div>
</c:if>
</div>
</c:if>
