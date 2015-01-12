package cz.cdv.datex2.internal;

import javax.jws.WebService;

import eu.datex2.schema._2._2_0.D2LogicalModel;
import eu.datex2.wsdl.clientpull._2_0.ClientPullInterface;

@WebService(endpointInterface = "eu.datex2.wsdl.clientpull._2_0.ClientPullInterface", targetNamespace = "http://datex2.eu/wsdl/clientPull/2_0", name = "clientPullInterface", serviceName = "clientPullService", portName = "clientPullSoapEndPoint")
public class ClientPullImpl implements ClientPullInterface {

	private Datex2SupplierImpl supplier;

	public ClientPullImpl(Datex2SupplierImpl supplier) {
		this.supplier = supplier;
	}

	@Override
	public D2LogicalModel getDatex2Data() {
		return supplier.getSnapshot();
	}

}
