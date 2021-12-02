package ru.ostrov77.lobby;


import java.util.HashMap;
import java.util.Map;
import lbb.Romindous.Rom;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import ru.komiss77.LocalDB;
import ru.komiss77.Ostrov;
import ru.komiss77.OstrovDB;



public class Main extends JavaPlugin {
    
    public static Main instance;
    protected static Location newBieSpawnLocation;
    protected static Location spawnLocation;
    protected static Map<String,LobbyPlayer>lobbyPlayers;


    
    @Override
    public void onEnable() {

        instance = this;
        final World world = Bukkit.getWorld("world");
        if (world==null) {
            Ostrov.log_err("LobbyOstrov - world недоступен! офф..");
            Bukkit.shutdown();
            return;
        }
        if (!LocalDB.useLocalData) {
            Ostrov.log_err("LobbyOstrov - LocalDB.useLocalData выключена! офф..");
            Bukkit.shutdown();
            return;
        }
        if (!OstrovDB.useOstrovData) {
            Ostrov.log_err("LobbyOstrov - OstrovDB.useOstrovData выключена! офф..");
            Bukkit.shutdown();
            return;
        }
        lobbyPlayers = new HashMap<>();
        
        newBieSpawnLocation = new Location(world, 30.5, 160, 50.5, 0, 0);
        spawnLocation = new Location(world, .5, 100, .5, 0, 0);
        
        getServer().getPluginManager().registerEvents(new ListenerOne(), instance);
        
        Rom.onEnable(this);
        
        instance.getCommand("oscom").setExecutor(new OsComCmd());
        Ostrov.log_ok("OsComCmd загружен");

    }

    
    
    
    
    
    
    
    
    
 
        
}





























