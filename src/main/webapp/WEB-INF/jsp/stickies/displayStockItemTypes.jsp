<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="stockItem" action="${pageContext.request.contextPath}/stickies/manageStickyStockItemType" method="post">
	<div class="rows">
	        <div class="row">
	          <div class="column w-33-percent">
	          	<form:label for="type" path="type">Edit Stickies For : </form:label><br/>
				<form:select id="stockItemTypeSelect" path="type">
	            			<form:option value="" label="Select"/>
            				<form:options items="${stockItemTypeList}" itemLabel="displayName"/></form:select>			
		  	 </div>
	         </div>
	        <div class="row">
	          <div class="column w-100-percent">
	          	<input type="submit" class="btn btn-primary" id="selectSupplierSubmitButton" value="Manage Stickies"/>
	          </div>
		  	</div>
	</div>
</form:form>