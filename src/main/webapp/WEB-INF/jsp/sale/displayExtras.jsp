<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<display:table name="extras"
		     	   decorator="org.bookmarks.ui.ExtrasDecorator"
				   class="table table-striped table-bordered table-hover table-condensed"
				   id="searchTable">
			  <display:column property="id" sortable="true" sortName="s.id" title="ID"/>

			 <display:column title="Actions">

				<c:if test="${sessionScope.invoice != null}">
					<a href="/invoice/addStockItem?id=${searchTable.id}"><img src="../resources/images/invoice.png" title="Add To Invoice" /></a>
				</c:if>

				<c:if test="${sessionScope.saleOrReturn != null}">
					<a href="/saleOrReturn/addStockItem?id=${searchTable.id}"><img src="../resources/images/saleOrReturn.png" title="Add To Sale Or Return" /></a>
				</c:if>

				<c:if test="${sessionScope.customerOrder != null}">
					<a href="/customerOrder/addStockItem?id=${searchTable.id}"><img src="../resources/images/order.png" title="Add To Customer Order" /></a>
				</c:if>

				<a href="/sale/sellAndGoByISBN?isbn=${searchTable.isbn}"><img src="../resources/images/sell.png" title="Sell" /></a>

				<a href="/stock/removeFromExtras/${searchTable.id}"><img src="../resources/images/delete_medium.png" title="Remove" /></a>

			</display:column>

			  <display:column property="title" title="Title"/>
			  <display:column property="type.displayName" title="Type"/>
			  <display:column property="isbn" title="ISBN"/>

</display:table>
