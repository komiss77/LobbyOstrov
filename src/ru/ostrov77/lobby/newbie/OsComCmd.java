package ru.ostrov77.lobby.newbie;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.lobby.DebugMenu;
import ru.ostrov77.lobby.area.AreaViewMenu;
import ru.ostrov77.lobby.quest.QuestViewMenu;




public class OsComCmd implements CommandExecutor, TabCompleter {
    
    private final List <String> subCommands = Arrays.asList(  "debug", "ghast", "quest", "area");

        
        
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
                
              /*  case "newbieTest":
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
                    return true;*/
                    
                case "debug":
                    if (ApiOstrov.isLocalBuilder(cs, true)) {
                        SmartInventory
                            .builder()
                            .id("flags"+p.getName())
                            .provider(new DebugMenu())
                            .size(3, 9)
                            .title("меню отладки")
                            .build()
                            .open(p);
                    }
                    return true;
                    
              /*  case "newbieMenu":
                    //if (ApiOstrov.isLocalBuilder(cs, true)) {
                    SmartInventory.builder()
                        .type(InventoryType.HOPPER)
                        .id("oscom"+p.getName()) 
                        .provider(new OsComMenu())
                        .title("§aОсКом")
                        .build()
                        .open(p);
                        //}
                    return true;*/
                    
               case "quest":
                    //if (ApiOstrov.isLocalBuilder(cs, true)) {
                    SmartInventory.builder()
                        .type(InventoryType.CHEST)
                        .id("quest"+p.getName()) 
                        .provider(new QuestViewMenu())
                        .title("Задания")
                        .size (5,9)
                        .build()
                        .open(p);
                        //}
                    return true;
                    
               case "area":
                    //if (ApiOstrov.isLocalBuilder(cs, true)) {
                    SmartInventory.builder()
                        .type(InventoryType.CHEST)
                        .id("area"+p.getName()) 
                        .provider(new AreaViewMenu())
                        .title("Локации")
                        .size (5,9)
                        .build()
                        .open(p);
                        //}
                    return true;
                    
                //case "openCosmetics":
                    //ProCosmeticsAPI.openMainMenu(p);
                    //ProCosmeticsAPI.getUser(p).getAbstract3DMenu().run();
                   // return true;
                     
               // case "unequipCosmetics":
                   //ProCosmeticsAPI.getUser(p).fullyUnequipCosmetics(true);
                 //  return true;
                     
                case "t":
                    return true;
            }
        }

        p.sendMessage( "§cOsComCmd параметр?");


        return true;
    }
    



    
    
    

    
    
    
    
    
    
    
    
    









    
    
    


}
    
    
 
