package ru.ostrov77.lobby.bots;

import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import com.destroystokyo.paper.entity.ai.Goal;
import ru.komiss77.listener.ChatLst;
import ru.komiss77.modules.bots.BotEntity;
import ru.komiss77.modules.player.PM;
import ru.komiss77.modules.quests.QuestManager;
import ru.komiss77.modules.world.WXYZ;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.quest.Quests;


public class LobbyBot extends BotEntity {

	public LobbyBot(final String name, final WXYZ loc) {
		super(name, loc.w);
		telespawn(loc.getCenterLoc(), null);
		tab("", ChatLst.NIK_COLOR, "");
                tag("", ChatLst.NIK_COLOR, "");
	}
	
	@Override
	public Goal<Mob> getGoal(final Mob org) {
		return new LobbyGoal(this, org);
	}
	
	@Override
	public void onDamage(final EntityDamageEvent e) {
		if (e instanceof final EntityDamageByEntityEvent ee) {
			if (ee.getDamager() instanceof final Player pl) {
				QuestManager.complete(pl, PM.getOplayer(pl, LobbyPlayer.class), Quests.greet);
			}
		}
		super.onDamage(e);
	}
	
	@Override
	public void onDeath(EntityDeathEvent e) {
		remove();
	}
	
}
