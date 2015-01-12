package cz.cdv.datex2.internal;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.ws.Holder;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;

import eu.datex2.schema._2._2_0.D2LogicalModel;
import eu.datex2.wsdl.supplierpush._2_0.SupplierPushInterface;
import eu.datex2.wsdl.supplierpush._2_0.SupplierPushService;

public class Pusher {

	private Map<PushTarget, WeakReference<SupplierPushInterface>> stubs = new ConcurrentHashMap<>();

	public void push(PushTarget target, D2LogicalModel model) {
		SupplierPushInterface client = getSupplierPush(target);
		client.putDatex2Data(new Holder<D2LogicalModel>(model));
	}

	private SupplierPushInterface getSupplierPush(PushTarget target) {
		SupplierPushInterface push = null;

		WeakReference<SupplierPushInterface> ref = stubs.get(target);
		if (ref != null)
			push = ref.get();

		if (push == null) {
			push = createSupplierPush(target);
			stubs.put(target, new WeakReference<>(push));
		}

		return push;
	}

	private SupplierPushInterface createSupplierPush(PushTarget target) {
		SupplierPushService pushService = new SupplierPushService(
				target.getUrl());
		SupplierPushInterface push = pushService.getSupplierPushSoapEndPoint();

		if (target.getUsername() != null) {
			Client client = ClientProxy.getClient(push);
			HTTPConduit http = (HTTPConduit) client.getConduit();
			AuthorizationPolicy authorization = http.getAuthorization();
			authorization.setUserName(target.getUsername());
			authorization.setPassword(target.getPassword());
		}

		return push;
	}

}
