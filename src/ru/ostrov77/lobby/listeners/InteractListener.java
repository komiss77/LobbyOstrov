package ru.ostrov77.lobby.listeners;

import com.meowj.langutils.lang.LanguageHelper;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.Timer;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.ChunkContent;
import ru.ostrov77.lobby.area.LCuboid;
import ru.komiss77.modules.world.XYZ;
import ru.ostrov77.lobby.game.Parkur;
import ru.ostrov77.lobby.quest.Quest;
import ru.ostrov77.lobby.quest.QuestManager;


public class InteractListener implements Listener {


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        final LobbyPlayer lp = Main.getLobbyPlayer(p);
        
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
                        QuestManager.tryCompleteQuest(p, Main.getLobbyPlayer(p), b.getType() == Material.COBBLESTONE ? Quest.CobbleGen : Quest.MineDiam);
                        p.playSound(b.getLocation(), Sound.BLOCK_NETHER_BRICKS_BREAK, 1, 0.8f);
                        b.getWorld().spawnParticle(Particle.BLOCK_CRACK, b.getLocation().add(0.5d, 0.5d, 0.5d), 40, 0.4d, 0.4d, 0.4d, b.getBlockData());
                        break;
                    default:
                        break;
                }
                return;
            } 
            
            if (lp.hasQuest(Quest.FindBlock) && lp.foundBlocks.add(b.getType())) {
                //int currentProgress = lp.getProgress(Quest.FindBlock);
                final int found = QuestManager.updateProgress(p, lp, Quest.FindBlock, true);
                //final int sz = lp.foundBlocks.size();
                //QuestManager.progressAdv(p, lp, Quest.FindBlock, sz);
                if (found < Quest.FindBlock.ammount) {
                    ApiOstrov.sendActionBarDirect(p, "§7Найден блок §e" + LanguageHelper.getMaterialName(b.getType(), "RU_ru") + "§7, осталось: §e" + (Quest.FindBlock.ammount - found));
                } else {
                    //lp.foundBlocks.clear();
                    QuestManager.tryCompleteQuest(p, lp, Quest.FindBlock);
                    //lp.questDone(p, Quest.FindBlock, true);
                }
                //QuestManager.checkQuest(p, lp, Quest.FindBlock);
            }
            
            //спавн джина для новичка
            if (e.getAction()==Action.RIGHT_CLICK_BLOCK && b.getType()==Material.SOUL_LANTERN) {
                if (Timer.has(p.getEntityId())) return;
                Timer.add(p.getEntityId(), 3);
                final LCuboid lc = AreaManager.getCuboid(p.getLocation());
                if (lc!=null && lc.getName().equals("newbie")) {
                    if (b.getX()==Main.getLocation(Main.LocType.ginLampShip).getBlockX() && b.getZ()==Main.getLocation(Main.LocType.ginLampShip).getBlockZ()) {
                        p.performCommand("oscom gin");
                    } else {
                        p.sendMessage("§3Должно быть, другая лампа!");
                    }
                    
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
//e.getPlayer().sendMessage("§aПлита на коорд. -> "+second.toString());
                        loc.getWorld().spawnParticle(Particle.SOUL, loc, 40, 0.6d, 0.6d, 0.6d, 0d, null, false);
                        loc.getWorld().playSound(loc, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1f, 1f);
                        loc.getWorld().playSound(loc, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 0.8f);
                        final GameMode gm = p.getGameMode();
                        p.setGameMode(GameMode.SPECTATOR);
                        p.setVelocity(new Vector(second.x + 0.5d, second.y + 0.5d, second.z + 0.5d).subtract(loc.toVector()).multiply(0.1f));
                        
                        new BukkitRunnable() {
                            final String name = p.getName();
                            int count;
                            int previosDistance = Integer.MAX_VALUE;
                            int currDist;
                            
                            @Override
                            public void run() {
                                
                                final Player p = Bukkit.getPlayerExact(name);
                                if (p==null || !p.isOnline()) {
                                    this.cancel();
                                    return;
                                }
                                
                                
                                
                                final Location loc = p.getLocation();
                                currDist = second.getDistance(loc);//
//p.sendMessage("§8log: count="+count+" curr="+currDist+" previos="+previosDistance);
                                //if (Math.abs(loc.getBlockX() - second.x) < 2 && loc.getBlockY() == second.y && Math.abs(loc.getBlockZ() - second.z) < 2) {
                                if (count>=100 || previosDistance<=currDist) { //предыдущая дистанция меньше или равна - значит пролетел и начал удаляться
                                    
                                    this.cancel();
                                    loc.getWorld().spawnParticle(Particle.SOUL, loc, 40, 0.6d, 0.6d, 0.6d, 0d, null, false);
                                    loc.getWorld().playSound(loc, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 2f);
                                    p.setGameMode(gm);
                                    p.setFlying(false);
                                    p.setVelocity(new Vector(0, 0, 0));
                                    QuestManager.tryCompleteQuest(p, lp, Quest.HeavyFoot);
                                    
                                } else {
                                    
                                    previosDistance = currDist;  //запоминаем текущее расстояние для сравнения на в след.раз
                                    p.setVelocity(new Vector(second.x + 0.5d, second.y + 0.5d, second.z + 0.5d).subtract(loc.toVector()).multiply(0.1f));
                                    loc.getWorld().spawnParticle(Particle.NAUTILUS, loc, 40, 0.2d, 0.2d, 0.2d);
                                    
                                }
                                count++;
                            }

                        }.runTaskTimer(Main.instance, 10, 3);
                        return;
                        
                    } else {
                        ApiOstrov.sendActionBarDirect(p, "§cСначала разведайте пункт назначения!");
                        return;
                    }
                }
            }
            
            if (e.getClickedBlock().getType() == Material.WARPED_PRESSURE_PLATE && lp.pkrist == null) { //новый паркурист
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
                //Main.miniParks.add(pr);
            }
        }
    }

}
