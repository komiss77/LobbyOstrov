package ru.ostrov77.lobby.area;

import org.bukkit.Location;
import ru.komiss77.modules.world.Cuboid;


public class LCuboid extends Cuboid {

    public final int id;
    public final String name;
    public String displayName;
    
   // public LCuboid(Location loc, int sizeX, int sizeY, int sizeZ) {
     //   super(loc, sizeX, sizeY, sizeZ);
   // }
    
    public LCuboid(final int id, final String name, final String displayName, final Location location, final Location location2) {
        super (location, location2);
        this.id = id;
        this.name = name;
        this.displayName = displayName;
    }
    
    public LCuboid(final int id, final String name, final String displayName, final String cuboidAsString) {
        super (cuboidAsString);
        this.id = id;
        this.name = name;
        this.displayName = displayName;
    }
}
