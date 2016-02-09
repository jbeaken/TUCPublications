package org.bookmarks.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.persistence.Transient;
import org.bookmarks.domain.StockItem;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name="event")
public class Event extends AbstractNamedEntity {

	@NotNull
	@DateTimeFormat(pattern="dd/MM/yy")
	@Column(name="startDate")
	private Date startDate;

	@NotNull
	@Column(name="show_author")
	private Boolean showAuthor;

	@Column(name="show_name_not_stock_title")
	private Boolean showName;

	@Column(name="show_bookmarks_address")
	private Boolean showBookmarksAddress = true;

	@Column(name="entrance_price")
	private Float entrancePrice;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name="type")
	private EventType type;

	@Column(columnDefinition="text")
	private String description;

	private String startTime;

	private String endTime;

	//For upload of sales csv
	@Transient private MultipartFile file;

	@NotNull
	@DateTimeFormat(pattern="dd/MM/yy")
	@Column(name="endDate")
	private Date endDate;

	@OneToMany(mappedBy = "event")
	private Set<Sale> sales;

	@Column(name="totalSellPrice")
	private BigDecimal totalSellPrice;

	@ManyToOne
	@JoinColumn(name="stockitem_id")
	private StockItem stockItem;

	@Column(name="onWebsite")
	private Boolean onWebsite;

	//Constructors
	public Event() {
		super();
	}

	public Event(Long id, String name, EventType type, Date startDate, Date endDate, String note, BigDecimal totalSellPrice) {
		this();
		setId(id);
		setName(name);
		setStartDate(startDate);
		setEndDate(endDate);
		setNote(note);
		setTotalSellPrice(totalSellPrice);
		setType(type);
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public BigDecimal getTotalSellPrice() {
		if(totalSellPrice == null) totalSellPrice = new BigDecimal(0);
		return totalSellPrice;
	}

	public void setTotalSellPrice(BigDecimal totalSellPrice) {
		this.totalSellPrice = totalSellPrice;
	}

	public Set<Sale> getSales() {
		return sales;
	}

	public void setSales(Set<Sale> sales) {
		this.sales = sales;
	}

	public Boolean getOnWebsite() {
		return onWebsite;
	}

	public void setOnWebsite(Boolean onWebsite) {
		this.onWebsite = onWebsite;
	}

	public StockItem getStockItem() {
		return stockItem;
	}

	public void setStockItem(StockItem stockItem) {
		this.stockItem = stockItem;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Boolean getShowBookmarksAddress() {
		return showBookmarksAddress;
	}

	public void setShowBookmarksAddress(Boolean showBookmarksAddress) {
		this.showBookmarksAddress = showBookmarksAddress;
	}

	public Boolean getShowAuthor() {
		return showAuthor;
	}

	public void setShowAuthor(Boolean showAuthor) {
		this.showAuthor = showAuthor;
	}

	public Float getEntrancePrice() {
		return entrancePrice;
	}

	public Boolean getShowName() {
		return showName;
	}


	public void setShowName(Boolean showName) {
		this.showName = showName;
	}


	public void setEntrancePrice(Float entrancePrice) {
		this.entrancePrice = entrancePrice;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
}
