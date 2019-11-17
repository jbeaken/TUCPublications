<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="publisherSearchBean" action="search" method="post">
	<div class="rows">
	        <div class="row">
	          <div class="column w-33-percent">
					<form:label	for="publisher.name" path="publisher.name" cssErrorClass="error">Publisher Name</form:label><br/>
					<form:input path="publisher.name" id="focus"/> <form:errors path="publisher.name" />

		  	 </div>
		  	</div>
	      <div class="row">
	          <div class="column w-70-percent">
	 				<input type="submit" class="btn btn-primary"/>
		 				<a href="/publisher/displaySearch">
		 					<button type="button" class="btn btn-primary">Reset</button>
		 				</a>
		 	 </div>
		</div>
	</div>
</form:form>
		<br/>
			<c:if test="${requestScope.publisherList != null}">
					<display:table name="publisherList"
			   requestURI="search"
         decorator="org.bookmarks.ui.SearchPublishersDecorator"
			   sort="external"
			   defaultsort="2"
			   defaultorder="ascending"
			   export="true"
			   partialList="true"
			   size="${searchResultCount}"
			   pagesize="${pageSize}"
			   id="searchTable">
				 <display:setProperty name="export.pdf" value="true" />
				 <display:setProperty name="export.xml" value="false" />
				 <display:setProperty name="export.pdf.filename" value="publishers.pdf"/>
				 <display:setProperty name="export.csv.filename" value="publishers.txt"/>
					  <display:column property="id" sortable="true" sortName="p.id" title="ID"/>
					  <display:column property="name" sortable="true" sortName="p.name" title="Name"/>
					  <display:column property="telephone" title="Telephone"/>
					  <display:column property="contactName" title="Contact"/>
					  <display:column property="supplier.name" title="Supplier"/>
					  <display:column property="email" title="Email"/>
					  <display:column property="link" title="Actions" />
  					</display:table>
			</c:if>
