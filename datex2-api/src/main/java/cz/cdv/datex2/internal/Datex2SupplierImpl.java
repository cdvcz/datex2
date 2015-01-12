package cz.cdv.datex2.internal;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cdv.datex2.Datex2Supplier;
import cz.cdv.datex2.providers.ChangesProvider;
import cz.cdv.datex2.providers.Datex2Provider;
import cz.cdv.datex2.providers.SnapshotProvider;
import eu.datex2.schema._2._2_0.D2LogicalModel;
import eu.datex2.schema._2._2_0.UpdateMethodEnum;

public class Datex2SupplierImpl implements Datex2Supplier, InitializingBean {

	@Autowired
	private Subscriptions subscriptions;

	private String supplierPath;

	private Register<Datex2Provider> providers = new Register<>();

	private ProviderAdaptor<SnapshotProvider> snapshotProviderAdaptor = new ProviderAdaptor<SnapshotProvider>() {
		@Override
		public D2LogicalModel get(SnapshotProvider provider) {
			if (provider == null)
				return null;
			return provider.getSnapshot();
		}
	};

	public Datex2SupplierImpl(String supplierPath, String subscriptionPath) {
		this.supplierPath = supplierPath;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		subscriptions.registerSupplier(supplierPath, this);
	}

	@Override
	public void push(String... changes) {
		subscriptions.push(supplierPath, changes);
	}

	@Override
	public D2LogicalModel getSnapshot() {
		return getD2LogicalModel(SnapshotProvider.class,
				snapshotProviderAdaptor);
	}

	@Override
	public D2LogicalModel getChanges(final UpdateMethodEnum updateMethod,
			final String... changes) {

		return getD2LogicalModel(ChangesProvider.class,
				new ProviderAdaptor<ChangesProvider>() {
					@Override
					public D2LogicalModel get(ChangesProvider provider) {
						if (provider == null)
							return null;
						return provider.getChanges(updateMethod, changes);
					}
				});
	}

	private <P extends Datex2Provider> D2LogicalModel getD2LogicalModel(
			Class<P> providerClazz, ProviderAdaptor<P> adaptor) {

		for (P provider : getProviders(providerClazz)) {
			D2LogicalModel value = adaptor.get(provider);
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

	@Override
	public <S extends Datex2Provider> List<S> getProviders(Class<S> clazz) {
		return providers.getAll(clazz);
	}

	private static interface ProviderAdaptor<P extends Datex2Provider> {

		D2LogicalModel get(P provider);

	}

}
