package lbb.Romindous;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.minecraft.core.BaseBlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import ru.komiss77.Ostrov;

public class Rom {

	private static HashMap<BaseBlockPosition, BaseBlockPosition> plts = new HashMap<BaseBlockPosition, BaseBlockPosition>();
	private static HashMap<BaseBlockPosition, String> prts = new HashMap<BaseBlockPosition, String>();
	private static HashMap<Player, BaseBlockPosition> tps = new HashMap<Player, BaseBlockPosition>();
	private static final BlockFace[] nr = {BlockFace.DOWN, BlockFace.UP, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST};
	private static Plugin instance;
        
        
        
	public static void onEnable(final Plugin plugin) {
		instance = plugin;
		Ostrov.log_ok("§2Lobby вкл.!");
		Bukkit.getServer().getPluginManager().registerEvents(new RomListener(instance), instance);
		
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

        

	
	


}
