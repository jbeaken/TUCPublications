<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
Lines placed into stock
					<display:table name="supplierDeliveryLineList" requestURI="" decorator="org.bookmarks.ui.SupplierDeliveryLineDecorator">
					  <display:column property="stockItem.title" title="Title"/>
					  <display:column property="stockItem.category.name" title="Category"/>
					  <display:column property="amount" title="Amount"/>
					  <display:column property="sellPrice" title="Sell Price"/>
  					</display:table>
  					<br/>
<c:if test="${not empty customerOrderLineList}">
Lines for collection  	
					<display:table name="customerOrderLineList" 
						requestURI="" 
						decorator="org.bookmarks.ui.CustomerOrderLineForCollectionDecorator"
						id="searchTable">
					  <display:column property="customerName" title="Customer"/>
					  <display:column property="customerContactDetails" title="Contact Details"/>
					  <display:column property="stockItem.title" maxLength="15" title="Title"/>
					  <display:column property="paymentType.displayName" title="Payment"/>
					  <display:column property="amountFilled" title="Amount Filled"/>
					  <display:column property="amount" title="Order Amount"/>
					  
					  <display:column title="Actions" media="html" style="width:10%">
					  	<div class="btn-group">
						  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
						    Action <span class="caret"></span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
						    <li><a href="${pageContext.request.contextPath}/customerOrderLine/informCustomer?id=${searchTable.id}&customerOrderStatus=EMAILED_CUSTOMER" target="_blank">Emailed Customer</a></li>
						    <li><a href="${pageContext.request.contextPath}/customerOrderLine/informCustomer?id=${searchTable.id}&customerOrderStatus=LEFT_PHONE_MESSAGE" target="_blank">Left Phone Message</a></li>
						    <li><a href="${pageContext.request.contextPath}/customerOrderLine/informCustomer?id=${searchTable.id}&customerOrderStatus=SPOKE_TO_CUSTOMER" target="_blank">Spoke to Customer</a></li>
						  </ul>
						</div>
					  </display:column>	
					  
  					</display:table>
  					<br/>
</c:if>
<c:if test="${not empty customerOrderMailOrderList}">
Lines for mail order  	
					<display:table name="customerOrderMailOrderList" requestURI="" decorator="org.bookmarks.ui.SupplierDeliveryCustomerOrderLineDecorator">
					  <display:column property="customerName" title="Customer"/>
					  <display:column property="customerContactDetails" title="Contact Details"/>
					  <display:column property="stockItem.title" maxLength="15" title="Title"/>
					  <display:column property="paymentType.displayName" title="Payment"/>
					  <display:column property="amountFilled" title="Amount Filled"/>
					  <display:column property="amount" title="Order Amount"/>
  					</display:table>
</c:if>					