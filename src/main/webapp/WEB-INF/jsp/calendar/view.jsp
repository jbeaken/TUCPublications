<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<head>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/fullcalendar.css" />" />
<script type="text/javascript" src="<c:url value="/resources/js/moment.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/fullcalendar.js" />"></script>
<script>
$(document).ready(function() {

    // page is now ready, initialize the calendar...

    $('#calendar').fullCalendar({
        // put your options and callbacks here
    	 events: '/bookmarks/events/getJson',
    	 dayClick: function(date, jsEvent, view) {

	      //  alert('Clicked on: ' + date.format());

	       // alert('Coordinates: ' + jsEvent.pageX + ',' + jsEvent.pageY);

	       // alert('Current view: ' + view.name);

	        // change the day's background color just for fun
	        var startDate = moment(date).format('DD/MM/YYYY'); 
	        console.log(startDate)
	        window.location.href = "/bookmarks/events/displayAddFromCalendar?startDate=" + startDate;
	       // $(this).css('background-color', 'red');

    	}
    })

});
</script>
</head>

<body>

	<div id="calendar"></div>
</body>



