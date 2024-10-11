package ru.ostrov77.lobby.bots;

import java.util.EnumSet;
import com.destroystokyo.paper.entity.Pathfinder;
import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;
import ru.komiss77.modules.bots.Botter;
import ru.komiss77.modules.world.AStarPath;
import ru.komiss77.modules.world.WXYZ;
import ru.komiss77.utils.LocUtil;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.bots.spots.Spot;
import ru.ostrov77.lobby.bots.spots.SpotType;

public class LobbyGoal implements Goal<Mob> {

    public static final int MAX_LIVE_TICKS = 2000;
    public static final int TALK_TIME = 80;

    private static final GoalKey<Mob> key = GoalKey.of(Mob.class, new NamespacedKey(Main.instance, "bot"));

    private final LobbyBot ext;
    private final Botter bt;
    private final Mob rplc;
    private final Pathfinder pth;
    private final AStarPath arp;

    private Spot tgt;
    private Mob tgtMb;
    private int talk;

    public LobbyGoal(final LobbyBot ext, final Botter bot, final Mob mb) {
        this.ext = ext;
        this.rplc = mb;
        this.bt = bot;
        this.pth = mb.getPathfinder();
        this.arp = new AStarPath(mb, 1000, true);
        this.talk = 0;
    }

    @Override
    public boolean shouldActivate() {
        return true;
    }

    @Override
    public boolean shouldStayActive() {
        return true;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        ext.remove(bt);
    }

    @Override
    public void tick() {
        if (rplc == null || !rplc.isValid() || rplc.getTicksLived() > MAX_LIVE_TICKS) {
            bt.remove();
            return;
        }

        final Location loc = rplc.getLocation();

        final Vector vc;
        if (talk == 0) {
            final Location dir = arp.getNextLoc();
            vc = dir == null ? rplc.getEyeLocation().getDirection() : dir.subtract(loc).toVector().setY(0d);
            if (tgt == null) {
                final Spot sp = SpotManager.getRndSpot(SpotType.END);
                if (sp == null) {
                    loc.getWorld().spawnParticle(Particle.SOUL, loc, 40, 0.6d, 0.6d, 0.6d, 0d, null, false);
                    loc.getWorld().playSound(loc, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1f, 1f);
                    bt.remove();
                    return;
                } else {
                    tgt = sp;
                    arp.setTgt(new WXYZ(loc.getWorld(), sp.getLoc()));
                }
            }

            if (!arp.hasTgt()) {
                tgtMb = LocUtil.getClsChEnt(loc, 4d, Mob.class, e -> e.getType() != rplc.getType());
                if (tgtMb == null) {
                    loc.getWorld().spawnParticle(Particle.SOUL, loc, 40, 0.6d, 0.6d, 0.6d, 0d, null, false);
                    loc.getWorld().playSound(loc, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1f, 1f);
                    bt.remove();
                    return;
                }
                talk = TALK_TIME;
                tgt = null;
            }
        } else if (tgtMb != null && tgtMb.isValid()) {
            pth.stopPathfinding();
            talk--;
            vc = tgtMb.getLocation().subtract(loc).toVector();
            if ((talk & 7) == 0 && Main.rnd.nextBoolean()) {
                loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 0.6f, 1f);
                bt.swingHand(true);
                if (Main.rnd.nextInt(4) == 0) {
                    loc.getWorld().spawnParticle(Particle.SOUL, loc, 40, 0.6d, 0.6d, 0.6d, 0d, null, false);
                    loc.getWorld().playSound(loc, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1f, 1f);
                    bt.remove();
                    return;
                }
            }
        } else {
            final Location dir = arp.getNextLoc();
            vc = dir == null ? rplc.getEyeLocation().getDirection() : dir.subtract(loc).toVector().setY(0d);
        }

        if (talk == 0) {
            arp.tickGo(1.4f);
        }

        bt.move(loc, vc, true);
    }

    @Override
    public GoalKey<Mob> getKey() {
        return key;
    }

    @Override
    public EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE, GoalType.LOOK);
    }
}
