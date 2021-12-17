package ru.ostrov77.lobby;

import java.util.EnumSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.komiss77.LocalDB;
import ru.komiss77.Timer;
import ru.ostrov77.lobby.quest.PKrist;
import ru.ostrov77.lobby.quest.Quest;


public class LobbyPlayer {
    
    public final String name;
    private int flags; //флаги
    private int openedArea; //открытые локации
    public EnumSet<Quest> questDone; //завершенные задания
    public EnumSet<Quest> questAccept; //текущие задания
    
    //служебные
    public int lastCuboidId; //для playerMoveTask
    public int cuboidEntryTime = Timer.getTime(); //при входе равно текущему времени - может сразу появиться в кубоиде
    public int raceTime; //таймер гонки
    public int taxed; //кол-во собраных налогов
    public final EnumSet<Material> foundBlocks; //блоки для 50 блок. задания
    public PKrist pkrist;
    public int compasstarget; //ИД кубоида цели для компаса
    
    
    
    
    LobbyPlayer(final String name) {
        this.name = name;
        raceTime = -1;
        questDone = EnumSet.noneOf(Quest.class);
        questAccept = EnumSet.noneOf(Quest.class);
        foundBlocks = EnumSet.noneOf(Material.class);
    }
    
    
    
    
    
    public boolean isAreaDiscovered(final int areaId) {
        return (openedArea & (1 << areaId)) == (1 << areaId);
    }
    
    public void setAreaDiscovered(final int areaId) {
        openedArea =(openedArea | (1 << areaId));
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `lobbyData` SET `openedArea` = '"+openedArea+"' WHERE `name` = '"+name+"';");
    }

    
    
    
    public boolean hasFlag(final LobbyFlag flag) {
        return (flags & (1 << flag.tag)) == (1 << flag.tag);//return LobbyFlag.hasFlag(flags, flag);
    }
    
    public void setFlag(final LobbyFlag flag, final boolean state) {
        flags = state ? (flags | (1 << flag.tag)) : flags & ~(1 << flag.tag);
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `lobbyData` SET `flags` = '"+flags+"' WHERE `name` = '"+name+"';");
    }

    
    
    
    
    //только сохранение! все обработчики - в QuestManager
    public void questDone(final Player p, final Quest quest, final boolean condratulations) {
        //if (!Bukkit.isPrimaryThread()) {
        //    Ostrov.log_err("Асинхронный вызов questDone : "+p.getName()+" , "+quest);
        //}
        boolean change = questAccept.remove(quest); //сохранять только если что-то реально изменилось!
        if (questDone.add(quest)) {
            change = true;
            /*if (condratulations) {
                DonatEffect.spawnRandomFirework(p.getLocation());
                if (Main.advancements) {
                    Advance.completeAdv(p, quest.code);
                } else {
                    final ChatColor chatColor = colors[Ostrov.random.nextInt(colors.length)];
                    p.sendMessage(" ");
                    p.sendMessage(new StringBuilder().append(chatColor).append(ChatColor.STRIKETHROUGH).append("-----").append(ChatColor.DARK_RED).append(ChatColor.MAGIC).append(" AA").append(ChatColor.YELLOW).append(" Выполнены условия достижения ").append(ChatColor.DARK_RED).append(ChatColor.MAGIC).append("AA ").append(chatColor).append(ChatColor.STRIKETHROUGH).append("-----").toString());
                    p.sendMessage(chatColor + quest.displayName );
                    p.sendMessage(chatColor + " Квест завершен! " );
                    p.sendMessage(" ");               
                }

            } else {
p.sendMessage("§8log:  квест завершен без поздравлений "+quest.displayName);
            }*/
        } else {
p.sendMessage("§8log: квест "+quest+" уже завершен, игнор.");
        }
        if (change) {
            saveQuest();
        }
    }
    
    
    public void saveQuest() {
        final StringBuilder sbDone = new StringBuilder();
        for (Quest q:questDone) {
            sbDone.append(q.code);
        }
        final StringBuilder sbAccept = new StringBuilder();
        for (Quest q:questAccept) {
            sbAccept.append(q.code);
        }
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `lobbyData` SET `questDone` = '"+sbDone.toString()+"', `questAccept` = '"+sbAccept.toString()+"' WHERE `name` = '"+name+"';");
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void setFlags(final int flags) {
        this.flags = flags;
    }

    public void setOpenedArea(int openedArea) {
        this.openedArea = openedArea;
    }

    public Player getPlayer() {
        return Bukkit.getPlayerExact(name);
    }

    
    
    

    
}
