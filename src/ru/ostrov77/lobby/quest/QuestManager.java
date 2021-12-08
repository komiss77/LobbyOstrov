package ru.ostrov77.lobby.quest;

import java.util.EnumSet;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.komiss77.ApiOstrov;
import ru.komiss77.LocalDB;
import ru.komiss77.Ostrov;
import ru.komiss77.Timer;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.event.CuboidEvent;


public class QuestManager implements Listener{

    
    
    
    public QuestManager () {
        


        
        
    }
    
    
    @EventHandler
    public static void onCuboidEvent(final CuboidEvent e) {
        if (e.current!=null && e.previos!=null) {
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
        }
    }


    
    //SYNC !!!
    public static void onNewAreaDiscover(final Player p, final LobbyPlayer lp, final LCuboid cuboid) {
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, .5f);
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
        }
        
        return false;
    }


    


    
    



    
    
    
}
