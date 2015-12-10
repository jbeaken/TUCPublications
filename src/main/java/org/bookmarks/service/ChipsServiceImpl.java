package org.bookmarks.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Event;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.ReadingList;
import org.bookmarks.domain.StockItem;
import org.bookmarks.exceptions.BookmarksException;
import org.bookmarks.repository.StockItemRepository;
import org.bookmarks.website.domain.Customer;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;


@Service
public class ChipsServiceImpl implements ChipsService {
	
	@Autowired
	private StockItemRepository stockItemRepository;
	
	@Autowired
	private StockItemService stockItemService;
	
	@Autowired
	private ReadingListService readingListService;
	
	@Autowired
	private EventService eventService;	
	
	@Autowired
	private CategoryService categoryService;		
	
	@Autowired
	private PublisherService publisherService;
	
	@Value("#{ applicationProperties['imageFileLocation'] }")
	private String imageFileLocation;
	
	@Value("#{ applicationProperties['sftpHost'] }")
	private String sftpHost;
	
	@Value("#{ applicationProperties['sftpUsername'] }")
	private String sftpUsername;
	
	@Value("#{ applicationProperties['sftpPassword'] }")
	private String sftpPassword;
<<<<<<< HEAD
	
	@Value("#{ applicationProperties['chipsUrl'] }")
	private String chipsUrl;	
	
	@Value("#{ applicationProperties['salt'] }")
	private String salt;	
	
	@Autowired private Environment environment;
	
	final static Logger logger = LoggerFactory.getLogger(ChipsServiceImpl.class); 
	
//	@Override
=======

	@Value("#{ applicationProperties['chips.host'] }")
	private String chips_host;

	@Value("#{ applicationProperties['chips.context'] }")
	private String chips_context;

	@Value("#{ applicationProperties['chips.port'] }")
	private Integer chips_port;

	@Value("#{ applicationProperties['chips.protocol'] }")
	private String chips_protocol;

	@Value("#{ applicationProperties['chips.get.orders'] }")
	private Boolean chipsGetOrders;

	@Autowired
	private StandardPBEStringEncryptor jsonEcryptor;

	@Autowired
	private Environment environment;

	final static Logger logger = LoggerFactory.getLogger(ChipsServiceImpl.class);

	private HttpHost target;

	private CredentialsProvider credsProvider;

	private HttpClientContext localContext;

	@PostConstruct
	public void postConstruct() {
		logger.debug("Building chips host details");
		target = new HttpHost(chips_host, chips_port, chips_protocol);

		credsProvider = new BasicCredentialsProvider();

		// credsProvider.setCredentials( new AuthScope(AuthScope.ANY_HOST, -1),
		// new UsernamePasswordCredentials("little", "large"));
		credsProvider.setCredentials(new AuthScope(target.getHostName(), -1), new UsernamePasswordCredentials("little", "large"));

		// // Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// // Generate BASIC scheme object and add it to the local
		// // auth cache
		BasicScheme basicScheme = new BasicScheme();
		authCache.put(target, basicScheme);
		//
		// // Add AuthCache to the execution context
		localContext = HttpClientContext.create();
		localContext.setCredentialsProvider(credsProvider);
		localContext.setAuthCache(authCache);
	}

	// @Override
<<<<<<< HEAD
>>>>>>> fc67d45... Adding showHome
=======
	public void uploadBrochure(InputStream in) throws SftpException, JSchException, IOException {

		Session session = null;

		try {
			JSch jsch = new JSch();

			String knownHostsFilename = "/root/.ssh/known_hosts";
			jsch.setKnownHosts(knownHostsFilename);

			session = jsch.getSession(sftpUsername, sftpHost, 2298);
			// non-interactive version. Relies in host key being in known-hosts
			// file
			session.setPassword(sftpPassword);

			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();

			ChannelSftp sftpChannel = (ChannelSftp) channel;

			// SFTP up original file
		//	InputStream in = FileUtils.openInputStream(file);

			sftpChannel.put(in, "/images/abrochure.pdf", ChannelSftp.OVERWRITE);

			sftpChannel.exit();
		} catch (Exception e) {
			logger.error("FTP error", e);
		} finally {
			if (session != null)
				session.disconnect();
		}
	}

	// @Override
>>>>>>> 49e2612... Added upload brochure functionality
	private void uploadImageToChips(StockItem stockItem) throws SftpException, JSchException, IOException {
		
		if(!isProduction()) {
			logger.info("Not uploading image as this isn't production");
			//Don't do it!
			//return;
		}
		
		Session session = null;
		
		try {
			JSch jsch = new JSch();

<<<<<<< HEAD
			String knownHostsFilename = "/home/hal/.ssh/known_hosts";
			jsch.setKnownHosts( knownHostsFilename );
=======
			String knownHostsFilename = "/root/.ssh/known_hosts";
			jsch.setKnownHosts(knownHostsFilename);
>>>>>>> f35c91c... Chaning location of known hosts

			session = jsch.getSession( sftpUsername, sftpHost, 2298 );    
			// non-interactive version. Relies in host key being in known-hosts file
			session.setPassword( sftpPassword );

			session.connect();

			Channel channel = session.openChannel( "sftp" );
			channel.connect();

			ChannelSftp sftpChannel = (ChannelSftp) channel;

			//SFTP up original file
			File originalFile = new File(imageFileLocation + "original" + File.separator + stockItem.getImageFilename());
			InputStream in = FileUtils.openInputStream(originalFile);
			
			sftpChannel.put(in, "/images/original/" + stockItem.getImageFilename(), ChannelSftp.OVERWRITE);
			
			//sFTP up original file
			File thumbnailFile = new File(imageFileLocation + "150" + File.separator + stockItem.getImageFilename());
			InputStream inThumbnail = FileUtils.openInputStream(thumbnailFile);
			
			sftpChannel.put(inThumbnail, "/images/150/" + stockItem.getImageFilename(), ChannelSftp.OVERWRITE);			

			sftpChannel.exit();
		} catch(Exception e) {
			logger.error("FTP error", e);
		} finally {
			if(session != null) session.disconnect();
		}
	}
	
	@Override
	public void syncStockItemWithChips(StockItem stockItem) throws ClientProtocolException, IOException, SftpException, JSchException {
		 
		//First upload image if necessary
		if(stockItem.getPutImageOnWebsite() && stockItem.getImageFilename() != null && !stockItem.getImageFilename().isEmpty()) {
			uploadImageToChips(stockItem);
		}
		
		//Checks
		if(stockItem.getAlwaysInStock() != null && stockItem.getAlwaysInStock()) {
			stockItem.setQuantityInStock(1l);
		}
		
		//Get sales figures, salesLastYear and totalSales 
		Long salesTotal = stockItemRepository.getTotalSales(stockItem);
		Long salesLastYear = stockItemRepository.getSalesLastYear(stockItem);	
		
		stockItem.setSalesLastYear(salesLastYear.intValue());
		stockItem.setSalesTotal(salesTotal.intValue());
		
		Category category = categoryService.get(stockItem.getCategory().getId());
		stockItem.setCategory(category);
		
		//Publisher 
		Publisher azPublisher = publisherService.get(stockItem.getPublisher().getId());

		Publisher publisher = new Publisher(azPublisher.getId());
		publisher.setName(azPublisher.getName());
		
		stockItem.setPublisher(publisher);
		
		//Serialise using bookmarks domain object
		JSONSerializer jsonSerializer = new JSONSerializer();
		String jsonStockItem = jsonSerializer.include("authors").serialize(stockItem);
		
		logger.info(jsonStockItem);
		
		//Now post up to chips
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpPost httpPost = new HttpPost(chipsUrl + "/website/syncStockItemFromBeansWithJson");
		
		List<NameValuePair> nvps = getSecurityParameters();
		nvps.add(new BasicNameValuePair("jsonStockItem", jsonStockItem));
		 /*
		nvps.add(new BasicNameValuePair("availability", stockItem.getAvailability().toString()));
		nvps.add(new BasicNameValuePair("imageFilename", stockItem.getImageFilename()));
		nvps.add(new BasicNameValuePair("id", stockItem.getId().toString()));
		nvps.add(new BasicNameValuePair("isbn", stockItem.getIsbn()));
		nvps.add(new BasicNameValuePair("sellPrice", stockItem.getSellPrice().toString()));
		nvps.add(new BasicNameValuePair("postage", stockItem.getPostage().toString()));
		nvps.add(new BasicNameValuePair("title", stockItem.getTitle()));
		nvps.add(new BasicNameValuePair("quantityInStock", stockItem.getQuantityInStock().toString()));
		nvps.add(new BasicNameValuePair("type", stockItem.getStockItemType().toString()));
		nvps.add(new BasicNameValuePair("putOnWebsite", stockItem.getPutOnWebsite().toString()));
		nvps.add(new BasicNameValuePair("putImageOnWebsite", stockItem.getPutImageOnWebsite().toString()));
		nvps.add(new BasicNameValuePair("putReviewOnWebsite", stockItem.getPutReviewOnWebsite().toString()));
		nvps.add(new BasicNameValuePair("reviewAsText", stockItem.getReviewAsText()));
		*/
 		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		
		CloseableHttpResponse response = httpclient.execute(httpPost);

		StatusLine status = response.getStatusLine();

		checkStatus(status);

		try {
		    HttpEntity entity = response.getEntity();
		    String returnCode = EntityUtils.toString(entity);
		    //if(EntityUtils.toString(entity) != "success") throw new BookmarksException("Cannot update chips with filename information for stockitem " + stockItem.getId());
		    EntityUtils.consume(entity);
		    // do something useful with the response body
		    // and ensure it is fully consumed
		} finally {
		    response.close();
		}
	}
	
	@Override
	public void evictAll() throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpPost httpPost = new HttpPost(chipsUrl + "/website/evictAll");
		
		List<NameValuePair> nvps = getSecurityParameters();
		
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		
		CloseableHttpResponse response = httpclient.execute(httpPost);
		
		StatusLine status = response.getStatusLine();
		
		checkStatus(status);
	}		
	
	@Override
	public void updateReadingLists() throws ClientProtocolException, IOException {
		
		Collection<ReadingList> readingLists = readingListService.getAll();
		
		
		JSONSerializer serializer = new JSONSerializer();

		String readingListsAsJson = serializer.include("stockItems").serialize(readingLists);
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		 
		HttpPost httpPost = new HttpPost(chipsUrl + "/website/updateReadingLists");
		
		List<NameValuePair> nvps = getSecurityParameters();
		
		nvps.add(new BasicNameValuePair("readingListsAsJson", readingListsAsJson));
		
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		
		CloseableHttpResponse response = httpclient.execute(httpPost); 
		
		try {
		    checkStatus(response);
		} finally {
		    response.close();
		}		
	} 	
	
	@Override
	public void updateChips() throws ClientProtocolException, IOException {
		Collection<StockItem> stockItems = stockItemService.getBounciesAndStickies();
		Collection<StockItem> strippedStockItems = new ArrayList<StockItem>();
		
		Collection<ReadingList> readingLists = readingListService.getAll();
		
		//Transfer to reduce amount of unneeded fluff (could do this is hql I suppose)
		for(StockItem si : stockItems) {
			if(si.getPutOnWebsite() == false) continue; //TODO 
			StockItem bouncy = new StockItem(si.getId());
			bouncy.setTitle(si.getTitle());
			bouncy.setIsbn(si.getIsbn());
			bouncy.setImageFilename(si.getImageFilename());
			bouncy.setBouncyIndex(si.getBouncyIndex());
			bouncy.setStickyCategoryIndex(si.getStickyCategoryIndex());
			bouncy.setStickyTypeIndex(si.getStickyTypeIndex());
			strippedStockItems.add(bouncy);
		}
		
		JSONSerializer serializer = new JSONSerializer();

		String bounciesAndStickiesAsJson = serializer.serialize(strippedStockItems);
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpPost httpPost = new HttpPost(chipsUrl + "/website/updateChips");
		
		List<NameValuePair> nvps = getSecurityParameters();
		
		nvps.add(new BasicNameValuePair("bounciesAndStickiesAsJson", bounciesAndStickiesAsJson));
		
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		
		CloseableHttpResponse response = httpclient.execute(httpPost); 
		
		try {
		    checkStatus(response);
		} finally {
		    response.close();
		}		
	} 
	

	private void checkStatus(CloseableHttpResponse response) throws IOException {
	    HttpEntity entity = response.getEntity();
	    String returnMessage = EntityUtils.toString(entity);
	    if(!returnMessage.equals("success")) throw new BookmarksException(returnMessage);
	    EntityUtils.consume(entity);
	}

	@Override
	public List<Customer> getOrders() throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpPost httpPost = new HttpPost(chipsUrl + "/website/getOrders");
		
		List<NameValuePair> nvps = getSecurityParameters();
		
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		
		CloseableHttpResponse response = httpclient.execute(httpPost);
		
		StatusLine status = response.getStatusLine();
		
		checkStatus(status);
		

		String jsonCustomers = null;
<<<<<<< HEAD
=======

>>>>>>> fc67d45... Adding showHome
		try {
		    System.out.println(response.getStatusLine());
		    HttpEntity entity = response.getEntity();
		    jsonCustomers = EntityUtils.toString(entity);
		    EntityUtils.consume(entity);
		    // do something useful with the response body
		    // and ensure it is fully consumed
		} finally {
		    response.close();
		}
<<<<<<< HEAD
		List<Customer> chipsCustomers = new JSONDeserializer<List<Customer>>().deserialize( jsonCustomers );
=======

		//Decrypt json
		String decryptedJson = jsonEcryptor.decrypt(jsonCustomers);

		List<Customer> chipsCustomers = new JSONDeserializer<List<Customer>>().deserialize(decryptedJson);
		logger.info("Have retrived " + chipsCustomers.size() + " chips orders");

>>>>>>> fc67d45... Adding showHome
		return chipsCustomers;
	}
	
	@Override
	public void removeConsumedCustomers() throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpPost httpPost = new HttpPost(chipsUrl + "/website/removeConsumedCustomers");
		
		List<NameValuePair> nvps = getSecurityParameters();
		
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		
		CloseableHttpResponse response = httpclient.execute(httpPost);
		
		StatusLine status = response.getStatusLine();
		
		checkStatus(status);
		
		try {
		    HttpEntity entity = response.getEntity();
		    EntityUtils.consume(entity);
		} finally {
		    response.close();
		}
	}
	

	@Override
	public void buildIndex() throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpPost httpPost = new HttpPost(chipsUrl + "/website/buildIndex");
		
		List<NameValuePair> nvps = getSecurityParameters();
		
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		
		CloseableHttpResponse response = httpclient.execute(httpPost);
		
		StatusLine status = response.getStatusLine();
		
		checkStatus(status);
		
		try {
		    HttpEntity entity = response.getEntity();
		    EntityUtils.consume(entity);
		} finally {
		    response.close();
		}
	}	
	
	@Override
	public void removeFromWebsite(Long id) {
		stockItemRepository.removeFromWebsite(id);
	}

	@Override
	public void putOnWebsite(Long id) {
		stockItemRepository.putOnWebsite(id);
	}	
	
    private List<NameValuePair> getSecurityParameters() {
    	List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    	//Get sha512
		String message = new Date().getTime() + "";
		String beansSha512 = getSha512(message);
		
		nvps.add(new BasicNameValuePair("message", message));
		nvps.add(new BasicNameValuePair("beansSha512", beansSha512));
		return nvps;
	}

	private String getSha512(String message) {
    	message = message + salt;
    	
		MessageDigest mDigest = null;
		try {
			mDigest = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    byte[] result = mDigest.digest(message.getBytes());
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < result.length; i++) {
	        sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    return sb.toString();
	}
	
	private void checkStatus(StatusLine status) {
		if(status.getStatusCode() == 409) {
			throw new BookmarksException("Error, failed authentication " + status.getStatusCode());
		}	
		if(status.getStatusCode() == 424) {
			throw new BookmarksException("Error, cannot find image " + status.getStatusCode());
		}
		if(status.getStatusCode() == 403) {
			throw new BookmarksException("Error, bad time stamp " + status.getStatusCode());
		}			
		if(status.getStatusCode() != 200) {
			throw new BookmarksException("Error, status code " + status.getStatusCode());
		}
	}
	
	private boolean isProduction() {

		String profile = environment.getActiveProfiles()[0];
		
		logger.info("Current active profile : " + profile);
		
	    if(profile.equals("dev") || profile.equals("test")) {
	    	logger.info("Aborting due to profile");
	    	return false;		    	
	    }

	    if(profile.equals("prod")) {
	    	return true;		    	
	    }
	    
	    throw new BookmarksException("Invalid active profile : " + profile);
	}

	@Override
	public void updateEvents() throws ClientProtocolException, IOException {
		
		String profile = environment.getActiveProfiles()[0];
		
		logger.info("Current active profile : " + profile);
		
		logger.info("Updating events");
		
		Collection<Event> events = eventService.getChipsEvents();
		
		JSONSerializer serializer = new JSONSerializer();
		
		for(Event e : events) {
			logger.info("Uploading event " + e.getName());
		}

		String eventsAsJson = serializer.serialize(events);
<<<<<<< HEAD
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpPost httpPost = new HttpPost(chipsUrl + "/website/updateEvents");
		
		List<NameValuePair> nvps = getSecurityParameters();
		
		logger.info("Json " + eventsAsJson);
		
		nvps.add(new BasicNameValuePair("eventsAsJson", eventsAsJson));
		
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		
		CloseableHttpResponse response = httpclient.execute(httpPost); 
		
=======

		logger.info("Sending json " + eventsAsJson);

		CloseableHttpClient httpclient = getHttpClient();

		HttpPost httpPost = getHttpPost("/website/updateEvents", "eventsAsJson", eventsAsJson);

		CloseableHttpResponse response = httpclient.execute(httpPost);

>>>>>>> fc67d45... Adding showHome
		try {
		    checkStatus(response);
		} finally {
		    response.close();
		}		
	} 
}