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
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.DonatEffect;
import ru.ostrov77.lobby.LobbyFlag;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.AreaViewMenu;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.event.CuboidEvent;


public class QuestManager implements Listener {

    private static final ChatColor[] colors = new ChatColor[] { ChatColor.DARK_AQUA, ChatColor.GOLD, ChatColor.GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW };

    public static void sound(final Player p) {
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2, 0.5f);
        Ostrov.async(()-> {
            if (p.isOnline()) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1,  0.5f);
            }
        }, 5);
    }


    
    //нописание в actionbar куда человек зашел + квест на гонку для ПВЕ мини-игр
    @EventHandler
    public static void onCuboidEvent(final CuboidEvent e) {
    	
        if (e.getPrevois() != null) {
            switch (e.getPrevois().getName()) {
                
                case "daaria":
                case "skyworld":
                case "sumo":
                    e.getPlayer().getInventory().setItem(2, e.getLobbyPlayer().hasFlag(LobbyFlag.Elytra) ? Main.fw : Main.air);
                    break;
                    
                //case "pandora": //вышел из локации пандора - значит мог её использовать
                    //tryCompleteQuest(e.getPlayer(), e.getLobbyPlayer(), Quest.LeavePandora);
                    //break;
                    
                default:
                    break;
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
                case "start":
                if (e.getLobbyPlayer().isAreaDiscovered(AreaManager.getCuboid("nopvp").id)) {
                	final Player p = e.getPlayer();
                	p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 2f);
                    p.sendMessage("§5[§eСостязание§5] §7>> На старт! Внимание! Вперед!");
                    e.getLobbyPlayer().raceTime = 0;
                } else {
                    e.getPlayer().sendMessage("§5[§eСостязание§5] §7>> Найдите §eОазис§7 перед началом!");
                }
                break;
                /*case "start":
                    if (e.getLobbyPlayer().questAccept.contains(Quest.MiniRace)) {
                        e.getPlayer().sendMessage("§5[§eСостязание§5] §7>> На старт! Внимание! Вперед!");
                        //AreaManager.racePlayers.putIfAbsent(e.getLobbyPlayer().name, 0);
                        e.getLobbyPlayer().raceTime = 0;
                    } else if (e.getLobbyPlayer().questDone.contains(Quest.MiniRace)) {
                        e.getPlayer().sendMessage("§5[§eСостязание§5] §7>> Вы уже участвовали в состязании!");
                    } else {
                        e.getPlayer().sendMessage("§5[§eСостязание§5] §7>> Найдите §eОазис§7 перед началом!");
                    }
                    break;*/
                    
                case "end":
                    if (e.getLobbyPlayer().raceTime > 0) {
                    	final Player p = e.getPlayer();
                    	p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1f, 2f);
                    	p.sendMessage("§5[§eСостязание§5] §7>> Хорошо сработано! Время: §e" + ApiOstrov.secondToTime(e.getLobbyPlayer().raceTime / 2));
                        QuestManager.tryCompleteQuest(p, e.getLobbyPlayer(), Quest.MiniRace);
                    	e.getLobbyPlayer().raceTime = -1;
                        //lp.questDone(p, quest, true);
                    }
                    break;
                    
                case "daaria":
                case "skyworld":
                    Main.pickaxe.give(e.getPlayer());
                    break;
                    
                case "sumo":
                    Main.stick.give(e.getPlayer());
                    break;
                    
                default:
                    break;
            }
        }
    	
        
       /* if (e.getCurrent()!=null && e.getPrevois()!=null) {
        	ApiOstrov.sendActionBar(e.getPlayer(), "§8log: перешел из кубоида "+e.getPrevois().displayName+" в "+e.getCurrent().displayName+", вход:"+e.getPrevois()EntryTime+", пробыл:"+(Timer.getTime()-e.getPrevois()EntryTime));
            if (!e.getLobbyPlayer().isAreaDiscovered(e.getCurrent().id)) {
                QuestManager.onNewAreaDiscover(e.getPlayer(), e.getLobbyPlayer(), e.getCurrent());
            }
        } else if (e.getPrevois()!=null) {
        	ApiOstrov.sendActionBar(e.getPlayer(), "§8log: вышел из кубоида "+e.getPrevois().displayName+", вход:"+e.getPrevois()EntryTime+", пробыл:"+(Timer.getTime()-e.getPrevois()EntryTime));
        } else if (e.getCurrent()!=null) {
        	ApiOstrov.sendActionBar(e.getPlayer(), "§8log: вошел в кубоид "+e.getCurrent().displayName);
            if (!e.getLobbyPlayer().isAreaDiscovered(e.getCurrent().id)) {
                QuestManager.onNewAreaDiscover(e.getPlayer(), e.getLobbyPlayer(), e.getCurrent());
            }
        }*/
        
    }

    
    
    //SYNC !!!
    public static void onNewAreaDiscover(final Player p, final LobbyPlayer lp, final LCuboid cuboid) {
//p.sendMessage("§8log: onNewAreaDiscover "+p.getName()+" "+cuboid.getName());
        
        if (!lp.hasFlag(LobbyFlag.NewBieDone)) {  //новичёк - пока не откроет спавн, другие не давать
    	    switch (cuboid.getName()) {
                case "spawn"://новичёк дошел до спавна
                    tryCompleteQuest(p, lp, Quest.ReachSpawn);
                    break;
                case "newbie"://для кубоида новичков даём первые задания ниже
                    break;
                default://на остальные кубоиды новичёк не реагирует
                    return;
            }
        }

        completeCuboidAdv(p, cuboid.getName());
        lp.setAreaDiscovered(cuboid.id);
        
        final EnumSet<Quest> areaQuest = Quest.getAreaQuest(cuboid.getName());
        boolean save = false;
        if (!areaQuest.isEmpty()) { //с открытой зоной добавились новые задания
            for (Quest q : areaQuest) {
                if (addQuest(p, lp, q, false)) {
//p.sendMessage("§8log: +новое задание с открытием зоны "+cuboid.getName()+" : "+q.displayName);
                    save = true;
                }
            }
        }
        if (save) {
            lp.saveQuest();
        }

        if (cuboid.getInfo().canTp) {
            tryCompleteQuest(p, lp, Quest.DiscoverAllArea);
            ApiOstrov.sendBossbar(p, "Открыта новая локация: "+cuboid.displayName, 7, BarColor.GREEN, BarStyle.SOLID, false);
            if (lp.compasstarget==cuboid.id) {
                AreaViewMenu.resetCompassTarget(p);
            }
            sound(p);
        }
        
    }

    
    
    
    
    //отдельным методом, т.к. могут добавлять и НПС
    public static boolean addQuest(final Player p, final LobbyPlayer lp, final Quest quest, final boolean save) {
        if (!lp.questDone.contains(quest) && lp.questAccept.add(quest)) { //это задание ранее не выполнено и уже не было получено ранее
            if (Main.advancements) {
                Advance.sendToast(p, lp, quest);
            } else {
                ApiOstrov.sendTitleDelay(p, "", "§7Квест: "+quest.displayName, 20, 40, 20);
            }
            if (save) lp.saveQuest();
            return true;
        }
        return false;
    }
    
    
    
    public static int checkProgress(final Player p, final LobbyPlayer lp, final Quest quest) {
        if (!lp.questAccept.contains(quest) || lp.questDone.contains(quest)) {
            return -1;
        }
        switch (quest) {
            case DiscoverAllArea:
                final int dsc = getDiscAreas(lp);
                progressAdv(p, quest, dsc);
                return dsc;
                
		default:
                    break;
                 
        }
        return 0;
        
    }
    
    
    
    
    
    //вызывать SYNC !!!
    //по дефолту, задание будет выполнено, если оно было взято и не завершено.
    //для некоторых можно ставить сври чекающие обработчики
    public static boolean tryCompleteQuest(final Player p, final LobbyPlayer lp, final Quest quest) {
//p.sendMessage("§8log: tryCompleteQuest "+quest);
    	if (!Bukkit.isPrimaryThread()) {
            Ostrov.log_warn("Асинхронный вызов tryCompleteQuest :"+quest+", "+p.getName());
        }
    	boolean isComplete = false;
        
        if (lp.questDone.contains(quest)) {
//p.sendMessage("§8log: checkQuest "+quest+" - уже выполнен; return ");
            return isComplete;
        }
        if (!lp.questAccept.contains(quest)) {
//p.sendMessage("§8log: checkQuest "+quest+" - не был получен; return ");
            return isComplete;
        }
//p.sendMessage("§8log: tryCompleteQuest 2");
        
        //тут только дополнительные проверки. По дефолту, раз сюда засланао проверка, квест должен быть завершен.
        //ну, естественно он будет завершен, если был получен и не был завершен, что проверяется выше.
        switch (quest) {
            
            case DiscoverAllArea:
            	final int dsc = checkProgress(p, lp, quest);
                if (dsc>=quest.ammount) {
                    Main.pipboy.give(p);
                    completeAdv(p, lp, quest);
                    isComplete = true;
                }
                break;
                
            case PandoraLuck: //будет вызвано при выходе из кубоида пандоры
                //if (op!=null && op.hasDaylyFlag(StatFlag.Pandora)) { //пандора была заюзана. наличие квеста проверяется выше
                    Main.cosmeticMenu.give(p);
                    completeAdv(p, lp, quest);
                    //lp.questDone(p, quest, true);
                    isComplete = true;
                //}
                break;
                
                
            case ReachSpawn: //сработает при входе в зону спавн
                if (!lp.hasFlag(LobbyFlag.NewBieDone)) { //notPlJoin не чекаем, квесты новичка нужно завершить в любом случае, пусть даже при перезаходе
                    lp.setFlag(LobbyFlag.NewBieDone, true);
                    completeAdv(p, lp, quest);
                    if (lp.questAccept.contains(Quest.SpeakWithNPC)) { //завершаем, т.к. НЕновичёк выполнить больше на сможет
                        completeAdv(p, lp, Quest.SpeakWithNPC);
                    }
                    if (lp.questAccept.contains(Quest.SpawnGin)) { //завершаем, т.к. НЕновичёк выполнить больше на сможет
                        completeAdv(p, lp, Quest.SpawnGin);
                    }
                    //квест OpenAdvancements завершать не надо, его можно завершить позже и НЕновичку
                    if (PM.exist(p.getName())) {
                        PM.getOplayer(p).showScore();
                    }
                    isComplete = true;
                }
                break;
                
            case CobbleGen, MineDiam: // вызов когда киркой ломаешь булыгу // вызов когда киркой ломаешь алмазы
                final Material mat = quest == Quest.CobbleGen ? Material.COBBLESTONE : Material.DIAMOND;
                final PlayerInventory pi = p.getInventory();
                final ItemStack it = new ItemStack(mat);
                int num = 1;
                for (final ItemStack i : pi.getContents()) {
                    if (i != null && i.getType() == mat) {
                        num += i.getAmount();
                    }
                }
                progressAdv(p, quest, num);
                pi.setItemInOffHand(Main.air);
                pi.remove(mat);
                if (num == quest.ammount) {
                    completeAdv(p, lp, quest);//lp.questDone(p, quest, true);
                } else {
                    it.setAmount(num);
                    pi.setItemInOffHand(it);
                }
                break;
                
            case CollectTax:
            	final int i = (lp.hasFlag(LobbyFlag.MI1) ? 5 : 0) + (lp.hasFlag(LobbyFlag.MI2) ? 5 : 0) + (lp.hasFlag(LobbyFlag.MI3) ? 3 : 0);
                progressAdv(p, quest, i);
            	if (i == 13) {
                    completeAdv(p, lp, quest);
            	}
                break;
                
            case SpeakWithNPC:
                completeAdv(p, lp, quest);
                addQuest(p, lp, Quest.SpawnGin, true);
                break;

            default:
                completeAdv(p, lp, quest);
                isComplete = true;
                break;
                
        }
        
        if (isComplete && !lp.hasFlag(LobbyFlag.Elytra) && lp.questDone.size() == Quest.values().length && lp.questAccept.size() == 0) {
            lp.setFlag(LobbyFlag.Elytra, true);
            p.getInventory().setItem(2, Main.fw); //2
            Main.elytra.give(p);//ApiOstrov.getMenuItemManager().giveItem(p, "elytra"); //38
            if (Main.advancements) {
                Advance.completeAdv(p, "elytra");
            }
        }
        
        return isComplete;
    }
    

    public static void completeAdv(final Player p, final LobbyPlayer lp, final Quest quest) {
        DonatEffect.spawnRandomFirework(p.getLocation());
        lp.questDone(p, quest, true);
        if (Main.advancements) {
            Advance.completeAdv(p, quest.code);
        } //else {
            final ChatColor chatColor = colors[Ostrov.random.nextInt(colors.length)];
            p.sendMessage(" ");
            p.sendMessage(new StringBuilder().append(chatColor).append(ChatColor.STRIKETHROUGH).append("-----").append(ChatColor.DARK_RED).append(ChatColor.MAGIC).append(" AA").append(ChatColor.YELLOW).append(" Выполнены условия достижения ").append(ChatColor.DARK_RED).append(ChatColor.MAGIC).append("AA ").append(chatColor).append(ChatColor.STRIKETHROUGH).append("-----").toString());
            p.sendMessage(chatColor + quest.displayName );
            p.sendMessage(chatColor + " Квест завершен! " );
            p.sendMessage(" ");
        //}
    }   
    
    public static void completeCuboidAdv(final Player p, final String cuboidname) {
        if (Main.advancements) {
            Advance.completeAdv(p, cuboidname);
        }
    }

    public static void progressAdv(final Player p, final Quest quest, final int prg) {
        if (Main.advancements) {
            Advance.progressAdv(p, quest.code, prg);
        }
    }
	
    public static int getDiscAreas(final LobbyPlayer lp) {
        int dC = 0;
        for (final int id : AreaManager.getCuboidIds()) {
            if (lp.isAreaDiscovered(id)) dC++;
        }
        return dC;
    }
}
