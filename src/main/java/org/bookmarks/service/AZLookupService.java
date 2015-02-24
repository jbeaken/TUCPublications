package org.bookmarks.service;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.bookmarks.domain.StockItem;

public interface AZLookupService {

	StockItem lookupWithJSoup(String isbn) throws Exception;

	String getImageUrl(String isbn) throws IOException;

	StockItem lookupWithJSoup(String isbn, boolean downloadImage) throws Exception;

	BufferedImage saveImageToFileSystem(StockItem stockItem) throws IOException;

	BufferedImage saveImageToFileSystem(String imageUrl, StockItem stockItem);

	BufferedImage saveImageToFileSystem(String imageURL, StockItem si, boolean resize);
}
