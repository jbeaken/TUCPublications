<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<div class="rows">
		<div class="row">	
          <div class="column w-33-percent">
			<label>Title</label><br/>        
			${event.name}
	  	 </div>
		  <div class="column w-33-percent">
		          <label>Start Date</label><br/>
		          <fmt:formatDate pattern="dd/MM/yyyy" value="${event.startDate}"/>
	  	</div>		
          <div class="column w-33-percent">
                   <label>End Date</label><br/>
		          <fmt:formatDate pattern="dd/MM/yyyy" value="${event.endDate}"/>
	 	 </div>		  	
      </div>
</div>


