package ru.ostrov77.lobby;

import java.util.EnumSet;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import ru.komiss77.LocalDB;
import ru.komiss77.Timer;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.quests.QuestManager;
import ru.komiss77.modules.world.XYZ;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.CuboidInfo;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.event.CuboidEvent;
import ru.ostrov77.lobby.game.Parkur;
import ru.ostrov77.lobby.quest.Quests;


public class LobbyPlayer extends Oplayer {

	private int flags; //флаги
    public int openedArea; //открытые локации
    
    //служебные
    public int lastCuboidId; //для playerMoveTask
    public int cuboidEntryTime = Timer.getTime(); //при входе равно текущему времени - может сразу появиться в кубоиде
    public int raceTime = -1; //таймер гонки
    public int sumoWins = 0; //сумо киллы
    public final EnumSet<Material> foundBlocks = EnumSet.noneOf(Material.class); //блоки для 50 блок. задания
    public Parkur pkrist;
    public CuboidInfo target = CuboidInfo.DEFAULT; //ИД кубоида цели для компаса
    
//    public boolean toSave = false;
//    public boolean updAdv = false;
    
    public LobbyPlayer(final HumanEntity p) {
		super(p);
	}
    
    
    public boolean isAreaDiscovered(final int areaId) {
        return (openedArea & (1 << areaId)) == (1 << areaId);
    }
    
    public void setAreaDiscovered(final int areaId) {
        openedArea =(openedArea | (1 << areaId));
//        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `lobbyData` SET `openedArea` = '"+openedArea+"' WHERE `name` = '"+nik+"';");
    }
    
    public int getOpenAreaCount () {
        int x = openedArea;
        // Collapsing partial parallel sums method
        // Collapse 32x1 bit counts to 16x2 bit counts, mask 01010101
        x = (x >>> 1 & 0x55555555) + (x & 0x55555555);
        // Collapse 16x2 bit counts to 8x4 bit counts, mask 00110011
        x = (x >>> 2 & 0x33333333) + (x & 0x33333333);
        // Collapse 8x4 bit counts to 4x8 bit counts, mask 00001111
        x = (x >>> 4 & 0x0F0F0F0F) + (x & 0x0F0F0F0F);
        // Collapse 4x8 bit counts to 2x16 bit counts
        x = (x >>> 8 & 0x00FF00FF) + (x & 0x00FF00FF);
        // Collapse 2x16 bit counts to 1x32 bit count
        return (x >>> 16) + (x & 0x0000FFFF);
    }
    
    public boolean hasFlag(final LobbyFlag flag) {
        return (flags & (1 << flag.tag)) == (1 << flag.tag);//return LobbyFlag.hasFlag(flags, flag);
    }
    
    public void setFlag(final LobbyFlag flag, final boolean state) {
        flags = state ? (flags | (1 << flag.tag)) : flags & ~(1 << flag.tag);
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `lobbyData` SET `flags` = '"+flags+"' WHERE `name` = '"+nik+"';");
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void setFlags(final int flags) {
        this.flags = flags;
    }

    public void setOpenedArea(int openedArea) {
        this.openedArea = openedArea;
    }

    public LCuboid getCuboid() {
        return AreaManager.getCuboid(lastCuboidId);
    }
    
    
    
    @Override
    public void onLeave(final Player p) {
//    	mysqlData.put("logoutLoc", LocationUtil.toDirString(p.getLocation()));
    	mysqlData.put("area", String.valueOf(openedArea));
    	mysqlData.put("flags", String.valueOf(flags));
    	
    	super.onLeave(p);
        
        final LCuboid exitCuboid = AreaManager.getCuboid(p.getLocation());
        if (exitCuboid!=null) {
            if (exitCuboid.playerNames.remove(p.getName())) {
                Bukkit.getPluginManager().callEvent(new CuboidEvent(p, this, exitCuboid, null, cuboidEntryTime));
            }
        }
        p.removeMetadata("tp", Main.instance);
    }
    
    public void transport(final Player p, final XYZ to, final boolean inst) {
        final Location loc = p.getLocation();
        final XYZ fin = new XYZ("", to.x, to.y + ((to.y - loc.getBlockY()) >> 31) + 1, to.z);
        loc.getWorld().spawnParticle(Particle.SOUL, loc, 40, 0.6d, 0.6d, 0.6d, 0d, null, false);
        loc.getWorld().playSound(loc, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1f, 1f);
        
        if (inst) {
        	p.teleport(to.getCenterLoc(p.getWorld()));
            final Location nlc = p.getLocation();
            nlc.getWorld().spawnParticle(Particle.SOUL, nlc, 40, 0.6d, 0.6d, 0.6d, 0d, null, false);
            nlc.getWorld().playSound(nlc, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 1.4f);
            return;
        }
        
        final GameMode gm = p.getGameMode();
        p.setGameMode(GameMode.SPECTATOR);
        loc.getWorld().playSound(loc, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 0.8f);
        p.setVelocity(new Vector(fin.x + 0.5d, fin.y + 0.5d, fin.z + 0.5d).subtract(loc.toVector()).multiply(0.1f));
        final LobbyPlayer lp = this;
        
        new BukkitRunnable() {
            int count;
            int previosDistance = Integer.MAX_VALUE;
            int currDist;
            
            @Override
            public void run() {
                
                if (p==null || !p.isOnline()) {
                    this.cancel();
                    return;
                }
                
                
                
                final Location crl = p.getLocation();
                currDist = fin.distSq(crl);//
//p.sendMessage("§8log: count="+count+" curr="+currDist+" previos="+previosDistance);
                //if (Math.abs(loc.getBlockX() - second.x) < 2 && loc.getBlockY() == second.y && Math.abs(loc.getBlockZ() - second.z) < 2) {
                if (count>=100 || previosDistance<=currDist) { //предыдущая дистанция меньше или равна - значит пролетел и начал удаляться
                    this.cancel();
                    crl.getWorld().spawnParticle(Particle.SOUL, crl, 40, 0.6d, 0.6d, 0.6d, 0d, null, false);
                    crl.getWorld().playSound(crl, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 2f);
                    p.setGameMode(gm);
                    p.setFlying(false);
                    p.setVelocity(new Vector(0, 0, 0));
                    QuestManager.complete(p, lp, Quests.plate);
                } else {
                    previosDistance = currDist;  //запоминаем текущее расстояние для сравнения на в след.раз
                    p.setVelocity(new Vector(fin.x + 0.5d, fin.y + 0.5d, fin.z + 0.5d).subtract(crl.toVector()).multiply(0.1f));
                    crl.getWorld().spawnParticle(Particle.NAUTILUS, crl, 40, 0.2d, 0.2d, 0.2d);
                    
                }
                count++;
            }

        }.runTaskTimer(Main.instance, 10, 3);
        return;
    }
    
}
