package ru.ostrov77.lobby.quest;


public enum Quest {
    
    
    DiscoverAllArea ("Открыть все локации"),
    ;
    
    
    public final String desc;
    
    private Quest (final String desc) {
        this.desc = desc;
    }
    
}
