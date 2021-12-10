package ru.ostrov77.lobby.quest;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public enum Quest {
    
    //ИД локаций
    
    
    DiscoverAllArea         ('a',   "",                 "Открыть все локации"), //+при входе в новую зону сверяется размер изученных и существующих
    UsePandora              ('b',   "pandora",          "Уйти от Пандоры живым"), //+ чекается при выходе из кубоида пандоры
    OpenTreassureChest      ('c',   "treassureChest",   "Открыть сундук сокровищ"), //+по эвенту косметики открытие сундука
    GreetNewBie             ('d',   "spawn",            "Поприветствовать новичка (ПКМ на новичка)"),
    SpeakWithNPC            ('e',   "newbie",           "Разговорить лоцмана"),
    ReachSpawn              ('f',   "newbie",           "Добраться до спавна"), //+при входе в зону спавн
    ;


    
    
    public final char code;
    public final String attachedArea;
    public final String displayName;
    
    
    private Quest (final char code, final String attachedArea, final String displayName) {
        this.code = code;
        this.attachedArea = attachedArea;
        this.displayName = displayName;
    }
    
    private static final Map<Character,Quest> codeMap;
    static {
        Map<Character,Quest> im = new ConcurrentHashMap<>();
        for (Quest d : Quest.values()) {
            im.put(d.code,d);
        }
        codeMap = Collections.unmodifiableMap(im);
    }
    
    public static Quest byCode(final char code){
        return codeMap.get(code);//intMap.containsKey(tag) ? intMap.get(tag) : EMPTY;
    }
    
    public static EnumSet<Quest> getAreaQuest(final String cuboidName) {
        final EnumSet<Quest> areaQuest = EnumSet.noneOf(Quest.class);
        for (Quest q : Quest.values()) {
            if (q.attachedArea.equals(cuboidName)) {
                areaQuest.add(q);
            }
        }
        return areaQuest;
    }}
