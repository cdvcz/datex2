package cz.cdv.datex2.providers;

import eu.datex2.schema._2._2_0.CountryEnum;
import eu.datex2.schema._2._2_0.D2LogicalModel;
import eu.datex2.schema._2._2_0.Exchange;
import eu.datex2.schema._2._2_0.InternationalIdentifier;
import eu.datex2.schema._2._2_0.PayloadPublication;

public abstract class PayloadPublicationProvider implements Datex2Provider {

	private final CountryEnum country;
	private final String nationalIdentifier;

	protected PayloadPublicationProvider(CountryEnum country,
			String nationalIdentifier) {

		this.country = country;
		this.nationalIdentifier = nationalIdentifier;
	}

	@Override
	public D2LogicalModel get() {
		D2LogicalModel model = new D2LogicalModel();
		model.setModelBaseVersion("2");

		Exchange exchange = new Exchange();
		InternationalIdentifier intId = new InternationalIdentifier();
		intId.setCountry(country);
		intId.setNationalIdentifier(nationalIdentifier);
		exchange.setSupplierIdentification(intId);
		model.setExchange(exchange);

		model.setPayloadPublication(getPayloadPublication());

		return model;
	}

	protected abstract PayloadPublication getPayloadPublication();

	protected CountryEnum getCountry() {
		return country;
	}

	protected String getNationalIdentifier() {
		return nationalIdentifier;
	}

}
