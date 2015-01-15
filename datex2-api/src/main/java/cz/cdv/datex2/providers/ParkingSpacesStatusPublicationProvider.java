package cz.cdv.datex2.providers;

import java.util.List;

import eu.datex2.schema._2._2_0.CountryEnum;
import eu.datex2.schema._2._2_0.ParkingRecordStatus;
import eu.datex2.schema._2._2_0.ParkingRecordStatusParkingSpaceIndexParkingSpaceStatus;
import eu.datex2.schema._2._2_0.ParkingRecordVersionedReference;
import eu.datex2.schema._2._2_0.ParkingSiteStatus;
import eu.datex2.schema._2._2_0.ParkingSpaceStatus;
import eu.datex2.schema._2._2_0.ParkingTable;
import eu.datex2.schema._2._2_0.ParkingTableVersionedReference;
import eu.datex2.schema._2._2_0.UpdateMethodEnum;

public abstract class ParkingSpacesStatusPublicationProvider extends
		ParkingStatusPublicationProvider {

	protected ParkingSpacesStatusPublicationProvider(CountryEnum country,
			String nationalIdentifier, String lang) {

		super(country, nationalIdentifier, lang);
	}

	@Override
	protected void fillParkingStatus(
			List<ParkingTableVersionedReference> tableRefList,
			List<ParkingRecordStatus> statusList) {

		cache();

		String tableId = getParkingTableId();
		String tableVersion = getParkingTableVersion();

		ParkingTableVersionedReference tableRef = new ParkingTableVersionedReference();
		tableRefList.add(tableRef);
		tableRef.setTargetClass(ParkingTable.class.getName());
		tableRef.setId(tableId);
		tableRef.setVersion(tableVersion);

		for (String siteId : getParkingSitesIds()) {
			ParkingSiteStatus siteStatus = new ParkingSiteStatus();
			fillParkingSiteStatus(siteStatus);
			statusList.add(siteStatus);

			ParkingRecordVersionedReference parkingRecordRef = new ParkingRecordVersionedReference();
			parkingRecordRef.setId(getParkingRecordId(siteId));
			parkingRecordRef.setVersion(getParkingRecordVersion(siteId));
			siteStatus.setParkingRecordReference(parkingRecordRef);

			List<ParkingRecordStatusParkingSpaceIndexParkingSpaceStatus> spacesStatus = siteStatus
					.getParkingSpaceStatus();
			for (int index : getParkingSpaceIndices(siteId)) {
				boolean occupied = isParkingSpaceOccupied(siteId, index);

				ParkingRecordStatusParkingSpaceIndexParkingSpaceStatus spaceStatus = getParkingSpaceStatus(
						siteId, index, occupied);
				spacesStatus.add(spaceStatus);
			}
		}

		clearCache();
	}

	@Override
	protected void fillParkingStatusChanges(
			List<ParkingTableVersionedReference> tableRefList,
			List<ParkingRecordStatus> statusList,
			UpdateMethodEnum updateMethod, String... changes) {

		if (changes == null || changes.length == 0)
			throw new IllegalArgumentException(
					"At least one change must be specified");
		if (updateMethod != UpdateMethodEnum.ALL_ELEMENT_UPDATE
				&& updateMethod != UpdateMethodEnum.SINGLE_ELEMENT_UPDATE)
			throw new IllegalArgumentException("Unsupported update method: "
					+ updateMethod);

		boolean all = updateMethod == UpdateMethodEnum.ALL_ELEMENT_UPDATE;
		if (all)
			cache();
		cacheChanges(changes);

		String tableId = getParkingTableId();
		String tableVersion = getParkingTableVersion();

		ParkingTableVersionedReference tableRef = new ParkingTableVersionedReference();
		tableRefList.add(tableRef);
		tableRef.setTargetClass(ParkingTable.class.getName());
		tableRef.setId(tableId);
		tableRef.setVersion(tableVersion);

		for (String siteId : getChangedParkingSitesIds(changes)) {
			ParkingSiteStatus siteStatus = new ParkingSiteStatus();
			if (all)
				fillParkingSiteStatus(siteStatus);
			statusList.add(siteStatus);

			ParkingRecordVersionedReference parkingRecordRef = new ParkingRecordVersionedReference();
			parkingRecordRef.setId(getParkingRecordId(siteId));
			parkingRecordRef.setVersion(getParkingRecordVersion(siteId));
			siteStatus.setParkingRecordReference(parkingRecordRef);

			List<ParkingRecordStatusParkingSpaceIndexParkingSpaceStatus> spacesStatus = siteStatus
					.getParkingSpaceStatus();

			int[] spaceIndices;
			if (all)
				spaceIndices = getParkingSpaceIndices(siteId);
			else
				spaceIndices = getChangedParkingSpaceIndices(siteId, changes);

			for (int index : spaceIndices) {
				boolean occupied;
				if (all)
					occupied = isParkingSpaceOccupied(siteId, index);
				else
					occupied = isChangedParkingSpaceOccupied(siteId, index,
							changes);

				ParkingRecordStatusParkingSpaceIndexParkingSpaceStatus spaceStatus = getParkingSpaceStatus(
						siteId, index, occupied);
				spacesStatus.add(spaceStatus);
			}
		}

		clearCacheChanges(changes);
		if (all)
			clearCache();
	}

	protected void cache() {
	}

	protected void clearCache() {
	}

	protected void cacheChanges(String... changes) {
	}

	protected void clearCacheChanges(String... changes) {
	}

	protected abstract String getParkingTableId();

	protected abstract String getParkingTableVersion();

	protected abstract String getParkingRecordId(String siteId);

	protected abstract String getParkingRecordVersion(String siteId);

	protected abstract String[] getParkingSitesIds();

	protected abstract int[] getParkingSpaceIndices(String siteId);

	protected abstract boolean isParkingSpaceOccupied(String siteId, int index);

	protected abstract String[] getChangedParkingSitesIds(String[] changes);

	protected abstract int[] getChangedParkingSpaceIndices(String siteId,
			String[] changes);

	protected abstract boolean isChangedParkingSpaceOccupied(String siteId,
			int index, String[] changes);

	protected void fillParkingSiteStatus(ParkingSiteStatus siteStatus) {
	}

	protected ParkingRecordStatusParkingSpaceIndexParkingSpaceStatus getParkingSpaceStatus(
			String siteId, int index, boolean occupied) {

		ParkingRecordStatusParkingSpaceIndexParkingSpaceStatus spaceStatus = new ParkingRecordStatusParkingSpaceIndexParkingSpaceStatus();
		spaceStatus.setParkingSpaceIndex(index);
		ParkingSpaceStatus status = new ParkingSpaceStatus();
		status.setParkingSpaceOccupied(occupied);
		spaceStatus.setParkingSpaceStatus(status);

		return spaceStatus;
	}

}
