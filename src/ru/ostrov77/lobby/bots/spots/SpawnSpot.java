package ru.ostrov77.lobby.bots.spots;

import ru.komiss77.modules.world.XYZ;
import ru.ostrov77.lobby.area.AreaManager;

public class SpawnSpot implements Spot {
	
	private final XYZ loc;
	private final SpotType st;
	
	public SpawnSpot(final XYZ loc) {
		this.loc = loc;
		this.st = SpotType.SPAWN;
	}

	@Override
	public XYZ getLoc() {
		return loc;
	}

	@Override
	public SpotType getType() {
		return st;
	}
	
	@Override
	public boolean equals(final Object o) {
		return o instanceof Spot && loc.equals(((Spot)o).getLoc());
	}

	@Override
	public int hashCode() {
		return AreaManager.getcLoc(loc);
	}
	
	@Override
	public String toString() {
		return loc.toString() + ", t-" + st.toString();
	}
}
