package ru.ostrov77.lobby;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import ru.komiss77.Ostrov;



public class Main extends JavaPlugin {
    
    protected static Main instance;
    protected static Location newBieSpawnLocation;

    
    @Override
    public void onEnable() {

        instance = this;
        final World world = Bukkit.getWorld("world");
        if (world==null) {
            Ostrov.log_err("LobbyOstrov - world недоступен! офф..");
            return;
        }
        
        newBieSpawnLocation = new Location(world, 30, 160, 50, 0, 0);
        
        getServer().getPluginManager().registerEvents(new ListenerOne(), instance);
        
        instance.getCommand("oscom").setExecutor(new OsComCmd());
        Ostrov.log_ok("OsComCmd загружен");

    }

 
        
}





























