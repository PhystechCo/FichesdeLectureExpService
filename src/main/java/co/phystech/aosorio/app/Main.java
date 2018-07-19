package co.phystech.aosorio.app;

import static spark.Spark.*;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.phystech.aosorio.config.Constants;
import co.phystech.aosorio.config.CorsFilter;
import co.phystech.aosorio.config.Routes;
import co.phystech.aosorio.controllers.DocGenerator;
import co.phystech.aosorio.controllers.FicheController;
import co.phystech.aosorio.services.AuthorizeSvc;
import co.phystech.aosorio.services.GeneralSvc;

public class Main {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {

		port(getHerokuAssignedPort());

		CorsFilter.apply();

		get("/hello", (req, res) -> "Hello World");

		// .. Authorization
		if (args.length == 0)
			before(Routes.USERS + "*", AuthorizeSvc::authorizeUser);

		// ...

		post(Routes.FICHES, FicheController::createFicheDocx, GeneralSvc.json());

		get(Routes.FICHES, FicheController::getFicheDocx, GeneralSvc.json());
		
		post(Routes.FICHES + "/excel", FicheController::createFichesExcel, GeneralSvc.json());

		get(Routes.FICHES + "/excel", FicheController::getFichesExcel, GeneralSvc.json());

		get(Routes.FICHESRAW, FicheController::getFicheRaw);

		if (args.length != 0) {
			DocGenerator docgen = new DocGenerator();
			try {
				docgen.generate();
			} catch (IOException e) {
				slf4jLogger.info("Error generating default doc: " + e.getMessage());
			}
		}

		options("/*", (request, response) -> {

			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}
			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}
			return "OK";
		});

	}

	static int getHerokuAssignedPort() {
		ProcessBuilder processBuilder = new ProcessBuilder();
		if (processBuilder.environment().get("PORT") != null) {
			return Integer.parseInt(processBuilder.environment().get("PORT"));
		}
		return Constants.DEFAULT_SERVER_PORT;
	}

}
