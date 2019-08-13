package org.bookmarks.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.bookmarks.controller.helper.ISBNConvertor;
import org.hibernate.annotations.Cascade;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * img_url_az : from az http://www.amazon.co.uk/etc
 * img_url : 9782342342343.jpg for example
 * put_on_website : should this stockitem be uploaded to website, overrides put_review_on_website and put_image_on_website
 * put_review_on_website
 * put_image_on_website
 * synced_with_az : Has AZ been checked and if necc updated (doesn't mean it exists on az, just that there has been a check)
 * is_on_az
 * is_review_on_az
 * is_image_on_az
 *
 * @author jb
 *
 */
@Entity
@Table(name="stockitem")
@JsonIgnoreProperties(value = { "sales", "supplierOrderLines" })
public class StockItem extends AbstractEntity {

	//Overrides
	@Column(name="update_title") private Boolean updateTitle = true;
	@Column(name="update_availablity") private Boolean updateAvailablity = true;
	@Column(name="update_sellPrice") private Boolean updateSellPrice = true;
	@Column(name="update_review") private Boolean updateReview = true;
	@Column(name="update_image") private Boolean updateImage = true;
	@Column(name="update_publisher") private Boolean updatePublisher = true;
	@Column(name="update_authors") private Boolean updateAuthors = true;

	//Availability
	@Column(name="always_in_stock") private Boolean alwaysInStock = false;
	@Column(name="gardners_stock_level") private Long gardnersStockLevel = 0l;
	@Column(name="available_at_suppliers") private Boolean isAvailableAtSuppliers = false;

	//Stickies and bouncies
	@Column(name="sticky_category_idx") private Long stickyCategoryIndex;
	@Column(name="sticky_type_idx") private Long stickyTypeIndex;
	@Column(name="bouncy_idx") private Long bouncyIndex;
	@Column(name="merchandise_idx") private Long merchandiseIndex;

	//Ebook links
	@Column(name="ebook_turnaround_url") private String ebookTurnaroundUrl;
	@Column(name="ebook_alternate_url") private String ebookAlternateUrl;

	//Extas
	@Column(name="is_on_extras") private Boolean isOnExtras = false;

	@Column(name="is_new_release") private Boolean isNewRelease = false;

	@Transient private Integer salesLastYear;

    @Transient private Integer salesTotal;

	//For upload of images
	@Transient private MultipartFile file;

	@NotNull
	@DateTimeFormat(pattern="yyyy-mm-dd hh:mm:ss")
	private Date lastReorderReviewDate;
	@Transient
	private BigDecimal margin;

	//Quantities
	@NotNull
	@Min(value=0)
	private Long quantityOnLoan = 0l;

	@NotNull
	@Column(name="quantityInStock")
	private Long quantityInStock = 0l;

	@NotNull
	@Column(name="quantityOnOrder")
	private Long quantityOnOrder = 0l;

	@NotNull
	@Column(name="quantityToKeepInStock")
	@Min(value=0)
	private Long quantityToKeepInStock = 0l;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name="keepInStockLevel")
	private Level keepInStockLevel = Level.LOW;

	@NotNull
	@Column(name="quantityReadyForCustomer")
	private Long quantityReadyForCustomer = 0l;

	@NotNull
	@Min(value=0)
	private Long quantityForCustomerOrder = 0l;

	@Min(value=0)
	private Long quantityInStockForMarxism2012 = 0l;

	@Min(value=-1)
	private Long quantityForMarxism = 0l;

	@ManyToOne
	@JoinColumn(name="preferredSupplier_id")
	private Supplier preferredSupplier;

	@Column(name="img_url")
	private String imageURL;

	@Column(name="img_filename")
	private String imageFilename;

	@Enumerated(EnumType.STRING)
	@Column(name="stockItemType")
	private StockItemType type = StockItemType.BOOK;

	@NotNull
	@Size(min = 1, max = 255)
	@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
	private String title;

	//@NotNull
	//@Size(min = 1, max = 255)
	private String originalTitle;


	//Prices
		@Min(value=0)
		@NotNull
		@NumberFormat(pattern="#.##")
		@Column(name="publisherPrice")
		private BigDecimal publisherPrice;

		@Min(value=0)
		@Max(value=100)
		@NotNull
//		@NumberFormat(pattern="##")
		private BigDecimal discount = new BigDecimal(40);

//Booleans
	@NotNull
	@Column(name="isStaffPick")
	private Boolean isStaffPick;

	@Transient
	private Boolean generateISBN = Boolean.FALSE;


	@Column(name="review_as_html")
	@Type(type="text")
    private String reviewAsHTML;

	@Column(name="review_as_text")
	@Type(type="text")
    private String reviewAsText;

	@Column(name="has_newer_edition")
	private Boolean hasNewerEdition = false;

	@NotNull
	@Column(name="put_image_on_website")
	private Boolean putImageOnWebsite = Boolean.TRUE;

	@NotNull
	@Column(name="put_review_on_website")
	private Boolean putReviewOnWebsite = Boolean.TRUE;

	@NotNull
	@Column(name="put_on_website")
	private Boolean putOnWebsite = Boolean.TRUE;

	@Column(name="is_on_az")
	private Boolean isOnAZ;

	@Column(name="is_image_on_az")
	private Boolean isImageOnAZ;

	@Column(name="is_review_on_az")
	private Boolean isReviewOnAZ;

	@Column(name="is_synced_with_az")
	private Boolean syncedWithAZ;

	@OneToMany(mappedBy="stockItem")
	private Set<Sale> sales;

	@OneToMany(mappedBy="stockItem")
	private Set<SupplierOrderLine> supplierOrderLines;

//	private Boolean isForMarxism;

	private Long marxism2012SaleAmount;

	private Integer noOfPages;


    @ManyToMany(cascade={javax.persistence.CascadeType.ALL}, fetch=FetchType.EAGER)
    @JoinTable(joinColumns = { @JoinColumn(name = "stockitem_id") }, inverseJoinColumns = { @JoinColumn(name = "author_id") })
    @NotNull
    @IndexedEmbedded
    private Set<Author> authors = new HashSet<Author>();


	private String twentyTwelveSales;

	private String twentyThirteenSales;

	private String twentyFourteenSales;


	public StockItem() {
		super();
		this.isbn = new String();
		this.title = new String();
	}
	//Used by StockItemImages main programme
	//public StockItem(Long id, String isbn, String title) {
	//	this();
	//	setId(id);
	//	setIsbn(isbn);
	//	setTitle(title);
	//}

//	categoryRepository.getWebsiteStockItems
	public StockItem(Long id, String title, String reviewAsHTML, String imageURL, String isbn) {
		this();
		setId(id);
		setIsbn(isbn);
		setReviewAsHTML(reviewAsHTML);
		setImageURL(imageURL);
		setTitle(title);

	}
	//getExtras
	//new StockItem(si.id, si.isbn, si.title, si.type)
	public StockItem(Long id, String isbn, String title, StockItemType type) {
		this();
		setId(id);
		setIsbn(isbn);
		setTitle(title);
		setType(type);
	}

	//getSTockItemByISBNForSTockTake
	public StockItem(Long id, String title, String categoryName) {
		this();
		setId(id);
		setTitle(title);
		Category category = new Category();
		category.setName(categoryName);
		setCategory(category);
	}

	//reorderReviewRepository.getReorderReview
	public StockItem(Long id, String isbn, String title, String note, Long quantityInStock,
			Long quantityOnOrder, Long quantityToKeepInStock, Long quantityForMarxism, BigDecimal publisherPrice, BigDecimal costPrice, BigDecimal sellPrice, String twentyTwelveSales, String twentyThirteenSales, String twentyFourteenSales, Date publishedDate,
	Boolean putOnWebsite, Boolean putImageOnWebsite, Long preferredSupplierId, String publisherName, String imageURL, Long categoryId, String categoryName, String supplierName) {
			super();
			setId(id);
			setTitle(title);
			setNote(note);
			setQuantityInStock(quantityInStock);
			setQuantityOnOrder(quantityOnOrder);
			setQuantityForMarxism(quantityForMarxism);
			setIsbn(isbn);
			setQuantityToKeepInStock(quantityToKeepInStock);
			setPublisherPrice(publisherPrice);
			setSellPrice(sellPrice);
			setCostPrice(costPrice);
			setImageURL(imageURL);
			setPublishedDate(publishedDate);
			setPutOnWebsite(putOnWebsite);
			setPutImageOnWebsite(putImageOnWebsite);

			setTwentyFourteenSales(twentyFourteenSales);
			setTwentyThirteenSales(twentyThirteenSales);
			setTwentyTwelveSales(twentyTwelveSales);

			Publisher publisher = new Publisher(publisherName);
//			Supplier supplier = new Supplier(supplierId);
//			supplier.setName(supplierName);
//			publisher.setSupplier(supplier);
			setPublisher(publisher);

			Category category = new Category(categoryId);
			category.setName(categoryName);
			setCategory(category);

			Supplier preferredSupplier = new Supplier(preferredSupplierId);
			setPreferredSupplier(preferredSupplier);
	}

	//Used by getByISBNAsNumber
	public StockItem(Long id, String imageURL, String title, String note, BigDecimal publisherPrice, BigDecimal sellPrice, StockItemType stockItemType, Level keepInStockLevel, Long quantityToKeepInStock, Long quantityInStock, Long publisherId, String publisherName) {
		super();
		setId(id);
		setTitle(title);
		setNote(note);
		setPublisherPrice(publisherPrice);
		setSellPrice(sellPrice);
		setQuantityInStock(quantityInStock);
		setQuantityToKeepInStock(quantityToKeepInStock);
		setKeepInStockLevel(keepInStockLevel);
		setType(stockItemType);
		setImageURL(imageURL);
		Publisher publisher = new Publisher();
		publisher.setId(publisherId);
		publisher.setName(publisherName);
		setPublisher(publisher);
	}

	//used by search
	public StockItem(
		 	Long id,
			String imageURL,
			String title,
			String note,
			String isbn,
			Binding binding,
			BigDecimal publisherPrice,
			BigDecimal sellPrice,
			BigDecimal costPrice,
			Boolean putOnWebsite,
			Boolean putReviewOnWebsite,
			Boolean putImageOnWebsite,
			Long quantityInStock,
			Long quantityOnLoan,
			Long quantityOnOrder,
			Long quantityForCustomerOrder,
			Long quantityReadyForCustomer,
			Long quantityToKeepInStock,
			Long quantityForMarxism,
			StockItemType stockItemType,
			String twentyTwelveSales,
			String twentyThirteenSales,
			Date publishedDate,
			Long pubId,
			String pubName,
			Long categoryId,
			String categoryName) {
		super();
		setId(id);
		setImageURL(imageURL);
		setIsbn(isbn);
		setTwentyTwelveSales(twentyTwelveSales);
		setTwentyThirteenSales(twentyThirteenSales);
		setPublishedDate(publishedDate);
		setNote(note);
		setTitle(title);
		setBinding(binding);
		setPublisherPrice(publisherPrice);
		setSellPrice(sellPrice);
		setCostPrice(costPrice);
		setPutOnWebsite(putOnWebsite);
		setPutReviewOnWebsite(putReviewOnWebsite);
		setPutImageOnWebsite(putImageOnWebsite);
		setQuantityForCustomerOrder(quantityForCustomerOrder);
		setQuantityInStock(quantityInStock);
		setQuantityOnLoan(quantityOnLoan);
		setQuantityOnOrder(quantityOnOrder);
		setQuantityToKeepInStock(quantityToKeepInStock);
		setQuantityForMarxism(quantityForMarxism);
		setQuantityReadyForCustomer(quantityReadyForCustomer);
		setType(stockItemType);
		setPublisher(new Publisher(pubId, pubName));
		setCategory(new Category(categoryId, categoryName));
	}

	public StockItem(String title, String isbn) {
		setTitle(title);
		setIsbn(isbn);
	}

	public StockItem(Long id) {
		this();
		setId(id);
	}

	public StockItem(Long stockItemId, String isbn, String title, Long quantityInStock, Publisher publisher) {
		setId(stockItemId);
		setIsbn(isbn);
		setTitle(title);
		setQuantityInStock(quantityInStock);
		setPublisher(publisher);
	}

	public String getEbookTurnaroundUrl() {
		return ebookTurnaroundUrl;
	}

	public void setEbookTurnaroundUrl(String ebookTurnaroundUrl) {
		this.ebookTurnaroundUrl = ebookTurnaroundUrl;
	}

	public Boolean getGenerateISBN() {
		return generateISBN;
	}
	public void setGenerateISBN(Boolean generateISBN) {
		this.generateISBN = generateISBN;
	}

	public Boolean getIsStaffPick() {
		return isStaffPick;
	}

	public void setIsStaffPick(Boolean isStaffPick){
		this.isStaffPick = isStaffPick;
	}

	public boolean hasISBN(){
		if(getType() == StockItemType.MUG
    			|| getType() == StockItemType.POSTER
    			|| getType() == StockItemType.MARXISM_CD){
			return false;
		}
		return true;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public String getOriginalTitle() {
		return originalTitle;
	}
	public void setOriginalTitle(String originalTitle) {
		this.originalTitle = originalTitle;
	}

	@Min(value=0)
	@NotNull
	@NumberFormat(pattern="#.##")
	@Column(name="sellPrice")
	private BigDecimal sellPrice;

	@Min(value=0)
	@NumberFormat(pattern="#.##")
	private BigDecimal postage;

	@Min(value=0)
	@NumberFormat(pattern="#.##")
	@Column(name="price_third_party_second_hand")
	private BigDecimal priceThirdPartySecondHand;

	@Min(value=0)
	@NumberFormat(pattern="#.##")
	@Column(name="price_third_party_collectable")
	private BigDecimal priceThirdPartyCollectable;

	@Min(value=0)
	@NumberFormat(pattern="#.##")
	@Column(name="price_third_party_new")
	private BigDecimal priceThirdPartyNew;

	@Min(value=0)
	@NumberFormat(pattern="#.##")
	@Column(name="price_at_az")
	private BigDecimal priceAtAZ;

	@Min(value=0)
	@NotNull
	@NumberFormat(pattern="#.##")
	@Column(name="costPrice")
	private BigDecimal costPrice;

	@DateTimeFormat(pattern = "dd-MM-yy")
	@Column(name="publishedDate")
	private Date publishedDate;

	@NotNull
	@Column(unique=true)
	@Field(index=Index.YES, analyze=Analyze.YES, store=Store.YES)
	private String isbn;

	public BigDecimal getPriceAtAZ() {
		return priceAtAZ;
	}
	public void setPriceAtAZ(BigDecimal priceAtAZ) {
		this.priceAtAZ = priceAtAZ;
	}

	@NotNull
	@Column(name="isbnAsNumber", unique=true)
	private Long isbnAsNumber;

	public Long getIsbnAsNumber() {
		return isbnAsNumber;
	}

	public void setIsbnAsNumber(Long isbnAsNumber) {
		this.isbnAsNumber = isbnAsNumber;
	}

	@Enumerated(EnumType.STRING)
	@NotNull
	private Availablity availability = Availablity.PUBLISHED;


	@Enumerated(EnumType.STRING)
	@NotNull
	private Binding binding;

	@ManyToOne
	@Cascade(value={org.hibernate.annotations.CascadeType.PERSIST})
	@NotNull
	private Publisher publisher;

	String dimensions;

	@ManyToOne
	@NotNull
	@IndexedEmbedded
	private Category category;

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Availablity getAvailability() {
		return availability;
	}

	public Long getQuantityReadyForCustomer() {
		return quantityReadyForCustomer;
	}

	public void setQuantityReadyForCustomer(Long quantityReadyForCustomer) {
		this.quantityReadyForCustomer = quantityReadyForCustomer;
	}

	public Long getQuantityToKeepInStock() {
		return quantityToKeepInStock;
	}

	public void setKeepInStockLevel(Level keepInStockLevel) {
		this.keepInStockLevel = keepInStockLevel;
	}

	public Level getKeepInStockLevel() {
		return keepInStockLevel;
	}

	public void setQuantityToKeepInStock(Long quantityToKeepInStock) {
		this.quantityToKeepInStock = quantityToKeepInStock;
	}

	public Long getQuantityInStock() {
		return quantityInStock;
	}

	public void setQuantityInStock(Long quantityInStock) {
		this.quantityInStock = quantityInStock;
	}

	public Long getQuantityOnOrder() {
		return quantityOnOrder;
	}

	public void setQuantityOnOrder(Long quantityOnOrder) {
		this.quantityOnOrder = quantityOnOrder;
	}

	public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	public BigDecimal getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(BigDecimal costPrice) {
		this.costPrice = costPrice;
	}


	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		if(isbn != null) {
			isbn = isbn.replace("-", "").trim();
		}
		this.isbn = isbn;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getPublisherPrice() {
		return publisherPrice;
	}

	public void setPublisherPrice(BigDecimal publisherPrice) {
		this.publisherPrice = publisherPrice;
	}

	public BigDecimal getOurPrice() {
		return sellPrice;
	}

	public void setOurPrice(BigDecimal ourPrice) {
		this.sellPrice = ourPrice;
	}

	public Binding getBinding() {
		return binding;
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}


	public Date getPublishedDate() {
		return publishedDate;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	public void setPublishedDate(Date publishedDate) {
		this.publishedDate = publishedDate;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	public boolean isValidISBN() {
		String isbn = getIsbn();
		if(isbn.length() != 10 && isbn.length() != 13) {
			return false;
		}
		String convertedISBN = ISBNConvertor.ISBN1013(isbn);
		if(convertedISBN.equals("ERROR")) {
			return false;
		}
		return true;
	}

	public void calculateCostPrice() {
		float costPrice = getSellPrice().floatValue() * (1 - (getDiscount().floatValue() / 100));
		setCostPrice(new BigDecimal(costPrice));
	}

	public boolean isBookmarksPublication() {
		if(getPublisher() != null && getPublisher().getId() == 725) return true;
		return false;
	}

	public BigDecimal getVat() {
		switch(getType()) {
			case BOOK :
				return new BigDecimal(0);
			case PAMPHLET :
				return new BigDecimal(0);
			case DVD :
				return new BigDecimal(20);
			case BUST :
				return new BigDecimal(20);
			case CD :
				return new BigDecimal(20);
			case MUG :
				return new BigDecimal(20);
			case POSTER :
				return new BigDecimal(20);
			default :
				return new BigDecimal(20);
			}
	}
	public boolean isCDorDVD() {
		if(getType() == null) return false;
		switch (getType()) {
		case CD:
			return true;
		case DVD:
			return true;
		default:
			return false;
		}
	}
	public boolean isBook() {
		switch (getType()) {
		case BOOK:
			return true;
		default:
			return false;
		}
	}

	public boolean convertToISBN13() {
		String isbn = getIsbn();
		if(isbn.length() != 10) return true;

		String isbn13 = ISBNConvertor.ISBN1013(isbn);
		if(isbn13.equals("ERROR")){
			return false;
		}
		setIsbn(isbn13);
		return true;
	}

	private String getAuthorsToString() {
		StringBuilder build = new StringBuilder();
		for(Author author : getAuthors()) {
			build.append(author.getName() + ", ");
		}
		return build.toString();
	}
	public String getReviewAsHTML() {
		return reviewAsHTML;
	}
	public void setReviewAsHTML(String reviewAsHTML) {
		this.reviewAsHTML = reviewAsHTML;
	}
	public BigDecimal getPostage() {
		return postage;
	}
	public void setPostage(BigDecimal postage) {
		this.postage = postage;
	}
	public Long getQuantityOnLoan() {
		return quantityOnLoan;
	}
	public void setQuantityOnLoan(Long quantityOnLoan) {
		this.quantityOnLoan = quantityOnLoan;
	}
	public Long getQuantityForCustomerOrder() {
		return quantityForCustomerOrder;
	}
	public void setQuantityForCustomerOrder(Long quantityForCustomerOrder) {
		this.quantityForCustomerOrder = quantityForCustomerOrder;
	}
	public Long getQuantityInStockForMarxism2012() {
		return quantityInStockForMarxism2012;
	}
	public void setQuantityInStockForMarxism2012(Long quantityInStockForMarxism2012) {
		this.quantityInStockForMarxism2012 = quantityInStockForMarxism2012;
	}
	public Supplier getPreferredSupplier() {
		return preferredSupplier;
	}
	public void setPreferredSupplier(Supplier preferredSupplier) {
		this.preferredSupplier = preferredSupplier;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public String getImageFilename() {
		return imageFilename;
	}
	public void setImageFilename(String imageFilename) {
		this.imageFilename = imageFilename;
	}
	public Boolean getIsReviewOnAZ() {
		return isReviewOnAZ;
	}
	public void setIsReviewOnAZ(Boolean isReviewOnAZ) {
		this.isReviewOnAZ = isReviewOnAZ;
	}
	public Boolean getPutImageOnWebsite() {
		return putImageOnWebsite;
	}
	public void setPutImageOnWebsite(Boolean putImageOnWebsite) {
		this.putImageOnWebsite = putImageOnWebsite;
	}
	public Boolean getPutReviewOnWebsite() {
		return putReviewOnWebsite;
	}
	public void setPutReviewOnWebsite(Boolean putReviewOnWebsite) {
		this.putReviewOnWebsite = putReviewOnWebsite;
	}
	public Boolean getPutOnWebsite() {
		return putOnWebsite;
	}
	public void setPutOnWebsite(Boolean putOnWebsite) {
		this.putOnWebsite = putOnWebsite;
	}
	public String getReviewAsText() {
		return reviewAsText;
	}
	public void setReviewAsText(String reviewAsText) {
		this.reviewAsText = reviewAsText;
	}
	public BigDecimal getPriceThirdPartySecondHand() {
		return priceThirdPartySecondHand;
	}
	public void setPriceThirdPartySecondHand(BigDecimal priceThirdPartySecondHand) {
		this.priceThirdPartySecondHand = priceThirdPartySecondHand;
	}
	public BigDecimal getPriceThirdPartyCollectable() {
		return priceThirdPartyCollectable;
	}
	public void setPriceThirdPartyCollectable(BigDecimal priceThirdPartyCollectable) {
		this.priceThirdPartyCollectable = priceThirdPartyCollectable;
	}
	public BigDecimal getPriceThirdPartyNew() {
		return priceThirdPartyNew;
	}
	public void setPriceThirdPartyNew(BigDecimal priceThirdPartyNew) {
		this.priceThirdPartyNew = priceThirdPartyNew;
	}
	public String getDimensions() {
		return dimensions;
	}
	public void setDimensions(String dimensions) {
		this.dimensions = dimensions;
	}
	public void setAvailability(Availablity availability) {
		this.availability = availability;
	}
	public BigDecimal getMargin() {
		return margin;
	}
	public void setMargin(BigDecimal margin) {
		this.margin = margin;
	}
	public Boolean getHasNewerEdition() {
		return hasNewerEdition;
	}
	public void setHasNewerEdition(Boolean hasNewerEdition) {
		this.hasNewerEdition = hasNewerEdition;
	}
	public Boolean getIsOnAZ() {
		return isOnAZ;
	}
	public void setIsOnAZ(Boolean isOnAZ) {
		this.isOnAZ = isOnAZ;
	}
	public Boolean getIsImageOnAZ() {
		return isImageOnAZ;
	}
	public void setIsImageOnAZ(Boolean isImageOnAZ) {
		this.isImageOnAZ = isImageOnAZ;
	}
	public Boolean getSyncedWithAZ() {
		return syncedWithAZ;
	}
	public void setSyncedWithAZ(Boolean syncedWithAZ) {
		this.syncedWithAZ = syncedWithAZ;
	}
	public Set<Sale> getSales() {
		return sales;
	}
	public void setSales(Set<Sale> sales) {
		this.sales = sales;
	}
	public Set<SupplierOrderLine> getSupplierOrderLines() {
		return supplierOrderLines;
	}
	public void setSupplierOrderLines(Set<SupplierOrderLine> supplierOrderLines) {
		this.supplierOrderLines = supplierOrderLines;
	}
	public Long getMarxism2012SaleAmount() {
		return marxism2012SaleAmount;
	}
	public void setMarxism2012SaleAmount(Long marxism2012SaleAmount) {
		this.marxism2012SaleAmount = marxism2012SaleAmount;
	}
	public Integer getNoOfPages() {
		return noOfPages;
	}
	public void setNoOfPages(Integer noOfPages) {
		this.noOfPages = noOfPages;
	}
	public Set<Author> getAuthors() {
		return authors;
	}
	public void setAuthors(Set<Author> authors) {
		this.authors = authors;
	}
	public String getTwentyTwelveSales() {
		return twentyTwelveSales;
	}
	public void setTwentyTwelveSales(String twentyTwelveSales) {
		this.twentyTwelveSales = twentyTwelveSales;
	}

	public Date getLastReorderReviewDate() {
		return lastReorderReviewDate;
	}
	public void setLastReorderReviewDate(Date lastReorderReviewDate) {
		this.lastReorderReviewDate = lastReorderReviewDate;
	}
	public String getTwentyThirteenSales() {
		return twentyThirteenSales;
	}
	public void setTwentyThirteenSales(String twentyThirteenSales) {
		this.twentyThirteenSales = twentyThirteenSales;
	}
	public Boolean getUpdateAvailablity() {
		return updateAvailablity;
	}
	public void setUpdateAvailablity(Boolean updateAvailablity) {
		this.updateAvailablity = updateAvailablity;
	}
	public Boolean getUpdateSellPrice() {
		return updateSellPrice;
	}
	public void setUpdateSellPrice(Boolean updateSellPrice) {
		this.updateSellPrice = updateSellPrice;
	}
	public Boolean getUpdateReview() {
		return updateReview;
	}
	public void setUpdateReview(Boolean updateReview) {
		this.updateReview = updateReview;
	}
	public Boolean getUpdateImage() {
		return updateImage;
	}
	public void setUpdateImage(Boolean updateImage) {
		this.updateImage = updateImage;
	}
	public Boolean getUpdatePublisher() {
		return updatePublisher;
	}
	public void setUpdatePublisher(Boolean updatePublisher) {
		this.updatePublisher = updatePublisher;
	}
	public Boolean getUpdateAuthors() {
		return updateAuthors;
	}
	public void setUpdateAuthors(Boolean updateAuthors) {
		this.updateAuthors = updateAuthors;
	}
	public Long getBouncyIndex() {
		return bouncyIndex;
	}
	public void setBouncyIndex(Long bouncyIndex) {
		this.bouncyIndex = bouncyIndex; 
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	public Boolean getUpdateTitle() {
		return updateTitle;
	}
	public void setUpdateTitle(Boolean updateTitle) {
		this.updateTitle = updateTitle;
	}
	public Boolean getAlwaysInStock() {
		return alwaysInStock;
	}
	public void setAlwaysInStock(Boolean alwaysInStock) {
		this.alwaysInStock = alwaysInStock;
	}
	public Long getStickyCategoryIndex() {
		return stickyCategoryIndex;
	}
	public void setStickyCategoryIndex(Long stickyCategoryIndex) {
		this.stickyCategoryIndex = stickyCategoryIndex;
	}
	public Long getStickyTypeIndex() {
		return stickyTypeIndex;
	}
	public void setStickyTypeIndex(Long stickyTypeIndex) {
		this.stickyTypeIndex = stickyTypeIndex;
	}
	public String getTwentyFourteenSales() {
		return twentyFourteenSales;
	}
	public void setTwentyFourteenSales(String twentyFourteenSales) {
		this.twentyFourteenSales = twentyFourteenSales;
	}
	public Integer getSalesLastYear() {
		return salesLastYear;
	}
	public void setSalesLastYear(Integer salesLastYear) {
		this.salesLastYear = salesLastYear;
	}
	public Integer getSalesTotal() {
		return salesTotal;
	}
	public void setSalesTotal(Integer salesTotal) {
		this.salesTotal = salesTotal;
	}
	public Boolean getIsAvailableAtSuppliers() {
		return isAvailableAtSuppliers;
	}
	public void setIsAvailableAtSuppliers(Boolean isAvailableAtSuppliers) {
		this.isAvailableAtSuppliers = isAvailableAtSuppliers;
	}
	public Long getGardnersStockLevel() {
		return gardnersStockLevel;
	}
	public void setGardnersStockLevel(Long gardnersStockLevel) {
		this.gardnersStockLevel = gardnersStockLevel;
	}
	public StockItemType getType() {
		return type;
	}
	public void setType(StockItemType type) {
		this.type = type;
	}
	public String getMainAuthor() {
		if(authors == null || authors.isEmpty()) return "No Author";
		StringBuilder builder = new StringBuilder();
		for(Author a : authors) {
			builder.append(a.getName() + " ");
		}
		return builder.toString();
	}
	public Long getQuantityForMarxism() {
		return quantityForMarxism;
	}
	public void setQuantityForMarxism(Long quantityForMarxism) {
		this.quantityForMarxism = quantityForMarxism;
	}
	public Boolean getIsOnExtras() {
		return isOnExtras;
	}
	public void setIsOnExtras(Boolean isOnExtras) {
		this.isOnExtras = isOnExtras;
	}

	public Long getMerchandiseIndex() {
		return merchandiseIndex;
	}

	public void setMerchandiseIndex(Long merchandiseIndex) {
		this.merchandiseIndex = merchandiseIndex;
	}

	public Boolean getIsNewRelease() {
		return isNewRelease;
	}

	public void setIsNewRelease(Boolean isNewRelease) {
		this.isNewRelease = isNewRelease;
	}

	@Override
	public String toString() {
		return "StockItem [bouncyIndex=" + bouncyIndex + ", title=" + title + ", getId()=" + getId() + ", getIsbn()=" + getIsbn() + "]";
	}
}
