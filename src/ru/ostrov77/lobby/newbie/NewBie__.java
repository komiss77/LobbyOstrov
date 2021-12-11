package ru.ostrov77.lobby.newbie;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.ostrov77.lobby.Main;



public class NewBie__ implements Listener {
  /*  
    private static final Map <String,NewBieTask__> tasks = new HashMap<>();
    //protected static final ItemStack clock = new ItemBuilder(Material.CLOCK).name("§aОсКом").lore("временный коммуникатор").build();

    public NewBie () {
    }


    public static void start(final Player p, final int stage) {

        PM.getOplayer(p).hideScore();
        
        cancelNewBieTask(p);
        //if (tasks.containsKey(p.getName())) {
        //    tasks.get(p.getName()).cancel();
            //tasks.remove(p.getName()); //удалится само в NewBieTask
        //}

        NewBieTask__ nbt =  new NewBieTask__(p); //добавится само в NewBieTask
        /*switch (stage) {
            case 1: 
                nbt.tick = 0;
                break;
            case 2: 
                nbt.tick = 530;
                p.setGameMode(GameMode.SURVIVAL); 
                break;
            case 3:
                p.setGameMode(GameMode.SURVIVAL); 
                p.teleport(NewBie.spawnNbShip);
                nbt.tick = 700;
                break;
            default:
                break;
        }/
        tasks.put(p.getName(),nbt);
    
    }

    public static void stop(final Player p) {
        if (cancelNewBieTask(p)) {
            p.teleport(Main.spawnLocation);
            final Oplayer op = PM.getOplayer(p);
            op.score.getSideBar().reset();
            //op.showScore();
            Main.giveItems(p);
p.sendMessage("§6Интро прервано");
        } else {
p.sendMessage("§6Интро не было запущено.");
        }
    }
    

    
    
    
    
    
    

    
    @EventHandler (priority = EventPriority.MONITOR)
    public void onTeleport(final PlayerTeleportEvent e) {
        if (hasNewBieTask(e.getPlayer())) {
            stop(e.getPlayer());
        }
    }
    
    
    
    
    
    
    
    
    public static boolean hasNewBieTask(final Player p) {
        return tasks.containsKey(p.getName()) && tasks.get(p.getName())!=null;
    }
    public static boolean cancelNewBieTask(final Player p) {
        if (hasNewBieTask(p)) {
            tasks.remove(p.getName()).cancel();
            return true;
        }
        return false;
    }
    
    
    
    

    
    
    
    
    
    
    
    
    
    public static void log(String s) { 
        Bukkit.getConsoleSender().sendMessage("§fNewBie : " + s);
        //Bukkit.broadcastMessage("§8NewBie : " + s);
    }

    
    */

    
    
    
    
}


























       // log ("inter "+e.getAction()+" item="+e.getItem()+" block="+e.getClickedBlock()+" point="+e.getInteractionPoint());
       // if (e.getAction()==Action.RIGHT_CLICK_AIR && e.getItem()!=null && e.getItem().getType()==Material.NAME_TAG) {
            //log("title");
            //p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 5));
         //   return;
       // }
       /* if (p.isOp() && e.getAction()==Action.RIGHT_CLICK_AIR && e.getItem()!=null && e.getItem().getType()==Material.NETHER_STAR) {
            log("spawn NETHER_STAR");
            for (int x=-25; x<25; x+=6) {
                //if (x>-10 && x<10) continue;
                for (int z=-25; z<25; z+=6) {
                    //if (z>-10 && z<10) continue;
                    for (int y=100; y<150; y+=6) {
                        if (x>-17 && x<17 && z>-17 && z<17 && y>113 && y<143) continue;
                        final Location loc = p.getWorld().getBlockAt(x+ApiOstrov.randInt(-2, 2), y+ApiOstrov.randInt(-2, 2), z+ApiOstrov.randInt(-2, 2)).getLocation();
                        final Item item = loc.getWorld().dropItem(loc, new ItemStack(Material.NETHER_STAR));
                        item.setGlowing(true);
                        item.setGravity(false);
                        item.setCanMobPickup(false);
                        item.setInvulnerable(true);
                    }
                }
            }
            return;
        }   */
        
        

        //if (e.getAction()==Action.RIGHT_CLICK_BLOCK && e.getItem()!=null && e.getItem().getType()==Material.STICK) {
       //     start(p, "");
       // }
