package ru.ostrov77.lobby.newbie;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.lobby.FlagsDebug;




public class OsComCmd implements CommandExecutor, TabCompleter {
    
    private final List <String> subCommands = Arrays.asList("newbie", "menu", "flagdebug", "ghast");

        
        
    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmnd, String command, String[] args) {
        
        final List <String> sugg = new ArrayList<>();
        switch (args.length) {
            
            case 1:
                //0- пустой (то,что уже введено)
                for (final String s : subCommands) {
                    if (s.startsWith(args[0])) sugg.add(s);
                }

                break;


        }
        
       return sugg;
    }    
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String string, String[] arg) {
        
        if ( ! (cs instanceof Player) ) {
            cs.sendMessage("§cНе консольная команда!");
            return true;
        }
        final Player p = (Player) cs;
        
        if (arg.length==1) {
            
            switch (arg[0]) {
                
                case "newbie":
                    if (NewBie.hasNewBieTask(p)) {
                        p.sendMessage("§cВы уже в процессе!");
                        return true;
                    }
                    if (arg.length==2) {
                        p.sendMessage("§cУкажите стадию от 1 до 3!");
                        int stage = ApiOstrov.getInteger(arg[1]);
                        if (stage<1 || stage>3) {
                            p.sendMessage("§cCтадия - число от 1 до 3!");
                            return true;
                        }
                        NewBie.start(p, stage);
                        return true;
                    } else {
                        NewBie.start(p, 0);
                    }
                    
                    //p.teleport(Main.newBieSpawnLocation);// тп на 30 160 50
                    return true;
                    
                case "flagdebug":
                    if (ApiOstrov.isLocalBuilder(cs, true)) {
                        SmartInventory
                            .builder()
                            .id("flags"+p.getName())
                            .provider(new FlagsDebug())
                            .size(6, 9)
                            .title("флаги лобби")
                            .build()
                            .open(p);
                    }
                    return true;
                    
                case "menu":
                    //if (ApiOstrov.isLocalBuilder(cs, true)) {
                    SmartInventory.builder()
                        .type(InventoryType.HOPPER)
                        .id("oscom"+p.getName()) 
                        .provider(new OsComMenu())
                        .title("§aОсКом")
                        .build()
                        .open(p);
                        //}
                    return true;
                    
                case "ghast":
                	final Blaze gh = (Blaze) p.getWorld().spawnEntity(p.getLocation(), EntityType.BLAZE);
                    gh.addPassenger(p);
                	Bukkit.getMobGoals().removeAllGoals(gh);
                    final GhastGoal goal = new GhastGoal(gh, new Location(p.getWorld(), 0, 0, 0));
                    Bukkit.getMobGoals().addGoal(gh, 1, goal);
                    return true;
            }
        }

        p.sendMessage( "§cOsComCmd параметр?");


        return true;
    }
    



    
    
    

    
    
    
    
    
    
    
    
    









    
    
    


}
    
    
 