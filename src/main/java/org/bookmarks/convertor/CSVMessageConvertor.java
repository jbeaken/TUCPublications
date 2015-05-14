package org.bookmarks.convertor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.bookmarks.bean.CSVResponse;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public class CSVMessageConvertor extends AbstractHttpMessageConverter<CSVResponse> {

	public static final MediaType MEDIA_TYPE = new MediaType("text", "csv", Charset.forName("utf-8"));

	private static final CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");

	public CSVMessageConvertor() {
		super(MEDIA_TYPE);
	}

	protected boolean supports(Class<?> clazz) {
		return CSVResponse.class.equals(clazz);
	}

	protected void writeInternal(CSVResponse response, HttpOutputMessage output) throws IOException, HttpMessageNotWritableException {

		output.getHeaders().setContentType(MEDIA_TYPE);

		output.getHeaders().set("Content-Disposition", "attachment; filename=\"" + response.getFilename() + "\"");

		OutputStream out = output.getBody();

		CSVPrinter csvFilePrinter = new CSVPrinter(new OutputStreamWriter(out), csvFileFormat);

		List<String[]> rows = response.getRecords();

		csvFilePrinter.printRecords(rows);

//		for(String obj : rows) {
//			csvFilePrinter.print(obj);
//			csvFilePrinter.println();
//		}

		csvFilePrinter.close();
	}

	@Override
	protected CSVResponse readInternal(Class<? extends CSVResponse> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		// TODO Auto-generated method stub
		return null;
	}
}
