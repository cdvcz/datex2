package cz.cdv.datex2.supplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import cz.cdv.datex2.Datex2Supplier;
import cz.cdv.datex2.providers.ParkingSpacesStatusPublicationProvider;
import eu.datex2.schema._2._2_0.CountryEnum;

public class RandomSpacesProvider extends
		ParkingSpacesStatusPublicationProvider {

	private static final Logger log = Logger.getLogger(Supplier.class
			.getSimpleName());

	private Map<String, Site> data = new HashMap<>();
	private Map<String, List<Space>> changesMap = new HashMap<>();
	private ThreadLocal<Map<String, Site>> changesData = new ThreadLocal<>();

	private String tableId;
	private String tableVersion;

	private Datex2Supplier supplier;
	private TaskScheduler scheduler;

	private Trigger randomTrigger = Randomizer.getInstance().createTrigger(5,
			20);
	private Runnable randomCars = new Runnable() {
		@Override
		public void run() {
			String[] sitesIds = getParkingSitesIds();
			int siteIndex = Randomizer.getInstance().nextInt(sitesIds.length);
			String siteId = sitesIds[siteIndex];

			int[] spaces = getParkingSpaceIndices(siteId);
			int spaceIndex = Randomizer.getInstance().nextInt(spaces.length);
			int spaceId = spaces[spaceIndex];

			Space space = getParkingSpace(siteId, spaceId);
			space.change(); // change occupancy
			log.info("CHANGE: " + space);

			String changeId = UUID.randomUUID().toString();
			List<Space> changes = new ArrayList<>();
			changes.add(space);
			changesMap.put(changeId, changes);

			supplier.push(changeId);
		}
	};

	protected RandomSpacesProvider(Datex2Supplier supplier,
			TaskScheduler scheduler, CountryEnum country,
			String nationalIdentifier, String lang) {

		super(country, nationalIdentifier, lang);
		this.supplier = supplier;
		this.scheduler = scheduler;

		init();
	}

	private void init() {
		tableId = UUID.randomUUID().toString();
		tableVersion = "1";

		int parksSize = 1 + Randomizer.getInstance().nextInt(6);
		for (int i = 1; i <= parksSize; i++) {
			Site park = Site.random(Integer.toString(i));
			data.put(park.getId(), park);

			int size = 1 + Randomizer.getInstance().nextInt(49);
			for (int j = 1; j <= size; j++) {
				park.setSpace(Space.random(j));
			}
		}

		scheduler.schedule(randomCars, randomTrigger);
	}

	@Override
	protected String getParkingTableId() {
		return tableId;
	}

	@Override
	protected String getParkingTableVersion() {
		return tableVersion;
	}

	@Override
	protected String getParkingRecordId(String siteId) {
		return data.get(siteId).getRecordId();
	}

	@Override
	protected String getParkingRecordVersion(String siteId) {
		return data.get(siteId).getRecordVersion();
	}

	@Override
	protected String[] getParkingSitesIds() {
		return data.keySet().toArray(new String[0]);
	}

	@Override
	protected int[] getParkingSpaceIndices(String siteId) {
		return data.get(siteId).getSpacesIds();
	}

	@Override
	protected boolean isParkingSpaceOccupied(String siteId, int index) {
		return getParkingSpace(siteId, index).isOccupied();
	}

	private Space getParkingSpace(String siteId, int index) {
		return data.get(siteId).getSpace(index);
	}

	@Override
	protected String[] getChangedParkingSitesIds(String... changes) {
		if (changes == null || changes.length == 0)
			return new String[0];

		Map<String, Site> cache = changesData.get();
		return cache.keySet().toArray(new String[0]);
	}

	@Override
	protected int[] getChangedParkingSpaceIndices(String siteId,
			String... changes) {

		if (changes == null || changes.length == 0)
			return new int[0];

		Map<String, Site> cache = changesData.get();
		Site site = cache.get(siteId);
		return site.getSpacesIds();
	}

	@Override
	protected boolean isChangedParkingSpaceOccupied(String siteId, int index,
			String... changes) {

		Map<String, Site> cache = changesData.get();
		Site site = cache.get(siteId);
		return site.getSpace(index).isOccupied();
	}

	@Override
	protected void cache() {
		// cache data, e.g. obtain it from parking site controller if such
		// operation would be too resource consuming for individual values
	}

	@Override
	protected void clearCache() {
		// clear cache
	}

	@Override
	protected void cacheChanges(String... changes) {
		// cache data for specific status changes denoted by 'changes' (array of
		// changes' IDs)

		Map<String, Site> map = new HashMap<>();
		changesData.set(map);

		for (String change : changes) {
			List<Space> list = changesMap.get(change);
			if (list == null || list.size() == 0)
				continue;

			for (Space s : list) {
				String siteId = s.getSiteId();
				Site site = map.get(siteId);
				if (site == null) {
					Site origSite = data.get(siteId);
					site = new Site(siteId, origSite.getRecordId(),
							origSite.getRecordVersion());
					map.put(siteId, site);
				}

				site.setSpace(s);
			}
		}

	}

	@Override
	protected void clearCacheChanges(String... changes) {
		// clear changes cache

		changesData.set(null);
		changesData.remove();
	}

}
