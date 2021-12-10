package ru.ostrov77.lobby.quest;

import java.util.EnumSet;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.event.CuboidEvent;


public class QuestManager implements Listener {

    public static void sound(final Player p) {
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 2);
        Ostrov.async(()-> {
            if (p.isOnline()) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 2);
            }
        }, 10);
    }


    
    //нописание в actionbar куда человек зашел + квест на гонку для ПВЕ мини-игр
    @EventHandler
    public static void onCuboidEvent(final CuboidEvent e) {
    	if (e.current == null) {
    		ApiOstrov.sendActionBar(e.p, "§7§l⟣ &3&lАрхипелаг §7§l⟢");
    		
    	} else {
    		ApiOstrov.sendActionBar(e.p, "§7§l⟣ " + e.current.displayName + " §7§l⟢");
    		if (!e.lp.isAreaDiscovered(e.current.id)) {
                QuestManager.onNewAreaDiscover(e.p, e.lp, e.current);
            }
    		if (e.current.name == "start") {
    			if (e.lp.questAccept.contains(Quest.MiniRace)) {
        			e.p.sendMessage("§5[§eСостязание§5] §f>> На старт! Внимание! Вперед!");
        			AreaManager.racePlayers.putIfAbsent(e.lp.name, 0);
    			} else if (e.lp.questDone.contains(Quest.MiniRace)) {
        			e.p.sendMessage("§5[§eСостязание§5] §f>> Вы уже участвовали в состязании!");
    			} else {
        			e.p.sendMessage("§5[§eСостязание§5] §f>> Перед началом, возьмите задание у §eИгромана§f!");
				}
    		} else if (e.current.name == "end" && e.lp.questAccept.contains(Quest.MiniRace)) {
    			final Integer time = AreaManager.racePlayers.remove(e.lp.name);
    			if (time != null) {
        			e.p.sendMessage("§5[§eСостязание§5] §f>> Хорошо сработано! Время: §e" + (time / 60) + (time % 60 > 9 ? ":" + time % 60 : ":0" + time % 60));
        			e.lp.questDone(e.p, Quest.MiniRace);
    			}
			}
		}
    	
        /*if (e.current!=null && e.previos!=null) {
        	ApiOstrov.sendActionBar(e.p, "log: перешел из кубоида "+e.previos.displayName+" в "+e.current.displayName+", вход:"+e.previosEntryTime+", пробыл:"+(Timer.getTime()-e.previosEntryTime));
            if (!e.lp.isAreaDiscovered(e.current.id)) {
                QuestManager.onNewAreaDiscover(e.p, e.lp, e.current);
            }
        } else if (e.previos!=null) {
        	ApiOstrov.sendActionBar(e.p, "log: вышел из кубоида "+e.previos.displayName+", вход:"+e.previosEntryTime+", пробыл:"+(Timer.getTime()-e.previosEntryTime));
        } else if (e.current!=null) {
        	ApiOstrov.sendActionBar(e.p, "log: вошел в кубоид "+e.current.displayName);
            if (!e.lp.isAreaDiscovered(e.current.id)) {
                QuestManager.onNewAreaDiscover(e.p, e.lp, e.current);
            }
        }*/
    }

    
    
    //SYNC !!!
    public static void onNewAreaDiscover(final Player p, final LobbyPlayer lp, final LCuboid cuboid) {
        sound(p);
        ApiOstrov.sendBossbar(p, "Открыта новая локация: "+cuboid.displayName, 7, BarColor.GREEN, BarStyle.SOLID, false);
        lp.setAreaDiscovered(cuboid.id);
        
        final EnumSet<Quest> areaQuest = Quest.getAreaQuest(cuboid.name);
        boolean save = false;
        if (!areaQuest.isEmpty()) { //с открытой зоной добавились новые задания
            for (Quest q : areaQuest) {
                if (!lp.questDone.contains(q) && lp.questAccept.add(q)) { //это задание ранее не выполнено и уже не было получено ранее
                    save = true;
                    p.sendMessage("log: +новое задание с открытием зоны "+cuboid.name+" : "+q.displayName);
                }
            }
        }
        if (save) {
            lp.saveQuest();
        }
        
        checkQuest(p, lp, Quest.DiscoverAllArea);
        if (cuboid.name.equals("spawn")) {
            checkQuest(p, lp, Quest.ReachSpawn);
        } //else if (cuboid.name.equals("pandora")) {
            //эвент пандоры??
        //}
    }

    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //может вызываться из ASYNC !!!
    public static boolean checkQuest (final Player p, final LobbyPlayer lp, final Quest quest) {
        
        if (lp.questDone.contains(quest)) {
        	p.sendMessage("log: checkQuest "+quest+" - уже выполнен; return ");
            return false;
        }
        p.sendMessage("log: checkQuest "+quest);
        
        switch (quest) {
            case DiscoverAllArea:
                int discoverCount=0;
                for (int id:AreaManager.getCuboidIds()) {
                    if (lp.isAreaDiscovered(id)) discoverCount++;
                }
                if (discoverCount>=AreaManager.getCuboidIds().size()) {
                    lp.questDone(p, quest);
                } else {
                	p.sendMessage("log: checkQuest DiscoverAllArea всего локаций="+AreaManager.getCuboidIds().size()+", открыто="+discoverCount);
                }
                break;
		default:
			break;
        }
        
        return false;
    }


    


    
    



    
    
    
}
