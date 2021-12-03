package ru.ostrov77.lobby;

import org.bukkit.Bukkit;
import ru.komiss77.LocalDB;


public class LobbyPlayer {
    
    protected final String name;
    protected int flags;
    protected String logoutLoc;

    LobbyPlayer(final String name) {
        this.name = name;
    }
    
    
    
    
    
    
    
    
    
    
    
       public boolean hasFlag(final LobbyFlag flag) {
        return LobbyFlag.hasFlag(flags, flag);
    }
    
    
    public void setFlag(final LobbyFlag flag, final boolean state) {
        flags = state ? (flags | (1 << flag.tag)) : flags & ~(1 << flag.tag);
        save(this);
    }

    protected static void save(final LobbyPlayer lp) {
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "INSERT INTO `lobbyData` (name,logoutLoc,flags) VALUES "
                        + "('"+lp.name+"','"+lp.logoutLoc+"','0') "
                        + "ON DUPLICATE KEY UPDATE "
                        + "`logoutLoc`='"+lp.logoutLoc+"', "
                        + "`flags`='"+lp.flags+"' ;");
    }
    
}
