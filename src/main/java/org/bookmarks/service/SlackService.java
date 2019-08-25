package org.bookmarks.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ullink.slack.simpleslackapi.*;
import com.ullink.slack.simpleslackapi.impl.*;

@Service
public class SlackService {

	@Value("#{ applicationProperties['slack-bot-auth-token'] }")
	private String slackBotAuthToken;

	@Autowired
	private Environment environment;

	private final Logger logger = LoggerFactory.getLogger(SlackService.class);

	public void post() throws java.io.IOException {

		logger.info("posting to slack");

		SlackSession session = SlackSessionFactory.createWebSocketSlackSession("xoxb-277950732593-59sYYh9qBg23TW8Tf5FB9hn7");


		session.connect();

		SlackChannel channel = session.findChannelByName("general"); //make sure bot is a member of the channel.

		session.sendMessage(channel, "hi im beans!!" );
	}

	public void todo() throws java.io.IOException {

		logger.info("todo slack");

		// Requires legacy token
		SlackSession session = SlackSessionFactory.createWebSocketSlackSession("xoxp-269044738705-269044738897-277855547008-84efc9dbc7bfaad4a743a8b00c636960");
		session.connect();
		//
		// for(SlackBot bot : session.getBots()) {
		// 	logger.info("Have bot {}", bot);
		// }

		Map<String, String> params = new HashMap<>();

		params.put("text", "do something");
		params.put("command", "/todo");
		//params.put("token", "xoxp-269044738705-269044738897-277855547008-84efc9dbc7bfaad4a743a8b00c636960");


		SlackChannel channel = session.findChannelByName("general"); //make sure bot is a member of the channel.

		//Legacy token requires id
		params.put("channel", channel.getId());

		session.postGenericSlackCommand(params, "/chat.command");
	}
}
