package ru.ostrov77.lobby.area;


import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ru.komiss77.ApiOstrov;
import ru.komiss77.builder.SetupMode;
import ru.komiss77.builder.SetupMode.LastEdit;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.inventory.SmartInventory;




public class AreaCmd implements CommandExecutor, TabCompleter {
    

        
        
    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmnd, String command, String[] args) {
        
        final List <String> sugg = new ArrayList<>();
        switch (args.length) {
            
            case 1:
                //0- пустой (то,что уже введено)
               // for (final String s : subCommands) {
              //      if (s.startsWith(args[0])) sugg.add(s);
              //  }

                break;


        }
        
       return sugg;
    }    
    
    
    
    
    
    
    //private final List <String> subCommands = Arrays.asList("create","list");
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String string, String[] arg) {
        
        if ( ! (cs instanceof Player) ) {
            cs.sendMessage("§cНе консольная команда!");
            return true;
        }
        final Player p = (Player) cs;
        
        if (!ApiOstrov.isLocalBuilder(cs, true)) return false;
        
        if (!p.getWorld().getName().equals("world")) {
            cs.sendMessage("§cлокации работают только в world!");
            return true;
        }
        
        final Oplayer op = PM.getOplayer(p);
        //final SetupMode setup = op.setup;
        
        if (op.setup==null) {
            //cs.sendMessage("§cрежим билдера не активирован! (/builder)");
            op.setup = new SetupMode(p);
            //return true;
        }
        //выдать свой предмет, или билдер открывает меню схематика!
        
       // if (arg.length==1) {
            
         //   switch (arg[0]) {


               // case "create":
                   // if (setup.getCuboid()==null) {
                   //     cs.sendMessage("");
                  //      cs.sendMessage("§6В меню билдера создай новый схематик, но НЕ СОХРАНЯЙ, а повтори area create");
                  //      cs.sendMessage("§6При создании схематика название - любое (уникальное), параметр - уникальный ИД от 1 до 32");
                  ////      cs.sendMessage("§6Отображаемое название потом можно изменить в §barea list");
                 //       cs.sendMessage("");
                 //       return true;
                 //   }
                    //if (AreaManager.getCuboid(setup.schemName)!=null) {
                    //    cs.sendMessage("§cлокация с названием "+setup.schemName+" уже есть!");
                    //    return true;
                    //}
                    //int id = ApiOstrov.getInteger(setup.param);
                   // if (id<1 || id>32) {
                   //     cs.sendMessage("§cID локации - число от 1 до 32");
                   //     return true;
                   // }
                   // if (AreaManager.getCuboid(id)!=null) {
                   //     cs.sendMessage("§cлокация с ID "+id+" уже есть!");
                  //      return true;
                  //  }
                    
                    //пров.перекрытие
                    //LCuboid overlap = null;
                   // Location loc;
                    //Iterator<Location> it = setup.getCuboid().borderIterator(p.getWorld());
                    //while (it.hasNext()) {
                    //    loc = it.next();
                    //    if ( AreaManager.getCuboid(loc)!=null) {
                   //         overlap = AreaManager.getCuboid(loc);
                   //         break;
                   //     }
                   // }
                   // if (overlap!=null) {
                     //   cs.sendMessage("§cвыделение пересекается с "+overlap.name+" (ID="+overlap.id+")");
                    //    return true;
                   // }
                    
                    //final LCuboid lc = new LCuboid(id, setup.schemName, setup.pos1, setup.pos2);
                    //AreaManager.addCuboid(lc, true);
                 //   cs.sendMessage("§aЛокация добавлена и сохранена на диск!");
                //    return true;
                    
                    
               // case "list":
                    
           // }
       // }

      //  p.sendMessage( "§cArea параметр?");
        if (op.setup.lastEdit==LastEdit.SchemEdit) {
            openAreaEditMenu(p, op.setup.schemName);
        } else {
            openAreaMainMenu(p);
        }

        return true;
    }
    


    public static void openAreaMainMenu(final Player p) {
        final Oplayer op = PM.getOplayer(p);
        op.setup.lastEdit = LastEdit.SchemMain;
        SmartInventory
            .builder()
            .id("area"+p.getName())
            .provider(new AreaMainMenu())
            .size(6, 9)
            .title("локации")
            .build()
            .open(p);
    }    
    
    public static void openAreaEditMenu(final Player p, final String schemName) {
        final Oplayer op = PM.getOplayer(p);
        if (schemName.isEmpty()) {
            op.setup.schemName=schemName;
            op.setup.extra1=schemName;
            openAreaMainMenu(p);
            return;
        }
        op.setup.lastEdit = LastEdit.SchemEdit;
        op.setup.schemName = schemName;
        SmartInventory.builder()
            .id("area"+schemName+p.getName())
            .provider(new AreaEditor())
            .size(6, 9)
            .title("§9Локация "+schemName)
            .build().open(p);
    }    
    
    

    
    
    
    
    
    
    
    
    









    
    
    


}
    
    
 
