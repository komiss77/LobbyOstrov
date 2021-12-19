package ru.ostrov77.lobby.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Orientable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;
import ru.komiss77.ApiOstrov;
import ru.komiss77.LocalDB;
import ru.komiss77.Ostrov;
import ru.komiss77.Timer;
import ru.komiss77.events.ArmorEquipEvent;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.LocationUtil;
import ru.ostrov77.lobby.LobbyFlag;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.Main.LocType;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.ChunkContent;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.area.XYZ;
import ru.ostrov77.lobby.event.CuboidEvent;
import ru.ostrov77.lobby.newbie.JinGoal;
import ru.ostrov77.lobby.quest.PKrist;
import ru.ostrov77.lobby.quest.Quest;
import ru.ostrov77.lobby.quest.Advance;
import ru.ostrov77.lobby.quest.QuestManager;





public class ListenerWorld implements Listener {
    
    private static final BlockFace[] nr = {BlockFace.DOWN, BlockFace.UP, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST};

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onArmor (final ArmorEquipEvent e) {
System.out.println("ArmorEquipEvent");
        if (!e.getPlayer().isSneaking()) e.setCancelled(true);
    }

    	
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityPortalEnter(final EntityPortalEnterEvent e) {
        
        if (e.getEntityType()==EntityType.PLAYER) {
            
            if (e.getEntity().getTicksLived() < 100 || Timer.has(e.getEntity().getEntityId()))  return;
            final Player p = (Player) e.getEntity();
            Timer.add(p.getEntityId(), 5);
            
            for (final XYZ xyzw : Main.serverPortals.keySet()) {
                if (xyzw.nearly(e.getLocation(), 16)) {
                    p.performCommand("server "+Main.serverPortals.get(xyzw));
                }
            }

        }
        
          /*  if (e.getEntityType() == EntityType.PLAYER && !Main.prts.isEmpty()) {
                    final XYZW xyzw = new XYZW (e.getLocation());
                    
                    int d = Integer.MAX_VALUE;
                    String n = "";
                    for (final Entry<BaseBlockPosition, String> en : Main.prts.entrySet()) {
                            final int dd = (int) en.getKey().distanceSquared(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), true);
                            if (dd < d) {
                                    d = dd;
                                    n = en.getValue();
                            }
                    }
                    final Player p = (Player) e.getEntity();
                    if (Timer.has(p, "portal") && p.getTicksLived() < 100) {
                            return;
                }
                Timer.add(p, "portal", 5);
Ostrov.log_warn("EntityPortalEnter performCommand server "+n);
                    //if (n!=null) {
                        p.performCommand("server " + n);
                    //} else {
                    //    Ostrov.log_err("onPrtl n=null , чекайте почему!");
                    //}
            }*/
    }
    
    
    
   // @EventHandler (priority = EventPriority.MONITOR)
   // public void onBungeeData(final BungeeDataRecieved e) {
       // final Player p = e.getPlayer();
        //final LobbyPlayer lp = Main.getLobbyPlayer(p);
        
   // }    
    @EventHandler (priority = EventPriority.MONITOR)
    public void onJoin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        final LobbyPlayer lp = Main.createLobbyPlayer(p);
        //QuestAdvance.onJoin(p);
        
        Ostrov.async( () -> {

            Statement stmt = null;
            ResultSet rs = null;
            String logoutLoc = "";
            
            try {  
                stmt = LocalDB.GetConnection().createStatement(); 
                rs = stmt.executeQuery( "SELECT * FROM `lobbyData` WHERE `name` = '"+lp.name+"' LIMIT 1" );
                
                if (rs.next()) {
                    logoutLoc = rs.getString("logoutLoc");
                    lp.setFlags(rs.getInt("flags"));
                    lp.setOpenedArea(rs.getInt("openedArea"));
                    Quest quest;
                    for (char c : rs.getString("questDone").toCharArray()) {
                        quest = Quest.byCode(c);
                        if (quest!=null) {
                            lp.questDone.add(quest);
                        }
                    }
                    for (char c : rs.getString("questAccept").toCharArray()) {
                        quest = Quest.byCode(c);
                        if (quest!=null) {
                            lp.questAccept.add(quest);
                        }
                    }
                } else { //создать сразу запись, или не будут работать save : executePstAsync UPDATE
                    LocalDB.executePstAsync(Bukkit.getConsoleSender(), "INSERT INTO `lobbyData` (name) VALUES ('"+lp.name+"') ");
                }
                
                //Ostrov.sync(()-> {
                //    onDataLoad(p, lp, l);
                //}, 0);

            } catch (SQLException ex) {

                Ostrov.log_err("ListenerOne error  "+lp.name+" -> "+ex.getMessage());

            } finally {
                
                //Ostrov.sync(()-> {
                    onDataLoad(p, lp, logoutLoc);
                //}, 0);
                try{
                    if (rs!=null && !rs.isClosed()) rs.close();
                    if (stmt!=null) stmt.close();
                } catch (SQLException ex) {
                    Ostrov.log_err("ListenerOne close error - "+ex.getMessage());
                }
            }
            
        }, 0);

    }
    
    

    private void onDataLoad(Player p, LobbyPlayer lp, final String logoutLocString) {
        Ostrov.sync( ()-> {

            if (Main.advancements) {
                Advance.send(p);
            }
            
            if (!lp.hasFlag(LobbyFlag.NewBieDone)) {
                //p.getInventory().clear(); - не надо, инв. не сохраняется, при входе будет пусто
                //lp.setFlag(LobbyFlag.NewBieDone, true); -не ставитть сразу, или не смогут выполнить задание приветствие новичка
                //NewBie.start(p, 0);
                PM.getOplayer(p).hideScore();
                p.teleport(Main.getLocation(Main.LocType.newBieSpawn));// тп на 30 160 50
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 5));
                p.setCollidable(false);
                Main.oscom.give(p); //ApiOstrov.getMenuItemManager().giveItem(p, "newbie");
                ApiOstrov.sendBossbar(p, "#3 Остров.", 5, BarColor.PINK, BarStyle.SOLID, false);
            } else {
                Main.giveItems(p);
                final Location logoutLoc = LocationUtil.LocFromString(logoutLocString);
                if (logoutLoc !=null && ApiOstrov.teleportSave(p, logoutLoc, false)) {
//ApiOstrov.sendActionBarDirect(p, "§8log: тп на точку выхода");
                } else {
                    p.teleport(Main.getLocation(Main.LocType.Spawn));
//ApiOstrov.sendActionBarDirect(p, "§8log: точка выхода опасна, тп на спавн");
                }
            }
        }, 2);        
    }

    
    
    
    @EventHandler (priority = EventPriority.NORMAL)
    public void onQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        //NewBie__.stop(p);
        final LobbyPlayer lp = Main.destroyLobbyPlayer(p.getName());
        if (lp!=null) {
            final String logoutLoc = LocationUtil.StringFromLocWithYawPitch(p.getLocation());
            LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `lobbyData` SET `logoutLoc` = '"+logoutLoc+"' WHERE `name` = '"+lp.name+"';");
            final LCuboid exitCuboid = AreaManager.getCuboid(p.getLocation());
            if (exitCuboid!=null) {
                if (exitCuboid.playerNames.remove(p.getName())) {
                    Bukkit.getPluginManager().callEvent(new CuboidEvent(p, lp, exitCuboid, null, lp.cuboidEntryTime));
                }
            }
        }
        p.removeMetadata("tp", Main.instance);
        if (Main.advancements) {
            Advance.onQuit(p);
        }
    }    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // ******** эвенты по новичку
    
    @EventHandler
    public static void onCuboidEvent(final CuboidEvent e) {
        if (e.getPrevois()!=null && e.getPrevois().getName().equals("newbie")) { //выход из кубоида новичка
            final Player p = e.getPlayer();
            for (final Player cp : e.getPrevois().getCuboidPlayers()) {
                if (!cp.getName().equals(p.getName())) {
                    cp.showPlayer(Main.instance, p);
                    p.showPlayer(Main.instance, cp);
//cp.sendMessage("§8log: §c showPlayer "+p.getName());
//p.sendMessage("§8log: §c showPlayer "+cp.getName());                  
                }
            }
            /*for (final LobbyPlayer lp : Main.getLobbyPlayers()) {
                if (lp.name.equals(e.lp.name) || lp.lastCuboidId!=e.previos.id) continue; //самого себя и тех, кто не на кораблике пропускаем
                lp.getPlayer().showPlayer(Main.instance, p);
                p.showPlayer(Main.instance, lp.getPlayer());
//lp.getPlayer().sendMessage("§8log: §ashowPlayer "+p.getName());
//p.sendMessage("§8log: §ashowPlayer "+lp.name);          
            }*/
        }
        if (e.getCurrent()!=null && e.getCurrent().getName().equals("newbie")) { //вход в кубоида новичка
            final Player p = e.getPlayer();
            for (final Player cp : e.getCurrent().getCuboidPlayers()) {
                if (!cp.getName().equals(p.getName())) {
                    cp.hidePlayer(Main.instance, p);
                    p.hidePlayer(Main.instance, cp);
//cp.sendMessage("§8log: §c hidePlayer "+p.getName());
//p.sendMessage("§8log: §c hidePlayer "+cp.getName());              
                }
            }
            /*for (final LobbyPlayer lp : Main.getLobbyPlayers()) {
                if (lp.name.equals(e.lp.name) || lp.lastCuboidId!=e.current.id) continue; //самого себя и тех, кто не на кораблике пропускаем
                lp.getPlayer().hidePlayer(Main.instance, p);
                p.hidePlayer(Main.instance, lp.getPlayer());
//lp.getPlayer().sendMessage("§8log: §chidePlayer "+p.getName());
//p.sendMessage("§8log: §chidePlayer "+lp.name);
            }*/
        }
    }    
    
    
    
    
    
    @EventHandler (ignoreCancelled = true)
    public void onPlayerChat(AsyncChatEvent e) { //только когда новичёк пишет в чат
        if (!e.getPlayer().getWorld().getName().equals("world")) return;
        final LCuboid lc = AreaManager.getCuboid(e.getPlayer().getLocation());
        if (lc!=null && lc.getName().equals("newbie")) {
            //e.
//e.getPlayer().sendMessage("§8log: чат новичка "+e.viewers());            
            //e.setCancelled(true);
            //e.viewers().clear();
        }
    }      
    
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDismount(final EntityDismountEvent e) {
//e.getEntity().sendMessage("§7log: onDismount getEntityType="+e.getEntityType()+" getDismountedType="+e.getDismounted().getType()+" getEntityType"+e.getEntity().getType());
        if (e.getEntityType()==EntityType.PLAYER && e.getDismounted().getType()==EntityType.BLAZE) {
            //final Player p = (Player) e.getEntity();
            if (e.getDismounted().isCustomNameVisible() && e.getDismounted().getCustomName().equals(JinGoal.ginName)) {
                e.setCancelled(true);
                if (!Timer.has(e.getEntity().getEntityId())) {
                    e.getEntity().sendMessage("§6Погодите, уже скоро будем на месте!");
                    Timer.add(e.getEntity().getEntityId(), 3);
                }
            }
            //final LobbyPlayer lp = Main.getLobbyPlayer(p);
            //if (!lp.hasFlag(LobbyFlag.NewBieDone))
        }
    }
    
    
    //***************************        
    
    
    
    
   // @EventHandler (ignoreCancelled = true)
   // public void onPlayerChat(AsyncChatEvent e) {
      //  if (!e.getPlayer().getWorld().getName().equals("world")) return;
       // if (NewBie__.hasNewBieTask(e.getPlayer())) {
            
        //}
        //e.setCancelled(true);
        //e.viewers().clear();
        //e.getPlayer().sendMessage("§6Для пропуска интро просто перезайдите.");
   // }      
    
    
    
    
    
    
    
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onPlace(final BlockPlaceEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("world")) return;
        if (!ApiOstrov.isLocalBuilder(e.getPlayer()) ) e.setCancelled(true);
        
        final Block b = e.getBlockPlaced();
		
        switch (b.getType()) {
            
            case FIRE:
                if (plcAtmpt(b, BlockFace.EAST) || plcAtmpt(b, BlockFace.SOUTH)) {
                    
                    final ItemStack it = e.getItemInHand();
                    if (it.getType()!=Material.AIR && it.hasItemMeta()) {
                        final String servername = ((TextComponent) it.getItemMeta().displayName()).content();
                        final Location loc = b.getLocation();
                        
                        Main.serverPortals.put(new XYZ(loc), servername);
                        Main.savePortals();
                        
                        
                        /*final FileConfiguration cfg = Main.instance.getConfig();
                        final ConfigurationSection cs = cfg.getConfigurationSection("prtls");
                        if (cs == null) {
                        cfg.set("prtls.x", loc.getBlockX());
                        cfg.set("prtls.y", loc.getBlockY());
                        cfg.set("prtls.z", loc.getBlockZ());
                        cfg.set("prtls.s", nm);
                        } else {
                        cs.set("x", cs.getString("x") + ":" + loc.getBlockX());
                        cs.set("y", cs.getString("y") + ":" + loc.getBlockY());
                        cs.set("z", cs.getString("z") + ":" + loc.getBlockZ());
                        cs.set("s", cs.getString("s") + ":" + nm);
                        }
                        
                        try {
                        cfg.save(Main.instance.getDataFolder() + File.separator + "config.yml");
                        e.getPlayer().sendMessage("§eПортал " + nm + " создан!");
                        } catch (IOException ex) {
                        ex.printStackTrace();
                        }*/
                    }
                }
                break;
                
                
        //else if (!clear_stats) PM.Addbplace(e.getPlayer().getName());
            case HEAVY_WEIGHTED_PRESSURE_PLATE:
                final Player p = e.getPlayer();
                final Location loc = b.getLocation();
                
                if (p.hasMetadata("tp")) {
                    
                    final XYZ firstPlateXYZ = (XYZ) p.getMetadata("tp").get(0).value();
                    final XYZ secondPlateXYZ = new XYZ(loc);
 //System.out.println("§8log: firstPlateXYZ="+firstPlateXYZ.toString());                    
 //System.out.println("§8log: secondPlateXYZ="+secondPlateXYZ.toString());    
 
                    final int cLoc = AreaManager.getcLoc(firstPlateXYZ);
                    final ChunkContent cc = AreaManager.getChunkContent(cLoc, true); //берём чанк по ПЕРВОЙ плите, запоминаем то в первой!
                    cc.addPlate(firstPlateXYZ, secondPlateXYZ);
                    AreaManager.savePlate(firstPlateXYZ, secondPlateXYZ);
                    
                    /*final FileConfiguration cfg = Main.instance.getConfig();
                    final ConfigurationSection cs = cfg.getConfigurationSection("plts");
                    final BaseBlockPosition fst = (BaseBlockPosition) p.getMetadata("tp").get(0).value();
                    if (cs == null) {
                    cfg.set("Rom.plts.bx", fst.getX());
                    cfg.set("Rom.plts.by", fst.getY());
                    cfg.set("Rom.plts.bz", fst.getZ());
                    cfg.set("Rom.plts.ex", loc.getBlockX());
                    cfg.set("Rom.plts.ey", loc.getBlockY());
                    cfg.set("Rom.plts.ez", loc.getBlockZ());
                    } else {
                    cs.set("bx", cs.getString("bx") + ":" + fst.getX());
                    cs.set("by", cs.getString("by") + ":" + fst.getY());
                    cs.set("bz", cs.getString("bz") + ":" + fst.getZ());
                    cs.set("ex", cs.getString("ex") + ":" + loc.getBlockX());
                    cs.set("ey", cs.getString("ey") + ":" + loc.getBlockY());
                    cs.set("ez", cs.getString("ez") + ":" + loc.getBlockZ());
                    }
                    PlateManager.plts.put(fst, new BaseBlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                    
                    try {
                    cfg.save(Main.instance.getDataFolder() + File.separator + "config.yml");
                    } catch (IOException ex) {
                    ex.printStackTrace();
                    }*/
                    
                    p.sendMessage("§2Вторая плита поставлена на координатах (§7" + loc.getBlockX() + "§2, §7" + loc.getBlockY() + "§2, §7" + loc.getBlockZ() + "§2)!");
                    p.removeMetadata("tp", Main.instance);
                    e.setCancelled(true);
                    p.sendMessage("§2Плита создана!");
                } else {
                    //p.setMetadata("tp", new FixedMetadataValue(Main.instance, new BaseBlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())));
                    p.setMetadata("tp", new FixedMetadataValue(Main.instance, new XYZ(loc)));
                    p.sendMessage("§aПервая плита поставлена на координатах (§7" + loc.getBlockX() + "§a, §7" + loc.getBlockY() + "§a, §7" + loc.getBlockZ() + "§a)!");
                }
                break;
                
                
            case BEDROCK:
                Main.loadCfgs();
                e.getPlayer().sendMessage("§eПерезагружено!");
                break;
                
            default:
                break;
                
        }
        
    }
    
    
    
    
    
    
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onBreak(final BlockBreakEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("world")) return;
        if (!ApiOstrov.isLocalBuilder(e.getPlayer()) ) e.setCancelled(true);
        
		if (e.getBlock().getType() == Material.NETHER_PORTAL && !Main.serverPortals.isEmpty()) {
                    
			final Location loc = e.getBlock().getLocation();
                        XYZ find = null;
                        for (final XYZ xyzw : Main.serverPortals.keySet()) {
                            if (xyzw.nearly(loc, 16)) {
                                find = xyzw;
                                break;
                            }
                        }
                        if (find!=null) {
                            Main.serverPortals.remove(find);
                            Main.savePortals();
                        }
                        return;
			//final FileConfiguration cfg = Main.instance.getConfig();
			/*int d = Integer.MAX_VALUE;
			BaseBlockPosition rb = new BaseBlockPosition(0, 0, 0);
			for (final Entry<BaseBlockPosition, String> en : Main.serverPortals.entrySet()) {
				final int dd = (int) en.getKey().distanceSquared(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), true);
				if (dd < d) {
					d = dd;
					rb = en.getKey();
				}
			}
			
			//убираем из HashMap
			final String pnm = Main.serverPortals.remove(rb);
		
			//убираем из файла
			if (Main.serverPortals.isEmpty()) {
				cfg.set("prtls", null);
			} else {
				final StringBuffer nx = new StringBuffer("");
				final StringBuffer ny = new StringBuffer("");
				final StringBuffer nz = new StringBuffer("");
				final StringBuffer ns = new StringBuffer("");
				d = Main.prts.size();
				for (final Entry<BaseBlockPosition, String> en : Main.prts.entrySet()) {
					d--;
					nx.append(en.getKey().getX() + (d == 0 ? "" : ":"));
					ny.append(en.getKey().getY() + (d == 0 ? "" : ":"));
					nz.append(en.getKey().getZ() + (d == 0 ? "" : ":"));
					ns.append(en.getValue() + (d == 0 ? "" : ":"));
				}
				cfg.set("prtls.x", nx.toString());
				cfg.set("prtls.y", ny.toString());
				cfg.set("prtls.z", nz.toString());
				cfg.set("prtls.s", ns.toString());
			}
			
			try {
				cfg.save(Main.instance.getDataFolder() + File.separator + "config.yml");
				e.getPlayer().sendMessage("§6Портал " + pnm + " убран!");
			} catch (IOException ex) {
				ex.printStackTrace();
			}*/
		} 
                
                if (e.getBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) { //ставим золотую плату, чтобы убрать обработчик
                    
                    final Location loc = e.getBlock().getLocation();
                    final ChunkContent cc = AreaManager.getChunkContent(loc);
//System.out.println("§8log: cc="+cc+" has?"+(cc!=null && cc.hasPlate()));  
                    if (cc!=null && cc.hasPlate()) {
                        final XYZ second = cc.getPlate(loc);
//System.out.println("§8log: second="+second);  
                        if (second!=null) { //пункт назначения назначен - значит плата есть
                            cc.delPlate(loc);
                            AreaManager.savePlate(new XYZ(loc), null);
                            e.getPlayer().sendMessage("§6Плита, ведущаяя к §a"+second+" §6убрана!");
                        }
                    }
                    
			/*final FileConfiguration cfg = Main.instance.getConfig();
			//убираем из HashMap
			final BaseBlockPosition rb = new BaseBlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			if (PlateManager.plts.remove(rb) != null) {
				//убираем из файла
				if (PlateManager.plts.isEmpty()) {
					cfg.set("Rom.plts", null);
				} else {
					final StringBuffer nbx = new StringBuffer("");
					final StringBuffer nby = new StringBuffer("");
					final StringBuffer nbz = new StringBuffer("");
					final StringBuffer nex = new StringBuffer("");
					final StringBuffer ney = new StringBuffer("");
					final StringBuffer nez = new StringBuffer("");
		
					int d = PlateManager.plts.size();
					for (final Entry<BaseBlockPosition, BaseBlockPosition> en : PlateManager.plts.entrySet()) {
						d--;
						nbx.append(en.getKey().getX() + (d == 0 ? "" : ":"));
						nby.append(en.getKey().getY() + (d == 0 ? "" : ":"));
						nbz.append(en.getKey().getZ() + (d == 0 ? "" : ":"));
						nex.append(en.getValue().getX() + (d == 0 ? "" : ":"));
						ney.append(en.getValue().getY() + (d == 0 ? "" : ":"));
						nez.append(en.getValue().getZ() + (d == 0 ? "" : ":"));
					}
					cfg.set("Rom.plts.bx", nbx.toString());
					cfg.set("Rom.plts.by", nby.toString());
					cfg.set("Rom.plts.bz", nbz.toString());
					cfg.set("Rom.plts.ex", nex.toString());
					cfg.set("Rom.plts.ey", ney.toString());
					cfg.set("Rom.plts.ez", nez.toString());
				}
				
				try {
					cfg.save(Main.instance.getDataFolder() + File.separator + "config.yml");
					e.getPlayer().sendMessage("§6Плита на коорд. (§7" + rb.getX() + "§6, §7" + rb.getY() + "§6, §7" + rb.getZ() + "§6) убрана!");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}*/
		}
        //else if (!clear_stats) PM.get(e.getPlayer().getName());
    }

    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onHangingBreakByEntityEvent(final HangingBreakByEntityEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) return;
        if (  e.getRemover()!=null && e.getRemover().getType()==EntityType.PLAYER && !Ostrov.isCitizen(e.getEntity()) ) {
                if (!ApiOstrov.isLocalBuilder((Player) e.getRemover()) ) e.setCancelled(true);
        } 

    }
       
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onHangingBreakEvent(final HangingBreakEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) return;
        if ( e.getEntity().getType()==EntityType.PLAYER) {
                if (!ApiOstrov.isLocalBuilder((Player) e.getEntity()) ) e.setCancelled(true);
        } 
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onPlayerInteractAtEntityEvent(final PlayerInteractAtEntityEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("world")) return;
        final Player p = e.getPlayer();
        if( e.getRightClicked().getType() ==EntityType.ARMOR_STAND && !ApiOstrov.isLocalBuilder(p) ) {
            e.setCancelled(true);
        }
        if (e.getRightClicked().getType()==EntityType.PLAYER) {
            final LobbyPlayer lp = Main.getLobbyPlayer(p);
            final LobbyPlayer clickedLp = Main.getLobbyPlayer(e.getRightClicked().getName());
//p.sendMessage("§8log: ПКМ на игрока, новичёк?"+!clickedLp.questDone.contains(Quest.DiscoverAllArea));
            if (lp!=null && clickedLp!=null && !clickedLp.hasFlag(LobbyFlag.NewBieDone)) {
                QuestManager.tryCompleteQuest(p, lp, Quest.GreetNewBie);
            	//QuestManager.completeAdv(p, Quest.GreetNewBie);
                //lp.questDone(p, Quest.GreetNewBie, true);
            }
        }
    }


   

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void PlayerArmorStandManipulateEvent(final PlayerArmorStandManipulateEvent e){
        if (!e.getPlayer().getWorld().getName().equals("world")) return;
        if (!ApiOstrov.isLocalBuilder(e.getPlayer())) e.setCancelled(true);
    }        
    
    
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onPlayerRespawn(final PlayerRespawnEvent e) {
         if (!e.getPlayer().getWorld().getName().equals("world")) return;
    }
    
    
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void ProjectileHitEvent(final ProjectileHitEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) return;
    }    





    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent e) { 
        if (!e.getEntity().getWorld().getName().equals("world")) return;
        
        //subo
        if (e.getEntityType()==EntityType.PLAYER && !Ostrov.isCitizen(e.getEntity()) ) {
            final LivingEntity le = (LivingEntity) e.getEntity();
            switch (e.getCause()) {
                case VOID:
                    e.setDamage(0);
                    final Player p = (Player) e.getEntity();
                    final LobbyPlayer lp = Main.getLobbyPlayer(p);
                    if (lp.hasFlag(LobbyFlag.NewBieDone)) { //старичков кидаем на спавн
                        p.teleport (Main.getLocation(LocType.Spawn), PlayerTeleportEvent.TeleportCause.COMMAND);
                    } else { //новичков - если прыгнул за борт - на точку прибытия
                        Main.arriveNewBie(p);
                    }
                    final EntityDamageEvent de = le.getLastDamageCause();
                    if (de instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) de).getDamager().getType() == EntityType.PLAYER) {
                    	final Player dp = (Player) ((EntityDamageByEntityEvent) de).getDamager();
                    	QuestManager.tryCompleteQuest(dp, Main.getLobbyPlayer(dp), Quest.SumoVoid);
                        for (final Player pl : dp.getWorld().getPlayers()) {
                            pl.sendMessage("§7[§cСумо§7] Игрок §a" + dp.getName() + " §7 скинул §с" + p.getName() + "§7 в пустоту!");
                        }                    
                    }
                    return;
                    
                case FALL:
                case THORNS:        //чары шипы на оружие-ранит нападающего
                case LIGHTNING:     //молния
                case DRAGON_BREATH: //дыхание дракона
                case CONTACT:       //кактусы
                case FIRE:          //огонь
                case FIRE_TICK:     //горение
                case HOT_FLOOR:     //BlockMagma
                case CRAMMING:      //EntityVex
                case DROWNING:      //утопление
                case STARVATION:    //голод
                case LAVA:			//лава
                    e.setCancelled(true);
                    return;
                    //break;

                case ENTITY_ATTACK: //ентити ударяет
                	if (e instanceof EntityDamageByEntityEvent) {
                		final EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
                		if (ee.getDamager().getType() == EntityType.PLAYER) {
                			final LCuboid vCub = AreaManager.getCuboid(e.getEntity().getLocation());
                			if (vCub != null && vCub.getName().equals("sumo")) {
                    			final LCuboid dCub = AreaManager.getCuboid(ee.getDamager().getLocation());
                    			if (dCub != null && dCub.getName().equals("sumo")) {
                    				Ostrov.sync(() -> le.setHealth(le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()), 2);
                    				return;//сумо
                    			}
                			}
                			
                		}
                	}
                default:
                    e.setCancelled(true);
                    
                    //return;
            }
        } else {
            e.setCancelled(true);
        }
                

    }






    @EventHandler (ignoreCancelled = true)
    public void onPlayerPickUpItem(final EntityPickupItemEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) return;
    }


   // @EventHandler(ignoreCancelled = true)
   // public void onPlayerSwapoffHand(PlayerSwapHandItemsEvent e) {
   //     if (!e.getPlayer().getWorld().getName().equals("world")) return;
   //     e.setCancelled(true);
  //  }
   
  
    @EventHandler  (ignoreCancelled = true)
    public void onHungerChange(final FoodLevelChangeEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) return;
        e.setCancelled(true); 
        ((Player)e.getEntity()).setFoodLevel(20);
    }
        

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    //@EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = true)
   // public void onMerge(final ItemMergeEvent e) {
    //    if (!e.getEntity().getWorld().getName().equals("world")) return;
    //    e.setCancelled(true);
   // }

   
  //  @EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = true)
   // public void onDespawn(final ItemDespawnEvent e) {
//log("onDespawn nb?"+e.getEntity().getWorld().getName().startsWith("newbie"));
        //if (!e.getEntity().getWorld().getName().equals("world")) return;
        //if (e.getEntity().getItemStack().getType()==Material.NETHER_STAR) {
        //    e.setCancelled(true);
        //}
   // }

   


    @EventHandler (ignoreCancelled = true)
    public void onBlockFade(final BlockFadeEvent e) {
        if (!e.getBlock().getWorld().getName().equals("world")) return;
        if( e.getBlock().getType() == Material.ICE || e.getBlock().getType() == Material.PACKED_ICE || e.getBlock().getType() == Material.SNOW || e.getBlock().getType() == Material.SNOW_BLOCK) 
        e.setCancelled(true);
    }


    
    
    @EventHandler (ignoreCancelled = true)
    public void onCreatureSpawnEvent(final CreatureSpawnEvent e) {
        if (e.getEntity().getWorld().getName().equals("world")) return;
        e.setCancelled(true);
    }
  
    
    @EventHandler (ignoreCancelled = true)
    public void onWeatherChange(final WeatherChangeEvent e) {
        if (!e.getWorld().getName().equals("world")) return;
        if (e.toWeatherState()) e.setCancelled(true);
    }


          
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockSpread(final BlockSpreadEvent e) {
        if (!e.getBlock().getWorld().getName().equals("world")) return;
        e.setCancelled(true);
    }  
        
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockGrowth(final BlockGrowEvent e) { 
        if (!e.getBlock().getWorld().getName().equals("world")) return;
        e.setCancelled(true);
    }    

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onStructureGrow(final StructureGrowEvent e) { 
        if (!e.getLocation().getWorld().getName().equals("world")) return;
        e.setCancelled(true);
    }    




















    
    
    //------------- ЭЛИТРЫ ------------------
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLaunch(final ProjectileLaunchEvent e) { //PlayerElytraBoostEvent !!!
            final Projectile prj = e.getEntity();
            if (prj.getShooter() instanceof Player && prj.getType() == EntityType.FIREWORK) {
                Ostrov.sync(()-> ((HumanEntity) prj.getShooter()).getInventory().setItem(2, Main.fw), 8);
            }
    }

    
    
    
    
    
    
    
    
    
    
    
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInter(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        final LobbyPlayer lp = Main.getLobbyPlayer(p);
        
        if (e.getClickedBlock() != null) {
            
            //для элитр
            if (e.hasItem() && e.getItem().getType()==Material.FIREWORK_ROCKET) {
                e.setUseItemInHand(Event.Result.DENY); 
                return;
            }
            
            //копатель
            if (e.hasItem() && e.getItem().getType()==Material.DIAMOND_PICKAXE && e.getAction() == Action.LEFT_CLICK_BLOCK) {
            	final Block b = e.getClickedBlock();
                final Material m = b.getType();
            	switch (m) {
                    case DIAMOND_ORE:
                    case COBBLESTONE:
                        Ostrov.sync(() -> p.sendBlockChange(b.getLocation(), Material.AIR.createBlockData()), 2);
                        Ostrov.sync(() -> p.sendBlockChange(b.getLocation(), m.createBlockData()), 40);
                        QuestManager.tryCompleteQuest(p, Main.getLobbyPlayer(p), m == Material.COBBLESTONE ? Quest.CobbleGen : Quest.MineDiam);
                        p.playSound(b.getLocation(), Sound.BLOCK_NETHER_BRICKS_BREAK, 1, 0.8f);
                        b.getWorld().spawnParticle(Particle.BLOCK_CRACK, b.getLocation().add(0.5d, 0.5d, 0.5d), 40, 0.4d, 0.4d, 0.4d, b.getBlockData());
                        break;
                    default:
                        break;
                }
                return;
            } 
            
            if (lp.questAccept.contains(Quest.FindBlock) && !lp.questDone.contains(Quest.FindBlock) && lp.foundBlocks.add(e.getClickedBlock().getType())) {
                final int sz = lp.foundBlocks.size();
                QuestManager.progressAdv(p, Quest.FindBlock, sz);
                if (sz < 50) {
                    ApiOstrov.sendActionBarDirect(p, "§7Найден блок §e" + Main.nrmlzStr(e.getClickedBlock().getType().toString()) + "§7, осталось: §e" + (50 - sz));
                } else {
                    lp.foundBlocks.clear();
                    QuestManager.tryCompleteQuest(p, lp, Quest.FindBlock);
                    //lp.questDone(p, Quest.FindBlock, true);
                }
                //QuestManager.checkQuest(p, lp, Quest.FindBlock);
            }
            
                        //final HashSet<Material> ms = Main.mts.get(p.getName());
           /* //счётчик блоков для игры аркаим
            if (lp.findBlocks != null && lp.findBlocks.size() < 50) {
                final Material mat = e.getClickedBlock().getType();
                if (lp.findBlocks.add(mat)) {
                        ApiOstrov.sendTitle(p, "", "§7Найден блок §6" + LanguageHelper.getMaterialName(mat, "RU_ru") + "§7, осталось: §6" + (50 - lp.findBlocks.size()));
                        //bossbar???
                }
            }*/
            
            //спавн джина для новичка
            if (e.getAction()==Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType()==Material.SOUL_LANTERN) {
                if (Timer.has(p.getEntityId())) return;
                Timer.add(p.getEntityId(), 3);
            //if (e.getAction()==Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType()==Material.SOUL_LANTERN && p.getVehicle()==null ) {
                final LCuboid lc = AreaManager.getCuboid(p.getLocation());
                if (lc!=null && lc.getName().equals("newbie")) {
                    if (e.getClickedBlock().getX()==Main.getLocation(LocType.ginLampShip).getBlockX() && e.getClickedBlock().getZ()==Main.getLocation(LocType.ginLampShip).getBlockZ()) {
                        p.performCommand("oscom gin");
                    } else {
                        p.sendMessage("§3Должно быть, другая лампа!");
                    }
                    
                }
            }
           
        }
        
        
        if (e.getAction() == Action.PHYSICAL) {
            final Location loc = e.getClickedBlock().getLocation();

            final ChunkContent cc = AreaManager.getChunkContent(loc);
            if (cc!=null && cc.hasPlate()) {
                final XYZ second = cc.getPlate(loc);
                if (second!=null) { //пункт назначения назначен - значит плата есть
                    final LCuboid sCub = AreaManager.getCuboid(second); //кубоид назначения брать после проверки second на null!!!
                    if (sCub == null || lp.isAreaDiscovered(sCub.id)) {  //в точке назначения нет кубоида, или территория уже изучена
                		//e.getPlayer().sendMessage("§aПлита на коорд. -> "+second.toString());
                        loc.getWorld().spawnParticle(Particle.SOUL, loc, 40, 0.6d, 0.6d, 0.6d, 0d, null, false);
                        loc.getWorld().playSound(loc, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1f, 1f);
                        loc.getWorld().playSound(loc, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 0.8f);
                        final GameMode gm = p.getGameMode();
                        p.setGameMode(GameMode.SPECTATOR);
                        //p.setFlying(true);
                            
                        new BukkitRunnable() {
                            final String name = p.getName();
                            int count;

                            @Override
                            public void run() {
                                
                                final Player p = Bukkit.getPlayerExact(name);
                                if (count==100 || p==null || !p.isOnline()) {
                                    this.cancel();
                                    return;
                                }
                                
                                final Location loc = p.getLocation();
                               
                                if (Math.abs(loc.getBlockX() - second.x) < 2 && loc.getBlockY() == second.y && Math.abs(loc.getBlockZ() - second.z) < 2) {
                                    loc.getWorld().spawnParticle(Particle.SOUL, loc, 40, 0.6d, 0.6d, 0.6d, 0d, null, false);
                                    loc.getWorld().playSound(loc, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 2f);
                                    p.setGameMode(gm);
                                    p.setFlying(false);
                                    p.setVelocity(new Vector(0, 0, 0));
                                    //p.setFlying(false);
                                    this.cancel();
                                } else {
                                    p.setVelocity(new Vector(second.x + 0.5d, second.y + 0.5d, second.z + 0.5d).subtract(loc.toVector()).multiply(0.1f));
                                    loc.getWorld().spawnParticle(Particle.NAUTILUS, loc, 40, 0.2d, 0.2d, 0.2d);
                                }
                                count++;
                            }
                        }.runTaskTimer(Main.instance, 3, 3);
                        return;
                    } else {
                        ApiOstrov.sendActionBarDirect(p, "§cСначала разведайте пункт назначения!");
                        return;
                    }
             /*   final BaseBlockPosition lp = PlateManager.plts.get(new BaseBlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                if (lp != null) {
                        e.setCancelled(true);
                        loc.getWorld().spawnParticle(Particle.SOUL, loc, 40, 0.6d, 0.6d, 0.6d, 0d, null, false);
                        loc.getWorld().playSound(loc, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1f, 1f);
                        loc.getWorld().playSound(loc, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 0.8f);
                        p.setGameMode(GameMode.SPECTATOR);
                        p.setFlying(true);
                        PlateManager.tps.put(p, lp);
                }*/                
                }
            }
            
            if (e.getClickedBlock().getType() == Material.WARPED_PRESSURE_PLATE && lp.pkrist == null) { //новый паркурист
                final PKrist pr = new PKrist(p);

                    pr.bLast = new XYZ(loc.add(0d, 30d, 0d));
                    final Block b = loc.getBlock();
                    b.setType(Material.LIME_CONCRETE, false);
                    final BlockFace sd = PKrist.sds[Ostrov.random.nextInt(4)];
                    final Block n = ApiOstrov.randBoolean() ? 
                            b.getRelative(sd, 2).getRelative(sd.getModZ() == 0 ? (ApiOstrov.randBoolean() ? BlockFace.NORTH : BlockFace.SOUTH) : (ApiOstrov.randBoolean() ? BlockFace.WEST : BlockFace.EAST)) 
                            : 
                            b.getRelative(sd, 2).getRelative(BlockFace.UP);
                    n.setType(Material.LIME_CONCRETE, false);
                    pr.bNext = new XYZ(n.getLocation());
                    p.teleport(loc.add(0.5d, 1.1d, 0.5d));
                    Ostrov.sync(() -> {
                        p.playSound(p.getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_PLACE, 2f, 1.4f);
                        lp.pkrist = pr;
                    }, 4);
                //Main.miniParks.add(pr);
            }
        }
    }
    // ---------------------------------------- 

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	public boolean plcAtmpt(Block b, final BlockFace bf) {
		byte i = 0;
		while (b.getRelative(bf.getOppositeFace()).getType().isAir() && i < 10) {
			i++;
			b = b.getRelative(bf.getOppositeFace());
		}

		byte h = 1;
		byte w = 1;

		hh : while (h < 20) {
			switch (b.getRelative(BlockFace.UP, h).getType()) {
			case AIR:
			case CAVE_AIR:
			case FIRE:
				h++;
				break;
			default:
				break hh;
			}
		}

		ww : while (w < 20) {
			switch (b.getRelative(bf, w).getType()) {
			case AIR:
			case CAVE_AIR:
			case FIRE:
				w++;
				break;
			default:
				break ww;
			}
		}
		
		if (bf.getModX() == 0) {
			if (!ntAirMinBlck(b.getRelative(0, 0, 0), (byte) 2) || 
				!ntAirMinBlck(b.getRelative(0, h - 1, 0), (byte) 2) || 
				!ntAirMinBlck(b.getRelative(0, 0, w - 1), (byte) 2) || 
				!ntAirMinBlck(b.getRelative(0, h - 1, w - 1), (byte) 2)) {
				return false;
			}
			for (byte y = 1; y < h - 1; y++) {
				if (!ntAirMinBlck(b.getRelative(0, y, 0), (byte) 1) || 
					!ntAirMinBlck(b.getRelative(0, y, w - 1), (byte) 1)) {
					return false;
				}
			}
			for (byte xz = 1; xz < w - 1; xz++) {
				if (!ntAirMinBlck(b.getRelative(0, 0, xz), (byte) 1) || 
					!ntAirMinBlck(b.getRelative(0, h - 1, xz), (byte) 1)) {
					return false;
				}
			}
			if (w < 2 || h < 3) {
				return false;
			}
			final Block[] pbs = new Block[w * h];
			short j = 0;
			for (byte xz = 0; xz < w; xz++) {
				for (byte y = 0; y < h; y++) {
					final Block bl = b.getRelative(0, y, xz);
					switch (bl.getType()) {
					case AIR:
					case CAVE_AIR:
					case FIRE:
						pbs[j] = bl;
						j++;
						break;
					default:
						return false;
					}
				}
			}
			
			for (final Block pb : pbs) {
				pb.setType(Material.NETHER_PORTAL);
				final Orientable or = (Orientable) pb.getBlockData();
				or.setAxis(Axis.Z);
				pb.setBlockData(or);
			}
		} else {
			if (!ntAirMinBlck(b.getRelative(0, 0, 0), (byte) 3) || 
				!ntAirMinBlck(b.getRelative(0, h - 1, 0), (byte) 3) || 
				!ntAirMinBlck(b.getRelative(w - 1, 0, 0), (byte) 3) || 
				!ntAirMinBlck(b.getRelative(w - 1, h - 1, 0), (byte) 3)) {
				return false;
			}
			for (byte y = 1; y < h - 1; y++) {
				if (!ntAirMinBlck(b.getRelative(0, y, 0), (byte) 2) || 
					!ntAirMinBlck(b.getRelative(w - 1, y, 0), (byte) 2)) {
					return false;
				}
			}
			for (byte xz = 1; xz < w - 1; xz++) {
				if (!ntAirMinBlck(b.getRelative(xz, 0, 0), (byte) 2) || 
					!ntAirMinBlck(b.getRelative(xz, h - 1, 0), (byte) 2)) {
					return false;
				}
			}
			if (w < 2 || h < 3) {
				return false;
			}
			final Block[] pbs = new Block[w * h];
			short j = 0;
			for (byte xz = 0; xz < w; xz++) {
				for (byte y = 0; y < h; y++) {
					final Block bl = b.getRelative(xz, y, 0);
					switch (bl.getType()) {
					case AIR:
					case CAVE_AIR:
					case FIRE:
						pbs[j] = bl;
						j++;
						break;
					default:
						return false;
					}
				}
			}
			
			for (final Block pb : pbs) {
				pb.setType(Material.NETHER_PORTAL);
			}
		}
		return true;
	}

	public boolean ntAirMinBlck(final Block b, byte amt) {
		for (final BlockFace bf : nr) {
			if (!b.getRelative(bf).getType().isAir()) {
				amt--;
			}
		}
		return amt <= 0;
	}
        
        
        
  
}
