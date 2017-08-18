/**
 * 
 */
package co.phystech.aosorio.services;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;

import spark.ResponseTransformer;

/**
 * @author AOSORIO
 *
 */
public class GeneralSvc {
	
	public static String dataToJson(Object data) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			//mapper.enable(SerializationFeature.INDENT_OUTPUT);
			StringWriter sw = new StringWriter();
			mapper.writeValue(sw, data);
			return sw.toString();
		} catch (IOException e) {
			throw new RuntimeException("IOException from a StringWriter?");
		}
	}
	
	public static ResponseTransformer json() {

		return GeneralSvc::dataToJson;
	}

	public static String convertFileToString(File file) throws IOException{
        byte[] bytes = Files.readAllBytes(file.toPath());   
        return new String(Base64.getEncoder().encode(bytes));
    }
	
}
