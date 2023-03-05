package org.bookmarks.service;


import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

@Service
public class StockItemImageServiceImpl implements StockItemImageService {

	@Override
	public BufferedImage getImage(String isbn) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Cycle through options (az, open library etc)
	 * and see if images exist at those locations. 
	 * Must have a preference, need to see which is best quality
	 * but for now assume open library as it's the biggest file
	 */
	@Override
	public String getImageURL2(String isbn) {
		String imageUrl = getImageURLFromAZ(isbn);

		if(imageUrl == null) {
			BufferedImage image = downloadImageFromOpenLibrary(isbn);
			if(image != null && image.getWidth() != 1) { //open libary returns non null 1 width images which are empty
				imageUrl = getImageURLFromOpenLibrary(isbn); 
			} else {
				image = downloadImageFromWordPower(isbn);
				if(image != null) {
					imageUrl =  getImageURLFromWordPower(isbn);
				}
			}
		}
		
//		System.out.println("Have imageURL = " + imageUrl);
		
		return imageUrl;
	}
			

	/**
	 * 
	 * Cycle through options (az, open library etc)
	 * and see if images exist at those locations. 
	 * Must have a preference, need to see which is best quality
	 * but for now assume open library as it's the biggest file
	 */
	@Override
	public String getImageURL(String isbn) {
		String imageUrl = null;

		BufferedImage image = downloadImageFromOpenLibrary(isbn);
		if(image != null && image.getWidth() != 1) { //open libary returns non null 1 width images which are empty
			imageUrl = getImageURLFromOpenLibrary(isbn); 
		} else {
			image = downloadImageFromWordPower(isbn);
			if(image != null) {
				imageUrl =  getImageURLFromWordPower(isbn);
			}
		}
		if(image == null) {
			imageUrl = getImageURLFromAZ(isbn);
			image = getImageFromURL(imageUrl);
		}

		
		//Save to file system
		
		BufferedImage resizedImage = resizeImage(image, 166);
		
		if(image != null) {
			saveImageToLocalFileSystem(resizedImage, "c://isbn//" + isbn +  ".jpg");
			saveImageToLocalFileSystem(image, "c://isbn//original//" + isbn +  ".jpg");
			return imageUrl;
		}
		
		return null;
	}
	
	/**
	 * Cycle through options (az, open library etc)
	 * and see if images exist at those locations. 
	 * Must have a preference, need to see which is best quality
	 * but for now assume open library as it's the biggest file
	 */
	@Override	
	public BufferedImage getImageFromURL(String urlLocation) {
		BufferedImage image = null;
		try {
			URL url = new URL(urlLocation);
			image = ImageIO.read(url);
		} catch (IOException e) {
//			e.printStackTrace();
		}
		return image;
	}	
	
	private BufferedImage downloadImageFromWordPower(String isbn) {
		return getImageFromURL(getImageURLFromWordPower(isbn));
	}
	
	private BufferedImage downloadImageFromOpenLibrary(String isbn) {
		return getImageFromURL(getImageURLFromOpenLibrary(isbn));
	}	
	
	private String getImageURLFromWordPower(String isbn) {
		return "http://images.word-power.co.uk/images/product_images/" + isbn + ".jpg";
	}
	
	private String getImageURLFromOpenLibrary(String isbn) {
		return "http://covers.openlibrary.org/b/isbn/" + isbn + "-M.jpg";
	}
	
	@Override
	public void saveImageToLocalFileSystem(BufferedImage image,
			String fileLocation) {
	    File outputFile = new File(fileLocation);
	    try {
			ImageIO.write(image, "jpg", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	@Override
	public String getImageURLFromAZ(String isbn) {
//		isbn = "9781407109527"; //for testing
		String drillDownURL = null; 
   	  	String imageURL = null;
   	  	String smallImageURL = null;
		try {
			URL url = new URL("http://www.amazon.co.uk/s/ref=nb_sb_noss?url=search-alias%3Dstripbooks&field-keywords=" + isbn + "&rh=n%3A266239%2Ck%3A" + isbn + "&ajr=0");
			URLConnection conn = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			//Skip through the bumf
			reader.skip(18204 + 13675);
			
		    String line;
		
		    while ((line = reader.readLine()) != null) {
//		    		count = count + line.length();
//		    		System.out.println(line);
		    		if(line.contains("<div class=\"image\"")){
		    			//Can get small image
		    			break;
		    		}
		    		if(line.contains("\"productImage\"")){
		    			break;
		    		}
		    		if(line.contains("id=\"noResultsTitle\">")){
//		    			System.out.println("Get no results title for isbn " + isbn);
		    			//Lost cause
		    			return null;
		    		}
		    }//end while
		    
		    //Have got a line with all the info we want! get url for link to page with bigger picture
		    int startIndex = line.indexOf("f=\"");
		    int endIndex = line.indexOf("\">", startIndex);
		    if(startIndex == -1){
		    	//Think it's a lost hope
		    	return null;
		    }
		    drillDownURL = line.substring(startIndex + 3, endIndex);
		    line = reader.readLine();
		    line = reader.readLine();
		    line = reader.readLine();
		    line = reader.readLine();
//		    line = reader.readLine();
		    int start = line.indexOf(" src=\"http");
		    int end = line.indexOf("\" class");
		    if(start != -1 && end != -1) {
		    	smallImageURL = line.substring(start + 6, end); //We have small image
		    } else {
		    	System.out.println("Cannot get small url from line " + line);
		    	//Keep going
		    }
		    reader.close();
		    System.out.println("DrilldownURL = " + drillDownURL);
	    }catch(Exception e) {
//	    	e.printStackTrace();
	    }
		
		//Now drilldown
		try {
			URL url = new URL(drillDownURL);
			URLConnection conn = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			//Skip through the bumf
//			reader.skip(119422);
			
		    String line;
		
		    while ((line = reader.readLine()) != null) {
//		    		System.out.println(line);
		    		if(line.indexOf("\"main-image-inner-wrapper\"") != -1){
		    			break;
		    		}
		    }//end while 
		    line = reader.readLine(); //skip one line
		    
		    //Have got a line with all the info we want! get url for link to page with bigger picture
		    int startIndex = line.indexOf("src");
		    int endIndex = line.indexOf("jpg", startIndex);
		    imageURL = line.substring(startIndex + 5, endIndex + 3);
//		    System.out.println("Image url = " + imageURL);
		    reader.close();
	    }catch(Exception e) {
//	    	e.printStackTrace();
	    }
		if(imageURL == null) imageURL = smallImageURL;
		
		if(imageURL != null && imageURL.indexOf("no-img-lg-uk") != -1) {
			//This is a no-image image, no use at all
			imageURL = null;
		}
		
		//Now remove 
		//-sticker-arrow-click
		if(imageURL != null && imageURL.indexOf("-sticker-arrow-click") != -1) {
			imageURL = imageURL.replace("-sticker-arrow-click", "");
		}
		return imageURL;	
	}	
	
	@Override
	public BufferedImage resizeImage(BufferedImage originalImage, int height){
		int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		
		double originalHeight = originalImage.getHeight();
		double originalWidth = originalImage.getWidth();
		
		double ratio = originalHeight / originalWidth;
		// r = h / w;
		// h = r * w;
		// w = h / r;
		Double newWidth = (double)height / ratio;
		
		BufferedImage resizedImage = new BufferedImage(newWidth.intValue(), height, type);
		
        Graphics2D g = resizedImage.createGraphics();
        
  		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);	
        g.drawImage(originalImage, 0, 0, newWidth.intValue(), height, null);	
        g.dispose();
        return resizedImage;
	}	
	
	private static BufferedImage resizeImage(String isbn){
		URL url = null;
		try {
			url = new URL("http://images.word-power.co.uk/images/product_images/" + isbn + ".jpg");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    BufferedImage originalImage = null;
		try {
			originalImage = ImageIO.read(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
	    
	    double originalHeight = originalImage.getHeight();
	    double originalWidth = originalImage.getWidth();
	    
	    double ratio = originalHeight / originalWidth;
	    // r = h / w;
	    // h = r * w;
	    // w = h / r;
	    Double newWidth = 166 / ratio;
		BufferedImage resizedImage = new BufferedImage(newWidth.intValue(), 166, type);
		Graphics2D g = resizedImage.createGraphics();
		g.setComposite(AlphaComposite.Src);
		 
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
		RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);		
		g.drawImage(originalImage, 0, 0, newWidth.intValue(), 166, null);
		g.dispose();
	 

		
		return resizedImage;
	}		
	
}
