package ru.ostrov77.lobby.area;


import ru.ostrov77.lobby.newbie.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.inventory.InventoryType;
import ru.komiss77.ApiOstrov;
import ru.komiss77.builder.SetupMode;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.lobby.area.AreaMainMenu;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.LCuboid;




public class AreaCmd implements CommandExecutor, TabCompleter {
    

        
        
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
    
    
    
    
    
    
    private final List <String> subCommands = Arrays.asList("create","list");
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String string, String[] arg) {
        
        if ( ! (cs instanceof Player) ) {
            cs.sendMessage("§cНе консольная команда!");
            return true;
        }
        final Player p = (Player) cs;
        
        if (!ApiOstrov.isLocalBuilder(cs, true)) return false;
        
        final Oplayer op = PM.getOplayer(p);
        final SetupMode setup = op.setup;
        
        if (setup==null) {
            cs.sendMessage("§cрежим билдера не активирован! (/builder)");
            return true;
        }
        
        
        if (arg.length==1) {
            
            switch (arg[0]) {


                case "create":
                    if (setup.getCuboid()==null) {
                        cs.sendMessage("");
                        cs.sendMessage("§6В меню билдера создай новый схематик, но НЕ СОХРАНЯЙ, а повтори area create");
                        cs.sendMessage("§6При создании схематика название - любое (уникальное), параметр - уникальный ИД от 1 до 32");
                        cs.sendMessage("");
                        return true;
                    }
                    if (!p.getWorld().getName().equals("world") || !setup.pos1.getWorld().getName().equals("world")) {
                        cs.sendMessage("§cлокации создаются только в world!");
                        return true;
                    }
                    if (AreaManager.getCuboid(setup.schemName)!=null) {
                        cs.sendMessage("§cлокация с названием "+setup.schemName+" уже есть!");
                        return true;
                    }
                    int id = ApiOstrov.getInteger(setup.param);
                    if (id<1 || id>32) {
                        cs.sendMessage("§cID локации - число от 1 до 32");
                        return true;
                    }
                    if (AreaManager.getCuboid(id)!=null) {
                        cs.sendMessage("§cлокация с ID "+id+" уже есть!");
                        return true;
                    }
                    
                    //пров.перекрытие
                    LCuboid overlap = null;
                    Location loc;
                    Iterator<Location> it = setup.getCuboid().borderIterator(p.getWorld());
                    while (it.hasNext()) {
                        loc = it.next();
                        if ( AreaManager.getCuboid(loc)!=null) {
                            overlap = AreaManager.getCuboid(loc);
                            break;
                        }
                    }
                    if (overlap!=null) {
                        cs.sendMessage("§cвыделение пересекается с "+overlap.name+" (ID="+overlap.id+")");
                        return true;
                    }
                    
                    final LCuboid lc = new LCuboid(id, setup.schemName, setup.pos1, setup.pos2);
                    AreaManager.addCuboid(lc);
                    cs.sendMessage("§aЛокация добавлена!");
                    return true;
                    
                    
                case "list":
                    SmartInventory
                        .builder()
                        .id("area"+p.getName())
                        .provider(new AreaMainMenu())
                        .size(6, 9)
                        .title("локации")
                        .build()
                        .open(p);
                    return true;
            }
        }

        p.sendMessage( "§cArea параметр?");


        return true;
    }
    



    
    
    

    
    
    
    
    
    
    
    
    









    
    
    


}
    
    
 