<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
	<script type="text/javascript" src="<c:url value="/resources/js/jquery-1.5.1.min.js" />" ></script>
	<script type="text/javascript" src="<c:url value="/resources/js/jquery-ui-1.8.14.custom.min.js" />"></script>
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/ui-lightness/jquery-ui-1.8.14.custom.css" />" />	
	<link rel="stylesheet" href="<c:url value="/resources/css/bookmarks.css" />" type="text/css" />
	<link rel="stylesheet" href="<c:url value="/resources/css/style.css" />" type="text/css" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/displaytag.css" />" type="text/css" />
	    
    <script type="text/javascript">
    window.print();
    </script>
    
    </head>
	<body>
        <table cellpadding="2" cellspacing="2" align="center">
            <tr>
                <td style="height:20px;">
                </td>
            </tr>
            <tr>
                <td>
                    <tiles:insertAttribute name="body" />
                </td>
            </tr>
        </table>
    </body>
</html>
