<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<h1>Select brochure file</h1>
<form:form modelAttribute="stockItem" action="${pageContext.request.contextPath}/chips/uploadBrochure" method="post" enctype="multipart/form-data">


	<div class="rows">
	        <div class="row">
	          	<div class="column w-33-percent">
							<label for="file">Pdf File To Upload</label><br/>
							<input name="file" type="file" required="required" />
		  	 </div>
		  </div>

			<div class="rows">
	      <div class="row">
	          <div class="column w-33-percent">
	 				<input type="submit" class="btn btn-primary" value="Upload Brochure"/>
		 	 </div>
		</div>

	</div>
</form:form>
