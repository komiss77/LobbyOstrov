package ru.ostrov77.lobby.quest;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public enum Quest {
    
    
    DiscoverAllArea ('a',"Открыть все локации"),
    FindPandora ('b',"Найти шкатулку Пандоры"),
    OpenTreassureChest ('c',"Открыть сундук сокровищ"),
    GreetNewBie ('d',"Поприветствовать новичка (ПКМ на новичка)"),
    ;
    
    
    public final char code;
    public final String displayName;
    private static final Map<Character,Quest> codeMap;
    
    
    private Quest (final char code, final String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
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
    
}
