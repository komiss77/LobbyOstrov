package ru.ostrov77.lobby;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.komiss77.Ostrov;
import ru.komiss77.modules.menuItem.MenuItem;
import ru.komiss77.modules.menuItem.MenuItemBuilder;
import ru.komiss77.modules.player.PM;
import ru.komiss77.modules.quests.QuestManager;
import ru.komiss77.modules.world.XYZ;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.OConfig;
import ru.komiss77.OConfigManager;
import ru.komiss77.utils.TCUtil;
import ru.ostrov77.lobby.area.AreaCmd;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.listeners.CosmeticListener;
import ru.ostrov77.lobby.listeners.FigureListener;
import ru.ostrov77.lobby.listeners.InteractListener;
import ru.ostrov77.lobby.listeners.ListenerWorld;
import ru.ostrov77.lobby.quest.Quests;

import java.util.*;


public class Main extends JavaPlugin {
    
    public static Main instance;
    private static OConfig serverPortalsConfig;
    //public static RealTime timeManager;
    //public static SpotManager botManager;
    public static AreaManager areaManager;
    public static OConfigManager configManager;
    public static Random rnd = new Random();
    //public static boolean holo = false;
    private static final EnumMap<LocType,Location>locations;
    public static final HashMap<XYZ, String> serverPortals;//порталы по типу точка портала : сервер
    

    
    static {
        locations = new EnumMap<>(LocType.class);
        serverPortals = new HashMap<>();
    }



    @Override
    public void onEnable() {

        instance = this;
        configManager = new OConfigManager(this);
        
        //OSTROV
        TCUtil.N = "§7"; TCUtil.P = "§с"; TCUtil.A = "§3";
        PM.setOplayerFun(he -> new LobbyPlayer(he), true);
        QuestManager.setOnCloseTab(p -> QuestManager.complete(p, PM.getOplayer(p), Quests.qmenu));
        Quests.load();
        //--
        
        final World world = Bukkit.getWorld("world");
        if (world==null) {
            Ostrov.log_err("LobbyOstrov - world недоступен! офф..");
            Bukkit.shutdown();
            return;
        }

        serverPortalsConfig = configManager.getNewConfig("serverPortals.yml");
        loadLocaions(world);
        
        //new Quests(); 
        //timeManager = new RealTime();
        //botManager = new SpotManager();
        areaManager = new AreaManager();
        
        world.setStorm((Calendar.getInstance().get(Calendar.DAY_OF_MONTH) & 7) == 0);
        if (world.hasStorm()) world.setThundering(false);
        world.setWeatherDuration(10000000);
        
        new BukkitRunnable() {
                @Override
                public void run() {
                    world.setTime(RealTime.getMCTime());
                }
        }.runTaskTimer(instance, 40, 500);
        
        getServer().getPluginManager().registerEvents(new ListenerWorld(), instance);
        getServer().getPluginManager().registerEvents(new FigureListener(), instance);
        getServer().getPluginManager().registerEvents(new InteractListener(), instance);
        
        if (Bukkit.getPluginManager().getPlugin("ProCosmetics")!=null) {
            getServer().getPluginManager().registerEvents(new CosmeticListener(), instance);
        }
       
        createMenuItems();

        instance.getCommand("oscom").setExecutor(new OsComCmd());
        instance.getCommand("area").setExecutor(new AreaCmd());
        
        loadPortals();
		
        Ostrov.log_ok("Lobby загружен");
    }
    
    
    @Override
    public void onDisable() {
    	
    }
  
    
    
    
    
    
    
    
    public static void savePortals() {
        final List<String>list = new ArrayList<>();
        for ( Map.Entry<XYZ, String> entry : Main.serverPortals.entrySet()) {
            list.add(entry.getKey().toString()+","+entry.getValue());
        }
        Main.serverPortalsConfig.set("portalData", list);
        Main.serverPortalsConfig.saveConfig();   
    }  
    
    
    public static void loadPortals() {
        serverPortals.clear();
        if (serverPortalsConfig.getStringList("portalData")!=null) {
            for (final String portalData : serverPortalsConfig.getStringList("portalData")) {
                final XYZ xyz = XYZ.fromString(portalData);
                if (xyz!=null) {
                    serverPortals.put(xyz, portalData.substring(portalData.lastIndexOf(",")+1));
                }
            }
        }
    }

    public static void giveItems(final Player p) {
        p.getInventory().clear();
        final LobbyPlayer lp = PM.getOplayer(p, LobbyPlayer.class);
//        final boolean justGame = lp.hasSettings(Settings.JustGame);
        
        if (QuestManager.isComplete(lp, Quests.doctor)) {
            p.getInventory().setItem(2, fw); //2
            elytra.giveForce(p);
        }
        
        final LCuboid lc = AreaManager.getCuboid(p.getLocation());
        if (lc != null && lc.getName().equals("daaria") && lc.getName().equals("skyworld")) {
            pickaxe.giveForce(p);
        }

        oscom.giveForce(p);

        if (QuestManager.isComplete(lp, Quests.pandora)) {
            cosmeticMenu.giveForce(p);
        }

        if (lp.firstJoin || QuestManager.isComplete(lp, Quests.discover)) {
            oscom.remove(p);
            pipboy.giveForce(p);
        }
        p.updateInventory();
        PM.getOplayer(p).showScore();
    }

    private static ItemStack mkFwrk(final ItemStack fw) {
        final FireworkMeta fm = (FireworkMeta) fw.getItemMeta();
        final FireworkEffect fc = FireworkEffect.builder().withColor(Color.TEAL).withFade(Color.ORANGE).with(FireworkEffect.Type.BURST).build();
        final FireworkEffect fl = FireworkEffect.builder().withColor(Color.LIME).withFade(Color.YELLOW).with(FireworkEffect.Type.BURST).build();
        fm.addEffects(fc,fc,fc,fc,fl,fl,fl);
        fw.setItemMeta(fm);
        return fw;
    }
    


    public static MenuItem oscom;
    public static MenuItem pipboy;
    public static MenuItem cosmeticMenu;
    public static MenuItem elytra;
    public static MenuItem pickaxe;
    public static MenuItem stick;
    //public static MenuItem rocket;
        public final static ItemStack fw = mkFwrk (new ItemBuilder(Material.FIREWORK_ROCKET)
            .name("§7Топливо для §bКрыльев")
            .flags(ItemFlag.HIDE_ATTRIBUTES)
            .lore("§7Осторожно,")
            .lore("§7иногда взрывается!")
            .build()
        );    
        
    private void createMenuItems() {
        
        final ItemStack is=new ItemBuilder(Material.ELYTRA)
            .name("§bКрылья Островитянина")
            .unbreak(true)
            .lore("§7Используйте для")
            .lore("§7перемещения по §eОстрову§7!")
            .build();
        elytra = new MenuItemBuilder("elytra", is)
            .slot(38) //Chestplate
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .create();
        

        final ItemStack pip=new ItemBuilder(Material.CLOCK)
            .name("§6ЛКМ§e-профиль §2ПКМ§a-сервера")
            .unbreak(true)
            .enchant(Enchantment.LUCK_OF_THE_SEA, 1)
            .build();
        pipboy = new MenuItemBuilder("pipboy", pip)
            .slot(4)
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .duplicate(false)
            .leftClickCmd("oscom area")
            .rightClickCmd("menu")
            .create();
        
        //COMPASS лучше не ставить, FAWE тэпэшит при клике, сбивает с толку!
        final ItemStack newbie=new ItemBuilder(Material.RECOVERY_COMPASS)
            .name("§3ОСКом")
            .lore("§6ЛКМ§e - задачи")
            .lore("§2ПКМ§a - локации")
            .unbreak(true)
            .flags(ItemFlag.HIDE_UNBREAKABLE)
            .flags(ItemFlag.HIDE_ENCHANTS)
            .build();
        oscom = new MenuItemBuilder("oscom", newbie)
            .slot(4)
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .duplicate(false)
            .leftClickCmd("oscom quest")
            .rightClickCmd("oscom area")
            .leftShiftClickCmd("profile")
            .rightShiftClickCmd("menu")
            .create();

        
        final ItemStack cosmetic=new ItemBuilder(Material.ENDER_CHEST)
            .name("§aИндивидуальность")
            .lore("§7Для Игроманов - всё и сразу!")
            .build();
        cosmeticMenu = new MenuItemBuilder("cosmetic", cosmetic)
            .slot(8)
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .duplicate(false)
            .rightClickCmd("cosmetics")
//            .leftClickCmd("oscom unequipCosmetics")
            .create();
        
        final ItemStack pckx = new ItemBuilder(Material.DIAMOND_PICKAXE)
            .name("§bРазрушитель 3000")
            .lore("§7Cносит блоки с одного удара!,")
            .lore("§7(но только §fБулыжник §7и §bАлмазы§7)")
            .build();
        pickaxe = new MenuItemBuilder("pickaxe", pckx)
            .slot(2)
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .duplicate(false)
            .create();
        
        final ItemStack st = new ItemBuilder(Material.BLAZE_ROD)
            .name("§6Заряженый Жезл")
            .lore("§7Враги улетят в след. измерение!")
            .enchant(Enchantment.KNOCKBACK, 1)
            .build();
        stick = new MenuItemBuilder("stick", st)
            .slot(2)
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .duplicate(false)
            .create();
    }


    private static void loadLocaions(final World world) {
        locations.put(LocType.spawn, new Location(world, 0.5, 101.5, 0.5, 180, 0));
        locations.put(LocType.newBieSpawn, new Location(world, 60.5, 160.5, -79.5, 90, 0));
        locations.put(LocType.newBieArrive, new Location(world, 16.5, 100.5, 25.5, 150, 0));
        locations.put(LocType.ginLamp, new Location(world, 52.5, 162.5, -79.5));
        locations.put(LocType.ginArrive, new Location(world, 13.5, 100, 26.5));
        locations.put(LocType.raceLoc, new Location(world, -9.5, 94.5, -66.5));
        locations.put(LocType.sumoLoc, new Location(world, 61.5, 91.5, 54.5));
    }
    
    public static Location getLocation(final LocType type) {
        return locations.get(type);
    }
        
    public enum LocType {
        spawn,
        newBieSpawn,
        newBieArrive,
        ginLamp,
        ginArrive,
        raceLoc,
        sumoLoc,
    }

}