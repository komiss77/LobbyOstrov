package ru.ostrov77.lobby;


public enum LobbyFlag {
    
    
    
        //Pandora(1,"Шкатулка пандоры сегодня"),
        NewBieDone(2,"Посвящение новичка пройдено"),
        Elytra(3,"Элитры получены"),
        
        //при изменении Talk... менять onSpeak в FigureListener
        TalkMI(4,"Диалог с НПС Мидгарда"), 
        TalkAR(5,"Диалог с НПС Аркаима"),
        TalkDA(6,"Диалог с НПС Даарии"),
        TalkSW(7,"Диалог с НПС Скайблока"),
        TalkOB(8,"Диалог с НПС ВанБлока"),
        TalkPVE(9,"Диалог с НПС ПВЕ"),
        TalkSN(10,"Диалог с НПС Седны"),
        TalkPK(11,"Диалог с НПС Паркуров"),
        TalkPVP(12,"Диалог с НПС ПВП"),
        TalkSP(13,"Диалог с НПС Спавна"),
        
        MI1(14,"Монеты Ромы"),
        MI2(15,"Монеты Валеры"),
        MI3(16,"Монеты Олега"), 
        ;
    
        public final int tag;
        public final String displayName;

        private LobbyFlag (final int tag, final String displayName) {
            this.tag = tag;
            this.displayName = displayName;
        }
    

   // public static boolean hasFlag(final int flagsArray, final LobbyFlag flag) {
   //     return (flagsArray & (1 << flag.tag)) == (1 << flag.tag);
   // }


}
