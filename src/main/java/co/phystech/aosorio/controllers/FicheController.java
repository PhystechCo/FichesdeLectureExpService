/**
 * 
 */
package co.phystech.aosorio.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.phystech.aosorio.config.Constants;
import co.phystech.aosorio.models.BackendMessage;
import co.phystech.aosorio.models.Fiche;
import co.phystech.aosorio.models.NewFichePayload;
import spark.Request;
import spark.Response;
import co.phystech.aosorio.services.GeneralSvc;

/**
 * @author AOSORIO
 *
 */
public class FicheController {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(FicheController.class);

	public static Object createFicheDocx(Request pRequest, Response pResponse) {

		BackendMessage returnMessage = new BackendMessage();

		pResponse.type("application/json");

		try {

			ObjectMapper mapper = new ObjectMapper();

			NewFichePayload inputFiche = mapper.readValue(pRequest.body(), NewFichePayload.class);
		
			String ficheUrl = pRequest.headers("Referer");
			slf4jLogger.info(ficheUrl);
					
			DocGenerator docxGen = new DocGenerator(inputFiche,ficheUrl);
			docxGen.generate();

			slf4jLogger.info(pRequest.body());

			pResponse.status(200);

			return returnMessage.getOkMessage(String.valueOf(0));

		} catch (NullPointerException ex) {
		
			slf4jLogger.info("Problem adding fiche - incomplete fiche");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Problem adding fiche");
			
		} catch (IOException jpe) {
		
			slf4jLogger.info("Problem creating the DOCX");
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Problem adding fiche");
		}

	}

	public static Object getFicheDocx(Request pRequest, Response pResponse) {

		slf4jLogger.info(pRequest.headers().toString());

		pResponse.type("application/json");
		
		BackendMessage response = new BackendMessage();

		try {

			slf4jLogger.info("Sending file");

			Path path = Paths.get("./" + "fiche.docx");

			String docx64Encoded = GeneralSvc.convertFileToString(path.toFile());

			return response.getOkMessage(docx64Encoded);

		} catch (NullPointerException e1) {
			slf4jLogger.info("Exception 1: " + e1.getLocalizedMessage());
		} catch (InvalidPathException e2) {
			slf4jLogger.info("Exception 1: " + e2.getLocalizedMessage());
		} catch (IOException e3) {
			slf4jLogger.info("Exception 1: " + e3.getLocalizedMessage());
		}

		return response.getNotOkMessage("File not generated");
		
	}

	public static Object getFicheRaw(Request pRequest, Response pResponse) throws Exception {

		slf4jLogger.info(pRequest.headers().toString());

		byte[] data = null;
		try {

			Path path = Paths.get("./" + "fiche.docx");
			try {
				data = Files.readAllBytes(path);
			} catch (IOException e) {
				throw e;
			}
		} catch (NullPointerException e1) {
			throw e1;
		} catch (InvalidPathException e2) {
			throw e2;
		}

		HttpServletResponse raw = pResponse.raw();
		pResponse.type("application/octet-stream");
		pResponse.header("Content-Disposition", "attachment; filename=fiche.docx");
		pResponse.header("Access-Control-Allow-Origin", "*");

		try {

			raw.getOutputStream().write(data);
			raw.getOutputStream().flush();
			raw.getOutputStream().close();

		} catch (Exception e1) {
			slf4jLogger.info("Exception 1: " + e1.getLocalizedMessage());
		}
		return raw;
	}

	public static Object createFichesExcel(Request pRequest, Response pResponse) {

		BackendMessage returnMessage = new BackendMessage();

		pResponse.type("application/json");

		slf4jLogger.info(pRequest.body());
		
		try {

			ObjectMapper mapper = new ObjectMapper();
			
			Fiche[] inputFiches = mapper.readValue(pRequest.body(),Fiche[].class);
							
			slf4jLogger.info("Input is fine");
			
			XslxGenerator xlsxGen = new XslxGenerator(Arrays.asList(inputFiches));
			
			slf4jLogger.info("Excel generator constructed");

			xlsxGen.generate();

			pResponse.status(200);

			return returnMessage.getOkMessage(String.valueOf(0));

		} catch (NullPointerException e1) {		
			slf4jLogger.info("Problem adding fiche - incomplete fiche");
			slf4jLogger.info("Exception 1: " + e1.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Problem adding fiche");
			
		} catch (IOException e2) {
			slf4jLogger.info("Exception 2: " + e2.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("IO Problem creating the Excel");
			
		} catch (Exception e3) {
			slf4jLogger.info("Exception 3: " + e3.getLocalizedMessage());
			pResponse.status(Constants.HTTP_BAD_REQUEST);
			return returnMessage.getNotOkMessage("Problem creating the Excel");
		}

	}
	
	public static Object getFichesExcel(Request pRequest, Response pResponse) {

		slf4jLogger.info(pRequest.headers().toString());

		pResponse.type("application/json");
		
		BackendMessage response = new BackendMessage();

		try {

			slf4jLogger.info("Sending file");

			Path path = Paths.get("./" + "fichelist.xlsx");

			String docx64Encoded = GeneralSvc.convertFileToString(path.toFile());

			return response.getOkMessage(docx64Encoded);

		} catch (NullPointerException e1) {
			slf4jLogger.info("Exception 1: " + e1.getLocalizedMessage());
		} catch (InvalidPathException e2) {
			slf4jLogger.info("Exception 1: " + e2.getLocalizedMessage());
		} catch (IOException e3) {
			slf4jLogger.info("Exception 1: " + e3.getLocalizedMessage());
		}

		return response.getNotOkMessage("File not generated");
		
	}
	
}
