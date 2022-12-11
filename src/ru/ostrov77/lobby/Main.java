package ru.ostrov77.lobby;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.kyori.adventure.text.Component;
import net.minecraft.server.dedicated.DedicatedServer;
import ru.komiss77.Ostrov;
import ru.komiss77.modules.menuItem.MenuItem;
import ru.komiss77.modules.menuItem.MenuItemBuilder;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.OstrovConfig;
import ru.komiss77.utils.OstrovConfigManager;
import ru.ostrov77.lobby.area.AreaCmd;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.bots.BotManager;
import ru.ostrov77.lobby.listeners.CosmeticListener;
import ru.ostrov77.lobby.listeners.ListenerWorld;
import ru.ostrov77.lobby.quest.Quest;
import ru.ostrov77.lobby.quest.AdvanceCrazy;
import ru.ostrov77.lobby.quest.QuestManager;
import ru.komiss77.modules.world.XYZ;
import ru.ostrov77.lobby.listeners.FigureListener;
import ru.ostrov77.lobby.listeners.InteractListener;
import ru.ostrov77.lobby.quest.IAdvance;
import ru.ostrov77.lobby.quest.AdvanceVanila;

    
    
    /*
    CREATE TABLE `lobbyData` (
  `name` varchar(16) NOT NULL,
  `openedArea` int(11) NOT NULL DEFAULT '0',
  `questDone` varchar(128) NOT NULL DEFAULT '',
  `questAccept` varchar(128) NOT NULL DEFAULT '',
  `flags` int(11) NOT NULL DEFAULT '0',
  `logoutLoc` varchar(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
ALTER TABLE `lobbyData`
  ADD PRIMARY KEY (`name`);
COMMIT;
    */

public class Main extends JavaPlugin {
    
    public static Main instance;

    public static RealTime timeManager;
    public static BotManager botManager;
    public static AreaManager areaManager;
    public static QuestManager questManager;
    public static OstrovConfigManager configManager;
    
    public static Random rnd = new Random();
    
    public static boolean langUtils = false;
    public static IAdvance advance;
    public static boolean holo = false;
    
    public static final Map<String,LobbyPlayer>lobbyPlayers = new HashMap<>();
    private static final EnumMap<LocType,Location>locations = new EnumMap<LocType, Location>(LocType.class);
    
    private static OstrovConfig serverPortalsConfig;
    public static DedicatedServer ds;
    
    public static final HashMap<XYZ, String> serverPortals = new HashMap<>();//порталы по типу точка портала : сервер

    
    


    
    @Override
    public void onEnable() {

        instance = this;
        configManager = new OstrovConfigManager(this);
        
        final World world = Bukkit.getWorld("world");
        if (world==null) {
            Ostrov.log_err("LobbyOstrov - world недоступен! офф..");
            Bukkit.shutdown();
            return;
        }

        serverPortalsConfig = configManager.getNewConfig("serverPortals.yml");
        
        try {
            ds = (DedicatedServer) getServer().getClass().getMethod("getServer").invoke(getServer());
        } catch (IllegalAccessException|InvocationTargetException|NoSuchMethodException e) {
            e.printStackTrace();
        }
        
        timeManager = new RealTime();
        botManager = new BotManager();
        areaManager = new AreaManager();
        questManager = new QuestManager();
        
        new BukkitRunnable() {
                @Override
                public void run() {
                    world.setTime(timeManager.getMCTime());
                }
        }.runTaskTimer(instance, 40, 500);
        
        
        getServer().getPluginManager().registerEvents(new ListenerWorld(), instance);
        getServer().getPluginManager().registerEvents(new QuestManager(), instance);
        getServer().getPluginManager().registerEvents(new FigureListener(), instance);
        getServer().getPluginManager().registerEvents(new InteractListener(), instance);
        
        if (Bukkit.getPluginManager().getPlugin("ProCosmetics")!=null) {
            getServer().getPluginManager().registerEvents(new CosmeticListener(), instance);
            //cosmetics = true;
        }
        //подгрузка ачивок. После AreaManager!!
        if (Bukkit.getPluginManager().getPlugin("CrazyAdvancementsAPI")!=null) {
            advance =  new AdvanceCrazy();
        } else {
            advance =  new AdvanceVanila();
        }
        
        langUtils = Bukkit.getPluginManager().getPlugin("LangUtils")!=null;
        holo = Bukkit.getPluginManager().getPlugin("HolographicDisplays")!=null;
       
        createMenuItems();

        instance.getCommand("oscom").setExecutor(new OsComCmd());
        instance.getCommand("area").setExecutor(new AreaCmd());
        
        
        loadCfgs();
        loadLocaions(world);
		
        Ostrov.log_ok("Lobby загружен");

    }
    
    @Override
    public void onDisable() {
		for (final Player pl : getServer().getOnlinePlayers()) {
			BotManager.removePlayer(pl);
		}
    	BotManager.clearBots();
    }
    
    
    public static void arriveNewBie(final Player p) {
        
        
        if (p.getVehicle()!=null) {
            final Entity gin = p.getVehicle();
            gin.customName(Component.text("§cРаб лампы")); //!! сначала сменит имя, или сработает onDismount cancel!!
            p.getVehicle().eject();
            ((LivingEntity)gin).setAI(false);
//p.sendMessage("loc="+gin.getLocation()); 13.8 102.72 24.8
            gin.teleport(getLocation(LocType.ginFinal));
            showGinHopper(getLocation(LocType.ginLampArrive).clone(), true); //партиклами воронка, уходящая в лампу
            //gin.setVelocity(new Vector(0, -0.5, 0)); //всасывание джина в лампу 
            //gin.setGravity(true);
            
            p.getWorld().playSound(getLocation(LocType.ginLampArrive), Sound.BLOCK_CONDUIT_DEACTIVATE, 5, .3f);

            Ostrov.sync( ()-> {
                if (!gin.isDead()) {
                    gin.getWorld().playSound(getLocation(LocType.ginLampArrive), Sound.BLOCK_BEEHIVE_EXIT, 5, .5f);
                    gin.remove();
                }
            }, 100);
//p.sendMessage("§8log: прибыли на джине ginTicks="+gin.getTicksLived());

        } else {
            p.teleport (getLocation(LocType.newBieArrive), PlayerTeleportEvent.TeleportCause.COMMAND);
//p.sendMessage("§8log: прибыли своим ходом");
            
        }
        //эффект, музыка 
        //DonatEffect.spawnRandomFirework(p.getLocation());
    }

    
    
    protected static void showGinHopper(final Location loc, final boolean in) {
        new BukkitRunnable() {
            double radius = in ? 2.043476540885901 : 0.1; //нисходящая спираль
            double y = in ? 4 : 0; //нисходящая спираль
            @Override
            public void run() {

                for (int t= 0; t <= 40; t++) {
                    y= in ? y-0.002 : y+0.002;
                    radius= in ? radius/1.0015 : radius*1.0015;
                    double x = radius * Math.cos(Math.pow(y, 2)*10);
                    double z = radius * Math.sin(Math.pow(y, 2)*10);
                    loc.add(x,y,z);
                    loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc, 1, 0, 0, 0);
                    loc.subtract(x,y,z);
                }
                if ( (in && y<=0) || y>=4) {
                    this.cancel();
                }           
            }
        }.runTaskTimerAsynchronously(Ostrov.instance, 0, 1);
    }





    
    
    
    
    
    
    
    
    
    
    
    public static void savePortals() {
        final List<String>list = new ArrayList<>();
        for ( Map.Entry<XYZ, String> entry : Main.serverPortals.entrySet()) {
            list.add(entry.getKey().toString()+","+entry.getValue());

        }
        Main.serverPortalsConfig.set("portalData", list);
        Main.serverPortalsConfig.saveConfig();   
    }  
    
    
    public static void loadCfgs() {
        serverPortals.clear();
        if (serverPortalsConfig.getStringList("portalData")!=null) {
            for (final String portalData : serverPortalsConfig.getStringList("portalData")) {
                final XYZ xyz = XYZ.fromString(portalData);
                if (xyz!=null) {
//System.out.println("=== portalData="+portalData+" serv=>"+portalData.replace(xyz.toString(), "")+"<");
                    serverPortals.put(xyz, portalData.substring(portalData.lastIndexOf(",")+1));
                }
            }
        }
    }
    
    
    
    
    
    
    //public static void addLobbyPlayer(final LobbyPlayer lp) {
        //final LobbyPlayer lp = new LobbyPlayer(p.getName());
        //lobbyPlayers.put(lp.name, lp);
        //return lp;
    //}
    
    public static LobbyPlayer getLobbyPlayer(final Player p) {
        return lobbyPlayers.get(p.getName());
    }  
    
    public static LobbyPlayer getLobbyPlayer(final String name) {
        return lobbyPlayers.get(name);
    }
    
    //public static LobbyPlayer destroyLobbyPlayer(final String name) {
    //    return lobbyPlayers.remove(name);
    //}
    
    public static Iterable<LobbyPlayer> getLobbyPlayers() {
        return lobbyPlayers.values();
    }

    
    
    

    public static void giveItems(final Player p) {
        p.getInventory().clear();
        final LobbyPlayer lp = Main.getLobbyPlayer(p);
        if (lp==null) return;
        if (lp.hasFlag(LobbyFlag.Elytra)) {
            p.getInventory().setItem(2, fw); //2
            elytra.giveForce(p);//ApiOstrov.getMenuItemManager().giveItem(p, "elytra"); //38
        }
        
        final LCuboid lc = AreaManager.getCuboid(p.getLocation());
        if (lc != null && lc.getName().equals("daaria") && lc.getName().equals("skyworld")) {
            pickaxe.giveForce(p);
        }
        //ProCosmeticsAPI.giveCosmeticMenu(p);
        oscom.giveForce(p);
        if (lp.questDone.contains(Quest.PandoraLuck)) {
            cosmeticMenu.giveForce(p);// ApiOstrov.getMenuItemManager().giveItem(p, "cosmetic"); //4
        }
        if (lp.questDone.contains(Quest.DiscoverAllArea)) {
            pipboy.giveForce(p);//ApiOstrov.getMenuItemManager().giveItem(p, "pipboy"); //8
        }
        p.updateInventory();
        PM.getOplayer(p).showScore();
    }
    
    public static final ItemStack air = new ItemStack(Material.AIR);
    
    public static final ItemStack fw = mkFwrk (new ItemBuilder(Material.FIREWORK_ROCKET)
        .setName("§7Топливо для §bКрыльев")
        .lore("§7Осторожно,")
        .lore("§7иногда взрывается!")
        .build()
    );

    private static ItemStack mkFwrk(final ItemStack fw) {
        final FireworkMeta fm = (FireworkMeta) fw.getItemMeta();
        final FireworkEffect fc = FireworkEffect.builder().withColor(Color.TEAL).withFade(Color.ORANGE).with(FireworkEffect.Type.BURST).build();
        final FireworkEffect fl = FireworkEffect.builder().withColor(Color.LIME).withFade(Color.YELLOW).with(FireworkEffect.Type.BURST).build();
        fm.addEffects(fc,fc,fc,fc,fl,fl,fl);
        fw.setItemMeta(fm);
        return fw;
    }
    
    
    
    
    /*public static boolean chckAKTsk(final Player p) {
		final LobbyPlayer lp = Main.getLobbyPlayer(p);
		if (lp != null && lp.questAccept.contains(Quest.FindBlock)) {
			final HashSet<Material> ms = mts.get(p.getName());
			if (ms == null) {
				p.sendMessage("§9[§eНПС§9] §fЗдравствуй, будующий §eстроитель§f!");
				Ostrov.sync(new Runnable() {
					@Override
					public void run() {
						p.sendMessage("§fПодо мной находиться портал на §e§lАркаим§f,\n§fкреатив-сервер, ограниченный лишь твоей фантазией!");
					}
				}, 20);
				Ostrov.sync(new Runnable() {
					@Override
					public void run() {
						p.sendMessage("§fОднако, перед разблокировкой §dмгновенного перемещения §fтуда, тебе нужно изучить блоки в этом лобби!\n§6[§fНайди §e50 §fразных §eблоков §fв этом лобби§6]");
						//bossbar???
						//mts.put(p.getName(), new HashSet<Material>());
					}
				}, 80);
			} else if (lp.findBlocks.size() > 50) {
				p.sendMessage("§9[§eНПС§9] §fМолодец, тебе удалось найти различные §eблоки §fв этом лобби! Теперь ты можешь §dмгновенно §fперемещатся на §e§lАркаим§f!");
				lp.questDone(LobbyFlag.Arcaim, true);
				mts.remove(p.getName());
			} else {
				p.sendMessage("§9[§eНПС§9] §fОсталось найти всего §e" + (50 - lp.findBlocks.size()) + " §fблок(ов)!");
			}
		}
		return false;
	}*/

    public static MenuItem oscom;
    public static MenuItem pipboy;
    public static MenuItem cosmeticMenu;
    public static MenuItem elytra;
    public static MenuItem pickaxe;
    public static MenuItem stick;
    
    private void createMenuItems() {
        final ItemStack is=new ItemBuilder(Material.ELYTRA)
            .setName("§bКрылья Островитянина")
            .setUnbreakable(true)
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
            .setName(" §6ЛКМ§e-профиль §2ПКМ§a-сервера")
            .setUnbreakable(true)
            .unsaveEnchantment(Enchantment.LUCK, 1)
            .build();
        pipboy = new MenuItemBuilder("pipboy", pip)
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
            .create();
        
        
        final ItemStack newbie=new ItemBuilder(Material.COMPASS)
            .setName("§3ОСКом")
            .lore("§6ЛКМ§e - задачи")
            .lore("§2ПКМ§a - локации")
            .setUnbreakable(true)
            .addFlags(ItemFlag.HIDE_UNBREAKABLE)
            .addFlags(ItemFlag.HIDE_ENCHANTS)
            //.addFlags(ItemFlag.HIDE_ATTRIBUTES)
            //.unsaveEnchantment(Enchantment.LUCK, 1)
            .build();
        oscom = new MenuItemBuilder("oscom", newbie)
            .slot(0)
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
            .rightShiftClickCmd("menu")
            .leftShiftClickCmd("serv")
            .create();

        
        final ItemStack cosmetic=new ItemBuilder(Material.ENDER_CHEST)
            .setName("§aИндивидуальность")
            .lore("§7Для Игроманов - всё и сразу!")
            .build();
        cosmeticMenu = new MenuItemBuilder("cosmetic", cosmetic)
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
            .create();
        
        final ItemStack pckx = new ItemBuilder(Material.DIAMOND_PICKAXE)
            .setName("§bРазрушитель 3000")
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
                .setName("§6Заряженый Жезл")
                .lore("§7Враги улетят в след. измерение!")
                .addEnchantment(Enchantment.KNOCKBACK, 1)
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
    //делает "String Text" из "STRING_TEXT"
	/*public static String nrmlzStr(final String s) {
		final char[] ss = s.toLowerCase().toCharArray();
		ss[0] = (char) (ss[0] & 0x5f);
		for (byte i = (byte) (ss.length - 1); i > 0; i--) {
			if (ss[i] == '_') {
				ss[i] = ' ';
				ss[i + 1] = (char) (ss[i + 1] & 0x5f);
			}
		}
		return String.valueOf(ss);
	}*/

    private static void loadLocaions(final World world) {
        locations.put(LocType.Spawn,  new Location(world, 0.5, 100.5, 0.5, 0, 0));
        locations.put(LocType.newBieSpawn,  new Location(world, 38.5, 160.5, -79.5, -90, 0));
        locations.put(LocType.newBieArrive,  new Location(world, 16.5, 100.5, 25.5, 150, 0));
        locations.put(LocType.ginFinal,  new Location(world, 13.5, 102, 26.5));
        locations.put(LocType.ginLampShip,  new Location(world, 32.5, 162.5, -79.5));
        locations.put(LocType.ginLampArrive,  new Location(world, 13.5, 100, 26.5));
    }
    
    public static Location getLocation(final LocType type) {
        return locations.get(type);
    }
        
    public enum LocType {
        Spawn, newBieSpawn, newBieArrive, ginFinal, ginLampShip, ginLampArrive;
    }

}





























