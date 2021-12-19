package ru.ostrov77.lobby.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import ru.ostrov77.lobby.LobbyFlag;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.quest.Quest;
import ru.ostrov77.lobby.quest.QuestManager;
import se.file14.procosmetics.api.events.PlayerOpenTreasureEvent;
import se.file14.procosmetics.api.ProCosmeticsAPI;
import se.file14.procosmetics.api.events.PlayerPreEquipCosmeticEvent;
import se.file14.procosmetics.user.User;


public class CosmeticListener implements Listener {
 
    
    //https://github.com/File14/ProCosmetics/wiki/API
    
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTreassure (final PlayerOpenTreasureEvent e) {
        final LobbyPlayer lp = Main.getLobbyPlayer(e.getPlayer());
//e.getPlayer().sendMessage("§8log: PlayerOpenTreasureEvent ");
        if (lp!=null) {
            QuestManager.tryCompleteQuest(e.getPlayer(), lp, Quest.OpenTreassureChest);
        }
    }

	public static void removeCosm(final Player p) {
		final User us = ProCosmeticsAPI.getUser(p);
		if (us != null) {
			us.unequipCosmetics(true);
		}
	}

	public static void giveCosm(final Player p) {
		final User us = ProCosmeticsAPI.getUser(p);
		if (us != null) {
			us.equipLastCosmetics(true);
		}
	}
    
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPreEquip (final PlayerPreEquipCosmeticEvent e) {
        final LobbyPlayer lp = Main.getLobbyPlayer(e.getPlayer());
//e.getPlayer().sendMessage("§8log: PlayerOpenTreasureEvent ");
        if (lp==null || !lp.hasFlag(LobbyFlag.NewBieDone)) {
        	e.setCancelled(true);
        } else {
			e.getPlayer().sendMessage(e.getCosmeticType().getConfigPath());
		}
    }
    
    
    

    
    
   //PlayerPreEquipCosmeticEvent.class
    //PlayerPurchaseTreasureEvent.class
    //PlayerUnequipCosmeticEvent.class
    //PlayerUseGadgetEvent.class
    //CosmeticEntitySpawnEvent.class
    
}
