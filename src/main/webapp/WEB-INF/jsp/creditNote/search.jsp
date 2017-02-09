<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="creditNoteSearchBean" action="search" method="post" role="form">
	<div class="row">
		<div class="col-sm-3">
			<div class="form-group">	  
				<label>Customer</label>
			</div>
		</div>
		<div class="col-sm-3">
			<div class="form-group">	  
				<label>Reconcilation</label>
				<div>${clubAccountOutgoings} + ${clubAccountIncomings} = ${clubAccountBalance}</div>
			</div>
		</div>		
	</div>
			
			<input type="submit" class="btn btn-danger" id="searchCreditNotesButton"/> 
			
			<a href="/bookmarks/creditNote/displaySearch" class="btn btn-danger">Reset</a>
			
</form:form>

<br/>
<c:if test="${creditNoteList != null}">
	<display:table name="creditNoteList" 
	   			   requestURI="search" 
		     	   decorator="org.bookmarks.ui.SearchCreditNotesDecorator"
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
			   <display:setProperty name="export.pdf.filename" value="creditNotes.pdf"/> 				   
			   <display:setProperty name="export.csv.filename" value="creditNotes.txt"/> 	
			   				   				
			  <display:column property="id" sortable="true" sortName="cn.id" title="ID"/>	
			  <display:column property="date" sortable="true" sortName="cn.date" title="Date"/>
			  <display:column property="customer" sortable="true" sortName="cn.date" title="Customer"/>
			  <display:column property="amount" sortable="true" sortName="cn.amount" title="Amount"/>
			  <display:column property="transactionDescription" sortable="true" sortName="cn.transactionDescription" title="Desc"/>
			  <display:column property="transactionReference" sortable="true" sortName="cn.transactionReference" title="Ref"/>
			  <display:column property="transactionType" sortable="true" sortName="cn.transactionType" title="Type"/>
			  
<display:column title="Actions" media="html" style="width:10%">
					  	<div class="btn-group">
						  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
						    Action <span class="caret"></span>
						  </button>
						  <ul class="dropdown-menu" role="menu">
						    <li><a href="${pageContext.request.contextPath}/creditNote/edit?id=${searchTable.id}&flow=search" target="_blank">Edit</a></li>
						    <li class="divider"></li>
						    <li><a href="${pageContext.request.contextPath}/creditNote/delete?id=${searchTable.id}">Delete</a></li>
						  </ul>
						</div>
					  </display:column>	  
	</display:table>
</c:if>
