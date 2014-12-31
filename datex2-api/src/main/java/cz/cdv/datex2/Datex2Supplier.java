package cz.cdv.datex2;

import java.util.List;

import cz.cdv.datex2.providers.Datex2Provider;

public interface Datex2Supplier {

	void push();

	void addProvider(Datex2Provider provider);

	void removeProvider(Datex2Provider provider);

	List<Datex2Provider> getProviders();

}
