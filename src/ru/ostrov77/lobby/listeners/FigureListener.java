package ru.ostrov77.lobby.listeners;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.komiss77.events.FigureClickEvent;
import ru.komiss77.objects.FigureAnswer;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.area.LCuboid;





public class FigureListener implements Listener {
    
    
    @EventHandler
    public void onFigureClick (final FigureClickEvent e) {
        final Player p = e.getPlayer();
//p.sendMessage("§8log: tag="+e.getFigure().getTag()+" left?"+e.isLeftClick());
        final LobbyPlayer lp = Main.getLobbyPlayer(p);
        
        
        if (e.getFigure().getTag().equals("aaa")) {
            
            if (e.isLeftClick()) {
                e.setAnswer(
                    new FigureAnswer()
                    .add("§cНе бейте, а гладьте))", (c) -> {
                        final LCuboid lc = lp.getCuboid();
                        p.sendMessage(lc==null ? "ты не в кубоиде" : "ты в кубоиде:"+lc.displayName);
                    })
                );

            } else {

                final FigureAnswer answer = new FigureAnswer()
                    .add("§eПривет!")
                    .add("§aТы тут новенький?")
                    .add("§bНа корабле есть §lособенная лампа §r§b,потри её.")
                    .add(Material.SOUL_LANTERN)
                    .add("§6Ну или прыгай за борт.")
                    .time(15)
                    .vibration()
                    .beforeEyes()
                    .sound(Sound.ENTITY_VILLAGER_TRADE)
                    ;


                e.setAnswer(answer);

            }
            
        }





    }
    

}
