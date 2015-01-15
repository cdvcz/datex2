package cz.cdv.datex2.internal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;

import cz.cdv.datex2.wsdl.clientsubscribe.ClientSubscribeInterface;
import eu.datex2.schema._2._2_0.D2LogicalModel;
import eu.datex2.schema._2._2_0.Exchange;
import eu.datex2.schema._2._2_0.OperatingModeEnum;
import eu.datex2.schema._2._2_0.Subscription;
import eu.datex2.schema._2._2_0.Target;
import eu.datex2.schema._2._2_0.UpdateMethodEnum;

@WebService(endpointInterface = "cz.cdv.datex2.wsdl.clientsubscribe.ClientSubscribeInterface", targetNamespace = "http://cdv.cz/datex2/wsdl/clientSubscribe", name = "clientSubscribeInterface", serviceName = "clientSubscribeService", portName = "clientSubscribeSoapEndPoint")
public class ClientSubscriptionImpl implements ClientSubscribeInterface {

	@Autowired
	private Subscriptions subscriptions;

	private String supplierPath;

	public ClientSubscriptionImpl(String supplierPath, String subscriptionPath) {
		this.supplierPath = supplierPath;
	}

	@Override
	public String subscribe(D2LogicalModel body) {
		if (body == null || body.getExchange() == null
				|| body.getExchange().getSubscription() == null)
			return null;

		Exchange exchange = body.getExchange();

		String subscriptionReference = exchange.getSubscriptionReference();
		Subscription subscription = exchange.getSubscription();
		if (subscription == null)
			return null;

		OperatingModeEnum mode = subscription.getOperatingMode();

		if (Boolean.TRUE.equals(subscription.isDeleteSubscription())) {
			if (subscriptionReference != null) {
				subscriptions.delete(supplierPath, subscriptionReference);
			}
			return null;
		}

		UpdateMethodEnum updateMethod = subscription.getUpdateMethod();
		List<PushTarget> pushTargets = getPushTargets(subscription.getTarget());

		Float periodSeconds = subscription.getDeliveryInterval();
		Calendar startTime = subscription.getSubscriptionStartTime();
		Calendar stopTime = subscription.getSubscriptionStopTime();

		if (mode == OperatingModeEnum.OPERATING_MODE_2 && periodSeconds != null) {
			// push periodic
			if (periodSeconds <= 0)
				return null;

			if (subscriptionReference == null) {
				String reference = subscriptions.addPeriodic(supplierPath,
						startTime, stopTime, periodSeconds, updateMethod,
						pushTargets);
				return reference;
			} else {
				String reference = subscriptions.updatePeriodic(supplierPath,
						subscriptionReference, startTime, stopTime,
						periodSeconds, updateMethod, pushTargets);
				return reference;
			}
		}

		else if (mode == OperatingModeEnum.OPERATING_MODE_1) {
			// push on occurrence
			if (subscriptionReference == null) {
				String reference = subscriptions.add(supplierPath,
						updateMethod, startTime, stopTime, pushTargets);
				return reference;
			} else {
				String reference = subscriptions.update(supplierPath,
						subscriptionReference, updateMethod, startTime,
						stopTime, pushTargets);
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

			pushTargets.add(PushTarget.create(t));
		}

		return pushTargets;
	}

}
