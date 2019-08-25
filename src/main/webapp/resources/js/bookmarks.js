function removeAuthor(name) {
	var encoded = encodeURIComponent(name);
	$('#authorTable').load("/bookmarks/author/removeAuthor?name=" + encoded);
}
	
function addAuthor() {
	var authorName = $("#authorAutoComplete").val();
	var encoded = encodeURIComponent(authorName);
	$('#authorTable').load("/bookmarks/author/addAuthor?name=" + encoded);
	$("#authorAutoComplete").val('');
}
	
function authoriseSuperUser(url) {
	var credentials = prompt("Please enter password");
	if(credentials == null) { //Cancel pressed
		return;
	}	
	if(credentials != "1381") {
		alert("Invalid password!");
		return;
	} 
	window.location = url;
} 

function authoriseUser(url) {
	var credentials = prompt("Please enter password");
	if(credentials == null) { //Cancel pressed
		return;
	}
	if(credentials != "1871") {
		alert("Invalid password!");
		return;
	} 
	window.location = url;
}


function calculatePricesFromDiscount(activator) {
		var discount = $('#discount').val();
		var publisherPrice = $('#publisherPrice').val();
		var sellPrice = $('#sellPrice').val();
		if(isNaN(parseFloat(discount))) {
			return;
		}
		if(isNaN(parseFloat(publisherPrice))) {
			return;
		}
		var costPrice = publisherPrice / 100 * (100 - discount);
		costPrice = Math.round(costPrice * Math.pow(10, 2)) / Math.pow(10, 2);
		$('#costPrice').val(costPrice);
		setSellPrice();
	}	
	
	//User may have already set a sell price higher than pub price
	//Only change if they are equal
	function setSellPrice() {	
		var publisherPrice = $('#publisherPrice').val();
		var sellPrice = $('#sellPrice').val();
		if(sellPrice == publisherPrice) {
			$('#sellPrice').val(publisherPrice);
		}		
	}
	
	function calculatePricesFromPublisherPrice(activator) {
		var discount = $('#discount').val();
		var publisherPrice = $('#publisherPrice').val();
		var sellPrice = $('#sellPrice').val();
		if(isNaN(parseFloat(publisherPrice))) {
			return;
		}
		if(isNaN(parseFloat(discount))) {
			return;
		}		
		setSellPrice();
		var costPrice = publisherPrice / 100 * (100 - discount);
		costPrice = Math.round(costPrice * Math.pow(10, 2)) / Math.pow(10, 2);
		$('#costPrice').val(costPrice);
	}	
	
	function calculatePricesFromCostPrice(activator) {
		var publisherPrice = $('#publisherPrice').val();
		var costPrice = $('#costPrice').val();
		if(isNaN(parseFloat(publisherPrice))) {
			return;
		}
		if(isNaN(parseFloat(costPrice))) {
			return;
		}
		var discount = 100 - (costPrice / publisherPrice * 100);
		discount = Math.round(discount * Math.pow(10, 2)) / Math.pow(10, 2);
		$('#discount').val(discount);
		$('#sellPrice').val(publisherPrice);
	}
	
	function calculatePricesFromMargin() {
		var margin = $('#margin').val();
		var costPrice = $('#costPrice').val();
		if(isNaN(parseFloat(margin))) {
			return;
		}
		if(isNaN(parseFloat(costPrice))) {
			return;
		}
		var sellPrice = costPrice / ((100 - margin) / 100);
		//var discount =  To-do
		sellPrice = Math.round(sellPrice * Math.pow(10, 2)) / Math.pow(10, 2);
		$('#sellPrice').val(sellPrice);
	}	
	
	function checkAZLookUp(e) {
		var keynum
	
		if(window.event) // IE
		{
		keynum = e.keyCode
		}
		else if(e.which) // Netscape/Firefox/Opera
		{
		keynum = e.which
		}
		if(keynum == 13) {
			submitForm('/bookmarks/stock/azlookup');
		}
		//alert(keynum);
	}
	
	
	function submitFormOnPressingReturn(e, submitURL) {
		var keynum
	
		if(window.event) // IE
		{
		keynum = e.keyCode
		}
		else if(e.which) // Netscape/Firefox/Opera
		{
		keynum = e.which
		}
		if(keynum == 13) {
			submitForm(submitURL);
		}
		//alert(keynum);
	}	
