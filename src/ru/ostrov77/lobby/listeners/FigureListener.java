package ru.ostrov77.lobby.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.komiss77.events.FigureClickEvent;





public class FigureListener implements Listener {
    
    @EventHandler
    public void onFigureClick (final FigureClickEvent e) {
        final Player p = e.getPlayer();
//p.sendMessage("§8log: tag="+e.getFigure().getTag()+" left?"+e.isLeftClick());
        if (e.isLeftClick()) {
            e.setSpeach(p, "§cНе бейте, а гладьте))", 5);
        } else {
            e.setSpeach(p, new String[]{"§eПривет!","§aТы тут новенький?","§bТри лампу","ITEM:soul_lantern","§bИли прыгай за борт."}, 10);
        }





    }
    

}
