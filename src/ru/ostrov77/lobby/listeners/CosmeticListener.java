package ru.ostrov77.lobby.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import ru.ostrov77.lobby.LobbyFlag;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.event.CuboidEvent;
import ru.ostrov77.lobby.quest.Quest;
import ru.ostrov77.lobby.quest.QuestManager;
import se.file14.procosmetics.api.ProCosmeticsAPI;
import se.file14.procosmetics.api.events.PlayerOpenTreasureEvent;
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

    
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public static void onCuboidEvent(final CuboidEvent e) {
        
        final User us = ProCosmeticsAPI.getUser(e.getPlayer());
        
        if (e.getPrevois()!=null && e.getPrevois().getInfo().unequpCosmetic) {
            if (us != null) {
                us.equipLastCosmetics(true);
            }
        } 
        
        if (e.getCurrent()!=null && e.getCurrent().getInfo().unequpCosmetic) {
            if (us != null) {
                us.unequipCosmetics(true);
            }
        }
        
    }
    
    
	/*public static void removeCosm(final Player p) {
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
	}*/
    
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPreEquip (final PlayerPreEquipCosmeticEvent e) {
        final LobbyPlayer lp = Main.getLobbyPlayer(e.getPlayer());
//e.getPlayer().sendMessage("§8log: PlayerOpenTreasureEvent ");
        if (lp==null || !lp.hasFlag(LobbyFlag.NewBieDone)) {
            e.setCancelled(true);
        } else {
            if (lp.questDone.contains(Quest.DiscoverAllArea))return; 
//e.getPlayer().sendMessage("§6getConfigPath="+e.getCosmeticType().getConfigPath()+" getVariableName="+e.getCosmeticType().getVariableName()+" getName="+e.getCosmeticType().getName());
            
            if (e.getCosmeticType().getConfigPath().equals("mounts")) {
//e.getPlayer().sendMessage(" getVariableName="+e.getCosmeticType().getVariableName()+" getName="+e.getCosmeticType().getName());
                
                switch (e.getCosmeticType().getVariableName()) {
                    case "molten-snake":
                    case "ethereal-dragon":
                    case "hype-train":
                    case "pirate-ship":
                        e.setCancelled(true);
                }
            } else if (e.getCosmeticType().getConfigPath().equals("gadgets")) {
                
//e.getPlayer().sendMessage(" getVariableName="+e.getCosmeticType().getVariableName()+" getName="+e.getCosmeticType().getName());
                switch (e.getCosmeticType().getVariableName()) {
                    case "ethereal-pearl":
                    case "rocket":
                    case "trampoline":
                    case "wither-missile":
                        e.setCancelled(true);
                }
            }
            
            if (e.isCancelled()) {
                e.getPlayer().sendMessage("§6*Вы сможете использовать "+e.getCosmeticType().getName()+" §6когда выполните задание "+Quest.DiscoverAllArea.displayName);
            }
            
//e.getPlayer().sendMessage("§6getConfigPath="+e.getCosmeticType().getConfigPath()+" getVariableName="+e.getCosmeticType().getVariableName()+" getName="+e.getCosmeticType().getName());
        }
    }
    
    
    

    
    
   //PlayerPreEquipCosmeticEvent.class
    //PlayerPurchaseTreasureEvent.class
    //PlayerUnequipCosmeticEvent.class
    //PlayerUseGadgetEvent.class
    //CosmeticEntitySpawnEvent.class
    
}
