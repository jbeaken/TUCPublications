package org.bookmarks.service;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Collection;

import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;


public interface WebsiteImageService {

	BufferedImage download(String isbn);

	BufferedImage resize(BufferedImage originalImage, int height);
}
