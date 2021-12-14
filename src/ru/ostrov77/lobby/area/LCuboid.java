package ru.ostrov77.lobby.area;

import org.bukkit.Location;
import ru.komiss77.modules.world.Cuboid;
import ru.ostrov77.lobby.Main;


public class LCuboid extends Cuboid {

    public final int id;
    public final String name;
    public String displayName;
    public Location spawnPoint;
    
    //сохранении нового в редакторе
    public LCuboid(final int id, final String name, final String displayName, final Location spawnPoint, final Location pos1, final Location pos2) {
        super (pos1, pos2);
        this.id = id;
        this.name = name;
        this.displayName = displayName.isEmpty() ? name : displayName;
        this.spawnPoint =   spawnPoint==null ? this.getCenter(Main.spawnLocation): spawnPoint;
    }
    
    //загрузка
    public LCuboid(final int id, final String name, final String displayName, final Location spawnPoint, final String cuboidAsString) {
        super (cuboidAsString);
        this.id = id;
        this.name = name;
        this.displayName = displayName.isEmpty() ? name : displayName;
        this.spawnPoint =   spawnPoint==null ? this.getCenter(Main.spawnLocation): spawnPoint;
    }
}
