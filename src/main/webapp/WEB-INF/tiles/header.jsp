<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script>
	$(function() {
		$("#globalCustomerAutoComplete").autocomplete( {
			source: "${pageContext.request.contextPath}/customer/autoCompleteSurname",
			minLength: 3,
			select: function( event, ui ) {
				$("#globalCustomerAutoComplete").val(ui.item.label);
				$("#globalCustomerId").val(ui.item.value);
				$("#globalCustomerForm").submit();
				return false;
			},
			focus: function( event, ui ) {
				$("#globalCustomerAutoComplete").val(ui.item.label);
				return false;
			}
		});
		$('#globalCustomerId').val(''); //clear customer id
		$('#globalCustomerAutoComplete').focus();
	});

</script>
<ul id="css3menu1" class="topmenu">
	<li class="topfirst"><a href="<c:url value="/sale/sell" />" accesskey="S" title="Sell" style="height:16px;line-height:16px;">Sell</a>
		<ul>
			<li class="subfirst"><a href="<c:url value="/sale/reset" />"  accessKey="R" title="Reset"><img src="<c:url value="/resources/images/blue-health.png" />" alt="Reset"/>Reset</a></li>
			<li><a href="<c:url value="/sale/displayExtras" />" title="Extras"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Extras"/>Extras</a></li>
		</ul>
	</li>
	<li class="topmenu"><a href="<c:url value="/customer/displaySearch" />" accessKey="C" title="Customer" style="height:16px;line-height:16px;"><span>Customer</span></a>
		<ul>
			<li class="subfirst"><a href="<c:url value="/customer/add" />" title="Add"><img src="<c:url value="/resources/images/blue-health.png" />" alt="Add"/>Add</a></li>
			<li><a href="<c:url value="/customer/displaySearch" />" title="Search"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Search"/>Search</a></li>
			<li><a href="<c:url value="/customerOrderLine/displaySearch" />" title="Search Customer Orders"><img src="<c:url value="/resources/images/blue-database1.png" />" alt="Search Customer Orders"/>Search Customer Orders</a></li>
			<li><a href="<c:url value="/invoice/displaySearch" />" title="Search Invoices"><img src="<c:url value="/resources/images/invoiceLarge.png" />" alt="Search Invoices"/>Search Invoices</a></li>
			<li><a href="<c:url value="/saleOrReturn/displaySearch" />" title="Search Sale or Returns"><img src="<c:url value="/resources/images/saleOrReturnLarge.png" />" alt="Search Sale Or Returns"/>Search Sale or Returns</a></li>
			<li><a href="<c:url value="/customer/merge" />" title="Merge Customer"><img src="<c:url value="/resources/images/saleOrReturnLarge.png" />" alt="Merge Customers"/>Merge Customers</a></li>
			<li><a href="<c:url value="/creditNote/displaySearch" />" title="Accounts"><img src="<c:url value="/resources/images/saleOrReturnLarge.png" />" alt="Accounts"/>Accounts</a></li>
			<li><a href="<c:url value="/tsb/uploadAccountsFromTSB" />" title="Upload Accounts From TSB"><img src="<c:url value="/resources/images/saleOrReturnLarge.png" />" alt="Upload Accounts From TSB"/>Upload Accounts From TSB</a></li>
			<li><a href="<c:url value="/vtTransaction/displaySearch" />" title="VT Transactions"><img src="<c:url value="/resources/images/saleOrReturnLarge.png" />" alt="VT Transactions"/>VT Transactions</a></li>
		<c:if test="${sessionScope.invoice != null}">
			<li><a href="<c:url value="/invoice/continue" />" title="Invoice"><img src="<c:url value="/resources/images/blue-database1.png" />" />Invoice</a></li>
		</c:if>
		<c:if test="${sessionScope.saleOrReturn != null}">
			<li><a href="<c:url value="/saleOrReturn/continue" />" title="Sale Or Return"><img src="<c:url value="/resources/images/blue-database1.png" />" />Sale Or Return</a></li>
		</c:if>
		<c:if test="${sessionScope.customerOrder != null}">
			<li><a href="<c:url value="/customerOrder/continue" />" title="Customer Order"><img src="<c:url value="/resources/images/blue-database1.png" />" />Customer Order</a></li>
		</c:if>
		</ul>
	</li>
	<li class="topmenu"><a href="<c:url value="/supplier/displaySearch" />" accessKey="U" title="Supplier" style="height:16px;line-height:16px;">Supplier</a>
		<ul>
			<li class="subfirst"><a href="<c:url value="/supplier/add" />" title="Add Supplier"><img src="<c:url value="/resources/images/blue-health.png" />" alt="Add"/>Add Supplier</a></li>
			<li><a href="<c:url value="/supplier/displaySearch" />" title="Search Supplier"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Search"/>Search Suppliers</a></li>
			<li><a href="<c:url value="/supplierOrderLine/displaySearch" />" title="Supplier Order Lines"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Search"/>Supplier Order Lines</a></li>
			<li><a href="<c:url value="/supplierDelivery/displaySearch" />" title="Search  Supplier Deliveries"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Search"/>Search  Supplier Deliveries</a></li>
			<li><a href="<c:url value="/supplierReturn/displaySearch" />" title="Search  Supplier Returns"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Search"/>Search Supplier Returns</a></li>
			<li><a href="<c:url value="/reorderReview/init" />" title="Reorder Review"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Search"/>Reorder Review</a></li>
			<li><a href="<c:url value="/publisher/add" />" title="Add"><img src="<c:url value="/resources/images/blue-health.png" />" alt="Add Publisher"/>Add Publisher</a></li>
			<li><a href="<c:url value="/publisher/displaySearch" />" title="Search Publisher"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Search"/>Search Publishers</a></li>
		</ul>
	<li class="topmenu"><a href="<c:url value="/stock/displaySearch" />" accessKey="T" title="Stock" style="height:16px;line-height:16px;"><span>Stock</span></a>
		<ul>
			<li class="subfirst"><a href="<c:url value="/stock/add" />" title="Add Stock"><img src="<c:url value="/resources/images/blue-health.png" />" alt="Add"/>Add</a></li>
			<li><a href="<c:url value="/stock/displaySearch" />" title="Search Stock"><img src="<c:url value="/resources/images/blue-sitemap2.png" />" alt="Search"/>Search</a></li>
			<li><a href="<c:url value="/supplierDelivery/selectSupplier" />" title="Supplier Delivery"><img src="<c:url value="/resources/images/blue-sitemap2.png" />" alt="Search"/>Supplier Delivery</a></li>
			<li><a href="<c:url value="/stockTakeLine/init" />" title="Stock Take"><img src="<c:url value="/resources/images/blue-sitemap2.png" />" alt="Stock Take"/>Stock Take</a></li>
			<li><a href="<c:url value="/stockTakeLine/displaySearch" />" title="Search Stock Take"><img src="<c:url value="/resources/images/blue-sitemap2.png" />" alt="Search Stock Take"/>Search Stock Take</a></li>
			<li><a href="<c:url value="/author/displaySearch" />" title="Search Authors"><img src="<c:url value="/resources/images/blue-sitemap2.png" />" alt="Search Authors"/>Search Authors</a></li>
			<li><a href="<c:url value="/author/add" />" title="Add Author"><img src="<c:url value="/resources/images/blue-sitemap2.png" />" alt="Add Author"/>Add Author</a></li>
			<li><a href="<c:url value="/supplierReturn/selectSupplier" />" title="Supplier Return"><img src="<c:url value="/resources/images/blue-sitemap2.png" />" alt="Search"/>Supplier Return</a></li>

		</ul>
	</li>
	<li class="topmenu"><a href="<c:url value="/saleReport/init" />" accessKey="X" title="Reports" style="height:16px;line-height:16px;"><span>Reports</span></a>
		<ul>
			<li class="subfirst"><a href="<c:url value="/saleReport/init" />" title="Sale Report"><img src="<c:url value="/resources/images/blue-stats 3.png" />" alt="Sales"/>Sales</a></li>
			<li><a href="<c:url value="/customerReport/init" />" title="Customer Report"><img src="<c:url value="/resources/images/blue-sitemap2.png" />" alt="Customer"/>Customers</a></li>
		</ul>
	</li>
	<li class="topmenu"><a href="<c:url value="/events/search" />" accessKey="V" title="Events" style="height:16px;line-height:16px;">Events</a>
		<ul>
			<li class="subfirst"><a href="<c:url value="/events/add" />" title="Add"><img src="<c:url value="/resources/images/blue-health.png" />" alt="Add"/>Add</a></li>
			<li><a href="<c:url value="/events/search" />" title="Search Event"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Search"/>Search</a></li>
			<c:if test="${sessionScope.event != null}">
				<li><a href="<c:url value="/events/stopSelling" />" title="Stop Selling"><img src="<c:url value="/resources/images/blue-database1.png" />" />Stop Selling</a></li>
			</c:if>
			<li><a href="<c:url value="/calendar/view" />" title="Calendar"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Calendar"/>Calendar</a></li>
			<li><a href="<c:url value="/events/downloadSales" />" title="Download Sales"><img src="<c:url value="/resources/images/blue-health.png" />" alt="Download Sales"/>Download Sales</a></li>
		</ul>
	</li>
	<li class="topmenu"><a href="<c:url value="/bouncies/manage" />" accessKey="W" title="Website" style="height:16px;line-height:16px;">Website</a>
		<ul>
			<li><a href="<c:url value="/category/add" />" title="Add Category"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Add Category"/>Add Category</a></li>
			<li><a href="<c:url value="/category/displaySearch" />" title="Search Category"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Search Category"/>Search Category</a></li>
			<li><a href="<c:url value="/readingList/add" />" title="Add Reading List"><img src="<c:url value="/resources/images/blue-sitemap2.png" />" alt="Add Reading List"/>Add Reading List</a></li>
			<li><a href="<c:url value="/readingList/displaySearch" />" title="Search Reading List"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Search Reading List"/>Search Reading List</a></li>
			<li><a href="<c:url value="/stickies/displayStockItemTypes" />" title="Manage Stickies"><img src="<c:url value="/resources/images/blue-health.png" />" alt="Stickies"/>Manage Stickies</a></li>
			<li><a href="<c:url value="/bouncies/manage" />" title="Manage Bouncies"><img src="<c:url value="/resources/images/blue-database1.png" />" alt="Bouncies"/>Manage Bouncies</a></li>
			<li><a href="<c:url value="/bounciesMerchandise/manage" />" title="Manage Merchandise"><img src="<c:url value="/resources/images/blue-database1.png" />" alt="Merchandise"/>Manage Merchandise</a></li>
			<li><a href="<c:url value="/chips/getOrders" />" title="Get Chips Orders"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Send Email Report"/>Get Chips Orders</a></li>
			<li><a onclick='javascript:authoriseUser("${pageContext.request.contextPath}/chips/updateChips")' title="Update Chips"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Update Chips"/>Update Chips</a></li>
			<li><a onclick='javascript:authoriseUser("${pageContext.request.contextPath}/chips/updateEvents")' title="Update Events"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Update Events"/>Update Events</a></li>
			<li><a onclick='javascript:authoriseUser("${pageContext.request.contextPath}/chips/uploadBrochure")' title="Upload Brochure"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Upload Brochure"/>Upload Brochure</a></li>
			<li><a onclick='javascript:authoriseUser("${pageContext.request.contextPath}/chips/updateReadingLists")' title="Update Reading Lists"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Update Reading Lists"/>Update Reading Lists</a></li>
			<li><a onclick='javascript:authoriseUser("${pageContext.request.contextPath}/chips/buildIndex")'" title="Build Index"><img src="<c:url value="/resources/images/blue-sitemap1.png" />" alt="Build Index"/>Build Index</a></li>
		</ul>
	</li>
</ul>
<!--

<div id="globalCustomerAutoCompleteWrapper">
<input type="text" id="globalCustomerAutoComplete"/>

<form id="globalCustomerForm" action="${pageContext.request.contextPath}/customer/search"s>

<input type="hidden" name="customer.id" accesskey="M" id="globalCustomerId"/>
</form>
</div>
-->
