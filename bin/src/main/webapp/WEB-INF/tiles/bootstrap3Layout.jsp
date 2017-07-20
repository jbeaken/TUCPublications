<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css">

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
 
<link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/themes/smoothness/jquery-ui.css" />

<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>

<!-- Application javascript -->
<script type="text/javascript" src="<c:url value="/resources/js/bookmarks.js" />" ></script>

<!-- Latest compiled and minified JavaScript -->
<script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script> 
  <style>
	  body {
	  	min-height: 2000px;
	  	padding-top: 70px;
		}
  </style> 
	<script type="text/javascript">
	$(function() {
			<c:if test="${message != null}">
				alert("${message}");
			</c:if>
			<c:if test="${closeWindow != null}">
				window.opener.document.forms[0].submit();
				window.close();
			</c:if>
			<c:if test="${closeWindowWithFormSubmit != null}">
				window.opener.document.forms[0].submit();
				window.close();
			</c:if>
			<c:if test="${addPublisherToPublisherSelect != null}">
				window.opener.$('#publisher.id').append('<option value="${publisherId}">${publisherName}</option');
				window.close();
			</c:if>
			<c:if test="${closeWindowNoRefresh != null}">
				window.close();
			</c:if>
			<c:if test="${closeWindowWithRefresh != null}">
				window.opener.location.reload();
				window.close();
			</c:if>
			<c:if test="${focusId != null}">
				$('#${focusId}').focus();
			</c:if>
			<c:if test="${focusId == null}">
				$('#focus').focus();
			</c:if>	
	});
		function submitForm(action) {
			document.forms[0].action = action;
			document.forms[0].submit();
		}
		function confirmation(action, text) {
			var c = confirm(text);
			if(c == true) {
				submitForm(action);
			}
		}
		function confirmDeletion(action) {
			confirmation(action, "Are you sure you want to delete? Cannot be undone!");
		}
	</script>    
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><tiles:insertAttribute name="title" ignore="true" /></title>
    </head>
    
	<body>
		<tiles:insertAttribute name="navbar" />
   
		<div class="container">
            <c:if test="${info != null}">
            	<div class="alert alert-info">
              		<button type="button" class="close" data-dismiss="alert">×</button>
              		<h4>Information</h4>
              		<p>${info}</p>
            	</div>
            </c:if>
            <c:if test="${warning != null}">
            	<div class="alert alert-warning">
              		<button type="button" class="close" data-dismiss="alert">×</button>
              		<h4>Warning!</h4>
              		<p>${warning}</p>
            	</div>
            </c:if>
            <c:if test="${error != null}">
            	<div class="alert alert-danger">
              		<button type="button" class="close" data-dismiss="alert">×</button>
              		<h4>Error!</h4>
              		<p>${error}</p>
            	</div>
            </c:if>
            <c:if test="${success != null}">
            	<div class="alert alert-success">
              		<button type="button" class="close" data-dismiss="alert">×</button>
              		<h4>Success!</h4>
              		<p>${success}</p>
            	</div>
            </c:if>		
			<tiles:insertAttribute name="body" />
		</div>
		
    </body>
</html>
