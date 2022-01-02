package ru.ostrov77.lobby.quest;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import ru.komiss77.objects.CaseInsensitiveMap;
import ru.komiss77.utils.ItemUtils;


public enum Quest {
    
//смещения работают относительно parent
    
//                             смещение Х 
//                                  | смещение Y                      одна буква-код задания, 
//                                  |    |  требуемое колл-во          или название кубоида
//                                  |    |   |                     после которого станет видимым
    DiscoverAllArea         ('a',   3,   0, 13, Material.COMPASS,		"HeavyFoot",           	"Открыть все Локации",		"Исследуйте всю территорию лобби, и получи крутой коммуникатор. : 8рил", 6),//+при входе в новую зону сверяется размер изученных и существующих
    PandoraLuck             ('b',  -1,  -4,  0, Material.SPONGE,		"spawn",      		"Удача Пандоры", 	        "Испытайте свою удачу в Разломе Пандоры : 2рил", 2),//+ чекается при выходе из кубоида пандоры
    OpenTreassureChest      ('c',  -2,   0,  0, Material.ENDER_CHEST,           "PandoraLuck", 		"Открыть Сундук Сокровищ", 	"Получите примочки из Сундука Сокровищ : 4рил", 4), //+по эвенту косметики открытие сундука
    GreetNewBie             ('d',  -1,   4,  0, Material.QUARTZ,		"spawn",        	"Поприветствовать Новичка", 	"Нажмите ПКМ на нового игрока", 0),
    SpeakWithNPC            ('e',   2,   0,  0, Material.GLOBE_BANNER_PATTERN,  "newbie",       	"Разговорить Лоцмана", 		"Выведайте куда вы прибыли у Лоцмана", 0),
    ReachSpawn              ('f',   1,   2,  0, Material.ENDER_PEARL,           "newbie",       	"Добраться до Спавна", 		"Нажмите на Джина для перемещения на спавн", 0),//+при входе в зону спавн
    MiniRace                ('g',   2,  -1,  0, Material.TURTLE_HELMET,         "nopvp",       		"Олимпиада", 			"Пройти состязание менее чем за 5 минут", 0),
    MiniPark                ('h',   2,  -1,  0, Material.SMALL_DRIPLEAF,	"parkur",       	"Прыжок за Прыжком", 		"Пропрыгать 12+ блоков на мини-паркурах", 0),
    CobbleGen               ('i',   2,  -1, 12, Material.COBBLESTONE,           "skyworld",       	"Прокачка Острова", 		"Выкопать 12 булыжника в генераторе", 0),
    FindBlock               ('j',   2,  -1, 50, Material.NETHERITE_BLOCK,	"arcaim",       	"Юный Майнкрафтолог", 		"Найти 50 различных блоков в лобби", 0),
    MineDiam                ('k',   2,   1, 10, Material.DIAMOND,		"daaria",       	"Зазнавшийся Шахтер", 		"Добыть 10 алмазов в шахте", 0),
    CollectTax              ('l',   2,   1, 13, Material.RAW_GOLD,		"midgard",       	"Казначей",                     "Собрать золотых с жителей поселка", 0),
    SumoVoid           	    ('m',   2,   1,  0, Material.SHULKER_SHELL,         "pvp",       		"Сумо Мастер",                  "Сбить человека с сумо платформы", 0),
    SpawnGin          	    ('n',   0,  -2,  0, Material.BLAZE_ROD,             "SpeakWithNPC",  	"Раб Лампы",                    "Освободи Джина из лампы", 0),
    OpenAdvancements        ('o',   2,   2,  0, Material.BOOKSHELF,             "newbie",      		"Грамотность",                  "Посмотреть меню Квестов", 0),
    
    Navigation              ('p',   3,   0,  0, Material.ARROW,                 "spawn",      		"Навигатор",                    "Навести компас на цель (клик на название в меню локаций)", 0),
    HeavyFoot               ('q',   2,   0,  0, Material.IRON_BOOTS,            "Navigation",     	"Тяжелая поступь",              "Совершите перемещение с помощью плиты", 0),
    Elytra                  ('r',  -2,   0,  0, Material.WRITTEN_BOOK,          "GreetNewBie",     	"Доктор Географ. Наук",     "Выполни все задания в Лобби : 8рил", 8),

    Passport                ('s',  2,   0,  50, Material.PAPER,                 "DiscoverAllArea",     	"§fГражданин Острова",          "Заполни более 50% полей Паспорта Островитянина", 0),
    TalkAllNpc              ('t', -2,   0,   9, Material.BOOKSHELF,             "OpenTreassureChest",   "Комерческий Агент",            "Поговори со всеми НПС : 4рил", 4),
    FirstMission            ('u',  0,   2,   0, Material.GOLD_INGOT,            "GreetNewBie",          "Путь к успеху",                "Прими первую Миссию : 2рил", 2),
    ;


    
    
    public final char code; //только для загрузки/сохранения!
    public final int dx2;
    public final int dy2;
    public final int ammount;
    public final Material icon;
    public final String parent;
    public final String displayName;
    public final String description;
    public final int pay;
    
    
    //с квестами связано
    //public static final Map<String,Integer>racePlayers = new HashMap<>();
    
    
    private Quest (final char code, final int dx2, final int dy2, final int ammount, final Material icon, final String parent, final String displayName, final String description, final int pay) {
        this.code = code;
        this.dx2 = dx2;//х коорд * 2
        this.dy2 = dy2;//у коорд * 2
        this.ammount = ammount;//сколько надо для задания
        this.icon = icon;
        this.parent = parent;
        this.displayName = displayName;
        this.description = description;
        this.pay = pay;
    }
    
    private static final Map<Character,Quest> codeMap;
    private static final Map<String,Quest> nameMap;
    private static final Map<Quest,List<String>> loreMap;
    
    static {
        Map<Character,Quest> c = new ConcurrentHashMap<>();
        Map<String,Quest> n = new CaseInsensitiveMap<>();
        Map<Quest,List<String>> l = new ConcurrentHashMap<>();
        for (final Quest d : Quest.values()) {
            c.put(d.code,d);
            n.put(d.name(),d);
            l.put(d,ItemUtils.Gen_lore(null, d.description, "§7"));
        }
        codeMap = Collections.unmodifiableMap(c);
        nameMap = Collections.unmodifiableMap(n);
        loreMap = Collections.unmodifiableMap(l);
    }
    
    public static List<String> getLore(final Quest q){
        return loreMap.get(q);
    }
    
    public static Quest byCode(final char code){
        return codeMap.get(code);//intMap.containsKey(tag) ? intMap.get(tag) : EMPTY;
    }    
    
    public static Quest byName(final String name){
        return nameMap.get(name);//intMap.containsKey(tag) ? intMap.get(tag) : EMPTY;
    }
    
    public static EnumSet<Quest> getChidren(String parentName) {
        //parentName = parentName.toLowerCase();
        final EnumSet<Quest> result = EnumSet.noneOf(Quest.class);
        for (final Quest q : Quest.values()) {
            if (!q.name().equalsIgnoreCase(parentName) && q.parent.equalsIgnoreCase(parentName)) {
                result.add(q);
            }
        }
        return result;
    }
    
   /* public static EnumSet<Quest> getQuestChildren(final Quest parent) {
        final EnumSet<Quest> result = EnumSet.noneOf(Quest.class);
        for (final Quest q : Quest.values()) {
            if (q==parent) continue;
             if (q.parent.equals(parent.)) {
                result.add(q);
            }
        }
        return result;
    }*/

}
