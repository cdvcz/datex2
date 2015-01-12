package cz.cdv.datex2;

import java.util.List;

import cz.cdv.datex2.providers.Datex2Provider;
import eu.datex2.schema._2._2_0.D2LogicalModel;
import eu.datex2.schema._2._2_0.UpdateMethodEnum;

public interface Datex2Supplier {

	void push(String... changes);

	D2LogicalModel getSnapshot();

	D2LogicalModel getChanges(UpdateMethodEnum updateMethod, String... changes);

	void addProvider(Datex2Provider provider);

	void removeProvider(Datex2Provider provider);

	List<Datex2Provider> getProviders();

	<S extends Datex2Provider> List<S> getProviders(Class<S> clazz);

}
