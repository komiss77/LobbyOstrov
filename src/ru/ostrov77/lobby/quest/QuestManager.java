package ru.ostrov77.lobby.quest;

import java.util.EnumSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.DonatEffect;
import ru.ostrov77.lobby.LobbyFlag;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.event.CuboidEvent;


public class QuestManager implements Listener {


    private static final ChatColor[] colors = new ChatColor[] { ChatColor.DARK_AQUA, ChatColor.GOLD, ChatColor.GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW };

    
    
    @EventHandler (priority = EventPriority.MONITOR)
    public static void onInvClose(final InventoryCloseEvent e) {
        if (e.getInventory().getType()==InventoryType.CHEST && e.getView().getTitle().equals("Профиль : Паспорт")) {
            final LobbyPlayer lp = Main.getLobbyPlayer(e.getPlayer().getName());
            if (lp==null) return;
            if (lp.hasQuest(Quest.Passport)) {
                //final Oplayer op = PM.getOplayer(e.getPlayer().getName());
                //e.getPlayer().sendMessage("getPasportFillPercent="+op.getPasportFillPercent());
                if (updateProgress(lp.getPlayer(), lp, Quest.Passport, true)>0) {
                    tryCompleteQuest(lp.getPlayer(), lp, Quest.Passport);
                }
            }
        }
    }
    
    //нописание в actionbar куда человек зашел + квест на гонку для ПВЕ мини-игр
    @EventHandler
    public static void onCuboidEvent(final CuboidEvent e) {  //если лоббиплеер нуль, то сюда никогда не придёт
    	
        if (e.getPrevois() != null) {
            switch (e.getPrevois().getName()) {
                case "daaria", "skyworld", "sumo" -> {
                    e.getPlayer().getInventory().setItem(2, e.getLobbyPlayer().hasFlag(LobbyFlag.Elytra) ? Main.fw : Main.air);
                }
            }
        }
        
    	if (e.getCurrent() == null) {
            
            ApiOstrov.sendActionBarDirect(e.getPlayer(), "§7§l⟣ §3§lАрхипелаг §7§l⟢");
            
    	} else {

            ApiOstrov.sendActionBarDirect(e.getPlayer(), "§7§l⟣ " + e.getCurrent().displayName + " §7§l⟢");
            if (!e.getLobbyPlayer().isAreaDiscovered(e.getCurrent().id)) {
               onNewAreaDiscover(e.getPlayer(), e.getLobbyPlayer(), e.getCurrent()); //новичёк или нет - обработается внутри
            }
    		
            if (!e.getLobbyPlayer().hasFlag(LobbyFlag.NewBieDone)) return; //далее - новичкам ничего не надо
            
            switch (e.getCurrent().getName()) {
                
                case "start" -> {
                    if (e.getLobbyPlayer().isAreaDiscovered(AreaManager.getCuboid("nopvp").id)) {
                        final Player p = e.getPlayer();
                        p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 2f);
                        p.sendMessage("§5[§eСостязание§5] §7>> На старт! Внимание! Вперед!");
                        e.getLobbyPlayer().raceTime = 0;
                    } else {
                        e.getPlayer().sendMessage("§5[§eСостязание§5] §7>> Найдите §eОазис§7 перед началом!");
                    }
                }
                    
                case "end" -> {
                    if (e.getLobbyPlayer().raceTime > 0) {
                        final Player p = e.getPlayer();
                        p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1f, 2f);
                        p.sendMessage("§5[§eСостязание§5] §7>> Хорошо сработано! Время: §e" + ApiOstrov.secondToTime(e.getLobbyPlayer().raceTime / 2));
                        QuestManager.tryCompleteQuest(p, e.getLobbyPlayer(), Quest.MiniRace);
                        e.getLobbyPlayer().raceTime = -1;
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
//p.sendMessage("§8log: onNewAreaDiscover "+p.getName()+" "+cuboid.getName());
        
        if (!lp.hasFlag(LobbyFlag.NewBieDone) && p.getTicksLived()>20) {  //новичёк - пока не откроет спавн, другие не давать + защита от появления на спавне после reset
    	    switch (cuboid.getName()) {
                case "spawn" -> {//новичёк дошел до спавна
                    tryCompleteQuest(p, lp, Quest.ReachSpawn);
                }
                case "newbie" -> {
                    //идём ниже
                }
                default -> {
                    //на остальные кубоиды новичёк не реагирует
                    return;
                }
            }
            //для кубоида новичков даём первые задания ниже
        }
 
        Main.advance.sendComplete(p, cuboid.getName(), false);//Complete(p, cuboid.getName());
        lp.setAreaDiscovered(cuboid.id);
        
        final EnumSet<Quest> childrenQuest = Quest.getChidren(cuboid.getName());
        if (!childrenQuest.isEmpty()) { //найти зависимые от его выполнения квесты
            for (Quest childQuest : childrenQuest) {
                if (lp.addQuest(childQuest)) {
//p.sendMessage("§8log: +новое задание с открытием зоны "+cuboid.getName()+" -> "+childQuest.displayName);
                    if (childQuest.ammount>0) {
                        Main.advance.sendProgress(p, childQuest,lp.getProgress(childQuest));//sendProgress(p, lp, childQuest, QuestManager.getProgress(p, lp, childQuest, true)); //чтобы отобразило
                    }
                }
            }
        }
      
        if (cuboid.getInfo().canTp) {
            //Софтлок (нельзя пройти) задания Навигатор, если все локации открыты -
            //при открытии последней новой зоны сначала автовыполнение навигатора
            if (lp.getProgress(Quest.DiscoverAllArea)>=Quest.DiscoverAllArea.ammount) {
                tryCompleteQuest(p, lp, Quest.Navigation, false);
            }
            tryCompleteQuest(p, lp, Quest.DiscoverAllArea);
            ApiOstrov.sendBossbar(p, "Открыта новая локация: "+cuboid.displayName, 7, BarColor.GREEN, BarStyle.SOLID, false);
            if (lp.compasstarget==cuboid.getInfo()) {
                AreaManager.resetCompassTarget(p, lp);
            }
            sound(p);
        }
        
    }

    
    
    
    

    
    
    //для квестов где ammount>0
    public static int updateProgress(final Player p, final LobbyPlayer lp, final Quest quest, final boolean update) {
        //if (!lp.hasQuest(quest)) return -1; -не надо, или когда вызывает DiscoverAllArea по окончании HeavyFoot не даёт колл-во
        int progress = 0;
        final Oplayer op = PM.getOplayer(p);
        
        switch (quest) {
            
            case DiscoverAllArea:
                progress = lp.getOpenAreaCount(); //открытые добавляются выше в onNewAreaDiscover
                break;
                
            case FindBlock:
                progress = lp.getProgress(quest);
                if (update) Main.advance.sendProgress(p, quest, progress);
                return progress; //тут не надо lp.setProgress, обновляется при интеракт
                //break;
                
            case CobbleGen, MineDiam, KillMobs: // вызов когда киркой ломаешь булыгу // вызов когда киркой ломаешь алмазы
                final Material mat;
            	switch (quest) {
                    case MineDiam:
                            mat = Material.DIAMOND;
                            break;
                    case KillMobs:
                            mat = Material.ROTTEN_FLESH;
                            break;
                    case CobbleGen:
                    default:
                        mat = Material.COBBLESTONE;
                        break;
                }
//Ostrov.log("updateProgress "+quest+" mat="+mat);
                final PlayerInventory pi = p.getInventory();
                final ItemStack it = new ItemStack(mat);
                progress = 1;
                for (final ItemStack i : pi.getContents()) {
                    if (i != null && i.getType() == mat) {
                        progress += i.getAmount();
                    }
                }
                pi.setItemInOffHand(Main.air);
                pi.remove(mat);
                if (progress < quest.ammount) {
                    it.setAmount(progress);
                    pi.setItemInOffHand(it);
                }
                break;
                
            case CollectTax:
            	progress = (lp.hasFlag(LobbyFlag.MI1) ? 5 : 0) + (lp.hasFlag(LobbyFlag.MI2) ? 5 : 0) + (lp.hasFlag(LobbyFlag.MI3) ? 3 : 0);    
                break; 
                
            case Passport:
            	progress = op==null ? 0 : op.getPasportFillPercent();    
                break; 
                
            case TalkAllNpc:
                for (final LobbyFlag f : LobbyFlag.values()) {
                    switch (f.tag) {
                        case 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 -> {
                            if (lp.hasFlag(f)) {
                                progress++;
                            }
                        }
                    }
                }
                break; 
                
        }
//p.sendMessage("§8log: getProgress "+quest+"="+progress);
        lp.setProgress(quest, progress); //сохранить в кэш
        if (update) Main.advance.sendProgress(p, quest, progress);//progressAdv(p, lp, quest, dsc);
        return progress;
        
    }
    
    
    
    
    
    // вызывать SYNC !!!
    //тут только дополнительные проверки.
    //По дефолту, раз сюда засланао проверка, квест должен быть завершен.
    //ну, естественно он будет завершен, если был получен и не был завершен, что проверяется выше.
    //checkProgress нужен для отладки из меню квестов (чтобы не засылало в updateProgress и не меняло lp.getProgress)
    public static boolean tryCompleteQuest(final Player p, final LobbyPlayer lp, final Quest quest) {
        return tryCompleteQuest(p, lp, quest, quest.ammount>0);
    }
    
    public static boolean tryCompleteQuest(final Player p, final LobbyPlayer lp, final Quest quest, final boolean checkProgress) {
    	if (!Bukkit.isPrimaryThread()) {
            Ostrov.log_warn("Асинхронный вызов tryCompleteQuest :"+quest+", "+p.getName());
        }
//p.sendMessage("§8log: tryCompleteQuest "+quest+" has?"+lp.hasQuest(quest));
        if (!lp.hasQuest(quest)) return false;
        
        if (checkProgress && quest.ammount>0) { //перед завершением квестов со счётчиками обновить прогресс
            updateProgress(p, lp, quest, true);
        }
        
        switch (quest) {
            
            case SpeakWithNPC -> lp.addQuest(Quest.SpawnGin);
                        
            case ReachSpawn -> { //сработает при входе в зону спавн
                if (lp.hasFlag(LobbyFlag.NewBieDone)) { //notPlJoin не чекаем, квесты новичка нужно завершить в любом случае, пусть даже при перезаходе
                    return false;
                }
                lp.setFlag(LobbyFlag.NewBieDone, true); //квест OpenAdvancements завершать не надо, его можно завершить позже и НЕновичку
                if (lp.questDone(Quest.SpeakWithNPC)) { //завершаем, т.к. НЕновичёк выполнить больше на сможет
                    Main.advance.sendComplete(p, Quest.SpeakWithNPC.name(), true);//lp.questDone(Quest.SpeakWithNPC);//completeAdv(p, lp, Quest.SpeakWithNPC);
                }
                if (lp.questDone(Quest.SpawnGin)) { //if (lp.questAccept.remove(Quest.SpawnGin)) { //завершаем, т.к. НЕновичёк выполнить больше на сможет
                    Main.advance.sendComplete(p, Quest.SpawnGin.name(), true);//lp.questDone.add(Quest.SpawnGin);//completeAdv(p, lp, Quest.SpawnGin);
                }
                if (PM.exist(p.getName())) {
                    PM.getOplayer(p).showScore();
                }
            }
                
            case DiscoverAllArea -> {
                if (lp.getProgress(quest)<quest.ammount) { //недостаточно - выход
                    return false;
                }
                Main.pipboy.giveForce(p);
                //tryCompleteQuest(p, lp, Quest.Navigation); - так не сработает
                //tryCompleteQuest(p, lp, Quest.Passport); //заслать автоматом, вдруг уже  заполнен
            }
                
            case PandoraLuck -> Main.cosmeticMenu.giveForce(p);
                
                
                
            case CobbleGen, MineDiam -> { // вызов когда киркой ломаешь булыгу // вызов когда киркой ломаешь алмазы
                if ( lp.getProgress(quest) < quest.ammount) {
                    return false;
                }
            }
                
            case CollectTax, Passport -> {
                if ( lp.getProgress(quest) < quest.ammount) {//if (tax < 13) {
                    return false;
                }
            }

            case TalkAllNpc -> {
                if ( lp.getProgress(quest) < quest.ammount) {//if (tax < 13) {
                    return false;
                }
            }


            case FindBlock, KillMobs -> {
                if ( lp.getProgress(quest) < quest.ammount) {//if (tax < 13) {
                    return false;
                } else {
                    //lp.foundBlocks=null;
                    //ApiOstrov.sendActionBarDirect(p, "§7Найден блок §e" + Main.nrmlzStr(e.getClickedBlock().getType().toString()) + "§7, осталось: §e" + (50 - sz));
                }
            }

            case Elytra -> {
                lp.setFlag(LobbyFlag.Elytra, true);
                p.getInventory().setItem(2, Main.fw);
                Main.elytra.giveForce(p);
            }
            
                
            case HeavyFoot -> updateProgress(p, lp, Quest.DiscoverAllArea, true);
                
        }
        
        lp.questDone(quest);
        
        DonatEffect.spawnRandomFirework(p.getLocation());
        final ChatColor chatColor = colors[Ostrov.random.nextInt(colors.length)];
        p.sendMessage(" ");
        p.sendMessage(new StringBuilder().append(chatColor).append(ChatColor.STRIKETHROUGH).append("-----").append(ChatColor.DARK_RED).append(ChatColor.MAGIC).append(" AA").append(ChatColor.YELLOW).append(" Выполнены условия достижения ").append(ChatColor.DARK_RED).append(ChatColor.MAGIC).append("AA ").append(chatColor).append(ChatColor.STRIKETHROUGH).append("-----").toString());
        p.sendMessage(chatColor + quest.displayName );
        p.sendMessage(chatColor + " Квест завершен! " );
        p.sendMessage(" ");
        
        Main.advance.sendComplete(p, quest.name(), false);//Complete(p, lp, quest, false);//помеить завершенным
        
        final EnumSet<Quest> childrenQuest = Quest.getChidren(quest.name());
        if (!childrenQuest.isEmpty()) { //найти зависимые от его выполнения квесты
            for (Quest childQuest : childrenQuest) {
                if (lp.addQuest(childQuest)) {
//p.sendMessage("§8log: +новое задание с выполнением  квеста "+quest+" -> "+childQuest.displayName);
                    if (childQuest.ammount>0) {
                        Main.advance.sendProgress(p, childQuest,lp.getProgress(childQuest));//sendProgress(p, lp, childQuest, QuestManager.getProgress(p, lp, childQuest, true)); //чтобы отобразило
                    }
                }
            }
        }
        
        if (!lp.hasFlag(LobbyFlag.Elytra) && lp.questDone.size() == Quest.values().length-1) {
            tryCompleteQuest(p, lp, Quest.Elytra); //осторожно, можно упасть в деадЛок если сделать кривую проверку!
        }
        ApiOstrov.addExp(p, 30);
        return true;
    }

    
    
    
    
    
    

    public static void sound(final Player p) {
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2, 0.5f);
        Ostrov.async(()-> {
            if (p.isOnline()) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1,  0.5f);
            }
        }, 5);
    }
    
}
