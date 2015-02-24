<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="saleOrReturnSearchBean" action="search" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
				<form:label	for="saleOrReturn.id" path="saleOrReturn.id" cssErrorClass="error">ID</form:label><br/>
				<form:input path="saleOrReturn.id"/><form:errors path="saleOrReturn.id" />			
	  	 </div>
          <div class="column w-33-percent">
				<form:label	for="saleOrReturn.customer.firstName" path="saleOrReturn.customer.firstName" cssErrorClass="error">First Name</form:label><br/>
				<form:input path="saleOrReturn.customer.firstName"/><form:errors path="saleOrReturn.customer.firstName" />			
	  	 </div>
		  <div class="column w-33-percent">
	          <form:label for="saleOrReturn.customer.lastName" path="saleOrReturn.customer.lastName" cssErrorClass="error">Last Name</form:label><br/>
	          <form:input path="saleOrReturn.customer.lastName"/><form:errors path="saleOrReturn.customer.lastName" />
	  	</div>		
      </div>
        <div class="row">
          <div class="column w-33-percent">
		         <form:label for="saleOrReturn.saleOrReturnStatus" path="saleOrReturn.saleOrReturnStatus" cssErrorClass="error">Status</form:label><br/>
       			<form:select path="saleOrReturn.saleOrReturnStatus">
        			<form:option value="" label="All"/>
        			<form:options items="${saleOrReturnStatusList}" itemLabel="displayName"/>
    			</form:select> 
	  	 </div>
		  <div class="column w-33-percent">
	          <form:label for="stockItem.isbn" path="stockItem.isbn" cssErrorClass="error">ISBN</form:label><br/>
	          <form:input path="stockItem.isbn"/><form:errors path="stockItem.isbn" />
	  	</div>		
		  <div class="column w-33-percent">
	          <form:label for="overdue" path="overdue" cssErrorClass="error">Overdue</form:label><br/>
	          <form:checkbox path="overdue"/><form:errors path="overdue" />
	  	</div>		
	  </div>
	  <div class="row">
          <div class="column w-100-percent">
				<input type="submit" class="btn btn-primary"/>
					<a href="${pageContext.request.contextPath}/saleOrReturn/displaySearch">
	 					<button type="button" class="btn btn-warning">Reset</button>
	 				</a>
	  	 </div>
	  </div>
</div>	
</form:form>
<br/>
<c:if test="${saleOrReturnList != null}">
					<display:table 
			   name="saleOrReturnList"
			   requestURI="search" 
			   decorator="org.bookmarks.ui.SearchSaleOrReturnDecorator"
			   sort="external" 
			   defaultsort="2" 
			   defaultorder="ascending"
			   partialList="true"
			   size="${searchResultCount}"
			   pagesize="10"
			   id="searchTable">
					  <display:column sortable="true" sortName="s.id" property="id" title="ID"/>	
					  <display:column sortable="true" sortName="s.creationDate" property="creationDate" title="Date"/>
					  <display:column sortable="true" sortName="cus.lastName" maxLength="15" property="customerName" title="Customer"/>
					  <display:column property="telephoneNumber" maxLength="15" title="Telephone"/>
					  <display:column sortable="true" sortName="s.saleOrReturnStatus" property="saleOrReturnStatus.displayName" title="Delivery Type"/>
					  <display:column property="totalPrice" title="Total Price"/>
					  <display:column property="totalAmount" title="Total Amount"/>
					  <display:column sortable="true" sortName="s.returnDate" property="returnDate" title="Return Date"/>
					  <display:column property="link" title="Actions"/>
  					</display:table>
</c:if>		