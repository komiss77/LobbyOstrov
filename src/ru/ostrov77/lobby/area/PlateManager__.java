package ru.ostrov77.lobby.area;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import net.minecraft.core.BaseBlockPosition;
import ru.ostrov77.lobby.Main;
/*
public class PlateManager {
	
    public static BukkitTask playerPlateTask;
    //public static final HashMap<BaseBlockPosition, BaseBlockPosition> plts = new HashMap<BaseBlockPosition, BaseBlockPosition>();//плиты по типу точка начала : точка конца
    public static final HashMap<Player, BaseBlockPosition> tps = new HashMap<Player, BaseBlockPosition>();//плиты по типу игрок : точка прибытия
    
	public static boolean strtPlts() {
		playerPlateTask = new BukkitRunnable() {
			
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
		}.runTaskTimer(Main.instance, 3, 3);
		return true;
	}
}
*/