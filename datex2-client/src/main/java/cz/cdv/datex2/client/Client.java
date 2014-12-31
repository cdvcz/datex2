package cz.cdv.datex2.client;

import java.net.URL;
import java.util.logging.Logger;

import cz.cdv.datex2.Datex2;
import cz.cdv.datex2.Datex2Client;

public class Client {

	private static final Logger log = Logger.getLogger(Client.class
			.getSimpleName());

	public static void main(String[] args) throws Exception {
		log.info("Initializing JAX-WS client ...");
		Datex2Client client = Datex2.createClient(new URL(
				"http://localhost:8080/datex2/pull"), null, null);
		log.info("Client initialized.");

		client.pull();
	}

}
