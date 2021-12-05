package ru.ostrov77.lobby;


import ru.ostrov77.lobby.area.AreaCmd;
import ru.ostrov77.lobby.newbie.OsComCmd;
import ru.ostrov77.lobby.newbie.NewBie;
import net.minecraft.core.BaseBlockPosition;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
import ru.ostrov77.lobby.area.PlateManager;
import ru.ostrov77.lobby.quest.QuestManager;



public class Main extends JavaPlugin {
    
    public static Main instance;
    public static Location newBieSpawnLocation;
    public static Location spawnLocation;
    public static AreaManager areaManager;
    public static QuestManager questManager;
    
	public static final HashMap<BaseBlockPosition, String> prts = new HashMap<BaseBlockPosition, String>();//порталы по типу точка портала : сервер
	public static final HashMap<String, HashSet<Material>> mts = new HashMap<String, HashSet<Material>>();//найденые блоки по типу ник : найденые материалы
    
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
        /*if (!LocalDB.useLocalData) {
            Ostrov.log_err("LobbyOstrov - LocalDB.useLocalData выключена! офф..");
            Bukkit.shutdown();
            return;
        }
        if (!OstrovDB.useOstrovData) {
            Ostrov.log_err("LobbyOstrov - OstrovDB.useOstrovData выключена! офф..");
            Bukkit.shutdown();
            return;
        }*/
        //lobbyPlayers = new HashMap<>();
        
        newBieSpawnLocation = new Location(world, 30.5, 160, 50.5, 0, 0);
        spawnLocation = new Location(world, .5, 100, .5, 0, 0);
        
        getServer().getPluginManager().registerEvents(new ListenerOne(), instance);
        getServer().getPluginManager().registerEvents(new NewBie(), instance);
        
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
        
        
        loadCfgs();
		
        Ostrov.log_ok("OsComCmd загружен");

    }
    
    
    
    
    protected static void loadCfgs() {
		prts.clear();
		PlateManager.plts.clear();
		File file = new File(instance.getDataFolder() + File.separator + "config.yml");
		//System.out.println("----------------- loadCfgs exist?"+file.exists());
        if (file.exists()) {
    		final FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
    		if (cfg.isConfigurationSection("prtls")) {
    			final String[] xs = cfg.getString("prtls.x").split(":");
    			final String[] ys = cfg.getString("prtls.y").split(":");
    			final String[] zs = cfg.getString("prtls.z").split(":");
    			final String[] ss = cfg.getString("prtls.s").split(":");
    			for (int i = xs.length - 1; i >= 0; i--) {
    				prts.put(new BaseBlockPosition(Integer.parseInt(xs[i]), Integer.parseInt(ys[i]), Integer.parseInt(zs[i])), ss[i]);
    			}
    		}
    		if (cfg.isConfigurationSection("plts")) {
    			final String[] bxs = cfg.getString("plts.bx").split(":");
    			final String[] bys = cfg.getString("plts.by").split(":");
    			final String[] bzs = cfg.getString("plts.bz").split(":");
    			final String[] exs = cfg.getString("plts.ex").split(":");
    			final String[] eys = cfg.getString("plts.ey").split(":");
    			final String[] ezs = cfg.getString("plts.ez").split(":");
    			for (int i = bxs.length - 1; i >= 0; i--) {
    				PlateManager.plts.put(new BaseBlockPosition(Integer.parseInt(bxs[i]), Integer.parseInt(bys[i]), Integer.parseInt(bzs[i])), 
    				new BaseBlockPosition(Integer.parseInt(exs[i]), Integer.parseInt(eys[i]), Integer.parseInt(ezs[i])));
    			}
    			PlateManager.strtPlts();
    		}
        } else {
        	Bukkit.getServer().getConsoleSender().sendMessage("§6Config для Lobby не найден, делаем новый...");
    		instance.getConfig().options().copyDefaults(true);
    		try {
				instance.getConfig().save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
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
    
    public static boolean chckAKTsk(final Player p) {
		final LobbyPlayer lp = Main.getLobbyPlayer(p);
		if (lp != null && !lp.hasFlag(LobbyFlag.Arcaim)) {
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
						mts.put(p.getName(), new HashSet<Material>());
					}
				}, 80);
			} else if (ms.size() > 50) {
				p.sendMessage("§9[§eНПС§9] §fМолодец, тебе удалось найти различные §eблоки §fв этом лобби! Теперь ты можешь §dмгновенно §fперемещатся на §e§lАркаим§f!");
				lp.setFlag(LobbyFlag.Arcaim, true);
				mts.remove(p.getName());
			} else {
				p.sendMessage("§9[§eНПС§9] §fОсталось найти всего §e" + (50 - ms.size()) + " §fблок(ов)!");
			}
		}
		return false;
	}
}





























