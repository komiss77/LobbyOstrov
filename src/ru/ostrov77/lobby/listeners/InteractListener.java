package ru.ostrov77.lobby.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.Timer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.modules.quests.QuestManager;
import ru.komiss77.modules.translate.Lang;
import ru.komiss77.modules.world.XYZ;
import ru.komiss77.utils.TCUtils;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.ChunkContent;
import ru.ostrov77.lobby.area.CuboidInfo;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.game.Parkur;
import ru.ostrov77.lobby.quest.Quests;


public class InteractListener implements Listener {


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        final LobbyPlayer lp = PM.getOplayer(p, LobbyPlayer.class);
        if (lp==null) return;
        if (e.getClickedBlock() != null) {
            final Block b = e.getClickedBlock();
            //для элитр
            if (e.hasItem() && e.getItem().getType()==Material.FIREWORK_ROCKET) {
                e.setUseItemInHand(Event.Result.DENY);
                return;
            }
            
            //копатель
            if (e.hasItem() && e.getItem().getType()==Material.DIAMOND_PICKAXE && e.getAction() == Action.LEFT_CLICK_BLOCK) {
            	switch (b.getType()) {
                    case DIAMOND_ORE:
                    case COBBLESTONE:
                        Ostrov.sync(() -> p.sendBlockChange(b.getLocation(), Material.AIR.createBlockData()), 2);
                        Ostrov.sync(() -> p.sendBlockChange(b.getLocation(), b.getType().createBlockData()), 40);
                        QuestManager.addProgress(p, lp, b.getType() == Material.COBBLESTONE ? Quests.cobble : Quests.dims);
                        p.playSound(b.getLocation(), Sound.BLOCK_NETHER_BRICKS_BREAK, 1, 0.8f);
                        b.getWorld().spawnParticle(Particle.BLOCK_CRACK, b.getLocation().add(0.5d, 0.5d, 0.5d), 40, 0.4d, 0.4d, 0.4d, b.getBlockData());
                        break;
                    default:
                        break;
                }
                return;
            } 
            
            
            if (QuestManager.isComplete(lp, Quests.arcaim) && lp.foundBlocks.add(b.getType()) && QuestManager.addProgress(p, lp, Quests.find)) {
                ApiOstrov.sendActionBarDirect(p, "§7Найден блок §e" + TCUtils.toString(Lang.t(b.getType(), p.locale())) +
                	"§7, осталось: §e" + (Quests.find.amount - QuestManager.getProgress(lp, Quests.find)));
            }
            
            //спавн джина для новичка
            if (e.getAction()==Action.RIGHT_CLICK_BLOCK) {
                switch (b.getType()) {
                case SOUL_LANTERN:
                    if (Timer.has(p.getEntityId())) return;
                    Timer.add(p.getEntityId(), 3);
                    final LCuboid lc = AreaManager.getCuboid(p.getLocation());
                    if (lc!=null && lc.getInfo() == CuboidInfo.NEWBIE) {
                        if (b.getX()==Main.getLocation(Main.LocType.ginLamp).getBlockX() && b.getZ()==Main.getLocation(Main.LocType.ginLamp).getBlockZ()) {
                            p.performCommand("oscom gin");
                        } else {
                            p.sendMessage("§3Должно быть, другая лампа!");
                        }
                    }
                    break;
                default:
                    if (b.getBlockData() instanceof Openable || Tag.FLOWER_POTS.isTagged(b.getType())) {
                        e.setCancelled(!ApiOstrov.isLocalBuilder(e.getPlayer(), false));
                    }
                    break;
                }
            }
           
        }
        
        
        if (e.getAction() == Action.PHYSICAL) {
            final Location loc = e.getClickedBlock().getLocation();

            final ChunkContent cc = AreaManager.getChunkContent(loc);
            if (cc!=null && cc.hasPlate()) {
                final XYZ second = cc.getPlate(loc);
                if (second!=null) { //пункт назначения назначен - значит плата есть
                    final LCuboid sCub = AreaManager.getCuboid(second); //кубоид назначения брать после проверки second на null!!!
                    if (sCub == null || lp.isAreaDiscovered(sCub.id)) {  //в точке назначения нет кубоида, или территория уже изучена
                    	lp.transport(p, second, false);
                    } else {
                        ApiOstrov.sendActionBarDirect(p, "§cСначала разведайте пункт назначения!");
                    }
                    return;
                }
            }
            
            switch (e.getClickedBlock().getType()) {
			case WARPED_PRESSURE_PLATE:
	            if (lp.pkrist == null) { //новый паркурист
	                final Parkur pr = new Parkur(p);

	                    pr.bLast = new XYZ(loc.add(0d, 30d, 0d));
	                    final Block b = loc.getBlock();
	                    b.setType(Material.LIME_CONCRETE, false);
	                    final BlockFace sd = Parkur.sds[Ostrov.random.nextInt(4)];
	                    final Block n = ApiOstrov.randBoolean() ? 
	                            b.getRelative(sd, 2).getRelative(sd.getModZ() == 0 ? (ApiOstrov.randBoolean() ? BlockFace.NORTH : BlockFace.SOUTH) : (ApiOstrov.randBoolean() ? BlockFace.WEST : BlockFace.EAST)) 
	                            : 
	                            b.getRelative(sd, 2).getRelative(BlockFace.UP);
	                    n.setType(Material.LIME_CONCRETE, false);
	                    pr.bNext = new XYZ(n.getLocation());
	                    p.teleport(loc.add(0.5d, 1.1d, 0.5d));
	                    Ostrov.sync(() -> {
	                        p.playSound(p.getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_PLACE, 2f, 1.4f);
	                        lp.pkrist = pr;
	                    }, 4);
	            }
				break;
			case FARMLAND:
				e.setCancelled(!ApiOstrov.isLocalBuilder(p, false));
				break;
			default:
				break;
			}
        }
    }

}
