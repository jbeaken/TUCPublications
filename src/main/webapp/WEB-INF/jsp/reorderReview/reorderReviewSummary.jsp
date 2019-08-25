<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
					<display:table name="reorderReviewStockItemBeanList">
						<display:column property="stockItem.isbn" title="ISBN"/>
						<display:column property="stockItem.title" title="Title"/>
						<display:column property="stockItem.quantityForMarxism" title="Marxism"/>
						<display:column property="supplierOrderLine.amount" title="Amount"/>
  					</display:table>
  					<br/>
<a href="${pageContext.request.contextPath}/reorderReview/save">
	<button class="btn btn-primary" id="saveButton">Save</button>
</a>
&nbsp;&nbsp;&nbsp;
<a href="${pageContext.request.contextPath}/reorderReview/cancel">
	<button class="btn btn-primary" id="cancelButton">Cancel</button>
</a>