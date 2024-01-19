package ru.ostrov77.lobby.bots.spots;

import org.bukkit.World;

import ru.komiss77.modules.world.XYZ;

public interface Spot {

	public XYZ getLoc();
	
	public SpotType getType();
	
	public World getWorld();
	
	@Override
	public boolean equals(final Object o);
	
	@Override
	public int hashCode();
	
	@Override
	public String toString();
	
}
