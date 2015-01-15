package cz.cdv.datex2.internal;

import java.util.List;

import cz.cdv.datex2.Datex2Client;
import cz.cdv.datex2.Datex2Subscription;
import cz.cdv.datex2.handlers.Datex2Handler;
import cz.cdv.datex2.wsdl.clientsubscribe.ClientSubscribeInterface;
import eu.datex2.schema._2._2_0.D2LogicalModel;
import eu.datex2.wsdl.clientpull._2_0.ClientPullInterface;

public class Datex2ClientImpl implements Datex2Client {

	private ClientPullInterface pullEndPoint;
	private ClientSubscribeInterface subscriptionEndPoint;

	private Register<Datex2Handler> handlers = new Register<>();

	public Datex2ClientImpl(ClientPullInterface pullEndPoint,
			ClientSubscribeInterface subscriptionEndPoint) {

		this.pullEndPoint = pullEndPoint;
		this.subscriptionEndPoint = subscriptionEndPoint;
	}

	@Override
	public void pull() {
		handle(get());
	}

	@Override
	public String subscribe(Datex2Subscription subscription) {
		if (subscriptionEndPoint == null)
			throw new UnsupportedOperationException(
					"Client is not initialized with subscription endpoint URL");
		if (subscription == null)
			throw new IllegalArgumentException("Subscription must be specified");

		return subscriptionEndPoint.subscribe(subscription.getModel());
	}

	@Override
	public D2LogicalModel get() {
		return pullEndPoint.getDatex2Data();
	}

	public void push(D2LogicalModel model) {
		handle(model);
	}

	private void handle(D2LogicalModel model) {
		for (Datex2Handler handler : getHandlers()) {
			handler.handle(model);
		}
	}

	@Override
	public void addHandler(Datex2Handler handler) {
		handlers.add(handler);
	}

	@Override
	public void removeHandler(Datex2Handler handler) {
		handlers.remove(handler);
	}

	@Override
	public List<Datex2Handler> getHandlers() {
		return handlers.getAll();
	}

}
