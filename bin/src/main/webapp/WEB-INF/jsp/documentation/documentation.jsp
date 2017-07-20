<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
	<script>
		$(function() {

		});
	</script>

	<style>
		p.breadcrumb {
			font-size: 18px;
			color : red;
		}
		span.bold {
			font-weight: bold;
			color : red;
		}

		button.btn {
			margin-left: 10px;
			margin-right: 10px;
		}
	</style>

<div class="rows">

				<div class="row">

          <div class="column w-100-percent">
							<h1>List of Customer Invoices</h1>
							<p class="breadcrumb">Reports -> Sales -> Select Type 'Invoice List' -> Select Start Date and End Date</p>
							<p>Click Show Report</p>

							<h1>Create Event For Website</h1>
							<p class="breadcrumb">Event -> Add Event</p>
							<p>Make sure <span class="bold">On Website</span> is clicked if event is for website</p>
							<p>Website -> Update Events -> This will sync the latest events with website</p>

							<h1>Stock take</h1>
							<p class="breadcrumb">Stock -> Stock Take</p>
							<p>To start the stock take, click <button type="button" class="btn btn-danger">Reset Stock Take</button></p>
							<p>This will reset record, so only do once at the start of each stock take</p>
							<p>Now scan all isbns into the stock take screen</p>
							<p>Once all isbns of all books are scanned, you transfer the stock take to the actual real record by clicking <button type="button" class="btn btn-danger">Update Stock Record</button>
							<p>Only do the transfer once you are ready as it's hard to reverse</p>
					</div>

				</div>
</div>
