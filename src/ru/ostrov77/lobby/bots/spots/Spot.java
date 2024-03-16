package ru.ostrov77.lobby.bots.spots;

import org.bukkit.World;

import ru.komiss77.modules.world.XYZ;

public interface Spot {

	XYZ getLoc();
	
	SpotType getType();
	
	World getWorld();
	
	@Override
    boolean equals(final Object o);
	
	@Override
    int hashCode();
	
	@Override
    String toString();
	
}
