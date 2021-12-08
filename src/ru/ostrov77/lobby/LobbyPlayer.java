package ru.ostrov77.lobby;

import java.util.EnumSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.komiss77.LocalDB;
import ru.komiss77.Timer;
import ru.ostrov77.lobby.quest.Quest;


public class LobbyPlayer {
    
    public final String name;
    private int flags;
    public int lastCuboidId;
    private int openedArea;
    public EnumSet<Quest> questDone;
    public EnumSet<Quest> questAccept;
    
    public int cuboidEntryTime = Timer.getTime(); //при входе равно текущему времени - может сразу появиться в кубоиде

    LobbyPlayer(final String name) {
        this.name = name;
        questDone = EnumSet.noneOf(Quest.class);
        questAccept = EnumSet.noneOf(Quest.class);
    }
    
    
    
    
    
    public boolean isAreaDiscovered(final int areaId) {
        return (openedArea & (1 << areaId)) == (1 << areaId);
    }
    
    public void setAreaDiscovered(final int areaId) {
        openedArea =(openedArea | (1 << areaId));
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `lobbyData` SET `openedArea` = '"+openedArea+"' WHERE `name` = '"+name+"';");
    }
    
    //public LCuboid getCurrentCuboid(final Player p) {
    //    return AreaManager.getCuboid(p.getLocation());
    //}
    
    
    
    
    public boolean hasFlag(final LobbyFlag flag) {
        return (flags & (1 << flag.tag)) == (1 << flag.tag);//return LobbyFlag.hasFlag(flags, flag);
    }
    
    
    public void setFlag(final LobbyFlag flag, final boolean state) {
        flags = state ? (flags | (1 << flag.tag)) : flags & ~(1 << flag.tag);
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `lobbyData` SET `flags` = '"+flags+"' WHERE `name` = '"+name+"';");
    }

    
    public void questDone(final Player p, final Quest quest) {
        boolean change = questAccept.remove(quest); //сохранять только если что-то реально изменилось!
        if (questDone.add(quest)) {
            change = true;
p.sendMessage("log: выполнен квест "+Quest.DiscoverAllArea.displayName);
        } else {
p.sendMessage("log: квест "+quest+" уже завершен, игнор.");
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

    
    
    

    
}
