package ru.ostrov77.lobby;


import ru.ostrov77.lobby.area.AreaCmd;
import ru.ostrov77.lobby.newbie.OsComCmd;
import ru.ostrov77.lobby.newbie.NewBie;
import lbb.Romindous.Rom;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import ru.komiss77.ApiOstrov;
import ru.komiss77.LocalDB;
import ru.komiss77.Ostrov;
import ru.komiss77.OstrovDB;
import ru.komiss77.modules.menuItem.MenuItemBuilder;
import ru.komiss77.utils.ItemBuilder;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.quest.QuestManager;



public class Main extends JavaPlugin {
    
    public static Main instance;
    public static Location newBieSpawnLocation;
    public static Location spawnLocation;
    public static AreaManager areaManager;
    public static QuestManager questManager;
    
    protected static final ItemStack fw = mkFwrk (new ItemBuilder(Material.FIREWORK_ROCKET)
                .setName("§7Топливо для §bКрыльев")
                .lore("§7Осторожно,")
                .lore("§7иногда взрывается!")
                .build()
    );

    
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
        areaManager = new AreaManager();
        questManager = new QuestManager();
        
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
            .setName(" §6ЛКМ§e-профиль §2ПКМ§a-сервера")
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
            .rightClickCmd("serv")
            .leftClickCmd("menu")
            .add();

        final ItemStack cosmetic=new ItemBuilder(Material.ENDER_CHEST)
            .setName("§aИндивидуальность")
            .lore("§7Для Игроманов - всё и сразу!")
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
            .rightClickCmd("cosmetics")
            .leftClickCmd("oscom unequipCosmetics")
            .add();


        instance.getCommand("oscom").setExecutor(new OsComCmd());
        instance.getCommand("area").setExecutor(new AreaCmd());
        
        Ostrov.log_ok("OsComCmd загружен");

    }

 
    
    public static LobbyPlayer getLobbyPlayer(final Player p) {
        return ListenerOne.lobbyPlayers.get(p.getName());
    }

    
    
    
    public static void giveItems(final Player p) {
        p.getInventory().clear();
        final LobbyPlayer lp = Main.getLobbyPlayer(p);
        if (lp.hasFlag(LobbyFlag.Elytra)) {
            p.getInventory().setItem(2, fw); //2
            ApiOstrov.getMenuItemManager().giveItem(p, "elytra"); //38
        }
        //ProCosmeticsAPI.giveCosmeticMenu(p);
        ApiOstrov.getMenuItemManager().giveItem(p, "cosmetic"); //4
        ApiOstrov.getMenuItemManager().giveItem(p, "pipboy"); //8
        p.updateInventory();
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





























