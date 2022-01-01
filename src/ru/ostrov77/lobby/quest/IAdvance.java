package ru.ostrov77.lobby.quest;


import org.bukkit.entity.Player;
import ru.ostrov77.lobby.LobbyPlayer;


public interface IAdvance {

    public void join (final Player p, final LobbyPlayer lp);
    
    public void sendToast(final Player p, final LobbyPlayer lp, final Quest quest);
    
    //из эвента PlayerQuitEvent
    public void onQuit(final Player p);
    
    //для oscom reset
    public void resetProgress(final Player p);
    
    //использует QuestManager
    public void sendComplete(final Player p,  final String advName, final boolean silent);
    
   //использует QuestManager
    public void sendProgress(final Player p, final Quest quest, final int progress);
    
    //public int getProgress(final Player p, final Quest quest);
    
    public void updVisib(final Player p);
    
    
}
  


    
    

    
    

