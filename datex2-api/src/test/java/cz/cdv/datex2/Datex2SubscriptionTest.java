package cz.cdv.datex2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import cz.cdv.datex2.internal.PushTarget;

public class Datex2SubscriptionTest {

	@Test
	public void testAlmostAll() throws MalformedURLException {
		Datex2Subscription.newBuilder().reference("fdsa").start((Date) null)
				.stop((Date) null).periodic(null).update(null)
				.target((List<PushTarget>) null).build();
	}

	@Test
	public void testOnOccurrence() throws MalformedURLException {
		Datex2Subscription.newBuilder().singleElement()
				.target(new URL("http://localhost:9090/datex2/push?wsdl"))
				.build();
	}

	@Test
	public void testDelete() {
		Datex2Subscription.newBuilder().reference("asdf").delete().build();
	}

}
