<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="supplierSearchBean" action="search" method="post">
	<div class="rows">
	        <div class="row">
	          <div class="column w-33-percent">
					<form:label	for="supplier.name" path="supplier.name" cssErrorClass="error">Supplier Name</form:label><br/>
					<form:input path="supplier.name" id="focus"/> <form:errors path="supplier.name" />			
					
		  	 </div>
		  	</div>
	      <div class="row">
	          <div class="column w-70-percent">
	 				<input type="submit" class="btn btn-danger" id="searchSuppliersButton"/> 
	 				<a href="/supplier/displaySearch">
	 					<button type="button" class="btn btn-danger">Reset</button>
	 				</a>
		 	 </div>
		</div>		
	</div>		
</form:form>
<br/>
<c:if test="${supplierList != null}">
	<display:table name="supplierList" 
	   			   requestURI="search" 
		     	   decorator="org.bookmarks.ui.SearchSuppliersDecorator"
				   sort="external" 
				   defaultsort="2" 
				   defaultorder="ascending"
				   export="true"
				   partialList="true"
				   pagesize="${pageSize}"
				   size="${searchResultCount}"
				   id="searchTable">	
			   <display:setProperty name="export.pdf" value="true" /> 
			   <display:setProperty name="export.xml" value="false" /> 
			   <display:setProperty name="export.pdf.filename" value="suppliers.pdf"/> 				   
			   <display:setProperty name="export.csv.filename" value="suppliers.txt"/> 					   				
			  <display:column property="id" sortable="true" sortName="s.id" title="ID"/>	
			  <display:column property="name" sortable="true" sortName="s.name" title="Name"/>
			  <display:column property="telephone" title="Telephone"/>
			  <display:column property="contactName" title="Contact"/>
			  <display:column property="email" title="Email"/>
			  <display:column property="accountNumber" title="Account No."/>
			  <display:column property="link" title="Actions" />
	</display:table>
</c:if>
