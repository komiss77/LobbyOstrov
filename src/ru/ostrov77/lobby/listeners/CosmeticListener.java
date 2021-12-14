package ru.ostrov77.lobby.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.quest.Quest;
import ru.ostrov77.lobby.quest.QuestManager;
import sv.file14.procosmetics.api.events.PlayerOpenTreasureEvent;


public class CosmeticListener implements Listener {
 
    
    //https://github.com/File14/ProCosmetics/wiki/API
    
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTreassure (final PlayerOpenTreasureEvent e) {
    	final Player p = e.getPlayer();
        final LobbyPlayer lp = Main.getLobbyPlayer(p);
        e.getPlayer().sendMessage("§8log: PlayerOpenTreasureEvent ");
        QuestManager.checkQuest(p, lp, Quest.OpenTreassureChest, true);
    }
    
   //PlayerPreEquipCosmeticEvent.class
    //PlayerPurchaseTreasureEvent.class
    //PlayerUnequipCosmeticEvent.class
    //PlayerUseGadgetEvent.class
    //CosmeticEntitySpawnEvent.class
    
}
