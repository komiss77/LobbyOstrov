package ru.ostrov77.lobby;


import ru.ostrov77.lobby.hd.HD;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import ru.komiss77.ApiOstrov;
import ru.komiss77.LocalDB;
import ru.komiss77.Ostrov;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.AreaViewMenu;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.quest.Advance;
import ru.ostrov77.lobby.quest.Quest;
import ru.ostrov77.lobby.quest.QuestManager;
import ru.ostrov77.lobby.quest.QuestViewMenu;




public class OsComCmd implements CommandExecutor, TabCompleter {
    
    private final List <String> subCommands = Arrays.asList(  "debug", "gin", "quest", "area", "reset");
    private static final Set<String>ginOwner = new HashSet<>();
        
        
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
        
        final LobbyPlayer lp = Main.getLobbyPlayer(p);
        final LCuboid lc = AreaManager.getCuboid(p.getLocation());
        
        
        
        if (arg.length==1) {
            
            switch (arg[0]) {
                    
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
                    
                case "reset":
                    //if (ApiOstrov.isLocalBuilder(cs, true)) {
                        p.closeInventory();
                        Advance.resetProgress(p);
                        ApiOstrov.sendToServer(p, "arcaim", "");
                        Ostrov.async(()-> LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `lobbyData` WHERE `name` = '"+p.getName()+"';"), 20);
                    //}
                    return true;

                    
               case "quest":
                    if (HD.isOpen(p)) return true; //с компасом в руке ждём клики по голограммам
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
                    if (Main.holo) {
                        if (HD.isOpen(p)) return true; //с компасом в руке ждём клики по голограммам
                        //if (!Timer.has(p, "menu")) {
                            HD.openAreaMenu(p, lp);
                            //Timer.add(p, "menu", 1);
                        //}
                    } else {
                        SmartInventory.builder()
                            .type(InventoryType.CHEST)
                            .id("area"+p.getName()) 
                            .provider(new AreaViewMenu())
                            .title("Локации")
                            .size (6,9)
                            .build()
                            .open(p);
                    }
                    return true;
                    
                    
                    
                case "gin":
                    if (!ApiOstrov.isLocalBuilder(p) && lp.hasFlag(LobbyFlag.NewBieDone)) {
                        if (ApiOstrov.canBeBuilder(p)) {
                            p.sendMessage("§cвключи гм1!");
                        } else {
                            p.sendMessage("§cМогут только новички!");
                        }
                        //p.sendMessage("§cМогут только новички!");
                        return true;
                    }
                    if (p.getVehicle()!=null) {
                        p.sendMessage("§cНадо спешиться!");
                        return true;
                    }
                    if (lc==null || !lc.getName().equals("newbie")) {
                        p.sendMessage("§cНадо быть на кораблике!");
                        return true;
                    }
                    if (ginOwner.contains(p.getName())) {
                        p.sendMessage("§6Джин уже выходит!");
                        return true;
                    }
                    ginOwner.add(p.getName());
                    p.sendMessage("§6Кажется, сработало!");
                    Main.showGinHopper(Main.getLocation(Main.LocType.ginLampShip).clone(), false);
                    QuestManager.tryCompleteQuest(p, lp, Quest.SpawnGin);
                    //if (lp.questAccept.contains(Quest.SpawnGin)) {
                    //    lp.questDone(p, Quest.SpawnGin, false);
                     //   QuestManager.completeAdv(p, Quest.SpawnGin);
                    //}
                    p.playSound(Main.getLocation(Main.LocType.ginLampShip), Sound.ENTITY_EVOKER_PREPARE_WOLOLO, 5, 1);
                    

                    Ostrov.sync( ()->{
                        if (!p.isOnline() || !AreaManager.getCuboid("newbie").hasPlayer(p)) {
                            Ostrov.log_warn("spawn gin phase 1 : !p.isOnline()");
                            ginOwner.remove(p.getName());
                            return;
                        }
                        final Blaze gin = spawnGin();//(Blaze) entity;
                          
                            Ostrov.sync( ()->{
                                if (gin.isDead()) {
                                    Ostrov.log_warn("spawn gin phase 2 : gin isDead");
                                    ginOwner.remove(p.getName());
                                    return;
                                }
                                if (!p.isOnline() || !AreaManager.getCuboid("newbie").hasPlayer(p) ) {
                                    gin.remove();
                                    Ostrov.log_warn("spawn gin phase 2 : !p.isOnline()");
                                    ginOwner.remove(p.getName());
                                    return;
                                }
                                final Mob mob = (Mob) gin;
                                final JinGoal goal = new JinGoal(mob);
                                gin.addPassenger(p);
                                Bukkit.getMobGoals().addGoal(gin, 1, goal);
                                ginOwner.remove(p.getName());
                            }, 40);
                            
                    }, 40);

                    
                    for (final LobbyPlayer lp_ : Main.getLobbyPlayers()) {
                        if (!lp_.name.equals(lp.name) && lp_.questAccept.contains(Quest.GreetNewBie)) {
                            lp_.getPlayer().sendMessage("§6Ожидается прибытие новичка через 15 секунд!");
                        }
                    }
                    
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

    private Blaze spawnGin() {
        final Location loc = Main.getLocation(Main.LocType.ginLampShip);
        final Entity entity = loc.getWorld().spawnEntity(loc, EntityType.BLAZE);
        final Blaze gin = (Blaze) entity;
        gin.setGlowing(true);
        gin.setGravity(false);
        gin.setInvulnerable(true);
        gin.setCustomName(JinGoal.ginName);
        gin.setCustomNameVisible(true);
        Bukkit.getMobGoals().removeAllGoals(gin);
        return gin;
    }
    



    
    
    

    
    
    
    
    
    
    
    
    









    
    
    


}
    
    
 
