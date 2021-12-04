package ru.ostrov77.lobby;


public enum LobbyFlag {
    
    
    
        //Pandora(1,"Шкатулка пандоры сегодня"),
        NewBieDone(2,"Посвящение новичка пройдено"),
        Elytra(3,"Элитры получены"),
        Arcaim(4,"Аркаим открыт"),
        Daaria(5,"Даария открыта"),
        Midgard(6,"Мидгард открыт"),
        SkyWorld(7,"Острова открыты"),
        Parkour(8,"Паркуры открыты"),
        Sedna(9,"Седна открыта"),

        ;
    
        public final int tag;
        public final String displayName;

        private LobbyFlag (final int tag, final String displayName) {
            this.tag = tag;
            this.displayName = displayName;
        }
    

    public static boolean hasFlag(final int flagsArray, final LobbyFlag flag) {
        return (flagsArray & (1 << flag.tag)) == (1 << flag.tag);
    }


}
