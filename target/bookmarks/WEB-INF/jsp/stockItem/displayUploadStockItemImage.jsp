<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<h1>Select a file to upload for stockitem : ${stockItem.title}</h1>
<form:form modelAttribute="stockItem" action="${pageContext.request.contextPath}/stock/addImageToStockItem" method="post" enctype="multipart/form-data">
<form:hidden path="id"/>
	<div class="rows">
	        <div class="row">
	          <div class="column w-33-percent">
					<form:label	for="file" path="file">Image To Upload</form:label><br/>
					<form:input path="file" type="file" required="required" />			
		  	 </div>
		  	  <div class="column w-33-percent">
		  	  		<form:checkbox path="putOnWebsite" />
					<form:label for="putOnWebsite" path="putOnWebsite" cssErrorClass="error">Put Stock Item On Website</form:label>
               		<br/>  
					<form:checkbox path="putImageOnWebsite" />
               		<form:label for="putImageOnWebsite" path="putImageOnWebsite" cssErrorClass="error">Put Image On Website</form:label>
              		<br/> 		
		  	 </div>
		  </div>
	      <div class="row">
	          <div class="column w-33-percent">
	 				<input type="submit" class="btn btn-primary" value="Upload Image And Sync with Chips"/> 
		 	 </div>		 	 
		</div>	
	   <div class="row">
	          <div class="column w-50-percent">
				<c:if test="${stockItem.imageURL == null}">
					No image available, click Download Image to get one
				</c:if>  
				<c:if test="${stockItem.imageURL != null}">
               		<img src="${stockItem.imageURL}"/> 
				</c:if>
		 	 </div>		 	 
		</div>	
	</div>		
</form:form>