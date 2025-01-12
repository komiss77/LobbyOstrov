package ru.ostrov77.lobby.quest;

import org.bukkit.inventory.ItemType;
import ru.komiss77.modules.quests.Quest;
import ru.komiss77.modules.quests.Quest.QuestFrame;
import ru.komiss77.modules.quests.Quest.QuestVis;
import ru.komiss77.modules.quests.QuestManager;


public class Quests {

	public static final Quest newbie = new Quest('a', ItemType.OAK_CHEST_BOAT, 0, null, null, "§e§lМесто Прибытия",
		"Наконец-то здесь...", "textures/block/azalea_leaves.png", QuestVis.ALWAYS, QuestFrame.TASK, 0);
	  
	 public static final Quest locman = new Quest('h', ItemType.GLOBE_BANNER_PATTERN, 0, null, newbie, 
		"Разговорить Лоцмана", "Выведайте куда вы прибыли у Лоцмана", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
	
	  public static final Quest ostrov = new Quest('i', ItemType.HEART_OF_THE_SEA, 0, null, locman, 
		"§н§lАрхипелаг", "Доберись до центра лобби", "", QuestVis.PARENT, QuestFrame.CHALLENGE, 0);
		
	   public static final Quest arcaim = new Quest('j', ItemType.BEDROCK, 0, null, ostrov, 
		"§9§lРисталище", "Изучи остров ПВЕ Мини-Игр", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	    public static final Quest find = new Quest('l', ItemType.NETHERITE_BLOCK, 50, null, arcaim, 
		"Юный Майнкрафтолог", "ПКМ на 50 различных блоков", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
		
	   public static final Quest midgard = new Quest('k', ItemType.CAMPFIRE, 0, null, ostrov, 
		"§c§lХуторок", "Открой остров Мидгарда", "", QuestVis.PARENT, QuestFrame.TASK, 0);

	    private static final String[] tax = {"К", "М", "Ф"};
	    public static final Quest gold = new Quest('m', ItemType.RAW_GOLD, tax.length, tax, midgard, 
		"Строгий Казначей", "Собрать золотых с жителей поселка", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
		
	   public static final Quest daaria = new Quest('n', ItemType.OAK_LOG, 0, null, ostrov, 
		"§a§lПерелесок", "Посети остров Даарии", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	    public static final Quest dims = new Quest('o', ItemType.DIAMOND, 10, null, daaria, 
		"Зазнавшийся Шахтер", "Добыть 10 алмазов в шахте", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
		
	   public static final Quest skyworld = new Quest('p', ItemType.FLOWERING_AZALEA, 0, null, ostrov, 
		"§3§lОстровки", "Найди остров Скайблока", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	    public static final Quest cobble = new Quest('q', ItemType.COBBLESTONE, 12, null, skyworld, 
		"Прокачка Острова", "Выкопать 12 булыжника в генераторе", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
	  
	   public static final Quest nopvp = new Quest('r', ItemType.HONEYCOMB, 0, null, ostrov, 
		"§e§lОазис", "Изучи остров ПВЕ Мини-Игр", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	    public static final Quest race = new Quest('s', ItemType.TURTLE_HELMET, 0, null, nopvp, 
		"Олимпиада", "Пройти состязание за 5 минут", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
		
	   public static final Quest sedna = new Quest('t', ItemType.CRIMSON_NYLIUM, 0, null, ostrov, 
		"§4§lКровавая Пустошь", "Найди остров Седны", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	    public static final Quest warrior = new Quest('u', ItemType.REDSTONE, 8, null, sedna, 
		"Бардовый Воин", "Убить 8 мертвецов из спавнера", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
	
	   public static final Quest parkur = new Quest('v', ItemType.FEATHER, 0, null, ostrov, 
		"§b§lБерезовый Парк", "Посети остров Паркуров", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	    public static final Quest jump = new Quest('w', ItemType.SMALL_DRIPLEAF, 0, null, parkur, 
		"Прыжок за Прыжком", "Пропрыгать 12 блоков на мини-паркуре", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
	
	   public static final Quest pvp = new Quest('x', ItemType.NETHERITE_AXE, 0, null, ostrov, 
		"§6§lДолина Войны", "Разведай остров ПВП Мини-Игр", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	    public static final Quest sumo = new Quest('y', ItemType.SHULKER_SHELL, 0, null, pvp, 
		"Сумо Мастер", "Сбить человека с сумо платформы", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
		
	 public static final Quest qmenu = new Quest('b', ItemType.CHISELED_BOOKSHELF, 0, null, newbie, 
		"Грамотность", "Посмотреть меню Квестов", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	  public static final Quest pass = new Quest('c', ItemType.KNOWLEDGE_BOOK, 0, null, qmenu, 
		"Гражданин Острова", "Заполни несколько полей в Пасспорте", "", QuestVis.PARENT, QuestFrame.TASK, 0);
		
	  public static final Quest pandora = new Quest('d', ItemType.SPONGE, 0, null, qmenu, 
		"Удача Пандоры", "Испытайте удачу в Разломе Пандоры", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	  
	   public static final Quest treasure = new Quest('e', ItemType.ENDER_CHEST, 0, null, pandora, 
		"Сундук Сокровищ", "Получите примочки из Сундука Сокровищ", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
	
	  public static final Quest mission = new Quest('f', ItemType.GOLD_INGOT, 0, null, qmenu, 
		"Путь к Успеху", "Прими первую Миссию", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
	
	   private static final String[] npcs = {"LB", "AR", "MI", "DA", "SW", "OB", "NM", "SE", "PA", "PM"};
	   public static final Quest agent = new Quest('g', ItemType.END_CRYSTAL, npcs.length, npcs, mission, 
		"Комерческий Агент", "Поговори со всеми НПС", "", QuestVis.PARENT, QuestFrame.CHALLENGE, 0);
	
	 public static final Quest lamp = new Quest('z', ItemType.BLAZE_ROD, 0, null, newbie, 
		"Раб Лампы", "Освободи Джина из лампы", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	
	  public static final Quest navig = new Quest('A', ItemType.ARROW, 0, null, lamp, 
		"Навигатор", "Навести компас (клик на иконку)", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	
	   public static final Quest discover = new Quest('B', ItemType.COMPASS, 9, null, navig, 
		"Открыть все Локации", "Исследуйте все острова лобби", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
	
	  public static final Quest plate = new Quest('C', ItemType.IRON_BOOTS, 0, null, navig, 
		"Сверх-Транспорт", "Переместитесь с помощью плиты", "", QuestVis.PARENT, QuestFrame.TASK, 0);
	   
	  public static final Quest greet = new Quest('D', ItemType.QUARTZ, 0, null, lamp, 
		"Приветствие Новичка", "Нажми ПКМ на нового игрока", "", QuestVis.PARENT, QuestFrame.GOAL, 0);
	   
	   public static final Quest doctor = new Quest('E', ItemType.WRITTEN_BOOK, 0, null, greet, 
		"Доктор Географ. Наук", "Выполни все задания в Лобби", "", QuestVis.HIDDEN, QuestFrame.CHALLENGE, 0);
	
	public static void load() {QuestManager.loadQuests();}
    
}
