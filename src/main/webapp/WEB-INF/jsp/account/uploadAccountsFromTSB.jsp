<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<h1>Upload CSV file from TSB</h1>


<form:form action="${pageContext.request.contextPath}/tsb/uploadCSVFiles"
    method="post"
    enctype="multipart/form-data">    

  
  <div class="rows">
 
      <div class="row">

        <div class="column w-33-percent">
         <label>Files to upload</label>
         <br/>
         <input type="file" required="required" multiple="multiple" name="csvFiles" />
        </div>

         <br/><br/>

      </div>

        <div class="row">
            <div class="column w-33-percent">
           <input type="submit" class="btn btn-primary" value="Upload CSV Files"/>
        </div>
    </div>

  </div>
</form:form>
