package ru.ostrov77.lobby;

import io.papermc.paper.event.player.AsyncChatEvent;
import ru.ostrov77.lobby.newbie.NewBie;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
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
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import ru.komiss77.ApiOstrov;
import ru.komiss77.LocalDB;
import ru.komiss77.Ostrov;
import ru.komiss77.utils.LocationUtil;





public class ListenerOne implements Listener {
    
    protected static Map<String,LobbyPlayer>lobbyPlayers = new HashMap<>();;
    
    
    
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

            try {  
                stmt = LocalDB.GetConnection().createStatement(); 
                rs = stmt.executeQuery( "SELECT * FROM `lobbyData` WHERE `name` = '"+lp.name+"' LIMIT 1" );
                
                if (rs.next()) {
                    lp.logoutLoc = rs.getString("logoutLoc");
                    lp.flags = rs.getInt("flags");
                }
                
                Ostrov.sync(()-> {
                    onDataLoad(p, lp);
                }, 0);

            } catch (SQLException ex) {

                Ostrov.log_err("ListenerOne error  "+lp.name+" -> "+ex.getMessage());

            } finally {
                try{
                    if (rs!=null && !rs.isClosed()) rs.close();
                    if (stmt!=null) stmt.close();
                } catch (SQLException ex) {
                    Ostrov.log_err("ListenerOne close error - "+ex.getMessage());
                }
            }

        }, 0);
    }
    
    

    private void onDataLoad(Player p, LobbyPlayer lp) {

        if (!lp.hasFlag(LobbyFlag.NewBieDone)) {
            lp.setFlag(LobbyFlag.NewBieDone, true);
            NewBie.start(p, 0);
        } else {
            Main.giveItems(p);
            final Location logoutLoc = LocationUtil.LocFromString(lp.logoutLoc);
            if (logoutLoc !=null && ApiOstrov.teleportSave(p, logoutLoc, false)) {
p.sendMessage("log: тп на точку выхода");
            } else {
                p.teleport(Main.spawnLocation);
p.sendMessage("log: точка выхода опасна, тп на спавн");
            }
        }
    }

    
    
    
    @EventHandler (priority = EventPriority.NORMAL)
    public void onQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        NewBie.stop(p);
        final LobbyPlayer lp = lobbyPlayers.remove(p.getName());
        if (lp!=null) {
            lp.logoutLoc = LocationUtil.StringFromLocWithYawPitch(p.getLocation());
            LobbyPlayer.save(lp);
        }
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
        //else if (!clear_stats) PM.Addbplace(e.getPlayer().getName());
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onBreak(BlockBreakEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("world")) return;
        if (!ApiOstrov.isLocalBuilder(e.getPlayer()) ) e.setCancelled(true);
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
        if (!e.getEntity().getWorld().getName().equals("world")) return;
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
        if (e.hasItem() && e.getClickedBlock() != null  && e.getItem().getType()==Material.FIREWORK_ROCKET) {
            e.setUseItemInHand(Event.Result.DENY);
        }
    }
    // ----------------------------------------   





    
}
