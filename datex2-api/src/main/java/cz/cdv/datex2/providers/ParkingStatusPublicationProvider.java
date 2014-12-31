package cz.cdv.datex2.providers;

import java.util.GregorianCalendar;
import java.util.List;

import eu.datex2.schema._2._2_0.CountryEnum;
import eu.datex2.schema._2._2_0.GenericPublication;
import eu.datex2.schema._2._2_0.GenericPublicationExtensionType;
import eu.datex2.schema._2._2_0.InternationalIdentifier;
import eu.datex2.schema._2._2_0.ParkingRecordStatus;
import eu.datex2.schema._2._2_0.ParkingStatusPublication;
import eu.datex2.schema._2._2_0.ParkingTablePublication;
import eu.datex2.schema._2._2_0.ParkingTableVersionedReference;
import eu.datex2.schema._2._2_0.PayloadPublication;

public abstract class ParkingStatusPublicationProvider extends
		PayloadPublicationProvider {

	private String lang;

	protected ParkingStatusPublicationProvider(CountryEnum country,
			String nationalIdentifier, String lang) {

		super(country, nationalIdentifier);
		this.lang = lang;
	}

	public static final String GENERIC_PUBLICATION_NAME = ParkingTablePublication.class
			.getSimpleName();

	@Override
	protected PayloadPublication getPayloadPublication() {
		GenericPublication gp = new GenericPublication();
		gp.setLang(lang);
		gp.setGenericPublicationName(GENERIC_PUBLICATION_NAME);
		gp.setPublicationTime(new GregorianCalendar());

		InternationalIdentifier intId = new InternationalIdentifier();
		intId.setCountry(getCountry());
		intId.setNationalIdentifier(getNationalIdentifier());
		gp.setPublicationCreator(intId);

		GenericPublicationExtensionType extension = new GenericPublicationExtensionType();
		ParkingStatusPublication parkingStatusPublication = new ParkingStatusPublication();
		extension.setParkingStatusPublication(parkingStatusPublication);
		gp.setGenericPublicationExtension(extension);

		List<ParkingTableVersionedReference> tableRefList = parkingStatusPublication
				.getParkingTableReference();
		List<ParkingRecordStatus> statusList = parkingStatusPublication
				.getParkingRecordStatus();

		fillParkingStatus(tableRefList, statusList);

		return gp;
	}

	protected abstract void fillParkingStatus(
			List<ParkingTableVersionedReference> tableRefList,
			List<ParkingRecordStatus> statusList);

}
