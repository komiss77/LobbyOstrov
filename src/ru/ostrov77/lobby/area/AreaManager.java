package ru.ostrov77.lobby.area;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;


public class AreaManager {
    
    
    private static BukkitTask playerMoveTask;
    private static Map<Integer,ChunkContent>chunkContetnt;
    private static Map<Integer,LCuboid>cuboids;
    
    
    
    public AreaManager () {
        
        chunkContetnt = new HashMap<>();
        cuboids = new HashMap<>();
        
        if (playerMoveTask!=null) {
            playerMoveTask.cancel();
        }
        
        playerMoveTask = new BukkitRunnable() {     //   !!!!ASYNC !!!!    каждую секунду
            
            LobbyPlayer lp;
            int currentCuboidId;
            LCuboid previos;
            LCuboid current;
            
            @Override
            public void run() {
                
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    currentCuboidId = getCuboidId(p.getLocation());
                    if (lp.lastCuboidId!=currentCuboidId) { //зашел в новый кубоид
                        if (currentCuboidId==0) { //вышел из кубоида в пространство
                            previos = cuboids.get(lp.lastCuboidId);
p.sendMessage("вышел из кубоида "+previos.name);
                            
                        } else if(lp.lastCuboidId==0) { //из пространства в кубоид
                            current = cuboids.get(currentCuboidId);
p.sendMessage("вошел в кубоид "+current.name);
                            
                        } else { //из кубоида в кубоил
                            previos = cuboids.get(lp.lastCuboidId);
                            current = cuboids.get(currentCuboidId);
p.sendMessage("перешел из кубоида "+previos.name+" в "+current.name);
                            
                        }
                        lp.lastCuboidId = currentCuboidId;
                    }
                    lp = Main.getLobbyPlayer(p);
                }
                
            }

             
        }.runTaskTimerAsynchronously(Main.instance, 20, 20);
        
        
    }
    
    
        public static int getCuboidId(final Location loc) {
            int cLoc = getcLoc(loc);
            final ChunkContent cc = chunkContetnt.get(cLoc);
            if (cc==null || cc.cuboids.isEmpty()) return 0;
            for (int cuboidId : cc.cuboids) {
                if (cuboids.containsKey(cuboidId) && cuboids.get(cuboidId).contains(loc)) {
                    return cuboidId;
                }
            }
            return 0;
        }
        
        public static LCuboid getCuboid(final Location loc) {
            int cLoc = getcLoc(loc);
            final ChunkContent cc = chunkContetnt.get(cLoc);
            if (cc==null || cc.cuboids.isEmpty()) return null;
            for (int cuboidId : cc.cuboids) {
                if (cuboids.containsKey(cuboidId) && cuboids.get(cuboidId).contains(loc)) {
                    return cuboids.get(cuboidId);
                }
            }
            return null;
        }
    
    
    
    
    
    
    public static int getcLoc(final Location loc) {
        return getcLoc(loc.getWorld().getName(), loc.getChunk().getX(), loc.getChunk().getZ());
    }
    
    public static int getcLoc(final String worldName, final int cX, final int cZ) {
        return worldName.length()<<26 | (cX+4096)<<13 | (cZ+4096);
    }    
    
    public static Chunk getChunk(final int cLoc) {
        return Bukkit.getWorld("world").getChunkAt(getChunkX(cLoc), getChunkZ(cLoc));
    }
    
    public static int getChunkX(int cLoc) { //len<<26 | (x+4096)<<13 | (z+4096);
        return ((cLoc>>13 & 0x1FFF)-4096); //8191 = 1FFF = 0b00000000_00000000_00011111_11111111
    }
    
    public static int getChunkZ(int cLoc) { //len<<26 | (x+4096)<<13 | (z+4096);
        return ((cLoc & 0x1FFF)-4096); //8191 = 1FFF = 0b00000000_00000000_00011111_11111111
    }    
    
    
    
    
    
    
    
    
    
}