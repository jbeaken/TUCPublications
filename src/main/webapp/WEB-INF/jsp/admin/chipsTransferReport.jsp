<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<div class="rows">
        <div class="row">
          <div class="column w-100-percent">
			<label>Report for Transfer From Chips</label>
	  	 </div>
      </div>	
</div>	
<br/>
<br/>
<br/>
 <c:forEach var="customer" items="${websiteCustomerList}" varStatus="rowCounter">
 	
 	<label>CUSTOMER : </label>${customer.fullName}<br/>
 	
 	No of order lines : ${customer.orders.size()}<br/>
 	
 		 <c:forEach var="order" items="${customer.orders}" varStatus="rowCounter">
 		 		<label>Stock : ${order.stockItem.title}</label><br/>
 		 </c:forEach>
 		  	
 </c:forEach>
