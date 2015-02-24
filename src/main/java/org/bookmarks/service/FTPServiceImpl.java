package org.bookmarks.service;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpException;
//import org.apache.commons.httpclient.UsernamePasswordCredentials;
//import org.apache.commons.httpclient.auth.AuthScope;
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
//import org.apache.commons.httpclient.methods.multipart.Part;
//import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.bookmarks.controller.helper.ISBNConvertor;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.StockItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
public class FTPServiceImpl implements FTPService {
	
	@Override
	public void downloadFile(String remoteFileName, String localFileName) throws SocketException, IOException {
		FTPClient ftp = new FTPClient();
		FileOutputStream fos = null;
		login(ftp);
		fos = new FileOutputStream(new File(localFileName));
		ftp.retrieveFile(remoteFileName, fos);
	}	

	@Override
	public void uploadFile(String fileName , String location) {
		FTPClient ftp = new FTPClient();
		try {
			login(ftp);
			FileInputStream  in = new FileInputStream(fileName);
			ftp.storeFile(location, in);
			ftp.disconnect();
		}catch(Exception e){
			System.out.println(e);
		}
	}	
	
	@Override
	public void login(FTPClient ftp) throws SocketException, IOException {
		ftp.connect("ftp.bookmarksbookshop.co.uk");
		ftp.login("u34059913-kevin","obama08elected");
		ftp.getReplyCode();
	}

	@Override
	public boolean doesFileExist(String fileName) throws IOException{
		FTPClient ftpClient = new FTPClient();
		login(ftpClient);
		return doesFileExist(fileName, ftpClient);
	}

	@Override
	public boolean doesFileExist(String fileName, FTPClient ftpClient) throws IOException{
			FTPFile ftpFile = ftpClient.mlistFile(fileName);
			if(ftpFile == null) return false; //Doesn't exist
			return true;
	}
}
