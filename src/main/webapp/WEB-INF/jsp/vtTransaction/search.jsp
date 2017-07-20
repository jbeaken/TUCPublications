<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<form:form modelAttribute="vtTransactionSearchBean" action="search" method="post" role="form">
	<div class="row">
		<div class="col-sm-3">
			<div class="form-group">	  
				<label>Customer</label>
			</div>
		</div>
		
	</div>
			
			<input type="submit" class="btn btn-danger" id="searchVTTransactionsButton"/> 
			
			<a href="/bookmarks/vtTransaction/displaySearch" class="btn btn-danger">Reset</a>
			
</form:form>

<br/>
<c:if test="${vtTransactionList != null}">
	<display:table name="vtTransactionList" 
	   			   requestURI="search" 
		     	   decorator="org.bookmarks.ui.SearchVTTransactionsDecorator"
				   sort="external" 
				   defaultsort="2" 
				   defaultorder="ascending"
				   class="table table-striped table-bordered table-hover table-condensed"
				   export="true"
				   partialList="true"
				   pagesize="${pageSize}"
				   size="${searchResultCount}"
				   id="searchTable">	
			   <display:setProperty name="export.pdf" value="true" /> 
			   <display:setProperty name="export.xml" value="false" /> 
			   <display:setProperty name="export.pdf.filename" value="vtTransactions.pdf"/> 				   
			   <display:setProperty name="export.csv.filename" value="vtTransactions.txt"/> 	
			   				   				
			  <display:column property="id" sortable="true" sortName="t.id" title="ID"/>	
			  <display:column property="total" sortable="true" sortName="t.total" title="Total"/>
			  
<display:column title="Actions" media="html" style="width:10%">
					  	<div class="btn-group">
						  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
						    Action <span class="caret"></span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
						    <li><a href="${pageContext.request.contextPath}/vtTransaction/edit?id=${searchTable.id}&flow=search" target="_blank">Edit</a></li>
						    <li class="divider"></li>
						    <li><a href="${pageContext.request.contextPath}/vtTransaction/delete?id=${searchTable.id}">Delete</a></li>
						  </ul>
						</div>
					  </display:column>	  
	</display:table>
</c:if>
