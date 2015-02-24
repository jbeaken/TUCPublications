package org.bookmarks.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.NumberFormat;

@Embeddable
public class AZInfo {
	
private String azPrice;
	
	@Min(value=0)
	private Integer numberOfItems;
	
	@NumberFormat(pattern="####.##")
	@Min(value=0)
	private BigDecimal height;
	

	@NumberFormat(pattern="####.##")
	@Min(value=0)
	private BigDecimal width;
	
	@NumberFormat(pattern="####.##")
	@Min(value=0)
	private BigDecimal weight;
	
	@NumberFormat(pattern="####.##")
	@Min(value=0)
	private BigDecimal length;
	
	private MeasurementUnit heigthUnit;
	
	private MeasurementUnit widthUnit;
	
	private MeasurementUnit weightUnit;

	private MeasurementUnit lengthUnit;

	private Currency azCurrency;
	
	private String largeImageURL;
	
	private String smallImageURL;
	
	@Transient
	private List<String> categories;

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public String getLargeImageURL() {
		return largeImageURL;
	}

	public void setLargeImageURL(String largeImageURL) {
		this.largeImageURL = largeImageURL;
	}

	public String getSmallImageURL() {
		return smallImageURL;
	}

	public void setSmallImageURL(String smallImageURL) {
		this.smallImageURL = smallImageURL;
	}

	public String getAzPrice() {
		return azPrice;
	}

	public void setAzPrice(String azPrice) {
		this.azPrice = azPrice;
	}

	public Integer getNumberOfItems() {
		return numberOfItems;
	}

	public void setNumberOfItems(Integer numberOfItems) {
		this.numberOfItems = numberOfItems;
	}

	public BigDecimal getHeight() {
		return height;
	}

	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	public BigDecimal getWidth() {
		return width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public BigDecimal getLength() {
		return length;
	}

	public void setLength(BigDecimal length) {
		this.length = length;
	}

	public MeasurementUnit getHeigthUnit() {
		return heigthUnit;
	}

	public void setHeigthUnit(MeasurementUnit heigthUnit) {
		this.heigthUnit = heigthUnit;
	}

	public MeasurementUnit getWidthUnit() {
		return widthUnit;
	}

	public void setWidthUnit(MeasurementUnit widthUnit) {
		this.widthUnit = widthUnit;
	}

	public MeasurementUnit getWeightUnit() {
		return weightUnit;
	}

	public void setWeightUnit(MeasurementUnit weightUnit) {
		this.weightUnit = weightUnit;
	}

	public MeasurementUnit getLengthUnit() {
		return lengthUnit;
	}

	public void setLengthUnit(MeasurementUnit lengthUnit) {
		this.lengthUnit = lengthUnit;
	}

	public Currency getAzCurrency() {
		return azCurrency;
	}

	public void setAzCurrency(Currency azCurrency) {
		this.azCurrency = azCurrency;
	}

	public Integer getNumberOfPages() {
		return numberOfPages;
	}

	public void setNumberOfPages(Integer numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	private Integer numberOfPages;	

}
