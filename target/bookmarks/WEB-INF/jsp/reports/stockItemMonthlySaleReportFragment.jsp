<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<script>
	function showYear(year) {
		$('.hide_it').hide();
		$('#sales' + year).show();
	}
	
	$(function() {
		showYear(${monthlySaleReportBean.currentYear});
	});

</script>
<div class="rows">
	<div class="row">
		<c:forEach items="${monthlySaleReportBean.sales}" var="sales">
			<span class="yearly-sales">
				<a href="javascript:showYear('${sales.year}')"><h1>${sales.year}</h1></a>
				<strong>${sales.salesList[12]}</strong>
			</span>
		</c:forEach>
	</div>
	
<c:forEach items="${monthlySaleReportBean.sales}" var="sales">	
   <div class="row hide_it" id="sales${sales.year}">
        <div class="column w-8-percent">
			<label>Jan</label><br/>
			${sales.salesList[0]}
		</div>   
        <div class="column w-8-percent">
			<label>Feb</label><br/>
			${sales.salesList[1]}
		</div>
        <div class="column w-8-percent">
			<label>Mar</label><br/>
			${sales.salesList[2]}
		</div>	
        <div class="column w-8-percent">
			<label>Apr</label><br/>
			${sales.salesList[3]}
		</div>			
        <div class="column w-8-percent">
			<label>May</label><br/>
			${sales.salesList[4]}
		</div>			
        <div class="column w-8-percent">
			<label>Jun</label><br/>
			${sales.salesList[5]}
		</div>			
        <div class="column w-8-percent">
			<label>Jul</label><br/>
			${sales.salesList[6]}
		</div>	
        <div class="column w-8-percent">
			<label>Aug</label><br/>
			${sales.salesList[7]}
		</div>			
        <div class="column w-8-percent">
			<label>Sep</label><br/>
			${sales.salesList[8]}
		</div>	
		<div class="column w-8-percent">
			<label>Oct</label><br/>
			${sales.salesList[9]}
		</div>	
        <div class="column w-8-percent">
			<label>Nov</label><br/>
			${sales.salesList[10]}
		</div>
        <div class="column w-8-percent">
			<label>Dec</label><br/>
			${sales.salesList[11]}
		</div>		
	</div><!-- /row -->

</c:forEach>	
	
</div>
<div class="rows">
   <div class="row">
          <div class="column w-33-percent">
			<strong>Last Sales</strong><br/>
				<c:forEach var="sale" items="${monthlySaleReportBean.lastSales}">
					<fmt:formatDate pattern="dd-MMM" value="${sale.creationDate}" />&nbsp;
					<c:if test="${sale.event != null}"> ${sale.event.name}</c:if>
					<c:if test="${sale.event == null}"> Shop</c:if>
					<br/>
				</c:forEach>
		   </div>	
     		<div class="column w-33-percent">

		  </div>	
     		<div class="column w-33-percent">
			<strong>Last Delivery</strong><br/>
			<c:if test="${monthlySaleReportBean.lastSupplierDeliveryLine == null}">
				None
			</c:if>
			<c:if test="${monthlySaleReportBean.lastSupplierDeliveryLine != null}">
				<fmt:formatDate pattern="dd-MMM-yy" value="${monthlySaleReportBean.lastSupplierDeliveryLine.creationDate}" />
				(${monthlySaleReportBean.lastSupplierDeliveryLine.amount} Delivered from ${monthlySaleReportBean.lastSupplierDelivery.supplier.name})
			</c:if>	
		</div>

	</div>
</div>