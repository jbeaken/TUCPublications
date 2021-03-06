<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<form:form modelAttribute="creditNoteSearchBean" action="search"
	method="post" role="form">
	<div class="rows" style="padding-bottom: 20px">
		<div class="row">

			<div class="column w-33-percent">
				<form:label for="creditNote.customer.firstName" path="creditNote.customer.firstName"
					cssErrorClass="error">First Name</form:label>
				<br />
				<form:input path="creditNote.customer.firstName" />
				<form:errors path="creditNote.customer.firstName" />

			</div>

			<div class="column w-33-percent">
				<form:label for="creditNote.customer.lastName" path="creditNote.customer.lastName"
					cssErrorClass="error">Last Name</form:label>
				<br />
				<form:input path="creditNote.customer.lastName" />
				<form:errors path="creditNote.customer.lastName" />

			</div>

			<div class="column w-33-percent">
				<form:label for="creditNote.transactionReference" path="creditNote.transactionReference"
					cssErrorClass="error">Reference</form:label>
				<br />
				<form:input path="creditNote.transactionReference" />
				<form:errors path="creditNote.transactionReference" />
			</div>
		</div>

		<div class="row">

			<div class="column w-33-percent">
				<form:label for="creditNote.transactionDescription" path="creditNote.transactionDescription"
					cssErrorClass="error">Match</form:label>
				<br />
				<form:input path="creditNote.transactionDescription" />
				<form:errors path="creditNote.transactionDescription" />
			</div>
		</div>

		<div class="column w-33-percent">
			<form:label for="creditNote.transactionType" path="creditNote.transactionType"
				cssErrorClass="error">Type</form:label>
			<br />
			<form:select path="creditNote.transactionType">
				<form:option value="" label="All"/>
				<form:options items="${transactionTypeList}" itemLabel="displayName"/>
			</form:select>
			<form:errors path="creditNote.transactionType" />
		</div>
	</div>

	</div>
	<!-- /.rows -->

	<input type="submit" class="btn btn-danger"
		id="searchCreditNotesButton" />

	<a href="/creditNote/displaySearch" class="btn btn-danger">Reset</a>

</form:form>

<br />
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
		<display:setProperty name="export.pdf.filename"
			value="creditNotes.pdf" />
		<display:setProperty name="export.csv.filename"
			value="creditNotes.txt" />

		<display:column property="id" sortable="true" sortName="cn.id"
			title="ID" />
		<display:column property="date" sortable="true" sortName="cn.date"
			title="Date" />
		<display:column property="customerRaw" media="excel csv pdf" title="Customer" />

		<display:column property="customer" media="html" sortable="true" sortName="cn.date"
			title="Customer" />
		<display:column property="amount" media="html" sortable="true" sortName="cn.amount"
			title="Amount" />
			<display:column property="amountRaw" media="excel csv pdf" title="Amount" />

		<display:column property="transactionDescription" sortable="true"
			sortName="cn.transactionDescription" title="Customer Match" />
		<display:column property="transactionReference" sortable="true"
			sortName="cn.transactionReference" title="Ref" />
		<display:column property="transactionType" sortable="true"
			sortName="cn.transactionType" title="Type" />

		<display:column title="Actions" media="html" style="width:10%">
			<div class="btn-group">
				<button type="button" class="btn btn-primary dropdown-toggle"
					data-toggle="dropdown">
					Action <span class="caret"></span>
				</button>
				<ul class="dropdown-menu" role="menu">
					<li><a
						href="${pageContext.request.contextPath}/creditNote/edit?id=${searchTable.id}&flow=search"
						target="_blank">Edit</a></li>
					<li class="divider"></li>
					<li><a
						href="${pageContext.request.contextPath}/creditNote/delete?id=${searchTable.id}">Delete</a></li>
					<li><a href="${pageContext.request.contextPath}/customerReport/report?customer.id=${searchTable.customer.id}&customerReportType=INVOICE">Statement</a></li>
				</ul>
			</div>
		</display:column>
	</display:table>
</c:if>
