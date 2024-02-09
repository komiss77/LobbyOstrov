package ru.ostrov77.lobby.area;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import net.kyori.adventure.text.Component;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.Timer;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.modules.quests.QuestManager;
import ru.komiss77.modules.world.XYZ;
import ru.komiss77.objects.CaseInsensitiveMap;
import ru.komiss77.utils.LocationUtil;
import ru.komiss77.utils.OstrovConfig;
import ru.komiss77.utils.TCUtils;
import ru.ostrov77.lobby.bots.SpotManager;
import ru.ostrov77.lobby.bots.spots.SpotType;
import ru.ostrov77.lobby.event.CuboidEvent;
import ru.ostrov77.lobby.game.Parkur;
import ru.ostrov77.lobby.LobbyFlag;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.quest.Quests;


public class AreaManager {
    
    private static OstrovConfig areaConfig;
    private static BukkitTask playerMoveTask;
    private static Map<Integer,ChunkContent>chunkContetnt;
    private static Map<Integer,LCuboid>cuboids;
    private static Map<String,Integer>cuboidNames;
    private static final List<String>compasslore = Arrays.asList("§6ЛКМ§e - задачи ","§2ПКМ§a - локации");
    

    public AreaManager () {

        chunkContetnt = new HashMap<>();
        cuboids = new HashMap<>();
        cuboidNames = new CaseInsensitiveMap<>();
        areaConfig = Main.configManager.getNewConfig("area.yml");
        
        if (areaConfig.getConfigurationSection("areas")!=null) {
            for (String areaID : areaConfig.getConfigurationSection("areas").getKeys(false) ) {
                try {
                    final int id = Integer.parseInt(areaID);
                    final String name = areaConfig.getString("areas."+areaID+".name");
                    final String displayName = areaConfig.getString("areas."+areaID+".displayName");
                    final String cuboidAsString = areaConfig.getString("areas."+areaID+".cuboidAsString");
                    final Location spawnPoint = ApiOstrov.locFromString(areaConfig.getString("areas."+areaID+".spawnPoint"));
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
        
        if (areaConfig.getConfigurationSection("spot")!=null) {
            for (String spotData : areaConfig.getConfigurationSection("spot").getKeys(false)) {
                SpotManager.addSpot(XYZ.fromString(spotData), SpotType.valueOf(areaConfig.getString("spot."+spotData)));
            }
        }
        
        if (playerMoveTask!=null) {
            playerMoveTask.cancel();
        }
        
        
        
        
        playerMoveTask = new BukkitRunnable() {     //   !!!!ASYNC !!!!    каждую секунду
            
            int currentCuboidId;
            final LCuboid ship = getCuboid("newbie");
            //Player p;
            
            @Override
            public void run() {
                
                for (final Oplayer op : PM.getOplayers()) { //if (lp==null) return; чекать не надо, перебор только созданных лоббиплееров
                	if (op instanceof final LobbyPlayer lp) {
                        final Player p = lp.getPlayer();
                        if (p==null || p.isDead() || p.getTicksLived()<20) continue; //или при входе новичка тп на спавн и сразу на кораблик - и сразу открывается кубоид спавн. 
                        
                    	if (lp.raceTime >= 0) { //>0 значит гонка активна
                            lp.raceTime++;
                            if (lp.raceTime>=600) {
                                p.sendMessage("§5[§eСостязание§5] §f>> Вы не дошли до §dфиниша §fвовремя!");
                                lp.raceTime = -1;
                            }
                    	}
                        
                        currentCuboidId = getCuboidId(p.getLocation());
                        
                        if (lp.lastCuboidId!=currentCuboidId) { //зашел в новый кубоид
                            
                            if (currentCuboidId==0) { //вышел из кубоида в пространство
                                
                            	final LCuboid previos = cuboids.get(lp.lastCuboidId);
                                if (previos!=null) { //сработало при удалении? пропускаем
    //ApiOstrov.sendActionBar(p, "вышел из кубоида "+previos.displayName);
                                    Ostrov.sync(()-> {
                                        Bukkit.getPluginManager().callEvent(new CuboidEvent(p, lp, previos, null, lp.cuboidEntryTime));
                                        previos.playerNames.remove(lp.nik);
                                    }, 0);
                                }
                                
                            } else if(lp.lastCuboidId==0) { //из пространства в кубоид
                                
                            	final LCuboid current = cuboids.get(currentCuboidId);
                                if (current!=null) { //сработало при удалении? пропускаем
    //ApiOstrov.sendActionBar(p, "вошел в кубоид "+current.displayName);
                                    Ostrov.sync(()-> {
                                        Bukkit.getPluginManager().callEvent(new CuboidEvent(p, lp, null, current, 0)); 
                                        lp.cuboidEntryTime = Timer.getTime();
                                        current.playerNames.add(lp.nik);
                                    }, 0);
                                    
                                }
                                
                            } else { //из кубоида в кубоил
                                
                            	final LCuboid previos = cuboids.get(lp.lastCuboidId);
                            	final LCuboid current = cuboids.get(currentCuboidId);
                                Ostrov.sync(()-> {
                                    Bukkit.getPluginManager().callEvent(new CuboidEvent(p, lp, previos, current, lp.cuboidEntryTime)); 
                                    if (current!=null) { //если есть кубоид входа, ставим метку времени
                                        lp.cuboidEntryTime = Timer.getTime();
                                        previos.playerNames.remove(lp.nik);
                                        current.playerNames.add(lp.nik);
                                    }
                                }, 0);

                            }
                            //еще надо ловить quitEvent
                            lp.lastCuboidId = currentCuboidId;
                        }
                        
                        
                        
                        
                        if (lp.pkrist != null) {
                           // final Player p = lp.getPlayer();
                            final Parkur pr = lp.pkrist;
                            final Location loc = p.getLocation();
                            if (loc.getY() < pr.bLast.y) { //упал
                                p.sendMessage("§7[§bМини-Паркур§7] >> Вы упали! Пропрыгано блоков: §b" + pr.jumps);
                                lp.pkrist = null;
                                loc.getWorld().getBlockAt(pr.bLast.x, pr.bLast.y, pr.bLast.z).setType(Material.AIR, false);
                                loc.getWorld().getBlockAt(pr.bNext.x, pr.bNext.y, pr.bNext.z).setType(Material.AIR, false);
                                p.teleport(getCuboid("parkur").spawnPoint);
                                lp.pkrist = null;
                                Ostrov.sync(() -> {
                                    p.playSound(loc, Sound.BLOCK_AMETHYST_CLUSTER_PLACE, 2f, 0.6f);
                                    if (pr.jumps >= 12) QuestManager.complete(p, op, Quests.jump);
                                }, 4);
                            } else if (loc.getBlockX() == pr.bNext.x && loc.getBlockZ() == pr.bNext.z) {
                                p.playSound(loc, Sound.BLOCK_AMETHYST_BLOCK_HIT, 2f, 0.1f * pr.jumps + 0.5f);
                                pr.nextBlock();
                            }
                        }                    
                        
                        
                    }
                    
                    if (ship!=null && !ship.playerNames.isEmpty()) {
                        final Location shipLamp = Main.getLocation(Main.LocType.ginLamp);
    //Bukkit.broadcastMessage("ship.playerNames="+ship.playerNames);
                        shipLamp.getWorld().spawnParticle(Particle.SPELL_WITCH, shipLamp, 2,  0.2, 0.1, 0.2, 0.01);
                    }
            	}
                
            }

        //}.runTaskTimerAsynchronously(Main.instance, 20, 10); java.util.ConcurrentModificationException
        }.runTaskTimer(Main.instance, 20, 8); 
    }
    
    
    
    
    public static void deleteCuboid(final int cuboidId) {
        if (cuboids.containsKey(cuboidId)) {
            final String name = cuboids.remove(cuboidId).getName();
            cuboidNames.remove(name);
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
    @SuppressWarnings("deprecation")
    protected static void addCuboid(final LCuboid lc, final boolean save) {
        cuboids.put(lc.id, lc);
        cuboidNames.put(lc.getName(), lc.id);
        final Set<Integer>cLocs = new HashSet<>(); //собираем cLoки кубоида для добавления в чанки
		final Iterator<Location> it = lc.borderIterator(Bukkit.getWorld("world"));
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
        areaConfig.set("areas."+lc.id+".name", lc.getName());
        areaConfig.set("areas."+lc.id+".displayName", lc.displayName);
        areaConfig.set("areas."+lc.id+".spawnPoint", LocationUtil.toString(lc.spawnPoint));
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

    public static void saveSpot(final XYZ loc, final SpotType st) {
        if (st==null) { //удаление
            areaConfig.set("spot." + loc.toString(), null);
        	Bukkit.broadcast(TCUtils.format("removing-" + loc.toString()));
        } else {
            areaConfig.set("spot." + loc.toString(), st.toString());
        	Bukkit.broadcast(TCUtils.format("created-" + loc.toString()));
        }
        
        areaConfig.saveConfig();
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
    
    public static boolean hasCuboid(final String cuboidName) {
        return cuboidNames.containsKey(cuboidName);
    }
    
    public static boolean hasCuboid(final int cuboidId) {
        return cuboids.containsKey(cuboidId);
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

    public static LCuboid getCuboid(final XYZ loc) {
        int cLoc = getcLoc(loc);
        final ChunkContent cc = chunkContetnt.get(cLoc);
        if (cc==null || !cc.hasCuboids()) return null;
        for (int cuboidId : cc.getCuboidsIds()) {
            if (cuboids.containsKey(cuboidId) && cuboids.get(cuboidId).contains(loc.x, loc.y, loc.z)) {
                return cuboids.get(cuboidId);
            }
        }
        return null;
    }
    
    public static LCuboid getCuboid(final String cuboidName) {
        return cuboidNames.containsKey(cuboidName) ? cuboids.get(cuboidNames.get(cuboidName)) : null;
    }

    public static LCuboid getCuboid(final CuboidInfo ci) {
        return getCuboid(ci.name());
    }

    public static Collection<LCuboid> getCuboids() {
        return cuboids.values();
    }
    
    public static Set<Integer> getCuboidIds() {
        return cuboids.keySet();
    }
    
   public static Set<String> getCuboidNames() {
        return cuboidNames.keySet();
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
    
    
    
    
    public static void setCompassTarget(final Player p, final LobbyPlayer lp, final LCuboid lc) {
        p.setCompassTarget(lc.spawnPoint);
        lp.target = lc.getInfo();
        final ItemStack compass = p.getInventory().getItem(0);
        if (compass!=null && compass.getType()==Material.COMPASS && compass.hasItemMeta()) {
            final ItemMeta im = compass.getItemMeta();
            if (im.hasLore()) {
                final List<Component> lore = new ArrayList<>();
                for (final String s : compasslore) {
                	lore.add(TCUtils.format(s));
                }
                lore.add(TCUtils.format(""));
                lore.add(TCUtils.format("§fНастроен на локацию:"));
                lore.add(TCUtils.format(lc.displayName));
                im.lore(lore);
                im.addEnchant(Enchantment.LUCK, 1, true);
                compass.setItemMeta(im);
                p.getInventory().setItem(0, compass);
                //p.updateInventory();
            }
        }
        p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_LAUNCH, 1, 1);
        if (lp.hasFlag(LobbyFlag.GinTravelDone)) QuestManager.complete(p, lp, Quests.navig);
    }

    public static void resetCompassTarget(final Player p, final LobbyPlayer lp) {
        p.setCompassTarget(p.getLocation());
        lp.target = CuboidInfo.DEFAULT;
        final ItemStack compass = p.getInventory().getItem(0);
        if (compass!=null && compass.getType()==Material.COMPASS && compass.hasItemMeta()) {
            final ItemMeta im = compass.getItemMeta();
            if (im.hasLore()) {
                final List<Component> lore = new ArrayList<>();
                for (final String s : compasslore) {
                	lore.add(TCUtils.format(s));
                }
                im.lore(lore);
                im.removeEnchant(Enchantment.LUCK);
                compass.setItemMeta(im);
                p.getInventory().setItem(0, compass);
            }
        }
        p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_LAUNCH, 1, 1);
    }
}
