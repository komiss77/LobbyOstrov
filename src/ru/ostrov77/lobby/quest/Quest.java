package ru.ostrov77.lobby.quest;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import ru.komiss77.utils.ItemUtils;


public enum Quest {
    

    DiscoverAllArea         ( 'a',	3, 0, 12, Material.COMPASS,		"spawn",             	"Открыть все Локации",		"Исследуйте всю территорию лобби"),//+при входе в новую зону сверяется размер изученных и существующих
    LeavePandora            ( 'b',	-1, -3, 0, Material.SPONGE,		"spawn",      		"Уйти от Пандоры живым", 	"Испытайте свою удачу в Разломе Пандоры"),//+ чекается при выходе из кубоида пандоры
    OpenTreassureChest      ( 'c',	-3, -3, 0, Material.ENDER_CHEST,	"spawn",   		"Открыть Сундук Сокровищ", 	"Получите примочки из Сундука Сокровищ"), //+по эвенту косметики открытие сундука
    GreetNewBie             ( 'd',	-1, 3, 0, Material.QUARTZ,		"spawn",        	"Поприветствовать Новичка", 	"Нажмите ПКМ на нового игрока"),
    SpeakWithNPC            ('e',	2, 0, 0, Material.GLOBE_BANNER_PATTERN,	"newbie",       	"Разговорить Лоцмана", 		"Выведайте куда вы прибыли у Лоцмана"),
    ReachSpawn              ( 'f',	1, 2, 0, Material.ENDER_PEARL,		"newbie",       	"Добраться до Спавна", 		"Нажмите на Гаста для перемещения на спавн"),//+при входе в зону спавн
    MiniRace                ( 'g',	2, -1, 0, Material.TURTLE_HELMET,	"nopvp",       		"Олимпиада", 			"Пройти состязание менее чем за 5 минут"),
    MiniPark                ('h',	2, -1, 0, Material.SMALL_DRIPLEAF,	"parkur",       	"Прыжок за Прыжком", 		"Пропрыгать 10+ блоков на мини-паркурах"),
    CobbleGen               ('i',	2, -1, 12, Material.COBBLESTONE,	"skyworld",       	"Прокачка Острова", 		"Выкопать 12 булыжника в генераторе"),
    FindBlock               ( 'j',	2, -1, 50, Material.NETHERITE_BLOCK,	"arcaim",       	"Юный Майнкрафтолог", 		"Найти 50 различных блоков в лобби"),
    MineDiam                ('k',	2, 1, 10, Material.DIAMOND,		"daaria",       	"Зазнавшийся Шахтер", 		"Добыть 10 алмазов в шахте"),
    CollectTax              ('l',	2, 1, 3, Material.RAW_GOLD,		"midgard",       	"Казначей",                     "Собрать налоги с жителей поселка"),
    SumoVoid           	    ('m',	2, 1, 0, Material.SHULKER_SHELL,	"pvp",       		"Сумо Мастер",                  "Сбить человека с сумо платформы"),
    SpawnGin           	    ('n',	2, 1, 0, Material.SHULKER_SHELL,	"newbie",      		"Сумо Мастер",                  "Сбить человека с сумо платформы"),
    ;


    
    
    public final char code;
    public final int dx2;
    public final int dy2;
    public final int num;
    public final Material icon;
    public final String attachedArea;
    public final String displayName;
    public final String description;
    
    //с квестами связано
    public static final Map<String,Integer>racePlayers = new HashMap<>();
    
    
    private Quest (final char code, final int dx2, final int dy2, final int num, final Material icon, final String attachedArea, final String displayName, final String description) {
        this.code = code;
        this.dx2 = dx2;//х коорд * 2
        this.dy2 = dy2;//у коорд * 2
        this.num = num;//сколько надо для задания
        this.icon = icon;
        this.attachedArea = attachedArea;
        this.displayName = displayName;
        this.description = description;
    }
    
    private static final Map<Character,Quest> codeMap;
    private static final Map<Quest,List<String>> loreMap;
    
    static {
        Map<Character,Quest> im = new ConcurrentHashMap<>();
        Map<Quest,List<String>> l = new ConcurrentHashMap<>();
        for (final Quest d : Quest.values()) {
            im.put(d.code,d);
            l.put(d,ItemUtils.Gen_lore(null, d.description, "§7"));
        }
        codeMap = Collections.unmodifiableMap(im);
        loreMap = Collections.unmodifiableMap(l);
    }
    
    public static List<String> getLore(final Quest q){
        return loreMap.get(q);
    }
    
    public static Quest byCode(final char code){
        return codeMap.get(code);//intMap.containsKey(tag) ? intMap.get(tag) : EMPTY;
    }
    
    public static EnumSet<Quest> getAreaQuest(final String cuboidName) {
        final EnumSet<Quest> areaQuest = EnumSet.noneOf(Quest.class);
        for (final Quest q : Quest.values()) {
            if (q.attachedArea.equals(cuboidName)) {
                areaQuest.add(q);
            }
        }
        return areaQuest;
    }
}
