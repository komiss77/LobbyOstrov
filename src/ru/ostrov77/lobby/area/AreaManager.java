package ru.ostrov77.lobby.area;

import java.util.ArrayList;
import java.util.Collection;
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
import ru.komiss77.Timer;
import ru.komiss77.utils.LocationUtil;
import ru.komiss77.utils.OstrovConfig;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.event.CuboidEvent;
import ru.ostrov77.lobby.listeners.QuestAdvance;


public class AreaManager {
    
    //public static final Map<String,Integer>racePlayers = new HashMap<>();
    
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
            for (int ccId : ids) {
                cc=chunkContetnt.get(ccId);
                if (cc.hasCuboids() && cc.deleteCuboidID(cuboidId)) {
                    if (cc.isEmpty()) { //убрать чанк, если нет там никакой инфы
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
            //if (!chunkContetnt.containsKey(cLoc)) {
            //    chunkContetnt.put(cLoc, new ChunkContent());
            //}
            getChunkContent(cLoc, true).addCuboidID(lc.id);
        }
        if (save) {
            saveCuboid(lc);
        }
    }
    
    protected static void saveCuboid(final LCuboid lc) {
        areaConfig.set("areas."+lc.id+".name", lc.name);
        areaConfig.set("areas."+lc.id+".displayName", lc.displayName);
        areaConfig.set("areas."+lc.id+".spawnPoint", LocationUtil.StringFromLocWithYawPitch(lc.spawnPoint));
        areaConfig.set("areas."+lc.id+".cuboidAsString", lc.toString());
        areaConfig.saveConfig();
    }

    public static void savePlate(final XYZ firstPlateXYZ, final XYZ secondPlateXYZ) {
        if (secondPlateXYZ==null) { //удаление
            areaConfig.set("plate."+firstPlateXYZ.toString(), null);
        } else {
            areaConfig.set("plate."+firstPlateXYZ.toString()+".second", secondPlateXYZ.toString());
        }
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
                    final String name = areaConfig.getString("areas."+areaID+".name");
                    final String displayName = areaConfig.getString("areas."+areaID+".displayName");
                    final String cuboidAsString = areaConfig.getString("areas."+areaID+".cuboidAsString");
                    final Location spawnPoint = LocationUtil.LocFromString(areaConfig.getString("areas."+areaID+".spawnPoint"));
                    final LCuboid lc = new LCuboid(id, name, displayName, spawnPoint, cuboidAsString);
                    addCuboid(lc, false);
                } catch (Exception ex) {
                    Ostrov.log_err("AreaManager ошибка загрузки локации "+areaID+" : "+ex.getMessage());
                }
                
            }
        }
        
        if (areaConfig.getConfigurationSection("plate")!=null) {
            for (String firstPlateData : areaConfig.getConfigurationSection("plate").getKeys(false) ) {
                final XYZ first = XYZ.fromString(firstPlateData);
                final XYZ second = XYZ.fromString(areaConfig.getString("plate."+firstPlateData+".second"));
                if (first==null || second==null) {
                    Ostrov.log_err("AreaManager ошибка загрузки платы "+firstPlateData+" : null");
                    continue;
                }
                final int cLoc = getcLoc(first);
                final ChunkContent cc = getChunkContent(cLoc, true);
                cc.addPlate(first, second);
            }
        }

        //подгрузка ачивок
        if (Main.advancements) {
            QuestAdvance.loadQuestAdv();
        }
       
        if (playerMoveTask!=null) {
            playerMoveTask.cancel();
        }
        
        playerMoveTask = new BukkitRunnable() {     //   !!!!ASYNC !!!!    каждую секунду
            
            int currentCuboidId;
            
            @Override
            public void run() {
                
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getTicksLived()<20) continue; //или при входе новичка тп на спавн и сразу на кораблик - и сразу открывается кубоид спавн. 
                                                        //причём в QuestManager так нельзя, или не детектит вход новичка!
                    
                    final LobbyPlayer lp = Main.getLobbyPlayer(p);
                    
                    //чек если игрок проходит состязание
                    //final Integer time = racePlayers.get(p.getName());
                    //if (time != null) {
                    	if (lp.raceTime > 0) { //>0 значит гонка активна
                            lp.raceTime++;
                            if (lp.raceTime>=300) {
                                p.sendMessage("§5[§eСостязание§5] §f>> Вы не дошли до §dфиниша §fвовремя!");
                                lp.raceTime = -1;
                            }
                    		//Ostrov.sync(()-> p.sendMessage("§5[§eСостязание§5] §f>> Вы не дошли до §dфиниша §fвовремя!"), 0);
                			//racePlayers.remove(p.getName());
                    	} //else {
                    		//racePlayers.replace(p.getName(), time + 1);
                        	//scoreboard время??
                        //}
                    //}
                    
                    currentCuboidId = getCuboidId(p.getLocation());
                    
                    if (lp.lastCuboidId!=currentCuboidId) { //зашел в новый кубоид
                        
                        if (currentCuboidId==0) { //вышел из кубоида в пространство
                            
                            LCuboid previos = cuboids.get(lp.lastCuboidId);
                            if (previos!=null) { //сработало при удалении? пропускаем
//ApiOstrov.sendActionBar(p, "вышел из кубоида "+previos.displayName);
                                Ostrov.sync(()-> Bukkit.getPluginManager().callEvent(new CuboidEvent(p, lp, previos, null, lp.cuboidEntryTime)), 0);
                            }
                            
                        } else if(lp.lastCuboidId==0) { //из пространства в кубоид
                            
                            final LCuboid current = cuboids.get(currentCuboidId);
                            if (current!=null) { //сработало при удалении? пропускаем
//ApiOstrov.sendActionBar(p, "вошел в кубоид "+current.displayName);
                                Ostrov.sync(()-> {
                                    Bukkit.getPluginManager().callEvent(new CuboidEvent(p, lp, null, current, 0)); 
                                    lp.cuboidEntryTime = Timer.getTime();
                                }, 0);
                                
                            }
                            
                        } else { //из кубоида в кубоил
                            
                            LCuboid previos = cuboids.get(lp.lastCuboidId);
                            LCuboid current = cuboids.get(currentCuboidId);
                            Ostrov.sync(()-> {
                                Bukkit.getPluginManager().callEvent(new CuboidEvent(p, lp, previos, current, lp.cuboidEntryTime)); 
                                if (current!=null) { //если есть кубоид входа, ставим метку времени
                                    lp.cuboidEntryTime = Timer.getTime();
                                }
                            }, 0);

                        }
                        
                        lp.lastCuboidId = currentCuboidId;
                    }
                }
                
            }

             
        }.runTaskTimerAsynchronously(Main.instance, 20, 9);
        
        
    }
    
    
    
    
    
    public static ChunkContent getChunkContent(final Location loc) {
        int cLoc = getcLoc(loc);
        return chunkContetnt.get(cLoc);
    }
        
    
    public static ChunkContent getChunkContent(final Location loc, final boolean createIfNotExist) {
        int cLoc = getcLoc(loc);
        return getChunkContent(cLoc, createIfNotExist);
    }
           
    public static ChunkContent getChunkContent(final int cLoc, final boolean createIfNotExist) {
        if (createIfNotExist && !chunkContetnt.containsKey(cLoc)) {
            final ChunkContent cc = new ChunkContent();
            chunkContetnt.put(cLoc, cc);
        }
        return chunkContetnt.get(cLoc);
    }    
    
    
    
    public static int getCuboidId(final Location loc) {
        final ChunkContent cc = getChunkContent(loc);
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

    public static Collection<LCuboid> getCuboids() {
        return cuboids.values();
    }
    
    public static Set<Integer> getCuboidIds() {
        return cuboids.keySet();
    }
    
    public static LCuboid getCuboid(final int cuboidId) {
        return cuboids.get(cuboidId);
    }



    
    
    
    
    public static int getcLoc(final XYZ xyz) {
        return getcLoc(xyz.worldName, xyz.x>>4, xyz.z>>4);
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
