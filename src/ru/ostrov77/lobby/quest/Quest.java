package ru.ostrov77.lobby.quest;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;


public enum Quest {
    

    openQuestMenu           ('a',	Material.DEAD_BUSH,	        "newbie",       	"Древо квестов", 			""),
    SpeakWithNPC            ('b',	Material.GLOBE_BANNER_PATTERN,	"newbie",       	"Разговорить Лоцмана", 			"Выведайте куда вы прибыли у Лоцмана"),
    ReachSpawn              ('c',	Material.ENDER_PEARL,		"newbie",       	"Добраться до Спавна", 			"Нажмите на Гаста для перемещения на спавн"),//+при входе в зону спавн
    DiscoverAllArea         ('d',	Material.COMPASS,		"spawn",             	"Открыть все Локации",			"Исследуйте всю территорию лобби"),//+при входе в новую зону сверяется размер изученных и существующих
    LeavePandora            ('e',	Material.SPONGE,		"pandora",     		"Уйти от Пандоры",               	"Испытайте свою удачу в Разломе Пандоры, и уди живым! Награда: меню индивидуальности"),//+ чекается при выходе из кубоида пандоры
    OpenTreassureChest      ('f',	Material.ENDER_CHEST,		"chest",   		"Открыть Сундук Сокровищ", 		"Получите чудеса из Сундука Сокровищ"), //+по эвенту косметики открытие сундука
    GreetNewBie             ('g',	Material.QUARTZ,		"spawn",        	"Поприветствовать Новичка",             "Правй клик на нового игрока и вы станете старым: получите новый коммуникатор!"),
    MiniRace                ('h',	Material.TURTLE_HELMET,		"nopvp",       		"Олимпиада", 				"Пройти состязание менее чем за 5 минут"),
    ;


    
    
    public final char code;
    public final Material icon;
    public final String attachedArea;
    public final String displayName;
    public final String description;
    
    
    private Quest (final char code, final Material icon, final String attachedArea, final String displayName, final String description) {
        this.code = code;
        this.icon = icon;
        this.attachedArea = attachedArea;
        this.displayName = displayName;
        this.description = description;
    }
    
    private static final Map<Character,Quest> codeMap;
    static {
        Map<Character,Quest> im = new ConcurrentHashMap<>();
        for (final Quest d : Quest.values()) {
            im.put(d.code,d);
        }
        codeMap = Collections.unmodifiableMap(im);
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
