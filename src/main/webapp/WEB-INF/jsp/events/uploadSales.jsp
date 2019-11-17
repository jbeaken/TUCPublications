<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<h1>Upload text file generated by mini-beans for event ${event.name}</h1>
<form:form
    modelAttribute="event"
    action="/events/uploadSales"
    method="post"
    enctype="multipart/form-data">

    <input type="hidden" value="${event.id}" name="id"/>
    <input type="hidden" value="${event.name}" name="name"/>

  <div class="rows">

      <div class="row">
            <div class="column w-33-percent">
          <form:label	for="file" path="file">CSV file To Upload</form:label><br/>
          <form:input path="file" type="file" required="required" />
         </div>

         <br/><br/>

      </div>

      <div class="row">
          <div class="column w-33-percent">
         <form:checkbox path="skipUpdatingStockRecord"/>
         Click here to record sales, but not change stock record
      </div>

        <div class="row">
            <div class="column w-33-percent">
           <input type="submit" class="btn btn-primary" value="Upload sales"/>
        </div>
    </div>

  </div>
</form:form>
