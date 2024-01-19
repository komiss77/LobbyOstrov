package ru.ostrov77.lobby.bots.spots;

import org.bukkit.Bukkit;
import org.bukkit.World;

import ru.komiss77.modules.world.XYZ;
import ru.ostrov77.lobby.area.AreaManager;

public class WalkSpot implements Spot {
	
	private final XYZ loc;
	private final SpotType st;
	private final World w;
	
	public WalkSpot(final XYZ loc) {
		this.loc = loc;
		this.st = SpotType.WALK;
		this.w = Bukkit.getWorld(loc.worldName);
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

	@Override
	public World getWorld() {
		return w;
	}
}
