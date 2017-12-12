<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- Fixed navbar -->
<div class="navbar navbar-default navbar-fixed-top" role="navigation">
  <div class="container">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="#">Bookmarks</a>
    </div>
    <div class="navbar-collapse collapse">
      <ul class="nav navbar-nav">

        <!-- Sell -->

        <li class="active"><a href="<c:url value="/sale/sell" />" accesskey="S" title="Sell">Sell</a></li>
        <li><a href="<c:url value="/sale/displayExtras" />" accesskey="E" title="Extra">Extras</a></li>

        <!-- Stock -->

        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown">Stock <b class="caret"></b></a>
          <ul class="dropdown-menu">
			<li><a href="<c:url value="/stock/add" />" title="Add Stock">Add Stock</a></li>
			<li><a href="<c:url value="/stock/displaySearch" />" title="Search Stock">Search Stock</a></li>

			<li class="divider"></li>

			<li><a href="<c:url value="/stockTakeLine/init" />" title="Stock Take">Stock Take</a></li>
			<li><a href="<c:url value="/stockTakeLine/displaySearch" />" title="Search Sale or Returns">Search Stock Take</a></li>

			<li class="divider"></li>

			<li><a href="<c:url value="/supplierDelivery/selectSupplier" />" title="New Delivery">New Delivery</a></li>


			<li class="divider"></li>

			<li><a href="<c:url value="/author/displaySearch" />" title="Invoice">Search Authors</a></li>
			<li><a href="<c:url value="/author/add" />" title="Sale Or Return">Add Author</a></li>
          </ul>
        </li>

        <!-- Events-->

 <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown">Events <b class="caret"></b></a>
          <ul class="dropdown-menu">
      <li><a href="<c:url value="/events/add" />" title="Add Event">Add Event</a></li>
      <li><a href="<c:url value="/events/search" />" title="Search Events">Search Events</a></li>

      <li class="divider"></li>
   <c:if test="${sessionScope.event != null}">
        <li><a href="<c:url value="/events/stopSelling" />" title="Stop Selling"><img src="<c:url value="/resources/images/blue-database1.png" />" />Stop Selling</a></li>
      </c:if>

      <li><a href="<c:url value="/calendar/view" />" title="Calendar">Calendar</a></li>
      <li><a href="<c:url value="/events/downloadSales" />" title="Download Sales">Download Sales</a></li>
        </ul>
        </li>


        <!-- Supplier -->

        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown">Supplier <b class="caret"></b></a>
          <ul class="dropdown-menu">
			<li><a href="<c:url value="/supplier/add" />" title="Add">Add</a></li>
			<li><a href="<c:url value="/supplier/displaySearch" />" title="Search">Search</a></li>
			<li class="divider"></li>
			<li><a href="<c:url value="/supplierDelivery/selectSupplier" />" title="Delivery">Delivery</a></li>
			<li><a href="<c:url value="supplierDelivery/displaySearch" />" title="Search Deliveries">Search Deliveries</a></li>
			<li class="divider"></li>
			<li><a href="<c:url value="/supplierOrderLine/displaySearch" />" title="Search Orders">Search Orders</a></li>
          </ul>
        </li>

        <!-- Publisher -->

        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown">Publisher <b class="caret"></b></a>
          <ul class="dropdown-menu">
			<li><a href="<c:url value="/publisher/add" />" title="Add">Add</a></li>
			<li><a href="<c:url value="/publisher/displaySearch" />" title="Search">Search</a></li>
          </ul>
        </li>

        <!-- Admin -->

        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown">Admin <b class="caret"></b></a>
          <ul class="dropdown-menu">
      			<li><a href="<c:url value="/category/add" />" title="Add Category">Add Category</a></li>
      			<li><a href="<c:url value="/category/displaySearch" />" title="Search Categories">Search Categories</a></li>

        <li class="divider"></li>

        <li><a href="<c:url value="/staff/add" />" title="Add Staff">Add Staff</a></li>
        <li><a href="<c:url value="/staff/displaySearch" />" title="Search Staff">Search Staff</a></li>

          </ul>
        </li>

        <!-- Reports -->

        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown">Reports <b class="caret"></b></a>
          <ul class="dropdown-menu">

			<li><a href="<c:url value="/saleReport/init" />" title="Add">Sale Report</a></li>
			<li><a href="<c:url value="/customerReport/init" />" title="Search">Customer Report</a></li>
          </ul>
        </li>

        <!-- Customer -->

        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown">Customer <b class="caret"></b></a>
          <ul class="dropdown-menu">

			<li><a href="<c:url value="/customer/add" />" title="Add">Add</a></li>
			<li><a href="<c:url value="/customer/displaySearch" />" title="Search">Search</a></li>
			<li class="divider"></li>
			<li><a href="<c:url value="/customerOrderLine/displaySearch" />" title="Search Customer Orders">Search Customer Orders</a></li>
			<li><a href="<c:url value="/invoice/displaySearch" />" title="Search Invoices">Search Invoices</a></li>
			<li><a href="<c:url value="/saleOrReturn/displaySearch" />" title="Search Sale or Returns">Search Sale or Returns</a></li>
			<c:if test="${sessionScope.invoice != null}">
				<li><a href="<c:url value="/invoice/continue" />" title="Invoice">Invoice</a></li>
			</c:if>
			<c:if test="${sessionScope.saleOrReturn != null}">
				<li><a href="<c:url value="/saleOrReturn/continue" />" title="Sale Or Return">Sale Or Return</a></li>
			</c:if>
			<c:if test="${sessionScope.customerOrder != null}">
				<li><a href="<c:url value="/customerOrder/continue" />" title="Customer Order">Customer Order</a></li>
			</c:if>
          </ul>
        </li>

        <!-- Website -->

        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown">Website <b class="caret"></b></a>
          <ul class="dropdown-menu">
			<li><a href="<c:url value="/bouncies/manage" />" title="Manage Bouncies">Manage Bouncies</a></li>
			<li><a href="<c:url value="/stickies/displayStockItemTypes" />" title="Manage Stickies">Manage Stickies</a></li>
			<li class="divider"></li>

			<li><a href="<c:url value="/readingList/add" />" title="Add Reading List">Add Reading List</a></li>
			<li><a href="<c:url value="/readingList/displaySearch" />" title="Search Reading List">Search Reading List</a></li>
			<li class="divider"></li>

      <li><a href="<c:url value="/mailchimp/subscribe" />" title="Mailchimp subscribe">Mailchimp Subscribe</a></li>

			<li><a onclick='javascript:authoriseUser("${pageContext.request.contextPath}/chips/updateChips")' title="Update Chips">Update Chips</a></li>
			<li><a href="<c:url value="/chips/getOrders" />" title="Get Orders">Get Orders</a></li>
			<li class="divider"></li>
          </ul>
        </li>
      </ul>

    </div><!--/.nav-collapse -->
  </div>
</div>
