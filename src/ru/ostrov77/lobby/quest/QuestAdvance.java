package ru.ostrov77.lobby.quest;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay.AdvancementFrame;
import eu.endercentral.crazy_advancements.advancement.AdvancementFlag;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import eu.endercentral.crazy_advancements.advancement.criteria.Criteria;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import java.util.Arrays;
import org.bukkit.Bukkit;
import ru.komiss77.Ostrov;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.LCuboid;


public class QuestAdvance {

	private static final HashSet<Advancement> adm = new HashSet<Advancement>();
	private static final AdvancementManager mgr = new AdvancementManager(new NameKey("ostrov", "pls"));
	private static final Criteria c0 = new Criteria(0);

	public static Advancement crtAdv(final String key, final String name, final String desc, final Criteria crt, final Material icon, final float x2, final float y2, final String back, final Advancement parent, final AdvancementFrame frame, final AdvancementVisibility vis, final AdvancementFlag... flags) {
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
	
    
        
        
    //из эвента PlayerJoinEvent
    public static void onJoin(final Player p) {
        mgr.addPlayer(p);
    }
    
    //после загрузки LobbyPlayerData, SYNC!
    public static void onDataLoad(final Player p) {
        load(p);
    }
    
    //из эвента PlayerQuitEvent
    public static void onQuit(final Player p) {
        mgr.removePlayer(p);
    }
    
    
    
    
    private static void load (final Player p) {
    	
		//mgr.removePlayer(p);
		//mgr.addPlayer(p);
                
//String s = "";
//for (Player m : mgr.getPlayers()) {
//    s=s+m.getName()+" ";
//}
//Bukkit.broadcastMessage("§8Загрузка Адв. для"+p.getName()+", теперь в менеджере игроки "+s);
    		
    		final LobbyPlayer lp = Main.getLobbyPlayer(p);
                mgr.grantAdvancement(p, getAdvByKey("newbie"));
    		for (final Advancement ad : mgr.getAdvancements()) {
    			if (ad.getChildren().isEmpty()) {
    				//квесты
    				final Quest q = Quest.byCode(ad.getName().getKey().charAt(0));
    				if (q != null) {
    					if (lp.questDone.contains(q)) {
        					mgr.grantAdvancement(p, ad);
    					} else {
							QuestManager.checkQuest(p, lp, q, false);
						}
    				}
    			} else {
					//локации
    				final LCuboid lc = AreaManager.getCuboid(ad.getName().getKey());
    				if (lc != null && lp.isAreaDiscovered(lc.id)) {
    					mgr.grantAdvancement(p, ad);
    				}
				}
    		}
    		
			mgr.updateVisibility(p);
//                    final Advancement ad = QuestAdvance.adm.toArray(aa)[0];
//                    mgr.grantAdvancement(p, ad);
//                    ad.displayToast(p);
    }
    
    
    
    
    public static void loadQuestAdv() {
        final Advancement parent = QuestAdvance.crtAdv("spawn", "§3§lАрхипелаг          ", "Доберись до центра лобби", c0, Material.HEART_OF_THE_SEA, 0, 0, "textures/block/azalea_leaves.png", null, AdvancementFrame.GOAL, AdvancementVisibility.ALWAYS);
        adm.add(parent);
        adm.add(QuestAdvance.crtAdv("newbie", "§6§lМесто Прибытия          ", "Наконец-то здесь...", c0, Material.OAK_BOAT, -4f, 0, "", parent, AdvancementFrame.TASK, AdvancementVisibility.ALWAYS));
        
        adm.add(QuestAdvance.crtAdv("nopvp", "§e§lОазис          ", "Изучи остров ПВЕ Мини-Игр", c0, Material.HONEYCOMB, 2, -10.5f, "", parent, AdvancementFrame.CHALLENGE, visOnDisc("spawn")));
        adm.add(QuestAdvance.crtAdv("parkur", "§b§lБерезовый Парк          ", "Посети остров Паркуров", c0, Material.FEATHER, 3f, -7.5f, "", parent, AdvancementFrame.CHALLENGE, visOnDisc("spawn")));
        adm.add(QuestAdvance.crtAdv("skyworld", "§3§lОстровки          ", "Найди остров Скайблока", c0, Material.FLOWERING_AZALEA, 4f, -4.5f, "", parent, AdvancementFrame.CHALLENGE, visOnDisc("spawn")));
        adm.add(QuestAdvance.crtAdv("arcaim", "§9§lРисталище          ", "Открой остров Акраима", c0, Material.BEDROCK, 5f, -1.5f, "", parent, AdvancementFrame.CHALLENGE, visOnDisc("spawn")));
        adm.add(QuestAdvance.crtAdv("daaria", "§a§lПерелесок          ", "Посети остров Даарии", c0, Material.OAK_LOG, 5f, 1.5f, "", parent, AdvancementFrame.CHALLENGE, visOnDisc("spawn")));
        adm.add(QuestAdvance.crtAdv("sedna", "§4§lКровавая Пустошь          ", "Найди остров Седны", c0, Material.CRIMSON_NYLIUM, 4f, 4.5f, "", parent, AdvancementFrame.CHALLENGE, visOnDisc("spawn")));
        adm.add(QuestAdvance.crtAdv("midgard", "§c§lХуторок          ", "Открой остров Мидгарда", c0, Material.CAMPFIRE, 3f, 7.5f, "", parent, AdvancementFrame.CHALLENGE, visOnDisc("spawn")));
        adm.add(QuestAdvance.crtAdv("pvp", "§6§lДолина Войны          ", "Разведай остров ПВП Мини-Игр", c0, Material.NETHERITE_AXE, 2, 10.5f, "", parent, AdvancementFrame.CHALLENGE, visOnDisc("spawn")));
        for (final Quest q : Quest.values()) {
        	adm.add(crtAdv(String.valueOf(q.code), q.displayName, q.description, new Criteria(q.num), q.icon, q.dx2, q.dy2, "", getParentAdv(q.attachedArea), AdvancementFrame.TASK, visOnDisc(q.attachedArea)));
        }
        mgr.addAdvancement(adm.toArray(new Advancement[0]));
        mgr.makeAccessible();
	}

	public static Advancement getParentAdv(final String area) {
		for (final Advancement a : adm) {
			if (area.isEmpty()) {
				return getParentAdv("spawn");
			} else if (a.getName().getKey().equals(area)) {
				return a;
			}
		}
		return null;
	}

	public static Advancement getAdvByKey(final String key) {
		for (final Advancement a : adm) {
			if (a.getName().getKey().equals(key)) {
				return a;
			}
		}
		return null;
	}

	public static AdvancementVisibility visOnDisc(final String area) {
		return new AdvancementVisibility() {
			@Override
			public boolean isVisible(final Player p, final Advancement adv) {
				final LCuboid lc = AreaManager.getCuboid(area);
				final LobbyPlayer lp = Main.getLobbyPlayer(p);
				return lc == null || lp == null ? adv.getParent().isGranted(p) : lp.isAreaDiscovered(lc.id);
			}
		};
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
    
	public static void progressAdv(final Player p, final String key, final int prg) {
		final Advancement ad = getAdvByKey(key);
		if (ad == null) {
			p.sendMessage("§8Ачивка " + key + " null");
		} else {
			mgr.setCriteriaProgress(p, ad, prg);
			if (ad.getCriteria().getRequiredNumber() == prg) {
				ad.displayToast(p);
			}
		}
	}

}
