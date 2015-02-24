package org.bookmarks.service;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

@Service
public class WebsiteImageServiceImpl implements WebsiteImageService {
	
	@Override
	public BufferedImage download(String isbn) {
		BufferedImage image = downloadImageFromOpenLibrary(isbn);
		if(image.getHeight() == 1) {
			image = downloadImageFromWordPower(isbn);
		}
		if(image == null) {
			image = downloadImageFromAZ(isbn);
		}
		return image;
	}
	
	@Override
	public BufferedImage resize(BufferedImage originalImage, int height){
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
	
	
	private BufferedImage downloadImageFromAZ(String isbn) {
		String drillDownURL = null; 
   	  	String imageURL = null;
		try {
			URL url = new URL("http://www.amazon.co.uk/s/ref=nb_sb_noss?url=search-alias%3Dstripbooks&field-keywords=" + isbn + "&rh=n%3A266239%2Ck%3A" + isbn + "&ajr=0");
			URLConnection conn = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			//Skip through the bumf
			reader.skip(18204 + 13675);
			
		    String line;
		
		    while ((line = reader.readLine()) != null) {
//		    		count = count + line.length();
		    		if(line.indexOf("\"productImage\"") != -1){
		    			break;
		    		}
		    }//end while
		    
		    //Have got a line with all the info we want! get url for link to page with bigger picture
		    int startIndex = line.indexOf("f=\"");
		    int endIndex = line.indexOf("\">", startIndex);
		    drillDownURL = line.substring(startIndex + 3, endIndex);
		    reader.close();
		    System.out.println("DrilldownURL = " + drillDownURL);
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
		
		//Now drilldown
		try {
			URL url = new URL(drillDownURL);
			URLConnection conn = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			//Skip through the bumf
			reader.skip(100000);
			
		    String line;
		
		    while ((line = reader.readLine()) != null) {
		    		if(line.indexOf("id=\"prodImageCell\"") != -1){
		    			break;
		    		}
		    }//end while
		    
		    //Have got a line with all the info we want! get url for link to page with bigger picture
		    int startIndex = line.indexOf("src");
		    int endIndex = line.indexOf("\" ", startIndex);
		    imageURL = line.substring(startIndex + 5, endIndex);
		    System.out.println("Image url = " + imageURL);
		    reader.close();
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
		
		return getImageFromURL(imageURL);	
	}
	
	private BufferedImage downloadImageFromWordPower(String isbn) {
		String urlLocation = "http://images.word-power.co.uk/images/product_images/" + isbn + ".jpg";
		return getImageFromURL(urlLocation);
	}
	
	private BufferedImage downloadImageFromOpenLibrary(String isbn) {
		String urlLocation = "http://covers.openlibrary.org/b/isbn/" + isbn + "-M.jpg";
		return getImageFromURL(urlLocation);
	}
	
	private BufferedImage getImageFromURL(String urlLocation) {
		BufferedImage image = null;
		try {
			URL url = new URL(urlLocation);
			image = ImageIO.read(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
}
