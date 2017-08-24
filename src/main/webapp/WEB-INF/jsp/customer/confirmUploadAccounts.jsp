<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<script>
	$(function() {
		$("#customerAutoComplete").autocomplete( {
			source: "${pageContext.request.contextPath}/customer/autoCompleteSurname?accountHolders=true",
			minLength: 3,
			select: function( event, ui ) {
				$("#customerAutoComplete").val(ui.item.label);
				$("#customerId").val(ui.item.value);
				$("#searchCustomerSubmitButton").click();
				return false;
			},
			focus: function( event, ui ) {
				$("#customerAutoComplete").val(ui.item.label);
				return false;
			}
		});
		$('#customerId').val(''); //clear customer id
		$('#customerAutoComplete').focus();
	});

	function match( transactionDescription ) {
		var customerId = $('#customerId').val();
		if(customerId == "") {
			alert("Please select customer first")
			return
		}
		//alert( customerId + " " + transactionDescription);
		window.location.href = "/bookmarks/tsb/match?customerId=" + customerId + "&transactionDescription=" + encodeURI( transactionDescription );
	}

</script>
<h1>Upload bank credits from TSB</h1>

		<div class="alert alert-error">
			<button type="button" class="close" data-dismiss="alert">�</button>
			<ul>
				<li>Uploaded ${noOfLines} lines from csv files</li>
				<li>Found ${noOfCreditNotes} credit Notes</li>
				<li>${noOfAlreadyProcessed} are already processed</li>
				<li>${noOfUnmatched} are unmatched</li>
				<li>${noOfMatched} are matched</li>
				<li>${noOfClubAccountsnoOfClubAccountsUnProcessed} are club accounts</li>
			</ul>
			<br />
		</div>

<input type="hidden" id="customerId"/>

    <div class="rows">
    				<div class="demo">
    							<label>Customer To Match</label>
    							<input type="text" id="customerAutoComplete"/>
    				</div>
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
