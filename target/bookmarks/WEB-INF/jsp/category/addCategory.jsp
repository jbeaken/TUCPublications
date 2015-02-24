<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<form:form modelAttribute="category" action="add?flow=${flow}" method="post">
<div class="rows">
        <div class="row">
          <div class="column w-33-percent">
			       <form:label for="name" path="name" cssErrorClass="error">Category Name</form:label><br/>
			       <form:input path="name" id="focus"/> <form:errors path="name" />				
	  	    </div>
          <div class="column w-33-percent">
             <form:label for="parent" path="parent" cssErrorClass="error">Parent Category</form:label><br/>
            <form:select path="parent.id">
                  <form:option value="" label="-select-" />
                  <form:options items="${categoryList}" itemValue="id" itemLabel="name"/>
                </form:select>       
          </div>    
	<div class="column w-33-percent">
			<form:checkbox path="isOnWebsite" />
               <form:label for="isOnWebsite" path="isOnWebsite" cssErrorClass="error">Put Stock Item On Website</form:label>
               <br/>  
				<form:checkbox path="isInSidebar" />
               <form:label for="isInSidebar" path="isInSidebar" cssErrorClass="error">Is In Website Sidebar</form:label>
               <br/>  				
	  	</div>	               
      </div>                
      </div>

      <div class="row">
          <div class="column w-70-percent">
 				<input type="submit" class="btn btn-primary" value="Add Category"/> 
 				<button type="button" class="btn btn-primary" onclick="javascript:reset()">Reset</button>
	 	 </div>
	</div>		
</div>		
</form:form>