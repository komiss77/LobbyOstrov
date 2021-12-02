package ru.ostrov77.lobby;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import ru.komiss77.ApiOstrov;
import ru.komiss77.LocalDB;
import ru.komiss77.Ostrov;
import ru.komiss77.events.BungeeDataRecieved;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.LocationUtil;





public class ListenerOne implements Listener {
    
    
    final ItemStack fw = mkFwrk (new ItemBuilder(Material.FIREWORK_ROCKET)
                .setName("§7Топливо для §bКрыльев")
                .lore("§7Осторожно,")
                .lore("§7иногда взрывается!")
                .build()
    );
    
    
    
    
    @EventHandler (priority = EventPriority.MONITOR)
    public void onJoin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        final LobbyPlayer lp = new LobbyPlayer(p.getName());
        Main.lobbyPlayers.put(p.getName(), lp);
        
        Ostrov.async( () -> {

            Statement stmt = null;
            ResultSet rs = null;

            try {  
                stmt = LocalDB.GetConnection().createStatement(); 

                rs = stmt.executeQuery( "SELECT * FROM `lobbyData` WHERE `name` = '"+lp.name+"' LIMIT 1" );
                
                final Location logoutLoc;
                if (rs.next()) {
                    logoutLoc = LocationUtil.LocFromString(rs.getString("logoutLoc"));
                    lp.flags = rs.getInt("flags");
                } else {
                    logoutLoc = null;
                }
                
                Ostrov.sync(()-> {
                    
                    if (!lp.hasFlag(LobbyFlag.NewBieDone)) {
                        
                        lp.setFlag(LobbyFlag.NewBieDone, true);
                        p.teleport(Main.newBieSpawnLocation);// тп на 30 160 50
                        
                    } else {
                        
                        //очистить инв - не надо, файлы не сохр!
                        //выдать положенные предметы
                        if (lp.hasFlag(LobbyFlag.Elytra)) {
                            p.getInventory().setItem(2, fw);
                        }
                        if (ApiOstrov.teleportSave(p, logoutLoc, false)) {
p.sendMessage("log: тп на точку выхода");
                        } else {
                            p.teleport(Main.spawnLocation);
p.sendMessage("log: точка выхода опасна, тп на спавн");
                        }

                    }
                    
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
    
    
    
    
    @EventHandler (priority = EventPriority.MONITOR)
    public void onBungeeData(final BungeeDataRecieved e) {
        final Player p = e.getPlayer();
        //final LobbyPlayer lp = Main.getLobbyPlayer(p);
        
    }
    
    
    
    @EventHandler (priority = EventPriority.NORMAL)
    public void onQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        final LobbyPlayer lp = Main.lobbyPlayers.remove(p.getName());
        if (lp!=null) {
            lp.logoutLoc = LocationUtil.StringFromLoc(p.getLocation());
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
                Ostrov.sync(()-> ((HumanEntity) prj.getShooter()).getInventory().setItem(2, fw), 8);
            }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInter(final PlayerInteractEvent e) {
        if (e.hasItem() && e.getClickedBlock() != null  && e.getItem().getType()==Material.FIREWORK_ROCKET) {
            e.setUseItemInHand(Event.Result.DENY);
        }
    }
    
    private static ItemStack mkFwrk(final ItemStack fw) {
        final FireworkMeta fm = (FireworkMeta) fw.getItemMeta();
        final FireworkEffect fc = FireworkEffect.builder().withColor(Color.TEAL).withFade(Color.ORANGE).with(FireworkEffect.Type.BURST).build();
        final FireworkEffect fl = FireworkEffect.builder().withColor(Color.LIME).withFade(Color.YELLOW).with(FireworkEffect.Type.BURST).build();
        fm.addEffects(fc,fc,fc,fc,fl,fl,fl);
        fw.setItemMeta(fm);
        return fw;
    }


    
}
