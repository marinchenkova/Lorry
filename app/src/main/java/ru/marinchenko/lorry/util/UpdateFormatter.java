package ru.marinchenko.lorry.util;

/**
 * Составление строки информации об обновлении списка сетей.
 */
public class UpdateFormatter {

    private static final String AUTO = "Список обновляется автоматически";
    private static final String MANUAL = "Список обновляется вручную";
    private static final String UPDATE = "Время обновления: ";

    private static final String SEC_10 = " секунд";

    private static final String MIN_1 = " минута";
    private static final String MIN_2 = " минуты";
    private static final String MIN_5 = " минут";

    public static String formatString(int sec) {
        if(sec == 0) return AUTO;
        else if(sec < 60) return UPDATE + sec + SEC_10;
        else if(sec == 60) return UPDATE + sec/60 + MIN_1;
        else if(sec < 300) return UPDATE + sec/60 + MIN_2;
        else if(sec <= 900) return UPDATE + sec/60 + MIN_5;
        else return MANUAL;
    }

    public static int formatTime(int val) {
        switch (val) {
            case 0: return 10;
            case 1: return 15;
            case 2: return 30;
            case 3: return 45;
            case 4: return 60;
            case 5: return 120;
            case 6: return 180;
            case 7: return 300;
            case 8: return 600;
            case 9: return 900;
        }
        return 10;
    }

}
