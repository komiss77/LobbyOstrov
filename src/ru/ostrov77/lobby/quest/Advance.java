package ru.ostrov77.lobby.quest;


import org.bukkit.Material;
import org.bukkit.entity.Player;
import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay.AdvancementFrame;
import eu.endercentral.crazy_advancements.advancement.AdvancementFlag;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import eu.endercentral.crazy_advancements.advancement.ToastNotification;
import eu.endercentral.crazy_advancements.advancement.criteria.Criteria;
import eu.endercentral.crazy_advancements.event.AdvancementScreenCloseEvent;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.komiss77.Ostrov;
import ru.ostrov77.lobby.LobbyFlag;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.LCuboid;

//     https://www.spigotmc.org/resources/crazy-advancements-api.51741/

public class Advance implements Listener {

    private static final AdvancementManager mgr = new AdvancementManager(new NameKey("ostrov", "pls"));
    private static final Map<String,Advancement> adm = new HashMap<>();
    private static final Criteria c0 = new Criteria(0);

    
    
    
    
    /*
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onInter(final PlayerInteractEvent e) {
        if (e.getAction()==Action.RIGHT_CLICK_AIR && e.getItem()!=null && e.getItem().getType()==Material.STICK) {
            final Player p = e.getPlayer();
            if (p.isSneaking()) {
                p.sendMessage("load");
                load(p);
            } else {
                p.sendMessage("addPlayer");
                mgr.addPlayer(p);
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
p.sendMessage("§8log: QuestAdvance AdvancementTabChangeEvent");        
    }*/
    
    public static void sendToast(final Player p, final LobbyPlayer lp, final Quest quest) {
//p.sendMessage("§8log: onQuestAdd ToastNotification - "+quest);  
        final ToastNotification toast = new ToastNotification(quest.icon, "§aНовый квест: "+quest.displayName, AdvancementDisplay.AdvancementFrame.TASK);
        toast.send(p);
    }

    
    
    
    //из эвента PlayerJoinEvent
    //public static void onJoin(final Player p) {
    //    mgr.addPlayer(p);
    //}
    
    //после загрузки LobbyPlayerData, SYNC!
    //public static void onDataLoad1(final Player p) {
    //    load(p);
    //}
    
    //из эвента PlayerQuitEvent
    public static void onQuit(final Player p) {
        mgr.removePlayer(p);
    }
    
    
    //можно ASYNC!
    public static void send (final Player p) {
    //mgr.removePlayer(p);
        mgr.addPlayer(p);
//String s = "";
//for (Player m : mgr.getPlayers()) {
//    s=s+m.getName()+" ";
//}
//Bukkit.broadcastMessage("§fЗагрузка Адв. для"+p.getName()+", теперь в менеджере игроки "+s);
        final LobbyPlayer lp = Main.getLobbyPlayer(p);
        mgr.grantAdvancement(p, getAdvByKey("newbie"));
        
        for (final Advancement ad : mgr.getAdvancements()) {
//Bukkit.broadcastMessage("§eload adv : "+ad.getName().getNamespace()+" hasChildren?"+!ad.getChildren().isEmpty());
            if (ad.getChildren().isEmpty()) {
                //квесты
                final Quest q = Quest.byCode(ad.getName().getKey().charAt(0));
                if (q == null) {
                	if (lp.hasFlag(LobbyFlag.Elytra)) mgr.grantAdvancement(p, getAdvByKey("elytra"));
                } else {
//Bukkit.broadcastMessage("Quest="+q+" done?"+lp.questDone.contains(q));
                    if (lp.questDone.contains(q)) {
                        mgr.grantAdvancement(p, ad);
                    } else if (lp.questAccept.contains(q)) {
                        //if(Bukkit.isPrimaryThread()) {
                            QuestManager.checkProgress(p, lp, q);
                        //} else {
                        //    Ostrov.sync(()->QuestManager.checkQuest(p, lp, q, false), 0);
                        //}
                    }
                }
            } else {
                //локации
                final LCuboid lc = AreaManager.getCuboid(ad.getName().getKey());
//Bukkit.broadcastMessage("LCuboid="+(lc== null?"null":lc.name)+" isAreaDiscovered?"+(lc != null && lp.isAreaDiscovered(lc.id)));
                if (lc != null && lp.isAreaDiscovered(lc.id)) {
                    mgr.grantAdvancement(p, ad);
                }
            }
        }
        //Ostrov.sync(() -> mgr.updateVisibility(p), 30);
//                    final Advancement ad = QuestAdvance.adm.toArray(aa)[0];
//                    mgr.grantAdvancement(p, ad);
//                    ad.displayToast(p);
    }
    
    
    
    /*  //можно ASYNC!
    public static void send (final Player p) {
    //mgr.removePlayer(p);
        mgr.addPlayer(p);
//String s = "";
//for (Player m : mgr.getPlayers()) {
//    s=s+m.getName()+" ";
//}
//Bukkit.broadcastMessage("§fЗагрузка Адв. для"+p.getName()+", теперь в менеджере игроки "+s);
        final LobbyPlayer lp = Main.getLobbyPlayer(p);
        mgr.grantAdvancement(p, getAdvByKey("newbie"));
        
        for (final Advancement ad : mgr.getAdvancements()) {
//Bukkit.broadcastMessage("§eload adv : "+ad.getName().getNamespace()+" hasChildren?"+!ad.getChildren().isEmpty());
            if (ad.getChildren().isEmpty()) {
                //квесты
                final Quest q = Quest.byCode(ad.getName().getKey().charAt(0));
                if (q != null) {
//Bukkit.broadcastMessage("Quest="+q+" done?"+lp.questDone.contains(q));
                    if (lp.questDone.contains(q)) {
                        mgr.grantAdvancement(p, ad);
                    } else if (lp.questAccept.contains(q)) {
                        //if(Bukkit.isPrimaryThread()) {
                            QuestManager.checkProgress(p, lp, q);
                        //} else {
                        //    Ostrov.sync(()->QuestManager.checkQuest(p, lp, q, false), 0);
                        //}
                    }
                }
            } else {
                //локации
                final LCuboid lc = AreaManager.getCuboid(ad.getName().getKey());
//Bukkit.broadcastMessage("LCuboid="+(lc== null?"null":lc.name)+" isAreaDiscovered?"+(lc != null && lp.isAreaDiscovered(lc.id)));
                if (lc != null && lp.isAreaDiscovered(lc.id)) {
                    mgr.grantAdvancement(p, ad);
                }
            }
        }
        //Ostrov.sync(() -> mgr.updateVisibility(p), 30);
//                    final Advancement ad = QuestAdvance.adm.toArray(aa)[0];
//                    mgr.grantAdvancement(p, ad);
//                    ad.displayToast(p);
    }
    */
    
    
    
    
    
   
    
    
    public static void loadQuestAdv() {
        final Advancement parent = Advance.crtAdv("spawn", "§3§lАрхипелаг          ", "Доберись до центра лобби", c0, Material.HEART_OF_THE_SEA, 0, 0, "textures/block/azalea_leaves.png", null, AdvancementFrame.GOAL, AdvancementVisibility.ALWAYS);
        adm.put("spawn", parent);
        
        adm.put("newbie", Advance.crtAdv("newbie",   "§6§lМесто Прибытия          ", "Наконец-то здесь...",      c0, Material.OAK_BOAT,          -4f, 0, "", parent, AdvancementFrame.TASK, AdvancementVisibility.ALWAYS));
        adm.put("nopvp", Advance.crtAdv("nopvp",    "§e§lОазис          ",  "Изучи остров ПВЕ Мини-Игр",        c0, Material.HONEYCOMB,         2, -10.5f, "", parent, AdvancementFrame.CHALLENGE, visOnDisc("spawn")));
        adm.put("parkur", Advance.crtAdv("parkur",   "§b§lБерезовый Парк          ", "Посети остров Паркуров",   c0, Material.FEATHER,           3f, -7.5f, "", parent, AdvancementFrame.CHALLENGE, visOnDisc("spawn")));
        adm.put("skyworld", Advance.crtAdv("skyworld", "§3§lОстровки          ", "Найди остров Скайблока",         c0, Material.FLOWERING_AZALEA,  4f, -4.5f, "", parent, AdvancementFrame.CHALLENGE, visOnDisc("spawn")));
        adm.put("arcaim", Advance.crtAdv("arcaim",   "§9§lРисталище          ", "Открой остров Акраима",         c0, Material.BEDROCK,           5f, -1.5f, "", parent, AdvancementFrame.CHALLENGE, visOnDisc("spawn")));
        adm.put("daaria", Advance.crtAdv("daaria",   "§a§lПерелесок          ", "Посети остров Даарии",          c0, Material.OAK_LOG,           5f, 1.5f, "", parent, AdvancementFrame.CHALLENGE, visOnDisc("spawn")));
        adm.put("sedna", Advance.crtAdv("sedna",    "§4§lКровавая Пустошь          ", "Найди остров Седны",     c0, Material.CRIMSON_NYLIUM,    4f, 4.5f, "", parent, AdvancementFrame.CHALLENGE, visOnDisc("spawn")));
        adm.put("midgard", Advance.crtAdv("midgard",  "§c§lХуторок          ", "Открой остров Мидгарда",          c0, Material.CAMPFIRE,          3f, 7.5f, "", parent, AdvancementFrame.CHALLENGE, visOnDisc("spawn")));
        adm.put("pvp",  Advance.crtAdv("pvp",      "§6§lДолина Войны          ", "Разведай остров ПВП Мини-Игр",c0,Material.NETHERITE_AXE,     2f, 10.5f, "", parent, AdvancementFrame.CHALLENGE, visOnDisc("spawn")));

        adm.put("elytra",  Advance.crtAdv("elytra",      "§5§lДоктор Географ. Наук          ", "Выполни все задания в Лобби",c0,Material.WRITTEN_BOOK,     -3f, 4f, "", parent, AdvancementFrame.GOAL, AdvancementVisibility.HIDDEN));
        for (final Quest q : Quest.values()) {
            if (q==Quest.ReachSpawn) continue;
            //if (q.ammount>=0) { //ReachSpawn пропустить
                adm.put(String.valueOf(q.code), crtAdv(String.valueOf(q.code), q.displayName, q.description, new Criteria(q.ammount), q.icon, q.dx2, q.dy2, "", getParentAdv(q.attachedArea), AdvancementFrame.TASK, visOnDisc(q.attachedArea)));
            //}
        }
        
        mgr.addAdvancement(adm.values().toArray(new Advancement[adm.size()]));
        mgr.makeAccessible();
    }

    
    private static Advancement crtAdv(final String key, final String name, final String desc, final Criteria crt, final Material icon, final float x2, final float y2, final String back, final Advancement parent, final AdvancementFrame frame, final AdvancementVisibility vis, final AdvancementFlag... flags) {
    	final AdvancementDisplay dis = new AdvancementDisplay(icon, name, desc, frame, back, vis);
        final Advancement ad;
    	if (parent == null) {
            dis.setCoordinates(0.5f * x2, 0.5f * y2);
            ad = new Advancement(new NameKey("ostrov", key), dis, flags);
    	} else {
            dis.setCoordinates(0.5f * x2 + parent.getDisplay().getX(), 0.5f * y2 + parent.getDisplay().getY());
            ad = new Advancement(parent, new NameKey("ostrov", key), dis, flags);
        }
    	
    	if (crt.getRequiredNumber() != 0) {
            ad.setCriteria(crt);
    	}
    	return ad;
    }    
    

    
    private static Advancement getParentAdv(final String area) {
        //for (final Advancement a : adm) {
            if (area.isEmpty()) {
                return adm.get("spawn");//getParentAdv("spawn");
            } else {//if (a.getName().getKey().equals(area)) {
                return adm.get(area);//a;
            }
        //}
        //return null;
    }

    private static Advancement getAdvByKey(final char key) {
        //final String s = String.valueOf(key);
        //переделать
        return  adm.get(String.valueOf(key));//getAdvByKey(s);
    }    
    
    private static Advancement getAdvByKey(final String key) {
        //for (final Advancement a : adm) {
        //    if (a.getName().getKey().equals(key)) {
        //        return a;
        //    }
        //}
        return adm.get(key);//null;
    }

    private static AdvancementVisibility visOnDisc(final String area) {
        return new AdvancementVisibility() {
            @Override
            public boolean isVisible(final Player p, final Advancement adv) {
                final LCuboid lc = AreaManager.getCuboid(area);
                final LobbyPlayer lp = Main.getLobbyPlayer(p);
                return lc == null || lp == null ? adv.getParent().isGranted(p) : lp.isAreaDiscovered(lc.id);
            }
        };
    }

    
    
    
    
    
    
    //использует QuestManager
    public static void completeAdv(final Player p, final char key) {
        final Advancement ad = getAdvByKey(key);
        if (ad == null) {
            p.sendMessage("§8Ачивка " + key + " null");
        } else {
            mgr.grantAdvancement(p, ad);
            ad.displayToast(p);
        }
    }    
    
    public static void completeAdv(final Player p, final String key) {
        final Advancement ad = getAdvByKey(key);
        if (ad == null) {
            p.sendMessage("§8Ачивка " + key + " null");
        } else {
            mgr.grantAdvancement(p, ad);
            ad.displayToast(p);
            if (key.length() > 1) {
                Ostrov.sync(() -> mgr.updateVisibility(p), 10);
            }
        }
    }
    
   //использует QuestManager
    public static void progressAdv(final Player p, final char key, final int prg) {
        final Advancement ad = getAdvByKey(key);
        if (ad == null) {
            p.sendMessage("§8Ачивка " + key + " null");
        } else {
            mgr.setCriteriaProgress(p, ad, prg);
            //if (ad.getCriteria().getRequiredNumber() == prg) {
            //    ad.displayToast(p);
            //}
        }
    }

}
