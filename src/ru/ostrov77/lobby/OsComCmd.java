package ru.ostrov77.lobby;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.inventory.SmartInventory;




public class OsComCmd implements CommandExecutor, TabCompleter {
    
    private final List <String> subCommands = Arrays.asList("newbiespawn", "flagdebug");

        
        
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
                case "newbiespawn":
                    p.teleport(Main.newBieSpawnLocation);// тп на 30 160 50
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
            }
        }

        p.sendMessage( "§cOsComCmd параметр?");


        return true;
    }
    



    
    
    

    
    
    
    
    
    
    
    
    









    
    
    


}
    
    
 