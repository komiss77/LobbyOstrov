package ru.ostrov77.lobby;


import java.util.HashMap;
import java.util.Map;
import lbb.Romindous.Rom;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import ru.komiss77.ApiOstrov;
import ru.komiss77.LocalDB;
import ru.komiss77.Ostrov;
import ru.komiss77.OstrovDB;
import ru.komiss77.modules.menuItem.MenuItem;
import ru.komiss77.modules.menuItem.MenuItemBuilder;
import ru.komiss77.utils.ItemBuilder;



public class Main extends JavaPlugin {
    
    public static Main instance;
    protected static Location newBieSpawnLocation;
    protected static Location spawnLocation;


    
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
        //lobbyPlayers = new HashMap<>();
        
        newBieSpawnLocation = new Location(world, 30.5, 160, 50.5, 0, 0);
        spawnLocation = new Location(world, .5, 100, .5, 0, 0);
        
        getServer().getPluginManager().registerEvents(new ListenerOne(), instance);
        getServer().getPluginManager().registerEvents(new NewBie(), instance);
        
        Rom.onEnable(this);
        
        final ItemStack is=new ItemBuilder(Material.ELYTRA)
            .setName("§bКрылья Островитянина")
            .setUnbreakable(true)
            .lore("§7Используйте для")
            .lore("§7перемещения по §eОстрову§7!")
            .build();
        new MenuItemBuilder("elytra", is)
            .slot(38) //Chestplate
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .add();
        

        final ItemStack pip=new ItemBuilder(Material.CLOCK)
            .setName(" §6ПКМ§e-профиль §2ЛКМ§a-сервера")
            .setUnbreakable(true)
            .unsaveEnchantment(Enchantment.LUCK, 1)
            .build();
        new MenuItemBuilder("pipboy", pip)
            .slot(8)
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .duplicate(false)
            .leftClickCmd("serv")
            .rightClickCmd("menu")
            .add();

        final ItemStack cosmetic=new ItemBuilder(Material.ENDER_CHEST)
            .setName("Индивидуальность")
            .build();
        new MenuItemBuilder("cosmetic", cosmetic)
            .slot(4)
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .duplicate(false)
            .rightClickCmd("procosmetics open main")
            .leftClickCmd("procosmetics unequipall")
            .add();


        instance.getCommand("oscom").setExecutor(new OsComCmd());
        Ostrov.log_ok("OsComCmd загружен");

    }

 
    
    public static LobbyPlayer getLobbyPlayer(final Player p) {
        return ListenerOne.lobbyPlayers.get(p.getName());
    }

    
    
    
    
    
    
 
        
}





























