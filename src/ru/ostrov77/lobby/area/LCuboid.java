package ru.ostrov77.lobby.area;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import ru.komiss77.Ostrov;
import ru.komiss77.modules.world.Cuboid;
import ru.komiss77.modules.world.XYZ;
import ru.ostrov77.lobby.Main;


public class LCuboid extends Cuboid {
	
    public final Set<String> playerNames = new HashSet<>();
    private final CuboidInfo info;
    
    //сохранении нового в редакторе
    public LCuboid(final int id, final String name, final String displayName, final Location spawnPoint, final XYZ min, final XYZ max) {
        super(min, max);
        this.id = id;
        this.name = name;
        this.displayName = displayName.isEmpty() ? name : displayName;
        setSpawn(spawnPoint==null ? this.getCenter(Main.getLocation(Main.LocType.Spawn)) : spawnPoint, false);
        info = CuboidInfo.find(name);
        if (info==CuboidInfo.DEFAULT) {
            Ostrov.log_warn("Не найден CuboidInfo для "+name);
        }
    }
    
    //загрузка
    public LCuboid(final int id, final String name, final String displayName, final Location spawnPoint, final String cuboidAsString) {
        super(cuboidAsString);
        this.id = id;
        this.name = name;
        this.displayName = displayName.isEmpty() ? name : displayName;
        setSpawn(spawnPoint==null ? this.getCenter(Main.getLocation(Main.LocType.Spawn)) : spawnPoint, false);
        info = CuboidInfo.find(name);
        if (info==CuboidInfo.DEFAULT) {
            Ostrov.log_warn("Не найден CuboidInfo для "+name);
        }
    }
    
    
    public List<Player> getCuboidPlayers() {
        return playerNames.stream().map(palyerName -> Bukkit.getPlayerExact(palyerName)).filter(p -> p!=null).collect(Collectors.toList());
    }
    
    public boolean hasPlayer(final Player p) {
        return playerNames.contains(p.getName());
    }
    
    public String getName() {
        return name;
    }

    public CuboidInfo getInfo() {
        return info;
    }
    
}
