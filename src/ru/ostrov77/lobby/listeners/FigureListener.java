package ru.ostrov77.lobby.listeners;

import java.util.Arrays;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.komiss77.events.FigureClickEvent;
import ru.komiss77.events.MissionEvent;
import ru.komiss77.events.PandoraUseEvent;
import ru.komiss77.modules.displays.DisplayManager;
import ru.komiss77.modules.player.PM;
import ru.komiss77.modules.world.BVec;
import ru.komiss77.objects.FigureAnswer;
import ru.ostrov77.lobby.LobbyFlag;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.CuboidInfo;
import ru.ostrov77.lobby.quest.Quests;

public class FigureListener implements Listener {

    private static final String tp = "       §e[§fЛКМ§e] - посетить§f       ";

    @EventHandler
    public void onFigureClick(final FigureClickEvent e) {
        final Player p = e.getPlayer();
//p.sendMessage("§8log: tag="+e.getFigure().getTag()+" left?"+e.isLeftClick());
        final LobbyPlayer lp = PM.getOplayer(p, LobbyPlayer.class);
        if (lp == null) {
            return;
        }
        if (e.getFigure().getTag().equals("info")) {
            final FigureAnswer fa = new FigureAnswer().vibration(true).beforeEyes(false);

            switch (e.getFigure().getEntityType()) {

                case ENDERMAN -> {
                    if (e.isRightClick()) {
                        fa.set(Arrays.asList(tp + "Добро пожаловать на §9Аркаим§f - режим, где тебе предстоит создать §9собственный мир§f, и управлять им, с халявным §9креативом§f! §eИграй один или с друзьями!" ))
                            .sound(Sound.ENTITY_ENDERMAN_AMBIENT);
                        Quests.agent.addProg(p, lp, "AR");
                    } else {
                        DisplayManager.rmvDis(p);
                        lp.transport(p, BVec.of(AreaManager.getCuboid(CuboidInfo.ARCAIM).getSpawnLocation(p.getWorld())), false);
                        return;
                    }
                }

                case PIGLIN_BRUTE -> {
                    if (e.isRightClick()) {
                        fa.set(Arrays.asList(tp + "Здравствуй, товарищ, и добро пожаловать в §cСове§f... точнее на §cМидгард§f - современный §cКлановый режим§f, где тебе и твоим друзьям предстоит построить §cимперию§f с нуля!"))
                            .sound(Sound.ENTITY_PIGLIN_BRUTE_AMBIENT);
                        Quests.agent.addProg(p, lp, "MI");
                    } else {
                        DisplayManager.rmvDis(p);
                        lp.transport(p, BVec.of(AreaManager.getCuboid(CuboidInfo.MIDGARD).getSpawnLocation(p.getWorld())), false);
                        return;
                    }
                }

                case ZOMBIE_VILLAGER -> {
                    if (e.isRightClick()) {
                        fa.set(Arrays.asList(tp + "Приветствую тебя, игрок, на режиме §aДаарии§f - ванильном выживании, с поддержкой всех последних обновлений §aМайнкрафта§f!"))
                            .sound(Sound.ENTITY_ZOMBIE_VILLAGER_AMBIENT);
                        Quests.agent.addProg(p, lp, "DA");
                    } else {
                        DisplayManager.rmvDis(p);
                        lp.transport(p, BVec.of(AreaManager.getCuboid(CuboidInfo.DAARIA).getSpawnLocation(p.getWorld())), false);
                    }
                }

                case VILLAGER -> {
                    if (lp.hasFlag(LobbyFlag.GinTravelDone)) {
                        if (e.isRightClick()) {
                            fa.set(Arrays.asList(tp + "§fРад видеть тебя снова!")).sound(Sound.ENTITY_VILLAGER_AMBIENT);
                            Quests.locman.complete(p, lp, false);
                        } else {
                            DisplayManager.rmvDis(p);
                            lp.transport(p, BVec.of(AreaManager.getCuboid(CuboidInfo.SPAWN).getSpawnLocation(p.getWorld())), false);
                            return;
                        }
                    } else {
                        fa.set(Arrays.asList("Мы наконец-то прибыли! Открой §e'Достижения' [Д]§f чтобы увидеть следующие задания. Кликни на лампу, и §6Джин§f мигом отвезет тебя на §6Спавн§f, либо просто прыгни за борт!"))
                            .sound(Sound.ENTITY_VILLAGER_AMBIENT);
                        Quests.locman.complete(p, lp, false);
                    }
                }

                case ZOMBIE -> {
                    if (e.isRightClick()) {
                        fa.set(Arrays.asList(tp + "Здесь режим §3Ван-Блок§f, на котором тебе предстоит построить §3островок§f буквально начиная только с §31 блоком§f! §3Скай-Блок§f, если что, дальше §3-->"))
                            .sound(Sound.ENTITY_ZOMBIE_AMBIENT);
                        Quests.agent.addProg(p, lp, "OB");
                    } else {
                        DisplayManager.rmvDis(p);
                        lp.transport(p, BVec.of(AreaManager.getCuboid(CuboidInfo.SKYWORLD).getSpawnLocation(p.getWorld())), false);
                        return;
                    }
                }

                case WITCH -> {
                    if (e.isRightClick()) {
                        fa.set(Arrays.asList(tp + "Приветствую, странник, приглашаю тебя на §3Скай-Блок§f - режим, заключенный в §3развити§f и §3прокачке§f острова, так сказать, из грязи в князи!"))
                            .sound(Sound.ENTITY_WITCH_AMBIENT);
                        Quests.agent.addProg(p, lp, "SW");
                    } else {
                        DisplayManager.rmvDis(p);
                        lp.transport(p, BVec.of(AreaManager.getCuboid(CuboidInfo.SKYWORLD).getSpawnLocation(p.getWorld())).add(0, -1, -20), false);
                        return;
                    }
                }

                case WANDERING_TRADER -> {
                    if (e.isRightClick()) {
                        fa.set(Arrays.asList(tp + "Привет, друг! Здесь, ты можешь §eразвлечься§f в ассортименте разных мини-игр, таких как §eКонтра§f, §eБилд Баттл§f, §eПрятки§f, и т.д., как и сам, так и со своими друзьями!"))
                            .sound(Sound.ENTITY_WANDERING_TRADER_AMBIENT);
                        Quests.agent.addProg(p, lp, "NM");
                    } else {
                        DisplayManager.rmvDis(p);
                        lp.transport(p, BVec.of(AreaManager.getCuboid(CuboidInfo.NOPVP).getSpawnLocation(p.getWorld())), false);
                        return;
                    }
                }

                case SNOW_GOLEM -> {
                    if (e.isRightClick()) {
                        fa.set(Arrays.asList(tp + "Бурлит §bадреналин§f в крови? Тогда ты по адресу, ведь на §bПаркурах§f, у тебя есть выбор среди §b60+ карт§f, все из них с интересными темами и прыжками!"))
                            .sound(Sound.BLOCK_SNOW_BREAK);
                        Quests.agent.addProg(p, lp, "PA");
                    } else {
                        DisplayManager.rmvDis(p);
                        lp.transport(p, BVec.of(AreaManager.getCuboid(CuboidInfo.PARKUR).getSpawnLocation(p.getWorld())), false);
                        return;
                    }
                }

                case WITHER_SKELETON -> {
                    if (e.isRightClick()) {
                        fa.set(Arrays.asList(tp + "Сможешь ли ты показать свой скилл на поле боя? Выбери §6PVP§f мини-игру на свой вкус (§6Кит-ПВП§f, §6Скай Варс§f, §6Зомби§f, и т.д.) и отжигай местную фауну с друзьями!"))
                            .sound(Sound.ENTITY_WITHER_SKELETON_AMBIENT);
                        Quests.agent.addProg(p, lp, "PM");
                    } else {
                        DisplayManager.rmvDis(p);
                        lp.transport(p, BVec.of(AreaManager.getCuboid(CuboidInfo.PVP).getSpawnLocation(p.getWorld())), false);
                        return;
                    }
                }

                case ZOMBIFIED_PIGLIN -> {
                    if (e.isRightClick()) {
                        fa.set(Arrays.asList(tp + "Здравствуй, §4скиталец§f. Думаешь что обычные мобы тебе больше не помеха? Тогда залетай на §4Хардкор-РПГ §fмиры Седны, выбирай класс, и покажи насколько ты годен в гуще §4кровавого §fзамеса!"))
                            .sound(Sound.ENTITY_ZOMBIFIED_PIGLIN_AMBIENT);
                        Quests.agent.addProg(p, lp, "SE");
                    } else {
                        DisplayManager.rmvDis(p);
                        lp.transport(p, BVec.of(AreaManager.getCuboid(CuboidInfo.SEDNA).getSpawnLocation(p.getWorld())), false);
//					p.teleport(AreaManager.getCuboid(CuboidInfo.SEDNA).getSpawnLocation(p.getWorld())), false;
                        return;
                    }
                }

                case CREEPER -> {
                    fa.set(Arrays.asList("Приветствую, §eпутник§f, и добро пожаловать на §3Архипелаг§f - лобби, где на каждом острове ты найдешь интересные и уникальные §6Большие Режимы§f, или §eМини-Игры§f для тебя и твоих друзей! §eИсследуй весь остров!"))
                        .sound(Sound.ENTITY_CREEPER_HURT);
                    Quests.agent.addProg(p, lp, "LB");
                }

            }

            e.setAnswer(fa);

        } else if (e.getFigure().getTag().equals("mid") && e.getFigure().getEntityType() == EntityType.PIGLIN) {
            final FigureAnswer fa = new FigureAnswer().vibration(true)
                .beforeEyes(false).sound(Sound.ENTITY_PIGLIN_AMBIENT);

            p.playSound(e.getFigure().getEntity().getLocation(), Sound.ENTITY_PIGLIN_AMBIENT, 1f, 1f);
            switch (e.getFigure().getName().charAt(4)) {
                case 'K':
                    if (Quests.gold.addProg(p, lp, "К")) {
                        fa.set(Arrays.asList("Приветствую! Налог §65§f золотых говорите? ну забирайте..."));
                        p.sendMessage("[§cКузнец§f] §6+5⛃");
                    } else {
                        fa.set(Arrays.asList("Я за сегодня 3 топора выковал!"));
                    }
                    break;
                case 'М':
                    if (Quests.gold.addProg(p, lp, "М")) {
                        fa.set(Arrays.asList("Ну что, опять налоги собираете? Только сегодня последние §62§f собрал... держите..."));
                        p.sendMessage("[§cМясник§f] §6+5⛃");
                    } else {
                        fa.set(Arrays.asList("Главное чтоб мясо не протухло!"));
                    }
                    break;
                case 'Ф':
                    if (Quests.gold.addProg(p, lp, "Ф")) {
                        fa.set(Arrays.asList("Здрасть, товарищ! §65§f? У меня столько нету... Вот §63§f, по факту все что есть..."));
                        p.sendMessage("[§cФермер§f] §6+3⛃");
                    } else {
                        fa.set(Arrays.asList("Сегодня впервые за месяц спеку хлеб!"));
                    }
                    break;
            }
            e.setAnswer(fa);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMission(final MissionEvent e) {
        if (e.action != MissionEvent.MissionAction.Accept) {
            return;
        }
        Quests.mission.complete(e.getPlayer(), PM.getOplayer(e.getPlayer()), false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPandoraUse(final PandoraUseEvent e) {
        Quests.pandora.complete(e.getPlayer(), PM.getOplayer(e.getPlayer()), false);
    }

}


/* if (e.getFigure().getTag().equals("ship")) {
            if (e.isLeftClick()) {
                e.setAnswer((new FigureAnswer()).add("§cНе бейте, а гладьте))", (player) -> {
                    LCuboid lc = lp.getCuboid();
                    p.sendMessage(lc == null ? "ты не в кубоиде" : "ты в кубоиде:" + lc.displayName);
                }));
            } else {
                FigureAnswer answer = new FigureAnswer()
                        //.add("§eПривет!", (player) -> {player.sendMessage("eПривет");} )
                        //.add("§aТы тут новенький?", (player) -> {player.sendMessage("новенький");} )
                        //.add("§bНа корабле есть §lособенная лампа §r§b,потри её.", (player) -> {player.sendMessage("особенная лампа");} )
                       // .add(ItemType.SOUL_LANTERN, (player) -> {player.sendMessage("pickup SOUL_LANTERN");} )
                        //.add("§6Ну или прыгай за борт.", (player) -> {player.sendMessage("прыгай");} )
                        .add("§eПривет!" )
                        .add("§aТы тут новенький?")
                        .add("§bНа корабле есть §lособенная лампа §r§b,потри её.")
                        .add(ItemType.SOUL_LANTERN)
                        .add("§6Ну или прыгай за борт.")
                        .time(15).vibration().sound(Sound.ENTITY_VILLAGER_TRADE)
                        .beforeEyes()
                        ;
                e.setAnswer(answer);
                QuestManager.tryCompleteQuest(p, lp, Quest.SpeakWithNPC);
            }
            
        } else */
