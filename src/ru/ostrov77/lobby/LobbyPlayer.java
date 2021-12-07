package ru.ostrov77.lobby;

import java.util.EnumSet;
import org.bukkit.Bukkit;
import ru.komiss77.LocalDB;
import ru.ostrov77.lobby.quest.Quest;


public class LobbyPlayer {
    
    public final String name;
    protected int flags;
    //protected String logoutLoc;
    public int lastCuboidId;
    public int openedArea;
    public EnumSet<Quest> questDone;
    public EnumSet<Quest> questAccept;

    LobbyPlayer(final String name) {
        this.name = name;
        questDone = EnumSet.noneOf(Quest.class);
        questAccept = EnumSet.noneOf(Quest.class);
    }
    
    
    
    
    //может вызываться из ASYNC !!!
    public boolean isAreaDiscovered(final int areaId) {
        return (openedArea & (1 << areaId)) == (1 << areaId);
    }
    //может вызываться из ASYNC !!!
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

    
    
    //public boolean checkQuest (final Quest quest) {
    //    return Main.questManager.checkQuest(this, quest);
    //}
    
    
    
    
  /*  protected static void save(final LobbyPlayer lp) {
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "INSERT INTO `lobbyData` (name,logoutLoc) VALUES "
                        + "('"+lp.name+"','"+lp.logoutLoc+"') "
                        + "ON DUPLICATE KEY UPDATE "
                        + "`logoutLoc`='"+lp.logoutLoc+"'; " ); //остальные сохраняются по мере обновления!
                        //+ "`openedArea`='"+lp.openedArea+"', "
                        //+ "`questDone`='"+lp.questDone+"', "
                        //+ "`questAccept`='"+lp.questAccept+"', "
                        //+ "`flags`='"+lp.flags+"' ;");
    }*/
    
    
    
    
    
    /*
    CREATE TABLE `lobbyData` (
  `name` varchar(16) NOT NULL,
  `openedArea` int(11) NOT NULL DEFAULT '0',
  `questDone` varchar(128) NOT NULL DEFAULT '',
  `questAccept` varchar(128) NOT NULL DEFAULT '',
  `flags` int(11) NOT NULL DEFAULT '0',
  `logoutLoc` varchar(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
ALTER TABLE `lobbyData`
  ADD PRIMARY KEY (`name`);
COMMIT;
    */
    
}
