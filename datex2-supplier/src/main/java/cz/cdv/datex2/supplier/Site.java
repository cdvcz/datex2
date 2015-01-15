package cz.cdv.datex2.supplier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Site {

	private final String id;
	private final String recordId;
	private final String recordVersion;
	private Map<Integer, Space> spaces = new HashMap<>();

	public static Site random(String id) {
		return new Site(id, UUID.randomUUID().toString(),
				Integer.toString(Randomizer.getInstance().nextInt(100)));
	}

	public Site(String id, String recordId, String recordVersion) {
		this.id = id;
		this.recordId = recordId;
		this.recordVersion = recordVersion;
	}

	public String getId() {
		return id;
	}

	public String getRecordId() {
		return recordId;
	}

	public String getRecordVersion() {
		return recordVersion;
	}

	public Space getSpace(int spaceId) {
		return spaces.get(spaceId);
	}

	public void setSpace(Space space) {
		space.setSiteId(id);
		spaces.put(space.getId(), space);
	}

	public int[] getSpacesIds() {
		Set<Integer> set = spaces.keySet();
		int[] ar = new int[set.size()];
		int i = 0;
		for (Integer value : set) {
			ar[i++] = value;
		}
		return ar;
	}

}
