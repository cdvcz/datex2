package cz.cdv.datex2.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebService;

import cz.cdv.datex2.wsdl.clientsubscribe.ClientSubscribeInterface;
import eu.datex2.schema._2._2_0.D2LogicalModel;
import eu.datex2.schema._2._2_0.Exchange;
import eu.datex2.schema._2._2_0.OperatingModeEnum;
import eu.datex2.schema._2._2_0.Subscription;
import eu.datex2.schema._2._2_0.Target;
import eu.datex2.schema._2._2_0.UpdateMethodEnum;

@WebService(endpointInterface = "cz.cdv.datex2.wsdl.clientsubscribe.ClientSubscribeInterface", name = "clientSubscribeInterface", serviceName = "clientSubscribeService", portName = "clientSubscribeSoapEndPoint")
public class ClientSubscriptionImpl implements ClientSubscribeInterface {

	private Subscriptions subscriptions = new Subscriptions();

	@Override
	public String subscribe(D2LogicalModel body) {
		if (body == null || body.getExchange() == null
				|| body.getExchange().getSubscription() == null)
			return null;

		Exchange exchange = body.getExchange();

		String subscriptionReference = exchange.getSubscriptionReference();
		Subscription subscription = exchange.getSubscription();

		OperatingModeEnum mode = subscription.getOperatingMode();

		if (subscription.isDeleteSubscription()) {
			if (subscriptionReference != null) {
				subscriptions.delete(subscriptionReference);
			}
			return null;
		}

		UpdateMethodEnum updateMethod = subscription.getUpdateMethod();
		List<PushTarget> pushTargets = getPushTargets(subscription.getTarget());

		if (mode == OperatingModeEnum.OPERATING_MODE_2) {
			// push periodic
			Float periodSeconds = subscription.getDeliveryInterval();
			Calendar startTime = subscription.getSubscriptionStartTime();
			Calendar stopTime = subscription.getSubscriptionStopTime();

			if (subscriptionReference == null) {
				String reference = subscriptions.addPeriodic(startTime,
						stopTime, periodSeconds, updateMethod, pushTargets);
				return reference;
			} else {
				String reference = subscriptions.updatePeriodic(
						subscriptionReference, startTime, stopTime,
						periodSeconds, updateMethod, pushTargets);
				return reference;
			}
		}

		else if (mode == OperatingModeEnum.OPERATING_MODE_1) {
			// push on occurrence
			if (subscriptionReference == null) {
				String reference = subscriptions.add(updateMethod, pushTargets);
				return reference;
			} else {
				String reference = subscriptions.update(subscriptionReference,
						updateMethod, pushTargets);
				return reference;
			}
		}

		else {
			return subscriptionReference;
		}
	}

	private List<PushTarget> getPushTargets(List<Target> targets) {
		List<PushTarget> pushTargets = new ArrayList<>();
		if (targets == null || targets.size() == 0)
			return pushTargets;

		for (Target t : targets) {
			if (t == null)
				continue;

			pushTargets.add(getPushTarget(t));
		}
		return pushTargets;
	}

	private PushTarget getPushTarget(Target t) {
		try {
			return new PushTarget(new URL(t.getAddress()), null, null);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
