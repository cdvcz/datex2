package cz.cdv.datex2.client;

import java.net.URL;
import java.util.logging.Logger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cdv.datex2.Datex2;
import cz.cdv.datex2.Datex2Client;

public class Client implements InitializingBean {

	private static final Logger log = Logger.getLogger(Client.class
			.getSimpleName());

	@Autowired
	private Datex2 datex2;

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("Initializing JAX-WS client ...");
		Datex2Client client = datex2.createClient(new URL(
				"http://localhost:8080/datex2/pull"), null, null);
		log.info("Client initialized.");

		client.pull();
	}

}
