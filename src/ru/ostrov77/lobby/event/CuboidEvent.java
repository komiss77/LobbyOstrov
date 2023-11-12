package ru.ostrov77.lobby.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.area.LCuboid;





public class CuboidEvent extends Event {

    private static HandlerList handlers = new HandlerList();
    private final Player p;
    private final LobbyPlayer lp;
    private final LCuboid previos;
    private final LCuboid current;
    private final int previosEntryTime;
    
    public CuboidEvent(final Player p, final LobbyPlayer lp, final LCuboid previos, final LCuboid current, final int previosEntryTime) {
        this.p = p;
        this.lp = lp;
        this.previos = previos;
        this.current = current;
        this.previosEntryTime = previosEntryTime;
    }

    public Player getPlayer() {
        return p;
    }
    
    public LobbyPlayer getLobbyPlayer() {
        return lp;
    }
    
    public LCuboid getLast() {
        return previos;
    }
    
    public LCuboid getCurrent() {
        return current;
    }
    
    public int getpreviosEntryTime() {
        return previosEntryTime;
    }
    
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
