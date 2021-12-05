package ru.ostrov77.lobby.newbie;

import java.util.EnumSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Mob;

import com.destroystokyo.paper.entity.Pathfinder.PathResult;
import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;

import ru.ostrov77.lobby.Main;

public class GhastGoal implements Goal<Blaze> {
    
    private final GoalKey<Blaze> key;
    private final Mob mob;
    private final Location loc;
    private boolean noPth = true;
    
    public GhastGoal(final Mob mob, final Location loc) {
        this.key = GoalKey.of(Blaze.class, new NamespacedKey(Main.instance, "ghast"));
            this.mob = mob;
            this.loc = loc;
    }
 
    @Override
    public boolean shouldActivate() {
        return true;
    }
 
    @Override
    public boolean shouldStayActive() {
        return shouldActivate();
    }
 
    @Override
    public void start() {
    }
 
    @Override
    public void stop() {
        mob.getPathfinder().stopPathfinding();
        mob.setTarget(null);
        //cooldown = 100;
    }
 
        
        
        
    @Override
    public void tick() {
        if ((mob.getTicksLived() & 63) == 0 && noPth) {
            final PathResult path = mob.getPathfinder().findPath(loc);
            /*Не работает ибо в moveTo() надо
            final PathResult path = new PathResult() {
        	
        	private int ptn = 0;
        	
        	final ArrayList<Location> pts = new ArrayList<Location>(Arrays.asList(loc, loc.add(10, 10, 10), loc.add(-10, 10, -10)));
			
			@Override
			public List<Location> getPoints() {
				return pts;
			}
			
			@Override
			public int getNextPointIndex() {
				return ptn;
			}
			
			@Override
			public Location getNextPoint() {
				ptn++;
				return ptn < pts.size() ? pts.get(ptn) : null;
			}
			
			@Override
			public Location getFinalPoint() {
				return pts.get(pts.size() - 1);
			}
		};*/
            if (path!=null) {
            	noPth = false;
            	Bukkit.getConsoleSender().sendMessage(path.getNextPoint() + " " + path.getFinalPoint() + " " + path.getNextPointIndex());
                final boolean done = mob.getPathfinder().moveTo(path, 1.6d);
            }
        }
    }
 
    @Override
    public GoalKey<Blaze> getKey() {
        return key;
    }
 
    @Override
    public EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE, GoalType.LOOK);
    }
}