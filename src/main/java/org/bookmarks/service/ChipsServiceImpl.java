package org.bookmarks.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
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
import org.bookmarks.website.domain.Address;
import org.bookmarks.website.domain.ContactDetails;
import org.bookmarks.website.domain.CreditCard;
import org.bookmarks.website.domain.OrderLine;
import org.bookmarks.website.domain.WebsiteCustomer;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

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
	
	@Value("#{ applicationProperties['sftpPort'] }")
	private Integer sftpPort;

	@Value("#{ applicationProperties['sftpUsername'] }")
	private String sftpUsername;

	@Value("#{ applicationProperties['sftpPassword'] }")
	private String sftpPassword;

	@Value("#{ applicationProperties['chips.url'] }")
	private String chipsUrl;

    private HttpHost target;

    private CredentialsProvider credsProvider;

	@Value("#{ applicationProperties['chips.get.orders'] }")
	private Boolean chipsGetOrders;

	@Autowired
	private StandardPBEStringEncryptor jsonEcryptor;

	@Autowired
	private Environment environment;

	@Autowired
	private CustomerOrderService customerOrderService;

	private final Logger logger = LoggerFactory.getLogger(ChipsServiceImpl.class);

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private RestTemplate chipsRestTemplate;

	@Override
	public void uploadBrochure(InputStream in) throws SftpException, JSchException, IOException {

		Session session = null;
		try {
			session = getJschSession();
			
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();

			ChannelSftp sftpChannel = (ChannelSftp) channel;

			sftpChannel.put(in, "/images/brochure.pdf", ChannelSftp.OVERWRITE);

			sftpChannel.exit();
		
		} finally {
			if (session != null)
				session.disconnect();
		}
	}

	private void uploadImageToChips(StockItem stockItem) throws SftpException, JSchException, IOException {

		Integer port = 22;

		if (!isProduction()) {
			logger.info("Not uploading image as this isn't production");
			return;
		}

		String filename = imageFileLocation + "original" + File.separator + stockItem.getImageFilename();

		logger.info("Attempting to upload image {} to host {}", filename, sftpHost);
		logger.debug("Username {}", sftpUsername);
		logger.debug("port {}", port);

		Session session = null;

		try {
			session = getJschSession();
			
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();

			ChannelSftp sftpChannel = (ChannelSftp) channel;

			// SFTP up original file
			File originalFile = new File(filename);
			InputStream in = FileUtils.openInputStream(originalFile);

			sftpChannel.put(in, "/images/original/" + stockItem.getImageFilename(), ChannelSftp.OVERWRITE);

			// sFTP up original file
			File thumbnailFile = new File(imageFileLocation + "150" + File.separator + stockItem.getImageFilename());
			InputStream inThumbnail = FileUtils.openInputStream(thumbnailFile);

			sftpChannel.put(inThumbnail, "/images/150/" + stockItem.getImageFilename(), ChannelSftp.OVERWRITE);

			sftpChannel.exit();

			logger.info("Image upload successful");


		} finally {
			if (session != null)
				session.disconnect();
		}
	}

	private Session getJschSession() throws JSchException {
		Session session = null;

		JSch jsch = new JSch();

		jsch.setKnownHosts("/home/sftponly/.ssh/known_hosts");
		jsch.addIdentity("/home/sftponly/.ssh/id_rsa");

		session = jsch.getSession(sftpUsername, sftpHost, sftpPort);
		session.setConfig("server_host_key","ecdsa-sha2-nistp256");
		
		return session;
	}

	@Override
	public String syncStockItemWithChips(StockItem stockItem) throws ClientProtocolException, IOException, SftpException, JSchException {

		logger.info("Attempting to sync stockitem with chips : {}", stockItem);

		String result = "";

		// First upload image if necessary
		if (stockItem.getPutImageOnWebsite() && stockItem.getImageFilename() != null && !stockItem.getImageFilename().isEmpty()) {
			try {
				uploadImageToChips(stockItem);
			} catch (Exception e) {
				logger.error("JSCH error. Image upload failed!", e);
				result = "imagefailure";
			}
		}

		// Checks
		if (stockItem.getAlwaysInStock() != null && stockItem.getAlwaysInStock()) {
			stockItem.setQuantityInStock(1l);
		}

		// Get sales figures, salesLastYear and totalSales
		Long salesTotal = stockItemRepository.getTotalSales(stockItem);
		Long salesLastYear = stockItemRepository.getSalesLastYear(stockItem);

		stockItem.setSalesLastYear(salesLastYear.intValue());
		stockItem.setSalesTotal(salesTotal.intValue());

		Category category = categoryService.get(stockItem.getCategory().getId());
		stockItem.setCategory(category);

		// Publisher
		Publisher azPublisher = publisherService.get(stockItem.getPublisher().getId());

		Publisher publisher = new Publisher(azPublisher.getId());
		publisher.setName(azPublisher.getName());

		stockItem.setPublisher(publisher);

		logger.info("Exchanging with chips url {}", chipsUrl);

		org.springframework.http.HttpEntity< StockItem > requestEntity = new org.springframework.http.HttpEntity<>( stockItem );

		logger.debug("HttpEntity body : {}", requestEntity.getBody());

		ResponseEntity<String> response = chipsRestTemplate.exchange(chipsUrl + "/website/syncStockItemFromBeansWithJson", HttpMethod.POST, requestEntity, String.class);

		logger.debug("Chips response : {}", response);

		if(result.equals("")) return response.getBody();

		return result;

	}

	@Override
	public void evictAll() throws ClientProtocolException, IOException {

		CloseableHttpClient httpclient = getHttpClient();

		HttpPost httpPost = getHttpPost("/website/evictAll");

		CloseableHttpResponse response = httpclient.execute(httpPost);

		StatusLine status = response.getStatusLine();

		checkStatus(status);
	}

	@Override
	public String updateReadingLists()  {

		Collection<ReadingList> readingLists = readingListService.getAll();

		logger.info("Have got {} reading lists to put on chips with url {}", readingLists.size(), chipsUrl);

		org.springframework.http.HttpEntity<Object> requestEntity = new org.springframework.http.HttpEntity<>( readingLists );

		logger.debug("HttpEntity body : {}", requestEntity.getBody());

		ResponseEntity<String> result = chipsRestTemplate.exchange(chipsUrl + "/website/updateReadingLists", HttpMethod.POST, requestEntity, String.class);

		logger.info("Readling lists return result : {}", result);

		return result.getBody();
	}

	/**
	 * At present only sending up bouncies, stickies aren't working
	 */
	@Override
	public String updateChips() throws ClientProtocolException, IOException {

		ArrayList<StockItem> stockItems = (ArrayList<StockItem>) stockItemService.getBounciesAndStickies();
		ArrayList<StockItem> strippedStockItems = new ArrayList<StockItem>();

		Collection<ReadingList> readingLists = readingListService.getAll();

		// Transfer to reduce amount of unneeded fluff
		for (StockItem si : stockItems) {

			if (si.getPutOnWebsite() == false || si.getBouncyIndex() == null)
				continue;

			StockItem bouncy = new StockItem();

			bouncy.setId(si.getId());
			bouncy.setTitle(si.getTitle());
			bouncy.setIsbn(si.getIsbn());
			bouncy.setImageFilename(si.getImageFilename());
			bouncy.setBouncyIndex(si.getBouncyIndex());
			bouncy.setStickyCategoryIndex(si.getStickyCategoryIndex());
			bouncy.setStickyTypeIndex(si.getStickyTypeIndex());
			bouncy.setPutImageOnWebsite(si.getPutImageOnWebsite());

			if(si.getPutImageOnWebsite() == false) {
				throw new BookmarksException("StockItem " + si.toString() + " has put image on website set to false, bouncies must have images");
			}

			logger.debug("Adding stockitem to bouncies {}", bouncy);

			strippedStockItems.add(bouncy);
		}

		// Check have enough
		if (strippedStockItems.size() < 16) {
			return "Need more bouncies, only have " + strippedStockItems.size() + ". Add more at 'Manage Bouncies'";
		}

		logger.info("Have got {} bouncies to put on chips with url {}", strippedStockItems.size(), chipsUrl);

		org.springframework.http.HttpEntity< ArrayList<StockItem> > requestEntity = new org.springframework.http.HttpEntity<>( strippedStockItems );

		logger.debug("HttpEntity body : {}", requestEntity.getBody());

		ResponseEntity<String> response = chipsRestTemplate.exchange(chipsUrl + "/website/updateChips", HttpMethod.POST, requestEntity, String.class);

		logger.info("Chips response : {}", response);

		return response.getBody();
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
		logger.debug("Creating HTTP Post with url " + chipsUrl);

		HttpPost httpPost = new HttpPost( chipsUrl );

		return httpPost;
	}

	private HttpGet getHttpGet(String uri) {
		logger.debug("Creating HTTP Get with url " + chipsUrl);

		HttpGet httpGet = new HttpGet(chipsUrl);

		return httpGet;
	}

	private CloseableHttpClient getHttpClient() {

		CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

		return httpclient;
	}

	private void checkStatus(CloseableHttpResponse response) throws IOException {
		HttpEntity entity = response.getEntity();

		String returnMessage = EntityUtils.toString(entity);

		if (!returnMessage.equals("success"))
			throw new BookmarksException(returnMessage);

		EntityUtils.consume(entity);
	}

	private RestTemplate getRestTemplate() {

		RestTemplate restTemplate = new RestTemplate();

		restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor("little", "bastard"));

		return restTemplate;
	}

	@Override
	@Transactional
	public Collection<WebsiteCustomer> getOrders() throws ClientProtocolException, IOException {

		if (chipsGetOrders != true) {
			logger.info("Aborting getOrders(), turned off");
			return null;
		}

		String json = chipsRestTemplate.getForObject("https://bookmarksbookshop.co.uk/website/getOrders", String.class);

		// Decrypt json
		String decryptedJson = jsonEcryptor.decrypt(json);

		//logger.debug("{}", decryptedJson);

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

		for (WebsiteCustomer chipsCustomer : chipsCustomers) {

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
				logger.debug( "{}", chipsOl.getStockItem() );

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
		} // end orderlines for
	}

	@Override
	public void removeConsumedCustomers() throws ClientProtocolException, IOException {

		CloseableHttpClient httpclient = getHttpClient();

		HttpPost httpPost = getHttpPost("/website/removeConsumedCustomers");

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

		RestTemplate restTemplate = getRestTemplate();

		String json = chipsRestTemplate.getForObject("https://bookmarksbookshop.co.uk/website/buildIndex", String.class);

		if( !json.equals("success") ) {
			throw new BookmarksException("Cannot build index " + json);
		}
		logger.info("buildIndex return : {}", json);
	}

	@Override
	public void removeFromWebsite(Long id) {
		stockItemRepository.removeFromWebsite(id);
	}

	@Override
	public void putOnWebsite(Long id) {
		stockItemRepository.putOnWebsite(id);
	}

	// private List<NameValuePair> getSecurityParameters() {
	// List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	// //Get sha512
	// String message = new Date().getTime() + "";
	// String beansSha512 = getSha512(message);
	//
	// nvps.add(new BasicNameValuePair("message", message));
	// nvps.add(new BasicNameValuePair("beansSha512", beansSha512));
	// return nvps;
	// }

	// private String getSha512(String message) {
	// message = message + salt;
	//
	// MessageDigest mDigest = null;
	// try {
	// mDigest = MessageDigest.getInstance("SHA-512");
	// } catch (NoSuchAlgorithmException e) {
	// e.printStackTrace();
	// }
	// byte[] result = mDigest.digest(message.getBytes());
	// StringBuffer sb = new StringBuffer();
	// for (int i = 0; i < result.length; i++) {
	// sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
	// }
	// return sb.toString();
	// }

	private void checkStatus(StatusLine status) {
		if (status.getStatusCode() == 409) {
			throw new BookmarksException("Error, failed authentication " + status.getStatusCode());
		}
		if (status.getStatusCode() == 401) {
			throw new BookmarksException("Error, failed basic authentication " + status.getStatusCode());
		}
		if (status.getStatusCode() == 424) {
			throw new BookmarksException("Error, cannot find image " + status.getStatusCode());
		}
		if (status.getStatusCode() == 403) {
			throw new BookmarksException("Error, security (perhaps csrf?) " + status.getStatusCode());
		}
		if (status.getStatusCode() != 200) {
			throw new BookmarksException("Error, status code " + status.getStatusCode());
		}
	}

	private boolean isProduction() {

		String profile = environment.getActiveProfiles()[0];

		logger.info("Current active profile : " + profile);

		if (profile.equals("dev") || profile.equals("test")) {
			logger.info("Aborting due to profile");
			return false;
		}

		if (profile.equals("prod")) {
			return true;
		}

		throw new BookmarksException("Invalid active profile : " + profile);
	}

	@Override
	public String updateEvents() {

		Collection<Event> events = eventService.getChipsEvents();

		String url = chipsUrl + "/website/updateEvents";

		logger.info("Have got {} events to post to chips with url {}", events.size(), url);

		org.springframework.http.HttpEntity<Object> requestEntity = new org.springframework.http.HttpEntity<>( events );

		logger.debug("HttpEntity body : {}", requestEntity.getBody());

		ResponseEntity<String> result = chipsRestTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

		String response = result.getBody();

		logger.debug("Events exchange result : {}", result);
		logger.info("Events exchange response : {}", response);

		return response;
	}
}
