<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<div class="rows">
        <div class="row">
          <div class="column w-100-percent">
				<label>Title:</label><br/>
				${lastStockRecord.stockItem.title}<br/>			
	  	 </div>
		</div>
		<div class="row">
          <div class="column w-33-percent">
				<label>Category:</label><br/>
				${lastStockRecord.stockItem.category.name}<br/>
	  	 </div>
		<div class="column w-33-percent">	
			    <label>Quantity:</label><br/>
				${lastStockRecord.quantity}<br/>	
		</div>			 
		<div class="column w-33-percent">	
				<label>Author:</label><br/>
				${lastStockRecord.stockItem.mainAuthor}<br/>	
		</div>	
		</div>
</div>      
<br/>
<form:form modelAttribute="stockItemSearchBean" action="save" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
				<form:label	for="stockItem.isbn" path="stockItem.isbn" cssErrorClass="error">ISBN</form:label><br/>
				<form:input path="stockItem.isbn" id="focus"/> <form:errors path="stockItem.isbn" />
	  	 </div>
	  	</div>
      <div class="row">
          <div class="column w-100-percent">
 				<input type="submit" id="sellStockSubmitButton" class="btn btn-primary" value="Add"/>
 				 <c:if test="${lastStockRecord != null}"> 
	 				<button type="button" onclick="javascript:authoriseUser('/bookmarks/stockTakeLine/edit?id=${lastStockRecord.id}&flow=search')" class="btn btn-primary" id="editLastStockRecordButton">Edit Last Record</button>
	 				<a href="increment?id=${lastStockRecord.id}&flow=search" tabindex="3">
	 					<button type="button" class="btn btn-primary" id="incrementLastStockRecordButton">Increment Last Record</button>
	 				</a>	 				
	 			</c:if>
	 	 </div>
	</div>				
</div>				
</form:form>
<br/>	
<p>
	<display:table name="stockTakeLineList" decorator="org.bookmarks.ui.StockTakeDecorator" id="stockRecordList">
	  <display:column property="stockItem.isbn" title="ISBN" class="isbn"/>
	  <display:column property="stockItem.title" title="Title" class="title"/>
	  <display:column property="stockItem.mainAuthor" title="Authors" class="concatenatedAuthors"/>
	  <display:column property="quantity" title="Quantity"/>
	  <display:column property="stockItem.category.name" title="Category"/>
	  <display:column property="link" title="Actions" />
	</display:table>
</p>
      <div class="row">
          <div class="column w-100-percent">
	 			<button type="button" class="btn btn-danger" onclick="javascript:authoriseSuperUser('/bookmarks/stockTakeLine/reset')">Reset Stock Record</button>&nbsp;&nbsp;
	 			<button type="button" class="btn btn-danger" onclick="javascript:authoriseSuperUser('/bookmarks/stockTakeLine/commit')">Update Stock Record</button>
	 	 </div>
	</div>		