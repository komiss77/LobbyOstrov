package ru.ostrov77.lobby.hd;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.modules.displays.DisplayManager;
import ru.komiss77.modules.displays.FakeItemDis;
import ru.komiss77.modules.world.XYZ;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.version.Nms;
import ru.ostrov77.lobby.area.CuboidInfo;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.area.AreaManager;


public class HD {
	
//    protected static final ConcurrentHashMap<String,MenuTask> TASKS = new ConcurrentHashMap<>();
    private static final ItemStack arrow = new ItemBuilder(Material.ARROW).enchant(Enchantment.INFINITY, 1).build();
    private static final ItemStack empty = new ItemStack(Material.GRAY_DYE);

    public static void openAreaMenu(final Player p, final LobbyPlayer lp) {

        DisplayManager.rmvDis(p);
        final Location eye = p.getEyeLocation();
        p.playSound(eye, Sound.BLOCK_BEEHIVE_ENTER, 2f, 1.2f);
        eye.add(eye.getDirection());
        eye.setPitch(0f); //чтобы не зависило от вверх - вниз
        
        for (final LCuboid lc : AreaManager.getCuboids()) {
            final CuboidInfo ci = lc.getInfo();
            
            if (ci==CuboidInfo.DEFAULT || !ci.canTp) continue;
            final Location nlc = eye.clone().add(ci.relX, ci.relY, ci.relZ);
            p.spawnParticle(Particle.GLOW_SQUID_INK, nlc, 2, 0d, 0d, 0d, 0d);
            if (Nms.getFastMat(nlc.getWorld(), nlc.getBlockX(), nlc.getBlockY(), nlc.getBlockZ()).isOccluding()) continue;
            if (lp.isAreaDiscovered(lc.id)) {
            	DisplayManager.fakeItemAnimate(p, nlc).setItem(new ItemStack(ci.icon)).setName(AreaManager.getCuboid(ci).displayName)
            	.setRotate(true).setIsDone(ie -> p.isSneaking() || ie > 1000).setOnClick((pl, dis) -> {
                    lp.transport(pl, new XYZ(lc.spawnPoint), true);
                    DisplayManager.rmvDis(pl);
                }).create();
            } else {
            	final FakeItemDis fid = DisplayManager.fakeItemAnimate(p, nlc).setItem(lp.target == lc.getInfo() ? arrow : empty)
            	.setName("§7*???*").setRotate(true).setIsDone(ie -> p.isSneaking() || ie > 1000);
            	fid.setOnClick((pl, dis) -> {
                    if (lp.target == lc.getInfo()) {
                        AreaManager.resetCompassTarget(p, lp);
                        fid.setItem(empty);
                    } else {
                        AreaManager.setCompassTarget(p, lp, lc);
                        fid.setItem(arrow);
                    }
            	}).create();
            }
        }
        
        
        
//        final MenuTask a = new MenuTask(p, center, holo);
//        TASKS.put(p.getName(), a);
    }

//    public static boolean isOpen( final Player p) {
//        return TASKS.containsKey(p.getName()) && TASKS.get(p.getName())!=null && !TASKS.get(p.getName()).isCanceled() ;
//    }
   /* protected static Location getHoloCentr(final Player p) {
        //final Location l = p.getEyeLocation();
        final Vector direction = p.getLocation().getDirection();
        final Location holoLoc = p.getEyeLocation().add(direction.multiply(2));
        holoLoc.setY(holoLoc.getY() + 1 + lines*0.25);//adelante.setY(adelante.getY() + hologram.size()*0.15);
        return holoLoc;
    }*/

    /*private static void setCompassTarget(final Player p, final LobbyPlayer lp, final LCuboid newTarget) {
        if (isOpen(p) && TASKS.get(p.getName()).holo.containsKey(lp.compasstarget)) { //в карте есть пункт с предыдущей целью компаса
            final Hologram h = TASKS.get(p.getName()).holo.get(lp.compasstarget); //вытаскиваем голограмму с предыдущей целью компаса
            if (h.getLines().size()!=0 && (h.getLines().get(0) instanceof ItemHologramLine)) { //первай строка - предмет
                final ItemHologramLine i = (ItemHologramLine) h.getLines().get(0); //вытаскиваем строку-предмет
                i.setItemStack(empty); //меняем её тип (цель не могла быть ранее открыта, так что только серый шарик
            }
        }   //
    
    }*/
    
    
}
