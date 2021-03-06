package org.bookmarks.website.domain;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author jack
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockItem extends AbstractEntity {

	public Boolean hasImage() {
		if (imageFilename != null)
			return true;
		return false;
	}

	public String getViewUrl() throws UnsupportedEncodingException {
		String title = getTitle();
		title = title.replace("/", "");
		if (title.length() > 200)
			title = title.substring(0, 200);
		return "/view/" + getId() + "/" + URLEncoder.encode(title, "UTF-8");
	}

	public String getImageUrl() {
		return "/imageFiles/isbn/" + getImageFilename(); // Local
	}

	public String getThumbnailImageUrl() {
		return "/imageFiles/200/" + getImageFilename(); // Local
	}

	public String getOriginalImageUrl() {
		return "/imageFiles/original/" + getImageFilename(); // Local
	}

	public String getSmallerImageUrl() {
		return "/imageFiles/150/" + getImageFilename(); // Local
	}

	public String getImageUrlForEmail() {
		return "http://109.109.239.50/imageFiles/isbn/" + getImageFilename();
	}

	public String getBouncyImageUrl() {
		return "/imageFiles/bouncy/" + getImageFilename(); // Local
	}

	public boolean getHasImage() {
		return (imageFilename == null) ? false : true;
	}

	public String getAvailabilityMessage() {
		Long qis = getQuantityInStock();
		if (qis != null && qis > 0) {
			return "<span class=\"in_stock\">In stock</span>";
		}

		String message = null;
		switch (getAvailability()) {
		case OUT_OF_PRINT:
			message = "<span class=\"second_hand\">Only available second hand</span>";
			break;
		case NOT_YET_PUBLISHED:
			message = "<span class=\"not_yet_published\">Not Yet Published</span>";
			break;
		default: // For published, or available new
			if (qis > 0) {
				message = "<span class=\"in_stock\">In stock</span>";
			} else {
				message = "<span class=\"can_be_ordered_in\">Can be ordered in</span>";
			}
		}
		return message;
	}

	// Stickies
	@Column(name = "sticky_category_idx")
	private Long stickyCategory;
	@Column(name = "sticky_type_idx")
	private Long stickyType;

	// Bouncy
	@Column(name = "bouncy_idx")
	private Long bouncyIndex;

	@NotNull
	@Size(min = 1, max = 255)
	@Field(index = Index.YES, store = Store.YES)
	private String title;

	@NotNull
	private BigDecimal postage;

	@Field(index = Index.NO, store = Store.YES)
	@Column(name = "img_url")
	private String imageURL;

	@Field(index = Index.NO, store = Store.YES)
	@Column(name = "img_filename")
	private String imageFilename;

	@Field(index = Index.NO, store = Store.YES)
	private Integer noOfPages;

	@NotNull
	@Field(index = Index.YES, store = Store.YES)
	private Long quantityInStock;

	@ManyToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.MERGE }, fetch = FetchType.EAGER)
	@JoinTable(joinColumns = { @JoinColumn(name = "stockitem_id") }, inverseJoinColumns = { @JoinColumn(name = "author_id") })
	@NotNull
	@IndexedEmbedded
	private Set<Author> authors;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	private String categoryName;

	@Column(name = "parent_category_id")
	private Long parentCategoryId;

	/*
	 * @ManyToMany(cascade={javax.persistence.CascadeType.PERSIST,
	 * javax.persistence.CascadeType.MERGE})
	 * 
	 * @JoinTable(joinColumns = { @JoinColumn(name="stockitem_id") },
	 * inverseJoinColumns = { @JoinColumn(name = "reading_list_id") })
	 * 
	 * @NotNull private List<ReadingList> readingLists;
	 */

	@Enumerated(EnumType.STRING)
	@NotNull
	@Field(index = Index.NO, store = Store.YES)
	private StockItemType type = StockItemType.BOOK;

	private Long width;

	private Long height;

	private Long depth;

	@Column(columnDefinition = "text")
	private String reviewAsText;

	@Field(index = Index.NO, store = Store.YES)
	private String reviewShort;

	@Column(columnDefinition = "text")
	private String reviewAsHtml;

	// @Field(index=Index.NO, store=Store.YES)
	private String dimensions;

	@Min(0)
	@NotNull
	@NumberFormat(pattern = "#.##")
	private BigDecimal publisherPrice;

	@Min(0)
	@NotNull
	@NumberFormat(pattern = "#.##")
	@Field(index = Index.NO, store = Store.YES)
	private BigDecimal sellPrice;

	@Min(value = 0)
	@NumberFormat(pattern = "#.##")
	@Field(index = Index.NO, store = Store.YES)
	private BigDecimal priceThirdPartySecondHand;

	@Min(value = 0)
	@NumberFormat(pattern = "#.##")
	@Field(index = Index.NO, store = Store.YES)
	private BigDecimal priceThirdPartyCollectable;

	@Min(value = 0)
	@NumberFormat(pattern = "#.##")
	@Field(index = Index.NO, store = Store.YES)
	private BigDecimal priceThirdPartyNew;

	@Min(value = 0)
	@NumberFormat(pattern = "#.##")
	@Field(index = Index.NO, store = Store.YES)
	@Column(name = "price_at_az")
	private BigDecimal priceAtAZ;

	@DateTimeFormat(pattern = "dd-MM-yy")
	@Field(index = Index.NO, store = Store.YES)
	private Date publishedDate;

	@NotNull
	@Column(unique = true)
	@Field(index = Index.NO, store = Store.YES)
	private String isbn;

	@NotNull
	@Field(index = Index.YES, store = Store.YES)
	private Integer salesLastYear;

	@NotNull
	@Field(index = Index.NO, store = Store.YES)
	private Integer salesTotal;

	@NotNull
	@Column(unique = true)
	private Long isbnAsNumber;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Field(index = Index.NO, store = Store.YES)
	private Availability availability = Availability.PUBLISHED;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Field(index = Index.NO, store = Store.YES)
	private Binding binding;

	// @ManyToOne(cascade={javax.persistence.CascadeType.PERSIST,
	// javax.persistence.CascadeType.MERGE}, fetch=FetchType.LAZY)
	@NotNull
	@Embedded
	// @JoinColumn(name="publisher_id")
	@IndexedEmbedded
	private Publisher publisher;

	// ACCESORS
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

	public Integer getNoOfPages() {
		return noOfPages;
	}

	public void setNoOfPages(Integer noOfPages) {
		this.noOfPages = noOfPages;
	}

	public String getTitle() {
		if (availability == Availability.OUT_OF_PRINT) {
			return title + " (Second Hand)";
		}
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getPostage() {
		if (postage == null)
			return new BigDecimal(2.25);
		return postage;
	}

	public void setPostage(BigDecimal postage) {
		this.postage = postage;
	}

	public Long getQuantityInStock() {
		return quantityInStock;
	}

	public void setQuantityInStock(Long quantityInStock) {
		this.quantityInStock = quantityInStock;
	}

	public Set<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(Set<Author> authors) {
		this.authors = authors;
	}

	public StockItemType getType() {
		return type;
	}

	public void setType(StockItemType type) {
		this.type = type;
	}

	public Long getWidth() {
		return width;
	}

	public void setWidth(Long width) {
		this.width = width;
	}

	public Long getHeight() {
		return height;
	}

	public void setHeight(Long height) {
		this.height = height;
	}

	public Long getDepth() {
		return depth;
	}

	public void setDepth(Long depth) {
		this.depth = depth;
	}

	public String getDimensions() {
		return dimensions;
	}

	public void setDimensions(String dimensions) {
		this.dimensions = dimensions;
	}

	public BigDecimal getPublisherPrice() {
		return publisherPrice;
	}

	public void setPublisherPrice(BigDecimal publisherPrice) {
		this.publisherPrice = publisherPrice;
	}

	public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	public Date getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(Date publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Long getIsbnAsNumber() {
		return isbnAsNumber;
	}

	public void setIsbnAsNumber(Long isbnAsNumber) {
		this.isbnAsNumber = isbnAsNumber;
	}

	public Availability getAvailability() {
		return availability;
	}

	public void setAvailability(Availability availability) {
		this.availability = availability;
	}

	public Binding getBinding() {
		return binding;
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	public String getReviewForSearch() {
		String review = getReviewAsText();
		if (review == null)
			return "No Review";
		int max = 200;
		if (review.length() < 200)
			max = review.length();
		return review.substring(0, max);
	}

	public BigDecimal getVat() {
		switch (type) {
		case BOOK:
			return new BigDecimal(0);
		case PAMPHLET:
			return new BigDecimal(0);
		default:
			return new BigDecimal(20);
		}
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

	public BigDecimal getPriceAtAZ() {
		return priceAtAZ;
	}

	public void setPriceAtAZ(BigDecimal priceAtAZ) {
		this.priceAtAZ = priceAtAZ;
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

	public String getReviewAsText() {
		return reviewAsText;
	}

	public void setReviewAsText(String reviewAsText) {
		this.reviewAsText = reviewAsText;
	}

	public String getReviewAsHtml() {
		return reviewAsHtml;
	}

	public void setReviewAsHtml(String reviewAsHtml) {
		this.reviewAsHtml = reviewAsHtml;
	}

	public String getReviewShort() {
		if (reviewShort == null)
			return null;
		return reviewShort + "...";
	}

	public void setReviewShort(String reviewShort) {
		this.reviewShort = reviewShort;
	}

	public Long getStickyCategory() {
		return stickyCategory;
	}

	public void setStickyCategory(Long stickyCategory) {
		this.stickyCategory = stickyCategory;
	}

	public Long getStickyType() {
		return stickyType;
	}

	public void setStickyType(Long stickyType) {
		this.stickyType = stickyType;
	}

	public Long getBouncyIndex() {
		return bouncyIndex;
	}

	public void setBouncyIndex(Long bouncyIndex) {
		this.bouncyIndex = bouncyIndex;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String toString() {
		return getId() + " " + getTitle();
	}

	// Constructors out of the way

	public StockItem() {
		super();
	}

	// new StockItem(si.id, si.isbn, si.title, si.imageFilename, si.reviewShort,
	// si.sellPrice, si.availability, si.publishedDate, si.binding,
	// si.publisher.name, si.publisher.id, si.category.id, si.categoryName)
	public StockItem(Long id, String isbn, String title, String imageFilename, String reviewShort, BigDecimal sellPrice, BigDecimal postage, Availability availability, Date publishedDate, Binding binding, String publisherName, Long publisherId, Long quantityInStock, Long categoryId,
			String categoryName) {
		this();
		setId(id);
		setTitle(title);
		setIsbn(isbn);
		setTitle(title);
		// setImageURL(imageUrl);
		setImageFilename(imageFilename);
		setReviewShort(reviewShort);
		setSellPrice(sellPrice);
		setPostage(postage);
		setAvailability(availability);
		setPublishedDate(publishedDate);
		setBinding(binding);
		setQuantityInStock(quantityInStock);

		Publisher publisher = new Publisher();
		publisher.setName(publisherName);
		publisher.setId(publisherId);
		setPublisher(publisher);

		Category category = new Category();
		category.setName(categoryName);
		category.setId(categoryId);
		setCategory(category);
	}

	public StockItem(Long id, Set<Author> authors) {

	}

	public StockItem(Long id, Author authors) {

	}

	// findByCategoryWithImage
	public StockItem(Long id, String title, String imageFilename, BigDecimal sellPrice) {
		this();
		setId(id);
		setTitle(title);
		setImageFilename(imageFilename);
		setSellPrice(sellPrice);
	}

	// getBouncies
	public StockItem(Long id, String title, String imageFilename) {
		this();
		setId(id);
		setTitle(title);
		setImageFilename(imageFilename);
	}

	public Long getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(Long parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}
}
