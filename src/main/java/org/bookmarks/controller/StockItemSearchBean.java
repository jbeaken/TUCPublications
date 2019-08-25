package org.bookmarks.controller;

import org.bookmarks.controller.helper.ISBNConvertor;
import org.bookmarks.domain.Availablity;
import org.bookmarks.domain.Binding;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemType;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.Level;
import org.bookmarks.domain.StockLevel;

public class StockItemSearchBean extends AbstractSearchBean {

	//Constructors
	public StockItemSearchBean() {
		this.stockItem = new StockItem();
		stockItem.setType(null);
		stockItem.setIsStaffPick(false);
		stockItem.setCategory(new Category());
		Supplier supplier = new Supplier();
		Publisher publisher = new Publisher();
		publisher.setSupplier(supplier);
		stockItem.setPublisher(publisher);
		stockItem.setAvailability(null);
		stockItem.setPutOnWebsite(null);
//		setGroupBy(true);
	}

	public StockItemSearchBean(String isbn) {
		this();
		getStockItem().setIsbn(isbn);
	}

	private boolean hideBookmarks = false;

	private Boolean reorderReview;

	private Boolean skipMarxismRejects;

	private Integer marxismStatus;

	private SearchEngine searchEngine = SearchEngine.LOCAL_DATABASE;

	private StockLevel stockLevel;

	private boolean keepInStock;

	private Long authorId;

	private String authorName;

	private boolean alwaysInStock;

	private Level keepInStockLevel;

	private String authors;

	private String q;

	public void setKeepInStockLevel(Level keepInStockLevel) {
		this.keepInStockLevel = keepInStockLevel;
	}

	public Level getKeepInStockLevel() {
		return keepInStockLevel;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public boolean isKeepInStock() {
		return keepInStock;
	}

	public void setKeepInStock(boolean keepInStock) {
		this.keepInStock = keepInStock;
	}

	public StockLevel getStockLevel() {
		return stockLevel;
	}

	public void setStockLevel(StockLevel stockLevel) {
		this.stockLevel = stockLevel;
	}

	private Supplier supplier;

	public SearchEngine getSearchEngine() {
		return searchEngine;
	}

	public void setSearchEngine(SearchEngine searchEngine) {
		this.searchEngine = searchEngine;
	}

	private StockItem stockItem;

	private String keyword;

	public String getKeyword() {
		return keyword;
	}
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	private String isbnSearchType = "full";

	public String getIsbnSearchType() {
		return isbnSearchType;
	}
	public void setIsbnSearchType(String isbnSearchType) {
		this.isbnSearchType = isbnSearchType;
	}

	public String checkValidityForISBN(){
		String isbn = stockItem.getIsbn().trim();
		//May be DVD or CD, not necessarily ISBN (length 13 or 10)
//		if(isbn.length() != 10 && isbn.length() != 13) {
//			return "Invalid ISBN length";
//		}
		if(isbn.length() == 10)	{
			isbn = ISBNConvertor.ISBN1013(stockItem.getIsbn());
		}
		stockItem.setIsbn(isbn);
		for (int i = 0; i < isbn.length(); i++) {
			char c = isbn.charAt(i);
			//If we find a non-digit character which isn't x
			if (!Character.isDigit(c)) {
				if(c != 'x' && c != 'X')
					return "Invalid character in isbn";
			}
		}
		return null;
	}

	public String checkValidity() {

		//If keep in stock then it's valid
		if(getQ() != null && !getQ().trim().isEmpty()) {
			return null;
		}

		//If keep in stock then it's valid
		if(isKeepInStock() ==  true) {
			return null;
		}

		if(getStockItem().getPutOnWebsite() !=  null) {
			return null;
		}

		//If non book option then it's valid
		if(getStockItem().getType() != null && getStockItem().getType() != StockItemType.BOOK) {
			return null;
		}

		//If non book option then it's valid
		if(getStockItem().getAvailability() != Availablity.PUBLISHED && getStockItem().getAvailability() != null) {
			return null;
		}

		//If a category has been selected then it's always valid
		if(getStockItem().getCategory() != null && getStockItem().getCategory().getId() != null) {
			return null;
		}
		//If a publisher has been selected then it's always valid
		if(getStockItem().getPublisher() != null && getStockItem().getPublisher().getId() != null) {
			return null;
		}
		//If a hardback binding has been selected then it's always valid
		if(getStockItem().getBinding() != null) {
			return null;
		}
		if(getAuthorId() != null) {
			return null;
		}
		//If in stock selected then it's always valid
		if(getStockLevel() != null) {
			return null;
		}


		String isbn = stockItem.getIsbn().trim();

//		if(isbn.length() < 3 && getStockItem().getTitle().length() < 3) {
//			return "Insufficient data for search";
//		}
		//If full isbn search, check length
		if(isbn.length() != 0) {
			for (int i = 0; i < isbn.length(); i++) {
				char c = isbn.charAt(i);
				//If we find a non-digit character which isn't x
				if (!Character.isDigit(c)) {
					if(c != 'x' && c != 'X')
						return "Invalid character in isbn";
				}
			}
			if(!convertToISBN13(stockItem)) {
				//Only converts if length - 10
				return "Invalid isbn";
			}
			//Isn't necessarily an isbn, could be a CD/DVD
//			if(getIsbnSearchType().equals("full")){
//				if(isbn.length() != 10 && isbn.length() != 13) {
//					return "Invalid ISBN length";
//				}
//			}
		}
		return null;
	}

	private boolean convertToISBN13(StockItem stockItem) {
		String isbn = stockItem.getIsbn();
		if(isbn.length() != 10) return true;

		String isbn13 = ISBNConvertor.ISBN1013(isbn);
		if(isbn13.equals("ERROR")){
			return false;
		}
		stockItem.setIsbn(isbn13);
		return true;
	}
	//Getters and setters
	public StockItem getStockItem() {
		return stockItem;
	}
	public void setStockItem(StockItem stockItem) {
		this.stockItem = stockItem;
	}

	public void reset() {
		stockItem = new StockItem();
	}

	/**
	 * TO-DO combine with isValid
	 * @return
	 */
	public boolean isbnIsValid() {
		String isbn = getStockItem().getIsbn();
		for (int i = 0; i < isbn.length(); i++) {
			char c = isbn.charAt(i);
			//If we find a non-digit character which isn't x
			if (!Character.isDigit(c)) {
				if(c != 'x' && c != 'X')
					return false;
			}
		}
		if(isbn.length() != 10 && isbn.length() != 13) {
			return false;
		}
		return true;
	}

	public boolean isAlwaysInStock() {
		return alwaysInStock;
	}

	public void setAlwaysInStock(boolean alwaysInStock) {
		this.alwaysInStock = alwaysInStock;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public Integer getMarxismStatus() {
		return marxismStatus;
	}

	public void setMarxismStatus(Integer marxismStatus) {
		this.marxismStatus = marxismStatus;
	}


	public Boolean getReorderReview() {
		return reorderReview;
	}

	public void setSkipMarxismRejects(Boolean skipMarxismRejects) {
		this.skipMarxismRejects = reorderReview;
	}
	
	public Boolean getSkipMarxismRejects() {
		return skipMarxismRejects;
	}

	public void setReorderReview(Boolean reorderReview) {
		this.reorderReview = reorderReview;
	}

	public boolean isHideBookmarks() {
		return hideBookmarks;
	}

	public void setHideBookmarks(boolean hideBookmarks) {
		this.hideBookmarks = hideBookmarks;
	}
}
