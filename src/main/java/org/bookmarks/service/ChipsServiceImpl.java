package org.bookmarks.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
<<<<<<< HEAD
import java.util.Date;
import java.util.List;

=======
import java.util.HashSet;
import java.util.List;

import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLSocket;
import javax.transaction.Transactional;

>>>>>>> 7fc11cb... getOrders now working
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bookmarks.domain.BookmarksAccount;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerType;
import org.bookmarks.domain.Event;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.ReadingList;
import org.bookmarks.domain.Source;
import org.bookmarks.domain.StockItem;
import org.bookmarks.exceptions.BookmarksException;
import org.bookmarks.repository.CustomerRepository;
import org.bookmarks.repository.StockItemRepository;
<<<<<<< HEAD
import org.bookmarks.website.domain.Customer;
import org.jfree.util.Log;
=======
import org.bookmarks.website.domain.Address;
import org.bookmarks.website.domain.ContactDetails;
import org.bookmarks.website.domain.CreditCard;
import org.bookmarks.website.domain.WebsiteCustomer;
import org.bookmarks.website.domain.OrderLine;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
>>>>>>> 7fc11cb... getOrders now working
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

	@Value("#{ applicationProperties['chips.username'] }")
	private String chips_username;

	@Value("#{ applicationProperties['chips.password'] }")
	private String chips_password;

	@Autowired
	private StandardPBEStringEncryptor jsonEcryptor;

	@Autowired
	private Environment environment;

	@Autowired
	private CustomerOrderService customerOrderService;

	private final Logger logger = LoggerFactory.getLogger(ChipsServiceImpl.class);

	private HttpHost target;

	private CredentialsProvider credsProvider;

	private HttpClientContext localContext;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerService customerService;

	@PostConstruct
	public void postConstruct() {

		logger.info("Building chips host details");

		// Defaults

		Integer port = chips_port == null ? 80 : chips_port;

		String protocol = chips_protocol == null ? "http" : chips_protocol;

		String host = (chips_context == null ? chips_host : chips_host + "/" + chips_context);

		// Build

		logger.info("Host : {}, port : {}, protocal : {}", host, port, protocol);
		logger.debug("Basic Authentication Username : {}, Password : {}", chips_username, chips_password);

		target = new HttpHost(host, port, protocol);

		credsProvider = new BasicCredentialsProvider();

		credsProvider.setCredentials(new AuthScope(target.getHostName(), -1), new UsernamePasswordCredentials(chips_username, chips_password));

		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();

		// Generate BASIC scheme object and add it to the local auth cache
		BasicScheme basicScheme = new BasicScheme();
		authCache.put(target, basicScheme);

		// Add AuthCache to the execution context
		localContext = HttpClientContext.create();
		localContext.setCredentialsProvider(credsProvider);
		localContext.setAuthCache(authCache);

		logger.info("Finished building chips host details");
	}

<<<<<<< HEAD
	// @Override
<<<<<<< HEAD
>>>>>>> fc67d45... Adding showHome
=======
=======
	@Override
>>>>>>> 407a726... Cleaned up basic auth
	public void uploadBrochure(InputStream in) throws SftpException, JSchException, IOException {

		Session session = null;

		try {
			JSch jsch = new JSch();

			String knownHostsFilename = "/root/.ssh/known_hosts";
			jsch.setKnownHosts(knownHostsFilename);

			session = jsch.getSession(sftpUsername, sftpHost, 2298);
			// non-interactive version. Relies in host key being in known-hosts file
			session.setPassword(sftpPassword);

			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();

			ChannelSftp sftpChannel = (ChannelSftp) channel;

			sftpChannel.put(in, "/images/brochure.pdf", ChannelSftp.OVERWRITE);

			sftpChannel.exit();
		} catch (Exception e) {
			logger.error("FTP error", e);
		} finally {
			if (session != null)
				session.disconnect();
		}
	}

<<<<<<< HEAD
	// @Override
>>>>>>> 49e2612... Added upload brochure functionality
=======
>>>>>>> 407a726... Cleaned up basic auth
	private void uploadImageToChips(StockItem stockItem) throws SftpException, JSchException, IOException {
		
		if(!isProduction()) {
			logger.info("Not uploading image as this isn't production");
<<<<<<< HEAD
			//Don't do it!
			//return;
=======
			return;
>>>>>>> ed4726e... Adding logging to edit stockitem
		}
<<<<<<< HEAD
		
=======

		String filename = imageFileLocation + "original" + File.separator + stockItem.getImageFilename();

		logger.info("Attempting to upload image {} to host {}", filename, sftpHost);
		logger.debug("Username {}", sftpUsername);
		logger.debug("port {}", 2298);

>>>>>>> d383330... ADding logging
		Session session = null;
		
		try {
			JSch jsch = new JSch();

<<<<<<< HEAD
<<<<<<< HEAD
			String knownHostsFilename = "/home/hal/.ssh/known_hosts";
			jsch.setKnownHosts( knownHostsFilename );
=======
			String knownHostsFilename = "/root/.ssh/known_hosts";
<<<<<<< HEAD
<<<<<<< HEAD
			jsch.setKnownHosts(knownHostsFilename);
>>>>>>> f35c91c... Chaning location of known hosts
=======
			jsch.setKnownHosts( knownHostsFilename );
<<<<<<< HEAD
=======
			jsch.setKnownHosts( "/root/.ssh/known_hosts" );
<<<<<<< HEAD
>>>>>>> daac66b... Adding customer search option merge
			jsch.addIdentity( "/root/.ssh/id_rsa.pub" );
>>>>>>> 1e478db... Added shh key for sftp
=======
			jsch.addIdentity( "/root/.ssh/id_rsa" );
>>>>>>> 0fc7bb9... Adding idneity
=======
			jsch.addIdentity( "/root/.ssh/id_rsa" );
>>>>>>> f7fb51f... Using private key for sftp, not public
=======
			jsch.setKnownHosts("/root/.ssh/known_hosts");
			jsch.addIdentity("/root/.ssh/id_rsa");
>>>>>>> 7fc11cb... getOrders now working

<<<<<<< HEAD
			session = jsch.getSession( sftpUsername, sftpHost, 2298 );    
			// non-interactive version. Relies in host key being in known-hosts file
=======
			session = jsch.getSession(sftpUsername, sftpHost, 2298);
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
			// non-interactive version. Relies in host key being in known-hosts
			// file
<<<<<<< HEAD
>>>>>>> 88dbf5a... ADding logging
			session.setPassword( sftpPassword );
=======
=======
			// non-interactive version. Relies in host key being in known-hosts file

			//Now using ssh keys (see addIdentity)
>>>>>>> daac66b... Adding customer search option merge
		//	session.setPassword( sftpPassword );
>>>>>>> 1e478db... Added shh key for sftp
=======
			// non-interactive version. Relies in host key being in known-hosts
			// file

			// Now using ssh keys (see addIdentity)
			// session.setPassword( sftpPassword );
>>>>>>> 7fc11cb... getOrders now working
=======
>>>>>>> 407a726... Cleaned up basic auth

			session.connect();

			Channel channel = session.openChannel( "sftp" );
			channel.connect();

			ChannelSftp sftpChannel = (ChannelSftp) channel;

<<<<<<< HEAD
			//SFTP up original file
			File originalFile = new File(imageFileLocation + "original" + File.separator + stockItem.getImageFilename());
=======
			// SFTP up original file
<<<<<<< HEAD
			File originalFile = new File( filename );
>>>>>>> 88dbf5a... ADding logging
=======
			File originalFile = new File(filename);
>>>>>>> 7fc11cb... getOrders now working
			InputStream in = FileUtils.openInputStream(originalFile);
			
			sftpChannel.put(in, "/images/original/" + stockItem.getImageFilename(), ChannelSftp.OVERWRITE);
			
			//sFTP up original file
			File thumbnailFile = new File(imageFileLocation + "150" + File.separator + stockItem.getImageFilename());
			InputStream inThumbnail = FileUtils.openInputStream(thumbnailFile);
			
			sftpChannel.put(inThumbnail, "/images/150/" + stockItem.getImageFilename(), ChannelSftp.OVERWRITE);			

			sftpChannel.exit();
<<<<<<< HEAD
		} catch(Exception e) {
=======

			logger.info("Image upload successful");

		} catch (Exception e) {
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> 88dbf5a... ADding logging
			logger.error("FTP error", e);
=======
			logger.error("Image upload failed!", e);
>>>>>>> 2192327... Formatting and better logging
=======
			logger.error("JSCH error. Image upload failed!", e);
>>>>>>> 1e478db... Added shh key for sftp
		} finally {
			if(session != null) session.disconnect();
		}
	}
	
	@Override
	public void syncStockItemWithChips(StockItem stockItem) throws ClientProtocolException, IOException, SftpException, JSchException {
<<<<<<< HEAD
		 
		//First upload image if necessary
		if(stockItem.getPutImageOnWebsite() && stockItem.getImageFilename() != null && !stockItem.getImageFilename().isEmpty()) {
=======

		logger.info("Attempting to sync stockitem with chips");

		// First upload image if necessary
		if (stockItem.getPutImageOnWebsite() && stockItem.getImageFilename() != null && !stockItem.getImageFilename().isEmpty()) {
>>>>>>> 88dbf5a... ADding logging
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
<<<<<<< HEAD
<<<<<<< HEAD
		
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
		
=======
=======
>>>>>>> 1f51618... Fixing session search for supplier deliveries

		logger.debug(jsonStockItem);

		// Now post up to chips
		CloseableHttpClient httpclient = getHttpClient();

		HttpPost httpPost = getHttpPost("/website/syncStockItemFromBeansWithJson", "jsonStockItem", jsonStockItem);

>>>>>>> 1978e06... Fixing session search for supplier returns
		CloseableHttpResponse response = httpclient.execute(httpPost);

		StatusLine status = response.getStatusLine();

		checkStatus(status);

		try {
<<<<<<< HEAD
		    HttpEntity entity = response.getEntity();
		    String returnCode = EntityUtils.toString(entity);
		    //if(EntityUtils.toString(entity) != "success") throw new BookmarksException("Cannot update chips with filename information for stockitem " + stockItem.getId());
		    EntityUtils.consume(entity);
		    // do something useful with the response body
		    // and ensure it is fully consumed
=======
			HttpEntity entity = response.getEntity();
			String returnCode = EntityUtils.toString(entity);
			// if(EntityUtils.toString(entity) != "success") throw new
			// BookmarksException("Cannot update chips with filename information
			// for stockitem "
			// + stockItem.getId());
			EntityUtils.consume(entity);
			// do something useful with the response body
			// and ensure it is fully consumed

>>>>>>> 88dbf5a... ADding logging
		} finally {
		    response.close();
		}

		logger.info("Successfully synced with chips");
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
<<<<<<< HEAD
		    response.close();
		}		
	} 
	
=======
			response.close();
		}
	}

	private CloseableHttpResponse execute(String url, String name, String value) throws ClientProtocolException, IOException {

		// Get Post
		HttpPost httpPost = getHttpPost(url, name, value);

		CredentialsProvider credsProvider = new BasicCredentialsProvider();

		// credsProvider.setCredentials( new AuthScope(AuthScope.ANY_HOST, -1),
		// new UsernamePasswordCredentials("little", "large"));
		credsProvider.setCredentials(new AuthScope(target.getHostName(), -1), new UsernamePasswordCredentials("little", "large"));

		CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

		// // Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// // Generate BASIC scheme object and add it to the local
		// // auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(target, basicAuth);
		//
		// // Add AuthCache to the execution context
		HttpClientContext localContext = HttpClientContext.create();
		localContext.setCredentialsProvider(credsProvider);
		localContext.setAuthCache(authCache);

		CloseableHttpResponse response = httpclient.execute(httpPost, localContext);

		return response;
	}

	private HttpPost getHttpPost(String uri, String name, String value) throws UnsupportedEncodingException {

		HttpPost httpPost = getHttpPost(uri);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		nvps.add(new BasicNameValuePair(name, value));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		// httpPost.setEntity(new UrlEncodedFormEntity(nvps));

		return httpPost;
	}

	private HttpPost getHttpPost(String uri) {
		String url = chips_protocol + "://" + chips_host;
		if (chips_port != 80 && !chips_protocol.equals("https")) {
			url = url + ":" + chips_port;
		}

		if (!chips_context.isEmpty()) {
			url = url + "/" + chips_context;
		}
		url = url + uri;

		logger.debug("Creating HTTP Post with url " + url);

		HttpPost httpPost = new HttpPost(url);

		return httpPost;
	}

	private HttpGet getHttpGet(String uri) {
		String url = chips_protocol + "://" + chips_host;

		if (chips_port != 80 && !chips_protocol.equals("https")) {
			url = url + ":" + chips_port;
		}

		if (!chips_context.isEmpty()) {
			url = url + "/" + chips_context;
		}
		url = url + uri;

		logger.debug("Creating HTTP Get with url " + url);

		HttpGet httpGet = new HttpGet(url);

		//httpGet.addHeader("accepts", MediaType.APPLICATION_JSON_VALUE);

		return httpGet;
	}

	private CloseableHttpClient getHttpClient() {

		CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

		return httpclient;
	}
>>>>>>> 7fc11cb... getOrders now working

	private void checkStatus(CloseableHttpResponse response) throws IOException {
	    HttpEntity entity = response.getEntity();
	    String returnMessage = EntityUtils.toString(entity);
	    if(!returnMessage.equals("success")) throw new BookmarksException(returnMessage);
	    EntityUtils.consume(entity);
	}

	@Override
<<<<<<< HEAD
	public List<Customer> getOrders() throws ClientProtocolException, IOException {
<<<<<<< HEAD
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpPost httpPost = new HttpPost(chipsUrl + "/website/getOrders");
		
		List<NameValuePair> nvps = getSecurityParameters();
		
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		
=======

		if (chipsGetOrders != true) {
			logger.info("Aborting getOrders(), turned off");
			return null;
		}
=======
	@Transactional
	public Collection<WebsiteCustomer> getOrders() throws ClientProtocolException, IOException {
<<<<<<< HEAD
>>>>>>> 7fc11cb... getOrders now working

		CloseableHttpClient httpclient = getHttpClient();

		HttpGet httpGet = getHttpGet("/website/getOrders");

<<<<<<< HEAD
>>>>>>> 84d9d23... Adding accepts headers
		CloseableHttpResponse response = httpclient.execute(httpPost);
		
=======
		CloseableHttpResponse response = httpclient.execute(httpGet);

>>>>>>> 7fc11cb... getOrders now working
		StatusLine status = response.getStatusLine();
<<<<<<< HEAD
		
=======

		logger.info("/website/getOrders return status : " + status);

>>>>>>> 973e5cd... Adding loggin
		checkStatus(status);
		

		String jsonCustomers = null;
<<<<<<< HEAD
=======

>>>>>>> fc67d45... Adding showHome
		try {
<<<<<<< HEAD
		    System.out.println(response.getStatusLine());
		    HttpEntity entity = response.getEntity();
		    jsonCustomers = EntityUtils.toString(entity);
		    EntityUtils.consume(entity);
		    // do something useful with the response body
		    // and ensure it is fully consumed
=======
			HttpEntity entity = response.getEntity();
			jsonCustomers = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
>>>>>>> 1494835... Got zed scrapper scraping
		} finally {
		    response.close();
		}
<<<<<<< HEAD
		List<Customer> chipsCustomers = new JSONDeserializer<List<Customer>>().deserialize( jsonCustomers );
=======
=======
RestTemplate restTemplate = new RestTemplate();

 restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor("little", "bastard"));
		// ResponseEntity<List<WebsiteCustomer>> rateResponse =
		//         restTemplate.exchange("https://bookmarksbookshop.co.uk/website/getOrders",
		//                     HttpMethod.GET, null, new ParameterizedTypeReference<List<WebsiteCustomer>>() {
		//             });
		// List<WebsiteCustomer> chipsCustomers = rateResponse.getBody();
		//
		// RestTemplate restTemplate = new RestTemplate();
		// 	 Collection<WebsiteCustomer> quote = restTemplate.getForObject("http://gturnquist-quoters.cfapps.io/api/random", List<WebsiteCustomer>.class);
		// 	 log.info(quote.toString());

String json = restTemplate.getForObject("https://bookmarksbookshop.co.uk/website/getOrders", String.class);
// Decrypt json
		String decryptedJson = jsonEcryptor.decrypt(json);
>>>>>>> 0b8750a... Converted getOrders to use Spring RestTemplate

		logger.debug("{}", decryptedJson);

		List<WebsiteCustomer> chipsCustomers = new ObjectMapper().readValue(decryptedJson, new TypeReference<List<WebsiteCustomer>>() {
		});

		logger.info("Have retrieved " + chipsCustomers.size() + " chips customer orders");

		// Now persist
		saveOrders(chipsCustomers);

		return chipsCustomers;

	}
// 	@Override
// 	@Transactional
// 	public Collection<WebsiteCustomer> getOrders() throws ClientProtocolException, IOException {
//
// 		CloseableHttpClient httpclient = getHttpClient();
//
// 		HttpGet httpGet = getHttpGet("/website/getOrders");
//
// 		CloseableHttpResponse response = httpclient.execute(httpGet);
//
// 		StatusLine status = response.getStatusLine();
//
// 		logger.info("/website/getOrders return status : " + status);
//
// 		checkStatus(status);
//
// 		String jsonCustomers = null;
//
// 		try {
// 			HttpEntity entity = response.getEntity();
// 			jsonCustomers = EntityUtils.toString(entity);
// 			EntityUtils.consume(entity);
// 		} finally {
// 			response.close();
// 		}
//
// 		// Decrypt json
// 		String decryptedJson = jsonEcryptor.decrypt(jsonCustomers);
//
// //		logger.debug("{}", decryptedJson);
//
// 		List<WebsiteCustomer> chipsCustomers = new ObjectMapper().readValue(decryptedJson, new TypeReference<List<WebsiteCustomer>>() {
// 		});
//
// 		logger.info("Have retrieved " + chipsCustomers.size() + " chips customer orders");
//
// 		// Now persist
// 		saveOrders(chipsCustomers);
//
// 		return chipsCustomers;
// 	}

	private void saveOrders(List<WebsiteCustomer> chipsCustomers) {

<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> fc67d45... Adding showHome
		return chipsCustomers;
=======
		for (WebsiteCustomer chipsCustomer : chipsCustomers) { 
			
=======
		for (WebsiteCustomer chipsCustomer : chipsCustomers) {

>>>>>>> 0b8750a... Converted getOrders to use Spring RestTemplate
			ContactDetails descryptedContactDetails = chipsCustomer.getContactDetails();

			CreditCard decryptedCreditCard = chipsCustomer.getCreditCard();

			Address decryptedAddress = chipsCustomer.getAddress();

			// Check if customers exist using email, if doesn't exist create new beans customer
			Customer beansCustomer = customerRepository.getByEmail(chipsCustomer.getContactDetails().getEmail());

			if (beansCustomer == null) {

				beansCustomer = new org.bookmarks.domain.Customer();

				// Set is from web
				beansCustomer.setCustomerType(CustomerType.WEB);

				beansCustomer.setFirstName(chipsCustomer.getFirstName());
				beansCustomer.setLastName(chipsCustomer.getLastName());
				beansCustomer.setAddress(decryptedAddress);

				BookmarksAccount account = new BookmarksAccount();
				beansCustomer.setBookmarksAccount(account);
			}

			beansCustomer.setContactDetails(descryptedContactDetails);

			beansCustomer.setWebAddress(decryptedAddress);

			// TODO What to do about name? Maybe need webname

			customerService.saveOrUpdate(beansCustomer);

			// Build up Beans CustomerOrderLines
			beansCustomer.setCustomerOrderLines(new HashSet<CustomerOrderLine>());

			for (OrderLine chipsOl : chipsCustomer.getOrders()) {

				logger.debug( "{}", chipsOl.getWebReference() );
				logger.debug( "{}", chipsOl.getStockItem().getId() );

				CustomerOrderLine beansOl = new CustomerOrderLine();

				beansOl.setAmount(new Long(chipsOl.getQuantity()));
				beansOl.setSellPrice(chipsOl.getSellPrice());
				beansOl.setPostage(chipsOl.getPostage());
				beansOl.setWebReference(chipsOl.getWebReference());

				if (chipsCustomer.getOrders().size() > 1) {
					beansOl.setIsMultipleOrder(true);
				} else {
					beansOl.setIsMultipleOrder(false);
				}

				StockItem stockItem = stockItemService.get(chipsOl.getStockItem().getId());

				beansOl.setStockItem(stockItem);
				beansOl.setAddress(chipsCustomer.getAddress());

				beansCustomer.getCustomerOrderLines().add(beansOl);

				logger.info("******");
				logger.info("StockItem : " + stockItem.getTitle());
				logger.info("Quantity : " + beansOl.getAmount());
				logger.info("Web ref : " + beansOl.getWebReference());
				logger.info("******");

			}

			// The customer order mechanism used by beans
			CustomerOrder customerOrder = new CustomerOrder();

			customerOrder.setPaymentType(chipsCustomer.getPaymentType());

			customerOrder.setDeliveryType(chipsCustomer.getDeliveryType());

			customerOrder.setCreditCard(decryptedCreditCard);

			customerOrder.setCustomer(beansCustomer);

			customerOrder.setSource(Source.WEB);

			customerOrderService.save(customerOrder, beansCustomer.getCustomerOrderLines());
		} // end for
>>>>>>> 7fc11cb... getOrders now working
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
<<<<<<< HEAD
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpPost httpPost = new HttpPost(chipsUrl + "/website/buildIndex");
		
		List<NameValuePair> nvps = getSecurityParameters();
		
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		
		CloseableHttpResponse response = httpclient.execute(httpPost);
		
=======

		CloseableHttpClient httpclient = getHttpClient();

		HttpGet httpGet = getHttpGet("/website/buildIndex");

		CloseableHttpResponse response = httpclient.execute( httpGet );

>>>>>>> e444a65... Fixed null context and port for website
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
<<<<<<< HEAD
		}	
		if(status.getStatusCode() == 424) {
=======
		}
		if (status.getStatusCode() == 401) {
			throw new BookmarksException("Error, failed basic authentication " + status.getStatusCode());
		}
		if (status.getStatusCode() == 424) {
>>>>>>> 407a726... Cleaned up basic auth
			throw new BookmarksException("Error, cannot find image " + status.getStatusCode());
		}
<<<<<<< HEAD
		if(status.getStatusCode() == 403) {
			throw new BookmarksException("Error, bad time stamp " + status.getStatusCode());
		}			
		if(status.getStatusCode() != 200) {
=======
		if (status.getStatusCode() == 403) {
			throw new BookmarksException("Error, security (perhaps csrf?) " + status.getStatusCode());
		}
		if (status.getStatusCode() != 200) {
>>>>>>> 7fc11cb... getOrders now working
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
<<<<<<< HEAD
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpPost httpPost = new HttpPost(chipsUrl + "/website/updateEvents");
		
		List<NameValuePair> nvps = getSecurityParameters();
		
		logger.info("Json " + eventsAsJson);
		
		nvps.add(new BasicNameValuePair("eventsAsJson", eventsAsJson));
		
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		
		CloseableHttpResponse response = httpclient.execute(httpPost); 
		
=======

		if(logger.isDebugEnabled()) {
=======

		if(logger.ifDebugEnabled()) {
>>>>>>> 1f51618... Fixing session search for supplier deliveries
			logger.info("Sending json " + eventsAsJson);
		}

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