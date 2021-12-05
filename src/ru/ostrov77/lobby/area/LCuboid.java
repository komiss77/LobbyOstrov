package ru.ostrov77.lobby.area;

import org.bukkit.Location;
import ru.komiss77.modules.world.Cuboid;


public class LCuboid extends Cuboid {

    public String name;
    
    public LCuboid(Location loc, int sizeX, int sizeY, int sizeZ) {
        super(loc, sizeX, sizeY, sizeZ);
    }
    
    public LCuboid(final Location location, final Location location2) {
        super (location, location2);
    }
}
