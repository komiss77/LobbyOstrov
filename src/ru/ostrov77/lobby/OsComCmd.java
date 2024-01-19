package ru.ostrov77.lobby;

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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import ru.komiss77.ApiOstrov;
import ru.komiss77.LocalDB;
import ru.komiss77.Ostrov;
import ru.komiss77.Timer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.modules.quests.QuestManager;
import ru.komiss77.modules.quests.QuestViewMenu;
import ru.komiss77.utils.TCUtils;
import ru.komiss77.utils.inventory.SmartInventory;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.hd.HD;
import ru.ostrov77.lobby.quest.Quests;

import java.util.*;


public class OsComCmd implements CommandExecutor, TabCompleter {
    
    private final List <String> subCommands = Arrays.asList(  "debug", "gin", "quest", "area", "reset");
    private static final Set<String>ginOwner = new HashSet<>();
        
        
    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmnd, String command, String[] args) {
        
        final List <String> sugg = new ArrayList<>();
        if (args.length == 1) {//0- пустой (то,что уже введено)
            for (final String s : subCommands) {
                if (s.startsWith(args[0])) sugg.add(s);
            }
        }
        
       return sugg;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String string, String[] arg) {
        
        if ( ! (cs instanceof final Player p) ) {
            cs.sendMessage("§cНе консольная команда!");
            return true;
        }
        final LobbyPlayer lp = PM.getOplayer(p, LobbyPlayer.class);
        
        //if (lp==null) { оплеер в острове есть всегда!
        //    cs.sendMessage("§cВы ГОСТЬ, либо нет данных с прокси!");
        //    return true;
        //}
        //final LCuboid lc = AreaManager.getCuboid(p.getLocation());
        
        if (Timer.has(p, "tp")) return true; //от срабатывания предмета в руке при клике по иконке;
        
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
                    if (ApiOstrov.isLocalBuilder(cs, true)) {
                        p.getInventory().clear();
                        p.closeInventory();
                        lp.mysqlData.clear();
                        QuestManager.resetProgress(p, PM.getOplayer(p));
                        ApiOstrov.sendToServer(p, "arcaim", "");
                        Ostrov.async(()-> {
                            LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `lobbyData` WHERE `name` = '"+p.getName()+"';");
                            LocalDB.executePstAsync(Bukkit.getConsoleSender(), "DELETE FROM `playerData` WHERE `name` = '"+p.getName()+"';");
                        }, 20);
                    }
                    return true;

                    
                case "quest":
                    SmartInventory.builder()
                        .type(InventoryType.CHEST)
                        .id("quest"+p.getName())
                        .provider(new QuestViewMenu())
                        .title("Задания")
                        .size (5,9)
                        .build()
                        .open(p);
                    return true;

                case "area":
                    HD.openAreaMenu(p, lp);
                    return true;

                /*case "menu":
                    SmartInventory.builder()
                        .type(InventoryType.CHEST)
                        .id("menu"+p.getName())
                        .provider(new OsComMenu())
                        .title("        Задания")
                        .size (6,9)
                        .build()
                        .open(p);
                    return true;*/

                case "gin":
                    if (!ApiOstrov.isLocalBuilder(p) && lp.hasFlag(LobbyFlag.GinTravelDone)) {
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
                    final LCuboid lc = AreaManager.getCuboid(p.getLocation());
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
                    JinGoal.showGinHopper(Main.getLocation(Main.LocType.ginLamp).clone(), false);
                    QuestManager.complete(p, PM.getOplayer(p), Quests.lamp);
                    //if (lp.questAccept.contains(Quest.SpawnGin)) {
                    //    lp.questDone(p, Quest.SpawnGin, false);
                    //   QuestManager.completeAdv(p, Quest.SpawnGin);
                    //}
                    p.playSound(Main.getLocation(Main.LocType.ginLamp), Sound.ENTITY_EVOKER_PREPARE_WOLOLO, 5, 1);


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
                            final JinGoal goal = new JinGoal(gin);
                            gin.addPassenger(p);
                            Bukkit.getMobGoals().addGoal(gin, 1, goal);
                            ginOwner.remove(p.getName());
                        }, 40);

                    }, 40);


                    for (final Player pl : Bukkit.getOnlinePlayers()) {
                        if (!pl.getName().equals(lp.nik) && QuestManager.isComplete(PM.getOplayer(pl), Quests.greet)) {
                            pl.sendMessage("§6Ожидается прибытие новичка через 15 секунд!");
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
        final Location loc = Main.getLocation(Main.LocType.ginLamp);
        final Entity entity = loc.getWorld().spawnEntity(loc, EntityType.BLAZE);
        final Blaze gin = (Blaze) entity;
        gin.setGlowing(true);
        gin.setGravity(false);
        gin.setInvulnerable(true);
        gin.customName(TCUtils.format(JinGoal.GIN_NAME));
        gin.setCustomNameVisible(true);
        Bukkit.getMobGoals().removeAllGoals(gin);
        return gin;
    }
    



    
    
    

    
    
    
    
    
    
    
    
    









    
    
    


}
    
    
 
