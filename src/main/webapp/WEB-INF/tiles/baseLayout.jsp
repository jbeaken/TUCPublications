<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
	<script type="text/javascript" src="<c:url value="/resources/js/jquery-1.11.1.min.js" />" ></script>
	<script type="text/javascript" src="<c:url value="/resources/js/jquery-ui.min.js" />"></script>
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/jquery-ui.min.css" />" />
	<link rel="stylesheet" href="<c:url value="/resources/css/bookmarks.css" />" type="text/css" />
	<link rel="stylesheet" href="<c:url value="/resources/css/css3style.css" />" type="text/css" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/displaytag.css" />" type="text/css" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/font-awesome.css" />" type="text/css" />
	<script type="text/javascript" src="<c:url value="/resources/js/chosen.jquery.min.js" />" ></script>
	<script type="text/javascript" src="<c:url value="/resources/js/bookmarks.js" />" ></script>
	<script type="text/javascript" src="<c:url value="/resources/js/bootstrap.js" />" ></script>
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/chosen.css" />" />
<style type="text/css">
body {
}
td.title {
  font-size : 120%;
}
ul#css3menu1 a {
	background-color:${requestScope.menuColour};
}
ul#css3menu1 li:hover>a,ul#css3menu1 li>a.pressed{
	background-color:${requestScope.menuHoverColour};
}
ul#css3menu1 li:hover>a{
	background-color:${requestScope.menuHoverColour};
}
ss3menu1 ul li:hover>a,ul#css3menu1 ul li>a.pressed{
	color:${requestScope.menuColour};
}
thead tr {
	background-color: ${requestScope.menuColour};
}
th.sorted {
	background-color: ${requestScope.menuColour};
}
tr.tableRowEven, tr.even {
	background-color: ${requestScope.tableRowEven};
}
.button {
	background:url(../resources//images/${requestScope.buttonImage}.gif);
}
h1 {
	color: ${requestScope.menuColour};
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
		function confirmationWithUrl(url, text) {
			var c = confirm(text);
			if(c == true) {
				window.location.href = url
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
        <table cellpadding="2" cellspacing="2" align="center" width="100%">
            <tr>
                <td>
                    <tiles:insertAttribute name="header" />
                </td>
            </tr>
            <c:if test="${info != null}">
	            <tr>
	            	<td>
		            	<div class="alert alert-info" style="width:400px">
		              		<button type="button" class="close" data-dismiss="alert">�</button>
		              		<h4>Information</h4>
		              		<p>${info}</p>
		            	</div>
	            	</td>
	            </tr>
            </c:if>
            <c:if test="${warning != null}">
	            <tr>
	            	<td>
		            	<div class="alert alert-block" style="width:400px">
		              		<button type="button" class="close" data-dismiss="alert">�</button>
		              		<h4>Warning!</h4>
		              		<p>${warning}</p>
		            	</div>
	            	</td>
	            </tr>
            </c:if>
            <c:if test="${error != null}">
	            <tr>
	            	<td>
		            	<div class="alert alert-error" style="width:400px">
		              		<button type="button" class="close" data-dismiss="alert">�</button>
		              		<h4>Error!</h4>
		              		<p>${error}</p>
		            	</div>
	            	</td>
	            </tr>
            </c:if>
            <c:if test="${success != null}">
	            <tr>
	            	<td>
		            	<div class="alert alert-success" style="width:400px">
		              		<button type="button" class="close" data-dismiss="alert">�</button>
		              		<h4>Success!</h4>
		              		<p>${success}</p>
		            	</div>
	            	</td>
	            </tr>
            </c:if>
            <tr>
                <td>
                    <tiles:insertAttribute name="body" />
                </td>
            </tr>
            <tr>
                <td>
                    <tiles:insertAttribute name="footer" />
                </td>
            </tr>            
        </table>
    </body>
</html>
