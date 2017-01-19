<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<script>
	$(function() {
		$("#customerAutoComplete").autocomplete( {
			source: "${pageContext.request.contextPath}/customer/autoCompleteSurname",
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

	function match(details) {
		 var customerId = $('#customerId').val();
			alert( customerId + " " + details);
      window.location.href = "/bookmarks/customer/match?customerId=" + customerId + "&details=" + encodeURI(details);
	}
</script>
<h1>Upload bank credits from TSB</h1>
<input type="hidden" id="customerId"/>

    <div class="rows">
            <div class="row">
              <div class="column w-33-percent">
    					<div class="demo">
    						<div class="ui-widget">
    							<label>Customer</label>
    							<input type="text" id="customerAutoComplete"/>
    						</div>
    					</div>
    		</div>
      </div>

         <display:table name="sessionScope.creditNoteMap"
            requestURI="search"
            decorator="org.bookmarks.ui.SearchCustomersDecorator"
            export="true"
            size="${sessionScope.creditNoteList.size()}"
            id="searchTable">
                    <display:setProperty name="export.pdf" value="true" />
                    <display:setProperty name="export.xml" value="false" />
                    <display:setProperty name="export.pdf.filename" value="customer.pdf"/>
                    <display:setProperty name="export.csv.filename" value="customer.txt"/>
                    <display:column property="date" title="Date" />
                    <display:column property="amount" title="Amount" />
                    <display:column property="details" title="Match"/>
										<display:column property="transactionReference" title="Reference"/>
                    <display:column property="customer.fullName" title="Customer"/>
                    <display:column>
                          <button onclick="javascript:match('${searchTable.details}')" class="btn btn-danger">Match</button>
                   </display:column>
          </display:table>

  </div>

      <div class="rows">
            <div class="row">
              <div class="column w-33-percent">
                  <a href="/bookmarks/customer/saveAccountsFromTSB" class="btn btn-danger">Save</a>
              </div>
        </div>
      </div>
