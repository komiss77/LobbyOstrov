package ru.ostrov77.lobby.bots.spots;

import org.bukkit.Bukkit;
import org.bukkit.World;
import ru.komiss77.modules.world.BVec;

public class WalkSpot implements Spot {
	
	private final BVec loc;
	private final SpotType st;
	private final World w;
	
	public WalkSpot(final BVec loc) {
		this.loc = loc;
		this.st = SpotType.WALK;
		final World tw = loc.w();
		this.w = tw == null ? Bukkit.getWorlds().getFirst() : tw;
	}

	@Override
	public BVec getLoc() {
		return loc;
	}

	@Override
	public SpotType getType() {
		return st;
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
