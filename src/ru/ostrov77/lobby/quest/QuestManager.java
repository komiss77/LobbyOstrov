package ru.ostrov77.lobby.quest;

import java.util.EnumSet;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.enums.StatFlag;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.ostrov77.lobby.LobbyFlag;
import ru.ostrov77.lobby.LobbyPlayer;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.LCuboid;
import ru.ostrov77.lobby.event.CuboidEvent;


public class QuestManager implements Listener {

    public static void sound(final Player p) {
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 2, 0.5f);
        Ostrov.async(()-> {
            if (p.isOnline()) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1,  0.5f);
            }
        }, 5);
    }


    
    //нописание в actionbar куда человек зашел + квест на гонку для ПВЕ мини-игр
    @EventHandler
    public static void onCuboidEvent(final CuboidEvent e) {
    	
        if (e.previos != null) {
            switch (e.previos.name) {
			case "daaria":
			case "skyworld":
	            e.p.getInventory().setItem(2, e.lp.hasFlag(LobbyFlag.Elytra) ? Main.fw : Main.air);
				break;
			case "pandora": //вышел из локации пандора - значит мог её использовать
	            checkQuest(e.p, e.lp, Quest.LeavePandora, true);
				break;
			default:
				break;
			}
        }
        
    	if (e.current == null) {
            ApiOstrov.sendActionBarDirect(e.p, "§7§l⟣ §3§lАрхипелаг §7§l⟢");
    	} else {
            
            ApiOstrov.sendActionBarDirect(e.p, "§7§l⟣ " + e.current.displayName + " §7§l⟢");
            if (!e.lp.isAreaDiscovered(e.current.id)) {
                onNewAreaDiscover(e.p, e.lp, e.current);
            }
            
            switch (e.current.name) {
			case "start":
                if (e.lp.questAccept.contains(Quest.MiniRace)) {
                    e.p.sendMessage("§5[§eСостязание§5] §7>> На старт! Внимание! Вперед!");
                    //AreaManager.racePlayers.putIfAbsent(e.lp.name, 0);
                    e.lp.raceTime = 0;
                } else if (e.lp.questDone.contains(Quest.MiniRace)) {
                    e.p.sendMessage("§5[§eСостязание§5] §7>> Вы уже участвовали в состязании!");
                } else {
                    e.p.sendMessage("§5[§eСостязание§5] §7>> Найдите §eОазис§7 перед началом!");
                }
				break;
			case "end":
                QuestManager.checkQuest(e.p, e.lp, Quest.MiniRace, true);
				break;
			case "daaria":
			case "skyworld":
				Main.pickaxe.give(e.p);
				break;
			default:
				break;
			}
        }
    	
        
       /* if (e.current!=null && e.previos!=null) {
        	ApiOstrov.sendActionBar(e.p, "§8log: перешел из кубоида "+e.previos.displayName+" в "+e.current.displayName+", вход:"+e.previosEntryTime+", пробыл:"+(Timer.getTime()-e.previosEntryTime));
            if (!e.lp.isAreaDiscovered(e.current.id)) {
                QuestManager.onNewAreaDiscover(e.p, e.lp, e.current);
            }
        } else if (e.previos!=null) {
        	ApiOstrov.sendActionBar(e.p, "§8log: вышел из кубоида "+e.previos.displayName+", вход:"+e.previosEntryTime+", пробыл:"+(Timer.getTime()-e.previosEntryTime));
        } else if (e.current!=null) {
        	ApiOstrov.sendActionBar(e.p, "§8log: вошел в кубоид "+e.current.displayName);
            if (!e.lp.isAreaDiscovered(e.current.id)) {
                QuestManager.onNewAreaDiscover(e.p, e.lp, e.current);
            }
        }*/
        
    }

    
    
    //SYNC !!!
    public static void onNewAreaDiscover(final Player p, final LobbyPlayer lp, final LCuboid cuboid) {
       if (!lp.hasFlag(LobbyFlag.NewBieDone)) {  //новичёк - пока не откроет спавн, другие не давать
           
    	   switch (cuboid.name) {
			case "spawn"://новичёк дошел до спавна
                checkQuest(p, lp, Quest.ReachSpawn, true);
				break;
			case "newbie"://для кубоида новичков даём первые задания ниже
				//
				break;
			default://на остальные кубоиды новичёк не реагирует
				return;
			}
            
         }

   		completeAdv(p, cuboid.name);
        lp.setAreaDiscovered(cuboid.id);
        
        final EnumSet<Quest> areaQuest = Quest.getAreaQuest(cuboid.name);
        boolean save = false;
        if (!areaQuest.isEmpty()) { //с открытой зоной добавились новые задания
            for (Quest q : areaQuest) {
                if (addQuest(p, lp, q)) {
                	p.sendMessage("§8log: +новое задание с открытием зоны "+cuboid.name+" : "+q.displayName);
                    save = true;
                }
            }
        }
        if (save) {
            lp.saveQuest();
        }

        checkQuest(p, lp, Quest.DiscoverAllArea, true);
        ApiOstrov.sendBossbar(p, "Открыта новая локация: "+cuboid.displayName, 7, BarColor.GREEN, BarStyle.SOLID, false);
        sound(p);
        
    }

    //отдельным методом, т.к. могут добавлять и НПС
    public static boolean addQuest(final Player p, final LobbyPlayer lp, final Quest quest) {
        if (!lp.questDone.contains(quest) && lp.questAccept.add(quest)) { //это задание ранее не выполнено и уже не было получено ранее
            if (Main.advancements) {
ApiOstrov.sendTitleDelay(p, "", "§7Квест: "+quest.displayName, 20, 40, 20);
            } else {
                ApiOstrov.sendTitleDelay(p, "", "§7Квест: "+quest.displayName, 20, 40, 20);
            }
            return true;
        }
        return false;
    }
    
    
    
    
    //может вызываться из ASYNC !!!
    //по дефолту, задание будет выполнено, если оно было взято и не завершено.
    //для некоторых можно ставить сври чекающие обработчики
    public static boolean checkQuest(final Player p, final LobbyPlayer lp, final Quest quest, final boolean notPlJoin) {
        
        if (lp.questDone.contains(quest)) {
p.sendMessage("§8log: checkQuest "+quest+" - уже выполнен; return ");
            return false;
        }
        if (!lp.questAccept.contains(quest)) {
p.sendMessage("§8log: checkQuest "+quest+" - не был получен; return ");
            return false;
        }
p.sendMessage("§8log: checkQuest "+quest);
        final Oplayer op = PM.getOplayer(p);
        
        
        
        switch (quest) {
            
            case DiscoverAllArea:
            	final int dsc = getDiscAreas(lp);
            	progressAdv(p, String.valueOf(quest.code), dsc);
                if (dsc>=quest.num) {
                    lp.questDone(p, quest, true);
                    Main.pipboy.give(p);
                    return true;
                } else {
                	p.sendMessage("§8log: checkQuest DiscoverAllArea всего локаций="+AreaManager.getCuboidIds().size()+", открыто="+dsc);
                }
	            break;
                
            case LeavePandora: //будет вызвано при выходе из кубоида пандоры
            	if (notPlJoin) {
                    if (op!=null && op.hasDaylyFlag(StatFlag.Pandora)) { //пандора была заюзана. наличие квеста проверяется выше
                    	completeAdv(p, String.valueOf(quest.code));
                        lp.questDone(p, quest, true);
                        Main.cosmeticMenu.give(p);
                        return true;
                    } else {
                    	p.sendMessage("§8log: checkQuest UsePandora  hasDaylyFlag?"+op.hasDaylyFlag(StatFlag.Pandora));
                    }
            	}
	            break;
                
                
            case ReachSpawn: //сработает при входе в зону спавн
                if (notPlJoin && !lp.hasFlag(LobbyFlag.NewBieDone)) {
                	completeAdv(p, String.valueOf(quest.code));
                    lp.setFlag(LobbyFlag.NewBieDone, true);
                	completeAdv(p, String.valueOf(Quest.SpeakWithNPC.code));
                    lp.questDone(p, Quest.SpeakWithNPC, false);
                    lp.questDone(p, Quest.ReachSpawn, false);
                    //lp.questDone(p, Quest.openQuestMenu, false);
                    if (PM.exist(p.getName())) {
                        PM.getOplayer(p).showScore();
                    }
                    return true;
                }
	            break;
			case CobbleGen: // вызов когда киркой ломаешь булыгу
			case MineDiam: // вызов когда киркой ломаешь алмазы
				final Material mat = quest == Quest.CobbleGen ? Material.COBBLESTONE : Material.DIAMOND;
				if (notPlJoin) {
					final PlayerInventory pi = p.getInventory();
					final ItemStack it = new ItemStack(mat);
					int num = 1;
					for (final ItemStack i : pi.getContents()) {
						if (i != null && i.getType() == mat) {
							num += i.getAmount();
						}
					}
	            	progressAdv(p, String.valueOf(quest.code), num);
	            	pi.setItemInOffHand(Main.air);
					pi.remove(mat);
	            	if (num == quest.num) {
	                    lp.questDone(p, quest, true);
	            	} else {
						it.setAmount(num);
						pi.setItemInOffHand(it);
					}
				} else {
	            	progressAdv(p, String.valueOf(quest.code), 0);
				}
				break;
			case CollectTax:
            	progressAdv(p, String.valueOf(quest.code), 0);
				break;
			case FindBlock:
            	progressAdv(p, String.valueOf(quest.code), 0);
				break;
			case GreetNewBie:
				break;
			case MiniRace:
				if (notPlJoin && lp.raceTime > 0) {
	                p.sendMessage("§5[§eСостязание§5] §7>> Хорошо сработано! Время: §e" + ApiOstrov.secondToTime(lp.raceTime));
	                lp.raceTime = -1;
                	completeAdv(p, String.valueOf(quest.code));
	                lp.questDone(p, quest, true);
	                return true;
				}
				break;
			case SpeakWithNPC:
				break;
			case SumoVoid:
			case OpenTreassureChest:
			case MiniPark:
				if (notPlJoin) {
		        	QuestManager.completeAdv(p, String.valueOf(quest.code));
		            lp.questDone(p, quest, true);
				}
				break;
        }
        
        return false;
    }
    
	public static void completeAdv(final Player p, final String key) {
		if (Main.advancements) {
			QuestAdvance.completeAdv(p, key);
		}
	}
    
	public static void progressAdv(final Player p, final String key, final int prg) {
		if (Main.advancements) {
			QuestAdvance.progressAdv(p, key, prg);
		}
	}
	
	public static int getDiscAreas(final LobbyPlayer lp) {
        int dC = 0;
        for (final int id : AreaManager.getCuboidIds()) {
            if (lp.isAreaDiscovered(id)) dC++;
        }
        return dC;
	}
}
