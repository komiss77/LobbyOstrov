package ru.ostrov77.lobby.bots;

import com.destroystokyo.paper.entity.ai.Goal;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.Nullable;
import ru.komiss77.listener.ChatLst;
import ru.komiss77.modules.bots.Botter;
import ru.komiss77.modules.player.PM;
import ru.komiss77.modules.quests.QuestManager;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.quest.Quests;


public class LobbyBot implements Botter.Extent {

    @Override
    public void create(Botter bt) {
        bt.tab("", ChatLst.NIK_COLOR, "");
        bt.tag("", ChatLst.NIK_COLOR, "");
    }

    public void remove(Botter botter) {}
    public void teleport(Botter botter, LivingEntity livingEntity) {}
    public void spawn(Botter botter, @Nullable LivingEntity livingEntity) {}
    public void hide(Botter botter, @Nullable LivingEntity livingEntity) {}

    public void click(Botter botter, PlayerInteractAtEntityEvent e) {
        final Player pl = e.getPlayer();
        QuestManager.complete(pl, PM.getOplayer(pl, LobbyPlayer.class), Quests.greet);
    }

    public void death(Botter bt, EntityDeathEvent e) {
        e.getDrops().clear();
        bt.remove();
    }

    public void damage(Botter bt, EntityDamageEvent e) {
        if (e instanceof final EntityDamageByEntityEvent ee) {
            if (ee.getDamager() instanceof final Player pl) {
                QuestManager.complete(pl, PM.getOplayer(pl, LobbyPlayer.class), Quests.greet);
            }
        }
        Botter.Extent.super.damage(bt, e);
    }

    public void pickup(Botter botter, Location location) {}
    public void drop(Botter botter, Location location) {}

    public Goal<Mob> goal(Botter bt, Mob mb) {
        return new LobbyGoal(this, bt, mb);
    }
}
