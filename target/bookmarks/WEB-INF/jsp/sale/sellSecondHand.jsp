<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<form:form modelAttribute="sale" action="/sale/sellSecondHand" method="post">
	<form:hidden path="creationDate"/>
	<form:hidden path="quantity"/>
	<form:hidden path="vat"/>
	<form:hidden path="event.id"/>
	<form:hidden path="stockItem.id"/>
	<form:hidden path="stockItem.title"/>
	<div class="rows">
		<div class="row">
			<div class="column w-33-percent">
				<form:label	for="sellPrice" path="sellPrice" cssErrorClass="error">Price</form:label><br/>
				<form:input path="sellPrice" autofocus="autofocus" required="required" />			
			</div>
			<div class="column w-33-percent">
				<form:label for="discount" path="discount" cssErrorClass="error">Discount</form:label><br/>
				<form:input path="discount" required="required" />
			</div>		
			<div class="column w-33-percent">
				<br/>
				<input type="submit" class="btn btn-primary" id="sellSecondHandSubmitButton"/>
			</div>
		</div>
	</div>
</form:form>


