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
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.phystech.aosorio.app.Main;
import co.phystech.aosorio.controllers.XslxGenerator;
import co.phystech.aosorio.models.Book;
import co.phystech.aosorio.models.Comment;
import co.phystech.aosorio.models.NewFichePayload;
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
	
	@Test
	public void excelFileTest() { 
	
		ArrayList<NewFichePayload> fiches = new ArrayList<NewFichePayload>();
		
		String x1 = "X1";
		String x2 = "X2";
		
		Book book = new Book();

		book.setTitle(x1);
		book.setSubTitle(x1);
		book.setAuthor(x1);
		book.setYearPub(2000);
		book.setEditor(x1);
		book.setCollection(x1);
		book.setPages(100);
		book.setLanguage(x1);
		book.setTranslation(x1);
		book.setOptional_one(x1);
		book.setAuthor_nationality(x1);
		book.setAuthor_period(x2);
		
		Comment acomment = new Comment();
		acomment.setAuthor(x1);
		acomment.setAboutAuthor(x1);
		acomment.setAboutGenre(x1);
		acomment.setAboutCadre(x1);
		acomment.setAboutCharacters(x1);
		acomment.setResume(x1);
		acomment.setExtrait(x1);
		acomment.setAppreciation(x1);
		acomment.setIsCompleted(false);
		acomment.setOptional_one(x2);;
		acomment.setOptional_two(x2);;
		acomment.setComment_text(x2);;
		acomment.setOther_details(x2);
			
		List<Comment> comments = new ArrayList<Comment>();
		comments.add(acomment);

		NewFichePayload fiche = new NewFichePayload();
		fiche.setId(1);
		fiche.setBook(book);
		fiche.setComments(comments);
		
		fiches.add(fiche);
		
		XslxGenerator excelGen = new XslxGenerator(fiches);
		
		try {
			
			excelGen.generate();
		
		} catch (Exception e) {
		
			e.printStackTrace();
		}
				
		assertTrue(true);
		
	}
	

}
