package ru.ostrov77.lobby.bots;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.destroystokyo.paper.entity.Pathfinder;
import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;

import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import ru.komiss77.modules.world.XYZ;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.bots.spots.EndSpot;
import ru.ostrov77.lobby.bots.spots.Spot;



public class BotGoal implements Goal<Husk> {
    
    public static final int MAX_LIVE_TICKS = 2000;
    
    private final GoalKey<Husk> key;
    private final Bot bot;
    
	private final List<Spot> last;
	private final Pathfinder pth;
	
	private Location tgt;
	private int lstSpTime;
	
	private WeakReference<LivingEntity> tgtLE;
	
	private int busy;
    
    public BotGoal(final Bot bot) {
        this.key = GoalKey.of(Husk.class, new NamespacedKey(Main.instance, "bot"));
        this.bot = bot;
		this.last = new ArrayList<>();
		this.pth = bot.rplc.getPathfinder();
		this.tgtLE = new WeakReference<LivingEntity>(null);
		this.tgt = null;
		this.busy = 0;
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
		bot.remove(false, true);
    }
    
    @Override
    public void tick() {
		if (bot.rplc == null || !bot.rplc.isValid() || bot.rplc.getTicksLived() > MAX_LIVE_TICKS) {
			bot.remove(true, true);
			return;
		} else {
			//Bukkit.broadcast(Component.text("le-" + bot.rplc.getName()));
			final Location loc = bot.rplc.getLocation();
			final Vector vc;
			//Bukkit.broadcast(Component.text("le-" + tgtLE.get()));
			
			if (busy != 0) {
				pth.stopPathfinding();
				busy--;
				if (tgtLE.get() != null) {
					tgt = tgtLE.get().getLocation();
					//Bukkit.broadcast(Component.text("tgt-" + tgt.toVector().toString()));
					vc = new Vector(tgt.getX() - loc.getX(), tgt.getY() - loc.getY(), tgt.getZ() - loc.getZ());
					bot.move(loc, vc, true);
					if ((busy & 7) == 0 && Main.rnd.nextBoolean()) {
						loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 0.6f, 1f);
						BotManager.sendWrldPckts(bot.s, new PacketPlayOutAnimation(bot, 0));
					}
				}
				return;
			}
			
			if (last.isEmpty()) {
				final Spot sp = BotManager.getCloseSpot(loc, Collections.emptyList());
				if (sp == null) {
					bot.remove(true, true);
					return;
				}
				last.add(sp);
				tgt = sp.getLoc().getCenterLoc();
				lstSpTime = 200;
				return;
			} else if ((lstSpTime--) == 0) {
				pth.stopPathfinding();
				final Spot sp = BotManager.getCloseSpot(loc, last);
				if (sp == null) {
					bot.remove(true, true);
					return;
				}
				last.add(sp);
				tgt = sp.getLoc().getCenterLoc();
				lstSpTime = 200;
				return;
			} else {
				if (tgt.distanceSquared(loc) < 8d) {
					pth.stopPathfinding();
					final Spot lst = last.get(last.size() - 1);
					switch (lst.getType()) {
					case END:
						final XYZ prtl = ((EndSpot) lst).getPortal();
						if (prtl != null) {
							tgt = prtl.getCenterLoc();
							pth.moveTo(tgt, 1.4d);
							lstSpTime = 240;
							break;
						}
					case WALK:
						if (Main.rnd.nextBoolean()) {
							final LivingEntity close = getCloseLE(loc, 8, bot.rid);
							if (close != null) {
								tgtLE = new WeakReference<LivingEntity>(close);
								busy = 40;
								return;
							}
						}
					case SPAWN:
					default:
						final Spot sp = BotManager.getCloseSpot(loc, last);
						if (sp == null) {
							bot.remove(true, true);
							return;
						}
						last.add(sp);
						tgt = sp.getLoc().getCenterLoc();
						lstSpTime = 200;
						return;
					}
				}
			}

			vc = new Vector(tgt.getX() - loc.getX(), tgt.getY() - loc.getY(), tgt.getZ() - loc.getZ());
			
			//attackMelee(ln);
			
			bot.pickupIts(loc);
			
			bot.move(loc, vc, true);
			
			vc.normalize();

			if (bot.tryJump(loc, vc)) {
				return;
			}

			if (bot.tryLadder(loc.getBlock(), vc)) {
				return;
			}
			
			vc.setY(0d);
			if (bot.rplc.isInWater()) {
				bot.rplc.setVelocity(bot.rplc.getVelocity().setY(0.1d));
			} else {
				if (bot.rplc.isOnGround()) {
					pth.moveTo(tgt, 1.4d);
				} else {
					if (pth.hasPath()) pth.stopPathfinding(); 
					bot.rplc.setVelocity(bot.rplc.getVelocity().add(vc.multiply(0.05d)));
				}
			}
		}
    }
    
    private LivingEntity getCloseLE(final Location loc, final int dst, final int botID) {
		final int X = loc.getBlockX(), Y = loc.getBlockY(), Z = loc.getBlockZ();
		LivingEntity cls = null;
		int dd = Integer.MAX_VALUE;
		for (final LivingEntity le : loc.getWorld().getLivingEntities()) {
			final Location ll = le.getLocation();
			final int d = Math.abs(ll.getBlockX() - X) + Math.abs(ll.getBlockY() - Y) + Math.abs(ll.getBlockZ() - Z);
			if (d < dd && !(le instanceof HumanEntity) && le.getEntityId() != botID) {
				cls = le;
				dd = d;
			}
		}
		return dd < dst ? cls : null;
	}

	@Override
    public GoalKey<Husk> getKey() {
        return key;
    }
    
    @Override
    public EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE, GoalType.LOOK);
    }
}