package cz.cdv.datex2.internal;

import java.util.List;

import cz.cdv.datex2.Datex2Supplier;
import cz.cdv.datex2.providers.Datex2Provider;
import eu.datex2.schema._2._2_0.D2LogicalModel;

public class Datex2SupplierImpl implements Datex2Supplier {

	private Register<Datex2Provider> providers = new Register<>();

	@Override
	public void push() {
		// FIXME: do get() and push to subscriptions
	}

	public D2LogicalModel get() {
		for (Datex2Provider provider : getProviders()) {
			D2LogicalModel value = provider.get();
			if (value != null)
				return value;
		}

		return null;
	}

	@Override
	public void addProvider(Datex2Provider provider) {
		providers.add(provider);
	}

	@Override
	public void removeProvider(Datex2Provider provider) {
		providers.remove(provider);
	}

	@Override
	public List<Datex2Provider> getProviders() {
		return providers.getAll();
	}

}
