<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<fmt:setLocale value="en_GB" scope="application"/>
<div class="rows">
        <div class="row">
          	<div class="column w-33-percent">
 				<label>Invoice Number : </label>B${invoice.id}
	 	 </div>
	          <div class="column w-33-percent">
	 				<label>Invoice Date : </label><fmt:formatDate value="${invoice.creationDate}" dateStyle="MEDIUM"/>
		 </div>
	          <div class="column w-33-percent">
	 				<label>Delivery Type : </label>${invoice.deliveryType.displayName}
		 </div>
	</div>
</div>
<div class="rows">
        <div class="row">
	          <div class="column w-33-percent">
	 				<label>Our Address: </label><br/>Bookmarks<br/>1 Bloomsbury St<br/>WC1B 3QE
		 	 </div>
	          <div class="column w-33-percent">
	 				<label>Our Contact Details: </label><br/>0207 637 1848<br/>info@bookmarksbookshop.co.uk
		 	 </div>
          <div class="column w-33-percent">
 				<label>VAT Number: </label>${vatNumber}
	 	 </div>
	  </div>
</div>
<jsp:include page="../customer/viewCustomerFragmentForPrinting.jsp"/>
<br/>
<br/>
<c:if test="${not empty invoice.sales}">
	<display:table
			name="invoice.sales"
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
	</display:table>
</c:if>
<br/>
<div class="rows">
<jsp:include page="pricesFragment.jsp"/>
</div>
