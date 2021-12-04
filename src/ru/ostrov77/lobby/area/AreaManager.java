package ru.ostrov77.lobby.area;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;


public class AreaManager {
    
    
    private static BukkitTask playerMoveTask;
    
    public AreaManager () {
        
        if (playerMoveTask!=null) {
            playerMoveTask.cancel();
        }
        
        playerMoveTask = new BukkitRunnable() {     //   !!!!ASYNC !!!!    каждую секунду
            LobbyPlayer lp;
            @Override
            public void run() {
                
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    //if (!p.getWorld().getName().equals("world")) {
                    //    continue;
                    //}
                    lp = Main.getLobbyPlayer(p);
                }
                
            }
             
        }.runTaskTimerAsynchronously(Main.instance, 20, 20);
        
        
    }
}
