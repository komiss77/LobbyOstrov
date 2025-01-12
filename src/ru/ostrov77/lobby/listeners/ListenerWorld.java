package ru.ostrov77.lobby.listeners;

import java.util.Set;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.Orientable;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.Timer;
import ru.komiss77.enums.StatFlag;
import ru.komiss77.events.LocalDataLoadEvent;
import ru.komiss77.events.QuestCompleteEvent;
import ru.komiss77.events.ScoreWorldRecordEvent;
import ru.komiss77.modules.bots.BotManager;
import ru.komiss77.modules.bots.Botter;
import ru.komiss77.modules.player.PM;
import ru.komiss77.modules.quests.Quest;
import ru.komiss77.modules.quests.QuestManager;
import ru.komiss77.modules.world.WXYZ;
import ru.komiss77.modules.world.XYZ;
import ru.komiss77.utils.*;
import ru.ostrov77.lobby.JinGoal;
import ru.ostrov77.lobby.LobbyFlag;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.ChunkContent;
import ru.ostrov77.lobby.area.CuboidInfo;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.bots.SpotManager;
import ru.ostrov77.lobby.bots.spots.Spot;
import ru.ostrov77.lobby.bots.spots.SpotType;
import ru.ostrov77.lobby.event.CuboidEvent;
import ru.ostrov77.lobby.game.RaceBoard;
import ru.ostrov77.lobby.game.SumoBoard;
import ru.ostrov77.lobby.quest.Quests;

public class ListenerWorld implements Listener {

    private static final BlockFace[] NEAREST = {BlockFace.DOWN, BlockFace.UP, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST};
    private static final RaceBoard race = new RaceBoard("§б§nСостязание", new WXYZ(Main.getLocation(Main.LocType.raceLoc)));
    private static final SumoBoard sumo = new SumoBoard("§c§nАрена Сумо", new WXYZ(Main.getLocation(Main.LocType.sumoLoc)));


     @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLaunch(final ProjectileLaunchEvent e) { //PlayerElytraBoostEvent !!!
        final Projectile prj = e.getEntity();
        if (prj.getShooter() instanceof final Player p && prj.getType() == EntityType.FIREWORK_ROCKET) {
            Ostrov.sync( ()-> Main.rocket.give(p), 8);
            //Ostrov.sync( ()-> Main.rocket.give(p), 8);
//            prj.remove();
        }
    }
    
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLocalData(final LocalDataLoadEvent e) {
        if (e.getOplayer() instanceof final LobbyPlayer lp) { //создается в острове
            final Player p = e.getPlayer();

            lp.setFlag(StatFlag.NewBieDone, true);

            final String flags = lp.mysqlData.get("flags");
            lp.setFlags(flags == null || flags.isEmpty() ? 0 : Integer.parseInt(flags));
            final String areas = lp.mysqlData.get("area");
            lp.setOpenedArea(areas == null || areas.isEmpty() ? 0 : Integer.parseInt(areas));

            //c флагом JustGame можно придти сразу, а можно получить после прыжка за борт | ??? зачем
            if (lp.isGuest) {

                Main.giveItems(p); //там justGame получат всё
                p.teleport(Main.getLocation(Main.LocType.spawn));
//                e.setLogoutLocation(Main.getLocation(Main.LocType.spawn));
                //lp.tag ("§8(","§8", "§8) §7"+lp.getDataString(Data.FAMILY));
                p.performCommand("menu");
                
            } else if (QuestManager.isComplete(lp, Quests.ostrov)) {

                Main.giveItems(p);
                //без тп, появиться, где вышел

            } else {
//                e.setLogoutLocation(Main.getLocation(Main.LocType.newBieSpawn));
                p.teleport(Main.getLocation(Main.LocType.newBieSpawn));
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 32, 2));
                p.setCollidable(false);
                Main.oscom.give(p);
                lp.hideScore();
                ScreenUtil.sendBossbar(p, "#3 Остров.", 5, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);

            }
        }
    }

    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityPortalEnter(final EntityPortalEnterEvent e) {

        if (e.getEntityType() == EntityType.PLAYER) {

            if (e.getEntity().getTicksLived() < 100 || Timer.has(e.getEntity().getEntityId())) {
                return;
            }
            final Player p = (Player) e.getEntity();
            Timer.add(p.getEntityId(), 5);

            for (final XYZ xyzw : Main.serverPortals.keySet()) {
                if (xyzw.nearly(e.getLocation(), 16)) {
                    p.performCommand("server " + Main.serverPortals.get(xyzw));
                }
            }
        } else {
            final Botter bt = BotManager.getBot(e.getEntity().getEntityId());
            if (bt != null) {
                bt.remove();
            }
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public static void onQuest(final QuestCompleteEvent e) {
        final LobbyPlayer lp = PM.getOplayer(e.getPlayer(), LobbyPlayer.class);
        if (e.getQuest().equals(Quests.doctor)) {
            Main.elytra.give(e.getPlayer());
            new CuboidEvent(e.getPlayer(), lp, lp.getCuboid(), lp.getCuboid(), lp.cuboidEntryTime).callEvent();
            return;
        }

        for (final Quest qs : QuestManager.getQuests(q -> !q.equals(Quests.doctor))) {
            if (!QuestManager.isComplete(lp, qs)) {
                return;
            }
        }

        QuestManager.complete(e.getPlayer(), lp, Quests.doctor);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onInvClose(final InventoryCloseEvent e) {
        if (e.getInventory().getType() == InventoryType.CHEST) {
            final LobbyPlayer lp = PM.getOplayer(e.getPlayer(), LobbyPlayer.class);
            if (PM.getPasportFillPercent(lp) > 20) {
                QuestManager.complete((Player) e.getPlayer(), lp, Quests.pass);
            }
        }
    }

    //нописание в actionbar куда человек зашел + квест на гонку для ПВЕ мини-игр
    @EventHandler
    public static void onCuboidEvent(final CuboidEvent e) {  //если лоббиплеер нуль, то сюда никогда не придёт

        final Player p = e.getPlayer();
//Ostrov.log("onCuboidEvent getLast="+e.getLast());

        if (e.getLast() != null) {
            if (e.getLast().getInfo().hidePlayers) { //выход из кубоида со скрытием //if (e.getPrevois()!=null && e.getPrevois().getName().equals("newbie") ) {
                for (final Player cp : e.getLast().getCuboidPlayers()) {
                    if (!cp.getName().equals(p.getName())) {
                        cp.showPlayer(Main.instance, p);
                        p.showPlayer(Main.instance, cp);
                    }
                }
            }
            switch (e.getLast().getName()) {
                case "daaria", "skyworld", "sumo" -> {
                    if (QuestManager.isComplete(e.getLobbyPlayer(), Quests.doctor)) {
                        Main.rocket.give(p);
                    } else {
                        p.getInventory().setItem(2, ItemUtil.air);
                    }
                }
            }
        }

        if (e.getCurrent() == null) {

            ScreenUtil.sendActionBarDirect(p, "§7§l⟣ §3§lАрхипелаг §7§l⟢");

        } else {

            if (e.getCurrent().getInfo().hidePlayers) {//вход в со скрытием //if (e.getCurrent()!=null && e.getCurrent().getName().equals("newbie")) { 
                for (final Player cp : e.getCurrent().getCuboidPlayers()) {
                    if (!cp.getName().equals(p.getName())) {
                        cp.hidePlayer(Main.instance, p);
                        p.hidePlayer(Main.instance, cp);
                    }
                }
            }

            final LobbyPlayer lp = e.getLobbyPlayer();

            ScreenUtil.sendActionBarDirect(p, "§7§l⟣ " + e.getCurrent().displayName + " §7§l⟢");

            if (!lp.isAreaDiscovered(e.getCurrent().id)) {
                onNewAreaDiscover(p, lp, e.getCurrent()); //новичёк или нет - обработается внутри
            }

            if (!lp.hasFlag(LobbyFlag.GinTravelDone)) {
                return; //далее - не прошедших джина не обрабатываем
            }
            switch (e.getCurrent().getName()) {

                case "start" -> {
                    if (lp.isAreaDiscovered(AreaManager.getCuboid("nopvp").id)) {
                        p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 2f);
                        p.sendMessage("§5[§eСостязание§5] §7>> На старт! Внимание! Вперед!");
                        lp.raceTime = 0;
                        Main.elytra.remove(p);
                    } else {
                        p.sendMessage("§5[§eСостязание§5] §7>> Найдите §eОазис§7 перед началом!");
                    }
                }

                case "end" -> {
                    if (lp.raceTime > 0) {
                        p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1f, 2f);
                        p.sendMessage("§5[§eСостязание§5] §7>> Хорошо сработано! Время: §e" + TimeUtil.secondToTime(lp.raceTime));
                        QuestManager.complete(p, e.getLobbyPlayer(), Quests.race);
                        race.tryAdd(p.getName(), lp.raceTime);
                        lp.raceTime = -1;
                        Main.elytra.give(p);
                        //lp.questDone(p, quest, true);
                    }
                }

                case "daaria", "skyworld" -> Main.pickaxe.give(p);

                case "sumo" -> Main.stick.give(p);

            }

        }

    }

    //SYNC !!!
    private static void onNewAreaDiscover(final Player p, final LobbyPlayer lp, final LCuboid cuboid) {
        lp.setAreaDiscovered(cuboid.id);

        final CuboidInfo ci = cuboid.getInfo();
        lp.setAreaDiscovered(cuboid.id);
        if (QuestManager.complete(p, lp, ci.quest)) {
            QuestManager.addProgress(p, lp, Quests.discover);
        }

        if (ci.quest.equals(Quests.ostrov)) {
            lp.setFlag(LobbyFlag.GinTravelDone, true); //попал на спавн - считаем что ждин пройден
        }

        if (QuestManager.isComplete(lp, Quests.discover)) {
            QuestManager.complete(p, lp, Quests.navig);
            Main.pipboy.give(p);
        }

        if (lp.target == cuboid.getInfo()) {
            AreaManager.resetCompassTarget(p, lp);
        }

        if (!ci.hidePlayers) {
            ScreenUtil.sendBossbar(p, "Открыта Локация: " + cuboid.displayName,
                7, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS);
            sound(p);
        }
    }

    public static void sound(final Player p) {
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2, 0.5f);
        Ostrov.async(() -> {
            if (p.isOnline()) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 0.5f);
            }
        }, 5);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDismount(final EntityDismountEvent e) {
        if (e.getEntityType() == EntityType.PLAYER && e.getDismounted().getType() == EntityType.BLAZE) {
            if (e.getDismounted().isCustomNameVisible() && JinGoal.GIN_NAME.equals(TCUtil.deform(e.getDismounted().customName()))) {
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
        if (!e.getPlayer().getWorld().getName().equals("world")) {
            return;
        }
        if (!ApiOstrov.isLocalBuilder(e.getPlayer())) {
            e.setCancelled(true);
        }

        final Block b = e.getBlockPlaced();
        final Location loc;

        switch (b.getType()) {

            case FIRE -> {
                if (plcAtmpt(b, BlockFace.EAST) || plcAtmpt(b, BlockFace.SOUTH)) {
                    final ItemStack it = e.getItemInHand();
                    if (ItemUtil.isBlank(it, true)) {
                        final String servername = ((TextComponent) it.getItemMeta().displayName()).content();
                        Main.serverPortals.put(new XYZ(b.getLocation()), servername);
                        Main.savePortals();
                    }
                }
            }

            case HEAVY_WEIGHTED_PRESSURE_PLATE -> {
                final Player p = e.getPlayer();
                loc = b.getLocation();

                if (p.hasMetadata("tp")) {
                    final XYZ firstPlateXYZ = (XYZ) p.getMetadata("tp").getFirst().value();
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
            }

            case BEDROCK -> {
                Main.loadPortals();
                e.getPlayer().sendMessage("§eПерезагружено!");
            }

            case DEAD_BRAIN_CORAL -> {
                loc = b.getLocation();
                SpotManager.addSpot(new XYZ(loc), SpotType.SPAWN);
            }
            case DEAD_TUBE_CORAL -> {
                loc = b.getLocation();
                SpotManager.addSpot(new XYZ(loc), SpotType.WALK);
            }
            case DEAD_FIRE_CORAL -> {
                loc = b.getLocation();
                SpotManager.addSpot(new XYZ(loc), SpotType.END);
            }
            case DEAD_BUBBLE_CORAL -> {
                final Spot sp = SpotManager.getRndSpot(SpotType.SPAWN);
                if (sp != null) {
                    final int pls = Bukkit.getOnlinePlayers().size();
                    if (pls != 0) {
                        loc = b.getLocation().toCenterLocation();
                        final Botter bt = BotManager.createBot(ClassUtil.rndElmt(SpotManager.names),
                            loc.getWorld(), SpotManager.BOT_EXT);
                        bt.telespawn(null, loc);
                    }
                }
            }
            default -> {
            }

        }

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(final BlockBreakEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("world")) {
            return;
        }
        if (!ApiOstrov.isLocalBuilder(e.getPlayer())) {
            e.setCancelled(true);
        }
        final Location loc = e.getBlock().getLocation();

        switch (e.getBlock().getType()) {
            case NETHER_PORTAL -> {
                if (!Main.serverPortals.isEmpty()) {
                    XYZ find = null;
                    for (final XYZ xyzw : Main.serverPortals.keySet()) {
                        if (xyzw.nearly(loc, 16)) {
                            find = xyzw;
                            break;
                        }
                    }
                    if (find != null) {
                        Main.serverPortals.remove(find);
                        Main.savePortals();
                    }
                }
            }
            case LIGHT_WEIGHTED_PRESSURE_PLATE -> {
                final ChunkContent cc = AreaManager.getChunkContent(loc);
                if (cc != null && cc.hasPlate()) {
                    final XYZ second = cc.getPlate(loc);
                    if (second != null) { //пункт назначения назначен - значит плата есть
                        cc.delPlate(loc);
                        AreaManager.savePlate(new XYZ(loc), null);
                        e.getPlayer().sendMessage("§6Плита, ведущаяя к §a" + second + " §6убрана!");
                    }
                }
            }
            case DEAD_HORN_CORAL ->
                SpotManager.deleteSpot(new XYZ(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            case DEAD_BUBBLE_CORAL ->
                BotManager.clearBots();
            default -> {
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingBreakByEntityEvent(final HangingBreakByEntityEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) {
            return;
        }
        if (e.getRemover().getType() == EntityType.PLAYER && PM.exist(e.getEntity().getName())) {
            if (!ApiOstrov.isLocalBuilder(e.getRemover())) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingBreakEvent(final HangingBreakEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) {
            return;
        }
        if (e.getEntity().getType() == EntityType.PLAYER) {
            if (!ApiOstrov.isLocalBuilder(e.getEntity())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onPlayerItemFrameChangeEvent(final PlayerItemFrameChangeEvent e) {
        e.setCancelled(!ApiOstrov.isLocalBuilder(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onPlayerInteractAtEntityEvent(final PlayerInteractAtEntityEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("world")) {
            return;
        }
        final Player p = e.getPlayer();
        switch (e.getRightClicked().getType()) {
            case ARMOR_STAND, ITEM_FRAME, GLOW_ITEM_FRAME ->
                e.setCancelled(!ApiOstrov.isLocalBuilder(p));
            case PLAYER -> {
                final LobbyPlayer lp = PM.getOplayer(p, LobbyPlayer.class);
                if (lp == null) {
                    return;
                }
                final LobbyPlayer olp = PM.getOplayer(e.getRightClicked().getUniqueId(), LobbyPlayer.class);
//p.sendMessage("§8log: ПКМ на игрока, новичёк?"+!clickedLp.questDone.contains(Quest.DiscoverAllArea));
                if (olp != null && (olp.isGuest || QuestManager.isComplete(olp, Quests.discover))) {
                    QuestManager.complete(p, lp, Quests.greet);
                }
            }
            default -> {
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void PlayerArmorStandManipulateEvent(final PlayerArmorStandManipulateEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("world")) {
            return;
        }

        if (!ApiOstrov.isLocalBuilder(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onEntityDamage(final EntityDamageEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) {
            return;
        }

        if (e.getEntityType() == EntityType.PLAYER && PM.exist(e.getEntity().getName())) {
            final Player p = (Player) e.getEntity();
            final LobbyPlayer lp = PM.getOplayer(p, LobbyPlayer.class);
            final LivingEntity de;
            switch (e.getCause()) {

                case VOID:
                    e.setDamage(0d);
                    e.setCancelled(true);
                    Ostrov.sync(() -> {
                        if (QuestManager.isComplete(lp, Quests.ostrov)) {//старичков кидаем на спавн
                            final Location loc = p.getLocation();
                            final LCuboid lc = AreaManager.getCuboid(new XYZ(loc.getWorld().getName(), loc.getBlockX(), 80, loc.getBlockZ()));
                            p.teleport(lc == null ? Main.getLocation(Main.LocType.spawn) : lc.spawnPoint, PlayerTeleportEvent.TeleportCause.COMMAND);
                        } else {
                            p.teleport(Main.getLocation(Main.LocType.newBieArrive), PlayerTeleportEvent.TeleportCause.COMMAND);
                            p.performCommand("menu");
                            Main.giveItems(p);
                        }
                    }, 1);

                    de = EntityUtil.lastDamager(p, false);
                    if (de instanceof final Player dp) {
                        final LobbyPlayer olp = PM.getOplayer(dp, LobbyPlayer.class);
                        if (olp != null) {
                            final Integer wns = sumo.amount(olp.nik);
                            olp.sumoWins = wns == null || wns < olp.sumoWins ? olp.sumoWins + 1 : wns + 1;
                            sumo.tryAdd(olp.nik, olp.sumoWins);
                            p.damage(1d, DamageSource.builder(DamageType.MAGIC).withCausingEntity(p).withDirectEntity(p).build());
                            QuestManager.complete(dp, olp, Quests.sumo);
                            for (final Player pl : dp.getWorld().getPlayers()) {
                                pl.sendMessage("§7[§cСумо§7] Игрок §a" + dp.getName() + "§7 скинул §c" + p.getName() + "§7 с арены!");
                            }
                        }
                    }
                    return;
                case FALL:
                    de = EntityUtil.lastDamager(p, false);
                    if (de instanceof final Player dp) {
                        final LobbyPlayer olp = PM.getOplayer(dp, LobbyPlayer.class);
                        if (olp != null) {
                            final Integer wns = sumo.amount(olp.nik);
                            olp.sumoWins = wns == null || wns < olp.sumoWins ? olp.sumoWins + 1 : wns + 1;
                            sumo.tryAdd(olp.nik, olp.sumoWins);
                            p.damage(1d, DamageSource.builder(DamageType.MAGIC).withCausingEntity(p).withDirectEntity(p).build());
                            p.teleport(AreaManager.getCuboid(CuboidInfo.SUMO).spawnPoint, PlayerTeleportEvent.TeleportCause.COMMAND);
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
                    p.teleport(Main.getLocation(Main.LocType.spawn));
                    p.sendMessage("§6[§eОстров§6] §eТебя зажала стена и переместила обратно на спавн!");
                    e.setCancelled(false);
                    return;

                case ENTITY_ATTACK: //ентити ударяет
                    if (e instanceof final EntityDamageByEntityEvent ee) {
                        if (ee.getDamager().getType() == EntityType.PLAYER) {
                            final LCuboid vCub = AreaManager.getCuboid(e.getEntity().getLocation());
                            if (vCub != null && vCub.getInfo() == CuboidInfo.SUMO) {
                                final LCuboid dCub = AreaManager.getCuboid(ee.getDamager().getLocation());
                                if (dCub != null && dCub.getInfo() == CuboidInfo.SUMO) {
                                    e.setDamage(0d);
                                    return;//сумо
                                }
                            }

                        }
                    }
                default:
                    e.setCancelled(true);
            }

        } else {
            if (e.getEntityType() == EntityType.HUSK) {
                if (e instanceof final EntityDamageByEntityEvent ee) {
                    if (ee.getDamager().getType() == EntityType.PLAYER) {
                        final Player dp = (Player) ee.getDamager();
                        e.setDamage(100d);
                        QuestManager.addProgress(dp, PM.getOplayer(dp), Quests.warrior);
                        dp.getWorld().spawnParticle(Particle.BLOCK, ((LivingEntity) e.getEntity()).getEyeLocation(),
                                40, 0.4d, 0.4d, 0.4d, 0d, BlockType.NETHER_WART_BLOCK.createBlockData(), false);
                    }
                } else {
                    switch (e.getCause()) {
                        case VOID, CRAMMING, CUSTOM, SUFFOCATION:
                            final Botter bt = BotManager.getBot(e.getEntity().getEntityId());
                            if (bt != null) {
                                e.setDamage(0d);
                                bt.remove();
                            }
                        default:
                            break;
                    }
                }
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onScore(final ScoreWorldRecordEvent e) {
        if (e.getScoreDis().equals(race)) {
            ApiOstrov.moneyChange(e.getName(), 20, "Состязание");
        }
    }

    /*@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityLoad(final EntitiesUnloadEvent e) {
        for (final Entity en : e.getEntities()) {
            //Bukkit.broadcast(Component.text("u=" + en.getType().toString()));
            //final LobbyBot bt = BotManager.getBot(en.getEntityId(), LobbyBot.class);
            final Botter bt = BotManager.getBot(en.getEntityId());
            if (bt != null) {
                bt.remove();
            }
        }
    }*/


    @EventHandler(ignoreCancelled = true)
    public void onHungerChange(final FoodLevelChangeEvent e) {
        if (!e.getEntity().getWorld().getName().equals("world")) {
            return;
        }
        e.setCancelled(true);
        e.getEntity().setFoodLevel(20);
    }

    private static final Set<BlockType> SNOWY = Set.of(BlockType.ICE, BlockType.PACKED_ICE, BlockType.SNOW, BlockType.SNOW_BLOCK);
    @EventHandler(ignoreCancelled = true)
    public void onBlockFade(final BlockFadeEvent e) {
        if (!e.getBlock().getWorld().getName().equals("world")) return;
        if (SNOWY.contains(e.getBlock().getType().asBlockType())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawnEvent(final CreatureSpawnEvent e) {
        if (e.getEntity().getWorld().getName().equals("world")) return;
        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onWeatherChange(final WeatherChangeEvent e) {
        if (!e.getWorld().getName().equals("world")) {
            return;
        }
        if (e.toWeatherState()) {
            e.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockSpread(final BlockSpreadEvent e) {
        if (!e.getBlock().getWorld().getName().equals("world")) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockGrowth(final BlockGrowEvent e) {
        if (!e.getBlock().getWorld().getName().equals("world")) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onStructureGrow(final StructureGrowEvent e) {
        if (!e.getLocation().getWorld().getName().equals("world")) {
            return;
        }
        e.setCancelled(true);
    }
  
    
    
    
    public boolean plcAtmpt(Block b, final BlockFace bf) {
        byte i = 0;
        while (b.getRelative(bf.getOppositeFace()).getType().isAir() && i < 10) {
            i++;
            b = b.getRelative(bf.getOppositeFace());
        }

        byte h = 1;
        byte w = 1;

        hh:
        while (h < 20) {
            switch (b.getRelative(BlockFace.UP, h).getType()) {
                case AIR, CAVE_AIR, FIRE -> h++;
                default -> {
                    break hh;
                }
            }
        }

        ww:
        while (w < 20) {
            switch (b.getRelative(bf, w).getType()) {
                case AIR, CAVE_AIR, FIRE -> w++;
                default -> {
                    break ww;
                }
            }
        }

        final Orientable or = BlockType.NETHER_PORTAL.createBlockData();
        if (bf.getModX() == 0) {
            if (!ntAirMinBlck(b.getRelative(0, 0, 0), (byte) 2)
                    || !ntAirMinBlck(b.getRelative(0, h - 1, 0), (byte) 2)
                    || !ntAirMinBlck(b.getRelative(0, 0, w - 1), (byte) 2)
                    || !ntAirMinBlck(b.getRelative(0, h - 1, w - 1), (byte) 2)) {
                return false;
            }
            for (byte y = 1; y < h - 1; y++) {
                if (!ntAirMinBlck(b.getRelative(0, y, 0), (byte) 1)
                        || !ntAirMinBlck(b.getRelative(0, y, w - 1), (byte) 1)) {
                    return false;
                }
            }
            for (byte xz = 1; xz < w - 1; xz++) {
                if (!ntAirMinBlck(b.getRelative(0, 0, xz), (byte) 1)
                        || !ntAirMinBlck(b.getRelative(0, h - 1, xz), (byte) 1)) {
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
                        case AIR, CAVE_AIR, FIRE -> {
                            pbs[j] = bl;
                            j++;
                        }
                        default -> {
                            return false;
                        }
                    }
                }
            }

            or.setAxis(Axis.Z);
            for (final Block pb : pbs) {
                pb.setBlockData(or, false);
            }
        } else {
            if (!ntAirMinBlck(b.getRelative(0, 0, 0), (byte) 3)
                    || !ntAirMinBlck(b.getRelative(0, h - 1, 0), (byte) 3)
                    || !ntAirMinBlck(b.getRelative(w - 1, 0, 0), (byte) 3)
                    || !ntAirMinBlck(b.getRelative(w - 1, h - 1, 0), (byte) 3)) {
                return false;
            }
            for (byte y = 1; y < h - 1; y++) {
                if (!ntAirMinBlck(b.getRelative(0, y, 0), (byte) 2)
                        || !ntAirMinBlck(b.getRelative(w - 1, y, 0), (byte) 2)) {
                    return false;
                }
            }
            for (byte xz = 1; xz < w - 1; xz++) {
                if (!ntAirMinBlck(b.getRelative(xz, 0, 0), (byte) 2)
                        || !ntAirMinBlck(b.getRelative(xz, h - 1, 0), (byte) 2)) {
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
                        case AIR, CAVE_AIR, FIRE -> {
                            pbs[j] = bl;
                            j++;
                        }
                        default -> {
                            return false;
                        }
                    }
                }
            }

            for (final Block pb : pbs) {
                pb.setBlockData(or, false);
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