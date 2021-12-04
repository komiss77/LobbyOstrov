package lbb.Romindous;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.minecraft.core.BaseBlockPosition;
import ru.komiss77.Ostrov;
import ru.ostrov77.lobby.LobbyFlag;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;

/*цвета серверов
PK - 7,b
SN - 4,d
MI - c,5
SW - 3,2
DA - a,6
AK - 9,e
PVP - c,6
PVE - e,
*/

public class Rom {
	
	public static final HashMap<BaseBlockPosition, BaseBlockPosition> plts = new HashMap<BaseBlockPosition, BaseBlockPosition>();//плиты по типу точка начала : точка конца
	public static final HashMap<BaseBlockPosition, String> prts = new HashMap<BaseBlockPosition, String>();//порталы по типу точка портала : 
	public static final HashMap<Player, BaseBlockPosition> tps = new HashMap<Player, BaseBlockPosition>();//плиты по типу игрок : точка прибытия
	public static final HashMap<String, HashSet<Material>> mts = new HashMap<String, HashSet<Material>>();//найденые блоки по типу ник : найденые материалы
	public static Plugin instance;
	
	public static void onEnable(final Plugin plug) {
		instance = plug;
		Ostrov.log_ok("§2Lobby вкл.!");
		Bukkit.getServer().getPluginManager().registerEvents(new RomListener(), plug);
		
		loadCfgs();
        
        new BukkitRunnable() {
			@Override
			public void run() {
	            final Iterator<Entry<Player, BaseBlockPosition>> it = tps.entrySet().iterator();
	            while (it.hasNext()) {
	            	final Entry<Player, BaseBlockPosition> en = it.next();
	            	final Player p = en.getKey();
	            	if (p == null) {
	            		it.remove();
	            		continue;
	            	}
	            	final Location loc = p.getLocation();
	            	final BaseBlockPosition lp = en.getValue();
	            	if (Math.abs(loc.getBlockX() - lp.getX()) < 2 && loc.getBlockY() == lp.getY() && Math.abs(loc.getBlockZ() - lp.getZ()) < 2) {
	    				loc.getWorld().spawnParticle(Particle.SOUL, loc, 40, 0.6d, 0.6d, 0.6d, 0d, null, false);
	    				loc.getWorld().playSound(loc, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 2f);
	            		p.setGameMode(GameMode.SURVIVAL);
	            		p.setVelocity(new Vector(0, 0, 0));
	            		p.setFlying(false);
	            		it.remove();
	            	} else {
						p.setVelocity(new Vector(lp.getX() + 0.5d, lp.getY() + 0.5d, lp.getZ() + 0.5d).subtract(loc.toVector()).multiply(0.1f));
						loc.getWorld().spawnParticle(Particle.NAUTILUS, loc, 40, 0.2d, 0.2d, 0.2d);
					}
	            }
			}
		}.runTaskTimer(instance, 3, 3);
	}
	
        
        
	protected static void loadCfgs() {
		prts.clear();
		plts.clear();
		File file = new File(instance.getDataFolder() + File.separator + "config.yml");
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
    				plts.put(new BaseBlockPosition(Integer.parseInt(bxs[i]), Integer.parseInt(bys[i]), Integer.parseInt(bzs[i])), 
    				new BaseBlockPosition(Integer.parseInt(exs[i]), Integer.parseInt(eys[i]), Integer.parseInt(ezs[i])));
    			}
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
	
	/*public static String OCPrfx() {
		return "§6[§eОстров§6] ";
	}*/
}
