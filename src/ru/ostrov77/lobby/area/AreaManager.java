package ru.ostrov77.lobby.area;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.komiss77.Ostrov;
import ru.komiss77.utils.OstrovConfig;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.quest.QuestManager;


public class AreaManager {
    
    private static OstrovConfig areaConfig;
    private static BukkitTask playerMoveTask;
    private static Map<Integer,ChunkContent>chunkContetnt;
    private static Map<Integer,LCuboid>cuboids;

    
    
    
    public static void deleteCuboid(final int cuboidId) {
        if (cuboids.containsKey(cuboidId)) {
            cuboids.remove(cuboidId);
            //убрать ид кубоида из чанков
            ChunkContent cc;
            final List<Integer>ids = new ArrayList<>(chunkContetnt.keySet());
            for (int ccId:ids) {
                cc=chunkContetnt.get(ccId);
                if (cc.deleteCuboidID(cuboidId)) {
                    if (!cc.hasCuboids()) { //убрать чанк, если нет там никакой инфы
                        chunkContetnt.remove(ccId);
                    }
                }
            }
            areaConfig.set("areas."+cuboidId, null);
            areaConfig.saveConfig();
        }
    }
    
    //добавление только после всех проверок в команде!
    protected static void addCuboid(final LCuboid lc, final boolean save) {
        cuboids.put(lc.id, lc);
        Set<Integer>cLocs = new HashSet<>(); //собираем cLoки кубоида для добавления в чанки
        Iterator<Location> it = lc.borderIterator(Bukkit.getWorld("world"));
        while (it.hasNext()) {
            cLocs.add(getcLoc(it.next()));
        }
        for (int cLoc : cLocs) { //добавляем ид кубоида в чанки
            if (!chunkContetnt.containsKey(cLoc)) {
                chunkContetnt.put(cLoc, new ChunkContent());
            }
            chunkContetnt.get(cLoc).addCuboidID(lc.id);
        }
        if (save) {
            save(lc);
        }
    }
    
    protected static void save(final LCuboid lc) {
        areaConfig.set("areas."+lc.id+".name", lc.name);
        areaConfig.set("areas."+lc.id+".displayName", lc.displayName);
        areaConfig.set("areas."+lc.id+".cuboidAsString", lc.toString());
        areaConfig.saveConfig();
    }
    
    public AreaManager () {
        
        chunkContetnt = new HashMap<>();
        cuboids = new HashMap<>();
        areaConfig = Main.configManager.getNewConfig("area.yml");
        if (areaConfig.getConfigurationSection("areas")!=null) {
            for (String areaID : areaConfig.getConfigurationSection("areas").getKeys(false) ) {
                try {
                    final int id = Integer.parseInt(areaID);
                    //final Location pos1 = LocationUtil.LocFromString(areaConfig.getString("areas."+areaID+".pos1"));
                    //final Location pos2 = LocationUtil.LocFromString(areaConfig.getString("areas."+areaID+".pos2"));
                    final String name = areaConfig.getString("areas."+areaID+".name");
                    final String displayName = areaConfig.getString("areas."+areaID+".displayName");
                    final String cuboidAsString = areaConfig.getString("areas."+areaID+".cuboidAsString");
                    final LCuboid lc = new LCuboid(id, name, displayName, cuboidAsString);
                    addCuboid(lc, false);
                } catch (Exception ex) {
                    Ostrov.log_err("AreaManager ошибка загрузки локации "+areaID+" : "+ex.getMessage());
                }
                
            }
        }
       
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
                    lp = Main.getLobbyPlayer(p);
                    currentCuboidId = getCuboidId(p.getLocation());
                    lp = Main.getLobbyPlayer(p);
                    if (lp.lastCuboidId!=currentCuboidId) { //зашел в новый кубоид
                        
                        if (currentCuboidId==0) { //вышел из кубоида в пространство
                            
                            previos = cuboids.get(lp.lastCuboidId);
                            if (previos!=null) { //вдруг удалился?

                                QuestManager.onCuboidExit(p, lp, previos);
                            }
                            
                        } else if(lp.lastCuboidId==0) { //из пространства в кубоид
                            
                            current = cuboids.get(currentCuboidId);
                            if (current!=null) { //вдруг удалился?
                                QuestManager.onCuboidEntry(p, lp, current);
                            }
                            
                        } else { //из кубоида в кубоил
                            
                            previos = cuboids.get(lp.lastCuboidId);
                            current = cuboids.get(currentCuboidId);
                            if (previos!=null && current!=null) { //вдруг удалился? тогда вызваем по отдельности
                                QuestManager.onCuboidChange(p, lp, previos, current);
                            } else if (current!=null) { //вдруг удалился?
                                QuestManager.onCuboidEntry(p, lp, current);
                            } else if (previos!=null) {
                                QuestManager.onCuboidExit(p, lp, previos);
                            }
                        }
                        lp.lastCuboidId = currentCuboidId;
                    }
                }
                
            }

             
        }.runTaskTimerAsynchronously(Main.instance, 20, 20);
        
        
    }
    
    
    public static int getCuboidId(final Location loc) {
        int cLoc = getcLoc(loc);
        final ChunkContent cc = chunkContetnt.get(cLoc);
        if (cc==null || !cc.hasCuboids()) return 0;
        for (int cuboidId : cc.getCuboidsIds()) {
            if (cuboids.containsKey(cuboidId) && cuboids.get(cuboidId).contains(loc)) {
                return cuboidId;
            }
        }
        return 0;
    }

    public static LCuboid getCuboid(final Location loc) {
        int cLoc = getcLoc(loc);
        final ChunkContent cc = chunkContetnt.get(cLoc);
        if (cc==null || !cc.hasCuboids()) return null;
        for (int cuboidId : cc.getCuboidsIds()) {
            if (cuboids.containsKey(cuboidId) && cuboids.get(cuboidId).contains(loc)) {
                return cuboids.get(cuboidId);
            }
        }
        return null;
    }
    public static LCuboid getCuboid(final String cuboidName) {
        for (LCuboid lc : cuboids.values()) {
            if (lc.name.equalsIgnoreCase(cuboidName)) {
                return lc;
            }
        }
        return null;
    }

    public static Set<Integer> getCuboidIds() {
        return cuboids.keySet();
    }
    
    public static LCuboid getCuboid(final int cuboidId) {
        return cuboids.get(cuboidId);
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
