package cz.cdv.datex2;

import java.net.URL;

import javax.xml.ws.Endpoint;

import cz.cdv.datex2.internal.ClientPullImpl;
import cz.cdv.datex2.internal.ClientSubscriptionImpl;
import cz.cdv.datex2.internal.Datex2ClientImpl;
import cz.cdv.datex2.internal.Datex2SupplierImpl;
import cz.cdv.datex2.internal.SupplierPushImpl;
import cz.cdv.datex2.wsdl.clientsubscribe.ClientSubscribeInterface;
import cz.cdv.datex2.wsdl.clientsubscribe.ClientSubscribeService;
import eu.datex2.wsdl.clientpull._2_0.ClientPullInterface;
import eu.datex2.wsdl.clientpull._2_0.ClientPullService;

public class Datex2 {

	public static Datex2Client createClient(URL supplierWsdlLocation,
			URL supplierSubscriptionWsdlLocation, String clientLocation) {

		ClientPullService pullService = new ClientPullService(
				supplierWsdlLocation);
		ClientPullInterface pullEndPoint = pullService
				.getClientPullSoapEndPoint();

		ClientSubscribeInterface subscriptionEndPoint = null;
		if (supplierSubscriptionWsdlLocation != null) {
			ClientSubscribeService subscriptionService = new ClientSubscribeService(
					supplierSubscriptionWsdlLocation);
			subscriptionEndPoint = subscriptionService
					.getClientSubscribeSoapEndPoint();
		}

		Datex2ClientImpl client = new Datex2ClientImpl(pullEndPoint,
				subscriptionEndPoint);

		if (clientLocation != null) {
			Endpoint.publish(clientLocation, new SupplierPushImpl(client));
		}

		return client;
	}

	public static Datex2Supplier createSupplier(String supplierLocation,
			String subscriptionLocation) {

		if (subscriptionLocation != null) {
			Endpoint.publish(subscriptionLocation, new ClientSubscriptionImpl());
		}

		Datex2SupplierImpl supplier = new Datex2SupplierImpl();

		Endpoint.publish(supplierLocation, new ClientPullImpl(supplier));

		return supplier;
	}

}
