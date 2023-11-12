package ru.ostrov77.lobby.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import ru.komiss77.modules.player.PM;
import ru.komiss77.modules.quests.QuestManager;
import ru.ostrov77.lobby.LobbyFlag;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.CuboidInfo;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.event.CuboidEvent;
import ru.ostrov77.lobby.quest.Quests;
import se.file14.procosmetics.ProCosmetics;
import se.file14.procosmetics.api.ProCosmeticsProvider;
import se.file14.procosmetics.api.events.PlayerOpenTreasureEvent;
import se.file14.procosmetics.api.events.PlayerPreEquipCosmeticEvent;
import se.file14.procosmetics.user.User;




public class CosmeticListener implements Listener {
 
    
    //https://github.com/File14/ProCosmetics/wiki/API
    
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTreassure (final PlayerOpenTreasureEvent e) {
        final LobbyPlayer lp = PM.getOplayer(e.getPlayer(), LobbyPlayer.class);
        QuestManager.complete(e.getPlayer(), lp, Quests.treasure);
    }

    
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public static void onCuboidEvent(final CuboidEvent e) {
        final ProCosmetics api = ProCosmeticsProvider.get();
        final User us = api.getUserManager().getUser(e.getPlayer());
        
        if (e.getLast()!=null && e.getLast().getInfo().unequpCosmetic) {
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
        final LobbyPlayer lp = PM.getOplayer(e.getPlayer(), LobbyPlayer.class);
//e.getPlayer().sendMessage("§8log: PlayerOpenTreasureEvent ");
        if (lp==null || !lp.hasFlag(LobbyFlag.NewBieDone)) {
            e.setCancelled(true);
            return;
        }
        if (QuestManager.isComplete(lp, Quests.discover)) {
            final LCuboid lc = AreaManager.getCuboid(e.getPlayer().getLocation());
            if (lc!=null && lc.getInfo() != CuboidInfo.SUMO) {
                return;
            } 
        } 
//e.getPlayer().sendMessage("§6getConfigPath="+e.getCosmeticType().getConfigPath()+" getVariableName="+e.getCosmeticType().getVariableName()+" getName="+e.getCosmeticType().getName());
        //final ProCosmetics api = ProCosmeticsProvider.get();

        if (e.getCosmeticType().getCategoryPath().equals("mounts")) {
//e.getPlayer().sendMessage(" getVariableName="+e.getCosmeticType().getVariableName()+" getName="+e.getCosmeticType().getName());
            switch (e.getCosmeticType().getName()) {
                case "molten-snake":
                case "ethereal-dragon":
                case "hype-train":
                case "pirate-ship":
                    e.setCancelled(true);
            }
        } else if (e.getCosmeticType().getCategoryPath().equals("gadgets")) {

//e.getPlayer().sendMessage(" getVariableName="+e.getCosmeticType().getVariableName()+" getName="+e.getCosmeticType().getName());
            switch (e.getCosmeticType().getName()) {
                case "ethereal-pearl":
                case "rocket":
                case "trampoline":
                case "wither-missile":
                    e.setCancelled(true);
            }
        } else if (e.getCosmeticType().getCategoryPath().equals("morphs")) {

//e.getPlayer().sendMessage(" getVariableName="+e.getCosmeticType().getVariableName()+" getName="+e.getCosmeticType().getName());
            switch (e.getCosmeticType().getName()) {
                case "bat":
                case "wither":
                case "blaze":
                    e.setCancelled(true);
            }
        }

        if (e.isCancelled()) {
            e.getPlayer().sendMessage("§6*Вы не сможете использовать §с"+e.getCosmeticType().getName()+" §6здесь!");
        }

//e.getPlayer().sendMessage("§6getConfigPath="+e.getCosmeticType().getConfigPath()+" getVariableName="+e.getCosmeticType().getVariableName()+" getName="+e.getCosmeticType().getName());

    }
    
    
    

    
    
   //PlayerPreEquipCosmeticEvent.class
    //PlayerPurchaseTreasureEvent.class
    //PlayerUnequipCosmeticEvent.class
    //PlayerUseGadgetEvent.class
    //CosmeticEntitySpawnEvent.class
    
}
