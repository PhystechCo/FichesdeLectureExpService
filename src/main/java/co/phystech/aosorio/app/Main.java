package co.phystech.aosorio.app;

import static spark.Spark.*;

import java.io.IOException;

import co.phystech.aosorio.config.CorsFilter;
import co.phystech.aosorio.config.Routes;
import co.phystech.aosorio.controllers.DocGenerator;
import co.phystech.aosorio.controllers.FicheController;
import co.phystech.aosorio.services.AuthorizeSvc;
import co.phystech.aosorio.services.GeneralSvc;

public class Main {

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

		get(Routes.FICHESRAW, FicheController::getFicheRaw);

		if (args.length != 0) {
			DocGenerator docgen = new DocGenerator();
			try {
				docgen.generate();
			} catch (IOException e) {
				e.printStackTrace();
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
		return 4567;
	}

}
