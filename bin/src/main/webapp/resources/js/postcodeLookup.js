
	$(function() {
			//Postcode stuff
  		$('input#postcodeLookup').keypress(function(e) {
  		    if(e.which == 13) {
  		    	postcodeLookup();
  		    }
  		});	    		
	});


       
//Postcode
function postcodeLookup() {
	var postcode = $('#postcodeLookup').val();
	console.log(postcode);
	
	if(postcode.length < 3)return;
	var encoded = encodeURIComponent(postcode);
	
	//var url = "http://api.postcodefinder.royalmail.com/CapturePlus/Interactive/Find/v2.00/json3ex.ws?Key=BH89-YF22-ZU91-EE62&Country=GBR&SearchTerm=" + encoded + "&LanguagePreference=en&LastId=&SearchFor=Everything&$block=true&$cache=true";						
	$.getJSON('/bookmarks/customer/lookupPostcode', {postcode : encoded}, function(jsonData) {
	        console.log( encoded + " :", jsonData );
	        //console.log( "Items:", jsonData.Items );

		var options = '';
	     $.each(jsonData.Items, function(i, data) {
	    	 var option = '<option value="' + data.Id +'">' + data.Text + '</option>';
	    	 options += option;
	     });
	     //alert("options : " + options);
	     $('#dropDown').html(options).show();
	    });
}

function lookupAddress() {
	var id = $('#dropDown').val();
	var encodedId = encodeURIComponent(id);
	console.log(id);
	
	//var url = "http://api.postcodefinder.royalmail.com/CapturePlus/Interactive/RetrieveFormatted/v2.00/json3ex.ws?Key=BH89-YF22-ZU91-EE62&Id=" + encodedId + "&Source=&$cache=true&field1format=%7BLatitude%7D&field2format=%7BLongitude%7D";
	$.getJSON('/bookmarks/customer/lookupAddress', {encodedId : encodedId}, function(jsonData) {
	        console.log( id + " : ", jsonData );

	   var line1 = jsonData.Items[0].Line1;
	   var line2 = jsonData.Items[0].Line2;
	   var line3 = jsonData.Items[0].Line3;
	   var line4 = jsonData.Items[0].Line4;
	   var city = jsonData.Items[0].City;
	   var postcode = jsonData.Items[0].PostalCode;
	   var countryName = jsonData.Items[0].CountryName;
	   console.log(line1);
	   
	   $('#address1').val(line1);
	   $('#address2').val(line2);
	   $('#address3').val(line3);
	   $('#address4').val(line4);
	   $('#city').val(city);
	   $('#postcode').val(postcode);
	   $('#country').val(countryName);
	   
	   //$('#postcodeLookup').val('');
	   //$('#dropDown').hide();
	    });
}