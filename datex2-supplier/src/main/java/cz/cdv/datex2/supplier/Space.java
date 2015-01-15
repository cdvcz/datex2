package cz.cdv.datex2.supplier;

public class Space {

	private String siteId;
	private final int id;
	private boolean occupied;

	public static Space random(int id) {
		return new Space(null, id, Randomizer.getInstance().nextBoolean());
	}

	public Space(String siteId, int id, boolean occupied) {
		this.siteId = siteId;
		this.id = id;
		this.occupied = occupied;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public int getId() {
		return id;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void change() {
		occupied = !occupied;
	}

	@Override
	public String toString() {
		return "Space [siteId=" + siteId + ", id=" + id + ", occupied="
				+ occupied + "]";
	}

}
