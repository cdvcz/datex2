package cz.cdv.datex2.internal;

import javax.jws.WebService;
import javax.xml.ws.Holder;

import eu.datex2.schema._2._2_0.D2LogicalModel;
import eu.datex2.wsdl.supplierpush._2_0.SupplierPushInterface;

@WebService(endpointInterface = "eu.datex2.wsdl.supplierpush._2_0.SupplierPushInterface", targetNamespace = "http://datex2.eu/wsdl/supplierPush/2_0", name = "supplierPushInterface", serviceName = "supplierPushService", portName = "supplierPushSoapEndPoint")
public class SupplierPushImpl implements SupplierPushInterface {

	private Datex2ClientImpl client;

	public SupplierPushImpl(Datex2ClientImpl client) {
		this.client = client;
	}

	@Override
	public void putDatex2Data(Holder<D2LogicalModel> holder) {
		if (holder == null)
			return;

		client.set(holder.value);
	}

}
