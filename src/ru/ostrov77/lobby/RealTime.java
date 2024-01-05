package ru.ostrov77.lobby;

import java.util.Calendar;
import ru.komiss77.Ostrov;


public class RealTime {

    private static final Calendar cal;
    private static boolean isWinter;
    private static float mcf;
    
    static {
        cal = Ostrov.calendar;
        switch (cal.get(Calendar.MONTH)) {
            case 0, 1, 2 -> {
                mcf = cal.get(Calendar.DAY_OF_YEAR) / 91.5f;
                isWinter = true;
            }
            case 9, 10, 11 -> {
                mcf = (365f - cal.get(Calendar.DAY_OF_YEAR)) / 91.5f;
                isWinter = true;
            }
            case 3, 4, 5 -> {
                mcf = (183 - cal.get(Calendar.DAY_OF_YEAR)) / 91.5f;
                isWinter = false;
            }
            case 6, 7, 8 -> {
                mcf = (cal.get(Calendar.DAY_OF_YEAR) - 183f) / 91.5f;
                isWinter = false;
            }
            default -> {
                isWinter = true;
                mcf = 0f;
            }
        }        
    }

    
    
    public static int getMCTime() {
        final int tm = (int) (cal.get(Calendar.HOUR_OF_DAY) * 1000f + (cal.get(Calendar.MINUTE) * 16.66f) + (cal.get(Calendar.SECOND) * 0.1666f));

        if (isWinter) {
            return switch (tm / 1000) {
                case 0, 1, 2, 3, 4, 5, 6, 7 ->
                    18000 + (int) (tm * (0.75f + (0.25f * mcf)));
                case 8, 9, 10, 11 ->
                    6000 - (int) ((12000 - tm) * (1.5f - (0.5f * mcf)));
                case 12, 13, 14, 15, 16, 17, 18, 19 ->
                    6000 + (int) ((tm - 12000) * (1.5f - (0.5f * mcf)));
                case 20, 21, 22, 23 ->
                    18000 - (int) ((24000 - tm) * (0.75f + (0.25f * mcf)));
                default ->
                    6000;
            };
        } else {
            return switch (tm / 1000) {
                case 0, 1, 2, 3 -> 18000 + (int) (tm * (1.5f - (0.5f * mcf)));
                case 4, 5, 6, 7, 8, 9, 10, 11 -> 6000 - (int) ((12000 - tm) * (0.75f + (0.25f * mcf)));
                case 12, 13, 14, 15 -> 6000 + (int) ((tm - 12000) * (0.75f + (0.25f * mcf)));
                case 16, 17, 18, 19, 20, 21, 22, 23 -> 18000 - (int) ((24000 - tm) * (1.5f - (0.5f * mcf)));
                default -> 6000;
            };
        }
    }

}
