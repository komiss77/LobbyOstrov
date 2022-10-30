package ru.ostrov77.lobby.quest;

import com.meowj.langutils.lang.LanguageHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay.AdvancementFrame;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import eu.endercentral.crazy_advancements.advancement.criteria.Criteria;
import eu.endercentral.crazy_advancements.event.AdvancementScreenCloseEvent;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.komiss77.Ostrov;
import ru.komiss77.objects.CaseInsensitiveMap;
import ru.ostrov77.lobby.LobbyFlag;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.LCuboid;

//     https://www.spigotmc.org/resources/crazy-advancements-api.51741/



public class AdvanceCrazy implements IAdvance, Listener {


    private static final AdvancementManager mgr = new AdvancementManager(new NameKey("ostrov", "pls"));
    private static final Map<String,Advancement> adm = new CaseInsensitiveMap<>();
    private static final List<Advancement> admRangeList = new ArrayList<>(); //для отправки в порядке наслодования, или не отображаются некоторые
    private static final Criteria c0 = new Criteria(0);
    private static Advancement root;

    
    /*
    AdvancementFrame 
    TASK-прямоугольная иконка, зелёные тосты 
    GOAL-иконка со скруглениями, зелёные тосты
    CHALLENGE-иконка в форме угольника, фиолетовые тосты
    */    
    public AdvanceCrazy () {

        root = createCuboidAdv("spawn", "§3§lАрхипелаг          ", "Доберись до центра лобби", 	c0, Material.HEART_OF_THE_SEA, 0, 0, "textures/block/azalea_leaves.png", null, AdvancementFrame.CHALLENGE, AdvancementVisibility.ALWAYS);
        
        //даётся сразу всегда
        createCuboidAdv("newbie",   "§6§lМесто Прибытия          ", "Наконец-то здесь...",      c0, Material.OAK_BOAT,          -4f, 0, "", root, AdvancementFrame.TASK, AdvancementVisibility.ALWAYS);
        
        //появятся без помпезностей при открытии спавна (привязаны к кубоидам)
        createCuboidAdv("nopvp",    "§e§lОазис          ",  "Изучи остров ПВЕ Мини-Игр",        c0, Material.HONEYCOMB,         2, -10.5f, "", root, AdvancementFrame.GOAL, AdvancementVisibility.PARENT_GRANTED);// AdvancementVisibility.PARENT_GRANTED) );// visOnDisc("spawn")));
        createCuboidAdv("parkur",   "§b§lБерезовый Парк          ", "Посети остров Паркуров",   c0, Material.FEATHER,           3f, -7.5f, "", root, AdvancementFrame.GOAL, AdvancementVisibility.PARENT_GRANTED);// visOnDisc("spawn")));
        createCuboidAdv("skyworld", "§3§lОстровки          ", "Найди остров Скайблока",         c0, Material.FLOWERING_AZALEA,  4f, -5f, "", root, AdvancementFrame.GOAL, AdvancementVisibility.PARENT_GRANTED);// visOnDisc("spawn")));
        createCuboidAdv("arcaim",   "§9§lРисталище          ", "Открой остров Акраима",         c0, Material.BEDROCK,           5f, -2.5f, "", root, AdvancementFrame.GOAL, AdvancementVisibility.PARENT_GRANTED);// visOnDisc("spawn")));
        createCuboidAdv("daaria",   "§a§lПерелесок          ", "Посети остров Даарии",          c0, Material.OAK_LOG,           5f, 2.5f, "", root, AdvancementFrame.GOAL, AdvancementVisibility.PARENT_GRANTED);// visOnDisc("spawn")));
        createCuboidAdv("sedna",    "§4§lКровавая Пустошь          ", "Найди остров Седны",     c0, Material.CRIMSON_NYLIUM,    4f, 5f, "", root, AdvancementFrame.GOAL, AdvancementVisibility.PARENT_GRANTED);// visOnDisc("spawn")));
        createCuboidAdv("midgard",  "§c§lХуторок          ", "Открой остров Мидгарда",          c0, Material.CAMPFIRE,          3f, 7.5f, "", root, AdvancementFrame.GOAL, AdvancementVisibility.PARENT_GRANTED);// visOnDisc("spawn")));
        createCuboidAdv("pvp",      "§6§lДолина Войны     ", "Разведай остров ПВП Мини-Игр",	c0,Material.NETHERITE_AXE,     2f, 10.5f, "", root, AdvancementFrame.GOAL, AdvancementVisibility.PARENT_GRANTED);// visOnDisc("spawn")));

        
        final EnumSet<Quest> find = EnumSet.allOf(Quest.class);
        find.remove(Quest.ReachSpawn);
        int round = find.size()*find.size();
        
        while (!find.isEmpty() && round>0) {
        Advancement parent;
            for (final Quest q : Quest.values()) {
                if (q==Quest.ReachSpawn) continue;
//System.out.println("round="+round);
                round--;
                if (round==0) {
                    Ostrov.log_err("loadQuestAdv DeadLock"); //while прервётся при round==0
                    break;
                }
                if (q.parent.isEmpty()) { //нет зависимости - берём root
                    parent = root;//adm.get("spawn");
                } else { //квест требует выполнения другого квеста
                    parent = adm.get(q.parent); //берём квест от которого зависит
                }
//System.out.println( "------ loadQuest "+q+" attached="+q.parent+" parent="+(parent==null ? "null" : parent.getName().getKey()) ); 
                if (parent!=null && find.remove(q)) { //найден - удаляем из списка поиска
                    
                    final AdvancementDisplay dis = new AdvancementDisplay(q.icon, q.displayName, q.description, getFrame(q), getVis(q));
                    dis.setCoordinates(0.5f * q.dx2 + parent.getDisplay().getX(), 0.5f * q.dy2 + parent.getDisplay().getY());
                    final Advancement ad = new Advancement(parent, new NameKey("ostrov", q.name()), dis);
                    if (q.ammount>0) ad.setCriteria( new Criteria(q.ammount));
//System.out.println( "+ "+q.name()+" parent="+ parent.getName().getKey()+" x2="+q.dx2+" y2="+q.dy2+" X="+dis.getX()+" Y="+dis.getY() ); 
//System.out.println( "x2="+x2+" y2="+y2+" X="+dis.getX()+" Y="+dis.getY() ); 
                    adm.put (q.name(),ad);
                    admRangeList.add(ad);
                    
                } 
            }
        }
        
        mgr.addAdvancement(admRangeList.toArray(new Advancement[adm.size()]));
        mgr.makeAccessible();
        
        Main.instance.getServer().getPluginManager().registerEvents(this, Main.instance);
    }
    
    
    private static Advancement createCuboidAdv(
            final String key, 
            final String displayName, 
            final String desc, 
            final Criteria crt, 
            final Material icon, 
            final float x2, 
            final float y2, 
            final String backGroundTexture, 
            final Advancement parent, 
            final AdvancementFrame frame, 
            final AdvancementVisibility vis 
            //final AdvancementFlag... flags //не ставить, или при каждом входе сыплет тосты по grantAdvancement
    ) {
    	final AdvancementDisplay dis = new AdvancementDisplay(icon, displayName, desc, frame, backGroundTexture, vis);
        final Advancement ad;
    	if (parent == null) {
            dis.setCoordinates(0.5f * x2, 0.5f * y2);
//System.out.println( "+ createAdv "+key+" parent="+(parent==null ? "null" : parent.getName().getKey())+" x2="+x2+" y2="+y2+" X="+dis.getX()+" Y="+dis.getY() ); 
            ad = new Advancement(new NameKey("ostrov", key), dis);
    	} else {
            dis.setCoordinates(0.5f * x2 + parent.getDisplay().getX(), 0.5f * y2 + parent.getDisplay().getY());
//System.out.println( "+ createAdv "+key+" parent="+ parent.getName().getKey()+" x2="+x2+" y2="+y2+" X="+dis.getX()+" Y="+dis.getY() ); 
//System.out.println( "x2="+x2+" y2="+y2+" X="+dis.getX()+" Y="+dis.getY() ); 
            ad = new Advancement(parent, new NameKey("ostrov", key), dis);
        }
//System.out.println("");    	
    	if (crt.getRequiredNumber() != 0) {
            ad.setCriteria(crt);
    	}
        
        adm.put (key,ad);
        mgr.addAdvancement(ad);
        admRangeList.add(ad);
        
    	return ad;
    }    


    private AdvancementVisibility getVis(final Quest q) {
      switch (q) {
        case Elytra:
            return AdvancementVisibility.HIDDEN;
        default:
            return AdvancementVisibility.PARENT_GRANTED;
        }
      }


      private AdvancementFrame getFrame(final Quest q) {
      switch (q) {
        case Elytra:
        case TalkAllNpc:
        case FirstMission:
            return AdvancementFrame.CHALLENGE;
        default:
            return AdvancementFrame.TASK;
        }
      }  
    
    
    /*
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onInter(final PlayerInteractEvent e) {
        if (e.getItem()!=null) {
            e.getPlayer().sendMessage("--"+LanguageHelper.getMaterialName(e.getItem().getType(), "RU_ru"));
        }
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


    
   /* @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onAdvChange(final AdvancementTabChangeEvent e) {
        final Player p = e.getPlayer();
//p.sendMessage("§8log: QuestAdvance AdvancementTabChangeEvent");        
    }*/













    
    
    //можно ASYNC!  отправляет выполненные ачивки
    @Override
    public void join (final Player p, final LobbyPlayer lp) {
        
        mgr.addPlayer(p);

        //костыли
        mgr.grantAdvancement(p, adm.get("newbie")); //стартовая лодочка
        if (lp.hasFlag(LobbyFlag.NewBieDone)) mgr.grantAdvancement(p, root); //показать спавн и его детей
        //if (lp.hasFlag(LobbyFlag.Elytra)) mgr.grantAdvancement(p, adm.get("elytra"));
//Bukkit.broadcastMessage("send "+root.getName().getKey()+", newbie");     

        String advName;
        LCuboid lc;
        Quest q;
        for (final Advancement ad : admRangeList) { //adm.values()) {
//Bukkit.broadcastMessage("§eload adv : "+ad.getName().getNamespace()+" hasChildren?"+!ad.getChildren().isEmpty());
            advName = ad.getName().getKey();
//Bukkit.broadcastMessage("send advName="+advName);
            
            q = Quest.byName(advName); //чекаем выполнение квестов в ветвях 
            if (q!=null) {
//Bukkit.broadcastMessage("send q="+q+" done?"+lp.questDone.contains(q));
                if (lp.questDone.contains(q)) {
                    mgr.grantAdvancement(p, ad);
                } else if (lp.questAccept.contains(q) && q.ammount>0) {
                    //sendProgress(p, lp, q, QuestManager.updateProgress(p, lp, q, true)); //чтобы отобразило
                    QuestManager.updateProgress(p, lp, q, true); //чтобы отобразило
                }
                continue;
            }

            //локации
            lc  = AreaManager.getCuboid(advName);
            if (lc != null) { //есть кубоид с таким названием
//Bukkit.broadcastMessage("send LCuboid="+lc.getName()+" isAreaDiscovered?"+ lp.isAreaDiscovered(lc.id));
                if (lp.isAreaDiscovered(lc.id)) {
                    mgr.grantAdvancement(p, ad);
                }
                continue;
            }
//Bukkit.broadcastMessage("§eload adv : "+ad.getName().getNamespace()+" hasChildren?"+!ad.getChildren().isEmpty());
        }
        
        updVisib(p);

    }
    
    


    
    

    
    
    
    
    
    
    
    
    
    
    
    
    @Override
    public void sendToast(final Player p, final LobbyPlayer lp, final Quest quest) {
//p.sendMessage("§8log: onQuestAdd ToastNotification - "+quest);  
        final ToastNotification toast = new ToastNotification(quest.icon, "§aНовый квест: "+quest.displayName, AdvancementDisplay.AdvancementFrame.TASK);
        toast.send(p);
    }


    
    //из эвента PlayerQuitEvent
    @Override
    public void onQuit(final Player p) {
        mgr.removePlayer(p);
    } 
    
    
    
    @Override
    public void resetProgress(final Player p) {
        for (final Advancement ad : mgr.getAdvancements()) {
            mgr.revokeAdvancement(p, ad);
            //mgr.revokeCriteria(p, ad, c0);
        }
    }


    
    //использует QuestManager
    @Override
    public void sendComplete(final Player p, final String advName, final boolean silent) {
        
        final Advancement ad = adm.get(advName);
        if (ad != null) {
            mgr.grantAdvancement(p, ad);
            if (!silent) ad.displayToast(p);
        } else {
//p.sendMessage("§ccompleteAdv Ачивка " + name + " null");
        }
    }
    
    
    
   //использует QuestManager
    @Override
    public void sendProgress(final Player p, final Quest quest, final int progress) {
        final Advancement ad = adm.get(quest.name());
        if (ad == null) {
//p.sendMessage("§cprogressAdv Ачивка " + name + " null");
        } else {
            mgr.setCriteriaProgress(p, ad, progress);
            /*if (ad.getCriteria().getRequiredNumber() == progress) {
                ad.displayToast(p);
            }*/
            //mgr.updateVisibility(p);
            //Ostrov.async(() -> mgr.updateVisibility(p), 10);
        }
    }
    
  
    
    @Override
    public void updVisib(final Player p) {
        mgr.updateVisibility(p);
    }
    
    
}
  


    
    

    
    

