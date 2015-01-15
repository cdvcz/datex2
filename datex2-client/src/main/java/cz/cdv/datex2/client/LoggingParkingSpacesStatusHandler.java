package cz.cdv.datex2.client;

import java.util.logging.Logger;

import cz.cdv.datex2.handlers.ParkingSpacesStatusHandler;
import eu.datex2.schema._2._2_0.ParkingRecordVersionedReference;

public class LoggingParkingSpacesStatusHandler extends
		ParkingSpacesStatusHandler {

	private static final Logger log = Logger.getLogger("SPACE");

	@Override
	protected void handleParkingSpace(
			ParkingRecordVersionedReference recordRef, int spaceIndex,
			boolean occupied) {

		log.info("record reference: " + recordRef.getId() + ", id: "
				+ spaceIndex + ", occupied: " + occupied);
	}

}
