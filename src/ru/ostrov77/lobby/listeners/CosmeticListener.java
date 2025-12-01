package ru.ostrov77.lobby.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.komiss77.modules.player.PM;
import ru.ostrov77.lobby.LobbyFlag;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.CuboidInfo;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.event.CuboidEvent;
import ru.ostrov77.lobby.quest.Quests;
import se.file14.procosmetics.api.ProCosmetics;
import se.file14.procosmetics.api.ProCosmeticsProvider;
import se.file14.procosmetics.api.event.PlayerOpenTreasureChestEvent;
import se.file14.procosmetics.api.event.PlayerPreEquipCosmeticEvent;
import se.file14.procosmetics.api.user.User;

public class CosmeticListener implements Listener {

    //https://github.com/File14/ProCosmetics/wiki/API
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTreassure(final PlayerOpenTreasureChestEvent e) {
        final LobbyPlayer lp = PM.getOplayer(e.getPlayer(), LobbyPlayer.class);
        Quests.treasure.complete(e.getPlayer(), lp, false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public static void onCuboidEvent(final CuboidEvent e) {
        final ProCosmetics api = ProCosmeticsProvider.get();
        final User us = api.getUserManager().getConnected(e.getPlayer());

        if (e.getLast() != null && e.getLast().getInfo().unequpCosmetic) {
            if (us != null) {
                us.equipSavedCosmetics(true);
            }
        }

        if (e.getCurrent() != null && e.getCurrent().getInfo().unequpCosmetic) {
            if (us != null) {
                us.unequipCosmetics(true, true);
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
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPreEquip(final PlayerPreEquipCosmeticEvent e) {
        final LobbyPlayer lp = PM.getOplayer(e.getPlayer(), LobbyPlayer.class);
//e.getPlayer().sendMessage("§8log: PlayerOpenTreasureEvent ");
        if (!lp.hasFlag(LobbyFlag.GinTravelDone)) {
            e.setCancelled(true);
            return;
        }
        if (Quests.discover.isComplete(lp)) {
            final LCuboid lc = AreaManager.getCuboid(e.getPlayer().getLocation());
            if (lc != null && lc.getInfo() != CuboidInfo.SUMO) {
                return;
            }
        }
//e.getPlayer().sendMessage("§6getConfigPath="+e.getCosmeticType().getConfigPath()+" getVariableName="+e.getCosmeticType().getVariableName()+" getName="+e.getCosmeticType().getName());
        //final ProCosmetics api = ProCosmeticsProvider.get();
        switch (e.getCosmeticType().getCategory().getKey()) {
            case "mounts" -> {
                //e.getPlayer().sendMessage(" getVariableName="+e.getCosmeticType().getVariableName()+" getName="+e.getCosmeticType().getName());
                switch (e.getCosmeticType().getKey()) {
                    case "molten-snake", "ethereal-dragon", "hype-train", "pirate-ship" ->
                        e.setCancelled(true);
                }
            }
            case "gadgets" -> {
                //e.getPlayer().sendMessage(" getVariableName="+e.getCosmeticType().getVariableName()+" getName="+e.getCosmeticType().getName());
                switch (e.getCosmeticType().getKey()) {
                    case "ethereal-pearl", "rocket", "trampoline", "wither-missile" ->
                        e.setCancelled(true);
                }
            }
            case "morphs" -> {
                //e.getPlayer().sendMessage(" getVariableName="+e.getCosmeticType().getVariableName()+" getName="+e.getCosmeticType().getName());
                switch (e.getCosmeticType().getKey()) {
                    case "bat", "wither", "blaze" ->
                        e.setCancelled(true);
                }
            }
            default -> {
            }
        }

        if (e.isCancelled()) {
            e.getPlayer().sendMessage("§6*Вы не сможете использовать §с" + e.getCosmeticType().getKey() + " §6здесь!");
        }

//e.getPlayer().sendMessage("§6getConfigPath="+e.getCosmeticType().getConfigPath()+" getVariableName="+e.getCosmeticType().getVariableName()+" getName="+e.getCosmeticType().getName());
    }

    //PlayerPreEquipCosmeticEvent.class
    //PlayerPurchaseTreasureEvent.class
    //PlayerUnequipCosmeticEvent.class
    //PlayerUseGadgetEvent.class
    //CosmeticEntitySpawnEvent.class
}
