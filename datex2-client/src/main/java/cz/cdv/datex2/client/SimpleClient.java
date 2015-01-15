package cz.cdv.datex2.client;

import java.net.URL;

import cz.cdv.datex2.Datex2;
import cz.cdv.datex2.Datex2Client;

public class SimpleClient {

	public static void main(String[] args) throws Exception {
		Datex2 datex2 = new Datex2();

		Datex2Client client = datex2.createClient(new URL(
				"http://localhost:8080/datex2/cdv?wsdl"));

		client.addHandler(new LoggingParkingSpacesStatusHandler());

		client.pull();
	}

}
