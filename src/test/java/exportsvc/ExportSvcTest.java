package exportsvc;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.phystech.aosorio.app.Main;
import spark.Spark;

public class ExportSvcTest {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(ExportSvcTest.class);

	private final int BUFFER_SIZE = 4096;

	@BeforeClass
	public static void beforeClass() {
		String[] args = {"test"};
		Main.main(args);
	}

	@AfterClass
	public static void afterClass() {
		Spark.stop();
	}

	@Test
	public void downloadTest() {

		slf4jLogger.info("Testing how to download the genereted word file. " + "Buffer size:" + BUFFER_SIZE);

		boolean result = false;

		int httpResult = 0;
		String httpMessage = "";

		String serverPath = "http://localhost:4567/users/fiches/raw";
		//String tmpFilePath = "";
		String saveDir = "";
		URL appUrl;

		try {

			appUrl = new URL(serverPath);

			HttpURLConnection urlConnection = (HttpURLConnection) appUrl.openConnection();

			httpResult = urlConnection.getResponseCode();
			httpMessage = urlConnection.getResponseMessage();

			if (httpResult == HttpURLConnection.HTTP_OK) {

				String disposition = urlConnection.getHeaderField("Content-Disposition");
				String contentType = urlConnection.getContentType();
				int contentLength = urlConnection.getContentLength();

				int index = disposition.indexOf("filename=");
				
				String fileName = null;
				if (index > 0) {
					String originalName = disposition.substring(index + 9, disposition.length());
					ArrayList<String> tmp = new ArrayList<>(Arrays.asList(originalName.split("\\.")));
					tmp.add(1,"_test.");
					slf4jLogger.debug(tmp.toString());
					fileName = String.join("",tmp);
				}

				slf4jLogger.info("Content-Type = " + contentType);
				slf4jLogger.info("Content-Disposition = " + disposition);
				slf4jLogger.info("Content-Length = " + contentLength);
				slf4jLogger.info("fileName = " + fileName);

				InputStream inputStream = urlConnection.getInputStream();

				String saveFilePath = saveDir + File.separator + fileName;

				FileOutputStream outputStream = new FileOutputStream(saveFilePath);

				int bytesRead = -1;
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}

				outputStream.close();
				inputStream.close();

				result = true;

				slf4jLogger.info("File downloaded");
				
			} else {
				
				slf4jLogger.info("No file to download. Server replied HTTP code: " + httpResult);
			}
			
		} catch (IOException e) {
			
			slf4jLogger.info("downloadTest> fails " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		assertEquals(200, httpResult);
		assertEquals("OK", httpMessage);
		assertTrue(result);

	}

}
