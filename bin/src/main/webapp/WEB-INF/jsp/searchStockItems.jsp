<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<h1>Search Stock Items</h1>
<form:form id="stockItemSearchForm" modelAttribute="stockItemSearchBean" action="${pageContext.request.contextPath}/stock/search" method="post">
  <jsp:include page="searchStockItemFragment.jsp" />
<div id="rows">
  <div class="row">
          <div class="column w-100-percent">
      	  <button type="button" class="btn btn-primary" id="searchStockSubmitButton" onclick="javascript:submitForm('${pageContext.request.contextPath}/stock/search')">Search</button>
      	  <button type="button" class="btn btn-warning" id="reorderReviewButton" onclick="javascript:submitForm('${pageContext.request.contextPath}/stock/search?reorderReview=true?skipMarxismRejects=false')">Reorder Review</button>&nbsp;
          <button type="button" class="btn btn-primary" id="reorderReviewButton" onclick="javascript:submitForm('${pageContext.request.contextPath}/stock/search?reorderReview=true&skipMarxismRejects=true')">Reorder Review (skip)</button>&nbsp;
          <button type="button" class="btn btn-warning" id="resetButton" accesskey="R">Reset</button>&nbsp;
          <c:if test="${stockItemList == null}">
      		<button type="button" class="btn btn-danger" onclick="javascript:submitForm('${pageContext.request.contextPath}/stock/azlookupFromSearchPage')">Add Stock</button>
      	  </c:if>
     </div>
  </div>
</div>
<br/>
<hr/>
</form:form>
<c:if test="${stockItemList == null}">
<br/>
<br/>
Nothing Found
</c:if>
<c:if test="${stockItemList != null}">

<display:table name="stockItemList"
         requestURI="search"
         decorator="org.bookmarks.ui.SearchStockItemsDecorator"
         sort="external"
         defaultsort="1"
         defaultorder="ascending"
         pagesize="${pageSize}"
         export="true"
         partialList="true"
         size="${searchResultCount}"
         id="searchTable"
         class="smallTextTable">
     <display:setProperty name="export.pdf" value="true" />
     <display:setProperty name="export.xml" value="false" />
     <display:setProperty name="export.pdf.filename" value="stock.pdf"/>
     <display:setProperty name="export.csv.filename" value="stock.csv"/>
  <display:column property="isbn" sortable="true" sortName="s.isbn" title="ISBN"/>
  <display:column title="Image" media="html">
    <c:if test="${searchTable.imageURL != null}">
      <img src="${searchTable.imageURL}" style="max-width : 30%"/>
    </c:if>
    <c:if test="${searchTable.imageURL == null}">
      No Image
    </c:if>
</display:column>
  <display:column property="title" sortable="true" sortName="s.title" maxLength="100" title="Title" style="width:50%"/>
  <display:column property="category.name" sortable="true" maxLength="10" class="category" sortName="s.category.name" title="Category"/>
  <display:column property="info" title="Info" maxLength="7"/>
  <display:column property="mainAuthor" maxLength="15" title="Authors"/>
  <display:column property="prices" sortable="true" sortName="s.sellPrice" title="Sell/Pub/Cost"/>
  <display:column property="quantities" sortable="true" sortName="s.quantityInStock" title="S/O/C/R/M" />
  <display:column property="priceForMarxism" media="excel csv pdf" title="Marxism Price" />
  <display:column property="realQuantityForMarxism" media="excel csv pdf" title="Real Quantity For Marxism" />
  <display:column property="priceMissingForMarxism" media="excel csv pdf" title="Real Marxism Price" />
<display:column title="Actions" media="html" style="width:10%">
					  	<div class="btn-group">
						  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
						    Action <span class="caret"></span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
						    <li><a href="${pageContext.request.contextPath}/stock/edit?id=${searchTable.id}&flow=search" target="_blank">Edit</a></li>
						    <li><a href="${pageContext.request.contextPath}/stock/displayEditNote?id=${searchTable.id}" target="_blank">Edit Note</a></li>
						    <li><a href="${pageContext.request.contextPath}/saleReport/displayStockItemMonthlySaleReport?id=${searchTable.id}" target="_blank">Show Sales</a></li>
						    <li><a href="${pageContext.request.contextPath}/supplierOrderLine/displayCreate?id=${searchTable.id}" target="_blank">Create Supplier Order</a></li>

						  	<c:if test="${invoice != null}">
  								<li><a href="${pageContext.request.contextPath}/invoice/addStockItem?id=${searchTable.id}">Add To Invoice</a></li>
  							</c:if>

						    <li class="divider"></li>

						    <li><a href="${pageContext.request.contextPath}/sale/sellAndGoByISBN?isbn=${searchTable.isbn}">Sell And Go<c:if test="${sessionScope.event != null}"> For Event ${sessionScope.event.name}</c:if></a></li>
						    <li><a href="${pageContext.request.contextPath}/sale/sellAndStayByISBN?isbn=${searchTable.isbn}">Sell And Stay<c:if test="${sessionScope.event != null}"> For Event ${sessionScope.event.name}</c:if></a></li>
						    <li><a href="${pageContext.request.contextPath}/stock/addImageToStockItem?id=${searchTable.id}" target="_blank">Add Image</a></li>
						    <li><a href="${pageContext.request.contextPath}/saleReport/displayStockItemMonthlySaleReport?id=${searchTable.id}" target="_blank">Show Sales</a></li>

						    <li class="divider"></li>

							<c:if test="${searchTable.putOnWebsite == true}">
								<li><a href="${pageContext.request.contextPath}/chips/removeFromWebsite?id=${searchTable.id}" target="_blank">Remove From Website</a></li>
							</c:if>
							<c:if test="${searchTable.putOnWebsite == false}">
								<li><a href="${pageContext.request.contextPath}/chips/putOnWebsite?id=${searchTable.id}" target="_blank">Put On Website</a></li>
							</c:if>
						    <li><a href="${pageContext.request.contextPath}/chips/syncStockItemWithChips?id=${searchTable.id}" target="_blank">Sync With Chips</a></li>

						    <li class="divider"></li>

						    <li><a onclick='javascript:authoriseUser("${pageContext.request.contextPath}/stock/delete?id=${searchTable.id}")' >Delete</a></li>
						  </ul>
						</div>
</display:column>
</display:table>
</c:if>
