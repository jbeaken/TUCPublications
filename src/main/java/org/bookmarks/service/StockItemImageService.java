package org.bookmarks.service;

import java.awt.image.BufferedImage;

import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.Invoice;

public interface StockItemImageService {
	
	public BufferedImage getImage(String isbn);
	
	public String getImageURL(String isbn);
	
	public String getImageURLFromAZ(String isbn);

	String getImageURL2(String isbn);

	BufferedImage getImageFromURL(String urlLocation);

	void saveImageToLocalFileSystem(BufferedImage image, String fileLocation);

	BufferedImage resizeImage(BufferedImage originalImage, int height);
}
