package ru.ostrov77.lobby;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.core.BaseBlockPosition;
import ru.ostrov77.lobby.area.PlateManager;
import ru.ostrov77.lobby.newbie.NewBie;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Orientable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
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

import ru.komiss77.ApiOstrov;
import ru.komiss77.LocalDB;
import ru.komiss77.Ostrov;
import ru.komiss77.Timer;
import ru.komiss77.utils.LocationUtil;
import ru.ostrov77.lobby.quest.Quest;





public class ListenerOne implements Listener {
    
    protected static Map<String,LobbyPlayer>lobbyPlayers = new HashMap<>();
	
	private static final BlockFace[] nr = {BlockFace.DOWN, BlockFace.UP, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST};
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPrtl(final EntityPortalEnterEvent e) {
		if (e.getEntityType() == EntityType.PLAYER && !Main.prts.isEmpty()) {
			final Location loc = e.getLocation();
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
			if (Timer.has(p, "portal")) {
				return;
		    }
		    Timer.add(p, "portal", 5);
			p.performCommand("server " + n);
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
        final LobbyPlayer lp = new LobbyPlayer(p.getName());
        lobbyPlayers.put(p.getName(), lp);
        
        Ostrov.async( () -> {

            Statement stmt = null;
            ResultSet rs = null;
            String logoutLoc = "";
            
            try {  
                stmt = LocalDB.GetConnection().createStatement(); 
                rs = stmt.executeQuery( "SELECT * FROM `lobbyData` WHERE `name` = '"+lp.name+"' LIMIT 1" );
                
                if (rs.next()) {
                    logoutLoc = rs.getString("logoutLoc");
                    lp.flags = rs.getInt("flags");
                    lp.openedArea = rs.getInt("openedArea");
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
        Ostrov.sync(()-> {
            if (!lp.hasFlag(LobbyFlag.NewBieDone)) {
                lp.setFlag(LobbyFlag.NewBieDone, true);
                NewBie.start(p, 0);
            } else {
                Main.giveItems(p);
                final Location logoutLoc = LocationUtil.LocFromString(logoutLocString);
                if (logoutLoc !=null && ApiOstrov.teleportSave(p, logoutLoc, false)) {
    ApiOstrov.sendActionBarDirect(p, "log: тп на точку выхода");
                } else {
                    p.teleport(Main.spawnLocation);
    ApiOstrov.sendActionBarDirect(p, "log: точка выхода опасна, тп на спавн");
                }
            }
        }, 0);        
    }

    
    
    
    @EventHandler (priority = EventPriority.NORMAL)
    public void onQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        NewBie.stop(p);
        final LobbyPlayer lp = lobbyPlayers.remove(p.getName());
        if (lp!=null) {
            final String logoutLoc = LocationUtil.StringFromLocWithYawPitch(p.getLocation());
            //LobbyPlayer.save(lp);
            //LocalDB.executePstAsync(Bukkit.getConsoleSender(), "INSERT INTO `lobbyData` (name,logoutLoc) VALUES "
            //            + "('"+lp.name+"','"+lp.logoutLoc+"') "
             //           + "ON DUPLICATE KEY UPDATE "
             //           + "`logoutLoc`='"+lp.logoutLoc+"'; " );
            LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `lobbyData` SET `logoutLoc` = '"+logoutLoc+"' WHERE `name` = '"+lp.name+"';");

        }
		p.removeMetadata("tp", Main.instance);
    }    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @EventHandler (ignoreCancelled = true)
    public void onPlayerChat(AsyncChatEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("world")) return;
        if (NewBie.hasNewBieTask(e.getPlayer())) {
            
        }
        //e.setCancelled(true);
        //e.viewers().clear();
        //e.getPlayer().sendMessage("§6Для пропуска интро просто перезайдите.");
    }      
    
    
    
    
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onPlace(BlockPlaceEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("world")) return;
        if (!ApiOstrov.isLocalBuilder(e.getPlayer()) ) e.setCancelled(true);
        
		final Block b = e.getBlockPlaced();
		
		if (b.getType() == Material.FIRE) {
			if (plcAtmpt(b, BlockFace.EAST) || plcAtmpt(b, BlockFace.SOUTH)) {
				final ItemStack it = e.getItemInHand();
				if (it != null && it.hasItemMeta()) {
					final String nm = ((TextComponent) it.getItemMeta().displayName()).content();
					final Location loc = b.getLocation();
					Main.prts.put(new BaseBlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), nm);
					final FileConfiguration cfg = Main.instance.getConfig();
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
					}
				}
			}
		} else if (b.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
			final Player p = e.getPlayer();
			final Location loc = b.getLocation();
			if (p.hasMetadata("tp")) {
				final FileConfiguration cfg = Main.instance.getConfig();
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
				}
				
				p.sendMessage("§2Вторая плита поставлена на координатах (§7" + loc.getBlockX() + "§2, §7" + loc.getBlockY() + "§2, §7" + loc.getBlockZ() + "§2)!");
				p.removeMetadata("tp", Main.instance);
				e.setCancelled(true);
				p.sendMessage("§2Плита создана!");
			} else {
				p.setMetadata("tp", new FixedMetadataValue(Main.instance, new BaseBlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())));
				p.sendMessage("§aПервая плита поставлена на координатах (§7" + loc.getBlockX() + "§a, §7" + loc.getBlockY() + "§a, §7" + loc.getBlockZ() + "§a)!");
			}
		} else if (b.getType() == Material.BEDROCK) {
			Main.loadCfgs();
			e.getPlayer().sendMessage("§eПерезагружено!");
		}
        //else if (!clear_stats) PM.Addbplace(e.getPlayer().getName());
    }
    
    
    
    
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onBreak(BlockBreakEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("world")) return;
        if (!ApiOstrov.isLocalBuilder(e.getPlayer()) ) e.setCancelled(true);
		if (e.getBlock().getType() == Material.NETHER_PORTAL && !Main.prts.isEmpty()) {
			final FileConfiguration cfg = Main.instance.getConfig();
			final Location loc = e.getBlock().getLocation();
			int d = Integer.MAX_VALUE;
			BaseBlockPosition rb = new BaseBlockPosition(0, 0, 0);
			for (final Entry<BaseBlockPosition, String> en : Main.prts.entrySet()) {
				final int dd = (int) en.getKey().distanceSquared(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), true);
				if (dd < d) {
					d = dd;
					rb = en.getKey();
				}
			}
			
			//убираем из HashMap
			final String pnm = Main.prts.remove(rb);
		
			//убираем из файла
			if (Main.prts.isEmpty()) {
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
			}
		} else if (e.getBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE && !PlateManager.plts.isEmpty()) {
			final FileConfiguration cfg = Main.instance.getConfig();
			final Location loc = e.getBlock().getLocation();
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
			}
		}
        //else if (!clear_stats) PM.get(e.getPlayer().getName());
    }

    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onHangingBreakByEntityEvent(HangingBreakByEntityEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) return;
        if (  e.getRemover()!=null && e.getRemover().getType()==EntityType.PLAYER && !Ostrov.isCitizen(e.getEntity()) ) {
                if (!ApiOstrov.isLocalBuilder((Player) e.getRemover()) ) e.setCancelled(true);
        } 

    }
       
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onHangingBreakEvent(HangingBreakEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) return;
        if ( e.getEntity().getType()==EntityType.PLAYER) {
                if (!ApiOstrov.isLocalBuilder((Player) e.getEntity()) ) e.setCancelled(true);
        } 
    }
    
    
    
    
    
    
    
    
    
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onPlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("world")) return;
        if( e.getRightClicked().getType() ==EntityType.ARMOR_STAND && !ApiOstrov.isLocalBuilder(e.getPlayer()) ) e.setCancelled(true);
    }


   

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void PlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent e){
        if (!e.getPlayer().getWorld().getName().equals("world")) return;
        if (!ApiOstrov.isLocalBuilder(e.getPlayer())) e.setCancelled(true);
    }        
    
    
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onPlayerRespawn(PlayerRespawnEvent e) {
         if (!e.getPlayer().getWorld().getName().equals("world")) return;
    }
    
    
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void ProjectileHitEvent(final ProjectileHitEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) return;
    }    





    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent e) { 
        if (!e.getEntity().getWorld().getName().equals("world")) return;
        
        if ( e.getEntityType()==EntityType.PLAYER && !Ostrov.isCitizen(e.getEntity()) ) {
                    
            switch (e.getCause()) {
                case VOID:
                    e.setDamage(0);
                    ((Player) e.getEntity()).teleport (Main.spawnLocation, PlayerTeleportEvent.TeleportCause.COMMAND);
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
                case LAVA:
                    e.setCancelled(true);
                    return;
                    //break;
                    
                default:
                    e.setCancelled(true);
                    //return;
            }
        } else {
            e.setCancelled(true);
        }
                

    }






    @EventHandler (ignoreCancelled = true)
    public void onPlayerPickUpItem(EntityPickupItemEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) return;
    }


   // @EventHandler(ignoreCancelled = true)
   // public void onPlayerSwapoffHand(PlayerSwapHandItemsEvent e) {
   //     if (!e.getPlayer().getWorld().getName().equals("world")) return;
   //     e.setCancelled(true);
  //  }
   
  
    @EventHandler  (ignoreCancelled = true)
    public void onHungerChange(FoodLevelChangeEvent e) {
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
    public void onBlockFade(BlockFadeEvent e) {
        if (!e.getBlock().getWorld().getName().equals("world")) return;
        if( e.getBlock().getType() == Material.ICE || e.getBlock().getType() == Material.PACKED_ICE || e.getBlock().getType() == Material.SNOW || e.getBlock().getType() == Material.SNOW_BLOCK) 
        e.setCancelled(true);
    }


    
    
    @EventHandler (ignoreCancelled = true)
    public void onCreatureSpawnEvent(CreatureSpawnEvent e) {
        if (e.getEntity().getWorld().getName().equals("world")) return;
        e.setCancelled(true);
    }
  
    
    @EventHandler (ignoreCancelled = true)
    public void onWeatherChange(WeatherChangeEvent e) {
        if (!e.getWorld().getName().equals("world")) return;
        if (e.toWeatherState()) e.setCancelled(true);
    }


          
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent e) {
        if (!e.getBlock().getWorld().getName().equals("world")) return;
        e.setCancelled(true);
    }  
        
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockGrowth(BlockGrowEvent e) { 
        if (!e.getBlock().getWorld().getName().equals("world")) return;
        e.setCancelled(true);
    }    

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent e) { 
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
        if (e.hasItem() && e.getClickedBlock() != null && e.getItem().getType()==Material.FIREWORK_ROCKET) {
            e.setUseItemInHand(Event.Result.DENY);
        }
        
		final Player p = e.getPlayer();
		if (e.getAction() == Action.PHYSICAL) {
			final Location loc = e.getClickedBlock().getLocation();
			final BaseBlockPosition lp = PlateManager.plts.get(new BaseBlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
			if (lp != null) {
				e.setCancelled(true);
				loc.getWorld().spawnParticle(Particle.SOUL, loc, 40, 0.6d, 0.6d, 0.6d, 0d, null, false);
				loc.getWorld().playSound(loc, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1f, 1f);
				loc.getWorld().playSound(loc, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 0.8f);
				p.setGameMode(GameMode.SPECTATOR);
				p.setFlying(true);
				PlateManager.tps.put(p, lp);
			}
		} else if (e.getClickedBlock() != null) {
			final HashSet<Material> ms = Main.mts.get(p.getName());
			if (ms != null) {
				final Material m = e.getClickedBlock().getType();
				if (ms.size() < 50 && ms.add(m)) {
					ApiOstrov.sendTitle(p, "", "§7Найден блок §6" + m.toString().replace('_', ' ').toLowerCase() + "§7, осталось: §6" + (50 - ms.size()));
					//bossbar???
				}
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
