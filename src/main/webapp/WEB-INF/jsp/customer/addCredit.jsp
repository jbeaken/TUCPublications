<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
		<form:form modelAttribute="creditNote" action="addCredit" method="post">
<div class="rows">
      <div class="row">
          <div class="column w-100-percent">
          		<form:hidden path="customer.id"/>	
               <form:label for="text" path="amount" cssErrorClass="error">Amount</form:label><br/>
	           <form:input path="amount"/> <form:errors path="amount" />                  
	 	 </div>
	</div>
      <div class="row">
          <div class="column w-100-percent">
	           <form:label for="text" path="note" cssErrorClass="error">Note</form:label><br/>
	           <form:textarea path="note" cols="50" rows="20"/> <form:errors path="note" />                  
	 	 </div>
	</div>	
      <div class="row">
		  <div class="column w-100-percent">
		  	<input type="submit" class="btn btn-primary"/>
		  </div>
	</div>
</div>		
</form:form>