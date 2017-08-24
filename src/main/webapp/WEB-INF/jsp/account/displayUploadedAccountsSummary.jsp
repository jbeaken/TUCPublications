<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<script>
	$(function() {

	});
</script>
<h1>Summary of Uploaded Accounts</h1>

		<div class="alert alert-info">
			<button type="button" class="close" data-dismiss="alert">�</button>
			<h2>Success!</h2>
			Have credited the following accounts :
			<br/>
		</div>

         <display:table name="creditNoteList"
            requestURI="search"
            decorator="org.bookmarks.ui.SearchCustomersDecorator"
            export="true"
            size="${creditNoteList.size()}"
            id="searchTable">
                    <display:setProperty name="export.pdf" value="true" />
                    <display:setProperty name="export.xml" value="false" />
                    <display:setProperty name="export.pdf.filename" value="customer.pdf"/>
                    <display:setProperty name="export.csv.filename" value="customer.txt"/>
                    <display:column property="date" title="Date" />
										<display:column property="status" title="Status" />
                    <display:column property="amount" title="Amount" />
                    <display:column property="transactionDescription" title="Description"/>
										<display:column property="transactionReference" title="Reference"/>
                    <display:column property="customer.fullName" title="Customer"/>
                    <display:column>
<div class="btn-group">
              <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
                Action <span class="caret"></span>
              </button>
               <ul class="dropdown-menu" role="menu">
               <c:if test="${searchTable.clubAccount == false}">
                <li>
                  <a onclick="javascript:match('${searchTable.transactionReference}')">Match</a>
                </li>
                </c:if>
               </ul>

                   </display:column>
          </display:table>

  </div>

      <div class="rows">
            <div class="row">
              <div class="column w-33-percent">
                  <a href="/bookmarks/tsb/saveAccountsFromTSB?credit=true" class="btn btn-danger">Save And Credit</a>
              </div>
              <div class="column w-33-percent">
                  <a href="/bookmarks/tsb/saveAccountsFromTSB?credit=false" class="btn btn-primary">Save Without Crediting</a>
              </div>
        </div>
      </div>
