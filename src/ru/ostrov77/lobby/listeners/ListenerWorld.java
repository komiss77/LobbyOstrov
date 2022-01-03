package ru.ostrov77.lobby.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Orientable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
import org.spigotmc.event.entity.EntityDismountEvent;
import ru.komiss77.ApiOstrov;
import ru.komiss77.LocalDB;
import ru.komiss77.Ostrov;
import ru.komiss77.Timer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.LocationUtil;
import ru.ostrov77.lobby.LobbyFlag;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.Main.LocType;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.ChunkContent;
import ru.ostrov77.lobby.area.LCuboid;
import ru.komiss77.modules.world.XYZ;
import ru.ostrov77.lobby.event.CuboidEvent;
import ru.ostrov77.lobby.JinGoal;
import ru.ostrov77.lobby.quest.Quest;
import ru.ostrov77.lobby.quest.AdvanceCrazy;
import ru.ostrov77.lobby.quest.QuestManager;





public class ListenerWorld implements Listener {
    
    private static final BlockFace[] nr = {BlockFace.DOWN, BlockFace.UP, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST};

   /* @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onArmor (final ArmorEquipEvent e) {
System.out.println("ArmorEquipEvent");
        if (!e.getPlayer().isSneaking()) e.setCancelled(true);
    }*/

    	
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
    }
    
    
    
   // @EventHandler (priority = EventPriority.MONITOR)
   // public void onBungeeData(final BungeeDataRecieved e) {
       // final Player p = e.getPlayer();
        //final LobbyPlayer lp = Main.getLobbyPlayer(p);
        
   // }   
    
    @EventHandler (priority = EventPriority.MONITOR)
    public void onJoin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        final LobbyPlayer lp = new LobbyPlayer(p.getName());//Main.createLobbyPlayer(p); тут только создаём, или квесты срабаывают при появлении на спавне
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
 
            } catch (SQLException ex) {

                Ostrov.log_err("ListenerOne error  "+lp.name+" -> "+ex.getMessage());

            } finally {
                
                    onDataLoad(p, lp, logoutLoc);

                try{
                    if (rs!=null && !rs.isClosed()) rs.close();
                    if (stmt!=null) stmt.close();
                } catch (SQLException ex) {
                    Ostrov.log_err("ListenerOne close error - "+ex.getMessage());
                }
            }
            
        }, 0);

    }
    
    

    private void onDataLoad(final Player p, final LobbyPlayer lp, final String logoutLocString) {
        Ostrov.sync(()-> {
            Main.lobbyPlayers.put(lp.name, lp); //заносим тут чтобы  квесты не срабаывали при мелькании на спавне
            Main.advance.join(p, lp);//Ostrov.async( ()-> Advance.send(p, lp), 10); //после добавления Lp!!!
                
            if (!lp.hasFlag(LobbyFlag.NewBieDone)) {
                //p.getInventory().clear(); - не надо, инв. не сохраняется, при входе будет пусто
                //lp.setFlag(LobbyFlag.NewBieDone, true); -не ставитть сразу, или не смогут выполнить задание приветствие новичка
                //NewBie.start(p, 0);
                PM.getOplayer(p).hideScore();
                p.teleport(Main.getLocation(LocType.newBieSpawn));// тп на 30 160 50
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 5));
                p.setCollidable(false);
                Main.oscom.giveForce(p); //ApiOstrov.getMenuItemManager().giveItem(p, "newbie");
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
        final LobbyPlayer lp = Main.lobbyPlayers.remove(p.getName());
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
        Main.advance.onQuit(p);
    }    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // ******** эвенты по новичку
    
    @EventHandler (priority = EventPriority.MONITOR)
    public static void onCuboidEvent(final CuboidEvent e) {
        
        if (e.getPrevois()!=null && e.getPrevois().getInfo().hidePlayers ) { //выход из кубоида со скрытием //if (e.getPrevois()!=null && e.getPrevois().getName().equals("newbie") ) {
            final Player p = e.getPlayer();
            for (final Player cp : e.getPrevois().getCuboidPlayers()) {
                if (!cp.getName().equals(p.getName())) {
                    cp.showPlayer(Main.instance, p);
                    p.showPlayer(Main.instance, cp);
//cp.sendMessage("§8log: §c showPlayer "+p.getName());
//p.sendMessage("§8log: §c showPlayer "+cp.getName());                  
                }
            }
        }
        if (e.getCurrent()!=null && e.getCurrent().getInfo().hidePlayers) {//вход в со скрытием //if (e.getCurrent()!=null && e.getCurrent().getName().equals("newbie")) { 
            final Player p = e.getPlayer();
            for (final Player cp : e.getCurrent().getCuboidPlayers()) {
                if (!cp.getName().equals(p.getName())) {
                    cp.hidePlayer(Main.instance, p);
                    p.hidePlayer(Main.instance, cp);
//cp.sendMessage("§8log: §c hidePlayer "+p.getName());
//p.sendMessage("§8log: §c hidePlayer "+cp.getName());              
                }
            }
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
            if (e.getDismounted().isCustomNameVisible() && e.getDismounted().getCustomName().equals(JinGoal.ginName)) {
                e.setCancelled(true);
                if (!Timer.has(e.getEntity().getEntityId())) {
                    e.getEntity().sendMessage("§6Погодите, уже скоро будем на месте!");
                    Timer.add(e.getEntity().getEntityId(), 3);
                }
            }
        }
    }
    
    
    //***************************        
    

    
    
    
    
    
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
                    }
                }
                break;
                
                
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
                    p.sendMessage("§2Вторая плита поставлена на координатах (§7" + loc.getBlockX() + "§2, §7" + loc.getBlockY() + "§2, §7" + loc.getBlockZ() + "§2)!");
                    p.removeMetadata("tp", Main.instance);
                    e.setCancelled(true);
                    p.sendMessage("§2Плита создана!");
                } else {
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
        }
        
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
        
       
        if (e.getEntityType()==EntityType.PLAYER && !Ostrov.isCitizen(e.getEntity()) ) {
            final LivingEntity le = (LivingEntity) e.getEntity();
            switch (e.getCause()) {
                case VOID:
                    e.setDamage(0);
                    final Player p = (Player) e.getEntity();
                    final LobbyPlayer lp = Main.getLobbyPlayer(p);
                    if (lp.hasFlag(LobbyFlag.NewBieDone)) { //старичков кидаем на спавн
                    	final Location loc = p.getLocation();
                    	final LCuboid lc = AreaManager.getCuboid(new XYZ(loc.getWorld().getName(), loc.getBlockX(), 80, loc.getBlockZ()));
                        p.teleport(lc == null ? Main.getLocation(LocType.Spawn) : lc.spawnPoint, PlayerTeleportEvent.TeleportCause.COMMAND);
                        
                        
                    } else { //новичков - если прыгнул за борт - на точку прибытия
                        Main.arriveNewBie(p);
                    }
                    final EntityDamageEvent de = le.getLastDamageCause();
                    if (de instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) de).getDamager().getType() == EntityType.PLAYER) {
                    	final Player dp = (Player) ((EntityDamageByEntityEvent) de).getDamager();
                    	QuestManager.tryCompleteQuest(dp, Main.getLobbyPlayer(dp), Quest.SumoVoid);
                        for (final Player pl : dp.getWorld().getPlayers()) {
                            pl.sendMessage("§7[§cСумо§7] Игрок §a" + dp.getName() + "§7 скинул §c" + p.getName() + "§7 в пустоту!");
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
                case LAVA:	    //лава
                    e.setCancelled(true);
                    return;
                    //break;
                case SUFFOCATION:   //зажатие в стене
                	le.teleport(Main.getLocation(LocType.Spawn));
                	le.sendMessage("§6[§eОстров§6] §eТебя зажала стена и переместила обратно на спавн!");
                	e.setCancelled(false);
                    return;

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
