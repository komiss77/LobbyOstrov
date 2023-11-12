package ru.ostrov77.lobby.quest;

import org.bukkit.Material;

import ru.komiss77.modules.quests.Quest;
import ru.komiss77.modules.quests.Quest.QuestFrame;
import ru.komiss77.modules.quests.Quest.QuestVis;
import ru.komiss77.modules.quests.QuestManager;


public class Quests {

	public static final Quest newbie = new Quest('a', Material.OAK_CHEST_BOAT, 0, null, null, "§e§lМесто Прибытия", 
		"Наконец-то здесь...", "textures/block/azalea_leaves.png", QuestVis.ALWAYS, QuestFrame.TASK, 0);
	  
	 public static final Quest locman = new Quest('h', Material.GLOBE_BANNER_PATTERN, 0, null, newbie, 
		"Разговорить Лоцмана", "Выведайте куда вы прибыли у Лоцмана", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
	
	  public static final Quest ostrov = new Quest('i', Material.HEART_OF_THE_SEA, 0, null, locman, 
		"§н§lАрхипелаг", "Доберись до центра лобби", "", QuestVis.PARENT, QuestFrame.CHALLENGE, 0);
		
	   public static final Quest arcaim = new Quest('j', Material.BEDROCK, 0, null, ostrov, 
		"§9§lРисталище", "Изучи остров ПВЕ Мини-Игр", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	    public static final Quest find = new Quest('l', Material.NETHERITE_BLOCK, 50, null, arcaim, 
		"Юный Майнкрафтолог", "ПКМ на 50 различных блоков", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
		
	   public static final Quest midgard = new Quest('k', Material.CAMPFIRE, 0, null, ostrov, 
		"§c§lХуторок", "Открой остров Мидгарда", "", QuestVis.PARENT, QuestFrame.TASK, 0);

	    private static final String[] tax = {"К", "М", "Ф"};
	    public static final Quest gold = new Quest('m', Material.RAW_GOLD, tax.length, tax, midgard, 
		"Строгий Казначей", "Собрать золотых с жителей поселка", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
		
	   public static final Quest daaria = new Quest('n', Material.OAK_LOG, 0, null, ostrov, 
		"§a§lПерелесок", "Посети остров Даарии", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	    public static final Quest dims = new Quest('o', Material.DIAMOND, 10, null, daaria, 
		"Зазнавшийся Шахтер", "Добыть 10 алмазов в шахте", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
		
	   public static final Quest skyworld = new Quest('p', Material.FLOWERING_AZALEA, 0, null, ostrov, 
		"§3§lОстровки", "Найди остров Скайблока", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	    public static final Quest cobble = new Quest('q', Material.COBBLESTONE, 12, null, skyworld, 
		"Прокачка Острова", "Выкопать 12 булыжника в генераторе", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
	  
	   public static final Quest nopvp = new Quest('r', Material.HONEYCOMB, 0, null, ostrov, 
		"§e§lОазис", "Изучи остров ПВЕ Мини-Игр", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	    public static final Quest race = new Quest('s', Material.TURTLE_HELMET, 0, null, nopvp, 
		"Олимпиада", "Пройти состязание за 5 минут", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
		
	   public static final Quest sedna = new Quest('t', Material.CRIMSON_NYLIUM, 0, null, ostrov, 
		"§4§lКровавая Пустошь", "Найди остров Седны", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	    public static final Quest warrior = new Quest('u', Material.REDSTONE, 8, null, sedna, 
		"Бардовый Воин", "Убить 8 мертвецов из спавнера", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
	
	   public static final Quest parkur = new Quest('v', Material.FEATHER, 0, null, ostrov, 
		"§b§lБерезовый Парк", "Посети остров Паркуров", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	    public static final Quest jump = new Quest('w', Material.SMALL_DRIPLEAF, 0, null, parkur, 
		"Прыжок за Прыжком", "Пропрыгать 12 блоков на мини-паркуре", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
	
	   public static final Quest pvp = new Quest('x', Material.NETHERITE_AXE, 0, null, ostrov, 
		"§6§lДолина Войны", "Разведай остров ПВП Мини-Игр", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	    public static final Quest sumo = new Quest('y', Material.SHULKER_SHELL, 0, null, pvp, 
		"Сумо Мастер", "Сбить человека с сумо платформы", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
		
	 public static final Quest qmenu = new Quest('b', Material.CHISELED_BOOKSHELF, 0, null, newbie, 
		"Грамотность", "Посмотреть меню Квестов", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	  public static final Quest pass = new Quest('c', Material.KNOWLEDGE_BOOK, 0, null, qmenu, 
		"Гражданин Острова", "Заполни несколько полей в Пасспорте", "", QuestVis.PARENT, QuestFrame.TASK, 0);
		
	  public static final Quest pandora = new Quest('d', Material.SPONGE, 0, null, qmenu, 
		"Удача Пандоры", "Испытайте удачу в Разломе Пандоры", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	  
	   public static final Quest treasure = new Quest('e', Material.ENDER_CHEST, 0, null, pandora, 
		"Сундук Сокровищ", "Получите примочки из Сундука Сокровищ", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
	
	  public static final Quest mission = new Quest('f', Material.GOLD_INGOT, 0, null, qmenu, 
		"Путь к Успеху", "Прими первую Миссию", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
	
	   private static final String[] npcs = {"LB", "AR", "MI", "DA", "SW", "OB", "NM", "SE", "PA", "PM"};
	   public static final Quest agent = new Quest('g', Material.END_CRYSTAL, npcs.length, npcs, mission, 
		"Комерческий Агент", "Поговори со всеми НПС", "", QuestVis.PARENT, QuestFrame.CHALLENGE, 0);
	
	 public static final Quest lamp = new Quest('z', Material.BLAZE_ROD, 0, null, newbie, 
		"Раб Лампы", "Освободи Джина из лампы", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	
	  public static final Quest navig = new Quest('A', Material.ARROW, 0, null, lamp, 
		"Навигатор", "Навести компас (клик на иконку)", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	
	   public static final Quest discover = new Quest('B', Material.COMPASS, 9, null, navig, 
		"Открыть все Локации", "Исследуйте все острова лобби", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
	
	  public static final Quest plate = new Quest('C', Material.IRON_BOOTS, 0, null, navig, 
		"Сверх-Транспорт", "Переместитесь с помощью плиты", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	  public static final Quest greet = new Quest('D', Material.QUARTZ, 0, null, lamp, 
		"Приветствие Новичка", "Нажми ПКМ на нового игрока", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
	   
	   public static final Quest doctor = new Quest('E', Material.WRITTEN_BOOK, 0, null, greet, 
		"Доктор Географ. Наук", "Выполни все задания в Лобби", "", QuestVis.HIDDEN, QuestFrame.CHALLENGE, 0);
	
	static {QuestManager.loadQuests();}
    

    
    
    //для квестов где ammount>0
    /*public static int updateProgress(final Player p, final LobbyPlayer lp, final Quest__ quest, final boolean update) {
        //if (!lp.hasQuest(quest)) return -1; -не надо, или когда вызывает DiscoverAllArea по окончании HeavyFoot не даёт колл-во
        int progress = 0;
        final Oplayer op = PM.getOplayer(p);
        
        switch (quest) {
            
            case DiscoverAllArea -> progress = lp.getOpenAreaCount(); //открытые добавляются выше в onNewAreaDiscover
                
            case FindBlock -> {
                progress = lp.getProgress(quest);
                if (update) Main.advance.sendProgress(p, quest, progress);
                return progress; //тут не надо lp.setProgress, обновляется при интеракт
                //break;
            }
            
            case CobbleGen, MineDiam, KillMobs -> {
                // вызов когда киркой ломаешь булыгу // вызов когда киркой ломаешь алмазы
                final Material mat;
                switch (quest) {
                    case MineDiam:
                        mat = Material.DIAMOND;
                        break;
                    case KillMobs:
                        mat = Material.ROTTEN_FLESH;
                        break;
                    case CobbleGen:
                    default:
                        mat = Material.COBBLESTONE;
                        break;
                }
                //Ostrov.log("updateProgress "+quest+" mat="+mat);
                final PlayerInventory pi = p.getInventory();
                final ItemStack it = new ItemStack(mat);
                progress = 1;
                for (final ItemStack i : pi.getContents()) {
                    if (i != null && i.getType() == mat) {
                        progress += i.getAmount();
                    }
                }
                pi.setItemInOffHand(Main.air);
                pi.remove(mat);
                if (progress < quest.ammount) {
                    it.setAmount(progress);
                    pi.setItemInOffHand(it);
                }
            }
                
            case CollectTax -> progress = (lp.hasFlag(LobbyFlag.MI1) ? 5 : 0) + (lp.hasFlag(LobbyFlag.MI2) ? 5 : 0) + (lp.hasFlag(LobbyFlag.MI3) ? 3 : 0); 
                
            case Passport -> progress = op==null ? 0 : op.getPasportFillPercent(); 
                
            case TalkAllNpc -> {
                for (final LobbyFlag f : LobbyFlag.values()) {
                    switch (f.tag) {
                        case 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 -> {
                            if (lp.hasFlag(f)) {
                                progress++;
                            }
                        }
                    }
                }
            } 
                
        }
        
	//p.sendMessage("§8log: getProgress "+quest+"="+progress);
        lp.setProgress(quest, progress); //сохранить в кэш
        if (update) Main.advance.sendProgress(p, quest, progress);//progressAdv(p, lp, quest, dsc);
        return progress;
        
    }*/
    
}
