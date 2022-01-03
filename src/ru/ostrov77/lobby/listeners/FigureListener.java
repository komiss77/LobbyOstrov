package ru.ostrov77.lobby.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.komiss77.Ostrov;
import ru.komiss77.enums.Data;
import ru.komiss77.events.FigureClickEvent;
import ru.komiss77.events.MissionEvent;
import ru.komiss77.events.PandoraUseEvent;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.objects.FigureAnswer;
import ru.ostrov77.lobby.LobbyFlag;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.quest.Quest;
import ru.ostrov77.lobby.quest.QuestManager;





public class FigureListener implements Listener {
    
    
    @EventHandler
    public void onFigureClick(final FigureClickEvent e) {
        final Player p = e.getPlayer();
//p.sendMessage("§8log: tag="+e.getFigure().getTag()+" left?"+e.isLeftClick());
        final LobbyPlayer lp = Main.getLobbyPlayer(p);
        
        if (e.getFigure().getTag().equals("info")) {
            final FigureAnswer fa = new FigureAnswer().vibration(true).beforeEyes(true);
            
            switch (e.getFigure().getEntityType()) {
                
    		case ENDERMAN:
                    fa.set(Arrays.asList("Добро пожаловать на §9Аркаим§f,", "На этом режиме вы сможете", "проявить свою полную фантазию", "благодаря халявному §9креативу§f!", "§eУдачного строительства!"))
                        .time(10).sound(Sound.ENTITY_ENDERMAN_AMBIENT);
                    onSpeak(p, lp, LobbyFlag.TalkAR);
                    break;
                    
    		case PIGLIN_BRUTE:
                    fa.set(Arrays.asList("Здравствуй, товарищ, и добро", "пожаловать в §cСове§f... точнее", "на §cМидгард§f - режим кланов и", "воинств, где тебе и твоим друзьям", "предстоит построить §cимперию§f с нуля!", "§eУдачных завоеваний!"))
                        .time(10).sound(Sound.ENTITY_PIGLIN_BRUTE_AMBIENT);
                    onSpeak(p, lp, LobbyFlag.TalkMI);
                    break;
                    
    		case ZOMBIE_VILLAGER:
                    fa.set(Arrays.asList("Приветствую тебя, игрок, на", "режиме §aДаария§f - ванильном", "выживании, с поддержкой всех", "последних обновлений §aМайнкрафта§f!", "§eСчастливых похождений!"))
                        .time(10).sound(Sound.ENTITY_ZOMBIE_VILLAGER_AMBIENT);
                    onSpeak(p, lp, LobbyFlag.TalkDA);
                    break;
                    
    		case VILLAGER:
                    if (lp.hasFlag(LobbyFlag.NewBieDone)) {
    			fa.set(Arrays.asList("§fРад видеть тебя снова!")).time(5).beforeEyes(false).sound(Sound.ENTITY_VILLAGER_AMBIENT);
                    } else {
    			fa.set(new ArrayList<String>(Arrays.asList("Мы наконец-то прибыли!", "Открой §e'Достижения' [Д]§f чтобы", "увидеть следующие задания!", "Кликни на лампу, и §6Джин§f мигом", "отвезет тебя на §6Спавн§f"))).add(Material.SOUL_LANTERN).add("Либо просто пригни за борт!")
                            .time(10).sound(Sound.ENTITY_VILLAGER_AMBIENT);
    			QuestManager.tryCompleteQuest(p, lp, Quest.SpeakWithNPC);
                    }
                    break;
                    
    		case ZOMBIE:
                    fa.set(Arrays.asList("§3СкайБлок§f если что дальше §3-->", "А здесь режим §3ВанБлок§f, на", "котором тебе предстоит построить", "островок буквально начиная", "только с §31§f блоком!", "§eУдачи в развитии!"))
                        .time(10).sound(Sound.ENTITY_ZOMBIE_AMBIENT);
                    onSpeak(p, lp, LobbyFlag.TalkOB);
                    break;
                    
    		case WITCH:
                    fa.set(Arrays.asList("Приветствую, странник, приглашаю", "тебя на §3СкайБлок§f - режим,", "заключенный в §3развити§f и", "§3прокачке§f острова, так", "сказать, из грязи в князи!", "§eУдачи в развитии!"))
                        .time(10).sound(Sound.ENTITY_WITCH_AMBIENT);
                    onSpeak(p, lp, LobbyFlag.TalkSW);
                    break;
                    
    		case WANDERING_TRADER:
                    fa.set(Arrays.asList("Привет, друг! Здесь, ты можешь", "развлечься в ассортименте разных", "мини-игр, таких как §eБилд Баттл§f,", "§eКонтра§f, §eПрятки§f, и т.д., как и", "сам, так и со своими друзьями!", "§eХорошего отдыха!"))
                        .time(10).sound(Sound.ENTITY_WANDERING_TRADER_AMBIENT);
                    onSpeak(p, lp, LobbyFlag.TalkPVE);
                    break;
                    
    		case SNOWMAN:
                    fa.set(Arrays.asList("Бурлит §bадреналин§f в крови? Тогда", "выпришли по адресу, ведь здесь,", " на §bПаркурах§f, у вас есть выбор", "среди 60+ карт, все из них с", "интересными темами и прыжками!", "§eПриятных прыганий!"))
                        .time(10).sound(Sound.BLOCK_SNOW_BREAK);
                    onSpeak(p, lp, LobbyFlag.TalkPK);
                    break;
                    
    		case WITHER_SKELETON:
                    fa.set(Arrays.asList("Готовы показать свой скилл на", "поле боя? Выберите §6PVP§f мини-игру", " на ваш вкус (§6Бед Варс§f, §6Скай", "§6Варс§f, §6Зомби§f, и т.д.) и", "отжигайте местную фауну с друзьями!", "§eДостойных побед!"))
                        .time(10).sound(Sound.ENTITY_WITHER_SKELETON_AMBIENT);
                    onSpeak(p, lp, LobbyFlag.TalkPVP);
                    break;
                    
    		case CREEPER:
                    fa.set(Arrays.asList("Приветствую, §eпутник§f, и добро", "пожатовать на §3Архипелаг§f - лобби,", "где на каждом острове ты найдешь", "интересные и уникальные §6Большие Режымы§f,", "или §eМини-Игры§f для вас и ваших друзей!", "§eИссладуйте весь остров!"))
                        .time(10).sound(Sound.ENTITY_CREEPER_HURT);
                    onSpeak(p, lp, LobbyFlag.TalkPVP);
                    break;
                    
            }
            
            e.setAnswer(fa);
            
            
            
            
            
            
        } else if (e.getFigure().getTag().equals("mid") && e.getFigure().getEntityType() == EntityType.PIGLIN) {
            
            p.playSound(e.getFigure().getEntity().getLocation(), Sound.ENTITY_PIGLIN_AMBIENT, 1f, 1f);
            switch (e.getFigure().getName().charAt(4)) {
            case 'K':
                if (lp.hasFlag(LobbyFlag.MI1)) {
                        p.sendMessage("[§cКузнец§f] Я за сегодня 3 топора выковал!");
                } else {
                        p.sendMessage("[§cКузнец§f] Приветствую! Налог 5 золотых говорите? ну забирайте... §6+5⛃");
                        //look
                        lp.setFlag(LobbyFlag.MI1, true);
                }
                break;
            case 'М':
                if (lp.hasFlag(LobbyFlag.MI2)) {
                        p.sendMessage("[§cМясник§f] Главное чтоб мясо не протухло!");
                } else {
                        p.sendMessage("[§cМясник§f] Ну что, опять налоги собираете?\nТолько сегодня последние 2 собрал... держите... §6+5⛃");
                        //look
                        lp.setFlag(LobbyFlag.MI2, true);
                }
                break;
            case 'Ф':
                if (lp.hasFlag(LobbyFlag.MI3)) {
                        p.sendMessage("[§cФермер§f] Сегодня впервые за месяц спеку хлеб!");
                } else {
                        p.sendMessage("[§cФермер§f] Здрасть, товарищ! 5? У меня столько нету...\nВот 3, по факту все что есть... §6+3⛃");
                        //look
                        lp.setFlag(LobbyFlag.MI3, true);
                }
                break;
            }
            
            Ostrov.sync(() -> QuestManager.tryCompleteQuest(p, lp, Quest.CollectTax), 20);
        }
        
    }
    
    
    private void onSpeak(Player p, LobbyPlayer lp, LobbyFlag flag) {
//p.sendMessage("§8log: onSpeak hasQuest?"+lp.hasQuest(Quest.TalkAllNpc)+" hasFlag?"+lp.hasFlag(flag));
       if (lp.hasQuest(Quest.TalkAllNpc) && !lp.hasFlag(flag)) {
            lp.setFlag(flag, true);
            QuestManager.updateProgress(p, lp, Quest.TalkAllNpc, true);
            if (QuestManager.tryCompleteQuest(p, lp, Quest.TalkAllNpc)) {
                final Oplayer op = PM.getOplayer(p);
                op.setData(Data.RIL, op.getDataInt(Data.RIL)+10);
            }
       }
    }    
    
    
    
    
    @EventHandler (priority = EventPriority.MONITOR)
    public void onMission(final MissionEvent e) {
        if (e.action!=MissionEvent.MissionAction.Accept) return;
//e.getPlayer().sendMessage("§8log: PandoraUseEvent");
        QuestManager.tryCompleteQuest(e.getPlayer(), Main.getLobbyPlayer(e.getPlayer()), Quest.FirstMission);
        //}
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPandoraUse(final PandoraUseEvent e) {
//e.getPlayer().sendMessage("§8log: PandoraUseEvent");
        //if (e.luck()) {
            QuestManager.tryCompleteQuest(e.getPlayer(), Main.getLobbyPlayer(e.getPlayer()), Quest.PandoraLuck);
        //}
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
                       // .add(Material.SOUL_LANTERN, (player) -> {player.sendMessage("pickup SOUL_LANTERN");} )
                        //.add("§6Ну или прыгай за борт.", (player) -> {player.sendMessage("прыгай");} )
                        .add("§eПривет!" )
                        .add("§aТы тут новенький?")
                        .add("§bНа корабле есть §lособенная лампа §r§b,потри её.")
                        .add(Material.SOUL_LANTERN)
                        .add("§6Ну или прыгай за борт.")
                        .time(15).vibration().sound(Sound.ENTITY_VILLAGER_TRADE)
                        .beforeEyes()
                        ;
                e.setAnswer(answer);
                QuestManager.tryCompleteQuest(p, lp, Quest.SpeakWithNPC);
            }
            
        } else */