package ru.ostrov77.lobby.quest;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import ru.komiss77.ApiOstrov;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;

//     https://www.spigotmc.org/resources/crazy-advancements-api.51741/



public class AdvanceVanila implements IAdvance, Listener {


    

    public AdvanceVanila () {
        
        Main.instance.getServer().getPluginManager().registerEvents(this, Main.instance);
    }
    

    
    
    
   /*
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onInter(final PlayerInteractEvent e) {
        if (e.getItem()!=null && e.getItem().getType()==Material.STICK) {
            final Player p = e.getPlayer();
            if (p.isSneaking()) {
                if (e.getAction()==Action.RIGHT_CLICK_AIR) {
                    p.sendMessage("updVisib");
                    updVisib(p);
                } else  if (e.getAction()==Action.RIGHT_CLICK_BLOCK) {
                    p.sendMessage("send");
                    send(p, Main.getLobbyPlayer(p));
                }
            } else {
                if (e.getAction()==Action.RIGHT_CLICK_AIR) {// p.sendMessage("addPlayer");
                //mgr.addPlayer(p);
                    for(Advancement advancement : mgr.getAdvancements()) {
                        AdvancementDisplay display = advancement.getDisplay();
                        boolean visible = display.isVisible(p, advancement);
                        p.sendMessage("  display="+display.getTitle()+" vis?"+display.isVisible(p, advancement));     
                    } 
                }
            }
        }
    }*/

/* 
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onAdvClose(final AdvancementScreenCloseEvent e) {
        final Player p = e.getPlayer();
        final LobbyPlayer lp = Main.getLobbyPlayer(p);
        if (!lp.questAccept.contains(Quest.OpenAdvancements)) return; //чтобы лишний раз не дрючить Ostrov.sync
        if(Bukkit.isPrimaryThread()) {
            QuestManager.tryCompleteQuest(p, lp, Quest.OpenAdvancements);
        } else {
            Ostrov.sync(()->QuestManager.tryCompleteQuest(p, lp, Quest.OpenAdvancements), 0);
        }
//p.sendMessage("§8log: QuestAdvance AdvancementScreenCloseEvent");        
    }   


    
   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onAdvChange(final AdvancementTabChangeEvent e) {
        final Player p = e.getPlayer();
//p.sendMessage("§8log: QuestAdvance AdvancementTabChangeEvent");        
    }*/


    
    
    //можно ASYNC!  отправляет выполненные ачивки
    @Override
    public void join (final Player p, final LobbyPlayer lp) {
 

    }
    
    
    @Override
    public void sendToast(final Player p, final LobbyPlayer lp, final Quest quest) {
//p.sendMessage("§8log: onQuestAdd ToastNotification - "+quest);  
        ApiOstrov.sendTitleDelay(p, "", "§7Квест: "+quest.displayName, 20, 40, 20);
    }

    
    //из эвента PlayerQuitEvent
    @Override
    public void onQuit(final Player p) {
    } 
    
    
    
    @Override
    public void resetProgress(final Player p) {

    }


    
    //использует QuestManager
    @Override
    public void sendComplete(final Player p, final String advName, final boolean silent) {

    }
    
   //использует QuestManager
    @Override
    public void sendProgress(final Player p, final Quest quest, final int progress) {

    }
    
   // @Override
   // public int getProgress(final Player p, final Quest quest) {
   //     return 0; //нужна какая-то система сохранения
        //return QuestManager.getProgress(p, lp, quest, false);
  //  }      
    
    @Override
    public void updVisib(final Player p) {
    }
    
    
}
  


    
    

    
    

