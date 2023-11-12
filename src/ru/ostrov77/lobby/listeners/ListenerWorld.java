package ru.ostrov77.lobby.listeners;

import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Orientable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.TextComponent;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.Timer;
import ru.komiss77.enums.Stat;
import ru.komiss77.enums.StatFlag;
import ru.komiss77.events.LocalDataLoadEvent;
import ru.komiss77.events.QuestCompleteEvent;
import ru.komiss77.events.ScoreWorldRecordEvent;
import ru.komiss77.modules.bots.BotEntity;
import ru.komiss77.modules.bots.BotManager;
import ru.komiss77.modules.player.PM;
import ru.komiss77.modules.quests.Quest;
import ru.komiss77.modules.quests.QuestManager;
import ru.komiss77.modules.world.WXYZ;
import ru.komiss77.modules.world.XYZ;
import ru.komiss77.utils.TCUtils;
import ru.ostrov77.lobby.JinGoal;
import ru.ostrov77.lobby.LobbyFlag;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.Main.LocType;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.ChunkContent;
import ru.ostrov77.lobby.area.CuboidInfo;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.bots.LobbyBot;
import ru.ostrov77.lobby.bots.SpotManager;
import ru.ostrov77.lobby.bots.spots.Spot;
import ru.ostrov77.lobby.bots.spots.SpotType;
import ru.ostrov77.lobby.event.CuboidEvent;
import ru.ostrov77.lobby.game.RaceBoard;
import ru.ostrov77.lobby.game.SumoBoard;
import ru.ostrov77.lobby.quest.Quests;





public class ListenerWorld implements Listener {
    
    private static final BlockFace[] NEAREST = {BlockFace.DOWN, BlockFace.UP, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST};
    private static final RaceBoard race = new RaceBoard("§б§nСостязание", new WXYZ(Main.getLocation(LocType.raceLoc)));
    private static final SumoBoard sumo = new SumoBoard("§c§nАрена Сумо", new WXYZ(Main.getLocation(LocType.sumoLoc)));

   /* @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onArmor (final ArmorEquipEvent e) {
System.out.println("ArmorEquipEvent");
        if (!e.getPlayer().isSneaking()) e.setCancelled(true);
    }*/

    	
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityPortalEnter(final EntityPortalEnterEvent e) {
        
        if (e.getEntityType()==EntityType.PLAYER) {
            
            if (e.getEntity().getTicksLived() < 100 || Timer.has(e.getEntity().getEntityId()))  return;
            final Player p = (Player) e.getEntity();
            Timer.add(p.getEntityId(), 5);
            
            for (final XYZ xyzw : Main.serverPortals.keySet()) {
                if (xyzw.nearly(e.getLocation(), 16)) {
                    p.performCommand("server "+Main.serverPortals.get(xyzw));
                }
            }
        } else {
			final BotEntity bt = BotManager.getBot(e.getEntity().getEntityId(), BotEntity.class);
			if (bt != null) bt.remove();
		}
    }
    
    
    
   /* @EventHandler (priority = EventPriority.MONITOR)
    public void onLogin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        if (!Main.lobbyPlayers.containsKey(p.getName())) {
            final LobbyPlayer lp = new LobbyPlayer(p.getName());
        }
        
    }   */
    
    @EventHandler (priority = EventPriority.MONITOR)
    //public void onJoin(final PlayerJoinEvent e) {
    public void onLocalData(final LocalDataLoadEvent e) {
    	if (e.getOplayer() instanceof final LobbyPlayer lp) {
    		final Player pl = e.getPlayer();
            if (lp.isGuest) { //данные не грузим, просто даём часики и на спавн
                Main.pipboy.giveForce(pl);
                //Main.oscom.giveForce(p);
                pl.teleport(Main.getLocation(Main.LocType.Spawn));
                if (lp.getStat(Stat.PLAY_TIME)<300) {
                    ApiOstrov.sendTitle(pl, "","§eЧасики откроют меню", 20, 100, 40);
                }
                return;
            }
            
            if (!lp.hasFlag(StatFlag.NewBieDone) && lp.getStat(Stat.PLAY_TIME)<100) {
            	lp.setFlag(StatFlag.NewBieDone, true);
                ApiOstrov.sendToServer(pl, "nb0", "");
                //return;
            }
            
            //создается в острове
//            final LobbyPlayer lp = new LobbyPlayer(p.getName());//Main.createLobbyPlayer(p); тут только создаём, или квесты срабаывают при появлении на спавне
            
            final String flags = lp.mysqlData.get("flags");
            lp.setFlags(flags == null || flags.isEmpty() ? 0 : Integer.parseInt(flags));
            final String areas = lp.mysqlData.get("area");
            lp.setOpenedArea(areas == null || areas.isEmpty() ? 0 : Integer.parseInt(areas));
            
            onDataLoad(pl, lp);
//            for (final Entry<Quest, IProgress> en : lp.quests.entrySet()) {
//            	pl.sendMessage("q-" + en.getKey().toString());
//            }
    	}
    }
    
    

    private void onDataLoad(final Player p, final LobbyPlayer lp) {//, final String logoutLocString
        if (QuestManager.isComplete(lp, Quests.ostrov)) {
            Main.giveItems(p);
//            final Location logoutLoc = LocationUtil.LocFromString(logoutLocString);
//            if (logoutLoc !=null && ApiOstrov.teleportSave(p, logoutLoc, false)) {
//            } else {
//                p.teleport(Main.getLocation(Main.LocType.Spawn));
//            }
        } else {
            p.teleport(Main.getLocation(LocType.newBieSpawn));// тп на 30 160 50
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 64, 5));
            p.setCollidable(false);
            Main.oscom.giveForce(p); //ApiOstrov.getMenuItemManager().giveItem(p, "newbie");
            if (PM.exist(p.getName())) PM.getOplayer(p).hideScore();
            ApiOstrov.sendBossbar(p, "#3 Остров.", 5, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // ******** эвенты по новичку
    /*
    @EventHandler (priority = EventPriority.MONITOR)
    public static void onCuboidEvent(final CuboidEvent e) {
        
        if (e.getLast()!=null && e.getLast().getInfo().hidePlayers ) { //выход из кубоида со скрытием //if (e.getPrevois()!=null && e.getPrevois().getName().equals("newbie") ) {
            final Player p = e.getPlayer();
            for (final Player cp : e.getLast().getCuboidPlayers()) {
                if (!cp.getName().equals(p.getName())) {
                    cp.showPlayer(Main.instance, p);
                    p.showPlayer(Main.instance, cp);      
                }
            }
        }
        if (e.getCurrent()!=null && e.getCurrent().getInfo().hidePlayers) {//вход в со скрытием //if (e.getCurrent()!=null && e.getCurrent().getName().equals("newbie")) { 
            final Player p = e.getPlayer();
            for (final Player cp : e.getCurrent().getCuboidPlayers()) {
                if (!cp.getName().equals(p.getName())) {
                    cp.hidePlayer(Main.instance, p);
                    p.hidePlayer(Main.instance, cp);
                }
            }
        }
    }*/
    
    @EventHandler (priority = EventPriority.MONITOR)
    public static void onQuest(final QuestCompleteEvent e) {
        final LobbyPlayer lp = PM.getOplayer(e.getPlayer(), LobbyPlayer.class);
    	if (e.getQuest().equals(Quests.doctor)) {
            Main.elytra.giveForce(e.getPlayer());
            new CuboidEvent(e.getPlayer(), lp, lp.getCuboid(), lp.getCuboid(), lp.cuboidEntryTime).callEvent();
            return;
    	}
    	
        for (final Quest qs : QuestManager.getQuests(q -> !q.equals(Quests.doctor))) {
        	if (!QuestManager.isComplete(lp, qs)) return;
        }
        
        QuestManager.complete(e.getPlayer(), lp, Quests.doctor);
    }
    
    @EventHandler (priority = EventPriority.MONITOR)
    public static void onInvClose(final InventoryCloseEvent e) {
        if (e.getInventory().getType()==InventoryType.CHEST) {
            final LobbyPlayer lp = PM.getOplayer(e.getPlayer(), LobbyPlayer.class);
            if (lp.getPasportFillPercent() > 20) QuestManager.complete((Player) e.getPlayer(), lp, Quests.pass);
        }
    }
    
    //нописание в actionbar куда человек зашел + квест на гонку для ПВЕ мини-игр
    @EventHandler
    public static void onCuboidEvent(final CuboidEvent e) {  //если лоббиплеер нуль, то сюда никогда не придёт
    	
        if (e.getLast() != null) {
        	if (e.getLast().getInfo().hidePlayers) { //выход из кубоида со скрытием //if (e.getPrevois()!=null && e.getPrevois().getName().equals("newbie") ) {
                final Player p = e.getPlayer();
                for (final Player cp : e.getLast().getCuboidPlayers()) {
                    if (!cp.getName().equals(p.getName())) {
                        cp.showPlayer(Main.instance, p);
                        p.showPlayer(Main.instance, cp);      
                    }
                }
        		
        	}
            switch (e.getLast().getName()) {
                case "daaria", "skyworld", "sumo" -> {
                    e.getPlayer().getInventory().setItem(2, QuestManager.isComplete(e.getLobbyPlayer(), Quests.doctor) ? Main.fw : Main.air);
                }
            }
        }
        
    	if (e.getCurrent() == null) {
            
            ApiOstrov.sendActionBarDirect(e.getPlayer(), "§7§l⟣ §3§lАрхипелаг §7§l⟢");
            
    	} else {
    		if (e.getCurrent().getInfo().hidePlayers) {//вход в со скрытием //if (e.getCurrent()!=null && e.getCurrent().getName().equals("newbie")) { 
                final Player p = e.getPlayer();
                for (final Player cp : e.getCurrent().getCuboidPlayers()) {
                    if (!cp.getName().equals(p.getName())) {
                        cp.hidePlayer(Main.instance, p);
                        p.hidePlayer(Main.instance, cp);
                    }
                }
    		}
    		
    		final LobbyPlayer lp = e.getLobbyPlayer();
            ApiOstrov.sendActionBarDirect(e.getPlayer(), "§7§l⟣ " + e.getCurrent().displayName + " §7§l⟢");
            if (!lp.isAreaDiscovered(e.getCurrent().id)) {
               onNewAreaDiscover(e.getPlayer(), lp, e.getCurrent()); //новичёк или нет - обработается внутри
            }
            
            if (!lp.hasFlag(LobbyFlag.NewBieDone)) return; //далее - новичкам ничего не надо
            
            switch (e.getCurrent().getName()) {
                
                case "start" -> {
                    if (lp.isAreaDiscovered(AreaManager.getCuboid("nopvp").id)) {
                        final Player p = e.getPlayer();
                        p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 2f);
                        p.sendMessage("§5[§eСостязание§5] §7>> На старт! Внимание! Вперед!");
                        lp.raceTime = 0;
                        Main.elytra.takeAway(p);
                    } else {
                        e.getPlayer().sendMessage("§5[§eСостязание§5] §7>> Найдите §eОазис§7 перед началом!");
                    }
                }
                
                case "end" -> {
                    if (lp.raceTime > 0) {
                        final Player p = e.getPlayer();
                        p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1f, 2f);
                        p.sendMessage("§5[§eСостязание§5] §7>> Хорошо сработано! Время: §e" + ApiOstrov.secondToTime(lp.raceTime));
                    	QuestManager.complete(p, e.getLobbyPlayer(), Quests.race);
                        race.tryAdd(p.getName(), lp.raceTime);
                        lp.raceTime = -1;
                        Main.elytra.giveForce(p);
                        //lp.questDone(p, quest, true);
                    }
                }
                    
                case "daaria", "skyworld" -> {
                    Main.pickaxe.giveForce(e.getPlayer());
                }
                
                case "sumo" -> {
                    Main.stick.giveForce(e.getPlayer());
                }

            }
            
        }

        
    }
    
    
    
    //SYNC !!!
    public static void onNewAreaDiscover(final Player p, final LobbyPlayer lp, final LCuboid cuboid) {
        lp.setAreaDiscovered(cuboid.id);
        
        final CuboidInfo ci = cuboid.getInfo();
        lp.setAreaDiscovered(cuboid.id);
        if (QuestManager.complete(p, lp, ci.quest)) 
        	QuestManager.addProgress(p, lp, Quests.discover);
        
        if (ci.quest.equals(Quests.ostrov)) lp.setFlag(LobbyFlag.NewBieDone, true);
        
        if (QuestManager.isComplete(lp, Quests.discover)) {
        	QuestManager.complete(p, lp, Quests.navig);
        	ApiOstrov.giveMenuItem(p);
        }
        
        ApiOstrov.sendBossbar(p, "Открыта Локация: "+cuboid.displayName, 7, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS);
        if (lp.target==cuboid.getInfo()) {
            AreaManager.resetCompassTarget(p, lp);
        }
        
        sound(p);
        
        /*final EnumSet<Quest__> childrenQuest = Quest__.getChidren(cuboid.getName());
        if (!childrenQuest.isEmpty()) { //найти зависимые от его выполнения квесты
            for (Quest__ childQuest : childrenQuest) {
                if (lp.addQuest(childQuest)) {
//p.sendMessage("§8log: +новое задание с открытием зоны "+cuboid.getName()+" -> "+childQuest.displayName);
                    if (childQuest.ammount>0) {
                        Main.advance.sendProgress(p, childQuest,lp.getProgress(childQuest));//sendProgress(p, lp, childQuest, QuestManager.getProgress(p, lp, childQuest, true)); //чтобы отобразило
                    }
                }
            }
        }
      
        if (cuboid.getInfo().canTp) {  //значимый кубоид для счётчика в DiscoverAllArea
            //Софтлок (нельзя пройти) задания Навигатор, если все локации открыты -
            //при открытии последней новой зоны сначала автовыполнение навигатора
            //lp.getProgress(Quest.DiscoverAllArea) выдаст 0, т.к. квест DiscoverAllArea не начат!
            //нужно вручную посчитать уже открытые зоны
            if (!lp.hasQuest(Quest__.DiscoverAllArea)) { //изучаем зону, когда квест DiscoverAllArea еще на получен. (получен будет после хэвифут)
                int opened = 0;
                for (LCuboid lc : AreaManager.getCuboids()) {
                    if (lc.getInfo().canTp && lp.isAreaDiscovered(lc.id)) {
                        opened++;
                   }
                }
    //Ostrov.log("DiscoverAllArea "+opened+" tryCompleteQuest complete Navigation ? "+(opened==Quest.DiscoverAllArea.ammount));
                if (opened==Quest__.DiscoverAllArea.ammount) {
                    tryCompleteQuest(p, lp, Quest__.Navigation, false);
                }
            } else {
                tryCompleteQuest(p, lp, Quest__.DiscoverAllArea);
            }
        }*/
        
    }

    public static void sound(final Player p) {
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2, 0.5f);
        Ostrov.async(()-> {
            if (p.isOnline()) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1,  0.5f);
            }
        }, 5);
    }
    
    
    
    
    
    @EventHandler (ignoreCancelled = true)
    public void onPlayerChat(final AsyncChatEvent e) { //только когда новичёк пишет в чат
        if (!e.getPlayer().getWorld().getName().equals("world")) return;
        final LCuboid lc = AreaManager.getCuboid(e.getPlayer().getLocation());
        if (lc!=null && lc.getName().equals("newbie")) {
            //e.
//e.getPlayer().sendMessage("§8log: чат новичка "+e.viewers());            
            //e.setCancelled(true);
            //e.viewers().clear();
        }
    }      
    
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDismount(final EntityDismountEvent e) {
//e.getEntity().sendMessage("§7log: onDismount getEntityType="+e.getEntityType()+" getDismountedType="+e.getDismounted().getType()+" getEntityType"+e.getEntity().getType());
        if (e.getEntityType()==EntityType.PLAYER && e.getDismounted().getType()==EntityType.BLAZE) {
            if (e.getDismounted().isCustomNameVisible() && JinGoal.ginName.equals(TCUtils.toString(e.getDismounted().customName()))) {
                e.setCancelled(true);
                if (!Timer.has(e.getEntity().getEntityId())) {
                    e.getEntity().sendMessage("§6Погодите, уже скоро будем на месте!");
                    Timer.add(e.getEntity().getEntityId(), 3);
                }
            }
        }
    }
    
    
    //***************************        
    

    
    
    
    
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onPlace(final BlockPlaceEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("world")) return;
        if (!ApiOstrov.isLocalBuilder(e.getPlayer()) ) e.setCancelled(true);
        
        final Block b = e.getBlockPlaced();
        final Location loc;
		
        switch (b.getType()) {
            
            case FIRE:
                if (plcAtmpt(b, BlockFace.EAST) || plcAtmpt(b, BlockFace.SOUTH)) {
                    final ItemStack it = e.getItemInHand();
                    if (it.getType()!=Material.AIR && it.hasItemMeta()) {
                        final String servername = ((TextComponent) it.getItemMeta().displayName()).content();
                        Main.serverPortals.put(new XYZ(b.getLocation()), servername);
                        Main.savePortals();
                    }
                }
                break;
                
                
            case HEAVY_WEIGHTED_PRESSURE_PLATE:
                final Player p = e.getPlayer();
                loc = b.getLocation();
                
                if (p.hasMetadata("tp")) {
                    final XYZ firstPlateXYZ = (XYZ) p.getMetadata("tp").get(0).value();
                    final XYZ secondPlateXYZ = new XYZ(loc);
 //System.out.println("§8log: firstPlateXYZ="+firstPlateXYZ.toString());                    
 //System.out.println("§8log: secondPlateXYZ="+secondPlateXYZ.toString());    
                    final int cLoc = AreaManager.getcLoc(firstPlateXYZ);
                    final ChunkContent cc = AreaManager.getChunkContent(cLoc, true); //берём чанк по ПЕРВОЙ плите, запоминаем то в первой!
                    cc.addPlate(firstPlateXYZ, secondPlateXYZ);
                    AreaManager.savePlate(firstPlateXYZ, secondPlateXYZ);
                    p.sendMessage("§2Вторая плита поставлена на координатах (§7" + loc.getBlockX() + "§2, §7" + loc.getBlockY() + "§2, §7" + loc.getBlockZ() + "§2)!");
                    p.removeMetadata("tp", Main.instance);
                    e.setCancelled(true);
                    p.sendMessage("§2Плита создана!");
                } else {
                    p.setMetadata("tp", new FixedMetadataValue(Main.instance, new XYZ(loc)));
                    p.sendMessage("§aПервая плита поставлена на координатах (§7" + loc.getBlockX() + "§a, §7" + loc.getBlockY() + "§a, §7" + loc.getBlockZ() + "§a)!");
                }
                break;
                
            case BEDROCK:
                Main.loadCfgs();
                e.getPlayer().sendMessage("§eПерезагружено!");
                break;
                
            case DEAD_BRAIN_CORAL:
                loc = b.getLocation();
            	SpotManager.addSpot(new XYZ(loc), SpotType.SPAWN);
                break;
            case DEAD_TUBE_CORAL:
                loc = b.getLocation();
            	SpotManager.addSpot(new XYZ(loc), SpotType.WALK);
                break;
            case DEAD_FIRE_CORAL:
                loc = b.getLocation();
            	SpotManager.addSpot(new XYZ(loc), SpotType.END);
                break;
            case DEAD_BUBBLE_CORAL:
                final Spot sp = SpotManager.getRndSpot(SpotType.SPAWN);
                if (sp != null) {
                	final int pls = Bukkit.getOnlinePlayers().size();
                	if (pls != 0) {
                		final String nm = ApiOstrov.rndElmt(SpotManager.names);
                		BotManager.createBot(nm, LobbyBot.class, () -> new LobbyBot(nm, new WXYZ(sp.getLoc())));
                	}
                }
                break;
			default:
				break;
                
        }
        
    }
    
    
    
    
    
    
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onBreak(final BlockBreakEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("world")) return;
        if (!ApiOstrov.isLocalBuilder(e.getPlayer()) ) e.setCancelled(true);
        final Location loc = e.getBlock().getLocation();
        
        switch (e.getBlock().getType()) {
		case NETHER_PORTAL:
			if (!Main.serverPortals.isEmpty()) {
                XYZ find = null;
                for (final XYZ xyzw : Main.serverPortals.keySet()) {
                    if (xyzw.nearly(loc, 16)) {
                        find = xyzw;
                        break;
                    }
                }
                if (find!=null) {
                    Main.serverPortals.remove(find);
                    Main.savePortals();
                }
                return;
			}
			break;
		case LIGHT_WEIGHTED_PRESSURE_PLATE:
            final ChunkContent cc = AreaManager.getChunkContent(loc);
//System.out.println("§8log: cc="+cc+" has?"+(cc!=null && cc.hasPlate()));  
            if (cc!=null && cc.hasPlate()) {
                final XYZ second = cc.getPlate(loc);
//System.out.println("§8log: second="+second);  
                if (second!=null) { //пункт назначения назначен - значит плата есть
                    cc.delPlate(loc);
                    AreaManager.savePlate(new XYZ(loc), null);
                    e.getPlayer().sendMessage("§6Плита, ведущаяя к §a"+second+" §6убрана!");
                }
            }
			break;
		case DEAD_HORN_CORAL:
			SpotManager.deleteSpot(new XYZ(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
			break;
        case DEAD_BUBBLE_CORAL:
        	BotManager.clearBots();
        	break;
		default:
			break;
		}
        
    }

    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onHangingBreakByEntityEvent(final HangingBreakByEntityEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) return;
        if (  e.getRemover()!=null && e.getRemover().getType()==EntityType.PLAYER && PM.exist(e.getEntity().getName()) ) {
                if (!ApiOstrov.isLocalBuilder((Player) e.getRemover()) ) e.setCancelled(true);
        } 

    }
       
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onHangingBreakEvent(final HangingBreakEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) return;
        if ( e.getEntity().getType()==EntityType.PLAYER) {
                if (!ApiOstrov.isLocalBuilder((Player) e.getEntity()) ) e.setCancelled(true);
        } 
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onPlayerItemFrameChangeEvent(final PlayerItemFrameChangeEvent e) {
        e.setCancelled(!ApiOstrov.isLocalBuilder(e.getPlayer()));
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onPlayerInteractAtEntityEvent(final PlayerInteractAtEntityEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("world")) return;
        final Player p = e.getPlayer();
        switch (e.getRightClicked().getType()) {
		case ARMOR_STAND, ITEM_FRAME, GLOW_ITEM_FRAME:
            e.setCancelled(!ApiOstrov.isLocalBuilder(p));
			break;
		case PLAYER:
            final LobbyPlayer lp = PM.getOplayer(p, LobbyPlayer.class);
            if (lp==null) return;
            final LobbyPlayer olp = PM.getOplayer(e.getRightClicked().getName(), LobbyPlayer.class);
//p.sendMessage("§8log: ПКМ на игрока, новичёк?"+!clickedLp.questDone.contains(Quest.DiscoverAllArea));
            if (olp!=null && (olp.isGuest || QuestManager.isComplete(olp, Quests.discover))) {
            	QuestManager.complete(p, lp, Quests.greet);
            }
			break;
		default:
			break;
		}
    }

   

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void PlayerArmorStandManipulateEvent(final PlayerArmorStandManipulateEvent e){
        if (!e.getPlayer().getWorld().getName().equals("world")) return;
        if (!ApiOstrov.isLocalBuilder(e.getPlayer())) e.setCancelled(true);
    }        
    
    
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onPlayerRespawn(final PlayerRespawnEvent e) {
         if (!e.getPlayer().getWorld().getName().equals("world")) return;
    }
    
    
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void ProjectileHitEvent(final ProjectileHitEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) return;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onEntityDamage(final EntityDamageEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) return;
        
        if (e.getEntityType()==EntityType.PLAYER && PM.exist(e.getEntity().getName()) ) {
            final Player p = (Player) e.getEntity();
            final EntityDamageEvent de;
            switch (e.getCause()) {
                case VOID:
                    e.setDamage(0d);
                    final LobbyPlayer lp = PM.getOplayer(p, LobbyPlayer.class);
                    if (lp==null || lp.hasFlag(LobbyFlag.NewBieDone)) { //старичков кидаем на спавн
                    	final Location loc = p.getLocation();
                    	final LCuboid lc = AreaManager.getCuboid(new XYZ(loc.getWorld().getName(), loc.getBlockX(), 80, loc.getBlockZ()));
                        p.teleport(lc == null ? Main.getLocation(LocType.Spawn) : lc.spawnPoint, PlayerTeleportEvent.TeleportCause.COMMAND);
                    } else { //новичков - если прыгнул за борт - на точку прибытия
                        Main.arriveNewBie(p);
                    }
                    
                    de = p.getLastDamageCause();
                    if (de instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) de).getDamager().getType() == EntityType.PLAYER) {
                    	final Player dp = (Player) ((EntityDamageByEntityEvent) de).getDamager();
                        final LobbyPlayer olp = PM.getOplayer(dp, LobbyPlayer.class);
                        if (olp!=null) {
                        	final Integer wns = sumo.getAmt(olp.nik);
                        	olp.sumoWins = wns == null || wns < olp.sumoWins ? olp.sumoWins + 1 : wns + 1;
                        	sumo.tryAdd(olp.nik, olp.sumoWins);
                        	p.setLastDamageCause(new EntityDamageEvent(p, DamageCause.CUSTOM, 0d));
                        	QuestManager.complete(dp, olp, Quests.sumo);
                            for (final Player pl : dp.getWorld().getPlayers()) {
                                pl.sendMessage("§7[§cСумо§7] Игрок §a" + dp.getName() + "§7 скинул §c" + p.getName() + "§7 с арены!");
                            }
                        }
                    }
                    return;
                case FALL:
                    de = p.getLastDamageCause();
                    if (de instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) de).getDamager().getType() == EntityType.PLAYER) {
                    	final Player dp = (Player) ((EntityDamageByEntityEvent) de).getDamager();
                        final LobbyPlayer olp = PM.getOplayer(dp, LobbyPlayer.class);
                        if (olp!=null) {
                        	final Integer wns = sumo.getAmt(olp.nik);
                        	olp.sumoWins = wns == null || wns < olp.sumoWins ? olp.sumoWins + 1 : wns + 1;
                        	sumo.tryAdd(olp.nik, olp.sumoWins);
                        	p.setLastDamageCause(new EntityDamageEvent(p, DamageCause.CUSTOM, 0d));
                        	QuestManager.complete(dp, olp, Quests.sumo);
                            for (final Player pl : dp.getWorld().getPlayers()) {
                                pl.sendMessage("§7[§cСумо§7] Игрок §a" + dp.getName() + "§7 скинул §c" + p.getName() + "§7 с арены!");
                            }
                        }
                    }
                case THORNS:        //чары шипы на оружие-ранит нападающего
                case LIGHTNING:     //молния
                case DRAGON_BREATH: //дыхание дракона
                case CONTACT:       //кактусы
                case FIRE:          //огонь
                case FIRE_TICK:     //горение
                case HOT_FLOOR:     //BlockMagma
                case CRAMMING:      //EntityVex
                case DROWNING:      //утопление
                case STARVATION:    //голод
                case LAVA:	    	//лава
                    e.setCancelled(true);
                    return;
                    //break;
                case SUFFOCATION:   //зажатие в стене
                	p.teleport(Main.getLocation(LocType.Spawn));
                	p.sendMessage("§6[§eОстров§6] §eТебя зажала стена и переместила обратно на спавн!");
                	e.setCancelled(false);
                    return;

                case ENTITY_ATTACK: //ентити ударяет
                    if (e instanceof EntityDamageByEntityEvent) {
                        final EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
                        if (ee.getDamager().getType() == EntityType.PLAYER) {
                            final LCuboid vCub = AreaManager.getCuboid(e.getEntity().getLocation());
                            if (vCub != null && vCub.getInfo() == CuboidInfo.SUMO) {
                                final LCuboid dCub = AreaManager.getCuboid(ee.getDamager().getLocation());
                                if (dCub != null && dCub.getInfo() == CuboidInfo.SUMO) {
//                                    Ostrov.sync(() -> p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()), 2);
                                	e.setDamage(0d);
                                    return;//сумо
                                }
                            }

                        }
                    }
                default:
                    e.setCancelled(true);
                    //return;
            }
            
        } else {
            if (e.getEntityType()==EntityType.HUSK) {
                if (e instanceof EntityDamageByEntityEvent) {
                    final EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
                    if (ee.getDamager().getType() == EntityType.PLAYER) {
                    	final LobbyBot bt = BotManager.getBot(e.getEntity().getEntityId(), LobbyBot.class);
                    	if (bt != null) {
                    		e.setDamage(0d);
                    		//bt.remove(true);
                    		return;
                    	}
                        final Player dp = (Player) ee.getDamager();
                        e.setDamage(100d);
                        final LobbyPlayer lp = PM.getOplayer(dp, LobbyPlayer.class);
                        if (lp!=null) {
                        	QuestManager.addProgress(dp, lp, Quests.warrior);
                            dp.getWorld().spawnParticle(Particle.BLOCK_CRACK, ((LivingEntity) e.getEntity()).getEyeLocation(),
                                    40, 0.4d, 0.4d, 0.4d, 0d, Material.NETHER_WART_BLOCK.createBlockData(), false);
                        }
                    }
                } else {
					//Bukkit.broadcast(Component.text("c=" + e.getCause().toString()));
                	switch (e.getCause()) {
                	case VOID, CRAMMING, CUSTOM, SUFFOCATION:
                    	final LobbyBot bt = BotManager.getBot(e.getEntity().getEntityId(), LobbyBot.class);
                    	if (bt != null) {
                    		e.setDamage(0d);
                    		bt.remove();
                    		return;
                    	}
					default:
						break;
                	}
                }
            } else {
                e.setCancelled(true);
            }
           /* switch (e.getEntityType()) {
                case HUSK:
                    if (e instanceof EntityDamageByEntityEvent) {
                        final EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
                        if (ee.getDamager().getType() == EntityType.PLAYER) {
                            final Player dp = (Player) ee.getDamager();
                            e.setDamage(100d);
                            QuestManager.tryCompleteQuest(dp, Main.getLobbyPlayer(dp), Quest.KillMobs);
                            dp.getWorld().spawnParticle(Particle.BLOCK_CRACK, ((LivingEntity) e.getEntity()).getEyeLocation(),
                                    40, 0.4d, 0.4d, 0.4d, 0d, Material.NETHER_WART_BLOCK.createBlockData(), false);
                        }
                    }
                    break;
                default:
                    e.setCancelled(true);
                    break;
            }*/
        	
        }
                

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onScore(final ScoreWorldRecordEvent e) {
    	if (e.getScoreBoard().equals(race)) {
    		ApiOstrov.moneyChange(e.getName(), 20, "Состязание");
    	}
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityLoad(final EntitiesUnloadEvent e) {
    	for (final Entity en : e.getEntities()) {
			//Bukkit.broadcast(Component.text("u=" + en.getType().toString()));
        	final LobbyBot bt = BotManager.getBot(en.getEntityId(), LobbyBot.class);
        	if (bt != null) bt.remove();
    	}
    }



    @EventHandler (ignoreCancelled = true)
    public void onPlayerPickUpItem(final EntityPickupItemEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) return;
    }


   // @EventHandler(ignoreCancelled = true)
   // public void onPlayerSwapoffHand(PlayerSwapHandItemsEvent e) {
   //     if (!e.getPlayer().getWorld().getName().equals("world")) return;
   //     e.setCancelled(true);
  //  }
   
  
    @EventHandler  (ignoreCancelled = true)
    public void onHungerChange(final FoodLevelChangeEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) return;
        e.setCancelled(true); 
        ((Player)e.getEntity()).setFoodLevel(20);
    }
        

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    //@EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = true)
   // public void onMerge(final ItemMergeEvent e) {
    //    if (!e.getEntity().getWorld().getName().equals("world")) return;
    //    e.setCancelled(true);
   // }

   
  //  @EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = true)
   // public void onDespawn(final ItemDespawnEvent e) {
//log("onDespawn nb?"+e.getEntity().getWorld().getName().startsWith("newbie"));
        //if (!e.getEntity().getWorld().getName().equals("world")) return;
        //if (e.getEntity().getItemStack().getType()==Material.NETHER_STAR) {
        //    e.setCancelled(true);
        //}
   // }

   


    @EventHandler (ignoreCancelled = true)
    public void onBlockFade(final BlockFadeEvent e) {
        if (!e.getBlock().getWorld().getName().equals("world")) return;
        if( e.getBlock().getType() == Material.ICE || e.getBlock().getType() == Material.PACKED_ICE || e.getBlock().getType() == Material.SNOW || e.getBlock().getType() == Material.SNOW_BLOCK) 
        e.setCancelled(true);
    }


    
    
    @EventHandler (ignoreCancelled = true)
    public void onCreatureSpawnEvent(final CreatureSpawnEvent e) {
        if (e.getEntity().getWorld().getName().equals("world")) return;
        e.setCancelled(true);
    }
  
    
    @EventHandler (ignoreCancelled = true)
    public void onWeatherChange(final WeatherChangeEvent e) {
        if (!e.getWorld().getName().equals("world")) return;
        if (e.toWeatherState()) e.setCancelled(false);
    }


          
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockSpread(final BlockSpreadEvent e) {
        if (!e.getBlock().getWorld().getName().equals("world")) return;
        e.setCancelled(true);
    }  
        
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockGrowth(final BlockGrowEvent e) { 
        if (!e.getBlock().getWorld().getName().equals("world")) return;
        e.setCancelled(true);
    }    

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onStructureGrow(final StructureGrowEvent e) { 
        if (!e.getLocation().getWorld().getName().equals("world")) return;
        e.setCancelled(true);
    }    




















    
    
    //------------- ЭЛИТРЫ ------------------
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLaunch(final ProjectileLaunchEvent e) { //PlayerElytraBoostEvent !!!
        final Projectile prj = e.getEntity();
        if (prj.getShooter() instanceof Player && prj.getType() == EntityType.FIREWORK) {
            Ostrov.sync(()-> ((HumanEntity) prj.getShooter()).getInventory().setItem(2, Main.fw), 8);
//            prj.remove();
        }
    }
    
    /*@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLaunch(final PlayerElytraBoostEvent e) { //PlayerElytraBoostEvent !!!
    	final Player p = e.getPlayer();
    	final World w = p.getWorld();
    	final ItemStack fi = e.getItemStack();
    	final int ln;
    	if (fi.getItemMeta() instanceof final FireworkMeta fm) {
    		ln = (fm.getPower() << 2) + 16;
    	} else ln = 16;
		w.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1f);
    	new BukkitRunnable() {
    		int i = 0;
			@Override
			public void run() {
				if (p == null || !p.isValid()) {
					cancel();
					return;
				}
				
				if (i++ == ln || !p.isGliding()) {
					p.setGliding(true);
					final Firework fw = p.launchProjectile(Firework.class);
					fw.setItem(fi);
					fw.detonate();
					cancel();
					return;
				}
				
				final Location eye = p.getEyeLocation();
				w.spawnParticle(Particle.FIREWORKS_SPARK, eye, 2, 0d, 0d, 0d, 0d);
				p.setVelocity(p.getVelocity().add(eye.getDirection().multiply(0.14d)));
			}
		}.runTaskTimer(Ostrov.instance, 0, 2);
    }*/
    
    

    // ---------------------------------------- 


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	public boolean plcAtmpt(Block b, final BlockFace bf) {
		byte i = 0;
		while (b.getRelative(bf.getOppositeFace()).getType().isAir() && i < 10) {
			i++;
			b = b.getRelative(bf.getOppositeFace());
		}

		byte h = 1;
		byte w = 1;

		hh : while (h < 20) {
			switch (b.getRelative(BlockFace.UP, h).getType()) {
			case AIR:
			case CAVE_AIR:
			case FIRE:
				h++;
				break;
			default:
				break hh;
			}
		}

		ww : while (w < 20) {
			switch (b.getRelative(bf, w).getType()) {
			case AIR:
			case CAVE_AIR:
			case FIRE:
				w++;
				break;
			default:
				break ww;
			}
		}
		
		if (bf.getModX() == 0) {
			if (!ntAirMinBlck(b.getRelative(0, 0, 0), (byte) 2) || 
				!ntAirMinBlck(b.getRelative(0, h - 1, 0), (byte) 2) || 
				!ntAirMinBlck(b.getRelative(0, 0, w - 1), (byte) 2) || 
				!ntAirMinBlck(b.getRelative(0, h - 1, w - 1), (byte) 2)) {
				return false;
			}
			for (byte y = 1; y < h - 1; y++) {
				if (!ntAirMinBlck(b.getRelative(0, y, 0), (byte) 1) || 
					!ntAirMinBlck(b.getRelative(0, y, w - 1), (byte) 1)) {
					return false;
				}
			}
			for (byte xz = 1; xz < w - 1; xz++) {
				if (!ntAirMinBlck(b.getRelative(0, 0, xz), (byte) 1) || 
					!ntAirMinBlck(b.getRelative(0, h - 1, xz), (byte) 1)) {
					return false;
				}
			}
			if (w < 2 || h < 3) {
				return false;
			}
			final Block[] pbs = new Block[w * h];
			short j = 0;
			for (byte xz = 0; xz < w; xz++) {
				for (byte y = 0; y < h; y++) {
					final Block bl = b.getRelative(0, y, xz);
					switch (bl.getType()) {
					case AIR:
					case CAVE_AIR:
					case FIRE:
						pbs[j] = bl;
						j++;
						break;
					default:
						return false;
					}
				}
			}
			
			for (final Block pb : pbs) {
				pb.setType(Material.NETHER_PORTAL);
				final Orientable or = (Orientable) pb.getBlockData();
				or.setAxis(Axis.Z);
				pb.setBlockData(or);
			}
		} else {
			if (!ntAirMinBlck(b.getRelative(0, 0, 0), (byte) 3) || 
				!ntAirMinBlck(b.getRelative(0, h - 1, 0), (byte) 3) || 
				!ntAirMinBlck(b.getRelative(w - 1, 0, 0), (byte) 3) || 
				!ntAirMinBlck(b.getRelative(w - 1, h - 1, 0), (byte) 3)) {
				return false;
			}
			for (byte y = 1; y < h - 1; y++) {
				if (!ntAirMinBlck(b.getRelative(0, y, 0), (byte) 2) || 
					!ntAirMinBlck(b.getRelative(w - 1, y, 0), (byte) 2)) {
					return false;
				}
			}
			for (byte xz = 1; xz < w - 1; xz++) {
				if (!ntAirMinBlck(b.getRelative(xz, 0, 0), (byte) 2) || 
					!ntAirMinBlck(b.getRelative(xz, h - 1, 0), (byte) 2)) {
					return false;
				}
			}
			if (w < 2 || h < 3) {
				return false;
			}
			final Block[] pbs = new Block[w * h];
			short j = 0;
			for (byte xz = 0; xz < w; xz++) {
				for (byte y = 0; y < h; y++) {
					final Block bl = b.getRelative(xz, y, 0);
					switch (bl.getType()) {
					case AIR:
					case CAVE_AIR:
					case FIRE:
						pbs[j] = bl;
						j++;
						break;
					default:
						return false;
					}
				}
			}
			
			for (final Block pb : pbs) {
				pb.setType(Material.NETHER_PORTAL);
			}
		}
		return true;
	}

	public boolean ntAirMinBlck(final Block b, byte amt) {
		for (final BlockFace bf : NEAREST) {
			if (!b.getRelative(bf).getType().isAir()) {
				amt--;
			}
		}
		return amt <= 0;
	}
        
        
        
  
}
