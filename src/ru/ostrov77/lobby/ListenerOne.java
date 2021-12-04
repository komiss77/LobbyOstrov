package ru.ostrov77.lobby;

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
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.komiss77.ApiOstrov;
import ru.komiss77.LocalDB;
import ru.komiss77.Ostrov;
import ru.komiss77.utils.LocationUtil;





public class ListenerOne implements Listener {
    
    
    
    
    protected static Map<String,LobbyPlayer>lobbyPlayers = new HashMap<>();;
    
    
    
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









    
   // @EventHandler (priority = EventPriority.MONITOR)
   // public void onBungeeData(final BungeeDataRecieved e) {
       // final Player p = e.getPlayer();
        //final LobbyPlayer lp = Main.getLobbyPlayer(p);
        
   // }
    
    
    
    @EventHandler (priority = EventPriority.NORMAL)
    public void onQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        NewBie.stop(p);
        final LobbyPlayer lp = lobbyPlayers.remove(p.getName());
        if (lp!=null) {
            lp.logoutLoc = LocationUtil.StringFromLocWithYawPitch(p.getLocation());
//System.out.println("onQuit logoutLoc="+lp.logoutLoc);
            LobbyPlayer.save(lp);
           /* LocalDB.executePstAsync(Bukkit.getConsoleSender(), "INSERT INTO `lobbyData` (name,logoutLoc,flags) VALUES "
                        + "('"+p.getName()+"','"+LocationUtil.StringFromLoc(p.getLocation())+"','0') "
                        + "ON DUPLICATE KEY UPDATE "
                        + "`logoutLoc`='"+LocationUtil.StringFromLoc(p.getLocation())+"', "
                        + "`flags`='"+lp.flags+"' ;");*/
        }
    }    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
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
    





    
}
