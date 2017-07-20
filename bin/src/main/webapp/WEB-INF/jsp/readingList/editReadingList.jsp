<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="readingList" action="edit?flow=${flow}" method="post">
			<form:hidden path="id"/>
<div class="rows">
      <div class="row">
          <div class="column w-33-percent">
			       <form:label for="name" path="name" cssErrorClass="error">readingLists Name</form:label><br/>
			       <form:input path="name" id="focus"/> <form:errors path="name" />				
	  	    </div>
          <div class="column w-33-percent">
               <form:label for="isOnWebsite" path="isOnWebsite" cssErrorClass="error">Is On Website</form:label> 				
				<form:checkbox path="isOnWebsite" />
				<br/>
				<form:label for="isOnSidebar" path="isOnSidebar" cssErrorClass="error">Is On Sidebar</form:label> 				
				<form:checkbox path="isOnSidebar" />		
	  	</div>	               
      </div>
     
      <div class="row">
          <div class="column w-70-percent">
 				<input type="submit" class="btn btn-primary"/> 
				<a href="${pageContext.request.contextPath}/readingList/searchFromSession"><button type="button" class="btn btn-danger">Back to search</button></a>
				<a href="${pageContext.request.contextPath}/readingList/view?id=${readingList.id}"><button type="button" class="btn btn-warning">Add Stock</button></a>
	 	 </div>
	</div>		
</div>		
</form:form>