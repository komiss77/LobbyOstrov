package ru.ostrov77.lobby;

import java.util.EnumSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.komiss77.modules.world.XYZ;



public class JinGoal implements Goal<Blaze> {
    
    public static final String ginName = "§6Исполняю желания!";
    
    private static final XYZ[] points = {
        new XYZ("world",-8,150,-72),
        new XYZ("world",-44,138,-48),
        new XYZ("world",-55,125,-16),
        new XYZ("world",-37,114,19),
        new XYZ("world",-8,108,35),
        new XYZ("world",9,105,34),
        new XYZ("world",13,102,26)
    };
    
    private final GoalKey<Blaze> key;
    private final Mob mob;
    
    private int currPointIndex = 0;
    private Vector moveVector;
    
    private XYZ nextPoint;
    private int previosDistance = Integer.MAX_VALUE;
    private boolean arrive;
    
    
    
    
    public JinGoal(final Mob mob) {
        this.key = GoalKey.of(Blaze.class, new NamespacedKey(Main.instance, "ghast"));
        this.mob = mob;
    }
 
    @Override
    public boolean shouldActivate() {
        return true;
    }
 
    @Override
    public boolean shouldStayActive() {
        return !arrive;//shouldActivate();
    }
 
    @Override
    public void start() {
    }
 
    @Override
    public void stop() {
        //mob.getPathfinder().stopPathfinding();
        //mob.setTarget(null);
        //cooldown = 100;
    }
 
        
        
        
    @Override
    public void tick() {
        
      

        
        if (arrive && mob.getTicksLived()>=1000) { //аварийно, на случай потеряшки джина
            mob.remove();
            //log("§5 remove=2");
            return;
        }

        
        if ( !arrive && mob.getTicksLived()%3 == 0 ) { //3-просто для разгрузки сервера
            
            if (mob.getTicksLived()<400 && mob.getPassengers().isEmpty()) { //обычно прибытие на 440, это если игрок отключится
                mob.remove();
                //log("§5 remove=1");
                return;
            }

            if (mob.getTicksLived()>=700) { //обычно результат 440. аварийно, на случай полной потеряшки c игроком
                arrive();
                return;
            }
            
            nextPoint = points[currPointIndex];

            int currDist = nextPoint.distSq(mob.getLocation());//getDist();
//log("currDist="+currDist+" previos="+previosDistance);            
            if (previosDistance<=currDist) { //предыдущая дистанция меньше или равна - значит пролетел и начал удаляться
                currPointIndex++;
                
                if (currPointIndex>=points.length) {
                    arrive();
                    return;
                }
                
                previosDistance = Integer.MAX_VALUE; //расчёт дистанции до след.точки
                moveVector=null; //сброс для пересчёта ниже
                //log("§6currPointIndex="+currPointIndex);
                return;
                
            } else { //предыдущая дистанция больше - значит приближается  долетел (или пролетел мимо) и начал удаляться. после перелёта цели текущая станет больше предыдущей
                previosDistance = currDist;//currDist; //запоминаем текущее расстояние для сравнения на в след.раз
            }
            
            
            if (moveVector==null) { //расчёт нового вектора после сброса старого в поворотной точке
                //log("§5 target="+target.toString());
                final Vector from = mob.getLocation().toVector();
                //log("§5 Vector from="+from);
                final Vector to = new Vector(nextPoint.x, nextPoint.y, nextPoint.z);
                //log("§5 x="+target.x+" y="+target.y+" z="+target.z);
                //log("§5 Vector to="+to);
                moveVector = to.subtract(from).normalize().multiply(0.6);//targetVector.subtract(mob.getLocation().toVector()).multiply(0.01f);
                //log("§5 moveVector="+moveVector);
                
                mob.lookAt(nextPoint.x, nextPoint.y, nextPoint.z);
            }

//final Player p = (Player) mob.getPassengers().get(0);
//if (p!=null && p.getInventory().getItemInMainHand().getType()==Material.COMPASS) {
            mob.setVelocity(moveVector);
    //log("§6 setVelocity "+moveVector);
//}
            
        }

    }
 
    
    
    private void arrive() {
        //mob.setVelocity(new Vector(0, currPointIndex, 0));
        if (mob.getPassengers().get(0).getType()==EntityType.PLAYER) {
            arrive = true;
            final Player p = (Player) mob.getPassengers().get(0);
            Main.arriveNewBie(p);
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
    
    
    //private static void log(final String s) {
    //    Bukkit.getConsoleSender().sendMessage("§8log: "+s);            
    //}

    


       
}