package ru.ostrov77.lobby.quest;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import ru.komiss77.ApiOstrov;
import ru.komiss77.LocalDB;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.LCuboid;


public class QuestManager {

    
    
    
    public QuestManager () {
        


        
        
    }
    
    
    // ASYNC !!!
    public static void onCuboidExit(final Player p, final LobbyPlayer lp, final LCuboid previos) {
ApiOstrov.sendActionBar(p, "вышел из кубоида "+previos.displayName);
    }

    // ASYNC !!!
    public static void onCuboidEntry(final Player p, final LobbyPlayer lp, final LCuboid current) {
ApiOstrov.sendActionBar(p, "вошел в кубоид "+current.displayName);
        if (!lp.isAreaDiscovered(current.id)) {
            onNewAreaDiscover(p, lp, current);
        }
    }

    // ASYNC !!!
    public static void onCuboidChange(final Player p, final LobbyPlayer lp, final LCuboid previos, final LCuboid current) {
ApiOstrov.sendActionBar(p, "перешел из кубоида "+(previos==null ? "" : previos.displayName)+" в "+(current==null ? "" : current.displayName));
        if (!lp.isAreaDiscovered(current.id)) {
            onNewAreaDiscover(p, lp, current);
        }
    }

    
    //может вызываться из ASYNC !!!
    private static void onNewAreaDiscover(final Player p, final LobbyPlayer lp, final LCuboid current) {
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, .5f);
        ApiOstrov.sendBossbar(p, "Изучена новая локация: "+current.displayName, 7, BarColor.GREEN, BarStyle.SOLID, false);
        lp.setAreaDiscovered(current.id);
        checkQuest(p, lp, Quest.DiscoverAllArea);
    }

    
    
    
    
    
    
    
    //может вызываться из ASYNC !!!
    public static boolean checkQuest (final Player p, final LobbyPlayer lp, final Quest quest) {
        
        switch (quest) {
            case DiscoverAllArea:
                int discoverCount=0;
                for (int id:AreaManager.getCuboidIds()) {
                    if (lp.isAreaDiscovered(id)) discoverCount++;
                }
                if (discoverCount>=AreaManager.getCuboidIds().size()) {
                    questDone(p, lp,quest);
                }
p.sendMessage("checkQuest DiscoverAllArea всего локаций="+AreaManager.getCuboidIds().size()+", открыто="+discoverCount);
                break;
        }
        
        return false;
    }


    
    private static void questDone(final Player p, final LobbyPlayer lp, final Quest quest) {
p.sendMessage("выполнен квест "+Quest.DiscoverAllArea.displayName);
        lp.questDone.add(quest);
        StringBuilder sb = new StringBuilder();
        for (Quest q:lp.questDone) {
            sb.append(quest.code);
        }
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `lobbyData` SET `questDone` = '"+sb.toString()+"' WHERE `name` = '"+lp.name+"';");
    }

    
}
