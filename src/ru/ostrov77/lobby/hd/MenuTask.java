package ru.ostrov77.lobby.hd;

import java.util.EnumMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import me.filoghost.holographicdisplays.api.beta.Position;
import me.filoghost.holographicdisplays.api.beta.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.util.Vector;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.area.CuboidInfo;


class MenuTask implements Runnable {

    protected BukkitTask task;
    protected int tick;
    protected final Player p;
    protected final String name;
    protected final Position center;
    protected final String worldName;
    protected final double x,y,z;
    protected final EnumMap<CuboidInfo,Hologram> holo;
    
    
    public MenuTask (final Player p, final Position center, final EnumMap<CuboidInfo,Hologram> holo) {
        this.p = p;
        this.holo = holo;
        this.center = center;
        name = p.getName();
        worldName = p.getWorld().getName();
        x = center.getX();
        y = center.getY();
        z = center.getZ();
        
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.instance, this, 1, 10);
        p.playSound(p.getLocation(), Sound.BLOCK_CONDUIT_ACTIVATE, .5f, 2);
    }
    

    
    
    
    
    
    @Override
    public void run() {
        
        //final Player p = Bukkit.getPlayerExact(name);
        if (p==null || !p.isOnline()) {
            cancel();
            return;
        }
        
        if (p.isDead() || p.isSneaking() || isAway()) {
            cancel();
            p.playSound(center.toLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, .5f, 2);
            return;
        }
        
        /*
        x = - sin(yaw)
        z = cos(yaw)
        You'll also need to convert the yaw in degrees to radians (Math#toRadians(double))
        */
        final double angle = Math.toRadians(p.getLocation().getYaw()); //угол влево-вправо
        final Location loc = p.getEyeLocation();
        loc.setY(loc.getY() + 0.25d);
        
        
        if (tick==1) {
            Hologram h;
            for (CuboidInfo ci : holo.keySet()) {
                h = holo.get(ci);
                
                if (ci.canTp) {
                    final Location pos = loc.clone();
                    pos.setPitch(ci.relPitch);
                    pos.setYaw(p.getLocation().getYaw() + ci.relYaw);
                    h.setPosition(pos.add(pos.getDirection().multiply(2.8f)));
            	}
                
                //final Vector direction = h.getPosition().toVector().subtract(p.getEyeLocation().toVector()).normalize();
                //double vx = direction.getX();
                //double vz = direction.getZ();
                
                //double cos = Math.cos(angle);
                //double sin = Math.sin(angle);
//p.sendMessage("§8log: angle="+angle+" sin="+sin+" cos="+cos);
                //holo.get(ci).setPosition(center.add( (ci.h * cos) + (ci.h * sin), ci.v, -(ci.h * sin) + (ci.h * cos) ));
                //holo.get(ci).setPosition(center.add( ci.h * sin, ci.v, ci.h * -cos ));
                //holo.get(ci).setPosition(center.add( ci.h * -cos, ci.v, ci.h * sin ));
            }
        }

        
        
        tick++;

    }
    
    
    
    
    public void cancel() {
        task.cancel();
        
        for (Hologram h : holo.values()) {
            h.delete();
        }
        HD.tasks.remove(name);
        
    }
    
    

    
  

    
    private boolean isAway() {
        return !p.getWorld().getName().equals(worldName) || 
                Math.abs(p.getLocation().getBlockX()-x)>3 ||
                Math.abs(p.getLocation().getBlockY()-y)>3 ||
                Math.abs(p.getLocation().getBlockZ()-z)>3
                ;
    }

    protected boolean isCanceled() {
        return task==null || task.isCancelled();
    }
    
    
    
    
    
    
    
    
    
    
}
