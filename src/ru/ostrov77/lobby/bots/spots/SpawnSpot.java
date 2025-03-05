package ru.ostrov77.lobby.bots.spots;

import org.bukkit.Bukkit;
import org.bukkit.World;

import ru.komiss77.modules.world.BVec;
import ru.komiss77.modules.world.XYZ;
import ru.ostrov77.lobby.area.AreaManager;

public class SpawnSpot implements Spot {
	
	private final BVec loc;
	private final SpotType st;
	private final World w;
	
	public SpawnSpot(final BVec loc) {
		this.loc = loc;
		this.st = SpotType.SPAWN;
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
	public World getWorld() {
		return w;
	}
}
