package cz.cdv.datex2.handlers;

import java.util.List;

import eu.datex2.schema._2._2_0.D2LogicalModel;
import eu.datex2.schema._2._2_0.GenericPublication;
import eu.datex2.schema._2._2_0.GenericPublicationExtensionType;
import eu.datex2.schema._2._2_0.ParkingRecordStatus;
import eu.datex2.schema._2._2_0.ParkingRecordStatusParkingSpaceIndexParkingSpaceStatus;
import eu.datex2.schema._2._2_0.ParkingRecordVersionedReference;
import eu.datex2.schema._2._2_0.ParkingSpaceStatus;
import eu.datex2.schema._2._2_0.ParkingStatusPublication;
import eu.datex2.schema._2._2_0.PayloadPublication;

public abstract class ParkingSpacesStatusHandler implements Datex2Handler {

	@Override
	public void handle(D2LogicalModel model) {
		if (model == null)
			return;

		PayloadPublication publication = model.getPayloadPublication();
		if (publication == null || !(publication instanceof GenericPublication))
			return;

		GenericPublication gp = (GenericPublication) publication;
		GenericPublicationExtensionType extension = gp
				.getGenericPublicationExtension();
		if (extension == null)
			return;

		ParkingStatusPublication parkingStatus = extension
				.getParkingStatusPublication();
		if (parkingStatus == null)
			return;

		List<ParkingRecordStatus> recordList = parkingStatus
				.getParkingRecordStatus();
		if (recordList == null)
			return;

		for (ParkingRecordStatus record : recordList) {
			if (record == null)
				continue;

			ParkingRecordVersionedReference recordRef = record
					.getParkingRecordReference();

			List<ParkingRecordStatusParkingSpaceIndexParkingSpaceStatus> spacesStatuses = record
					.getParkingSpaceStatus();
			if (spacesStatuses == null)
				continue;
			for (ParkingRecordStatusParkingSpaceIndexParkingSpaceStatus spaceStatus : spacesStatuses) {
				if (spaceStatus == null)
					continue;

				int spaceIndex = spaceStatus.getParkingSpaceIndex();
				ParkingSpaceStatus status = spaceStatus.getParkingSpaceStatus();
				if (status == null)
					continue;

				handleParkingSpace(recordRef, spaceIndex, status);
			}
		}
	}

	protected void handleParkingSpace(
			ParkingRecordVersionedReference recordRef, int spaceIndex,
			ParkingSpaceStatus status) {

		handleParkingSpace(recordRef, spaceIndex,
				status.isParkingSpaceOccupied());
	}

	protected abstract void handleParkingSpace(
			ParkingRecordVersionedReference recordRef, int spaceIndex,
			boolean occupied);

}
