<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<div class="rows">
		<div class="row">
          <div class="column w-33-percent">
			<label>Title</label><br/>
			${staff.name}
	  	 </div>
		  <div class="column w-33-percent">
		          <label>Email</label><br/>
		          ${staff.email}
	  	</div>
          <div class="column w-33-percent">
                   <label>Telephone</label><br/>
		          ${staff.telephone}
	 	 </div>
      </div>
</div>
