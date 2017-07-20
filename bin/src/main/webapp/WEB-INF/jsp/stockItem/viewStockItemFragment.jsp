<div class="rows">
     <div class="row">
          <div class="column w-33-percent">
			<label>Title</label><br/>        
			${stockItem.title}
	  	 </div>
		  <div class="column w-33-percent">
		  	<label>ISBN</label><br/> 
		  	${stockItem.isbn}     
	  	  </div>
          <div class="column w-33-percent">
			<label>Author(s)</label><br/>        
			${stockItem.mainAuthor}
	  	 </div>		  
	</div>
     <div class="row">	  	  		
          <div class="column w-33-percent">
			<label>Category</label><br/>        
			${stockItem.category.name}
	  	 </div>
          <div class="column w-33-percent">
			<label>Sell Price</label><br/>        
			${stockItem.sellPrice}
	  	 </div>
          <div class="column w-33-percent">
			<label>Publisher Price</label><br/>        
			${stockItem.publisherPrice}
	  	 </div>		 
	</div>
     <div class="row">	  	  		
          <div class="column w-33-percent">
			<label>Quanity In Stock</label><br/>        
			${stockItem.quantityInStock}
	  	 </div>	 
	</div>	
</div>