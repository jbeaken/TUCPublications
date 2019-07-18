<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<div class="rows">
	        <div class="row">
	          <div class="column w-33-percent">
	 				<label><c:if test="${invoice.customer.bookmarksAccount.accountHolder == false}">Proforma</c:if> Invoice Number : </label> B${invoice.id}
		 	 </div>
	          <div class="column w-33-percent"> 
	 				<label>Invoice Date : </label><fmt:formatDate value="${invoice.creationDate}" dateStyle="MEDIUM"/>  
		 	 </div>
	          <div class="column w-33-percent"> 
	 				<label>Delivery Type : </label>${invoice.deliveryType.displayName}
		 	 </div>
		  </div>  		  
</div>
<jsp:include page="../customer/viewCustomerFragment.jsp"/>
<div class="rows">
        <div class="row">
          <div class="column w-100-percent">
 				${invoice.note}
	 	 </div>
	  </div>  
</div>
<br/>
<c:if test="${not empty invoice.sales}">
	<display:table 
			name="invoice.sales" 
			decorator="org.bookmarks.ui.InvoiceOrderLineDecorator">
	  <display:column property="stockItem.isbn" title="ISBN"/>
	  <display:column title="Image">
	    <c:if test="${searchTable.stockItem.imageURL != null}">
	      <img src="${searchTable.stockItem.imageURL}" style="max-width : 100%"/>
	    </c:if>
	    <c:if test="${searchTable.stockItem.imageURL == null}">
	      No Image
	    </c:if>    
	  </display:column>		  
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
 <br/>   
<br/>
<c:if test="${flow == 'editCustomerOrderLine'}">
	<a href="/customerOrderLine/edit?id=${customerOrderLineId}">
	 	<button type="button" class="btn btn-primary">Back to Customer Order</button>
	</a>
</c:if>
<c:if test="${flow == 'searchInvoices'}">
	<a href="/invoice/searchFromSession">
	 	<button type="button" class="btn btn-primary">Back to Search</button>
	</a>
</c:if>
	<a href="/invoice/print?invoiceId=${invoice.id}" target="_blank">
	 	<button type="button" class="btn btn-primary">Print Invoice</button>
	</a>
<br/><br/>
<c:if test="${invoice.customerOrderLines != null}">
	<display:table
			   name="invoice.customerOrderLines"
			   requestURI="search" 
			   decorator="org.bookmarks.ui.SearchCustomerOrderLineDecorator"
			   sort="external" 
			   export="true"
			   defaultsort="4" 
			   defaultorder="ascending"
			   id="searchTable">
	   <display:setProperty name="export.pdf" value="true" /> 
	   <display:setProperty name="export.xml" value="false" /> 
	   <display:setProperty name="export.pdf.filename" value="customerOrders.pdf"/> 				   
	   <display:setProperty name="export.csv.filename" value="customerOrders.txt"/> 					   
					  <display:column sortable="true" sortName="col.id" property="id" title="No." media="html"/>
					  <display:column sortable="true" sortName="col.id" property="rawId" title="No." media="pdf"/>
					  <display:column sortable="true" sortName="col.creationDate"  property="creationDate" title="Date"/>
					  <display:column sortable="true" sortName="col.customer.lastName" maxLength="15" property="customerName" title="Customer" media="html"/>
					  <display:column maxLength="15" property="rawCustomerName" title="Customer" media="pdf"/>
					  <display:column property="telephoneNumber" maxLength="15" title="Telephone"/>
					  <display:column sortable="true" sortName="col.customerOrderStatus" maxLength="15" property="customerOrderStatus.displayName" title="Status"/>
					  <display:column sortable="true" sortName="col.deliveryType" property="deliveryType.displayName" title="Delivery Type"/>
					  <display:column sortable="true" sortName="col.paymentType" property="paymentType.displayName" title="Payment Type"/>
					  <display:column sortable="true" sortName="col.stockItem.isbn" property="stockItem.isbn" title="ISBN"/>
					  <display:column sortable="true" sortName="col.stockItem.title" maxLength="30" property="title" title="Title" media="html"/>
					   <display:column maxLength="30" property="rawTitle" title="Title" media="pdf"/>
					  <display:column maxLength="10" property="stockItem.mainAuthor" title="Authors"/>
					  <display:column sortable="true" sortName="col.amount" property="amount" title="Amount"/>
  	</display:table>
</c:if>