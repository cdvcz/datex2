package cz.cdv.datex2.client;

import java.net.URL;
import java.util.logging.Logger;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cdv.datex2.Datex2;
import cz.cdv.datex2.Datex2Client;
import cz.cdv.datex2.Datex2Subscription;

public class Client implements InitializingBean, DisposableBean {

	private static final Logger log = Logger.getLogger(Client.class
			.getSimpleName());

	@Autowired
	private Datex2 datex2;

	private Datex2Client client = null;
	private String subscriptionRef = null;

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("Initializing DATEX II client ...");
		client = datex2.createClient(new URL(
				"http://localhost:8080/datex2/cdv?wsdl"), new URL(
				"http://localhost:8080/datex2/subscribe/cdv?wsdl"), "/push");
		log.info("Client initialized.");

		client.addHandler(new LoggingParkingSpacesStatusHandler());

		// pull
		client.pull();

		// subscribe for on-occurrence notifications
		subscriptionRef = client.subscribe(Datex2Subscription.newBuilder()
				.singleElement()
				.target(new URL("http://localhost:9090/datex2/push?wsdl"))
				.build());
	}

	@Override
	public void destroy() throws Exception {
		if (client == null || subscriptionRef == null)
			return;

		// unsubscribe
		client.subscribe(Datex2Subscription.newBuilder()
				.reference(subscriptionRef).delete().build());
	}
}
