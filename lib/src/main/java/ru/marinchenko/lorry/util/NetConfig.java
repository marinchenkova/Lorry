package ru.marinchenko.lorry.util;


import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс содержит {@code static} методы для автоматической аутентификации в сетях ведирегистраторов.
 */
public class NetConfig {
    public static void main(String[] args) {
        System.out.println(generateBSSID());
    }
    /**
     * Проверка, раздается ли сеть Wi-Fi видеорегистратором.
     * @param name имя сети
     * @return {@code true} если сеть раздается Wi-Fi видеорегистратором
     */
    public static boolean ifRec(String name){
        Pattern pattern = Pattern.compile("LV-[0-9]{8}");
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    /**
     * Генерация имени сети для видеорегистратора. Имя имеет вид LV-xxxxxxxx, где xx..x -
     * десятичные цифры.
     * @return имя сети
     */
    public static String generateSSID(){
        int id = (int) (10000000 + Math.random() * 89999999);
        return "LV-" + String.valueOf(id);
    }

    public static String generateBSSID(){
        int n;
        String s = "";
        for(int i = 0; i < 6; i++){
            n = (int) (Math.random() * 99);
            if(n < 10) s += "0";
            s += n + ":";
        }
        return s.substring(0, s.length() - 1);
    }

    /**
     * Генерация пароля для сети видеорегистратора.
     * @param id имя сети
     * @return пароль
     */
    public static String generatePass(String id){
        int l = id.length() - 1;
        char[] array = id.toCharArray();

        for(int s = 0; s < l; s++) {
            for (int j = s; j < l; j++) {
                array[s] = utfUp(array[s], array[j + 1]);
            }
        }
        array[l] = utfUp(array[l], array[0]);

        return new String(array);
    }

    private static char utfUp(char a, char b){
        int c = toRelCode((int) a) + toRelCode((int) b);
        if(c > 61){ c -= 62; }
        return fromRelCode(c);
    }

    private static int toRelCode(int a){
        if(a > 47 & a < 58){
            return a - 48 == 0 ? 9 : a - 48;
        } else if(a > 64 & a < 91){
            return a - 55;
        } else if(a > 96 & a < 123){
            return a - 61;
        } else {
            return 7;
        }
    }

    private static char fromRelCode(int c){
        if(c > -1 & c < 10){
            return (char) (c + 48);
        } else if(c > 9 & c < 36){
            return (char) (c + 55);
        } else if(c > 35 & c < 62){
            return (char) (c + 61);
        } else {
            return (char) 56;
        }
    }
}
