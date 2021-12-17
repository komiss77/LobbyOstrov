package ru.ostrov77.lobby.area;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.komiss77.modules.world.Cuboid;
import ru.ostrov77.lobby.Main;


public class LCuboid extends Cuboid {

    public final int id;
    private final String name;
    public String displayName;
    public Location spawnPoint;
    public final Set<String>playerNames = new HashSet<>();
    public final CuboidInfo info;
    
    //сохранении нового в редакторе
    public LCuboid(final int id, final String name, final String displayName, final Location spawnPoint, final Location pos1, final Location pos2) {
        super (pos1, pos2);
        this.id = id;
        this.name = name;
        this.displayName = displayName.isEmpty() ? name : displayName;
        this.spawnPoint =   spawnPoint==null ? this.getCenter(Main.getLocation(Main.LocType.Spawn)): spawnPoint;
        info = CuboidInfo.find(name);
    }
    
    //загрузка
    public LCuboid(final int id, final String name, final String displayName, final Location spawnPoint, final String cuboidAsString) {
        super (cuboidAsString);
        this.id = id;
        this.name = name;
        this.displayName = displayName.isEmpty() ? name : displayName;
        this.spawnPoint =   spawnPoint==null ? this.getCenter(Main.getLocation(Main.LocType.Spawn)): spawnPoint;
        info = CuboidInfo.find(name);
    }
    
    
    public List<Player> getCuboidPlayers() {
        List <Player>list = new ArrayList<>();
        playerNames.stream().map( (palyerName) -> Bukkit.getPlayerExact(palyerName)).filter( (p) -> (p!=null) ).forEachOrdered( (p) -> {
            list.add(p);
        } );
        return list;
    }
    
    public boolean hasPlayer(final Player p) {
        return playerNames.contains(p.getName());
    }
    
    public String getName() {
        return name;
    }
    
}
