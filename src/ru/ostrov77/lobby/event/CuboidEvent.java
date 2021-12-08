package ru.ostrov77.lobby.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.area.LCuboid;





public class CuboidEvent extends Event {

    private static HandlerList handlers = new HandlerList();
    public final Player p;
    public final LobbyPlayer lp;
    public final LCuboid previos;
    public final LCuboid current;
    public final int previosEntryTime;
    
    public CuboidEvent(final Player p, final LobbyPlayer lp, final LCuboid previos, final LCuboid current, final int previosEntryTime) {
        this.p = p;
        this.lp = lp;
        this.previos = previos;
        this.current = current;
        this.previosEntryTime = previosEntryTime;
    }

    
    
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
