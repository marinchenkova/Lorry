package ru.marinchenko.lorry.util;

/**
 * Составление строки информации об обновлении списка сетей.
 */
public class UpdateFormatter {

    private static final String AUTO = "Список обновляется автоматически";
    private static final String UPDATE = "Время обновления: ";

    private static final String SEC_10 = " секунд";

    private static final String MIN_1 = " минута";
    private static final String MIN_2 = " минуты";
    private static final String MIN_5 = " минут";

    public static String format(int sec){
        if(sec == 0) return AUTO;
        else if(sec < 60) return UPDATE + sec + SEC_10;
        else if(sec == 60) return UPDATE + sec/60 + MIN_1;
        else if(sec < 300) return UPDATE + sec/60 + MIN_2;
        else return UPDATE + sec/60 + MIN_5;
    }

}
